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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.model.EventResultEntity;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceipt.Logs;
import org.fisco.bcos.sdk.transaction.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.model.bo.InputAndOutputResult;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;

/**
 * TransactionDecoderInterface @Description: TransactionDecoderInterface
 *
 * @author maojiayu
 * @data Jul 17, 2020 11:38:41 AM
 */
public interface TransactionDecoderInterface {

    public List<Type> decode(String rawInput, String abi) throws TransactionBaseException;

    public String decodeCall(String rawInput, String abi) throws TransactionBaseException;

    public String decodeOutputReturnJson(String abi, String input, String output)
            throws JsonProcessingException, TransactionBaseException, TransactionException;

    public InputAndOutputResult decodeOutputReturnObject(String abi, String input, String output)
            throws TransactionException, TransactionBaseException;

    public String decodeEventReturnJson(String abi, TransactionReceipt transactionReceipt)
            throws TransactionBaseException, IOException;

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String abi, TransactionReceipt transactionReceipt)
            throws TransactionBaseException, IOException;

    public String decodeEventReturnJson(String abi, List<Logs> logList)
            throws TransactionBaseException, IOException;

    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String abi, List<Logs> logList) throws TransactionBaseException, IOException;

    public String decodeReceiptMessage(String input);

    public TransactionResponse decodeTransactionReceipt(String abi, TransactionReceipt receipt)
            throws JsonProcessingException, TransactionBaseException, TransactionException,
                    IOException;
}
