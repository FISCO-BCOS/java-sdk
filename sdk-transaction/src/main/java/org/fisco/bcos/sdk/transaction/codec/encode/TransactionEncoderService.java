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
package org.fisco.bcos.sdk.transaction.codec.encode;

import com.qq.tars.protocol.tars.TarsOutputStream;
import org.fisco.bcos.sdk.client.protocol.model.tars.Transaction;
import org.fisco.bcos.sdk.client.protocol.model.tars.TransactionData;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerFactory;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerInterface;
import org.fisco.bcos.sdk.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionEncoderService implements TransactionEncoderInterface {
    protected static Logger logger = LoggerFactory.getLogger(TransactionEncoderService.class);
    private final Signature signature;
    private final TransactionSignerInterface transactionSignerService;
    private final CryptoSuite cryptoSuite;

    public TransactionEncoderService(CryptoSuite cryptoSuite) {
        super();
        this.cryptoSuite = cryptoSuite;
        this.signature = cryptoSuite.getSignatureImpl();
        this.transactionSignerService =
                TransactionSignerFactory.createTransactionSigner(this.signature);
    }

    public TransactionEncoderService(
            CryptoSuite cryptoSuite, RemoteSignProviderInterface transactionSignProvider) {
        super();
        this.cryptoSuite = cryptoSuite;
        this.signature = cryptoSuite.getSignatureImpl();
        this.transactionSignerService =
                TransactionSignerFactory.createTransactionSigner(
                        transactionSignProvider, cryptoSuite.getCryptoTypeConfig());
    }

    @Override
    public byte[] encode(TransactionData rawTransaction) {
        TarsOutputStream tarsOutputStream = new TarsOutputStream();
        rawTransaction.writeTo(tarsOutputStream);
        return tarsOutputStream.toByteArray();
    }

    @Override
    public String encodeAndSign(
            TransactionData rawTransaction, CryptoKeyPair cryptoKeyPair, int attribute) {
        return Hex.toHexString(this.encodeAndSignBytes(rawTransaction, cryptoKeyPair, attribute));
    }

    @Override
    public byte[] encodeAndHashBytes(TransactionData rawTransaction) {
        TarsOutputStream tarsOutputStream = new TarsOutputStream();
        rawTransaction.writeTo(tarsOutputStream);
        return this.cryptoSuite.hash(tarsOutputStream.toByteArray());
    }

    @Override
    public byte[] encodeAndSignBytes(
            TransactionData rawTransaction, CryptoKeyPair cryptoKeyPair, int attribute) {
        byte[] hash = this.encodeAndHashBytes(rawTransaction);
        SignatureResult result = this.transactionSignerService.sign(hash, cryptoKeyPair);
        return this.encodeToTransactionBytes(rawTransaction, hash, result, attribute);
    }

    @Override
    public byte[] encodeToTransactionBytes(
            TransactionData rawTransaction, byte[] hash, SignatureResult result, int attribute) {
        Transaction transaction =
                new Transaction(rawTransaction, hash, result.encode(), 0, attribute, null);
        TarsOutputStream tarsOutputStream = new TarsOutputStream();
        transaction.writeTo(tarsOutputStream);
        return tarsOutputStream.toByteArray();
    }

    @Override
    public byte[] encodeToTransactionBytes(
            TransactionData rawTransaction, SignatureResult result, int attribute) {
        byte[] hash = this.cryptoSuite.hash(encode(rawTransaction));
        return encodeToTransactionBytes(rawTransaction, hash, result, attribute);
    }

    /** @return the signature */
    public Signature getSignature() {
        return this.signature;
    }
}
