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

import static org.fisco.bcos.sdk.client.protocol.model.Transaction.LIQUID_CREATE;
import static org.fisco.bcos.sdk.client.protocol.model.Transaction.LIQUID_SCALE_CODEC;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.codec.ABICodec;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
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
    public String deployOnly(String abi, String bin, List<Object> params) throws ABICodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params);
        this.transactionPusher.pushOnly(txPair.getSignedTx());
        return txPair.getTxHash();
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(byte[] data) {
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_CREATE | LIQUID_SCALE_CODEC;
        }

        TxPair txPair =
                this.createSignedTransaction(null, data, "", this.cryptoKeyPair, txAttribute);
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
        } catch (TransactionException | IOException | ABICodecException e) {
            log.error("deploy exception: {}", e.getMessage());
            return new TransactionResponse(
                    receipt, ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ABICodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params);
        return this.deployAndGetResponse(abi, txPair.getSignedTx());
    }

    @Override
    public TransactionResponse deployAndGetResponseWithStringParams(
            String abi, String bin, List<String> params, String path) throws ABICodecException {
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_CREATE | LIQUID_SCALE_CODEC;
        }

        TxPair txPair =
                this.createSignedTransaction(
                        path,
                        this.abiCodec.encodeConstructorFromString(abi, bin, params),
                        abi,
                        this.cryptoKeyPair,
                        txAttribute);

        return this.deployAndGetResponse(abi, txPair.getSignedTx());
    }

    @Override
    public String deployAsync(
            String abi, String bin, List<Object> params, TransactionCallback callback)
            throws ABICodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params);
        this.transactionPusher.pushAsync(txPair.getSignedTx(), callback);
        return txPair.getTxHash();
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params) throws ABICodecException, JniException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params);
        return this.transactionPusher.pushAsync(txPair.getSignedTx());
    }

    /**
     * Deploy by bin and abi files. Should init with contractLoader.
     *
     * @param contractName the contract name
     * @param args the params when deploy a contract
     * @return the transaction response
     * @throws TransactionBaseException send transaction exception
     * @throws ABICodecException abi encode exception
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
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        TxPair txPair =
                this.createSignedTransaction(to, data, abi, this.cryptoKeyPair, txAttribute);
        TransactionReceipt receipt = this.transactionPusher.push(txPair.getSignedTx());
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
            throws ABICodecException, TransactionBaseException, JniException {
        byte[] data =
                this.abiCodec.encodeMethod(
                        this.contractLoader.getABIByContractName(contractName), functionName, args);
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        return this.sendTransactionAndGetReceipt(
                contractAddress,
                data,
                this.contractLoader.getABIByContractName(contractName),
                this.cryptoKeyPair,
                txAttribute);
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
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        this.sendTransactionAsync(to, data, abi, this.cryptoKeyPair, txAttribute, callback);
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
        String abi = this.contractLoader.getABIByContractName(contractName);
        byte[] data = this.abiCodec.encodeMethod(abi, functionName, args);
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        this.sendTransactionAsync(
                contractAddress, data, abi, this.cryptoKeyPair, txAttribute, callback);
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
    public TxPair createSignedConstructor(String abi, String bin, List<Object> params)
            throws ABICodecException {
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_CREATE | LIQUID_SCALE_CODEC;
        }
        TxPair txPair =
                this.createSignedTransaction(
                        null,
                        this.abiCodec.encodeConstructor(abi, bin, params),
                        abi,
                        this.cryptoKeyPair,
                        txAttribute);
        return txPair;
    }

    @Override
    public byte[] encodeFunction(String abi, String functionName, List<Object> params)
            throws ABICodecException {
        return this.abiCodec.encodeMethod(abi, functionName, params);
    }

    @Override
    public long getRawTransactionForConstructor(String abi, String bin, List<Object> params)
            throws ABICodecException, JniException {
        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                "",
                Hex.toHexString(this.abiCodec.encodeConstructor(abi, bin, params)),
                abi,
                client.getBlockLimit().longValue());
    }

    @Override
    public long getRawTransactionForConstructor(
            BigInteger blockLimit, String abi, String bin, List<Object> params)
            throws ABICodecException, JniException {

        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                "",
                Hex.toHexString(this.abiCodec.encodeConstructor(abi, bin, params)),
                abi,
                blockLimit.longValue());
    }

    @Override
    public long getRawTransaction(String to, String abi, String functionName, List<Object> params)
            throws ABICodecException, JniException {

        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                to,
                Hex.toHexString(this.abiCodec.encodeMethod(abi, functionName, params)),
                "",
                client.getBlockLimit().longValue());
    }

    @Override
    public long getRawTransaction(
            BigInteger blockLimit, String to, String abi, String functionName, List<Object> params)
            throws ABICodecException, JniException {

        return TransactionBuilderJniObj.createTransactionData(
                this.groupId,
                this.chainId,
                to,
                Hex.toHexString(this.abiCodec.encodeMethod(abi, functionName, params)),
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
