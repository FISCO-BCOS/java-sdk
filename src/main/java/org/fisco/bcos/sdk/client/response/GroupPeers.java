package org.fisco.bcos.sdk.client.response;

import java.util.List;
import org.fisco.bcos.sdk.client.RPCResponse;

/** getGroupPeers */
public class GroupPeers extends RPCResponse<List<String>> {
    public List<String> getGroupPeers() {
        return getResult();
    }
}
