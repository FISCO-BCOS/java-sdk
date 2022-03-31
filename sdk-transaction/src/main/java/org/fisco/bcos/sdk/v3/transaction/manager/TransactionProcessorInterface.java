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
package org.fisco.bcos.sdk.v3.transaction.manager;

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;

/**
 * TransactionManagerInterface @Description: TransactionManagerInterface
 *
 * @author maojiayu
 */
public interface TransactionProcessorInterface {

    /**
     * send deploy transaction to fisco bcos node and get transaction receipt.
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param abi ABI
     * @param cryptoKeyPair key pair
     * @return transaction receipt
     */
    public TransactionReceipt deployAndGetReceipt(
            String to, byte[] data, String abi, CryptoKeyPair cryptoKeyPair, int txAttribute)
            throws JniException;

    /**
     * send transaction to fisco bcos node and get transaction receipt.
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param cryptoKeyPair key pair
     * @return transaction receipt
     */
    public TransactionReceipt sendTransactionAndGetReceipt(
            String to, byte[] data, CryptoKeyPair cryptoKeyPair, int txAttribute)
            throws JniException;

    /**
     * send transaction to fisco bcos node and get transaction receipt asynchronously.
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param cryptoKeyPair key pair
     * @param callback define hook function
     */
    public String sendTransactionAsync(
            String to,
            byte[] data,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            TransactionCallback callback);

    /**
     * send call to fisco bcos node and receive call response.
     *
     * @param callRequest signed transaction string
     * @return Call
     */
    public Call executeCall(CallRequest callRequest);

    /**
     * send encoded function call to fisco bcos node and receive call response.
     *
     * @param from outer account address of sender
     * @param to target contract address
     * @param encodedFunction signed transaction string
     * @return Call
     */
    public Call executeCall(String from, String to, byte[] encodedFunction);

    /**
     * create deploy signed transaction
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param abi ABI
     * @param cryptoKeyPair key pair
     * @return hexed data of signed transaction
     */
    public TxPair createDeploySignedTransaction(
            String to, byte[] data, String abi, CryptoKeyPair cryptoKeyPair, int txAttribute)
            throws JniException;

    /**
     * create signed transaction
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param cryptoKeyPair key pair
     * @return hexed data of signed transaction
     */
    public TxPair createSignedTransaction(
            String to, byte[] data, CryptoKeyPair cryptoKeyPair, int txAttribute)
            throws JniException;
}
