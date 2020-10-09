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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.abi.tools.TopicTools;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscribeTest {
    private static final String configFile =
            SubscribeTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    private static final Logger logger = LoggerFactory.getLogger(SubscribeTest.class);
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    private static final String abi =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"u1\",\"type\":\"uint256[2]\"},{\"name\":\"u2\",\"type\":\"uint256[]\"},{\"name\":\"b\",\"type\":\"bytes\"},{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"call\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"u\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"uint256\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"add\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd1\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd2\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"u\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"a\",\"type\":\"uint256\"},{\"indexed\":true,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogAdd3\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd4\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogAdd5\",\"type\":\"event\"}]";

    @Test
    public void testEventSubModule() {
        // Init event subscribe module.
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        EventSubscribe eventSubscribe = sdk.getEventSubscribe(client.getGroupId());
        eventSubscribe.start();
        String contractAddress = "";
        try {
            AssembleTransactionProcessor manager =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, client.getCryptoSuite().createKeyPair(), abiFile, binFile);
            // deploy
            TransactionResponse response =
                    manager.deployByContractLoader("Add", Lists.newArrayList());
            if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
                return;
            }
            contractAddress = response.getContractAddress();

            // call function with event
            List<Object> paramsSetValues = Lists.newArrayList(20);
            paramsSetValues.add("AAA");
            TransactionResponse transactionResponse =
                    manager.sendTransactionAndGetResponse(
                            contractAddress, abi, "add", paramsSetValues);
            logger.info("transaction response : " + JsonUtils.toJson(transactionResponse));
        } catch (Exception e) {
            logger.error("exception:", e);
        }

        EventLogParams eventLogParams1 = new EventLogParams();
        eventLogParams1.setFromBlock("latest");
        eventLogParams1.setToBlock("latest");
        ArrayList<String> addresses = new ArrayList<>();
        addresses.add(contractAddress);
        eventLogParams1.setAddresses(addresses);
        ArrayList<Object> topics = new ArrayList<>();
        CryptoSuite invalidCryptoSuite =
                new CryptoSuite(client.getCryptoSuite().getCryptoTypeConfig());
        TopicTools topicTools = new TopicTools(invalidCryptoSuite);
        ArrayList<Object> topicsAt0 = new ArrayList<>();
        topicsAt0.add(topicTools.stringToTopic("LogAdd3(uint256,uint256,string)"));
        topics.add(topicsAt0);
        eventLogParams1.setTopics(topics);

        class SubscribeCallback implements EventCallback {
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
                if (logs != null) {
                    for (EventLog log : logs) {
                        logger.debug(
                                " blockNumber:"
                                        + log.getBlockNumber()
                                        + ",txIndex:"
                                        + log.getTransactionIndex()
                                        + " data:"
                                        + log.getData());
                        ABICodec abiCodec = new ABICodec(client.getCryptoSuite());
                        try {
                            List<Object> list = abiCodec.decodeEvent(abi, "LogAdd3", log);
                            logger.debug("decode event log content, " + list);
                            Assert.assertEquals(3, list.size());
                        } catch (ABICodecException e) {
                            logger.error("decode event log error, " + e.getMessage());
                        }
                    }
                }
            }
        }

        logger.info(" start to subscribe event");
        SubscribeCallback subscribeEventCallback1 = new SubscribeCallback();
        String registerId1 =
                eventSubscribe.subscribeEvent(eventLogParams1, subscribeEventCallback1);
        try {
            subscribeEventCallback1.semaphore.acquire(1);
            subscribeEventCallback1.semaphore.release();
            logger.info("subscribe successful, registerId is " + registerId1);
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            logger.error("exception:", e);
        }

        // FISCO BCOS node v2.7.0
        /*logger.info(" start to unregister event");
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
