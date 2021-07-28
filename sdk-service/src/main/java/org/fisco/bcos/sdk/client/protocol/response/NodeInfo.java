package org.fisco.bcos.sdk.client.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

import java.util.Objects;

public class NodeInfo extends JsonRpcResponse<NodeInfo.NodeInformation> {
    public NodeInfo.NodeInformation getNodeInfo() {
        return this.getResult();
    }

    public static class NodeInformation {
        @JsonProperty("nodeID")
        private String nodeID;

        @JsonProperty("agency")
        private String agency;

        @JsonProperty("version")
        private String version;

        @JsonProperty("supportedVersion")
        private String supportedVersion;

        @JsonProperty("chainID")
        private String chainId;

        @JsonProperty("groupID")
        private String groupId;

        @JsonProperty("buildTime")
        private String buildTime;

        @JsonProperty("BuildType")
        private String buildType;

        @JsonProperty("gitCommit")
        private String gitCommit;

        private Boolean smCrypto;
        private Boolean wasm;

        public String getNodeID() {
            return this.nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public String getAgency() {
            return this.agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
        }

        public String getVersion() {
            return this.version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getSupportedVersion() {
            return this.supportedVersion;
        }

        public void setSupportedVersion(String supportedVersion) {
            this.supportedVersion = supportedVersion;
        }

        public String getChainId() {
            return this.chainId;
        }

        public void setChainId(String chainId) {
            this.chainId = chainId;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getBuildTime() {
            return this.buildTime;
        }

        public void setBuildTime(String buildTime) {
            this.buildTime = buildTime;
        }

        public String getBuildType() {
            return this.buildType;
        }

        public void setBuildType(String buildType) {
            this.buildType = buildType;
        }

        public String getGitCommit() {
            return this.gitCommit;
        }

        public void setGitCommit(String gitCommit) {
            this.gitCommit = gitCommit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            NodeInformation that = (NodeInformation) o;
            return Objects.equals(this.nodeID, that.nodeID)
                    && Objects.equals(this.agency, that.agency)
                    && Objects.equals(this.version, that.version)
                    && Objects.equals(this.supportedVersion, that.supportedVersion)
                    && Objects.equals(this.chainId, that.chainId)
                    && Objects.equals(this.buildTime, that.buildTime)
                    && Objects.equals(this.buildType, that.buildType)
                    && Objects.equals(this.gitCommit, that.gitCommit);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.nodeID,
                    this.agency,
                    this.version,
                    this.supportedVersion,
                    this.chainId,
                    this.buildTime,
                    this.buildType,
                    this.gitCommit);
        }

        @Override
        public String toString() {
            return "NodeInfo{"
                    + "nodeId='"
                    + this.nodeID
                    + '\''
                    + ", agency='"
                    + this.agency
                    + '\''
                    + ", version='"
                    + this.version
                    + '\''
                    + ", supportedVersion='"
                    + this.supportedVersion
                    + '\''
                    + ", chainId='"
                    + this.chainId
                    + '\''
                    + ", buildTime='"
                    + this.buildTime
                    + '\''
                    + ", buildType='"
                    + this.buildType
                    + '\''
                    + ", gitCommit='"
                    + this.gitCommit
                    + '\''
                    + '}';
        }


        public Boolean getSmCrypto() {
            return this.smCrypto;
        }

        public void setSmCrypto(Boolean smCrypto) {
            this.smCrypto = smCrypto;
        }

        public Boolean getWasm() {
            return this.wasm;
        }

        public void setWasm(Boolean wasm) {
            this.wasm = wasm;
        }
    }
}
