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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.utils.Numeric;

public class EventLog {
    private boolean removed;
    private String logIndex;
    private String transactionIndex;
    private String transactionHash;
    private String blockHash;
    private String blockNumber;
    private String address;
    private String data;
    private String type;
    private List<String> topics;

    public EventLog() {}

    public EventLog(
            boolean removed,
            String logIndex,
            String transactionIndex,
            String transactionHash,
            String blockHash,
            String blockNumber,
            String address,
            String data,
            String type,
            List<String> topics) {
        this.removed = removed;
        this.logIndex = logIndex;
        this.transactionIndex = transactionIndex;
        this.transactionHash = transactionHash;
        this.blockHash = blockHash;
        this.blockNumber = blockNumber;
        this.address = address;
        this.data = data;
        this.type = type;
        this.topics = topics;
    }

    public EventLog(String data, List<String> topics) {
        this.data = data;
        this.topics = topics;
    }

    @JsonIgnore
    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public BigInteger getLogIndex() {
        return convert(logIndex);
    }

    @JsonIgnore
    public String getLogIndexRaw() {
        return logIndex;
    }

    public void setLogIndex(String logIndex) {
        this.logIndex = logIndex;
    }

    public BigInteger getTransactionIndex() {
        return convert(transactionIndex);
    }

    @JsonIgnore
    public String getTransactionIndexRaw() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public BigInteger getBlockNumber() {
        return convert(blockNumber);
    }

    @JsonIgnore
    public String getBlockNumberRaw() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    private BigInteger convert(String value) {
        if (value != null) {
            return Numeric.decodeQuantity(value);
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventLog)) {
            return false;
        }

        EventLog log = (EventLog) o;

        if (isRemoved() != log.isRemoved()) {
            return false;
        }
        if (getLogIndexRaw() != null
                ? !getLogIndexRaw().equals(log.getLogIndexRaw())
                : log.getLogIndexRaw() != null) {
            return false;
        }
        if (getTransactionIndexRaw() != null
                ? !getTransactionIndexRaw().equals(log.getTransactionIndexRaw())
                : log.getTransactionIndexRaw() != null) {
            return false;
        }
        if (getTransactionHash() != null
                ? !getTransactionHash().equals(log.getTransactionHash())
                : log.getTransactionHash() != null) {
            return false;
        }
        if (getBlockHash() != null
                ? !getBlockHash().equals(log.getBlockHash())
                : log.getBlockHash() != null) {
            return false;
        }
        if (getBlockNumberRaw() != null
                ? !getBlockNumberRaw().equals(log.getBlockNumberRaw())
                : log.getBlockNumberRaw() != null) {
            return false;
        }
        if (getAddress() != null
                ? !getAddress().equals(log.getAddress())
                : log.getAddress() != null) {
            return false;
        }
        if (getData() != null ? !getData().equals(log.getData()) : log.getData() != null) {
            return false;
        }
        if (getType() != null ? !getType().equals(log.getType()) : log.getType() != null) {
            return false;
        }
        return getTopics() != null ? getTopics().equals(log.getTopics()) : log.getTopics() == null;
    }

    @Override
    public int hashCode() {
        int result = (isRemoved() ? 1 : 0);
        result = 31 * result + (getLogIndexRaw() != null ? getLogIndexRaw().hashCode() : 0);
        result =
                31 * result
                        + (getTransactionIndexRaw() != null
                                ? getTransactionIndexRaw().hashCode()
                                : 0);
        result = 31 * result + (getTransactionHash() != null ? getTransactionHash().hashCode() : 0);
        result = 31 * result + (getBlockHash() != null ? getBlockHash().hashCode() : 0);
        result = 31 * result + (getBlockNumberRaw() != null ? getBlockNumberRaw().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getTopics() != null ? getTopics().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Log [logIndex="
                + logIndex
                + ", transactionIndex="
                + transactionIndex
                + ", transactionHash="
                + transactionHash
                + ", blockHash="
                + blockHash
                + ", blockNumber="
                + blockNumber
                + ", address="
                + address
                + ", data="
                + data
                + ", topics="
                + topics
                + "]";
    }
}
