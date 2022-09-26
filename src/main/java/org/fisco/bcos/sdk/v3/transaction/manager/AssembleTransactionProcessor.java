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
package org.fisco.bcos.sdk.v3.transaction.manager;

import static org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute.LIQUID_CREATE;
import static org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute.LIQUID_SCALE_CODEC;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.RespCallback;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.v3.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.v3.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.v3.transaction.tools.ContractLoader;
import org.fisco.bcos.sdk.v3.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.v3.utils.Hex;
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
    protected final ContractCodec contractCodec;
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
        this.contractCodec = new ContractCodec(this.cryptoSuite, client.isWASM());
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
        this.contractCodec = new ContractCodec(this.cryptoSuite, client.isWASM());
        this.contractLoader = new ContractLoader(contractName, abi, bin);
    }

    @Override
    public String deployOnly(String abi, String bin, List<Object> params, String path)
            throws ContractCodecException {
        return deployOnly(abi, bin, params, path, this.cryptoKeyPair);
    }

    @Override
    public String deployOnly(String abi, String bin, List<Object> params)
            throws ContractCodecException {
        return deployOnly(abi, bin, params, "", this.cryptoKeyPair);
    }

    @Override
    public String deployOnly(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params, path, cryptoKeyPair);
        this.transactionPusher.pushOnly(txPair.getSignedTx());
        return txPair.getTxHash();
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(byte[] data, String abi, String path) {
        return deployAndGetReceipt(data, abi, path, this.cryptoKeyPair);
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(byte[] data) {
        return deployAndGetReceipt(data, "", "", this.cryptoKeyPair);
    }

    public TransactionReceipt deployAndGetReceipt(
            byte[] data, String abi, String path, CryptoKeyPair cryptoKeyPair) {
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_CREATE | LIQUID_SCALE_CODEC;
        }

        TxPair txPair =
                this.createDeploySignedTransaction(path, data, abi, cryptoKeyPair, txAttribute);
        TransactionReceipt transactionReceipt = this.transactionPusher.push(txPair.getSignedTx());
        if (Objects.nonNull(transactionReceipt)
                && ((Objects.isNull(transactionReceipt.getTransactionHash()))
                        || "".equals(transactionReceipt.getTransactionHash()))) {
            transactionReceipt.setTransactionHash(txPair.getTxHash());
        }
        return transactionReceipt;
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String signedData) {
        TransactionReceipt receipt = this.transactionPusher.push(signedData);
        try {
            return this.transactionDecoder.decodeReceiptWithoutValues(abi, receipt);
        } catch (ContractCodecException e) {
            log.error("deploy exception: ", e);
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse deployAndGetResponse(
            String abi, String bin, List<Object> params, String path)
            throws ContractCodecException {
        return deployAndGetResponse(abi, bin, params, path, this.cryptoKeyPair);
    }

    @Override
    public TransactionResponse deployAndGetResponse(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params, path, cryptoKeyPair);
        return this.deployAndGetResponse(abi, txPair.getSignedTx());
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ContractCodecException {
        return deployAndGetResponse(abi, bin, params, "", this.cryptoKeyPair);
    }

    @Override
    public TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params, String path)
            throws ContractCodecException {
        return deployAndGetResponseWithStringParams(abi, bin, params, path, this.cryptoKeyPair);
    }

    @Override
    public TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException {
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_CREATE | LIQUID_SCALE_CODEC;
        }

        TxPair txPair =
                this.createDeploySignedTransaction(
                        path,
                        this.contractCodec.encodeConstructorFromString(abi, bin, params),
                        abi,
                        cryptoKeyPair,
                        txAttribute);

        return this.deployAndGetResponse(abi, txPair.getSignedTx());
    }

    @Override
    public String deployAsync(
            String abi, String bin, List<Object> params, TransactionCallback callback)
            throws ContractCodecException {
        return deployAsync(abi, bin, params, "", this.cryptoKeyPair, callback);
    }

    @Override
    public String deployAsync(
            String abi, String bin, List<Object> params, String path, TransactionCallback callback)
            throws ContractCodecException {
        return deployAsync(abi, bin, params, path, this.cryptoKeyPair, callback);
    }

    @Override
    public String deployAsync(
            String abi,
            String bin,
            List<Object> params,
            String path,
            CryptoKeyPair cryptoKeyPair,
            TransactionCallback callback)
            throws ContractCodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params, path, cryptoKeyPair);
        this.transactionPusher.pushAsync(txPair.getSignedTx(), callback);
        return txPair.getTxHash();
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params, String path)
            throws ContractCodecException {
        return deployAsync(abi, bin, params, path, this.cryptoKeyPair);
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException {
        return deployAsync(abi, bin, params, "", this.cryptoKeyPair);
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params, path, cryptoKeyPair);
        return this.transactionPusher.pushAsync(txPair.getSignedTx());
    }

    /**
     * Deploy by bin and abi files. Should init with contractLoader.
     *
     * @param contractName the contract name
     * @param args the params when deploy a contract
     * @return the transaction response
     * @throws TransactionBaseException send transaction exception
     * @throws ContractCodecException abi encode exception
     * @throws NoSuchTransactionFileException Files related to abi codec were not found
     */
    @Override
    public TransactionResponse deployByContractLoader(String contractName, List<Object> args)
            throws ContractCodecException, TransactionBaseException {
        return this.deployAndGetResponse(
                this.contractLoader.getABIByContractName(contractName),
                this.contractLoader.getBinaryByContractName(contractName),
                args);
    }

    @Override
    public void deployByContractLoaderAsync(
            String contractName, List<Object> args, TransactionCallback callback)
            throws ContractCodecException, NoSuchTransactionFileException {
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
            String to, String abi, String functionName, byte[] data) {
        return sendTransactionAndGetResponse(to, abi, functionName, data, this.cryptoKeyPair);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, byte[] data, CryptoKeyPair cryptoKeyPair) {
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        TxPair txPair = this.createSignedTransaction(to, data, cryptoKeyPair, txAttribute);
        TransactionReceipt receipt = this.transactionPusher.push(txPair.getSignedTx());
        try {
            return this.transactionDecoder.decodeReceiptWithValues(abi, functionName, receipt);
        } catch (ContractCodecException e) {
            log.error("sendTransaction exception: ", e);
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException {
        byte[] data = this.encodeFunction(abi, functionName, params);
        return this.sendTransactionAndGetResponse(to, abi, functionName, data);
    }

    @Override
    public TransactionResponse sendTransactionWithStringParamsAndGetResponse(
            String to, String abi, String functionName, List<String> params)
            throws ContractCodecException {
        byte[] data = this.contractCodec.encodeMethodFromString(abi, functionName, params);
        return this.sendTransactionAndGetResponse(to, abi, functionName, data);
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws ContractCodecException, TransactionBaseException {
        byte[] data =
                this.contractCodec.encodeMethod(
                        this.contractLoader.getABIByContractName(contractName), functionName, args);
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        return this.sendTransactionAndGetReceipt(
                contractAddress, data, this.cryptoKeyPair, txAttribute);
    }

    @Override
    public TransactionResponse sendTransactionAndGetResponseByContractLoader(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> funcParams)
            throws ContractCodecException, TransactionBaseException {
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
            throws ContractCodecException {
        byte[] data = this.encodeFunction(abi, functionName, params);
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        this.sendTransactionAsync(to, data, this.cryptoKeyPair, txAttribute, callback);
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
            throws ContractCodecException, TransactionBaseException {
        String abi = this.contractLoader.getABIByContractName(contractName);
        byte[] data = this.contractCodec.encodeMethod(abi, functionName, args);
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        this.sendTransactionAsync(contractAddress, data, this.cryptoKeyPair, txAttribute, callback);
    }

    @Override
    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException, ContractCodecException {
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
            throws TransactionBaseException, ContractCodecException {
        byte[] data = this.contractCodec.encodeMethod(abi, functionName, paramsList);
        return this.callAndGetResponse(from, to, abi, functionName, data);
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest)
            throws TransactionBaseException, ContractCodecException {
        Call call = this.executeCall(callRequest);
        CallResponse callResponse = this.parseCallResponseStatus(call.getCallResult());
        String callOutput = call.getCallResult().getOutput();
        Pair<List<Object>, List<ABIObject>> results =
                this.contractCodec.decodeMethodAndGetOutputObject(callRequest.getAbi(), callOutput);
        callResponse.setValues(JsonUtils.toJson(results.getLeft()));
        callResponse.setReturnObject(results.getLeft());
        callResponse.setReturnABIObject(results.getRight());
        return callResponse;
    }

    @Override
    public void sendCallAsync(
            String from,
            String to,
            String abi,
            String functionName,
            List<Object> params,
            RespCallback<CallResponse> callback)
            throws ContractCodecException {
        byte[] data = this.contractCodec.encodeMethod(abi, functionName, params);
        callAndGetResponseAsync(from, to, abi, functionName, data, callback);
    }

    @Override
    public void sendCallAsync(CallRequest callRequest, RespCallback<CallResponse> callback) {
        this.asyncExecuteCall(
                callRequest,
                new RespCallback<Call>() {
                    @Override
                    public void onResponse(Call call) {
                        try {
                            CallResponse callResponse =
                                    parseCallResponseStatus(call.getCallResult());
                            String callOutput = call.getCallResult().getOutput();
                            Pair<List<Object>, List<ABIObject>> results =
                                    contractCodec.decodeMethodAndGetOutputObject(
                                            callRequest.getAbi(), callOutput);
                            callResponse.setValues(JsonUtils.toJson(results.getLeft()));
                            callResponse.setReturnObject(results.getLeft());
                            callResponse.setReturnABIObject(results.getRight());
                            callback.onResponse(callResponse);
                        } catch (TransactionBaseException | ContractCodecException e) {
                            Response response = new Response();
                            response.setErrorMessage(e.getMessage());
                            response.setErrorCode(-5000);
                            callback.onError(response);
                        }
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse);
                    }
                });
    }

    @Override
    public CallResponse sendCallWithStringParams(
            String from, String to, String abi, String functionName, List<String> paramsList)
            throws TransactionBaseException, ContractCodecException {
        byte[] data = this.contractCodec.encodeMethodFromString(abi, functionName, paramsList);
        return this.callAndGetResponse(from, to, abi, functionName, data);
    }

    @Override
    public void sendCallWithStringParamsAsync(
            String from,
            String to,
            String abi,
            String functionName,
            List<String> params,
            RespCallback<CallResponse> callback)
            throws TransactionBaseException, ContractCodecException {
        byte[] data = this.contractCodec.encodeMethodFromString(abi, functionName, params);
        callAndGetResponseAsync(from, to, abi, functionName, data, callback);
    }

    private void callAndGetResponseAsync(
            String from,
            String to,
            String abi,
            String functionName,
            byte[] data,
            RespCallback<CallResponse> callback) {
        this.asyncExecuteCall(
                from,
                to,
                data,
                new RespCallback<Call>() {
                    @Override
                    public void onResponse(Call call) {
                        try {
                            CallResponse callResponse =
                                    parseCallResponseStatus(call.getCallResult());
                            List<Type> decodedResult =
                                    contractCodec.decodeMethodAndGetOutputObject(
                                            abi, functionName, call.getCallResult().getOutput());
                            callResponse.setResults(decodedResult);
                            callback.onResponse(callResponse);
                        } catch (TransactionBaseException | ContractCodecException e) {
                            Response response = new Response();
                            response.setErrorMessage(e.getMessage());
                            response.setErrorCode(-5000);
                            callback.onError(response);
                        }
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse);
                    }
                });
    }

    public CallResponse callAndGetResponse(
            String from, String to, String abi, String functionName, byte[] data)
            throws ContractCodecException, TransactionBaseException {
        Call call = this.executeCall(from, to, data);
        CallResponse callResponse = this.parseCallResponseStatus(call.getCallResult());
        List<Type> decodedResult =
                this.contractCodec.decodeMethodAndGetOutputObject(
                        abi, functionName, call.getCallResult().getOutput());
        callResponse.setResults(decodedResult);
        return callResponse;
    }

    @Override
    public TxPair createSignedConstructor(String abi, String bin, List<Object> params, String path)
            throws ContractCodecException {
        return createSignedConstructor(abi, bin, params, path, this.cryptoKeyPair);
    }

    @Override
    public TxPair createSignedConstructor(
            String abi, String bin, List<Object> params, String path, CryptoKeyPair cryptoKeyPair)
            throws ContractCodecException {
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_CREATE | LIQUID_SCALE_CODEC;
        }
        return this.createDeploySignedTransaction(
                Objects.nonNull(path) ? path : "",
                this.contractCodec.encodeConstructor(abi, bin, params),
                abi,
                cryptoKeyPair == null ? this.cryptoKeyPair : cryptoKeyPair,
                txAttribute);
    }

    @Override
    public byte[] encodeFunction(String abi, String functionName, List<Object> params)
            throws ContractCodecException {
        return this.contractCodec.encodeMethod(abi, functionName, params);
    }

    @Override
    public long getRawTransactionForConstructor(String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException {
        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                "",
                Hex.toHexString(this.contractCodec.encodeConstructor(abi, bin, params)),
                abi,
                client.getBlockLimit().longValue());
    }

    @Override
    public long getRawTransactionForConstructor(
            BigInteger blockLimit, String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException {

        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                "",
                Hex.toHexString(this.contractCodec.encodeConstructor(abi, bin, params)),
                abi,
                blockLimit.longValue());
    }

    @Override
    public long getRawTransaction(String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, JniException {

        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                to,
                Hex.toHexString(this.contractCodec.encodeMethod(abi, functionName, params)),
                "",
                client.getBlockLimit().longValue());
    }

    @Override
    public long getRawTransaction(
            BigInteger blockLimit, String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, JniException {

        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                to,
                Hex.toHexString(this.contractCodec.encodeMethod(abi, functionName, params)),
                "",
                blockLimit.longValue());
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
