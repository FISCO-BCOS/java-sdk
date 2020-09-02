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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.abi.EventEncoder;
import org.fisco.bcos.sdk.abi.wrapper.ABICodecObject;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition.NamedType;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.abi.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceipt.Logs;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.transaction.tools.ReceiptStatusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionDecoderService implements TransactionDecoderInterface {
    protected static Logger logger = LoggerFactory.getLogger(TransactionDecoderService.class);

    private CryptoInterface cryptoInterface;
    private final ABICodec abiCodec;
    private EventEncoder eventEncoder;

    /** @param cryptoInterface */
    public TransactionDecoderService(CryptoInterface cryptoInterface) {
        super();
        this.cryptoInterface = cryptoInterface;
        this.abiCodec = new ABICodec(cryptoInterface);
        this.eventEncoder = new EventEncoder(cryptoInterface);
    }

    @Override
    public String decodeReceiptMessage(String output) {
        return ReceiptStatusUtil.decodeReceiptMessage(output);
    }

    @Override
    public TransactionResponse decodeReceiptWithValues(
            String abi, String functionName, TransactionReceipt transactionReceipt)
            throws IOException, ContractException, ABICodecException, TransactionException {
        TransactionResponse response = decodeReceiptWithoutValues(abi, transactionReceipt);
        // only successful tx has return values.
        if (transactionReceipt.getStatus().equals("0x0")) {
            String values =
                    JsonUtils.toJson(
                            abiCodec.decodeMethod(
                                    abi, functionName, transactionReceipt.getOutput()));
            response.setValues(values);
        }
        return response;
    }

    @Override
    public TransactionResponse decodeReceiptWithoutValues(
            String abi, TransactionReceipt transactionReceipt)
            throws TransactionException, IOException, ContractException, ABICodecException {
        TransactionResponse response = decodeReceiptStatus(transactionReceipt);
        String events = JsonUtils.toJson(decodeEvents(abi, transactionReceipt.getLogs()));
        response.setTransactionReceipt(transactionReceipt);
        response.setEvents(events);
        response.setContractAddress(transactionReceipt.getContractAddress());
        return response;
    }

    @Override
    public TransactionResponse decodeReceiptStatus(TransactionReceipt receipt)
            throws ContractException {
        RetCode retCode = ReceiptParser.parseTransactionReceipt(receipt);
        TransactionResponse response = new TransactionResponse();
        response.setReturnCode(retCode.getCode());
        response.setReceiptMessages(retCode.getMessage());
        response.setReturnMessage(retCode.getMessage());
        return response;
    }

    @SuppressWarnings("static-access")
    @Override
    public Map<String, List<Object>> decodeEvents(String abi, List<Logs> logs)
            throws ABICodecException {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoInterface);
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(abi);
        Map<String, List<ABIDefinition>> eventsMap = contractABIDefinition.getEvents();
        Map<String, List<Object>> result = new HashMap<>();
        eventsMap.forEach(
                (name, events) -> {
                    for (ABIDefinition abiDefinition : events) {
                        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
                        ABIObject outputObject = abiObjectFactory.createInputObject(abiDefinition);
                        ABICodecObject abiCodecObject = new ABICodecObject();
                        for (Logs log : logs) {
                            String eventSignature =
                                    eventEncoder.buildEventSignature(
                                            decodeMethodSign(abiDefinition));
                            if (log.getTopics().isEmpty()
                                    || !log.getTopics().contains(eventSignature)) {
                                continue;
                            }
                            try {
                                List<Object> list =
                                        abiCodecObject.decodeJavaObject(
                                                outputObject, log.getData());
                                if (result.containsKey(name)) {
                                    result.get("name").addAll(list);
                                } else {
                                    result.put(name, list);
                                }
                            } catch (Exception e) {
                                logger.error(
                                        " exception in decodeEventToObject : {}", e.getMessage());
                            }
                        }
                    }
                });
        return result;
    }

    private String decodeMethodSign(ABIDefinition ABIDefinition) {
        List<NamedType> inputTypes = ABIDefinition.getInputs();
        StringBuilder methodSign = new StringBuilder();
        methodSign.append(ABIDefinition.getName());
        methodSign.append("(");
        String params =
                inputTypes.stream().map(NamedType::getType).collect(Collectors.joining(","));
        methodSign.append(params);
        methodSign.append(")");
        return methodSign.toString();
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
