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

package org.fisco.bcos.sdk.v3.config.model;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Tread pool configuration */
public class ThreadPoolConfig {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfig.class);

    private int threadPoolSize;

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public ThreadPoolConfig() {}

    public ThreadPoolConfig(ConfigProperty configProperty) {
        Map<String, Object> threadPoolConfig = configProperty.getThreadPool();
        String cpuNum = String.valueOf(Runtime.getRuntime().availableProcessors());
        String value = ConfigProperty.getValue(threadPoolConfig, "threadPoolSize", cpuNum);

        this.threadPoolSize = Integer.valueOf(value);
        logger.debug("Init ThreadPoolConfig, threadPoolSize: {}", this.threadPoolSize);
    }
}
