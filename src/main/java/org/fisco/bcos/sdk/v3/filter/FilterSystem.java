package org.fisco.bcos.sdk.v3.filter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.request.EthFilter;
import org.fisco.bcos.sdk.v3.client.protocol.response.Log;

public class FilterSystem {
    private ScheduledExecutorService scheduledExecutorService;
    private Client client;
    private long pollingInterval = 1 * 1000;

    public FilterSystem(Client client, int poolSize, long pollingInterval) {
        this.client = client;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(poolSize);
        this.pollingInterval = pollingInterval;
    }

    public FilterSystem(Client client, int poolSize) {
        this.client = client;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(poolSize);
    }

    private <T> void run(Filter<T> filter, long pollingInterval) {
        filter.run(scheduledExecutorService, pollingInterval);
    }

    public Publisher<Log> logPublisher(EthFilter filter) {
        Publisher<Log> publisher = new Publisher<Log>();
        run(new LogFilter(client, log -> publisher.publish(log), filter), pollingInterval);
        return publisher;
    }

    public Publisher<String> blockHashPublisher() {
        Publisher<String> publisher = new Publisher<String>();
        run(new BlockFilter(client, log -> publisher.publish(log)), pollingInterval);
        return publisher;
    }

    public Publisher<String> transactionHashPublisher() {
        Publisher<String> publisher = new Publisher<String>();
        run(new PendingTransactionFilter(client, log -> publisher.publish(log)), pollingInterval);
        return publisher;
    }

    public void stop() {
        // Disable new tasks from being submitted
        scheduledExecutorService.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks
                scheduledExecutorService.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
