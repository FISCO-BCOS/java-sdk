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
package org.fisco.bcos.sdk.transaction.core.interf.executor;

import java.util.concurrent.CompletableFuture;

import org.fisco.bcos.sdk.client.RespCallback;
import org.fisco.bcos.sdk.client.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.client.response.Call;

/**
 * TransactionPusher
 *
 * @Description: TransactionPusherInterface
 * @author maojiayu
 * @data Jul 17, 2020 2:13:39 PM
 *
 */
public interface TransactionPusherInterface {

    public void pushOnly(String signedTransaction);

    public BcosTransactionReceipt push(String signedTransaction);

    public <T> BcosTransactionReceipt push(String signedTransaction, RespCallback<T> callback);

    public CompletableFuture<BcosTransactionReceipt> pushAsync(String signedTransaction);

    public Call push(String from, String to, String encodedFunction);
}
