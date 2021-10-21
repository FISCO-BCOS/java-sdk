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
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.model.tars.TransactionData;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.codec.ABICodec;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.wrapper.ABIObject;
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
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            String groupId,
            String chainId,
            ContractLoader contractLoader) {
        super(client, cryptoKeyPair, groupId, chainId);
        this.transactionDecoder = new TransactionDecoderService(this.cryptoSuite, client.isWASM());
        this.transactionPusher = new TransactionPusherService(client);
        this.abiCodec = new ABICodec(this.cryptoSuite, client.isWASM());
        this.contractLoader = contractLoader;
    }

    public AssembleTransactionProcessor(
            Client client,
            CryptoKeyPair cryptoKeyPair,
            String groupId,
            String chainId,
            String contractName,
            String abi,
            String bin) {
        super(client, cryptoKeyPair, groupId, chainId);
        this.transactionDecoder = new TransactionDecoderService(this.cryptoSuite, client.isWASM());
        this.transactionPusher = new TransactionPusherService(client);
        this.abiCodec = new ABICodec(this.cryptoSuite, client.isWASM());
        this.contractLoader = new ContractLoader(contractName, abi, bin);
    }

    @Override
    public void deployOnly(String abi, String bin, List<Object> params) throws ABICodecException {
        this.transactionPusher.pushOnly(this.createSignedConstructor(abi, bin, params));
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(byte[] data) {
        String signedData = this.createSignedTransaction(null, data, this.cryptoKeyPair);
        return this.transactionPusher.push(signedData);
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String signedData) {
        TransactionReceipt receipt = this.transactionPusher.push(signedData);
        try {
            return this.transactionDecoder.decodeReceiptWithoutValues(abi, receipt);
        } catch (TransactionException | IOException | ABICodecException e) {
            log.error("deploy exception: {}", e.getMessage());
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ABICodecException {
        return this.deployAndGetResponse(abi, this.createSignedConstructor(abi, bin, params));
    }

    @Override
    public TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params, String path) throws ABICodecException {
        return this.deployAndGetResponse(
                abi,
                this.createSignedTransaction(
                        null,
                        this.abiCodec.encodeConstructorFromString(abi, bin, params, path),
                        this.cryptoKeyPair));
    }

    @Override
    public void deployAsync(
            String abi, String bin, List<Object> params, TransactionCallback callback)
            throws ABICodecException {
        this.transactionPusher.pushAsync(this.createSignedConstructor(abi, bin, params), callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params) throws ABICodecException {
        return this.transactionPusher.pushAsync(this.createSignedConstructor(abi, bin, params));
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
        return this.deployAndGetResponse(
                this.contractLoader.getABIByContractName(contractName),
                this.contractLoader.getBinaryByContractName(contractName),
                args);
    }

    @Override
    public void deployByContractLoaderAsync(
            String contractName, List<Object> args, TransactionCallback callback)
            throws ABICodecException, NoSuchTransactionFileException {
        this.deployAsync(
                this.contractLoader.getABIByContractName(contractName),
                this.contractLoader.getBinaryByContractName(contractName),
                args,
                callback);
    }

    @Override
    public void sendTransactionOnly(String signedData) {
        this.transactionPusher.pushOnly(signedData);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, byte[] data) throws ABICodecException {
        String signedData = this.createSignedTransaction(to, data, this.cryptoKeyPair);
        TransactionReceipt receipt = this.transactionPusher.push(signedData);
        try {
            return this.transactionDecoder.decodeReceiptWithValues(abi, functionName, receipt);
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
        byte[] data = this.encodeFunction(abi, functionName, params);
        return this.sendTransactionAndGetResponse(to, abi, functionName, data);
    }

    @Override
    public TransactionResponse sendTransactionWithStringParamsAndGetResponse(
            String to, String abi, String functionName, List<String> params)
            throws ABICodecException, TransactionBaseException {
        byte[] data = this.abiCodec.encodeMethodFromString(abi, functionName, params);
        log.info("encoded data: {}", Hex.toHexString(data));
        return this.sendTransactionAndGetResponse(to, abi, functionName, data);
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws ABICodecException, TransactionBaseException {
        byte[] data =
                this.abiCodec.encodeMethod(
                        this.contractLoader.getABIByContractName(contractName), functionName, args);
        return this.sendTransactionAndGetReceipt(contractAddress, data, this.cryptoKeyPair);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponseByContractLoader(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> funcParams)
            throws ABICodecException, TransactionBaseException {
        return this.sendTransactionAndGetResponse(
                contractAddress,
                this.contractLoader.getABIByContractName(contractName),
                functionName,
                funcParams);
    }

    @Override
    public void sendTransactionAsync(String signedTransaction, TransactionCallback callback) {
        this.transactionPusher.pushAsync(signedTransaction, callback);
    }

    @Override
    public void sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            TransactionCallback callback)
            throws TransactionBaseException, ABICodecException {
        byte[] data = this.encodeFunction(abi, functionName, params);
        this.sendTransactionAsync(to, data, this.cryptoKeyPair, callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(String signedData) {
        return this.transactionPusher.pushAsync(signedData);
    }

    @Override
    public void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> args,
            TransactionCallback callback)
            throws ABICodecException, TransactionBaseException {
        byte[] data =
                this.abiCodec.encodeMethod(
                        this.contractLoader.getABIByContractName(contractName), functionName, args);
        this.sendTransactionAsync(contractAddress, data, this.cryptoKeyPair, callback);
    }

    @Override
    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException, ABICodecException {
        return this.sendCall(
                this.cryptoKeyPair.getAddress(),
                contractAddress,
                this.contractLoader.getABIByContractName(contractName),
                functionName,
                args);
    }

    @Override
    public CallResponse sendCall(
            String from, String to, String abi, String functionName, List<Object> paramsList)
            throws TransactionBaseException, ABICodecException {
        byte[] data = this.abiCodec.encodeMethod(abi, functionName, paramsList);
        return this.callAndGetResponse(from, to, abi, functionName, data);
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest)
            throws TransactionBaseException, ABICodecException {
        Call call = this.executeCall(callRequest);
        CallResponse callResponse = this.parseCallResponseStatus(call.getCallResult());
        String callOutput = call.getCallResult().getOutput();
        Pair<List<Object>, List<ABIObject>> results =
                this.abiCodec.decodeMethodAndGetOutputObject(callRequest.getAbi(), callOutput);
        callResponse.setValues(JsonUtils.toJson(results.getLeft()));
        callResponse.setReturnObject(results.getLeft());
        callResponse.setReturnABIObject(results.getRight());
        return callResponse;
    }

    @Override
    public CallResponse sendCallWithStringParams(
            String from, String to, String abi, String functionName, List<String> paramsList)
            throws TransactionBaseException, ABICodecException {
        byte[] data = this.abiCodec.encodeMethodFromString(abi, functionName, paramsList);
        log.info("encoded data: {}", Hex.toHexString(data));
        return this.callAndGetResponse(from, to, abi, functionName, data);
    }

    public CallResponse callAndGetResponse(
            String from, String to, String abi, String functionName, byte[] data)
            throws ABICodecException, TransactionBaseException {
        Call call = this.executeCall(from, to, data);
        CallResponse callResponse = this.parseCallResponseStatus(call.getCallResult());
        List<Type> decodedResult =
                this.abiCodec.decodeMethodAndGetOutputObject(
                        abi, functionName, call.getCallResult().getOutput());
        callResponse.setResults(decodedResult);
        return callResponse;
    }

    @Override
    public String createSignedConstructor(String abi, String bin, List<Object> params)
            throws ABICodecException {
        return this.createSignedTransaction(
                null, this.abiCodec.encodeConstructor(abi, bin, params), this.cryptoKeyPair);
    }

    @Override
    public byte[] encodeFunction(String abi, String functionName, List<Object> params)
            throws ABICodecException {
        return this.abiCodec.encodeMethod(abi, functionName, params);
    }

    @Override
    public TransactionData getRawTransactionForConstructor(
            String abi, String bin, List<Object> params) throws ABICodecException {
        return this.transactionBuilder.createTransaction(
                null,
                this.abiCodec.encodeConstructor(abi, bin, params),
                this.chainId,
                this.groupId);
    }

    @Override
    public TransactionData getRawTransactionForConstructor(
            BigInteger blockLimit, String abi, String bin, List<Object> params)
            throws ABICodecException {
        return this.transactionBuilder.createTransaction(
                blockLimit,
                null,
                this.abiCodec.encodeConstructor(abi, bin, params),
                this.chainId,
                this.groupId);
    }

    @Override
    public TransactionData getRawTransaction(
            String to, String abi, String functionName, List<Object> params)
            throws ABICodecException {
        return this.transactionBuilder.createTransaction(
                to,
                this.abiCodec.encodeMethod(abi, functionName, params),
                this.chainId,
                this.groupId);
    }

    @Override
    public TransactionData getRawTransaction(
            BigInteger blockLimit, String to, String abi, String functionName, List<Object> params)
            throws ABICodecException {
        return this.transactionBuilder.createTransaction(
                blockLimit,
                to,
                this.abiCodec.encodeMethod(abi, functionName, params),
                this.chainId,
                this.groupId);
    }

    private CallResponse parseCallResponseStatus(Call.CallOutput callOutput)
            throws TransactionBaseException {
        CallResponse callResponse = new CallResponse();
        RetCode retCode = ReceiptParser.parseCallOutput(callOutput, "");
        callResponse.setReturnCode(callOutput.getStatus());
        callResponse.setReturnMessage(retCode.getMessage());
        if (!retCode.getMessage().equals(PrecompiledRetCode.CODE_SUCCESS.getMessage())) {
            throw new TransactionBaseException(retCode);
        }
        return callResponse;
    }

    public ContractLoader getContractLoader() {
        return this.contractLoader;
    }
}
