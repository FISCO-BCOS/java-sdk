/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolService {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolService.class);
    public static Integer DEFAULT_KEEP_ALIVE_TIME = 60;
    private final ExecutorService threadPool;

    public ThreadPoolService(String threadName, Integer maxBlockingQueueSize) {
        this(threadName, Runtime.getRuntime().availableProcessors(), maxBlockingQueueSize);
    }

    public ThreadPoolService(
            String threadName, Integer corePoolSize, Integer maxBlockingQueueSize) {
        this(threadName, corePoolSize, corePoolSize, DEFAULT_KEEP_ALIVE_TIME, maxBlockingQueueSize);
        logger.debug(
                "Create ThreadPoolService, threadName: {}, corePoolSize: {}, maxBlockingQueueSize: {}",
                threadName,
                corePoolSize,
                maxBlockingQueueSize);
    }

    public ThreadPoolService(
            String threadName,
            Integer corePoolSize,
            Integer maximumPoolSize,
            Integer keepAliveTime,
            Integer maxBlockingQueueSize) {
        // set thread name
        ThreadFactory threadFactory =
                new BasicThreadFactory.Builder().namingPattern(threadName).build();
        threadPool =
                new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(maxBlockingQueueSize),
                        threadFactory,
                        new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void stop() {
        stopThreadPool(threadPool);
    }

    public static void stopThreadPool(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            while (!threadPool.isTerminated()) {
                threadPool.awaitTermination(10, TimeUnit.MILLISECONDS);
            }
            threadPool.shutdownNow();
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
