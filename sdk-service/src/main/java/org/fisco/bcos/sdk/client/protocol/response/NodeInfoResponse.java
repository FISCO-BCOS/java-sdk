package org.fisco.bcos.sdk.client.protocol.response;

import org.fisco.bcos.sdk.channel.model.NodeInfo;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class NodeInfoResponse extends JsonRpcResponse<NodeInfo> {
    public NodeInfo getNodeInfo() {
        return this.getResult();
    }
}
