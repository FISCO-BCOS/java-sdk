/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.config.model;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolConfig {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);
    public static String DEFAULT_MAX_BLOCKING_QUEUE_SIZE = "102400";
    private Integer channelProcessorThreadSize;
    private Integer receiptProcessorThreadSize;
    private Integer maxBlockingQueueSize;

    public ThreadPoolConfig(ConfigProperty configProperty) {
        Map<String, Object> threadPoolConfig = configProperty.getThreadPool();
        String cpuNum = String.valueOf(Runtime.getRuntime().availableProcessors());
        String channelProcessorThread =
                ConfigProperty.getValue(threadPoolConfig, "channelProcessorThreadSize", cpuNum);
        String receiptProcessorThread =
                ConfigProperty.getValue(threadPoolConfig, "receiptProcessorThreadSize", cpuNum);
        channelProcessorThreadSize = Integer.valueOf(channelProcessorThread);
        receiptProcessorThreadSize = Integer.valueOf(receiptProcessorThread);
        if (channelProcessorThreadSize.intValue() <= 0) {
            channelProcessorThreadSize = Runtime.getRuntime().availableProcessors();
        }
        if (receiptProcessorThreadSize.intValue() <= 0) {
            receiptProcessorThreadSize = Runtime.getRuntime().availableProcessors();
        }
        maxBlockingQueueSize =
                Integer.valueOf(
                        ConfigProperty.getValue(
                                threadPoolConfig,
                                "maxBlockingQueueSize",
                                DEFAULT_MAX_BLOCKING_QUEUE_SIZE));
        if (maxBlockingQueueSize.intValue() <= 0) {
            maxBlockingQueueSize = Integer.valueOf(DEFAULT_MAX_BLOCKING_QUEUE_SIZE);
        }
        logger.debug(
                "Init ThreadPoolConfig, channelProcessorThreadSize: {}, receiptProcessorThreadSize: {}, maxBlockingQueueSize: {}",
                channelProcessorThreadSize,
                receiptProcessorThreadSize,
                maxBlockingQueueSize);
    }

    public Integer getChannelProcessorThreadSize() {
        return channelProcessorThreadSize;
    }

    public void setChannelProcessorThreadSize(Integer channelProcessorThreadSize) {
        this.channelProcessorThreadSize = channelProcessorThreadSize;
    }

    public Integer getReceiptProcessorThreadSize() {
        return receiptProcessorThreadSize;
    }

    public void setReceiptProcessorThreadSize(Integer receiptProcessorThreadSize) {
        this.receiptProcessorThreadSize = receiptProcessorThreadSize;
    }

    public Integer getMaxBlockingQueueSize() {
        return maxBlockingQueueSize;
    }

    public void setMaxBlockingQueueSize(Integer maxBlockingQueueSize) {
        this.maxBlockingQueueSize = maxBlockingQueueSize;
    }
}
