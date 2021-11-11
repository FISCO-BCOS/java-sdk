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
import org.fisco.bcos.sdk.model.JsonRpcResponse;

/** getPeers */
public class Peers extends JsonRpcResponse<Peers.PeersInfo> {
    public Peers.PeersInfo getPeersInfo() {
        return this.getResult();
    }

    @Override
    @JsonDeserialize(converter = Peers.PeersInfoConvert.class)
    public void setResult(Peers.PeersInfo result) {
        super.setResult(result);
    }

    public static class NodeIDInfo {
        @JsonProperty("group")
        private String group;

        @JsonProperty("nodeIDList")
        private List<String> nodeIDList;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public List<String> getNodeIDList() {
            return nodeIDList;
        }

        public void setNodeIDList(List<String> nodeIDList) {
            this.nodeIDList = nodeIDList;
        }

        @Override
        public String toString() {
            return "NodeIDInfo{" + "group='" + group + '\'' + ", nodeIDList=" + nodeIDList + '}';
        }
    }

    public static class PeerInfo {
        @JsonProperty("p2pNodeID")
        private String p2pNodeID;

        @JsonProperty("endPoint")
        private String endPoint;

        @JsonProperty("groupNodeIDInfo")
        List<NodeIDInfo> groupNodeIDInfo;

        public String getP2pNodeID() {
            return p2pNodeID;
        }

        public void setP2pNodeID(String p2pNodeID) {
            this.p2pNodeID = p2pNodeID;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public List<NodeIDInfo> getGroupNodeIDInfo() {
            return groupNodeIDInfo;
        }

        public void setGroupNodeIDInfo(List<NodeIDInfo> groupNodeIDInfo) {
            this.groupNodeIDInfo = groupNodeIDInfo;
        }

        @Override
        public String toString() {
            return "PeerInfo{"
                    + "p2pNodeID='"
                    + p2pNodeID
                    + '\''
                    + ", endPoint='"
                    + endPoint
                    + '\''
                    + ", groupNodeIDInfo="
                    + groupNodeIDInfo
                    + '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PeersInfo {
        @JsonProperty("p2pNodeID")
        private String p2pNodeID;

        @JsonProperty("endPoint")
        private String endPoint;

        @JsonProperty("groupNodeIDInfo")
        List<NodeIDInfo> groupNodeIDInfo;

        @JsonProperty("peers")
        List<PeerInfo> peersInfo;

        public String getP2pNodeID() {
            return p2pNodeID;
        }

        public void setP2pNodeID(String p2pNodeID) {
            this.p2pNodeID = p2pNodeID;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public List<NodeIDInfo> getGroupNodeIDInfo() {
            return groupNodeIDInfo;
        }

        public void setGroupNodeIDInfo(List<NodeIDInfo> groupNodeIDInfo) {
            this.groupNodeIDInfo = groupNodeIDInfo;
        }

        public List<PeerInfo> getPeersInfo() {
            return peersInfo;
        }

        public void setPeersInfo(List<PeerInfo> peersInfo) {
            this.peersInfo = peersInfo;
        }

        @Override
        public String toString() {
            return "PeersInfo{"
                    + "p2pNodeID='"
                    + p2pNodeID
                    + '\''
                    + ", endPoint='"
                    + endPoint
                    + '\''
                    + ", groupNodeIDInfo="
                    + groupNodeIDInfo
                    + ", peersInfo="
                    + peersInfo
                    + '}';
        }
    }

    public static class PeersInfoConvert implements Converter<String, Peers.PeersInfo> {
        @Override
        public Peers.PeersInfo convert(String value) {
            try {
                return new ObjectMapper().readValue(value, Peers.PeersInfo.class);
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
