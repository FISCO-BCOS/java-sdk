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
package org.fisco.bcos.sdk.transaction.signer;

import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

public class TransactionSignerWithRemoteSignService
        implements TransactionSignerInterface, AsyncTransactionSignercInterface {
    private RemoteSignProviderInterface transactionSignProvider;
    private int encryptType;

    public TransactionSignerWithRemoteSignService(
            RemoteSignProviderInterface transactionSignProvider, int encryptType) {
        this.transactionSignProvider = transactionSignProvider;
        this.encryptType = encryptType;
    }

    @Override
    public SignatureResult sign(String rawTxHash, CryptoKeyPair cryptoKeyPair) {
        return sign(rawTxHash.getBytes(), cryptoKeyPair);
    }

    @Override
    public SignatureResult sign(byte[] rawTxHash, CryptoKeyPair cryptoKeyPair) {
        String signatureStr = transactionSignProvider.requestForSign(rawTxHash, encryptType);
        return TransactionSignerServcie.decodeSignatureString(
                signatureStr, encryptType, cryptoKeyPair.getHexPublicKey());
    }

    @Override
    public void signAsync(byte[] dataToSign, RemoteSignCallbackInterface transactionSignCallback) {
        transactionSignProvider.requestForSignAsync(
                dataToSign, encryptType, transactionSignCallback);
    }
}
