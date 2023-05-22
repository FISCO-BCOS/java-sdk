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

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.rpc.RpcServiceJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.ClientImpl;
import org.fisco.bcos.sdk.v3.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.ResponseCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.encode.TransactionEncoderInterface;
import org.fisco.bcos.sdk.v3.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionProcessor implements TransactionProcessorInterface {
    protected static Logger log = LoggerFactory.getLogger(TransactionProcessor.class);
    protected final CryptoSuite cryptoSuite;
    protected final Client client;
    protected final String groupId;
    protected final String chainId;
    protected TransactionEncoderInterface transactionEncoder;
    protected CryptoKeyPair cryptoKeyPair;

    public TransactionProcessor(
            Client client, CryptoKeyPair cryptoKeyPair, String groupId, String chainId) {
        this.cryptoSuite = client.getCryptoSuite();
        this.cryptoKeyPair = cryptoKeyPair;
        this.client = client;
        this.groupId = groupId;
        this.chainId = chainId;
        this.transactionEncoder = new TransactionEncoderService(client.getCryptoSuite());
    }

    public CryptoKeyPair getCryptoKeyPair() {
        return cryptoKeyPair;
    }

    public void setCryptoKeyPair(CryptoKeyPair cryptoKeyPair) {
        this.cryptoKeyPair = cryptoKeyPair;
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(
            String to, byte[] data, String abi, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        TxPair txPair =
                this.createDeploySignedTransaction(
                        to,
                        data,
                        abi,
                        cryptoKeyPair == null ? this.cryptoKeyPair : cryptoKeyPair,
                        txAttribute,
                        client.getExtraData());
        TransactionReceipt transactionReceipt =
                this.client.sendTransaction(txPair.getSignedTx(), false).getTransactionReceipt();
        if (Objects.nonNull(transactionReceipt)
                && (Objects.isNull(transactionReceipt.getTransactionHash())
                        || "".equals(transactionReceipt.getTransactionHash()))) {
            transactionReceipt.setTransactionHash(txPair.getTxHash());
        }
        if (Objects.nonNull(transactionReceipt)
                && (Objects.isNull(transactionReceipt.getInput())
                        || transactionReceipt.getInput().isEmpty())) {
            transactionReceipt.setInput(Hex.toHexStringWithPrefix(data));
        }
        return transactionReceipt;
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(
            String to, byte[] data, String abi, int txAttribute) throws JniException {
        return deployAndGetReceipt(to, data, abi, this.cryptoKeyPair, txAttribute);
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(
            String to, byte[] data, CryptoKeyPair cryptoKeyPair, int txAttribute) {

        CompletableFuture<TransactionReceipt> future = new CompletableFuture<>();
        sendTransactionAsync(
                to,
                data,
                cryptoKeyPair,
                txAttribute,
                new TransactionCallback() {
                    @Override
                    public void onResponse(TransactionReceipt receipt) {
                        if (Objects.nonNull(receipt)
                                && (Objects.isNull(receipt.getInput())
                                        || receipt.getInput().isEmpty())) {
                            receipt.setInput(Hex.toHexStringWithPrefix(data));
                        }
                        future.complete(receipt);
                    }
                });

        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return transactionReceipt;

        /*
        TxPair txPair =
                this.createSignedTransaction(
                        to,
                        data,
                        cryptoKeyPair == null ? this.cryptoKeyPair : cryptoKeyPair,
                        txAttribute,
                        client.getExtraData());
        TransactionReceipt transactionReceipt =
                this.client.sendTransaction(txPair.getSignedTx(), false).getTransactionReceipt();
        if (Objects.nonNull(transactionReceipt)
                && StringUtils.isEmpty(transactionReceipt.getTransactionHash())) {
            transactionReceipt.setTransactionHash(txPair.getTxHash());
        }

        return transactionReceipt;
        */
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(
            String to, byte[] data, int txAttribute) {
        return sendTransactionAndGetReceipt(to, data, this.cryptoKeyPair, txAttribute);
    }

    @Override
    public String sendTransactionAsync(
            String to,
            byte[] data,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            TransactionCallback callback) {
        /*
        TxPair txPair =
                this.createSignedTransaction(
                        to, data, cryptoKeyPair, txAttribute, client.getExtraData());
        this.client.sendTransactionAsync(txPair.getSignedTx(), false, callback);
        */
        String extraData = client.getExtraData();
        String txHash =
                RpcServiceJniObj.sendTransaction(
                        this.client.getNativePointer(),
                        cryptoKeyPair.getJniKeyPair(),
                        this.groupId,
                        "",
                        Objects.nonNull(to) ? to : "",
                        data,
                        "",
                        txAttribute,
                        Objects.nonNull(extraData) ? extraData : "",
                        resp -> {
                            org.fisco.bcos.sdk.v3.model.Response response =
                                    new org.fisco.bcos.sdk.v3.model.Response();
                            response.setErrorCode(resp.getErrorCode());
                            response.setErrorMessage(resp.getErrorMessage());
                            response.setContent(resp.getData());

                            ResponseCallback responseCallback =
                                    ClientImpl.createResponseCallback(
                                            "sendTransaction",
                                            BcosTransactionReceipt.class,
                                            new RespCallback<BcosTransactionReceipt>() {
                                                @Override
                                                public void onResponse(
                                                        BcosTransactionReceipt
                                                                transactionReceiptWithProof) {
                                                    if (Objects.nonNull(
                                                                    transactionReceiptWithProof
                                                                            .getTransactionReceipt())
                                                            && (Objects.isNull(
                                                                            transactionReceiptWithProof
                                                                                    .getTransactionReceipt()
                                                                                    .getInput())
                                                                    || transactionReceiptWithProof
                                                                            .getTransactionReceipt()
                                                                            .getInput()
                                                                            .isEmpty())) {
                                                        transactionReceiptWithProof
                                                                .getTransactionReceipt()
                                                                .setInput(
                                                                        Hex.toHexStringWithPrefix(
                                                                                data));
                                                    }
                                                    callback.onResponse(
                                                            transactionReceiptWithProof
                                                                    .getTransactionReceipt());
                                                }

                                                @Override
                                                public void onError(
                                                        org.fisco.bcos.sdk.v3.model.Response
                                                                errorResponse) {
                                                    callback.onError(
                                                            errorResponse.getErrorCode(),
                                                            errorResponse.getErrorMessage());
                                                }
                                            });
                            responseCallback.onResponse(response);
                        });

        if (log.isDebugEnabled()) {
            log.debug(
                    "sendTransactionAsync, group: {}, to: {}, tx hash: {}",
                    this.groupId,
                    to,
                    txHash);
        }

        return txHash;
    }

    @Override
    public String sendTransactionAsync(
            String to, byte[] data, int txAttribute, TransactionCallback callback) {
        return sendTransactionAsync(to, data, this.cryptoKeyPair, txAttribute, callback);
    }

    @Override
    public Call executeCall(CallRequest callRequest) {
        return this.executeCall(
                callRequest.getFrom(), callRequest.getTo(), callRequest.getEncodedFunction());
    }

    @Override
    public Call executeCall(String from, String to, byte[] encodedFunction) {
        return this.client.call(new Transaction(from, to, encodedFunction));
    }

    @Override
    public Call executeCallWithSign(String from, String to, byte[] encodedFunction) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (client.isWASM()) {
                outputStream.write(Hex.decode(cryptoSuite.hash(to)));
            } else {
                outputStream.write(Hex.decode(to));
            }
            outputStream.write(encodedFunction);
            byte[] hash = this.cryptoSuite.hash(outputStream.toByteArray());
            SignatureResult sign = this.cryptoSuite.sign(hash, this.cryptoSuite.getCryptoKeyPair());
            return this.client.call(
                    new Transaction(from, to, encodedFunction), Hex.toHexString(sign.encode()));
        } catch (Exception e) {
            log.error(
                    "Sign call data failed: {}, to: {}, data:{}",
                    e.getMessage(),
                    to,
                    Hex.toHexString(encodedFunction),
                    e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Call executeCallWithSign(String from, String to, byte[] encodedFunction, String sign) {
        return this.client.call(new Transaction(from, to, encodedFunction), sign);
    }

    @Override
    public void asyncExecuteCall(
            String from, String to, byte[] encodedFunction, RespCallback<Call> callback) {
        this.client.callAsync(new Transaction(from, to, encodedFunction), callback);
    }

    @Override
    public void asyncExecuteCall(CallRequest callRequest, RespCallback<Call> callback) {
        this.asyncExecuteCall(
                callRequest.getFrom(),
                callRequest.getTo(),
                callRequest.getEncodedFunction(),
                callback);
    }

    @Override
    public void asyncExecuteCallWithSign(
            String from, String to, byte[] encodedFunction, RespCallback<Call> callback) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(to.getBytes());
            outputStream.write(encodedFunction);
            byte[] hash = this.cryptoSuite.hash(outputStream.toByteArray());
            SignatureResult sign = this.cryptoSuite.sign(hash, this.cryptoSuite.getCryptoKeyPair());
            this.client.callAsync(
                    new Transaction(from, to, encodedFunction), sign.toString(), callback);
        } catch (Exception e) {
            log.error(
                    "Sign call data failed: {}, to: {}, data:{}",
                    e.getMessage(),
                    to,
                    Hex.toHexString(encodedFunction),
                    e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TxPair createDeploySignedTransaction(
            String to, byte[] data, String abi, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        return createDeploySignedTransaction(
                to, data, abi, cryptoKeyPair, txAttribute, client.getExtraData());
    }

    @Override
    public TxPair createSignedTransaction(
            String to, byte[] data, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        return createSignedTransaction(to, data, cryptoKeyPair, txAttribute, client.getExtraData());
    }

    @Override
    public TxPair createDeploySignedTransaction(
            String to,
            byte[] data,
            String abi,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            String extraData) {
        try {
            if (log.isDebugEnabled()) {
                log.debug(
                        "createDeploySignedTransaction to: {}, abi: {}, attr: {}, extraData: {}",
                        to,
                        abi,
                        txAttribute,
                        extraData);
            }
            return TransactionBuilderJniObj.createSignedTransaction(
                    cryptoKeyPair.getJniKeyPair(),
                    this.groupId,
                    this.chainId,
                    Objects.nonNull(to) ? to : "",
                    Hex.toHexString(data),
                    Objects.nonNull(abi) ? abi : "",
                    client.getBlockLimit().longValue(),
                    txAttribute,
                    Objects.nonNull(extraData) ? extraData : "");
        } catch (JniException e) {
            log.error("jni e: ", e);
            return null;
        }
    }

    @Override
    public TxPair createSignedTransaction(
            String to,
            byte[] data,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            String extraData) {
        try {
            if (log.isDebugEnabled()) {
                log.debug(
                        "createSignedTransaction to: {}, attr: {}, extraData: {}",
                        to,
                        txAttribute,
                        extraData);
            }

            return TransactionBuilderJniObj.createSignedTransaction(
                    cryptoKeyPair.getJniKeyPair(),
                    this.groupId,
                    this.chainId,
                    Objects.nonNull(to) ? to : "",
                    Hex.toHexString(data),
                    "",
                    client.getBlockLimit().longValue(),
                    txAttribute,
                    Objects.nonNull(extraData) ? extraData : "");
        } catch (JniException e) {
            log.error("jni e: ", e);
            return null;
        }
    }
}
