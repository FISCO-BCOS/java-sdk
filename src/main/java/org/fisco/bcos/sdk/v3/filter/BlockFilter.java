package org.fisco.bcos.sdk.v3.filter;

import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogFilterResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogWrapper;

/** Handler for working with block filter requests. */
public class BlockFilter extends Filter<String> {

    public BlockFilter(Client client, Callback<String> callback) {
        super(client, callback);
    }

    @Override
    protected LogFilterResponse sendRequest() {
        return client.newBlockFilter();
    }

    @Override
    protected void process(List<LogWrapper.LogResult> logResults) {
        for (LogWrapper.LogResult logResult : logResults) {
            if (logResult instanceof LogWrapper.Hash) {
                String blockHash = ((LogWrapper.Hash) logResult).get();
                callback.onEvent(blockHash);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + ", required Hash");
            }
        }
    }
}
