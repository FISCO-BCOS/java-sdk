package org.fisco.bcos.sdk.client.protocol.response;

import java.util.List;
import org.fisco.bcos.sdk.model.JsonRpcResponse;

public class QueryPeers extends JsonRpcResponse<List<String>> {
    public List<String> getQueryPeers() {
        return getResult();
    }
}
