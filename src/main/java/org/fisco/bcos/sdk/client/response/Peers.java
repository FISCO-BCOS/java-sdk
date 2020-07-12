package org.fisco.bcos.sdk.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.fisco.bcos.sdk.client.RPCResponse;

/** getPeers */
public class Peers extends RPCResponse<List<Peers.Peer>> {

    public List<Peers.Peer> getPeers() {
        return getResult();
    }

    public static class Peer {
        @JsonProperty("IPAndPort")
        private String IPAndPort;

        @JsonProperty("NodeID")
        private String nodeID;

        @JsonProperty("Topic")
        private List<String> topic;

        public Peer() {}

        public Peer(String IPAndPort, String nodeID, List<String> topic) {
            this.IPAndPort = IPAndPort;
            this.nodeID = nodeID;
            this.topic = topic;
        }

        public String getIPAndPort() {
            return IPAndPort;
        }

        public void setIPAndPort(String IPAndPort) {
            this.IPAndPort = IPAndPort;
        }

        public String getNodeID() {
            return nodeID;
        }

        public void setNodeID(String nodeID) {
            this.nodeID = nodeID;
        }

        public List<String> getTopic() {
            return topic;
        }

        public void setTopic(List<String> topic) {
            this.topic = topic;
        }
    }
}
