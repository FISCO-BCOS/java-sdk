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

import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ContractlessTransactionManager @Description: ContractlessTransactionManager
 *
 * @author maojiayu
 */
public class AssembleTransactionProcessor extends TransactionProcessor
        implements AssembleTransactionProcessorInterface {
    protected static Logger log = LoggerFactory.getLogger(AssembleTransactionProcessor.class);
    protected final TransactionDecoderInterface transactionDecoder;
    protected final TransactionPusherInterface transactionPusher;
    protected final ABICodec abiCodec;
    protected ContractLoader contractLoader;

    public AssembleTransactionProcessor(
            Client client,
            CryptoKeyPair cryptoKeyPair,
            Integer groupId,
            String chainId,
            ContractLoader contractLoader) {
        super(client, cryptoKeyPair, groupId, chainId);
        this.transactionDecoder = new TransactionDecoderService(cryptoSuite);
        this.transactionPusher = new TransactionPusherService(client);
        this.abiCodec = new ABICodec(cryptoSuite);
        this.contractLoader = contractLoader;
    }

    public AssembleTransactionProcessor(
            Client client,
            CryptoKeyPair cryptoKeyPair,
            Integer groupId,
            String chainId,
            String contractName,
            String abi,
            String bin) {
        super(client, cryptoKeyPair, groupId, chainId);
        this.transactionDecoder = new TransactionDecoderService(cryptoSuite);
        this.transactionPusher = new TransactionPusherService(client);
        this.abiCodec = new ABICodec(cryptoSuite);
        this.contractLoader = new ContractLoader(contractName, abi, bin);
    }

    @Override
    public void deployOnly(String abi, String bin, List<Object> params) throws ABICodecException {
        transactionPusher.pushOnly(createSignedConstructor(abi, bin, params));
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(String data) {
        String signedData = createSignedTransaction(null, data, getCryptoKeyPair());
        return transactionPusher.push(signedData);
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String binary, String signedData) {
        TransactionReceipt receipt = transactionPusher.push(signedData);
        try {
            return transactionDecoder.decodeConstructorReceipt(abi, binary, receipt);
        } catch (TransactionException | IOException | ABICodecException e) {
            log.error("decode transaction receipt exception: {}", e);
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    /*
     *  In case of extends constructor, it won't work correct. */
    @Override
    @Deprecated
    public TransactionResponse deployAndGetResponse(String abi, String signedData) {
        TransactionReceipt receipt = transactionPusher.push(signedData);

        String code = null;
        if (receipt.isStatusOK()) {
            try {
                code = client.getCode(receipt.getContractAddress()).getCode();
            } catch (Exception e) {
                log.error("getCode exception: {}", e);
                return new TransactionResponse(
                        receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
            }
        }

        try {
            return transactionDecoder.decodeReceiptWithoutOutputValues(abi, receipt, code);
        } catch (TransactionException | IOException | ABICodecException e) {
            log.error("decode transaction receipt exception: {}", e);
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ABICodecException {
        return deployAndGetResponse(abi, bin, createSignedConstructor(abi, bin, params));
    }

    @Override
    public TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params) throws ABICodecException {
        return deployAndGetResponse(
                abi,
                bin,
                createSignedTransaction(
                        null,
                        abiCodec.encodeConstructorFromString(abi, bin, params),
                        getCryptoKeyPair()));
    }

    @Override
    public void deployAsync(
            String abi, String bin, List<Object> params, TransactionCallback callback)
            throws ABICodecException {
        transactionPusher.pushAsync(createSignedConstructor(abi, bin, params), callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params) throws ABICodecException {
        return transactionPusher.pushAsync(createSignedConstructor(abi, bin, params));
    }

    /**
     * Deploy by bin and abi files. Should init with contractLoader.
     *
     * @param contractName the contract name
     * @param args the params when deploy a contract
     * @return the transaction response
     * @throws TransactionBaseException send transaction exceptioned
     * @throws ABICodecException abi encode exceptioned
     * @throws NoSuchTransactionFileException Files related to abi codec were not found
     */
    @Override
    public TransactionResponse deployByContractLoader(String contractName, List<Object> args)
            throws ABICodecException, TransactionBaseException {
        return deployAndGetResponse(
                contractLoader.getABIByContractName(contractName),
                contractLoader.getBinaryByContractName(contractName),
                args);
    }

    @Override
    public void deployByContractLoaderAsync(
            String contractName, List<Object> args, TransactionCallback callback)
            throws ABICodecException, NoSuchTransactionFileException {
        deployAsync(
                contractLoader.getABIByContractName(contractName),
                contractLoader.getBinaryByContractName(contractName),
                args,
                callback);
    }

    @Override
    public void sendTransactionOnly(String signedData) {
        this.transactionPusher.pushOnly(signedData);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, String data)
            throws TransactionBaseException, ABICodecException {
        String signedData = createSignedTransaction(to, data, getCryptoKeyPair());
        TransactionReceipt receipt = this.transactionPusher.push(signedData);
        try {
            return transactionDecoder.decodeReceiptWithValues(abi, functionName, receipt);
        } catch (TransactionException | IOException e) {
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
    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws ABICodecException, TransactionBaseException {
        String data =
                abiCodec.encodeMethod(
                        contractLoader.getABIByContractName(contractName), functionName, args);
        return sendTransactionAndGetReceipt(contractAddress, data, cryptoKeyPair);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponseByContractLoader(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> funcParams)
            throws ABICodecException, TransactionBaseException {
        return sendTransactionAndGetResponse(
                contractAddress,
                contractLoader.getABIByContractName(contractName),
                functionName,
                funcParams);
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
        sendTransactionAsync(to, data, getCryptoKeyPair(), callback);
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
            throws ABICodecException, TransactionBaseException {
        String data =
                abiCodec.encodeMethod(
                        contractLoader.getABIByContractName(contractName), functionName, args);
        sendTransactionAsync(contractAddress, data, getCryptoKeyPair(), callback);
    }

    @Override
    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException, ABICodecException {
        return sendCall(
                getCryptoKeyPair().getAddress(),
                contractAddress,
                contractLoader.getABIByContractName(contractName),
                functionName,
                args);
    }

    @Override
    public CallResponse sendCall(
            String from, String to, String abi, String functionName, List<Object> paramsList)
            throws TransactionBaseException, ABICodecException {
        String data = abiCodec.encodeMethod(abi, functionName, paramsList);
        return callAndGetResponse(from, to, abi, functionName, data);
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest)
            throws TransactionBaseException, ABICodecException {
        Call call = executeCall(callRequest);
        CallResponse callResponse = parseCallResponseStatus(call.getCallResult());
        String callOutput = call.getCallResult().getOutput();
        Pair<List<Object>, List<ABIObject>> results =
                abiCodec.decodeMethodAndGetOutputObject(callRequest.getAbi(), callOutput);
        callResponse.setValues(JsonUtils.toJson(results.getLeft()));
        callResponse.setReturnObject(results.getLeft());
        callResponse.setReturnABIObject(results.getRight());
        return callResponse;
    }

    @Override
    public CallResponse sendCallWithStringParams(
            String from, String to, String abi, String functionName, List<String> paramsList)
            throws TransactionBaseException, ABICodecException {
        String data = abiCodec.encodeMethodFromString(abi, functionName, paramsList);
        return callAndGetResponse(from, to, abi, functionName, data);
    }

    public CallResponse callAndGetResponse(
            String from, String to, String abi, String functionName, String data)
            throws ABICodecException, TransactionBaseException {
        Call call = executeCall(from, to, data);
        CallResponse callResponse = parseCallResponseStatus(call.getCallResult());
        Pair<List<Object>, List<ABIObject>> results =
                abiCodec.decodeMethodAndGetOutputObject(
                        abi, functionName, call.getCallResult().getOutput());
        callResponse.setValues(JsonUtils.toJson(results.getLeft()));
        callResponse.setReturnObject(results.getLeft());
        callResponse.setReturnABIObject(results.getRight());
        return callResponse;
    }

    @Override
    public String createSignedConstructor(String abi, String bin, List<Object> params)
            throws ABICodecException {
        return createSignedTransaction(
                null, abiCodec.encodeConstructor(abi, bin, params), getCryptoKeyPair());
    }

    @Override
    public String encodeFunction(String abi, String functionName, List<Object> params)
            throws ABICodecException {
        return abiCodec.encodeMethod(abi, functionName, params);
    }

    @Override
    public RawTransaction getRawTransactionForConstructor(
            String abi, String bin, List<Object> params) throws ABICodecException {
        return transactionBuilder.createTransaction(
                null,
                abiCodec.encodeConstructor(abi, bin, params),
                new BigInteger(this.chainId),
                BigInteger.valueOf(this.groupId));
    }

    @Override
    public RawTransaction getRawTransactionForConstructor(
            BigInteger blockLimit, String abi, String bin, List<Object> params)
            throws ABICodecException {
        return transactionBuilder.createTransaction(
                blockLimit,
                null,
                abiCodec.encodeConstructor(abi, bin, params),
                new BigInteger(this.chainId),
                BigInteger.valueOf(this.groupId));
    }

    @Override
    public RawTransaction getRawTransaction(
            String to, String abi, String functionName, List<Object> params)
            throws ABICodecException {
        return transactionBuilder.createTransaction(
                to,
                abiCodec.encodeMethod(abi, functionName, params),
                new BigInteger(this.chainId),
                BigInteger.valueOf(this.groupId));
    }

    @Override
    public RawTransaction getRawTransaction(
            BigInteger blockLimit, String to, String abi, String functionName, List<Object> params)
            throws ABICodecException {
        return transactionBuilder.createTransaction(
                blockLimit,
                to,
                abiCodec.encodeMethod(abi, functionName, params),
                new BigInteger(this.chainId),
                BigInteger.valueOf(this.groupId));
    }

    private CallResponse parseCallResponseStatus(Call.CallOutput callOutput)
            throws TransactionBaseException {
        CallResponse callResponse = new CallResponse();
        RetCode retCode = ReceiptParser.parseCallOutput(callOutput, "");
        callResponse.setReturnCode(Numeric.decodeQuantity(callOutput.getStatus()).intValue());
        callResponse.setReturnMessage(retCode.getMessage());
        if (!retCode.getMessage().equals(PrecompiledRetCode.CODE_SUCCESS.getMessage())) {
            throw new TransactionBaseException(retCode);
        }
        return callResponse;
    }

    public ContractLoader getContractLoader() {
        return contractLoader;
    }
}