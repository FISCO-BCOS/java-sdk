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

package org.fisco.bcos.sdk.client.protocol.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

/**
 * getSyncStatus.
 *
 * <p>Returns an object with data about the sync status or false.
 */
public class SyncStatus extends JsonRpcResponse<SyncStatus.SyncStatusInfo> {
    public SyncStatus.SyncStatusInfo getSyncStatus() {
        return this.getResult();
    }

    @Override
    @JsonDeserialize(converter = SyncStatusInfoConvert.class)
    public void setResult(SyncStatus.SyncStatusInfo result) {
        super.setResult(result);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PeersInfo {
        @JsonProperty("nodeID")
        private String nodeId;

        private String genesisHash;
        private long blockNumber;
        private String latestHash;

        public String getNodeId() {
            return this.nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getGenesisHash() {
            return this.genesisHash;
        }

        public void setGenesisHash(String genesisHash) {
            this.genesisHash = genesisHash;
        }

        public long getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getLatestHash() {
            return this.latestHash;
        }

        public void setLatestHash(String latestHash) {
            this.latestHash = latestHash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            PeersInfo peersInfo = (PeersInfo) o;
            return Objects.equals(this.nodeId, peersInfo.nodeId)
                    && Objects.equals(this.genesisHash, peersInfo.genesisHash)
                    && Objects.equals(this.blockNumber, peersInfo.blockNumber)
                    && Objects.equals(this.latestHash, peersInfo.latestHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.nodeId, this.genesisHash, this.blockNumber, this.latestHash);
        }

        @Override
        public String toString() {
            return "PeersInfo{"
                    + "nodeId='"
                    + this.nodeId
                    + '\''
                    + ", genesisHash='"
                    + this.genesisHash
                    + '\''
                    + ", blockNumber='"
                    + this.blockNumber
                    + '\''
                    + ", latestHash='"
                    + this.latestHash
                    + '\''
                    + '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SyncStatusInfo {
        private Boolean isSyncing;
        private String protocolId;
        private String genesisHash;

        @JsonProperty("nodeID")
        private String nodeId;

        private long blockNumber;
        private String latestHash;
        private int knownHighestNumber;
        private String txPoolSize;
        private List<PeersInfo> peers;
        private String knownLatestHash;

        public String getKnownLatestHash() {
            return this.knownLatestHash;
        }

        public void setKnownLatestHash(String knownLatestHash) {
            this.knownLatestHash = knownLatestHash;
        }

        public Boolean getIsSyncing() {
            return this.isSyncing;
        }

        public void setIsSyncing(Boolean isSyncing) {
            this.isSyncing = isSyncing;
        }

        public String getProtocolId() {
            return this.protocolId;
        }

        public void setProtocolId(String protocolId) {
            this.protocolId = protocolId;
        }

        public String getGenesisHash() {
            return this.genesisHash;
        }

        public void setGenesisHash(String genesisHash) {
            this.genesisHash = genesisHash;
        }

        public String getNodeId() {
            return this.nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public long getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getLatestHash() {
            return this.latestHash;
        }

        public void setLatestHash(String latestHash) {
            this.latestHash = latestHash;
        }

        public int getKnownHighestNumber() {
            return this.knownHighestNumber;
        }

        public void setKnownHighestNumber(int knownHighestNumber) {
            this.knownHighestNumber = knownHighestNumber;
        }

        public String getTxPoolSize() {
            return this.txPoolSize;
        }

        public void setTxPoolSize(String txPoolSize) {
            this.txPoolSize = txPoolSize;
        }

        public List<PeersInfo> getPeers() {
            return this.peers;
        }

        public void setPeers(List<PeersInfo> peers) {
            this.peers = peers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            SyncStatusInfo that = (SyncStatusInfo) o;
            return Objects.equals(this.isSyncing, that.isSyncing)
                    && Objects.equals(this.protocolId, that.protocolId)
                    && Objects.equals(this.genesisHash, that.genesisHash)
                    && Objects.equals(this.nodeId, that.nodeId)
                    && Objects.equals(this.blockNumber, that.blockNumber)
                    && Objects.equals(this.latestHash, that.latestHash)
                    && Objects.equals(this.knownHighestNumber, that.knownHighestNumber)
                    && Objects.equals(this.txPoolSize, that.txPoolSize)
                    && Objects.equals(this.peers, that.peers)
                    && Objects.equals(this.knownLatestHash, that.knownLatestHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.isSyncing,
                    this.protocolId,
                    this.genesisHash,
                    this.nodeId,
                    this.blockNumber,
                    this.latestHash,
                    this.knownHighestNumber,
                    this.txPoolSize,
                    this.peers,
                    this.knownLatestHash);
        }

        @Override
        public String toString() {
            return "SyncStatusInfo{"
                    + "isSyncing='"
                    + this.isSyncing
                    + '\''
                    + ", protocolId='"
                    + this.protocolId
                    + '\''
                    + ", genesisHash='"
                    + this.genesisHash
                    + '\''
                    + ", nodeId='"
                    + this.nodeId
                    + '\''
                    + ", blockNumber='"
                    + this.blockNumber
                    + '\''
                    + ", latestHash='"
                    + this.latestHash
                    + '\''
                    + ", knownHighestNumber='"
                    + this.knownHighestNumber
                    + '\''
                    + ", txPoolSize='"
                    + this.txPoolSize
                    + '\''
                    + ", peers="
                    + this.peers
                    + ", knownLatestHash='"
                    + this.knownLatestHash
                    + '\''
                    + '}';
        }
    }

    public static class SyncStatusInfoConvert implements Converter<String, SyncStatusInfo> {
        @Override
        public SyncStatusInfo convert(String value) {
            try {
                return new ObjectMapper().readValue(value, SyncStatusInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructSimpleType(String.class, null);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructSimpleType(SyncStatusInfo.class, null);
        }
    }
}
