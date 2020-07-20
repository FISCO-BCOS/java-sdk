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

import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.client.response.BcosTransaction;
import org.fisco.bcos.sdk.client.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.transaction.domain.EventLog;
import org.fisco.bcos.sdk.transaction.domain.EventResultEntity;
import org.fisco.bcos.sdk.transaction.domain.InputAndOutputResult;
import org.fisco.bcos.sdk.transaction.domain.TransactionResponse;

/**
 * TransactionDecoderInterface @Description: TransactionDecoderInterface
 *
 * @author maojiayu
 * @data Jul 17, 2020 11:38:41 AM
 */
public interface TransactionDecoderInterface {

    public BcosTransaction decodeRlp(String hex);

    public String decodeCall(String abi, String output);

    public String decodeOutputReturnJson(String contractName, String input, String output);

    public InputAndOutputResult decodeOutputReturnObject(
            String contractName, String input, String output);

    public String decodeEventReturnJson(
            String contractName, BcosTransactionReceipt transactionReceipt);

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, BcosTransactionReceipt transactionReceipt);

    public String decodeEventReturnJson(String contractName, List<EventLog> logList);

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, List<EventLog> logList);

    public String decodeEventReturnJson(
            String contractName, String eventName, List<EventLog> logList);

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, String eventName, List<EventLog> logList);

    public String decodeReceiptMessage(String input);

    public TransactionResponse decodeTransactionReceipt(
            String contractName, BcosTransactionReceipt transactionReceipt);
}
