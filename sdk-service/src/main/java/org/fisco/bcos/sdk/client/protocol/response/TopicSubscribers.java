package org.fisco.bcos.sdk.client.protocol.response;

import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class TopicSubscribers extends JsonRpcResponse<TopicSubscribers.TopicSubscribersInfo> {

    public TopicSubscribers.TopicSubscribersInfo getTopicSubscribersInfo() {
        return getResult();
    }

    public static class TopicSubscribersInfo {
        private int total;
        private List<String> nodes;
        private List<String> sdks;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public List<String> getSdks() {
            return sdks;
        }

        public void setSdks(List<String> sdks) {
            this.sdks = sdks;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TopicSubscribers.TopicSubscribersInfo that = (TopicSubscribers.TopicSubscribersInfo) o;
            return Objects.equals(total, that.total)
                    && Objects.equals(nodes, that.nodes)
                    && Objects.equals(sdks, that.sdks);
        }

        @Override
        public int hashCode() {
            return Objects.hash(total, nodes, sdks);
        }

        @Override
        public String toString() {
            return "TopicSubscribersInfo{"
                    + "total="
                    + total
                    + ", nodes="
                    + nodes
                    + ", sdks="
                    + sdks
                    + '}';
        }
    }
}
