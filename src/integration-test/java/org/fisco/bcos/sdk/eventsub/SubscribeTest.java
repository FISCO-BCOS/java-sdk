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

import com.google.common.collect.Lists;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.abi.tools.TopicTools;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionManager;
import org.fisco.bcos.sdk.transaction.manager.TransactionManagerFactory;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SubscribeTest {
    private static final String configFile = SubscribeTest.class.getClassLoader().getResource(ConstantConfig.CONFIG_FILE_NAME).getPath();
    private static final Logger logger = LoggerFactory.getLogger(SubscribeTest.class);

    @Test
    public void TestEventSubModule() throws ConfigException {
        // Init event subscribe module.
        BcosSDK sdk = new BcosSDK(configFile);
        EventSubscribe eventSubscribe = EventSubscribe.build(sdk.getGroupManagerService(), 1);
        eventSubscribe.start();

        String abiFile = "src/integration-test/resources/abi/";
        String binFile = "src/integration-test/resources/bin/";
        Client client = sdk.getClient(Integer.valueOf(1));
        String contractAddress = "";
        try {
            AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                    client.getCryptoInterface(), abiFile, binFile);
            // deploy
            List<Object> params = Lists.newArrayList();
            params.add(1);
            params.add("test");
            TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
            if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
                return;
            }
            contractAddress = response.getContractAddress();
        } catch (Exception e) {
            logger.error("exception:", e);
        }


        EventLogParams eventLogParams1 = new EventLogParams();
        eventLogParams1.setFromBlock("1");
        eventLogParams1.setToBlock("latest");
        ArrayList<String> addresses = new ArrayList<>();
        addresses.add(contractAddress);
        eventLogParams1.setAddresses(addresses);
        ArrayList<Object> topics = new ArrayList<>();
        CryptoInterface invalidCryptoInterface = new CryptoInterface(client.getCryptoInterface().getCryptoTypeConfig());
        TopicTools topicTools = new TopicTools(invalidCryptoInterface);
        topics.add(topicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        eventLogParams1.setTopics(topics);

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
                Assert.assertEquals(status, 0);
                String str = "status in onReceiveLog : " + status;
                logger.debug(str);
                semaphore.release();
            }
        }

        logger.info(" start to subscribe event");
        SubscribeCallback subscribeEventCallback1 = new SubscribeCallback();
        String registerId1 = eventSubscribe.subscribeEvent(eventLogParams1, subscribeEventCallback1);
        try {
            subscribeEventCallback1.semaphore.acquire(1);
            subscribeEventCallback1.semaphore.release();
            logger.info("subscribe successful, registerId is " + registerId1);
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }

        // FISCO BCOS node v2.7.0
        /*try{
            Thread.sleep(3000);
        } catch (Exception e) {
            logger.error("exception:", e);
        }

        logger.info(" start to unregister event");
        SubscribeCallback subscribeEventCallback2 = new SubscribeCallback();
        eventSubscribe.unsubscribeEvent(registerId1, subscribeEventCallback2);
        try {
            subscribeEventCallback2.semaphore.acquire(1);
            subscribeEventCallback2.semaphore.release();
            logger.info("unregister event successful");
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }*/

        eventSubscribe.stop();
        sdk.getChannel().stop();
    }
}
