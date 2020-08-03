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
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.builder.TransactionBuilderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionRequest;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerInterface;
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
    private CryptoInterface cryptoInterface;
    private TransactionBuilderInterface transactionBuilder;
    private TransactionPusherInterface transactionPusher;
    private TransactionDecoderInterface transactionDecoder;
    private TransactionSignerInterface transactionSigner;
    private TransactionEncoderService transactionEncoder;
    private SecureRandom secureRandom;
    private Map<Integer, Client> clients;

    @Override
    public TransactionResponse deploy(TransactionRequest transactionRequest) {
        String contract = transactionRequest.getContractName();
        TransactionReceipt receipt = transactionPusher.push(transactionRequest.getSignedData());
        try {
            TransactionResponse response =
                    transactionDecoder.decodeTransactionReceipt(contract, receipt);
            return response;
        } catch (TransactionBaseException | TransactionException | IOException e) {
            log.error("deploy exception: {}", e.getMessage());
            return new TransactionResponse(
                    ResultCodeEnum.EXCEPTION_OCCUR.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendTransaction(TransactionRequest transactionRequest) {
        this.transactionPusher.pushOnly(transactionRequest.getSignedData());
    }

    @Override
    public void sendTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            BigInteger chainId,
            BigInteger groupId,
            TransactionCallback callback) {
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        gasPrice, gasLimit, to, data, value, chainId, groupId, "");
        byte[] signedTransaction =
                transactionEncoder.signMessage(rawTransaction, cryptoInterface.getKeyPairFactory());
        this.sendTransactionAsync(signedTransaction.toString(), callback);
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
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            TransactionRequest transactionRequest) {
        return this.transactionPusher.pushAsync(transactionRequest.getSignedData());
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest) {
        // TODO
        return null;
    }

    @Override
    public String getCurrentExternalAccountAddress() {
        // TODO
        return null;
    }
}
