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
package org.fisco.bcos.sdk.transaction.model.dto;

import org.fisco.bcos.sdk.model.TransactionReceipt;

/**
 * TransactionResponse @Description: TransactionResponse
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:16:51 PM
 */
public class TransactionResponse extends CommonResponse {
    private TransactionReceipt transactionReceipt;
    private String contractAddress;
    private String values;
    private String events;
    private String receiptMessages;
    /** @return the bcosTransactionReceipt */
    public TransactionReceipt getTransactionReceipt() {
        return transactionReceipt;
    }
    /** @param transactionReceipt the transactionReceipt to set */
    public void setTransactionReceipt(TransactionReceipt transactionReceipt) {
        this.transactionReceipt = transactionReceipt;
    }
    /** @return the contractAddress */
    public String getContractAddress() {
        return contractAddress;
    }
    /** @param contractAddress the contractAddress to set */
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    /** @return the values */
    public String getValues() {
        return values;
    }
    /** @param values the values to set */
    public void setValues(String values) {
        this.values = values;
    }
    /** @return the events */
    public String getEvents() {
        return events;
    }
    /** @param events the events to set */
    public void setEvents(String events) {
        this.events = events;
    }
    /** @return the receiptMessages */
    public String getReceiptMessages() {
        return receiptMessages;
    }
    /** @param receiptMessages the receiptMessages to set */
    public void setReceiptMessages(String receiptMessages) {
        this.receiptMessages = receiptMessages;
    }

    public static TransactionResponse from(TransactionResponse response) {
        TransactionResponse dto = new TransactionResponse();
        dto.setContractAddress(response.getContractAddress());
        dto.setEvents(response.getEvents());
        dto.setReceiptMessages(response.getReceiptMessages());
        dto.setTransactionReceipt(response.getTransactionReceipt());
        dto.setValues(response.getValues());
        return dto;
    }
}
