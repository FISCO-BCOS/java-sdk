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

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.client.RespCallback;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.domain.RawTransaction;
import org.fisco.bcos.sdk.transaction.domain.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.domain.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.domain.dto.TransactionRequest;
import org.fisco.bcos.sdk.transaction.domain.dto.TransactionResponse;

/**
 * TransactionManager @Description: TransactionManager
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:23:19 PM
 */
public class TransactionManager implements TransactionManagerInterface {

    @Override
    public TransactionResponse deploy(TransactionRequest transactionRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendTransactionOnly(TransactionRequest transactionRequest) {
        // TODO Auto-generated method stub

    }

    @Override
    public TransactionResponse sendTransaction(TransactionRequest transactionRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransactionReceipt sendTransaction(
            int groupId, String signedTransaction, RespCallback<TransactionResponse> callback) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            TransactionRequest transactionRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CallResponse sendCall(CallRequest callRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCurrentExternalAccountAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO
     *
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param data
     * @param value
     * @param object
     * @return
     */
    public RawTransaction createTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO
     *
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param data
     * @param value
     * @param object
     * @param callback
     */
    public void sendTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            Object object,
            ResponseCallback callback) {
        // TODO Auto-generated method stub

    }

    /**
     * TODO
     *
     * @param rawTransaction
     * @return
     */
    public String sign(RawTransaction rawTransaction) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * TODO
     *
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param data
     * @param value
     * @param object
     * @return
     */
    public TransactionReceipt executeTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            Object object) {
        // TODO Auto-generated method stub
        return null;
    }
}
