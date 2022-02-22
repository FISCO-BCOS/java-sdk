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

import java.util.Objects;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderInterface;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionProcessor implements TransactionProcessorInterface {
    protected static Logger log = LoggerFactory.getLogger(TransactionProcessor.class);
    protected final CryptoSuite cryptoSuite;
    protected final CryptoKeyPair cryptoKeyPair;
    protected final Client client;
    protected final String groupId;
    protected final String chainId;
    protected TransactionEncoderInterface transactionEncoder;

    public TransactionProcessor(
            Client client, CryptoKeyPair cryptoKeyPair, String groupId, String chainId) {
        this.cryptoSuite = client.getCryptoSuite();
        this.cryptoKeyPair = cryptoKeyPair;
        this.client = client;
        this.groupId = groupId;
        this.chainId = chainId;
        this.transactionEncoder = new TransactionEncoderService(client.getCryptoSuite());
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(
            String to, byte[] data, String abi, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        TxPair txPair = this.createSignedTransaction(to, data, abi, cryptoKeyPair, txAttribute);
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
    public String sendTransactionAsync(
            String to,
            byte[] data,
            String abi,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            TransactionCallback callback) {
        TxPair txPair = this.createSignedTransaction(to, data, abi, cryptoKeyPair, txAttribute);
        this.client.sendTransactionAsync(txPair.getSignedTx(), false, callback);
        return txPair.getTxHash();
    }

    @Override
    public Call executeCall(CallRequest callRequest) {
        return this.executeCall(
                callRequest.getFrom(), callRequest.getTo(), callRequest.getEncodedFunction());
    }

    @Override
    public Call executeCall(String from, String to, byte[] encodedFunction) {
        log.info("encoded function: {}", Hex.toHexString(encodedFunction));
        return this.client.call(new Transaction(from, to, encodedFunction));
    }

    @Override
    public TxPair createSignedTransaction(
            String to, byte[] data, String abi, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        try {

            if (log.isDebugEnabled()) {
                log.debug("to: {}, abi: {}, attr: {}", to, abi, txAttribute);
            }

            return TransactionBuilderJniObj.createSignedTransaction(
                    cryptoKeyPair.getJniKeyPair(),
                    this.groupId,
                    this.chainId,
                    Objects.nonNull(to) ? to : "",
                    Hex.toHexString(data),
                    Objects.isNull(to) ? (Objects.nonNull(abi) ? abi : "") : "",
                    client.getBlockLimit().longValue(),
                    txAttribute);
        } catch (JniException e) {
            log.error("jni e: ", e);
            return null;
        }
    }
}
