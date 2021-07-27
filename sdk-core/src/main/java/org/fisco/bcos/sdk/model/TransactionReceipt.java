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
package org.fisco.bcos.sdk.model;

import java.util.List;
import java.util.Objects;

public class TransactionReceipt {
    private String transactionHash;
    private String transactionIndex;
    private String root;
    private String blockNumber;
    private String blockHash;
    private String from;
    private String to;
    private String gasUsed;
    private String contractAddress;
    private List<Logs> logs;
    private String logsBloom;
    private Integer status;
    // the node returned status message corresponding to the status
    private String statusMsg;
    private String input;
    private String output;
    private List<MerkleProofUnit> txProof;
    private List<MerkleProofUnit> receiptProof;
    private String message;

    public boolean isStatusOK() {
        return this.status == 0;
    }

    public static class Logs {
        private String address;
        private List<String> topics;
        private String data;
        private String blockNumber;

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<String> getTopics() {
            return this.topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

        public String getData() {
            return this.data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public EventLog toEventLog() {
            EventLog eventLog = new EventLog();
            eventLog.setAddress(this.address);
            eventLog.setTopics(this.topics);
            eventLog.setData(this.data);
            eventLog.setBlockNumber(this.blockNumber);
            return eventLog;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            Logs logs = (Logs) o;
            return Objects.equals(this.address, logs.address)
                    && Objects.equals(this.topics, logs.topics)
                    && Objects.equals(this.data, logs.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.address, this.topics, this.data);
        }

        @Override
        public String toString() {
            return "Logs{"
                    + "address='"
                    + this.address
                    + '\''
                    + ", topics="
                    + this.topics
                    + ", data='"
                    + this.data
                    + '\''
                    + '}';
        }
    }

    public List<MerkleProofUnit> getReceiptProof() {
        return this.receiptProof;
    }

    public void setReceiptProof(List<MerkleProofUnit> receiptProof) {
        this.receiptProof = receiptProof;
    }

    public String getTransactionHash() {
        return this.transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getTransactionIndex() {
        return this.transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getRoot() {
        return this.root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getBlockNumber() {
        return this.blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockHash() {
        return this.blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGasUsed() {
        return this.gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getContractAddress() {
        return this.contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public List<Logs> getLogs() {
        return this.logs;
    }

    public void setLogs(List<Logs> logs) {
        this.logs = logs;
    }

    public String getLogsBloom() {
        return this.logsBloom;
    }

    public void setLogsBloom(String logsBloom) {
        this.logsBloom = logsBloom;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<MerkleProofUnit> getTxProof() {
        return this.txProof;
    }

    public void setTxProof(List<MerkleProofUnit> txProof) {
        this.txProof = txProof;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TransactionReceipt that = (TransactionReceipt) o;
        return Objects.equals(this.transactionHash, that.transactionHash)
                && Objects.equals(this.transactionIndex, that.transactionIndex)
                && Objects.equals(this.root, that.root)
                && Objects.equals(this.blockNumber, that.blockNumber)
                && Objects.equals(this.blockHash, that.blockHash)
                && Objects.equals(this.from, that.from)
                && Objects.equals(this.to, that.to)
                && Objects.equals(this.gasUsed, that.gasUsed)
                && Objects.equals(this.contractAddress, that.contractAddress)
                && Objects.equals(this.logs, that.logs)
                && Objects.equals(this.logsBloom, that.logsBloom)
                && Objects.equals(this.status, that.status)
                && Objects.equals(this.input, that.input)
                && Objects.equals(this.output, that.output)
                && Objects.equals(this.txProof, that.txProof)
                && Objects.equals(this.receiptProof, that.receiptProof);
    }

    public String getStatusMsg() {
        return this.statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.transactionHash,
                this.transactionIndex,
                this.root,
                this.blockNumber,
                this.blockHash,
                this.from,
                this.to,
                this.gasUsed,
                this.contractAddress,
                this.logs,
                this.logsBloom,
                this.status,
                this.input,
                this.output,
                this.txProof,
                this.receiptProof);
    }

    @Override
    public String toString() {
        return "TransactionReceipt{"
                + "transactionHash='"
                + this.transactionHash
                + '\''
                + ", transactionIndex='"
                + this.transactionIndex
                + '\''
                + ", root='"
                + this.root
                + '\''
                + ", blockNumber='"
                + this.blockNumber
                + '\''
                + ", blockHash='"
                + this.blockHash
                + '\''
                + ", from='"
                + this.from
                + '\''
                + ", to='"
                + this.to
                + '\''
                + ", gasUsed='"
                + this.gasUsed
                + '\''
                + ", contractAddress='"
                + this.contractAddress
                + '\''
                + ", logs="
                + this.logs
                + ", logsBloom='"
                + this.logsBloom
                + '\''
                + ", status='"
                + this.status
                + '\''
                + ", statusMsg='"
                + this.statusMsg
                + '\''
                + ", input='"
                + this.input
                + '\''
                + ", output='"
                + this.output
                + '\''
                + ", txProof="
                + this.txProof
                + ", receiptProof="
                + this.receiptProof
                + '}';
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
