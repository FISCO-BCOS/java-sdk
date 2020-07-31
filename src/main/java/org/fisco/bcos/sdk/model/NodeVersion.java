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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.fisco.bcos.sdk.client.protocol.response.JsonRpcResponse;

/** getNodeVersion. */
public class NodeVersion extends JsonRpcResponse<NodeVersion.ClientVersion> {
    public ClientVersion getNodeVersion() {
        return getResult();
    }

    public static class ClientVersion {
        @JsonProperty("FISCO-BCOS Version")
        private String version;

        @JsonProperty("Supported Version")
        private String supportedVersion;

        @JsonProperty("Chain Id")
        private String chainId;

        @JsonProperty("Build Time")
        private String buildTime;

        @JsonProperty("Build Type")
        private String buildType;

        @JsonProperty("Git Branch")
        private String gitBranch;

        @JsonProperty("Git Commit Hash")
        private String gitCommitHash;

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

        public String getGitCommitHash() {
            return gitCommitHash;
        }

        public void setGitCommitHash(String gitCommitHash) {
            this.gitCommitHash = gitCommitHash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientVersion that = (ClientVersion) o;
            return Objects.equals(version, that.version)
                    && Objects.equals(supportedVersion, that.supportedVersion)
                    && Objects.equals(chainId, that.chainId)
                    && Objects.equals(buildTime, that.buildTime)
                    && Objects.equals(buildType, that.buildType)
                    && Objects.equals(gitBranch, that.gitBranch)
                    && Objects.equals(gitCommitHash, that.gitCommitHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    version,
                    supportedVersion,
                    chainId,
                    buildTime,
                    buildType,
                    gitBranch,
                    gitCommitHash);
        }

        @Override
        public String toString() {
            return "ClientVersion{"
                    + "version='"
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
                    + ", gitCommitHash='"
                    + gitCommitHash
                    + '\''
                    + '}';
        }
    }
}
