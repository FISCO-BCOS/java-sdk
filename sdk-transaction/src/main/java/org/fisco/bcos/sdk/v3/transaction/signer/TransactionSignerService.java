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
package org.fisco.bcos.sdk.v3.transaction.signer;

import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;

public class TransactionSignerService implements TransactionSignerInterface {
    private Signature signature;

    /**
     * create the TransactionSignerService according the given signature
     *
     * @param signature the signature
     */
    public TransactionSignerService(Signature signature) {
        super();
        this.signature = signature;
    }

    @Override
    public SignatureResult sign(String hash, CryptoKeyPair cryptoKeyPair) {
        return signature.sign(hash, cryptoKeyPair);
    }

    @Override
    public SignatureResult sign(byte[] hash, CryptoKeyPair cryptoKeyPair) {
        return signature.sign(hash, cryptoKeyPair);
    }

    /** @return the signature */
    public Signature getSignature() {
        return signature;
    }

    /** @param signature the signature to set */
    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    /**
     * decode signature to SignatureResult
     *
     * @param signatureStr the signature string
     * @param cryptoType 0-ECC, 1-GM
     * @param publicKey public key string of signer
     * @return SignatureResult
     */
    public static SignatureResult decodeSignatureString(
            String signatureStr, int cryptoType, String publicKey) {
        SignatureResult signature;
        if (cryptoType == CryptoType.ECDSA_TYPE) {
            signature = new ECDSASignatureResult(signatureStr);
        } else if (cryptoType == CryptoType.SM_TYPE) {
            signature = new SM2SignatureResult(publicKey, signatureStr);
        } else {
            throw new IllegalStateException("Error cryptoType: " + cryptoType);
        }
        return signature;
    }
}
