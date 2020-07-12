package org.fisco.bcos.sdk.client.response;

import org.fisco.bcos.sdk.client.RPCResponse;

/** getConsensusStatus */
public class ConsensusStatus extends RPCResponse<String> {
    public String getConsensusStatus() {
        return getResult();
    }
}
