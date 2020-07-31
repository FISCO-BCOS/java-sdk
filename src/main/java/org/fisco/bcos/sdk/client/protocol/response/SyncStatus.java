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

import java.util.List;
import java.util.Objects;

/**
 * getSyncStatus.
 *
 * <p>Returns an object with data about the sync status or false.
 */
public class SyncStatus extends JsonRpcResponse<SyncStatus.SyncStatusInfo> {
    public SyncStatus.SyncStatusInfo getSyncStatus() {
        return getResult();
    }

    public static class PeersInfo {
        private String nodeId;
        private String genesisHash;
        private String blockNumber;
        private String latestHash;

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getGenesisHash() {
            return genesisHash;
        }

        public void setGenesisHash(String genesisHash) {
            this.genesisHash = genesisHash;
        }

        public String getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getLatestHash() {
            return latestHash;
        }

        public void setLatestHash(String latestHash) {
            this.latestHash = latestHash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PeersInfo peersInfo = (PeersInfo) o;
            return Objects.equals(nodeId, peersInfo.nodeId)
                    && Objects.equals(genesisHash, peersInfo.genesisHash)
                    && Objects.equals(blockNumber, peersInfo.blockNumber)
                    && Objects.equals(latestHash, peersInfo.latestHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeId, genesisHash, blockNumber, latestHash);
        }

        @Override
        public String toString() {
            return "PeersInfo{"
                    + "nodeId='"
                    + nodeId
                    + '\''
                    + ", genesisHash='"
                    + genesisHash
                    + '\''
                    + ", blockNumber='"
                    + blockNumber
                    + '\''
                    + ", latestHash='"
                    + latestHash
                    + '\''
                    + '}';
        }
    }

    public static class SyncStatusInfo {
        private String isSyncing;
        private String protocolId;
        private String genesisHash;
        private String nodeId;
        private String blockNumber;
        private String latestHash;
        private String knownHighestNumber;
        private String txPoolSize;
        private List<PeersInfo> peers;
        private String knownLatestHash;

        public String getKnownLatestHash() {
            return knownLatestHash;
        }

        public void setKnownLatestHash(String knownLatestHash) {
            this.knownLatestHash = knownLatestHash;
        }

        public String getIsSyncing() {
            return isSyncing;
        }

        public void setIsSyncing(String isSyncing) {
            this.isSyncing = isSyncing;
        }

        public String getProtocolId() {
            return protocolId;
        }

        public void setProtocolId(String protocolId) {
            this.protocolId = protocolId;
        }

        public String getGenesisHash() {
            return genesisHash;
        }

        public void setGenesisHash(String genesisHash) {
            this.genesisHash = genesisHash;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getLatestHash() {
            return latestHash;
        }

        public void setLatestHash(String latestHash) {
            this.latestHash = latestHash;
        }

        public String getKnownHighestNumber() {
            return knownHighestNumber;
        }

        public void setKnownHighestNumber(String knownHighestNumber) {
            this.knownHighestNumber = knownHighestNumber;
        }

        public String getTxPoolSize() {
            return txPoolSize;
        }

        public void setTxPoolSize(String txPoolSize) {
            this.txPoolSize = txPoolSize;
        }

        public List<PeersInfo> getPeers() {
            return peers;
        }

        public void setPeers(List<PeersInfo> peers) {
            this.peers = peers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SyncStatusInfo that = (SyncStatusInfo) o;
            return Objects.equals(isSyncing, that.isSyncing)
                    && Objects.equals(protocolId, that.protocolId)
                    && Objects.equals(genesisHash, that.genesisHash)
                    && Objects.equals(nodeId, that.nodeId)
                    && Objects.equals(blockNumber, that.blockNumber)
                    && Objects.equals(latestHash, that.latestHash)
                    && Objects.equals(knownHighestNumber, that.knownHighestNumber)
                    && Objects.equals(txPoolSize, that.txPoolSize)
                    && Objects.equals(peers, that.peers)
                    && Objects.equals(knownLatestHash, that.knownLatestHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    isSyncing,
                    protocolId,
                    genesisHash,
                    nodeId,
                    blockNumber,
                    latestHash,
                    knownHighestNumber,
                    txPoolSize,
                    peers,
                    knownLatestHash);
        }

        @Override
        public String toString() {
            return "SyncStatusInfo{"
                    + "isSyncing='"
                    + isSyncing
                    + '\''
                    + ", protocolId='"
                    + protocolId
                    + '\''
                    + ", genesisHash='"
                    + genesisHash
                    + '\''
                    + ", nodeId='"
                    + nodeId
                    + '\''
                    + ", blockNumber='"
                    + blockNumber
                    + '\''
                    + ", latestHash='"
                    + latestHash
                    + '\''
                    + ", knownHighestNumber='"
                    + knownHighestNumber
                    + '\''
                    + ", txPoolSize='"
                    + txPoolSize
                    + '\''
                    + ", peers="
                    + peers
                    + ", knownLatestHash='"
                    + knownLatestHash
                    + '\''
                    + '}';
        }
    }
}
