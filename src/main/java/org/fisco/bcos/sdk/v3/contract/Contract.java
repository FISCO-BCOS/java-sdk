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
package org.fisco.bcos.sdk.v3.contract;

import static org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute.LIQUID_CREATE;
import static org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute.LIQUID_SCALE_CODEC;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.EventValues;
import org.fisco.bcos.sdk.v3.codec.FunctionEncoderInterface;
import org.fisco.bcos.sdk.v3.codec.FunctionReturnDecoderInterface;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Array;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.eventsub.EventSubParams;
import org.fisco.bcos.sdk.v3.eventsub.EventSubscribe;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.fisco.bcos.sdk.v3.model.callback.CallCallback;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.DefaultTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.TransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.AbiEncodedRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
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
    protected TransactionProcessor transactionProcessor;
    // v2 transaction
    protected TransactionManager transactionManager = null;
    protected final Client client;
    public static final String FUNC_DEPLOY = "deploy";
    protected final FunctionEncoderInterface functionEncoder;
    protected final FunctionReturnDecoderInterface functionReturnDecoder;
    protected final CryptoKeyPair credential;
    protected final CryptoSuite cryptoSuite;
    protected final EventEncoder eventEncoder;
    private final EventSubscribe eventSubscribe;
    private boolean enableDAG = false;

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
                        ? new org.fisco.bcos.sdk.v3.codec.scale.FunctionEncoder(
                                cryptoSuite.getHashImpl())
                        : new org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder(
                                cryptoSuite.getHashImpl());
        this.functionReturnDecoder =
                client.isWASM()
                        ? new org.fisco.bcos.sdk.v3.codec.scale.FunctionReturnDecoder()
                        : new org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder();
        this.eventEncoder = new EventEncoder(cryptoSuite.getHashImpl());
        try {
            this.eventSubscribe = EventSubscribe.build(client);
        } catch (JniException e) {
            throw new RuntimeException(e);
        }
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

    protected Contract(
            String contractBinary,
            String contractAddress,
            Client client,
            TransactionManager transactionManager) {
        this.contractBinary = contractBinary;
        this.contractAddress = contractAddress;
        this.client = client;
        this.transactionManager = transactionManager;
        this.credential = client.getCryptoSuite().getCryptoKeyPair();
        this.cryptoSuite = client.getCryptoSuite();
        this.functionEncoder =
                client.isWASM()
                        ? new org.fisco.bcos.sdk.v3.codec.scale.FunctionEncoder(
                                cryptoSuite.getHashImpl())
                        : new org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder(
                                cryptoSuite.getHashImpl());
        this.functionReturnDecoder =
                client.isWASM()
                        ? new org.fisco.bcos.sdk.v3.codec.scale.FunctionReturnDecoder()
                        : new org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder();
        this.eventEncoder = new EventEncoder(cryptoSuite.getHashImpl());
        try {
            this.eventSubscribe = EventSubscribe.build(client);
        } catch (JniException e) {
            throw new RuntimeException(e);
        }
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

    public TransactionProcessor getTransactionProcessor() {
        return this.transactionProcessor;
    }

    public void setTransactionProcessor(TransactionProcessor transactionProcessor) {
        this.transactionProcessor = transactionProcessor;
    }

    public String getCurrentExternalAccountAddress() {
        return this.credential.getAddress();
    }

    public boolean isEnableDAG() {
        return enableDAG;
    }

    public void setEnableDAG(boolean enableDAG) {
        this.enableDAG = enableDAG;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Deploy contract
     *
     * @param type class type
     * @param client a Client object to send requests
     * @param credential key pair to use when sign transaction
     * @param abi ABI json string
     * @param binary the contract binary code hex string
     * @param encodedConstructor constructor params
     * @param path bfs path, this param only use in wasm vm
     * @param <T> a smart contract object extends Contract
     * @return T type smart contract
     * @throws ContractException throws when deploy failed
     */
    protected static <T extends Contract> T deploy(
            Class<T> type,
            Client client,
            CryptoKeyPair credential,
            String binary,
            String abi,
            byte[] encodedConstructor,
            String path)
            throws ContractException {
        try {
            Constructor<T> constructor =
                    type.getDeclaredConstructor(String.class, Client.class, CryptoKeyPair.class);
            constructor.setAccessible(true);
            T contract = constructor.newInstance(null, client, credential);

            ContractCodec codec = new ContractCodec(contract.cryptoSuite, client.isWASM());
            if (client.isWASM()) {
                // NOTE: it should set address first, contract.executeDeployTransaction will use it
                // as 'to'
                contract.setContractAddress(path);
            }
            TransactionReceipt transactionReceipt =
                    contract.executeDeployTransaction(
                            codec.encodeConstructorFromBytes(binary, encodedConstructor), abi);
            String contractAddress = transactionReceipt.getContractAddress();
            if (contractAddress == null
                    || transactionReceipt.getStatus()
                            != TransactionReceiptStatus.Success.getCode()) {
                // parse the receipt
                ReceiptParser.getErrorStatus(transactionReceipt);
            }
            contract.setContractAddress(client.isWASM() ? path : contractAddress);
            contract.setDeployReceipt(transactionReceipt);
            return contract;
        } catch (InstantiationException
                | InvocationTargetException
                | NoSuchMethodException
                | IllegalAccessException
                | ContractCodecException e) {
            throw new ContractException("deploy contract failed, error info: " + e.getMessage(), e);
        }
    }

    protected static <T extends Contract> T deploy(
            Class<T> type,
            Client client,
            CryptoKeyPair credential,
            String binary,
            String abi,
            byte[] encodedConstructor,
            String path,
            BigInteger value)
            throws ContractException {
        try {
            Constructor<T> constructor =
                    type.getDeclaredConstructor(String.class, Client.class, CryptoKeyPair.class);
            constructor.setAccessible(true);
            T contract = constructor.newInstance(null, client, credential);

            ContractCodec codec = new ContractCodec(contract.cryptoSuite, client.isWASM());
            if (client.isWASM()) {
                // NOTE: it should set address first, contract.executeDeployTransaction will use it
                // as 'to'
                contract.setContractAddress(path);
            }
            TransactionReceipt transactionReceipt =
                    contract.executeDeployTransaction(
                            codec.encodeConstructorFromBytes(binary, encodedConstructor),
                            abi,
                            value);
            String contractAddress = transactionReceipt.getContractAddress();
            if (contractAddress == null
                    || transactionReceipt.getStatus()
                            != TransactionReceiptStatus.Success.getCode()) {
                // parse the receipt
                ReceiptParser.getErrorStatus(transactionReceipt);
            }
            contract.setContractAddress(client.isWASM() ? path : contractAddress);
            contract.setDeployReceipt(transactionReceipt);
            return contract;
        } catch (InstantiationException
                | InvocationTargetException
                | NoSuchMethodException
                | IllegalAccessException
                | ContractCodecException e) {
            throw new ContractException("deploy contract failed, error info: " + e.getMessage(), e);
        }
    }

    private int generateTransactionAttribute(String funcName) {
        int attribute = 0;
        if (client.isWASM()) {
            attribute = LIQUID_SCALE_CODEC;
            if (Objects.equals(funcName, FUNC_DEPLOY)) {
                attribute |= LIQUID_CREATE;
            }
        } else {
            attribute |= TransactionAttribute.EVM_ABI_CODEC;
        }

        return attribute;
    }

    private List<Type> executeCall(Function function) throws ContractException {

        byte[] encodedFunctionData = this.functionEncoder.encode(function);
        Call response;
        if (transactionManager != null) {
            response = transactionManager.sendCall(this.contractAddress, encodedFunctionData);
        } else {
            CallRequest callRequest =
                    new CallRequest(
                            this.credential.getAddress(),
                            this.contractAddress,
                            encodedFunctionData);
            response = this.transactionProcessor.executeCall(callRequest);
        }
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
                    response.getCallResult());
            throw ReceiptParser.parseExceptionCall(contractException);
        }
        try {
            return functionReturnDecoder.decode(callResult, function.getOutputParameters());
        } catch (Exception e) {
            throw new ContractException(
                    "decode callResult failed, error info: " + e.getMessage(),
                    e,
                    response.getCallResult());
        }
    }

    protected void asyncExecuteCall(Function function, CallCallback callback) {
        if (transactionManager != null) {
            asyncExecuteCallByTransactionManager(function, callback);
            return;
        }
        byte[] encodedFunctionData = this.functionEncoder.encode(function);
        CallRequest callRequest =
                new CallRequest(
                        this.credential.getAddress(), this.contractAddress, encodedFunctionData);
        transactionProcessor.asyncExecuteCall(
                callRequest,
                new RespCallback<Call>() {
                    @Override
                    public void onResponse(Call response) {
                        String callResult = response.getCallResult().getOutput();
                        if (response.getCallResult().getStatus() != 0) {
                            logger.warn(
                                    "status of executeCall is non-success, status: {}, callResult: {}",
                                    response.getCallResult().getStatus(),
                                    response.getCallResult());
                            callback.onError(
                                    new Response(
                                            response.getCallResult().getStatus(),
                                            "execute "
                                                    + function.getName()
                                                    + " failed for non-zero status "
                                                    + response.getCallResult().getStatus()));
                            return;
                        }
                        List<Type> result;
                        try {
                            result =
                                    functionReturnDecoder.decode(
                                            callResult, function.getOutputParameters());
                        } catch (Exception e) {
                            callback.onError(
                                    new Response(
                                            -1,
                                            "decode callResult failed, error info: "
                                                    + e.getMessage()));
                            return;
                        }
                        callback.onResponse(result);
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse);
                    }
                });
    }

    protected void asyncExecuteCallByTransactionManager(Function function, CallCallback callback) {
        transactionManager.asyncSendCall(
                this.contractAddress,
                this.functionEncoder.encode(function),
                new RespCallback<Call>() {
                    @Override
                    public void onResponse(Call call) {
                        String callResult = call.getCallResult().getOutput();
                        if (call.getCallResult().getStatus() != 0) {
                            logger.warn(
                                    "status of executeCall is non-success, status: {}, callResult: {}",
                                    call.getCallResult().getStatus(),
                                    call.getCallResult());
                            callback.onError(
                                    new Response(
                                            call.getCallResult().getStatus(),
                                            "execute "
                                                    + function.getName()
                                                    + " failed for non-zero status "
                                                    + call.getCallResult().getStatus()));
                            return;
                        }
                        List<Type> result;
                        try {
                            result =
                                    functionReturnDecoder.decode(
                                            callResult, function.getOutputParameters());
                        } catch (Exception e) {
                            callback.onError(
                                    new Response(
                                            -1,
                                            "decode callResult failed, error info: "
                                                    + e.getMessage()));
                            return;
                        }
                        callback.onResponse(result);
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse);
                    }
                });
    }

    protected <T extends Type, R> R executeCallWithSingleValueReturn(
            Function function, Class<R> returnType) throws ContractException {
        T result;
        List<Type> values = this.executeCall(function);
        if (!values.isEmpty()) {
            result = (T) values.get(0);
        } else {
            throw new ContractException(
                    "executeCall for function "
                            + function.getName()
                            + " failed for empty returned value from the contract "
                            + this.contractAddress);
        }
        // cast the value into returnType
        Object value = result.getValue();
        if (returnType.isAssignableFrom(value.getClass())) {
            return (R) value;
        } else if (returnType.isAssignableFrom(result.getClass())) {
            return (R) result;
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

    protected int generateTxAttributeWithDagFlag(String functionName, int dagAttribute) {
        int txAttribute = generateTransactionAttribute(functionName);
        int dagFlag = dagAttribute;
        // enforce dag
        if (enableDAG) {
            dagFlag = TransactionAttribute.DAG;
        }
        txAttribute |= dagFlag;
        return txAttribute;
    }

    protected String asyncExecuteTransaction(
            byte[] data, String funName, TransactionCallback callback, int dagAttribute) {
        if (transactionManager != null) {
            try {
                return transactionManager.asyncSendTransaction(
                        this.contractAddress, data, BigInteger.ZERO, callback);
            } catch (JniException e) {
                logger.error("sendTransaction failed, error info: {}", e.getMessage(), e);
                return null;
            }
        }
        int txAttribute = generateTxAttributeWithDagFlag(funName, dagAttribute);
        return this.transactionProcessor.sendTransactionAsync(
                this.contractAddress, data, this.credential, txAttribute, callback);
    }

    protected String asyncExecuteTransaction(
            byte[] data,
            String funName,
            TransactionCallback callback,
            int dagAttribute,
            BigInteger value) {
        if (transactionManager != null) {
            try {
                return transactionManager.asyncSendTransaction(
                        this.contractAddress, data, value, callback);
            } catch (JniException e) {
                logger.error("sendTransaction failed, error info: {}", e.getMessage(), e);
                return null;
            }
        }
        int txAttribute = generateTxAttributeWithDagFlag(funName, dagAttribute);
        return this.transactionProcessor.sendTransactionAsync(
                this.contractAddress, data, this.credential, txAttribute, callback);
    }

    protected String asyncExecuteTransaction(Function function, TransactionCallback callback) {
        return this.asyncExecuteTransaction(
                this.functionEncoder.encode(function),
                function.getName(),
                callback,
                function.getTransactionAttribute(),
                function.getValue());
    }

    protected String asyncExecuteTransaction(
            FunctionWrapper functionWrapper, TransactionCallback callback) {
        try {
            TransactionManager txManager = this.transactionManager;
            if (txManager == null) {
                txManager = new DefaultTransactionManager(client);
            }
            AbiEncodedRequest abiEncodedRequest =
                    new TransactionRequestBuilder()
                            .setTo(this.contractAddress)
                            .setNonce(functionWrapper.getNonce())
                            .setBlockLimit(functionWrapper.getBlockLimit())
                            .setExtension(functionWrapper.getExtension())
                            .setValue(
                                    functionWrapper.getValue() != null
                                            ? functionWrapper.getValue().toBigIntegerExact()
                                            : null)
                            .buildAbiEncodedRequest(
                                    this.functionEncoder.encode(functionWrapper.getFunction()));
            return txManager.asyncSendTransaction(abiEncodedRequest, callback);
        } catch (JniException | ContractException e) {
            logger.error("sendTransaction failed, error info: {}", e.getMessage(), e);
        }
        return null;
    }

    protected TransactionReceipt executeTransaction(Function function) {

        if (transactionManager != null) {
            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt =
                        transactionManager.sendTransaction(
                                this.contractAddress,
                                this.functionEncoder.encode(function),
                                function.getValue());
            } catch (JniException e) {
                logger.error("sendTransaction failed, error info: {}", e.getMessage(), e);
            }
            return transactionReceipt;
        }
        int txAttribute =
                generateTxAttributeWithDagFlag(
                        function.getName(), function.getTransactionAttribute());

        return this.transactionProcessor.sendTransactionAndGetReceipt(
                this.contractAddress,
                this.functionEncoder.encode(function),
                this.credential,
                txAttribute);
    }

    protected TransactionReceipt executeTransaction(FunctionWrapper functionWrapper) {
        TransactionManager txManager = this.transactionManager;
        if (txManager == null) {
            txManager = new DefaultTransactionManager(client);
        }
        TransactionReceipt transactionReceipt = null;
        try {
            AbiEncodedRequest abiEncodedRequest =
                    new TransactionRequestBuilder()
                            .setTo(this.contractAddress)
                            .setNonce(functionWrapper.getNonce())
                            .setBlockLimit(functionWrapper.getBlockLimit())
                            .setExtension(functionWrapper.getExtension())
                            .setValue(
                                    functionWrapper.getValue() != null
                                            ? functionWrapper.getValue().toBigIntegerExact()
                                            : null)
                            .buildAbiEncodedRequest(
                                    this.functionEncoder.encode(functionWrapper.getFunction()));
            transactionReceipt = txManager.sendTransaction(abiEncodedRequest);
        } catch (JniException | ContractException e) {
            logger.error("sendTransaction failed, error info: {}", e.getMessage(), e);
        }
        return transactionReceipt;
    }

    protected TransactionReceipt executeDeployTransaction(byte[] data, String abi) {
        if (transactionManager != null) {

            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt =
                        this.transactionManager.sendTransaction(
                                this.contractAddress, data, BigInteger.ZERO, abi, true);
            } catch (JniException e) {
                logger.error("sendTransaction failed, error info: {}", e.getMessage(), e);
            }
            return transactionReceipt;
        }
        int txAttribute = generateTxAttributeWithDagFlag(Contract.FUNC_DEPLOY, 0);
        return this.transactionProcessor.deployAndGetReceipt(
                this.contractAddress, data, abi, this.credential, txAttribute);
    }

    protected TransactionReceipt executeDeployTransaction(
            byte[] data, String abi, BigInteger value) {
        if (transactionManager != null) {

            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt =
                        this.transactionManager.sendTransaction(
                                this.contractAddress, data, value, abi, true);
            } catch (JniException e) {
                logger.error("sendTransaction failed, error info: {}", e.getMessage(), e);
            }
            return transactionReceipt;
        }
        int txAttribute = generateTxAttributeWithDagFlag(Contract.FUNC_DEPLOY, 0);
        return this.transactionProcessor.deployAndGetReceipt(
                this.contractAddress, data, abi, this.credential, txAttribute);
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
        if (transactionManager != null) {
            try {
                byte[] methodId =
                        functionEncoder.buildMethodId(
                                FunctionEncoderInterface.buildMethodSignature(
                                        function.getName(), function.getInputParameters()));
                return transactionManager.createSignedTransaction(
                        this.contractAddress,
                        this.functionEncoder.encode(function),
                        function.getValue(),
                        transactionManager.getGasProvider().getGasPrice(methodId),
                        transactionManager.getGasProvider().getGasLimit(methodId),
                        client.getBlockLimit(),
                        "",
                        false);
            } catch (JniException e) {
                logger.error("createSignedTransaction failed, error info: {}", e.getMessage(), e);
                return null;
            }
        }
        int txAttribute =
                generateTxAttributeWithDagFlag(
                        function.getName(), function.getTransactionAttribute());
        TxPair txPair =
                this.transactionProcessor.createSignedTransaction(
                        this.contractAddress,
                        this.functionEncoder.encode(function),
                        this.credential,
                        txAttribute);

        return txPair.getSignedTx();
    }

    public void subscribeEvent(EventSubParams params, EventSubCallback callback) {
        this.eventSubscribe.subscribeEvent(params, callback);
    }

    public void subscribeEvent(String topic0, EventSubCallback callback) {
        subscribeEvent(topic0, BigInteger.valueOf(-1), BigInteger.valueOf(-1), callback);
    }

    public void subscribeEvent(
            String topic0, BigInteger fromBlock, BigInteger toBlock, EventSubCallback callback) {
        subscribeEvent(
                fromBlock,
                toBlock,
                Collections.singletonList(Collections.singletonList(topic0)),
                callback);
    }

    public void subscribeEvent(
            String topic0,
            List<String> otherTopics,
            BigInteger fromBlock,
            BigInteger toBlock,
            EventSubCallback callback) {
        List<List<String>> topics = new ArrayList<>();
        topics.add(Collections.singletonList(topic0));
        for (String otherTopic : otherTopics) {
            topics.add(Collections.singletonList(otherTopic));
        }
        subscribeEvent(fromBlock, toBlock, topics, callback);
    }

    public void subscribeEvent(
            BigInteger fromBlock,
            BigInteger toBlock,
            List<List<String>> topics,
            EventSubCallback callback) {
        EventSubParams eventSubParams = new EventSubParams();
        // self address
        eventSubParams.addAddress(getContractAddress());
        eventSubParams.setFromBlock(fromBlock);
        eventSubParams.setToBlock(toBlock);
        for (int i = 0; i < topics.size(); i++) {
            for (String topic : topics.get(i)) {
                eventSubParams.addTopic(i, topic);
            }
        }
        subscribeEvent(eventSubParams, callback);
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
                    functionReturnDecoder.decodeIndexedValue(
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
        for (S s : arr) {
            if (Array.class.isAssignableFrom(s.getClass())) {
                out.add((T) convertToNative((List<? extends Type>) ((Array) s).getValue()));
            } else {
                out.add((T) s.getValue());
            }
        }
        return out;
    }
}
