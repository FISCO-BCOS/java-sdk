package org.fisco.bcos.sdk.v3.filter;

import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.request.LogFilterRequest;
import org.fisco.bcos.sdk.v3.client.protocol.response.Log;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogFilterResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogWrapper;

/** Log filter handler. */
public class LogFilter extends Filter<Log> {

    protected final LogFilterRequest params;

    public LogFilter(Client client, Callback<Log> callback, LogFilterRequest params) {
        super(client, callback);
        this.params = params;
    }

    @Override
    protected LogFilterResponse sendRequest() {
        return client.newFilter(params);
    }

    @Override
    protected void process(List<LogWrapper.LogResult> logResults) {
        for (LogWrapper.LogResult logResult : logResults) {
            if (logResult instanceof LogWrapper.LogObject) {
                Log log = ((LogWrapper.LogObject) logResult).get();
                callback.onEvent(log);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + " required LogObject");
            }
        }
    }
}
