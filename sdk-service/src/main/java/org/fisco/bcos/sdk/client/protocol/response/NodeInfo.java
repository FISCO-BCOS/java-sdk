package org.fisco.bcos.sdk.client.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class NodeInfo extends JsonRpcResponse<NodeInfo.NodeInformation> {
    public NodeInfo.NodeInformation getNodeInfo() {
        return getResult();
    }

    public static class NodeInformation {
        @JsonProperty("NodeID")
        private String nodeID;

        @JsonProperty("Agency")
        private String agency;

        @JsonProperty("Version")
        private String version;

        @JsonProperty("SupportedVersion")
        private String supportedVersion;

        @JsonProperty("ChainId")
        private String chainId;

        @JsonProperty("BuildTime")
        private String buildTime;

        @JsonProperty("BuildType")
        private String buildType;

        @JsonProperty("GitBranch")
        private String gitBranch;

        @JsonProperty("GitCommit")
        private String gitCommit;

        public String getNodeID() {
            return nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public String getAgency() {
            return agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getSupportedVersion() {
            return supportedVersion;
        }

        public void setSupportedVersion(String supportedVersion) {
            this.supportedVersion = supportedVersion;
        }

        public String getChainId() {
            return chainId;
        }

        public void setChainId(String chainId) {
            this.chainId = chainId;
        }

        public String getBuildTime() {
            return buildTime;
        }

        public void setBuildTime(String buildTime) {
            this.buildTime = buildTime;
        }

        public String getBuildType() {
            return buildType;
        }

        public void setBuildType(String buildType) {
            this.buildType = buildType;
        }

        public String getGitBranch() {
            return gitBranch;
        }

        public void setGitBranch(String gitBranch) {
            this.gitBranch = gitBranch;
        }

        public String getGitCommit() {
            return gitCommit;
        }

        public void setGitCommit(String gitCommit) {
            this.gitCommit = gitCommit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeInformation that = (NodeInformation) o;
            return Objects.equals(nodeID, that.nodeID)
                    && Objects.equals(agency, that.agency)
                    && Objects.equals(version, that.version)
                    && Objects.equals(supportedVersion, that.supportedVersion)
                    && Objects.equals(chainId, that.chainId)
                    && Objects.equals(buildTime, that.buildTime)
                    && Objects.equals(buildType, that.buildType)
                    && Objects.equals(gitBranch, that.gitBranch)
                    && Objects.equals(gitCommit, that.gitCommit);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    nodeID,
                    agency,
                    version,
                    supportedVersion,
                    chainId,
                    buildTime,
                    buildType,
                    gitBranch,
                    gitCommit);
        }

        @Override
        public String toString() {
            return "NodeInfo{"
                    + "nodeId='"
                    + nodeID
                    + '\''
                    + ", agency='"
                    + agency
                    + '\''
                    + ", version='"
                    + version
                    + '\''
                    + ", supportedVersion='"
                    + supportedVersion
                    + '\''
                    + ", chainId='"
                    + chainId
                    + '\''
                    + ", buildTime='"
                    + buildTime
                    + '\''
                    + ", buildType='"
                    + buildType
                    + '\''
                    + ", gitBranch='"
                    + gitBranch
                    + '\''
                    + ", gitCommit='"
                    + gitCommit
                    + '\''
                    + '}';
        }
    }
}
