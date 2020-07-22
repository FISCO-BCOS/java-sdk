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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

/** getPeers */
public class Peers extends JsonRpcResponse<List<Peers.PeerInfo>> {
    public List<Peers.PeerInfo> getPeers() {
        return getResult();
    }

    public static class PeerInfo {
        @JsonProperty("NodeID")
        private String nodeID;

        @JsonProperty("IPAndPort")
        private String ipAndPort;

        @JsonProperty("Agency")
        private String agency;

        @JsonProperty("Topic")
        private List<String> topic;

        @JsonProperty("Node")
        private String node;

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public String getNodeID() {
            return nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public String getIpAndPort() {
            return ipAndPort;
        }

        public void setIpAndPort(String ipAndPort) {
            this.ipAndPort = ipAndPort;
        }

        public String getAgency() {
            return agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
        }

        public List<String> getTopic() {
            return topic;
        }

        public void setTopic(List<String> topic) {
            this.topic = topic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PeerInfo peerInfo = (PeerInfo) o;
            return Objects.equals(nodeID, peerInfo.nodeID)
                    && Objects.equals(ipAndPort, peerInfo.ipAndPort)
                    && Objects.equals(agency, peerInfo.agency)
                    && Objects.equals(topic, peerInfo.topic)
                    && Objects.equals(node, peerInfo.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeID, ipAndPort, agency, topic, node);
        }

        @Override
        public String toString() {
            return "PeerInfo{"
                    + "nodeID='"
                    + nodeID
                    + '\''
                    + ", ipAndPort='"
                    + ipAndPort
                    + '\''
                    + ", agency='"
                    + agency
                    + '\''
                    + ", topic="
                    + topic
                    + ", node='"
                    + node
                    + '\''
                    + '}';
        }
    }
}
