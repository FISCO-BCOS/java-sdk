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
package org.fisco.bcos.sdk.transaction.pusher;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;

import java.util.concurrent.CompletableFuture;

public class TransactionPusherService implements TransactionPusherInterface {

    private Client client;

    /**
     * create the TransactionPusherService
     *
     * @param client the client object responsible for send transaction
     */
    public TransactionPusherService(Client client) {
        super();
        this.client = client;
    }

    @Override
    public void pushOnly(String signedTransaction) {
        this.client.sendTransactionAsync(signedTransaction, false, null);
    }

    @Override
    public Call push(String from, String to, byte[] encodedFunction) {
        Transaction transaction = new Transaction(from, to, encodedFunction);
        return this.client.call(transaction);
    }

    @Override
    public TransactionReceipt push(String signedTransaction) {
        return this.client.sendTransaction(signedTransaction, false).getTransactionReceipt();
    }

    @Override
    public void pushAsync(String signedTransactionData, TransactionCallback callback) {
        this.client.sendTransactionAsync(signedTransactionData, false, callback);
    }

    @Override
    public CompletableFuture<TransactionReceipt> pushAsync(String signedTransaction) {
        CompletableFuture<TransactionReceipt> future =
                CompletableFuture.supplyAsync(() -> this.push(signedTransaction));
        return future;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return this.client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }
}
