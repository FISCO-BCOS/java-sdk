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
package org.fisco.bcos.sdk.transaction.codec;

import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.domain.EventResultEntity;
import org.fisco.bcos.sdk.transaction.domain.InputAndOutputResult;
import org.fisco.bcos.sdk.transaction.domain.RawTransaction;
import org.fisco.bcos.sdk.transaction.domain.dto.TransactionResponse;

public class TransactionDecoder implements TransactionDecoderInterface {

    @Override
    public RawTransaction decodeRlp(String hex) {
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
            String contractName, TransactionReceipt transactionReceipt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, TransactionReceipt transactionReceipt) {
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
            String contractName, TransactionReceipt transactionReceipt) {
        // TODO Auto-generated method stub
        return null;
    }
}
