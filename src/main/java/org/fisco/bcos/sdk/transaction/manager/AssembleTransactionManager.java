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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderInterface;
import org.fisco.bcos.sdk.transaction.builder.FunctionBuilderService;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContractlessTransactionManager @Description: ContractlessTransactionManager
 *
 * @author maojiayu
 * @data Aug 11, 2020 8:04:46 PM
 */
public class AssembleTransactionManager extends TransactionManager
        implements AssembleTransactionManagerInterface {
    protected static Logger log = LoggerFactory.getLogger(AssembleTransactionManager.class);
    protected final FunctionBuilderInterface functionBuilder;
    protected final TransactionDecoderInterface transactionDecoder;
    protected final TransactionPusherInterface transactionPusher;
    protected final ABICodec abiCodec;
    private ContractLoader contractLoader;

    /**
     * In file mode, use abi and bin to send transactions.
     *
     * @param client
     * @param cryptoInterface
     * @param groupId
     * @param chainId
     * @param contractLoader
     */
    public AssembleTransactionManager(
            Client client,
            CryptoInterface cryptoInterface,
            Integer groupId,
            String chainId,
            ContractLoader contractLoader) {
        super(client, cryptoInterface, groupId, chainId);
        this.functionBuilder = new FunctionBuilderService(contractLoader);
        this.transactionDecoder = new TransactionDecoderService(cryptoInterface);
        this.transactionPusher = new TransactionPusherService(client);
        this.abiCodec = new ABICodec(cryptoInterface);
        this.contractLoader = contractLoader;
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
            return transactionDecoder.decodeReceiptWithoutValues(abi, receipt);
        } catch (TransactionException | IOException | ContractException | ABICodecException e) {
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
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, String contractName, List<Object> params) {
        return transactionPusher.pushAsync(createSignedConstructor(abi, bin, contractName, params));
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
        return deployAndGetResponse(constructor.getAbi(), signedData);
    }

    @Override
    public void sendTransactionOnly(String signedData) {
        this.transactionPusher.pushOnly(signedData);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, String data)
            throws TransactionBaseException, ABICodecException {
        String signedData = createSignedTransaction(to, data);
        TransactionReceipt receipt = this.transactionPusher.push(signedData);
        try {
            return transactionDecoder.decodeReceiptWithValues(abi, functionName, receipt);
        } catch (TransactionException | IOException | ContractException e) {
            log.error("sendTransaction exception: {}", e.getMessage());
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, List<Object> params)
            throws ABICodecException, TransactionBaseException {
        String data = encodeFunction(abi, functionName, params);
        return sendTransactionAndGetResponse(to, abi, functionName, data);
    }

    @Override
    public TransactionResponse sendTransactionWithStringParamsAndGetResponse(
            String to, String abi, String functionName, List<String> params)
            throws ABICodecException, TransactionBaseException {
        String data = abiCodec.encodeMethodFromString(abi, functionName, params);
        return sendTransactionAndGetResponse(to, abi, functionName, data);
    }

    @Override
    public void deployByContractLoaderAsync(
            String contractName, List<Object> args, TransactionCallback callback)
            throws TransactionBaseException {
        SolidityConstructor constructor = functionBuilder.buildConstructor(contractName, args);
        String signedData = createSignedTransaction(null, constructor.getData());
        sendTransactionAsync(signedData, callback);
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws ABICodecException {
        String data =
                abiCodec.encodeMethod(
                        contractLoader.getABIByContractName(contractName),
                        functionName,
                        args,
                        true,
                        false);
        return sendTransactionAndGetReceipt(contractAddress, data);
    }

    @Override
    public void sendTransactionAsync(String signedTransaction, TransactionCallback callback) {
        transactionPusher.pushAsync(signedTransaction, callback);
    }

    @Override
    public void sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            TransactionCallback callback)
            throws TransactionBaseException, ABICodecException {
        String data = encodeFunction(abi, functionName, params);
        sendTransactionAsync(to, data, callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(String signedData) {
        return transactionPusher.pushAsync(signedData);
    }

    @Override
    public void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> args,
            TransactionCallback callback)
            throws ABICodecException {
        String data =
                abiCodec.encodeMethod(
                        contractLoader.getABIByContractName(contractName),
                        functionName,
                        args,
                        true,
                        false);
        sendTransactionAsync(contractAddress, data, callback);
    }

    @Override
    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException, ABICodecException {
        return sendCall(
                getCurrentExternalAccountAddress(),
                contractAddress,
                contractLoader.getABIByContractName(contractName),
                functionName,
                args);
    }

    @Override
    public CallResponse sendCall(
            String from, String to, String abi, String functionName, List<Object> paramsList)
            throws TransactionBaseException, ABICodecException {
        String data = abiCodec.encodeMethod(abi, functionName, paramsList, true, true);
        Call call = executeCall(from, to, data);
        CallResponse callResponse = parseCallResponseStatus(call.getCallResult());
        List<Object> results =
                abiCodec.decodeMethod(abi, functionName, call.getCallResult().getOutput());
        callResponse.setValues(JsonUtils.toJson(results));
        return callResponse;
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest)
            throws TransactionBaseException, ABICodecException {
        Call call = executeCall(callRequest);
        CallResponse callResponse = parseCallResponseStatus(call.getCallResult());
        String callOutput = call.getCallResult().getOutput();
        List<Object> results = abiCodec.decodeMethod(callRequest.getAbi(), callOutput);
        callResponse.setValues(JsonUtils.toJson(results));
        return callResponse;
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
            throws ABICodecException {
        return abiCodec.encodeMethod(abi, functionName, params);
    }

    private CallResponse parseCallResponseStatus(Call.CallOutput callOutput) {
        CallResponse callResponse = new CallResponse();
        if (callOutput.getStatus().equalsIgnoreCase("0x0")) {
            callResponse.setReturnCode(0);

        } else {
            RetCode retCode = ReceiptParser.parseCallOutput(callOutput, "");
            callResponse.setReturnCode(retCode.getCode());
            callResponse.setReturnMessage(retCode.getMessage());
        }
        return callResponse;
    }
}
