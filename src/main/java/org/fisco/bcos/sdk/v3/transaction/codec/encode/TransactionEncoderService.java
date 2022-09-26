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
package org.fisco.bcos.sdk.v3.transaction.codec.encode;

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.v3.transaction.signer.TransactionSignerFactory;
import org.fisco.bcos.sdk.v3.transaction.signer.TransactionSignerInterface;
import org.fisco.bcos.sdk.v3.utils.Hex;
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
    public byte[] encode(long transactionData) throws JniException {

        String encodedTransactionData =
                TransactionBuilderJniObj.encodeTransactionData(transactionData);
        return Hex.decode(encodedTransactionData);
    }

    @Override
    public String encodeAndSign(long transactionData, CryptoKeyPair cryptoKeyPair, int attribute)
            throws JniException {
        return Hex.toHexString(this.encodeAndSignBytes(transactionData, cryptoKeyPair, attribute));
    }

    @Override
    public byte[] encodeAndHashBytes(long transactionData) throws JniException {
        byte[] encode = encode(transactionData);
        return this.cryptoSuite.hash(encode);
    }

    @Override
    public byte[] encodeAndSignBytes(
            long transactionData, CryptoKeyPair cryptoKeyPair, int attribute) throws JniException {
        byte[] hash = this.encodeAndHashBytes(transactionData);
        SignatureResult result = this.transactionSignerService.sign(hash, cryptoKeyPair);
        return this.encodeToTransactionBytes(transactionData, hash, result, attribute);
    }

    @Override
    public byte[] encodeToTransactionBytes(
            long transactionData, byte[] hash, SignatureResult result, int attribute)
            throws JniException {

        String signedTransaction =
                TransactionBuilderJniObj.createSignedTransaction(
                        transactionData,
                        Hex.toHexString(result.encode()),
                        Hex.toHexString(hash),
                        attribute);
        return Hex.decode(signedTransaction);
    }

    @Override
    public byte[] encodeToTransactionBytes(
            long transactionData, SignatureResult result, int attribute) throws JniException {
        byte[] hash = this.cryptoSuite.hash(encode(transactionData));
        return encodeToTransactionBytes(transactionData, hash, result, attribute);
    }

    /** @return the signature */
    public Signature getSignature() {
        return this.signature;
    }
}
