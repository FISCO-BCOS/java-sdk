/*
 * Copyright 2014-2021  [fisco-dev]
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
package org.fisco.bcos.sdk.client.protocol.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupNodeGenesisInfo {
    GroupNodeGenesisInfo() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sealer {
        private String nodeID;
        private Integer weight;

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

        @Override
        public String toString() {
            return "Sealer{" + "nodeID='" + nodeID + '\'' + ", weight=" + weight + '}';
        }
    }

    private String consensusType;
    private Integer blockTxCountLimit;
    private Integer txGasLimit;
    private Integer consensusLeaderPeriod;
    List<Sealer> sealerList;

    public String getConsensusType() {
        return consensusType;
    }

    public void setConsensusType(String consensusType) {
        this.consensusType = consensusType;
    }

    public Integer getBlockTxCountLimit() {
        return blockTxCountLimit;
    }

    public void setBlockTxCountLimit(Integer blockTxCountLimit) {
        this.blockTxCountLimit = blockTxCountLimit;
    }

    public Integer getTxGasLimit() {
        return txGasLimit;
    }

    public void setTxGasLimit(Integer txGasLimit) {
        this.txGasLimit = txGasLimit;
    }

    public Integer getConsensusLeaderPeriod() {
        return consensusLeaderPeriod;
    }

    public void setConsensusLeaderPeriod(Integer consensusLeaderPeriod) {
        this.consensusLeaderPeriod = consensusLeaderPeriod;
    }

    public List<Sealer> getSealerList() {
        return sealerList;
    }

    public void setSealerList(List<Sealer> sealerList) {
        this.sealerList = sealerList;
    }

    @Override
    public String toString() {
        return "GroupNodeGenesisInfo{"
                + "consensusType='"
                + consensusType
                + '\''
                + ", blockTxCountLimit="
                + blockTxCountLimit
                + ", txGasLimit="
                + txGasLimit
                + ", consensusLeaderPeriod="
                + consensusLeaderPeriod
                + ", sealerList="
                + sealerList
                + '}';
    }
}
