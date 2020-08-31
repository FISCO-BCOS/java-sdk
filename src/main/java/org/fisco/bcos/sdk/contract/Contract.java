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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.EventEncoder;
import org.fisco.bcos.sdk.abi.EventValues;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.eventsub.EventLogParams;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.manager.TransactionManager;
import org.fisco.bcos.sdk.transaction.manager.TransactionManagerFactory;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Contract {
    protected static Logger logger = LoggerFactory.getLogger(Contract.class);
    protected final String contractBinary;
    protected String contractAddress;
    // transactionReceipt after deploying the contract
    protected TransactionReceipt deployReceipt;
    protected final TransactionManager transactionManager;
    protected final Client client;
    public static final String FUNC_DEPLOY = "deploy";
    protected final FunctionEncoder functionEncoder;
    protected final CryptoInterface credential;
    protected final EventEncoder eventEncoder;
    protected static String LATEST_BLOCK = "latest";

    @Deprecated
    protected Contract(
            String contractBinary,
            String contractAddress,
            Client client,
            CryptoInterface credential,
            TransactionManager transactionManager) {
        this.contractBinary = contractBinary;
        this.contractAddress = contractAddress;
        this.client = client;
        this.transactionManager = transactionManager;
        this.credential = credential;
        this.functionEncoder = new FunctionEncoder(credential);
        this.eventEncoder = new EventEncoder(credential);
    }

    @Deprecated
    protected Contract(
            String contractBinary,
            String contractAddress,
            Client client,
            CryptoInterface credential) {
        this(
                contractBinary,
                contractAddress,
                client,
                credential,
                TransactionManagerFactory.createTransactionManager(client, credential));
    }

    protected static <T extends Contract> T deploy(
            Class<T> type,
            Client client,
            CryptoInterface credential,
            TransactionManager transactionManager,
            String binary,
            String encodedConstructor)
            throws ContractException {
        try {
            Constructor<T> constructor =
                    type.getDeclaredConstructor(String.class, Client.class, CryptoInterface.class);
            constructor.setAccessible(true);
            T contract = constructor.newInstance(null, client, credential);
            return create(contract, binary, encodedConstructor);
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
            CryptoInterface credential,
            String binary,
            String encodedConstructor)
            throws ContractException {
        return deploy(
                type,
                client,
                credential,
                TransactionManagerFactory.createTransactionManager(client, credential),
                binary,
                encodedConstructor);
    }

    private static <T extends Contract> T create(
            T contract, String binary, String encodedConstructor) throws ContractException {
        TransactionReceipt transactionReceipt =
                contract.executeTransaction(binary + encodedConstructor, FUNC_DEPLOY);

        String contractAddress = transactionReceipt.getContractAddress();
        if (contractAddress == null) {
            throw new ContractException(
                    "Deploy contract failed: empty contract address returned, transactionReceipt: "
                            + transactionReceipt.toString());
        }
        contract.setContractAddress(contractAddress);
        contract.setDeployReceipt(transactionReceipt);
        return contract;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public TransactionReceipt getDeployReceipt() {
        return deployReceipt;
    }

    public void setDeployReceipt(TransactionReceipt deployReceipt) {
        this.deployReceipt = deployReceipt;
    }

    private List<Type> executeCall(Function function) throws ContractException {

        String encodedFunctionData = functionEncoder.encode(function);
        CallRequest callRequest =
                new CallRequest(
                        transactionManager.getCurrentExternalAccountAddress(),
                        contractAddress,
                        encodedFunctionData);
        Call response = transactionManager.executeCall(callRequest);
        // get value from the response
        String callResult = response.getCallResult().getOutput();
        if (!response.getCallResult().getStatus().equals("0x0")) {
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
            return FunctionReturnDecoder.decode(callResult, function.getOutputParameters());
        } catch (Exception e) {
            throw new ContractException(
                    "decode callResult failed, error info:" + e.getMessage(),
                    e,
                    response.getCallResult());
        }
    }

    protected <T extends Type> T executeCallWithSingleValueReturn(Function function)
            throws ContractException {
        List<Type> values = executeCall(function);
        if (!values.isEmpty()) {
            return (T) values.get(0);
        } else {
            throw new ContractException(
                    "executeCall for function "
                            + function.getName()
                            + " failed for empty returned value from the contract "
                            + contractAddress);
        }
    }

    protected <T extends Type, R> R executeCallWithSingleValueReturn(
            Function function, Class<R> returnType) throws ContractException {
        T result = executeCallWithSingleValueReturn(function);
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
        return executeCall(function);
    }

    protected void asyncExecuteTransaction(
            String data, String funName, TransactionCallback callback) {
        transactionManager.sendTransactionAsync(contractAddress, data, callback);
    }

    protected void asyncExecuteTransaction(Function function, TransactionCallback callback) {
        asyncExecuteTransaction(functionEncoder.encode(function), function.getName(), callback);
    }

    protected TransactionReceipt executeTransaction(Function function) {
        return executeTransaction(functionEncoder.encode(function), function.getName());
    }

    protected TransactionReceipt executeTransaction(String data, String functionName) {
        return transactionManager.sendTransactionAndGetReceipt(contractAddress, data);
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
            return eventValues.getIndexedValues();
        }

        public List<Type> getNonIndexedValues() {
            return eventValues.getNonIndexedValues();
        }

        public TransactionReceipt.Logs getLog() {
            return log;
        }
    }

    protected String createSignedTransaction(Function function) {
        return createSignedTransaction(contractAddress, this.functionEncoder.encode(function));
    }

    protected String createSignedTransaction(String to, String data) {
        return transactionManager.createSignedTransaction(to, data);
    }

    public void subscribeEvent(EventLogParams params, EventCallback callback) {
        this.client.getEventSubscribe().subscribeEvent(params, callback);
    }

    public void subscribeEvent(String abi, String bin, String topic0, EventCallback callback) {
        subscribeEvent(
                abi, bin, topic0, LATEST_BLOCK, LATEST_BLOCK, new ArrayList<String>(), callback);
    }

    public void subscribeEvent(
            String abi,
            String bin,
            String topic0,
            String fromBlock,
            String toBlock,
            List<String> otherTopics,
            EventCallback callback) {

        EventLogParams filter = new EventLogParams();
        filter.setFromBlock(fromBlock);
        filter.setToBlock(toBlock);

        List<String> addresses = new ArrayList<String>();
        addresses.add(getContractAddress());
        filter.setAddresses(addresses);

        List<Object> topics = new ArrayList<Object>();
        topics.add(topic0);
        if (otherTopics != null) {
            for (Object obj : otherTopics) {
                topics.add(obj);
            }
        }
        filter.setTopics(topics);
        this.subscribeEvent(filter, callback);
    }

    public static EventValues staticExtractEventParameters(
            EventEncoder eventEncoder, Event event, TransactionReceipt.Logs log) {
        List<String> topics = log.getTopics();
        String encodedEventSignature = eventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues =
                FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());

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
        return staticExtractEventParameters(eventEncoder, event, log);
    }

    protected List<EventValues> extractEventParameters(
            Event event, TransactionReceipt transactionReceipt) {
        return transactionReceipt
                .getLogs()
                .stream()
                .map(log -> extractEventParameters(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected EventValuesWithLog extractEventParametersWithLog(
            Event event, TransactionReceipt.Logs log) {
        final EventValues eventValues = extractEventParameters(event, log);
        return (eventValues == null) ? null : new EventValuesWithLog(eventValues, log);
    }

    protected List<EventValuesWithLog> extractEventParametersWithLog(
            Event event, TransactionReceipt transactionReceipt) {
        return transactionReceipt
                .getLogs()
                .stream()
                .map(log -> extractEventParametersWithLog(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected List<EventValuesWithLog> extractEventParametersWithLog(
            Event event, List<TransactionReceipt.Logs> logs) {
        return logs.stream()
                .map(log -> extractEventParametersWithLog(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <S extends Type, T> List<T> convertToNative(List<S> arr) {
        List<T> out = new ArrayList<T>();
        for (Iterator<S> it = arr.iterator(); it.hasNext(); ) {
            out.add((T) it.next().getValue());
        }
        return out;
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
}
