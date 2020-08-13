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
import org.fisco.bcos.sdk.model.LogResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SubscribeTest {
    private static final String configFile = SubscribeTest.class.getClassLoader().getResource("config-example.yaml").getPath();
    private static final Logger logger = LoggerFactory.getLogger(SubscribeTest.class);

    @Test
    public void TestInitEventSubModule() throws ConfigException {
        // Init event subscribe module.
        BcosSDK sdk = new BcosSDK(configFile);
        EventSubscribe eventSubscribe = EventSubscribe.build(sdk.getGroupManagerService(), 1);
        eventSubscribe.start();

        EventLogParams eventLogParams = new EventLogParams();
        eventLogParams.setFromBlock("1");
        eventLogParams.setToBlock("latest");
        eventLogParams.setAddresses(new ArrayList<String>());
        eventLogParams.setTopics(new ArrayList<Object>());

        EventCallback eventCallback = new EventCallback() {
            @Override
            public LogResult decodeLog(EventLog log) {
                return null;
            }

            @Override
            public void onReceiveLog(int status, List<LogResult> logs) {
                String str = "callback in event : ";
                for (LogResult log: logs) {
                    str += log.toString();
                }
                logger.debug(str);
            }
        };
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        eventSubscribe.subscribeEvent(eventLogParams, eventCallback);
        eventSubscribe.stop();
    }
}
