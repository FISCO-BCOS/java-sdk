package org.fisco.bcos.sdk.client.response;

import java.util.List;
import org.fisco.bcos.sdk.client.RPCResponse;

/** getNodeIDList */
public class NodeIDList extends RPCResponse<List<String>> {
    public List<String> getNodeIDList() {
        return getResult();
    }
}
