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

import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.client.RespCallback;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.transaction.core.interf.executor.TransactionPusherInterface;
import org.fisco.bcos.sdk.transaction.domain.TransactionReceipt;

public class TransactionPusher implements TransactionPusherInterface {

    @Override
    public void pushOnly(String signedTransaction) {
        // TODO Auto-generated method stub

    }

    @Override
    public TransactionReceipt push(String signedTransaction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> TransactionReceipt push(String signedTransaction, RespCallback<T> callback) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<TransactionReceipt> pushAsync(String signedTransaction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Call push(String from, String to, String encodedFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}
