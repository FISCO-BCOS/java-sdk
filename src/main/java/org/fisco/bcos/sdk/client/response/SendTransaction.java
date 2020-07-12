package org.fisco.bcos.sdk.client.response;

import org.fisco.bcos.sdk.client.RPCResponse;

/**
 * Return data structure of send transaction
 */
public class SendTransaction extends RPCResponse<String> {
    public String getTransactionHash() {
        return getResult();
    }
}
