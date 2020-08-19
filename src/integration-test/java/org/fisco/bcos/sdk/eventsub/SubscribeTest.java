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

package org.fisco.bcos.sdk.eventsub;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.config.ConfigException;
import org.fisco.bcos.sdk.model.EventLog;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SubscribeTest {
    private static final String configFile = SubscribeTest.class.getClassLoader().getResource("config-example.yaml").getPath();
    private static final Logger logger = LoggerFactory.getLogger(SubscribeTest.class);

    @Test
    public void TestInitEventSubModule() throws ConfigException {
        // Init event subscribe module.
        BcosSDK sdk = new BcosSDK(configFile);
        EventSubscribe eventSubscribe = EventSubscribe.build(sdk.getGroupManagerService(), 1);
        eventSubscribe.start();

        EventLogParams eventLogParams1 = new EventLogParams();
        eventLogParams1.setFromBlock("1");
        eventLogParams1.setToBlock("latest");
        eventLogParams1.setAddresses(new ArrayList<String>());
        eventLogParams1.setTopics(new ArrayList<Object>());

        class SubscribeCallback extends EventCallback {
            public transient Semaphore semaphore = new Semaphore(1, true);

            SubscribeCallback() {
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    logger.error("error :", e);
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onReceiveLog(int status, List<EventLog> logs) {
                String str = "status in onReceiveLog : " + status;
                logger.debug(str);
                semaphore.release();
            }
        }

        logger.info("subscribe event");
        SubscribeCallback subscribeEventCallback1 = new SubscribeCallback();
        String registerId1 = eventSubscribe.subscribeEvent(eventLogParams1, subscribeEventCallback1);
        try {
            subscribeEventCallback1.semaphore.acquire(1);
            logger.info("subscribe successful, registerId is " + registerId1);
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }

        logger.info("unregister event");
        EventCallback callback = new EventCallback() {
            @Override
            public void onReceiveLog(int status, List<EventLog> logs) {
                Assert.assertEquals(status, 0);
            }
        };
        eventSubscribe.unsubscribeEvent(registerId1, callback);

        eventSubscribe.stop();
        sdk.getChannel().stop();
    }
}
