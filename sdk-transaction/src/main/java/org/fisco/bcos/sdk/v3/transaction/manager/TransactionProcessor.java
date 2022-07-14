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

import java.util.Objects;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.encode.TransactionEncoderInterface;
import org.fisco.bcos.sdk.v3.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.StringUtils;
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
                        txAttribute);
        TransactionReceipt transactionReceipt =
                this.client.sendTransaction(txPair.getSignedTx(), false).getTransactionReceipt();
        if (Objects.nonNull(transactionReceipt)
                && (Objects.isNull(transactionReceipt.getTransactionHash())
                        || "".equals(transactionReceipt.getTransactionHash()))) {
            transactionReceipt.setTransactionHash(txPair.getTxHash());
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
        TxPair txPair =
                this.createSignedTransaction(
                        to,
                        data,
                        cryptoKeyPair == null ? this.cryptoKeyPair : cryptoKeyPair,
                        txAttribute);
        TransactionReceipt transactionReceipt =
                this.client.sendTransaction(txPair.getSignedTx(), false).getTransactionReceipt();
        if (Objects.nonNull(transactionReceipt)
                && StringUtils.isEmpty(transactionReceipt.getTransactionHash())) {
            transactionReceipt.setTransactionHash(txPair.getTxHash());
        }
        return transactionReceipt;
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(String to, byte[] data, int txAttribute)
            throws JniException {
        return sendTransactionAndGetReceipt(to, data, this.cryptoKeyPair, txAttribute);
    }

    @Override
    public String sendTransactionAsync(
            String to,
            byte[] data,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            TransactionCallback callback) {
        TxPair txPair = this.createSignedTransaction(to, data, cryptoKeyPair, txAttribute);
        this.client.sendTransactionAsync(txPair.getSignedTx(), false, callback);
        return txPair.getTxHash();
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
    public TxPair createDeploySignedTransaction(
            String to, byte[] data, String abi, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        try {

            if (log.isDebugEnabled()) {
                log.debug(
                        "createDeploySignedTransaction to: {}, abi: {}, attr: {}",
                        to,
                        abi,
                        txAttribute);
            }

            return TransactionBuilderJniObj.createSignedTransaction(
                    cryptoKeyPair.getJniKeyPair(),
                    this.groupId,
                    this.chainId,
                    Objects.nonNull(to) ? to : "",
                    Hex.toHexString(data),
                    Objects.nonNull(abi) ? abi : "",
                    client.getBlockLimit().longValue(),
                    txAttribute);
        } catch (JniException e) {
            log.error("jni e: ", e);
            return null;
        }
    }

    @Override
    public TxPair createSignedTransaction(
            String to, byte[] data, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        try {

            if (log.isDebugEnabled()) {
                log.debug("createSignedTransaction to: {}, attr: {}", to, txAttribute);
            }

            return TransactionBuilderJniObj.createSignedTransaction(
                    cryptoKeyPair.getJniKeyPair(),
                    this.groupId,
                    this.chainId,
                    Objects.nonNull(to) ? to : "",
                    Hex.toHexString(data),
                    "",
                    client.getBlockLimit().longValue(),
                    txAttribute);
        } catch (JniException e) {
            log.error("jni e: ", e);
            return null;
        }
    }
}
