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
package org.fisco.bcos.sdk.transaction.codec.decode;

import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.model.EventResultEntity;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.bo.InputAndOutputResult;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;

/**
 * TransactionDecoderInterface @Description: TransactionDecoderInterface
 *
 * @author maojiayu
 * @data Jul 17, 2020 11:38:41 AM
 */
public interface TransactionDecoderInterface {

    public RawTransaction decodeRlp(String hex);

    public String decodeCall(String abi, String output);

    public String decodeOutputReturnJson(String contractName, String input, String output);

    public InputAndOutputResult decodeOutputReturnObject(
            String contractName, String input, String output);

    public String decodeEventReturnJson(String contractName, TransactionReceipt transactionReceipt);

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, TransactionReceipt transactionReceipt);

    public String decodeEventReturnJson(String contractName, List<EventLog> logList);

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, List<EventLog> logList);

    public String decodeEventReturnJson(
            String contractName, String eventName, List<EventLog> logList);

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String contractName, String eventName, List<EventLog> logList);

    public String decodeReceiptMessage(String input);

    public TransactionResponse decodeTransactionReceipt(
            String contractName, TransactionReceipt receipt);
}
