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
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

public class TransactionSignerServcie implements TransactionSignerInterface {

    private Signature signature;
    private CryptoKeyPair cryptoKeyPair;

    /**
     * @param encryptType
     * @param cryptoKeyPair
     */
    public TransactionSignerServcie(Signature signature, CryptoKeyPair cryptoKeyPair) {
        super();
        this.signature = signature;
        this.cryptoKeyPair = cryptoKeyPair;
    }

    @Override
    public SignatureResult sign(byte[] bytes) {
        return signature.sign(bytes, cryptoKeyPair);
    }

    /** @return the cryptoKeyPair */
    public CryptoKeyPair getCryptoKeyPair() {
        return cryptoKeyPair;
    }

    /** @param cryptoKeyPair the cryptoKeyPair to set */
    public void setCryptoKeyPair(CryptoKeyPair cryptoKeyPair) {
        this.cryptoKeyPair = cryptoKeyPair;
    }

    /** @return the signature */
    public Signature getSignature() {
        return signature;
    }

    /** @param signature the signature to set */
    public void setSignature(Signature signature) {
        this.signature = signature;
    }
}
