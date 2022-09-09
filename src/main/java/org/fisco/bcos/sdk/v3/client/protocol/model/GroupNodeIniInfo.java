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
package org.fisco.bcos.sdk.v3.client.protocol.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupNodeIniInfo {
    public static class BinaryInfo {
        private String version;
        private String gitCommitHash;
        private String platform;
        private String buildTime;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getGitCommitHash() {
            return gitCommitHash;
        }

        public void setGitCommitHash(String gitCommitHash) {
            this.gitCommitHash = gitCommitHash;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getBuildTime() {
            return buildTime;
        }

        public void setBuildTime(String buildTime) {
            this.buildTime = buildTime;
        }

        @Override
        public String toString() {
            return "BinaryInfo{"
                    + "version='"
                    + version
                    + '\''
                    + ", gitCommitHash='"
                    + gitCommitHash
                    + '\''
                    + ", platform='"
                    + platform
                    + '\''
                    + ", buildTime='"
                    + buildTime
                    + '\''
                    + '}';
        }
    }

    private BinaryInfo binaryInfo;
    private String chainID;
    private String groupID;
    private Boolean smCryptoType;

    @JsonProperty("isWasm")
    private Boolean wasm;

    @JsonProperty("isAuthCheck")
    private Boolean isAuthCheck = false;

    private Boolean isSerialExecute = false;

    private String nodeID;
    private String nodeName;
    private String rpcServiceName;
    private String gatewayServiceName;

    public BinaryInfo getBinaryInfo() {
        return binaryInfo;
    }

    public void setBinaryInfo(BinaryInfo binaryInfo) {
        this.binaryInfo = binaryInfo;
    }

    public String getChainID() {
        return chainID;
    }

    public void setChainID(String chainID) {
        this.chainID = chainID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public Boolean getSmCryptoType() {
        return smCryptoType;
    }

    public void setSmCryptoType(Boolean smCryptoType) {
        this.smCryptoType = smCryptoType;
    }

    public Boolean getWasm() {
        return wasm;
    }

    public void setWasm(Boolean wasm) {
        this.wasm = wasm;
    }

    public Boolean getAuthCheck() {
        return isAuthCheck;
    }

    public void setAuthCheck(Boolean authCheck) {
        isAuthCheck = authCheck;
    }

    public Boolean getIsSerialExecute() {
        return isSerialExecute;
    }

    public void setSerialExecute(Boolean serialExecute) {
        this.isSerialExecute = serialExecute;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getRpcServiceName() {
        return rpcServiceName;
    }

    public void setRpcServiceName(String rpcServiceName) {
        this.rpcServiceName = rpcServiceName;
    }

    public String getGatewayServiceName() {
        return gatewayServiceName;
    }

    public void setGatewayServiceName(String gatewayServiceName) {
        this.gatewayServiceName = gatewayServiceName;
    }

    @Override
    public String toString() {
        return "GroupNodeIniInfo{"
                + "binaryInfo="
                + binaryInfo
                + ", chainID='"
                + chainID
                + '\''
                + ", groupID='"
                + groupID
                + '\''
                + ", smCryptoType="
                + smCryptoType
                + ", isWasm="
                + wasm
                + ", nodeID='"
                + nodeID
                + '\''
                + ", nodeName='"
                + nodeName
                + '\''
                + ", rpcServiceName='"
                + rpcServiceName
                + '\''
                + ", gatewayServiceName='"
                + gatewayServiceName
                + '\''
                + '}';
    }
}
