package org.fisco.bcos.sdk.client.response;

import org.fisco.bcos.sdk.client.RPCResponse;

/** getBlockHashByNumber */
public class BlockHash extends RPCResponse<String> {
    public String getBlockHashByNumber() {
        return getResult();
    }
}
