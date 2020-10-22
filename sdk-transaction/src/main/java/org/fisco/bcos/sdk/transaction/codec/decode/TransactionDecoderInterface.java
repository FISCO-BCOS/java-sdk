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
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceipt.Logs;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;

/**
 * TransactionDecoderInterface @Description: TransactionDecoderInterface
 *
 * @author maojiayu
 */
public interface TransactionDecoderInterface {

    public String decodeReceiptMessage(String input);

    public TransactionResponse decodeReceiptStatus(TransactionReceipt receipt);

    public TransactionResponse decodeReceiptWithValues(
            String abi, String functionName, TransactionReceipt receipt)
            throws JsonProcessingException, TransactionException, IOException, ABICodecException;

    public TransactionResponse decodeReceiptWithoutValues(
            String abi, TransactionReceipt transactionReceipt)
            throws TransactionException, IOException, ABICodecException;

    public Map<String, List<List<Object>>> decodeEvents(String abi, List<Logs> logs)
            throws ABICodecException;
}
