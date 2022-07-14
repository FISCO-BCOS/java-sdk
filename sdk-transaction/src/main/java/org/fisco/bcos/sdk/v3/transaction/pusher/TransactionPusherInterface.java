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
package org.fisco.bcos.sdk.v3.transaction.pusher;

import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;

/**
 * TransactionPusher @Description: TransactionPusherInterface
 *
 * @author maojiayu
 */
public interface TransactionPusherInterface {

    /**
     * push signed transaction to fisco bcos node only, without receive any response.
     *
     * @param signedTransaction signed transaction string
     */
    void pushOnly(String signedTransaction);

    /**
     * push signed transaction to fisco bcos node and receive transaction receipt.
     *
     * @param signedTransaction signed transaction string
     * @return transaction receipt
     */
    TransactionReceipt push(String signedTransaction);

    /**
     * push encoded function call to fisco bcos node and receive call response.
     *
     * @param from outer account address of sender
     * @param to target contract address
     * @param encodedFunction signed transaction string
     * @return Call hexed string of encoded function
     */
    Call push(String from, String to, byte[] encodedFunction);

    /**
     * push signed transaction to fisco bcos node asynchronously
     *
     * @param signedTransaction signed transaction string
     * @return wrapper Transaction receipt with CompletableFuture
     */
    CompletableFuture<TransactionReceipt> pushAsync(String signedTransaction);

    /**
     * push signed transaction to fisco bcos node asynchronously
     *
     * @param signedTransaction signed transaction string
     * @param callback define hook handle function
     */
    void pushAsync(String signedTransaction, TransactionCallback callback);
}
