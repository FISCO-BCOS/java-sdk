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
package org.fisco.bcos.sdk.transaction.manager;

import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;

/**
 * TransactionManagerInterface @Description: TransactionManagerInterface
 *
 * @author maojiayu
 */
public interface TransactionProcessorInterface {

    /**
     * send transaction to fisco bcos node and get transaction receipt.
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param cryptoKeyPair key pair
     * @return transaction receipt
     */
    public TransactionReceipt sendTransactionAndGetReceipt(
            String to, String data, CryptoKeyPair cryptoKeyPair);

    /**
     * send transaction to fisco bcos node and get transaction receipt asynchronously.
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param cryptoKeyPair key pair
     * @param callback define hook function
     */
    public void sendTransactionAsync(
            String to, String data, CryptoKeyPair cryptoKeyPair, TransactionCallback callback);

    public byte[] sendTransactionAsyncAndGetHash(
            String to, String data, CryptoKeyPair cryptoKeyPair, TransactionCallback callback);
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
    public Call executeCall(String from, String to, String encodedFunction);

    /**
     * create signed transaction
     *
     * @param to target contract address
     * @param data ABI encoded transaction data
     * @param cryptoKeyPair key pair
     * @return hexed data of signed transaction
     */
    public String createSignedTransaction(String to, String data, CryptoKeyPair cryptoKeyPair);
}
