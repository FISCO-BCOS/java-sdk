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

import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.client.response.BcosTransaction;
import org.fisco.bcos.sdk.client.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.transaction.core.interf.executor.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.domain.EventLog;
import org.fisco.bcos.sdk.transaction.domain.EventResultEntity;
import org.fisco.bcos.sdk.transaction.domain.InputAndOutputResult;
import org.fisco.bcos.sdk.transaction.domain.TransactionResponse;

public class TransactionDecoder implements TransactionDecoderInterface {

    @Override
    public BcosTransaction decodeRlp(String hex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String decodeCall(String abi, String output) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String decodeOutputReturnJson(String contractName, String input, String output) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputAndOutputResult decodeOutputReturnObject(
            String contractName, String input, String output) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String decodeEventReturnJson(
            String contractName, BcosTransactionReceipt transactionReceipt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, BcosTransactionReceipt transactionReceipt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String decodeEventReturnJson(String contractName, List<EventLog> logList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, List<EventLog> logList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String decodeEventReturnJson(
            String contractName, String eventName, List<EventLog> logList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, String eventName, List<EventLog> logList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String decodeReceiptMessage(String input) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransactionResponse decodeTransactionReceipt(
            String contractName, BcosTransactionReceipt transactionReceipt) {
        // TODO Auto-generated method stub
        return null;
    }
}
