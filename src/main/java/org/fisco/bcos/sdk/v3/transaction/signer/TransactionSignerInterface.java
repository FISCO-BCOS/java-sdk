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
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;

/**
 * TransactionSignerInterface @Description: TransactionSignerInterface
 *
 * @author maojiayu
 */
public interface TransactionSignerInterface {
    /**
     * sign raw transaction hash string and get signature result
     *
     * @param hash raw transaction hash string to be signed
     * @param cryptoKeyPair keypair
     * @return signature result
     */
    SignatureResult sign(String hash, CryptoKeyPair cryptoKeyPair);

    /**
     * sign raw transaction hash byte array and get signature result
     *
     * @param hash raw transaction hash byte array to be signed
     * @param cryptoKeyPair keypair
     * @return signature result
     */
    SignatureResult sign(byte[] hash, CryptoKeyPair cryptoKeyPair);

    /**
     * sign raw transaction hash string and get raw signature result
     *
     * @param hash raw transaction hash byte array to be signed
     * @param cryptoKeyPair keypair
     * @return signature result, hex string
     */
    String signWithRawResult(String hash, CryptoKeyPair cryptoKeyPair);

    /**
     * sign raw transaction hash byte array and get raw signature result
     *
     * @param hash raw transaction hash byte array to be signed
     * @param cryptoKeyPair keypair
     * @return signature result, hex string
     */
    String signWithRawResult(byte[] hash, CryptoKeyPair cryptoKeyPair);
}
