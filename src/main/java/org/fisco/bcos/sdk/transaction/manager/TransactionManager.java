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
package org.fisco.bcos.sdk.transaction.manager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.SolidityFunction;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderInterface;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderService;
import org.fisco.bcos.sdk.transaction.builder.TransactionBuilderInterface;
import org.fisco.bcos.sdk.transaction.builder.TransactionBuilderService;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderInterface;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.model.gas.DefaultGasProvider;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManager implements TransactionManagerInterface {
    protected static Logger log = LoggerFactory.getLogger(TransactionManager.class);
    protected final CryptoInterface cryptoInterface;
    protected final Client client;
    protected final Integer groupId;
    protected final String chainId;
    protected final TransactionBuilderInterface transactionBuilder;
    protected final FunctionBuilderInterface functionBuilder;
    protected final TransactionEncoderInterface transactionEncoder;
    protected final TransactionPusherInterface transactionPusher;
    protected final TransactionDecoderInterface transactionDecoder;
    protected final FunctionEncoder functionEncoder;

    public TransactionManager(
            Client client, CryptoInterface cryptoInterface, Integer groupId, String chainId) {
        this.cryptoInterface = cryptoInterface;
        this.client = client;
        this.groupId = groupId;
        this.chainId = chainId;
        this.transactionBuilder = new TransactionBuilderService(client);
        this.transactionEncoder = new TransactionEncoderService(cryptoInterface);
        this.transactionPusher = new TransactionPusherService(client);
        this.transactionDecoder = new TransactionDecoderService(cryptoInterface);
        this.functionEncoder = new FunctionEncoder(cryptoInterface);
        this.functionBuilder = new FunctionBuilderService();
    }

    @Override
    public void deployOnly(String abi, String bin, String contractName, List<Object> params) {
        transactionPusher.pushOnly(createSignedConstructor(abi, bin, contractName, params));
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(String data) {
        String signedData = createSignedTransaction(null, data);
        return transactionPusher.push(signedData);
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String signedData) {
        TransactionReceipt receipt = transactionPusher.push(signedData);
        try {
            TransactionResponse response =
                    transactionDecoder.decodeTransactionReceipt(abi, receipt);
            return response;
        } catch (TransactionBaseException | TransactionException | IOException e) {
            log.error("deploy exception: {}", e.getMessage());
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse deployAndGetResponse(
            String abi, String bin, String contractName, List<Object> params) {
        return deployAndGetResponse(abi, createSignedConstructor(abi, bin, contractName, params));
    }

    @Override
    public void deployAsync(
            String abi,
            String bin,
            String contractName,
            List<Object> params,
            TransactionCallback callback) {
        transactionPusher.pushAsync(
                createSignedConstructor(abi, bin, contractName, params), callback);
    }

    @Override
    public void sendTransactionOnly(String signedData) {
        this.transactionPusher.pushOnly(signedData);
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(String to, String data) {
        String signedData = createSignedTransaction(to, data);
        return this.client.sendRawTransactionAndGetReceipt(signedData);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(String to, String abi, String data)
            throws TransactionBaseException {
        String signedData = createSignedTransaction(to, data);
        TransactionReceipt receipt = this.transactionPusher.push(signedData);
        try {
            return transactionDecoder.decodeTransactionReceipt(abi, receipt);
        } catch (TransactionBaseException | TransactionException | IOException e) {
            log.error("sendTransaction exception: {}", e.getMessage());
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, List<Object> params)
            throws TransactionBaseException {
        String data = encodeFunction(abi, functionName, params);
        return sendTransactionAndGetResponse(to, abi, data);
    }

    @Override
    public void sendTransactionAsync(String signedTransaction, TransactionCallback callback) {
        transactionPusher.pushAsync(signedTransaction, callback);
    }

    @Override
    public void sendTransactionAsync(String to, String data, TransactionCallback callback) {
        String signedData = createSignedTransaction(to, data);
        client.asyncSendRawTransaction(signedData, callback);
    }

    @Override
    public void sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            TransactionSucCallback callback)
            throws TransactionBaseException {
        String data = encodeFunction(abi, functionName, params);
        String signedData = createSignedTransaction(to, data);
        client.asyncSendRawTransaction(signedData, callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(String signedData) {
        return this.transactionPusher.pushAsync(signedData);
    }

    @Override
    public CallResponse sendCall(
            String from, String to, String abi, String functionName, List<Object> params)
            throws TransactionBaseException {
        SolidityFunction solidityFunction =
                functionBuilder.buildFunctionByAbi(abi, functionName, params);
        if (!solidityFunction.getFunctionAbi().isConstant()) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(),
                    "Wrong transaction type, actually it's a transaction");
        }
        String data = functionEncoder.encode(solidityFunction.getFunction());
        CallRequest callRequest =
                new CallRequest(from, to, data, solidityFunction.getFunctionAbi());
        return sendCall(callRequest);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public CallResponse sendCall(CallRequest callRequest) throws TransactionBaseException {
        Call call = executeCall(callRequest);
        String callOutput = call.getCallResult().getOutput();
        ABIDefinition ad = callRequest.getAbi();
        List<TypeReference<Type>> list =
                ContractAbiUtil.paramFormat(ad.getOutputs())
                        .stream()
                        .map(l -> (TypeReference<Type>) l)
                        .collect(Collectors.toList());
        List<Type> values = FunctionReturnDecoder.decode(callOutput, list);
        CallResponse callResponse = new CallResponse();
        callResponse.setValues(JsonUtils.toJson(values));
        return callResponse;
    }

    @Override
    public Call executeCall(CallRequest callRequest) {
        return executeCall(
                callRequest.getFrom(), callRequest.getTo(), callRequest.getEncodedFunction());
    }

    @Override
    public Call executeCall(String from, String to, String encodedFunction) {
        return client.call(new Transaction(from, to, encodedFunction));
    }

    @Override
    public String getCurrentExternalAccountAddress() {
        return this.cryptoInterface.getCryptoKeyPair().getAddress();
    }

    @Override
    public String createSignedConstructor(
            String abi, String bin, String contractName, List<Object> params) {
        SolidityConstructor constructor =
                functionBuilder.buildConstructor(abi, bin, contractName, params);
        return createSignedTransaction(null, constructor.getData());
    }

    @Override
    public String encodeFunction(String abi, String functionName, List<Object> params)
            throws TransactionBaseException {
        SolidityFunction solidityFunction =
                functionBuilder.buildFunctionByAbi(abi, functionName, params);
        if (solidityFunction.getFunctionAbi().isConstant()) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(),
                    "Wrong transaction type, actually it's a call");
        }
        return functionEncoder.encode(solidityFunction.getFunction());
    }

    @Override
    public String createSignedTransaction(String to, String data) {
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        DefaultGasProvider.GAS_PRICE,
                        DefaultGasProvider.GAS_LIMIT,
                        to,
                        data,
                        BigInteger.ZERO,
                        new BigInteger(this.chainId),
                        BigInteger.valueOf(this.groupId),
                        "");
        return transactionEncoder.encodeAndSign(rawTransaction);
    }
}
