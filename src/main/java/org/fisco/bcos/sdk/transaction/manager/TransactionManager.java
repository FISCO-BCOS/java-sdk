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
import org.fisco.bcos.sdk.transaction.model.dto.TransactionRequest;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.model.gas.DefaultGasProvider;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransactionManager @Description: TransactionManager
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:23:19 PM
 */
public class TransactionManager implements TransactionManagerInterface {
    protected static Logger log = LoggerFactory.getLogger(TransactionManager.class);
    private final CryptoInterface cryptoInterface;
    private final Client client;
    private final Integer groupId;
    private final String chainId;
    private final TransactionBuilderInterface transactionBuilder;
    private final FunctionBuilderInterface functionBuilder;
    private final TransactionEncoderInterface transactionEncoder;
    private final TransactionPusherInterface transactionPusher;
    private final TransactionDecoderInterface transactionDecoder;
    private final FunctionEncoder functionEncoder;

    public TransactionManager(
            Client client, CryptoInterface cryptoInterface, Integer groupId, String chainId) {
        this(client, cryptoInterface, groupId, chainId, null);
    }

    /**
     * In file mode, use abi and bin to send transactions.
     *
     * @param client
     * @param cryptoInterface
     * @param groupId
     * @param chainId
     * @param contractLoader
     */
    public TransactionManager(
            Client client,
            CryptoInterface cryptoInterface,
            Integer groupId,
            String chainId,
            ContractLoader contractLoader) {
        if (contractLoader == null) {
            this.functionBuilder = new FunctionBuilderService();
        } else {
            this.functionBuilder = new FunctionBuilderService(contractLoader);
        }
        this.cryptoInterface = cryptoInterface;
        this.client = client;
        this.groupId = groupId;
        this.chainId = chainId;
        this.transactionBuilder = new TransactionBuilderService(client);
        this.transactionEncoder = new TransactionEncoderService(cryptoInterface);
        this.transactionPusher = new TransactionPusherService(client);
        this.transactionDecoder = new TransactionDecoderService(cryptoInterface);
        this.functionEncoder = new FunctionEncoder(cryptoInterface);
    }

    @Override
    public TransactionResponse deploy(TransactionRequest transactionRequest) {
        String contractName = transactionRequest.getContractName();
        TransactionReceipt receipt = transactionPusher.push(transactionRequest.getSignedData());
        try {
            TransactionResponse response =
                    transactionDecoder.decodeTransactionReceipt(contractName, receipt);
            return response;
        } catch (TransactionBaseException | TransactionException | IOException e) {
            log.error("deploy exception: {}", e.getMessage());
            return new TransactionResponse(
                    ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse deploy(
            String abi, String bin, String contractName, List<Object> args) {
        SolidityConstructor constructor =
                functionBuilder.buildConstructor(abi, bin, contractName, args);
        String signedData = createSignedTransaction(null, constructor.getData());
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSignedData(signedData);
        return deploy(transactionRequest);
    }

    /**
     * Deploy by bin & abi files. Should init with contractLoader.
     *
     * @param contractName
     * @param args
     * @return
     * @throws TransactionBaseException
     */
    @Override
    public TransactionResponse deployByContractLoader(String contractName, List<Object> args)
            throws TransactionBaseException {
        SolidityConstructor constructor = functionBuilder.buildConstructor(contractName, args);
        String signedData = createSignedTransaction(null, constructor.getData());
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSignedData(signedData);
        return deploy(transactionRequest);
    }

    @Override
    public void sendTransactionOnly(TransactionRequest transactionRequest) {
        this.transactionPusher.pushOnly(transactionRequest.getSignedData());
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(String to, String data) {
        String signedData = createSignedTransaction(to, data);
        return this.client.sendRawTransactionAndGetReceipt(signedData);
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException {
        SolidityFunction solidityFunction =
                functionBuilder.buildFunction(contractName, functionName, args);
        if (solidityFunction.getFunctionAbi().isConstant()) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(),
                    "Wrong transaction type, actually it's a call");
        }
        String data = functionEncoder.encode(solidityFunction.getFunction());
        return sendTransactionAndGetReceipt(contractAddress, data);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            TransactionRequest transactionRequest) {
        String contract = transactionRequest.getContractName();
        TransactionReceipt receipt =
                this.transactionPusher.push(transactionRequest.getSignedData());
        try {
            return transactionDecoder.decodeTransactionReceipt(contract, receipt);
        } catch (TransactionBaseException | TransactionException | IOException e) {
            log.error("sendTransaction exception: {}", e.getMessage());
            return new TransactionResponse(
                    ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendTransactionAsync(String signedTransaction, TransactionCallback callback) {
        this.transactionPusher.pushAsync(signedTransaction, callback);
    }

    @Override
    public void sendTransactionAsync(String to, String data, TransactionCallback callback) {
        String signedData = createSignedTransaction(to, data);
        this.client.asyncSendRawTransaction(signedData, callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            TransactionRequest transactionRequest) {
        return this.transactionPusher.pushAsync(transactionRequest.getSignedData());
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
    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException {
        SolidityFunction solidityFunction =
                functionBuilder.buildFunction(contractName, functionName, args);
        if (!solidityFunction.getFunctionAbi().isConstant()) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(),
                    "Wrong transaction type, actually it's a transaction");
        }
        String data = functionEncoder.encode(solidityFunction.getFunction());
        CallRequest callRequest =
                new CallRequest(getCurrentExternalAccountAddress(), contractAddress, data);
        callRequest.setAbi(solidityFunction.getFunctionAbi());
        return sendCall(callRequest);
    }

    @Override
    public String getCurrentExternalAccountAddress() {
        return this.cryptoInterface.getCryptoKeyPair().getAddress();
    }

    @Override
    public Call executeCall(CallRequest callRequest) {
        return client.call(
                new Transaction(
                        callRequest.getFrom(),
                        callRequest.getTo(),
                        callRequest.getEncodedFunction()));
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
