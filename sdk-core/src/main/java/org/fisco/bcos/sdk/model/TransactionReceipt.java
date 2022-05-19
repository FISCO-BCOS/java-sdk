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
    private String remainGas;
    private String contractAddress;
    private List<Logs> logs;
    private String logsBloom;
    private String status;
    // the node returned status message corresponding to the status
    private String statusMsg;
    private String input;
    private String output;
    private List<MerkleProofUnit> txProof;
    private List<MerkleProofUnit> receiptProof;
    private String message;

    public boolean isStatusOK() {
        if (Objects.isNull(status)) {
            return false;
        }

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

    public String getRemainGas() {
        return remainGas;
    }

    public void setRemainGas(String remainGas) {
        this.remainGas = remainGas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionReceipt receipt = (TransactionReceipt) o;
        return Objects.equals(transactionHash, receipt.transactionHash)
                && Objects.equals(transactionIndex, receipt.transactionIndex)
                && Objects.equals(root, receipt.root)
                && Objects.equals(blockNumber, receipt.blockNumber)
                && Objects.equals(blockHash, receipt.blockHash)
                && Objects.equals(from, receipt.from)
                && Objects.equals(to, receipt.to)
                && Objects.equals(gasUsed, receipt.gasUsed)
                && Objects.equals(remainGas, receipt.remainGas)
                && Objects.equals(contractAddress, receipt.contractAddress)
                && Objects.equals(logs, receipt.logs)
                && Objects.equals(logsBloom, receipt.logsBloom)
                && Objects.equals(status, receipt.status)
                && Objects.equals(statusMsg, receipt.statusMsg)
                && Objects.equals(input, receipt.input)
                && Objects.equals(output, receipt.output)
                && Objects.equals(txProof, receipt.txProof)
                && Objects.equals(receiptProof, receipt.receiptProof)
                && Objects.equals(message, receipt.message);
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
                remainGas,
                contractAddress,
                logs,
                logsBloom,
                status,
                statusMsg,
                input,
                output,
                txProof,
                receiptProof,
                message);
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
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
                + ", remainGas='"
                + remainGas
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
                + ", statusMsg='"
                + statusMsg
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
                + ", message='"
                + message
                + '\''
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
