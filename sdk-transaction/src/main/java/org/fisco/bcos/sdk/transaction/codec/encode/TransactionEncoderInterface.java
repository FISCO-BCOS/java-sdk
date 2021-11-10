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

import org.fisco.bcos.sdk.client.protocol.model.tars.TransactionData;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

/**
 * TransactionEncoderInterface @Description: TransactionEncoderInterface
 *
 * @author maojiayu
 */
public interface TransactionEncoderInterface {

    /**
     * Tars encode based on TransactionData
     *
     * @param rawTransaction
     * @return
     */
    byte[] encode(TransactionData rawTransaction);
    /**
     * Tars encode and sign based on TransactionData
     *
     * @param rawTransaction data to be encoded
     * @param cryptoKeyPair keypair
     * @return encoded & signed transaction byte array
     */
    byte[] encodeAndSignBytes(TransactionData rawTransaction, CryptoKeyPair cryptoKeyPair);

    /**
     * Tars encode and sign based on TransactionData
     *
     * @param rawTransaction data to be encoded
     * @param cryptoKeyPair keypair
     * @return encoded & signed transaction hexed String
     */
    String encodeAndSign(TransactionData rawTransaction, CryptoKeyPair cryptoKeyPair);

    /**
     * Tars encode and hash based on TransactionData
     *
     * @param rawTransaction data to be encoded
     * @return encoded & hashed transaction byte array
     */
    byte[] encodeAndHashBytes(TransactionData rawTransaction);

    /**
     * Tars encode rawTransaction to Transaction bytes
     *
     * @param rawTransaction
     * @param hash
     * @param result
     * @return
     */
    byte[] encodeToTransactionBytes(
            TransactionData rawTransaction, byte[] hash, SignatureResult result);
}
