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

import com.webank.pkeysign.utils.Numeric;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.RespCallback;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.TransactionEncoder;
import org.fisco.bcos.sdk.transaction.domain.RawTransaction;
import org.fisco.bcos.sdk.transaction.domain.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.domain.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.domain.dto.TransactionRequest;
import org.fisco.bcos.sdk.transaction.domain.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.pusher.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerInterface;

/**
 * TransactionManager @Description: TransactionManager
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:23:19 PM
 */
public class TransactionManager implements TransactionManagerInterface {

    private TransactionPusherInterface transactionPusher;

    private TransactionDecoderInterface transactionDecoder;

    private TransactionSignerInterface transactionSigner;

    private TransactionEncoder transactionEncoder;

    private SecureRandom secureRandom;

    private Map<Integer, Client> clients;

    @Override
    public TransactionResponse deploy(TransactionRequest transactionRequest) {
        String contract = transactionRequest.getContractName();
        TransactionReceipt receipt =
                this.transactionPusher.push(transactionRequest.getSignedData());
        TransactionResponse response =
                transactionDecoder.decodeTransactionReceipt(contract, receipt);
        return TransactionResponse.from(response);
    }

    @Override
    public void sendTransactionOnly(TransactionRequest transactionRequest) {
        this.transactionPusher.pushOnly(transactionRequest.getSignedData());
    }

    @Override
    public TransactionResponse sendTransaction(TransactionRequest transactionRequest) {
        String contract = transactionRequest.getContractName();
        TransactionReceipt receipt =
                this.transactionPusher.push(transactionRequest.getSignedData());
        TransactionResponse response =
                transactionDecoder.decodeTransactionReceipt(contract, receipt);
        return TransactionResponse.from(response);
    }

    @Override
    public void sendTransaction(
            String signedTransaction, RespCallback<TransactionReceipt> callback) {
        this.transactionPusher.pushAsync(signedTransaction, callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            TransactionRequest transactionRequest) {
        return this.transactionPusher.pushAsync(transactionRequest.getSignedData());
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest) {
        return null;
    }

    @Override
    public String getCurrentExternalAccountAddress() {
        return null;
    }

    /**
     * TODO
     *
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param data
     * @param value
     * @return
     */
    public RawTransaction createTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            BigInteger chainId,
            BigInteger groupId,
            String extraData) {
        BigInteger randomId = new BigInteger(250, secureRandom);
        Client client = this.clients.get(groupId);
        if (client == null) {
            throw new IllegalArgumentException("Invalid groupId " + groupId);
        }
        BigInteger blockLimit = client.getBlockLimit();
        return RawTransaction.createTransaction(
                randomId,
                gasPrice,
                gasLimit,
                blockLimit,
                to,
                value,
                data,
                chainId,
                groupId,
                extraData);
    }

    /**
     * TODO
     *
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param data
     * @param value
     * @param object
     * @param callback
     */
    public void sendTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            BigInteger chainId,
            BigInteger groupId,
            RespCallback<TransactionReceipt> callback) {
        RawTransaction transaction =
                this.createTransaction(gasPrice, gasLimit, to, data, value, chainId, groupId, "");
        String signedTransaction = this.sign(transaction);
        this.sendTransaction(signedTransaction, callback);
    }

    /**
     * TODO
     *
     * @param rawTransaction
     * @return
     */
    public String sign(RawTransaction rawTransaction) {
        byte[] bytes = this.transactionEncoder.encode(rawTransaction, null);
        SignatureResult signatureResult = this.transactionSigner.sign(bytes);
        byte[] encoded = this.transactionEncoder.encode(rawTransaction, signatureResult);
        return Numeric.toHexString(encoded);
    }

    /**
     * TODO
     *
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param data
     * @param value
     * @param object
     * @return
     */
    public TransactionReceipt executeTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            BigInteger chainId,
            BigInteger groupId,
            Object object) {
        RawTransaction transaction =
                this.createTransaction(gasPrice, gasLimit, to, data, value, chainId, groupId, "");
        String signedTransaction = this.sign(transaction);
        return this.transactionPusher.push(signedTransaction);
    }
}
