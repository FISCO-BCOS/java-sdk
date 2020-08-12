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
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;

public interface ContractlessTransactionManagerInterface {

    public TransactionResponse deployByContractLoader(String contractName, List<Object> params)
            throws TransactionBaseException;

    public void deployByContractLoaderAsync(
            String contractName, List<Object> args, TransactionCallback callback)
            throws TransactionBaseException;

    public TransactionReceipt sendTransactionAndGetReceiptByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> params)
            throws TransactionBaseException;

    public void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> args,
            TransactionCallback callback)
            throws TransactionBaseException;

    public CallResponse sendCallByContractLoader(
            String contractName, String contractAddress, String functionName, List<Object> params)
            throws TransactionBaseException;
}
