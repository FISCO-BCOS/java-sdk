package org.fisco.bcos.sdk.client.response;


import org.fisco.bcos.sdk.client.RPCResponse;

/**
 * Get code response
 * @author Maggie
 */
public class Code extends RPCResponse<String> {
    public String getCode() {
        return getResult();
    }
}
