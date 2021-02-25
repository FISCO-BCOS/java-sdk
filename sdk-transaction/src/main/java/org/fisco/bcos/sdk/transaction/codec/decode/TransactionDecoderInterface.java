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

    /**
     * parse revert message from receipt
     *
     * @param input the input of transaction receipt
     * @return the resolved revert message information
     */
    public String decodeReceiptMessage(String input);

    /**
     * parse the status and transaction detail from receipt
     *
     * @param receipt transaction receipt
     * @return the resolved status and other transaction detail
     */
    public TransactionResponse decodeReceiptStatus(TransactionReceipt receipt);

    /**
     * parse the transaction information of the function from receipt with return values
     *
     * @param abi contract abi
     * @param functionName referred function name
     * @param receipt transaction receipt
     * @return the resolved status and other transaction detail
     */
    public TransactionResponse decodeReceiptWithValues(
            String abi, String functionName, TransactionReceipt receipt)
            throws TransactionException, IOException, ABICodecException;

    /**
     * parse the transaction information from receipt without return values
     *
     * @param abi contract abi
     * @param transactionReceipt transaction receipt
     * @return the resolved status and other transaction detail
     */
    public TransactionResponse decodeReceiptWithoutValues(
            String abi, TransactionReceipt transactionReceipt)
            throws TransactionException, IOException, ABICodecException;

    /**
     * parse the transaction events from receipt logs
     *
     * @param abi contract abi
     * @param logs logs in the transaction receipt
     * @return Map<K,V>, K is event name, V is list of events in Json. May have several events of
     *     the same event
     */
    public Map<String, List<List<Object>>> decodeEvents(String abi, List<Logs> logs)
            throws ABICodecException;
}
