package org.fisco.bcos.sdk.v3.filter;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogFilterResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogWrapper;
import org.fisco.bcos.sdk.v3.client.protocol.response.UninstallLogFilter;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Class for creating managed filter requests with callbacks. */
public abstract class Filter<T> {

    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    protected final Client client;
    protected Callback<T> callback;

    protected volatile LogFilterResponse filter;

    protected ScheduledFuture<?> schedule;

    protected ScheduledExecutorService scheduledExecutorService;

    protected long blockTime;

    private static final String FILTER_NOT_FOUND_PATTERN = "(?i)\\bfilter\\s+not\\s+found\\b";

    public Filter(Client client, Callback<T> callback) {
        this.client = client;
        this.callback = callback;
    }

    public void run(ScheduledExecutorService scheduledExecutorService, long blockTime)
            throws FilterException {
        try {
            LogFilterResponse logFilterResponse = sendRequest();
            filter = logFilterResponse;
        } catch (Exception e) {
            throwException(e);
        }
        this.scheduledExecutorService = scheduledExecutorService;
        this.blockTime = blockTime;

        // wait for the completion of obtaining historical logs before starting to obtain new logs
        schedule =
                scheduledExecutorService.scheduleAtFixedRate(
                        () -> {
                            this.pollFilter(this.filter);
                        },
                        0,
                        blockTime,
                        TimeUnit.MILLISECONDS);
    }

    private void pollFilter(LogFilterResponse logFilterResponse) {
        client.getFilterChangesAsync(
                logFilterResponse,
                new RespCallback<LogWrapper>() {
                    @Override
                    public void onResponse(LogWrapper logWrapper) {
                        process(logWrapper.getLogs());
                    }

                    @Override
                    public void onError(Response error) {
                        String message = error.getErrorMessage();
                        if (Pattern.compile(FILTER_NOT_FOUND_PATTERN).matcher(message).find()) {
                            reinstallFilter();
                        } else {
                            log.warn("Error sending request, ", error);
                        }
                    }
                });
    }

    protected abstract LogFilterResponse sendRequest();

    protected abstract void process(List<LogWrapper.LogResult> logResults);

    private void reinstallFilter() {
        log.warn(
                "Previously installed filter has not been found, trying to re-install. Filter id: {}",
                filter.getFilterId());
        schedule.cancel(false);
        this.run(scheduledExecutorService, blockTime);
    }

    public void cancel() throws FilterException {
        schedule.cancel(false);
        try {
            UninstallLogFilter uninstallLogFilter = client.uninstallFilter(filter);
            if (!uninstallLogFilter.isUninstalled()) {
                throw new FilterException(
                        "Filter with id '" + filter.getFilterId() + "' failed to uninstall");
            }
        } catch (Exception e) {
            throwException(e);
        }
    }

    void throwException(JsonRpcResponse.Error error) {
        throw new FilterException(
                "Invalid request: " + (error == null ? "Unknown Error" : error.getMessage()));
    }

    void throwException(Throwable cause) {
        throw new FilterException("Error sending request", cause);
    }
}
