/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package org.fisco.bcos.sdk.contract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.codec.ABICodec;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.codec.EventEncoder;
import org.fisco.bcos.sdk.codec.FunctionEncoderInterface;
import org.fisco.bcos.sdk.codec.FunctionReturnDecoderInterface;
import org.fisco.bcos.sdk.codec.abi.*;
import org.fisco.bcos.sdk.codec.datatypes.*;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contract help manage all operations including deploy, send transaction, call contract, and
 * subscribe event of one specific contract. It is inherited by precompiled contracts and contract
 * java wrappers.
 */
public class Contract {
    protected static Logger logger = LoggerFactory.getLogger(Contract.class);
    protected final String contractBinary;
    protected String contractAddress;
    // transactionReceipt after deploying the contract
    protected TransactionReceipt deployReceipt;
    protected final TransactionProcessor transactionProcessor;
    protected final Client client;
    public static final String FUNC_DEPLOY = "deploy";
    protected final FunctionEncoderInterface functionEncoder;
    protected final FunctionReturnDecoderInterface functionReturnDecoder;
    protected final CryptoKeyPair credential;
    protected final CryptoSuite cryptoSuite;
    protected final EventEncoder eventEncoder;
    protected static String LATEST_BLOCK = "latest";

    /**
     * Constructor
     *
     * @param contractBinary the contract binary code hex string
     * @param contractAddress the contract address
     * @param client a Client object
     * @param credential key pair to use when sign transaction
     * @param transactionProcessor TransactionProcessor object
     */
    protected Contract(
            String contractBinary,
            String contractAddress,
            Client client,
            CryptoKeyPair credential,
            TransactionProcessor transactionProcessor) {
        this.contractBinary = contractBinary;
        this.contractAddress = contractAddress;
        this.client = client;
        this.transactionProcessor = transactionProcessor;
        this.credential = credential;
        this.cryptoSuite = client.getCryptoSuite();
        this.functionEncoder =
                client.isWASM()
                        ? new org.fisco.bcos.sdk.codec.scale.FunctionEncoder(cryptoSuite)
                        : new org.fisco.bcos.sdk.codec.abi.FunctionEncoder(cryptoSuite);
        this.functionReturnDecoder =
                client.isWASM()
                        ? new org.fisco.bcos.sdk.codec.scale.FunctionReturnDecoder()
                        : new org.fisco.bcos.sdk.codec.abi.FunctionReturnDecoder();
        this.eventEncoder = new EventEncoder(cryptoSuite);
    }

    /**
     * Constructor, auto create a TransactionProcessor object
     *
     * @param contractBinary the contract binary code hex string
     * @param contractAddress the contract address
     * @param client a Client object to send requests
     * @param credential key pair to use when sign transaction
     */
    protected Contract(
            String contractBinary,
            String contractAddress,
            Client client,
            CryptoKeyPair credential) {
        this(
                contractBinary,
                contractAddress,
                client,
                credential,
                TransactionProcessorFactory.createTransactionProcessor(client, credential));
    }

    /**
     * Deploy contract
     *
     * @param type
     * @param client a Client object to send requests
     * @param credential key pair to use when sign transaction
     * @param transactionManager TransactionProcessor
     * @param binary the contract binary code hex string
     * @param encodedConstructor
     * @param <T> a smart contract object extends Contract
     * @return <T> type smart contract
     * @throws ContractException
     */
    protected static <T extends Contract> T deploy(
            Class<T> type,
            Client client,
            CryptoKeyPair credential,
            TransactionProcessor transactionManager,
            String binary,
            String ABI,
            byte[] encodedConstructor,
            String path)
            throws ContractException {
        try {
            Constructor<T> constructor =
                    type.getDeclaredConstructor(String.class, Client.class, CryptoKeyPair.class);
            constructor.setAccessible(true);
            T contract = constructor.newInstance(null, client, credential);
            return create(contract, binary, ABI, encodedConstructor, path);
        } catch (InstantiationException
                | InvocationTargetException
                | NoSuchMethodException
                | IllegalAccessException e) {
            throw new ContractException("deploy contract failed, error info: " + e.getMessage());
        }
    }

    protected static <T extends Contract> T deploy(
            Class<T> type,
            Client client,
            CryptoKeyPair credential,
            String binary,
            String ABI,
            byte[] encodedConstructor,
            String path)
            throws ContractException {
        return deploy(
                type,
                client,
                credential,
                TransactionProcessorFactory.createTransactionProcessor(client, credential),
                binary,
                ABI,
                encodedConstructor,
                path);
    }

    private static <T extends Contract> T create(
            T contract, String binary, String abi, byte[] encodedConstructor, String path)
            throws ContractException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        TransactionReceipt transactionReceipt;
        if (contract.client.isWASM()) {
            ABICodec codec = new ABICodec(contract.cryptoSuite, true);
            try {
                // deploy fir byte is 0, new byte[1] default is 0
                outputStream.write(new byte[1]);
                outputStream.write(
                        codec.encodeConstructorFromBytes(binary, encodedConstructor, abi));
            } catch (IOException | ABICodecException e) {
                throw new ContractException("Deploy contract failed, error message: " + e);
            }
            contract.setContractAddress(path);
            transactionReceipt =
                    contract.executeTransaction(outputStream.toByteArray(), FUNC_DEPLOY);
        } else {
            try {
                outputStream.write(Hex.decode(binary));
                if (encodedConstructor != null) {
                    outputStream.write(encodedConstructor);
                }
            } catch (IOException e) {
                throw new ContractException("Deploy contract failed, error message: " + e);
            }
            transactionReceipt =
                    contract.executeTransaction(outputStream.toByteArray(), FUNC_DEPLOY);
            String contractAddress = transactionReceipt.getContractAddress();
            if (contractAddress == null) {
                // parse the receipt
                RetCode retCode = ReceiptParser.parseTransactionReceipt(transactionReceipt);
                throw new ContractException(
                        "Deploy contract failed, error message: " + retCode.getMessage());
            }
            contract.setContractAddress(contractAddress);
        }
        contract.setDeployReceipt(transactionReceipt);
        return contract;
    }

    public String getContractAddress() {
        return this.contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public TransactionReceipt getDeployReceipt() {
        return this.deployReceipt;
    }

    public void setDeployReceipt(TransactionReceipt deployReceipt) {
        this.deployReceipt = deployReceipt;
    }

    private List<Type> executeCall(Function function) throws ContractException {

        byte[] encodedFunctionData = this.functionEncoder.encode(function);
        CallRequest callRequest =
                new CallRequest(
                        this.credential.getAddress(), this.contractAddress, encodedFunctionData);
        Call response = this.transactionProcessor.executeCall(callRequest);
        // get value from the response
        String callResult = response.getCallResult().getOutput();
        if (response.getCallResult().getStatus() != 0) {
            ContractException contractException =
                    new ContractException(
                            "execute "
                                    + function.getName()
                                    + " failed for non-zero status "
                                    + response.getCallResult().getStatus(),
                            response.getCallResult());
            logger.warn(
                    "status of executeCall is non-success, status: {}, callResult: {}",
                    response.getCallResult().getStatus(),
                    response.getCallResult().toString());
            throw ReceiptParser.parseExceptionCall(contractException);
        }
        try {
            return functionReturnDecoder.decode(callResult, function.getOutputParameters());
        } catch (Exception e) {
            throw new ContractException(
                    "decode callResult failed, error info:" + e.getMessage(),
                    e,
                    response.getCallResult());
        }
    }

    protected <T extends Type> T executeCallWithSingleValueReturn(Function function)
            throws ContractException {
        List<Type> values = this.executeCall(function);
        if (!values.isEmpty()) {
            return (T) values.get(0);
        } else {
            throw new ContractException(
                    "executeCall for function "
                            + function.getName()
                            + " failed for empty returned value from the contract "
                            + this.contractAddress);
        }
    }

    protected <T extends Type, R> R executeCallWithSingleValueReturn(
            Function function, Class<R> returnType) throws ContractException {
        T result = this.executeCallWithSingleValueReturn(function);
        // cast the value into returnType
        Object value = result.getValue();
        if (returnType.isAssignableFrom(value.getClass())) {
            return (R) value;
        } else if (result.getClass().equals(Address.class) && returnType.equals(String.class)) {
            return (R) result.toString(); // cast isn't necessary
        } else {
            throw new ContractException(
                    "Unable convert response "
                            + value
                            + " to expected type "
                            + returnType.getSimpleName());
        }
    }

    protected List<Type> executeCallWithMultipleValueReturn(Function function)
            throws ContractException {
        return this.executeCall(function);
    }

    protected void asyncExecuteTransaction(
            byte[] data, String funName, TransactionCallback callback) {
        this.transactionProcessor.sendTransactionAsync(
                this.contractAddress, data, this.credential, callback);
    }

    protected void asyncExecuteTransaction(Function function, TransactionCallback callback) {
        this.asyncExecuteTransaction(
                this.functionEncoder.encode(function), function.getName(), callback);
    }

    protected TransactionReceipt executeTransaction(Function function) {
        return this.executeTransaction(this.functionEncoder.encode(function), function.getName());
    }

    protected TransactionReceipt executeTransaction(byte[] data, String functionName) {
        return this.transactionProcessor.sendTransactionAndGetReceipt(
                this.contractAddress, data, this.credential);
    }

    /** Adds a log field to {@link EventValues}. */
    public static class EventValuesWithLog {
        private final EventValues eventValues;
        private final TransactionReceipt.Logs log;

        private EventValuesWithLog(EventValues eventValues, TransactionReceipt.Logs log) {
            this.eventValues = eventValues;
            this.log = log;
        }

        public List<Type> getIndexedValues() {
            return this.eventValues.getIndexedValues();
        }

        public List<Type> getNonIndexedValues() {
            return this.eventValues.getNonIndexedValues();
        }

        public TransactionReceipt.Logs getLog() {
            return this.log;
        }
    }

    protected String createSignedTransaction(Function function) {
        return this.createSignedTransaction(
                this.contractAddress, this.functionEncoder.encode(function));
    }

    protected String createSignedTransaction(String to, byte[] data) {
        return this.transactionProcessor.createSignedTransaction(to, data, this.credential);
    }

    public static EventValues staticExtractEventParameters(
            EventEncoder eventEncoder,
            FunctionReturnDecoderInterface functionReturnDecoder,
            Event event,
            TransactionReceipt.Logs log) {
        List<String> topics = log.getTopics();
        String encodedEventSignature = eventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues =
                functionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value =
                    FunctionReturnDecoder.decodeIndexedValue(
                            topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    protected EventValues extractEventParameters(Event event, TransactionReceipt.Logs log) {
        return staticExtractEventParameters(
                this.eventEncoder, this.functionReturnDecoder, event, log);
    }

    protected List<EventValues> extractEventParameters(
            Event event, TransactionReceipt transactionReceipt) {
        return transactionReceipt.getLogEntries().stream()
                .map(log -> this.extractEventParameters(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected EventValuesWithLog extractEventParametersWithLog(
            Event event, TransactionReceipt.Logs log) {
        final EventValues eventValues = this.extractEventParameters(event, log);
        return (eventValues == null) ? null : new EventValuesWithLog(eventValues, log);
    }

    protected List<EventValuesWithLog> extractEventParametersWithLog(
            Event event, TransactionReceipt transactionReceipt) {
        return transactionReceipt.getLogEntries().stream()
                .map(log -> this.extractEventParametersWithLog(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected List<EventValuesWithLog> extractEventParametersWithLog(
            Event event, List<TransactionReceipt.Logs> logs) {
        return logs.stream()
                .map(log -> this.extractEventParametersWithLog(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static <S extends Type, T> List<T> convertToNative(List<S> arr) {
        List<T> out = new ArrayList<T>();
        for (Iterator<S> it = arr.iterator(); it.hasNext(); ) {
            out.add((T) it.next().getValue());
        }
        return out;
    }

    public TransactionProcessor getTransactionProcessor() {
        return this.transactionProcessor;
    }

    public String getCurrentExternalAccountAddress() {
        return this.credential.getAddress();
    }
}
