package org.fisco.bcos.sdk.client.response;

import java.util.List;
import org.fisco.bcos.sdk.client.RPCResponse;

public class ObserverList extends RPCResponse<List<String>> {

    public List<String> getObserverList() {
        return getResult();
    }
}
