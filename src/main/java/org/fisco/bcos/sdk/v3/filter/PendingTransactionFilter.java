package org.fisco.bcos.sdk.v3.filter;

import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogFilterResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogWrapper;

/** Handler for working with transaction filter requests. */
public class PendingTransactionFilter extends Filter<String> {

    public PendingTransactionFilter(Client client, Callback<String> callback) {
        super(client, callback);
    }

    @Override
    protected LogFilterResponse sendRequest() {
        return client.newPendingTransactionFilter();
    }

    @Override
    protected void process(List<LogWrapper.LogResult> logResults) {
        for (LogWrapper.LogResult logResult : logResults) {
            if (logResult instanceof LogWrapper.Hash) {
                String transactionHash = ((LogWrapper.Hash) logResult).get();
                callback.onEvent(transactionHash);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + ", required Hash");
            }
        }
    }
}
