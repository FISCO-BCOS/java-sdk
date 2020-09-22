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

import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;

/**
 * TransactionPusher @Description: TransactionPusherInterface
 *
 * @author maojiayu
 */
public interface TransactionPusherInterface {

    public void pushOnly(String signedTransaction);

    public TransactionReceipt push(String signedTransaction);

    public Call push(String from, String to, String encodedFunction);

    public CompletableFuture<TransactionReceipt> pushAsync(String signedTransaction);

    public void pushAsync(String signedTransaction, TransactionCallback callback);
}
