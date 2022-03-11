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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.List;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

/** getPeers */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Peers extends JsonRpcResponse<Peers.PeersInfo> {
    public Peers.PeersInfo getPeers() {
        return this.getResult();
    }

    @Override
    @JsonDeserialize(using = Peers.PeersDeserializer.class)
    public void setResult(Peers.PeersInfo result) {
        super.setResult(result);
    }

    public static class NodeIDInfo {
        private String group;
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
        private String p2pNodeID;
        private String endPoint;
        List<Peers.NodeIDInfo> groupNodeIDInfo;

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

        public List<Peers.NodeIDInfo> getGroupNodeIDInfo() {
            return groupNodeIDInfo;
        }

        public void setGroupNodeIDInfo(List<Peers.NodeIDInfo> groupNodeIDInfo) {
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
        private String p2pNodeID;
        private String endPoint;
        List<Peers.NodeIDInfo> groupNodeIDInfo;
        List<Peers.PeerInfo> peers;

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

        public List<Peers.NodeIDInfo> getGroupNodeIDInfo() {
            return groupNodeIDInfo;
        }

        public void setGroupNodeIDInfo(List<Peers.NodeIDInfo> groupNodeIDInfo) {
            this.groupNodeIDInfo = groupNodeIDInfo;
        }

        public List<Peers.PeerInfo> getPeers() {
            return peers;
        }

        public void setPeers(List<Peers.PeerInfo> peers) {
            this.peers = peers;
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
                    + ", peers="
                    + peers
                    + '}';
        }
    }

    // decode the block
    public static class PeersDeserializer extends JsonDeserializer<Peers.PeersInfo> {
        private final ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public Peers.PeersInfo deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return this.objectReader.readValue(jsonParser, Peers.PeersInfo.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }
}
