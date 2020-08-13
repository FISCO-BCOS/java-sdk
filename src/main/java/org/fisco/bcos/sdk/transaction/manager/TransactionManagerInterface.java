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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionRequest;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;

/**
 * TransactionManagerInterface @Description: TransactionManagerInterface
 *
 * @author maojiayu
 * @data Jul 17, 2020 2:59:21 PM
 */
public interface TransactionManagerInterface {

    public TransactionResponse deploy(TransactionRequest transactionRequest);

    public TransactionResponse deploy(
            String abi, String bin, String contractName, List<Object> args);

    public TransactionResponse deployByContractLoader(String contractName, List<Object> args)
            throws TransactionBaseException;

    public void sendTransactionOnly(TransactionRequest transactionRequest);

    public TransactionReceipt sendTransactionAndGetReceipt(String to, String data);

    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException;

    public TransactionResponse sendTransactionAndGetResponse(TransactionRequest transactionRequest);

    public void sendTransactionAsync(String signedTransaction, TransactionCallback callback);

    public void sendTransactionAsync(String to, String data, TransactionCallback callback);

    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            TransactionRequest transactionRequest);

    public CallResponse sendCall(CallRequest callRequest) throws TransactionBaseException;

    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> args)
            throws TransactionBaseException;

    public String getCurrentExternalAccountAddress();

    public Call executeCall(CallRequest callRequest);

    public String createSignedTransaction(String to, String data);
}
