package org.fisco.bcos.sdk.client.response;

import org.fisco.bcos.sdk.client.RPCResponse;

/** getSystemConfigByKey */
public class SystemConfig extends RPCResponse<String> {
    public String getSystemConfigByKey() {
        return getResult();
    }
}
