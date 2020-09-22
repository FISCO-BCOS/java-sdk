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
    private String status;
    private String input;
    private String output;
    private List<MerkleProofUnit> txProof;
    private List<MerkleProofUnit> receiptProof;
    private String message;

    public boolean isStatusOK() {
        return status.equals("0x0") || status.equals("0");
    }

    public static class Logs {
        private String address;
        private List<String> topics;
        private String data;
        private String blockNumber;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getBlockNumber() {
            return blockNumber;
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
            if (o == null || getClass() != o.getClass()) return false;
            Logs logs = (Logs) o;
            return Objects.equals(address, logs.address)
                    && Objects.equals(topics, logs.topics)
                    && Objects.equals(data, logs.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(address, topics, data);
        }

        @Override
        public String toString() {
            return "Logs{"
                    + "address='"
                    + address
                    + '\''
                    + ", topics="
                    + topics
                    + ", data='"
                    + data
                    + '\''
                    + '}';
        }
    }

    public List<MerkleProofUnit> getReceiptProof() {
        return receiptProof;
    }

    public void setReceiptProof(List<MerkleProofUnit> receiptProof) {
        this.receiptProof = receiptProof;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public List<Logs> getLogs() {
        return logs;
    }

    public void setLogs(List<Logs> logs) {
        this.logs = logs;
    }

    public String getLogsBloom() {
        return logsBloom;
    }

    public void setLogsBloom(String logsBloom) {
        this.logsBloom = logsBloom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<MerkleProofUnit> getTxProof() {
        return txProof;
    }

    public void setTxProof(List<MerkleProofUnit> txProof) {
        this.txProof = txProof;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionReceipt that = (TransactionReceipt) o;
        return Objects.equals(transactionHash, that.transactionHash)
                && Objects.equals(transactionIndex, that.transactionIndex)
                && Objects.equals(root, that.root)
                && Objects.equals(blockNumber, that.blockNumber)
                && Objects.equals(blockHash, that.blockHash)
                && Objects.equals(from, that.from)
                && Objects.equals(to, that.to)
                && Objects.equals(gasUsed, that.gasUsed)
                && Objects.equals(contractAddress, that.contractAddress)
                && Objects.equals(logs, that.logs)
                && Objects.equals(logsBloom, that.logsBloom)
                && Objects.equals(status, that.status)
                && Objects.equals(input, that.input)
                && Objects.equals(output, that.output)
                && Objects.equals(txProof, that.txProof)
                && Objects.equals(receiptProof, that.receiptProof);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                transactionHash,
                transactionIndex,
                root,
                blockNumber,
                blockHash,
                from,
                to,
                gasUsed,
                contractAddress,
                logs,
                logsBloom,
                status,
                input,
                output,
                txProof,
                receiptProof);
    }

    @Override
    public String toString() {
        return "TransactionReceipt{"
                + "transactionHash='"
                + transactionHash
                + '\''
                + ", transactionIndex='"
                + transactionIndex
                + '\''
                + ", root='"
                + root
                + '\''
                + ", blockNumber='"
                + blockNumber
                + '\''
                + ", blockHash='"
                + blockHash
                + '\''
                + ", from='"
                + from
                + '\''
                + ", to='"
                + to
                + '\''
                + ", gasUsed='"
                + gasUsed
                + '\''
                + ", contractAddress='"
                + contractAddress
                + '\''
                + ", logs="
                + logs
                + ", logsBloom='"
                + logsBloom
                + '\''
                + ", status='"
                + status
                + '\''
                + ", input='"
                + input
                + '\''
                + ", output='"
                + output
                + '\''
                + ", txProof="
                + txProof
                + ", receiptProof="
                + receiptProof
                + '}';
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /** @param message the message to set */
    public void setMessage(String message) {
        this.message = message;
    }
}
