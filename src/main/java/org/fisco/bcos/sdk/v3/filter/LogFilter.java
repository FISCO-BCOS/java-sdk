package org.fisco.bcos.sdk.v3.filter;

import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.EthFilter;
import org.fisco.bcos.sdk.v3.client.protocol.response.EthLog;
import org.fisco.bcos.sdk.v3.client.protocol.response.Log;

/** Log filter handler. */
public class LogFilter extends Filter<Log> {

    protected final org.fisco.bcos.sdk.v3.client.protocol.request.EthFilter params;

    public LogFilter(
            Client client,
            Callback<Log> callback,
            org.fisco.bcos.sdk.v3.client.protocol.request.EthFilter params) {
        super(client, callback);
        this.params = params;
    }

    @Override
    protected EthFilter sendRequest() {
        return client.newFilter(params);
    }

    @Override
    protected void process(List<EthLog.LogResult> logResults) {
        for (EthLog.LogResult logResult : logResults) {
            if (logResult instanceof EthLog.LogObject) {
                Log log = ((EthLog.LogObject) logResult).get();
                callback.onEvent(log);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + " required LogObject");
            }
        }
    }
}
