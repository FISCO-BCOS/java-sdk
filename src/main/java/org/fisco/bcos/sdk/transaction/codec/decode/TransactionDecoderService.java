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
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.EventResultEntity;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceipt.Logs;
import org.fisco.bcos.sdk.transaction.model.bo.InputAndOutputResult;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.transaction.tools.ReceiptStatusUtil;

public class TransactionDecoderService implements TransactionDecoderInterface {

    private CryptoInterface cryptoInterface;

    /** @param cryptoInterface */
    public TransactionDecoderService(CryptoInterface cryptoInterface) {
        super();
        this.cryptoInterface = cryptoInterface;
    }

    @Override
    public List<Type> decode(String rawInput, String abi) throws TransactionBaseException {
        ABIDefinition ad = JsonUtils.fromJson(abi, ABIDefinition.class);
        List<TypeReference<Type>> list =
                ContractAbiUtil.paramFormat(ad.getOutputs())
                        .stream()
                        .map(l -> (TypeReference<Type>) l)
                        .collect(Collectors.toList());
        return FunctionReturnDecoder.decode(rawInput, list);
    }

    @Override
    public String decodeCall(String rawInput, String abi) throws TransactionBaseException {
        return JsonUtils.toJson(decode(rawInput, abi));
    }

    @Override
    public String decodeOutputReturnJson(String abi, String input, String output)
            throws JsonProcessingException, TransactionBaseException, TransactionException {
        TransactionDecoder transactionDecoder = new TransactionDecoder(cryptoInterface, abi);
        return transactionDecoder.decodeOutputReturnJson(input, output);
    }

    @Override
    public InputAndOutputResult decodeOutputReturnObject(String abi, String input, String output)
            throws TransactionException, TransactionBaseException {
        TransactionDecoder transactionDecoder = new TransactionDecoder(cryptoInterface, abi);
        return transactionDecoder.decodeOutputReturnObject(input, output);
    }

    @Override
    public String decodeEventReturnJson(String abi, TransactionReceipt transactionReceipt)
            throws TransactionBaseException, IOException {
        return decodeEventReturnJson(abi, transactionReceipt.getLogs());
    }

    @Override
    public String decodeEventReturnJson(String abi, List<Logs> logList)
            throws TransactionBaseException, IOException {
        TransactionDecoder transactionDecoder = new TransactionDecoder(cryptoInterface, abi);
        return transactionDecoder.decodeEventReturnJson(logList);
    }

    @Override
    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String abi, TransactionReceipt transactionReceipt)
            throws TransactionBaseException, IOException {
        return decodeEventReturnObject(abi, transactionReceipt.getLogs());
    }

    @Override
    public Map<String, List<List<EventResultEntity>>> decodeEventReturnObject(
            String abi, List<Logs> logList) throws TransactionBaseException, IOException {
        TransactionDecoder transactionDecoder = new TransactionDecoder(cryptoInterface, abi);
        return transactionDecoder.decodeEventReturnObject(logList);
    }

    @Override
    public String decodeReceiptMessage(String output) {
        return ReceiptStatusUtil.decodeReceiptMessage(output);
    }

    @Override
    public TransactionResponse decodeTransactionReceipt(
            String abi, TransactionReceipt transactionReceipt)
            throws TransactionBaseException, TransactionException, IOException {
        String values =
                decodeOutputReturnJson(
                        abi, transactionReceipt.getInput(), transactionReceipt.getOutput());
        String events = decodeEventReturnJson(abi, transactionReceipt.getLogs());
        TransactionResponse response = new TransactionResponse();
        response.setTransactionReceipt(transactionReceipt);
        response.setEvents(events);
        response.setValues(values);
        response.setContractAddress(transactionReceipt.getContractAddress());
        response.setReceiptMessages(decodeReceiptMessage(transactionReceipt.getOutput()));
        response.setReturnCode(0);
        return null;
    }

    /** @return the cryptoInterface */
    public CryptoInterface getCryptoInterface() {
        return cryptoInterface;
    }

    /** @param cryptoInterface the cryptoInterface to set */
    public void setCryptoInterface(CryptoInterface cryptoInterface) {
        this.cryptoInterface = cryptoInterface;
    }
}
