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
package org.fisco.bcos.sdk.transaction.core.impl.executor;

import org.fisco.bcos.sdk.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.core.interf.executor.TransactionSignerInterface;

public class TransactionSigner implements TransactionSignerInterface {

    private int encryptType;
    private CryptoKeyPair cryptoKeyPair;

    /**
     * @param encryptType
     * @param cryptoKeyPair
     */
    public TransactionSigner(int encryptType, CryptoKeyPair cryptoKeyPair) {
        super();
        this.encryptType = encryptType;
        this.cryptoKeyPair = cryptoKeyPair;
    }

    @Override
    public byte[] sign(BcosTransaction bcosTransaction) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @return the encryptType */
    public int getEncryptType() {
        return encryptType;
    }

    /** @param encryptType the encryptType to set */
    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    /** @return the cryptoKeyPair */
    public CryptoKeyPair getCryptoKeyPair() {
        return cryptoKeyPair;
    }

    /** @param cryptoKeyPair the cryptoKeyPair to set */
    public void setCryptoKeyPair(CryptoKeyPair cryptoKeyPair) {
        this.cryptoKeyPair = cryptoKeyPair;
    }
}
