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

package org.fisco.bcos.sdk.v3.client.protocol.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

public class ConsensusStatus extends JsonRpcResponse<ConsensusStatus.ConsensusStatusInfo> {
    public ConsensusStatus.ConsensusStatusInfo getConsensusStatus() {
        return this.getResult();
    }

    @Override
    @JsonDeserialize(converter = ConsensusStatusInfoConvert.class)
    public void setResult(ConsensusStatus.ConsensusStatusInfo result) {
        super.setResult(result);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConsensusNodeInfo {
        @JsonProperty("nodeID")
        private String nodeID;

        @JsonProperty("weight")
        private Integer weight;

        @JsonProperty("termWeight")
        private Integer termWeight;

        @JsonProperty("index")
        private Integer index;

        public String getNodeID() {
            return nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public Integer getTermWeight() {
            return termWeight;
        }

        public void setTermWeight(Integer termWeight) {
            this.termWeight = termWeight;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "ConsensusNodeInfo{"
                    + "nodeID='"
                    + nodeID
                    + '\''
                    + ", weight='"
                    + weight
                    + '\''
                    + ", termWeight='"
                    + termWeight
                    + '\''
                    + ", index='"
                    + index
                    + '\''
                    + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConsensusNodeInfo that = (ConsensusNodeInfo) o;
            return Objects.equals(nodeID, that.nodeID)
                    && Objects.equals(weight, that.weight)
                    && Objects.equals(index, that.index);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeID, weight, index);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConsensusStatusInfo {
        @JsonProperty("nodeID")
        private String nodeID;

        @JsonProperty("index")
        private String index;

        @JsonProperty("leaderIndex")
        private Integer leaderIndex;

        @JsonProperty("consensusNodesNum")
        private Integer consensusNodesNum;

        @JsonProperty("maxFaultyQuorum")
        private Integer maxFaultyQuorum;

        @JsonProperty("minRequiredQuorum")
        private Integer minRequiredQuorum;

        @JsonProperty("isConsensusNode")
        private Boolean isConsensusNode;

        @JsonProperty("blockNumber")
        private Integer blockNumber;

        @JsonProperty("hash")
        private String hash;

        @JsonProperty("timeout")
        private Boolean timeout;

        @JsonProperty("changeCycle")
        private Integer changeCycle;

        @JsonProperty("view")
        private Integer view;

        @JsonProperty("connectedNodeList")
        private Integer connectedNodeList;

        @JsonProperty("consensusNodeList")
        private List<ConsensusStatus.ConsensusNodeInfo> consensusNodeInfos;

        public String getNodeID() {
            return nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public Integer getLeaderIndex() {
            return leaderIndex;
        }

        public void setLeaderIndex(Integer leaderIndex) {
            this.leaderIndex = leaderIndex;
        }

        public Integer getConsensusNodesNum() {
            return consensusNodesNum;
        }

        public void setConsensusNodesNum(Integer consensusNodesNum) {
            this.consensusNodesNum = consensusNodesNum;
        }

        public Integer getMaxFaultyQuorum() {
            return maxFaultyQuorum;
        }

        public void setMaxFaultyQuorum(Integer maxFaultyQuorum) {
            this.maxFaultyQuorum = maxFaultyQuorum;
        }

        public Integer getMinRequiredQuorum() {
            return minRequiredQuorum;
        }

        public void setMinRequiredQuorum(Integer minRequiredQuorum) {
            this.minRequiredQuorum = minRequiredQuorum;
        }

        public boolean isConsensusNode() {
            return isConsensusNode;
        }

        public void setConsensusNode(boolean consensusNode) {
            isConsensusNode = consensusNode;
        }

        public Integer getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(Integer blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public Boolean getTimeout() {
            return timeout;
        }

        public void setTimeout(Boolean timeout) {
            this.timeout = timeout;
        }

        public Integer getChangeCycle() {
            return changeCycle;
        }

        public void setChangeCycle(Integer changeCycle) {
            this.changeCycle = changeCycle;
        }

        public Integer getView() {
            return view;
        }

        public void setView(Integer view) {
            this.view = view;
        }

        public Boolean getConsensusNode() {
            return isConsensusNode;
        }

        public void setConsensusNode(Boolean consensusNode) {
            isConsensusNode = consensusNode;
        }

        public Integer getConnectedNodeList() {
            return connectedNodeList;
        }

        public void setConnectedNodeList(Integer connectedNodeList) {
            this.connectedNodeList = connectedNodeList;
        }

        public List<ConsensusNodeInfo> getConsensusNodeInfos() {
            return consensusNodeInfos;
        }

        public void setConsensusNodeInfos(List<ConsensusNodeInfo> consensusNodeInfos) {
            this.consensusNodeInfos = consensusNodeInfos;
        }

        @Override
        public String toString() {
            return "ConsensusStatusInfo{"
                    + "nodeID='"
                    + nodeID
                    + '\''
                    + ", index="
                    + index
                    + ", leaderIndex="
                    + leaderIndex
                    + ", consensusNodesNum="
                    + consensusNodesNum
                    + ", maxFaultyQuorum="
                    + maxFaultyQuorum
                    + ", minRequiredQuorum="
                    + minRequiredQuorum
                    + ", isConsensusNode="
                    + isConsensusNode
                    + ", blockNumber="
                    + blockNumber
                    + ", hash='"
                    + hash
                    + '\''
                    + ", timeout="
                    + timeout
                    + ", changeCycle="
                    + changeCycle
                    + ", view="
                    + view
                    + ", connectedNodeList="
                    + connectedNodeList
                    + ", consensusNodeInfos="
                    + consensusNodeInfos
                    + '}';
        }
    }

    public static class ConsensusStatusInfoConvert
            implements Converter<String, ConsensusStatusInfo> {
        @Override
        public ConsensusStatus.ConsensusStatusInfo convert(String value) {
            try {
                return ObjectMapperFactory.getObjectMapper()
                        .readValue(value, ConsensusStatus.ConsensusStatusInfo.class);
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
            return typeFactory.constructSimpleType(ConsensusStatus.ConsensusStatusInfo.class, null);
        }
    }
}
