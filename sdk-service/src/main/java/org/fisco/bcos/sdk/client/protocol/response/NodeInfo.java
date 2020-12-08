package org.fisco.bcos.sdk.client.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class NodeInfo extends JsonRpcResponse<NodeInfo.NodeInformation> {
    public NodeInfo.NodeInformation getNodeInfo() {
        return getResult();
    }

    public static class NodeInformation {
        @JsonProperty("NodeID")
        private String nodeID;

        @JsonProperty("IPAndPort")
        private String ipAndPort;

        @JsonProperty("Agency")
        private String agency;

        @JsonProperty("Node")
        private String node;

        @JsonProperty("Topic")
        private List<String> topic;

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

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
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
            NodeInformation that = (NodeInformation) o;
            return Objects.equals(nodeID, that.nodeID)
                    && Objects.equals(node, that.node)
                    && Objects.equals(agency, that.agency)
                    && Objects.equals(ipAndPort, that.ipAndPort)
                    && Objects.equals(topic, that.topic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, nodeID, agency, ipAndPort, topic);
        }

        @Override
        public String toString() {
            return "NodeInfo{"
                    + "nodeId='"
                    + nodeID
                    + '\''
                    + ", iPAndPort='"
                    + ipAndPort
                    + '\''
                    + ", node='"
                    + node
                    + '\''
                    + ", agency='"
                    + agency
                    + '\''
                    + ", topic='"
                    + topic
                    + '\''
                    + '}';
        }
    }
}
