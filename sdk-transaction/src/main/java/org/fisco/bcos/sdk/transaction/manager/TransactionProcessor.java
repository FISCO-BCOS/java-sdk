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

import java.math.BigInteger;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.builder.TransactionBuilderInterface;
import org.fisco.bcos.sdk.transaction.builder.TransactionBuilderService;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderInterface;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.gas.DefaultGasProvider;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionProcessor implements TransactionProcessorInterface {
    protected static Logger log = LoggerFactory.getLogger(TransactionProcessor.class);
    protected final CryptoSuite cryptoSuite;
    protected final CryptoKeyPair cryptoKeyPair;
    protected final Client client;
    protected final Integer groupId;
    protected final String chainId;
    protected final TransactionBuilderInterface transactionBuilder;
    protected TransactionEncoderInterface transactionEncoder;

    public TransactionProcessor(
            Client client, CryptoKeyPair cryptoKeyPair, Integer groupId, String chainId) {
        this.cryptoSuite = client.getCryptoSuite();
        this.cryptoKeyPair = cryptoKeyPair;
        this.client = client;
        this.groupId = groupId;
        this.chainId = chainId;
        this.transactionBuilder = new TransactionBuilderService(client);
        this.transactionEncoder = new TransactionEncoderService(client.getCryptoSuite());
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(
            String to, String data, CryptoKeyPair cryptoKeyPair) {
        if (cryptoKeyPair == null) {
            return this.client.sendRawTransactionAndGetReceipt(
                    createSignedTransaction(to, data, this.cryptoKeyPair));
        } else {
            return this.client.sendRawTransactionAndGetReceipt(
                    createSignedTransaction(to, data, cryptoKeyPair));
        }
    }

    public TransactionReceipt sendTransactionAndGetReceipt(String to, String data) {
        return sendTransactionAndGetReceipt(to, data, this.cryptoKeyPair);
    }

    @Override
    public void sendTransactionAsync(
            String to, String data, CryptoKeyPair cryptoKeyPair, TransactionCallback callback) {
        if (cryptoKeyPair == null) {
            client.sendRawTransactionAndGetReceiptAsync(
                    createSignedTransaction(to, data, this.cryptoKeyPair), callback);
        }
        client.sendRawTransactionAndGetReceiptAsync(
                createSignedTransaction(to, data, cryptoKeyPair), callback);
    }

    public void sendTransactionAsync(String to, String data, TransactionCallback callback) {
        sendTransactionAsync(to, data, this.cryptoKeyPair, callback);
    }

    @Override
    public byte[] sendTransactionAsyncAndGetHash(
            String to, String data, CryptoKeyPair cryptoKeyPair, TransactionCallback callback) {
        String signedData;
        if (cryptoKeyPair == null) {
            signedData = createSignedTransaction(to, data, this.cryptoKeyPair);
        } else {
            signedData = createSignedTransaction(to, data, cryptoKeyPair);
        }
        client.sendRawTransactionAndGetReceiptAsync(signedData, callback);
        byte[] transactionHash = cryptoSuite.hash(Hex.decode(Numeric.cleanHexPrefix(signedData)));
        return transactionHash;
    }

    public byte[] sendTransactionAsyncAndGetHash(
            String to, String data, TransactionCallback callback) {
        return sendTransactionAsyncAndGetHash(to, data, this.cryptoKeyPair, callback);
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
    public String createSignedTransaction(String to, String data, CryptoKeyPair cryptoKeyPair) {
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
        if (cryptoKeyPair == null) {
            return transactionEncoder.encodeAndSign(rawTransaction, this.cryptoKeyPair);
        }
        return transactionEncoder.encodeAndSign(rawTransaction, cryptoKeyPair);
    }

    public String createSignedTransaction(String to, String data) {
        return createSignedTransaction(to, data, this.cryptoKeyPair);
    }
}
