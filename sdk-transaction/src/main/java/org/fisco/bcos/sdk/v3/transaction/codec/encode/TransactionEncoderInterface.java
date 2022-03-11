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
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;

/**
 * TransactionEncoderInterface @Description: TransactionEncoderInterface
 *
 * @author maojiayu
 */
public interface TransactionEncoderInterface {

    /**
     * Tars encode based on TransactionData
     *
     * @param transactionData transaction data
     * @return encoded transaction
     */
    byte[] encode(long transactionData) throws JniException;
    /**
     * Tars encode and sign based on TransactionData
     *
     * @param transactionData data to be encoded
     * @param cryptoKeyPair keypair
     * @return encoded & signed transaction byte array
     */
    byte[] encodeAndSignBytes(long transactionData, CryptoKeyPair cryptoKeyPair, int attribute)
            throws JniException;

    /**
     * Tars encode and sign based on TransactionData
     *
     * @param transactionData data to be encoded
     * @param cryptoKeyPair keypair
     * @return encoded & signed transaction hexed String
     */
    String encodeAndSign(long transactionData, CryptoKeyPair cryptoKeyPair, int attribute)
            throws JniException;

    /**
     * Tars encode and hash based on TransactionData
     *
     * @param transactionData data to be encoded
     * @return encoded & hashed transaction byte array
     */
    byte[] encodeAndHashBytes(long transactionData) throws JniException;

    /**
     * Tars encode transactionData to Transaction bytes
     *
     * @param transactionData raw transaction data
     * @param hash transaction hash
     * @param result transaction signature
     * @return encoded bytes
     */
    byte[] encodeToTransactionBytes(
            long transactionData, byte[] hash, SignatureResult result, int attribute)
            throws JniException;

    /**
     * Tars encode transactionData to Transaction bytes
     *
     * @param transactionData raw transaction data
     * @param result transaction signature
     * @return encoded bytes
     */
    byte[] encodeToTransactionBytes(long transactionData, SignatureResult result, int attribute)
            throws JniException;
}
