package org.fisco.bcos.sdk.client.response;

import java.util.List;
import org.fisco.bcos.sdk.client.RPCResponse;

/** getGroupList */
public class GroupList extends RPCResponse<List<String>> {

    public List<String> getGroupList() {
        return getResult();
    }
}
