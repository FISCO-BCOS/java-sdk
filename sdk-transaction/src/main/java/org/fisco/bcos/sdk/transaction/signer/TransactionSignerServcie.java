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

import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

public class TransactionSignerServcie implements TransactionSignerInterface {

    private Signature signature;
    private final CryptoInterface cryptoInterface;
    /**
     * @param signature
     * @param cryptoInterface
     */
    public TransactionSignerServcie(Signature signature, CryptoInterface cryptoInterface) {
        super();
        this.signature = signature;
        this.cryptoInterface = cryptoInterface;
    }

    @Override
    public SignatureResult sign(String hash) {
        return signature.sign(hash, this.cryptoInterface.getCryptoKeyPair());
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
