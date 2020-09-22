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
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.abi.tools.TopicTools;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionManager;
import org.fisco.bcos.sdk.transaction.manager.TransactionManagerFactory;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SubscribeTest {
    private static final String configFile = SubscribeTest.class.getClassLoader().getResource(ConstantConfig.CONFIG_FILE_NAME).getPath();
    private static final Logger logger = LoggerFactory.getLogger(SubscribeTest.class);
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    private static final String abi =
            "[{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"_addrDArray\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_addr\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"incrementUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_bytesV\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_s\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"getSArray\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[2]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"bytesArray\",\"type\":\"bytes1[]\"}],\"name\":\"setBytesMapping\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"setBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"a\",\"type\":\"address[]\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"setValues\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes1\"}],\"name\":\"getByBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes1[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_intV\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"emptyArgs\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"s\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogIncrement\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogInit\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"address[]\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogSetValues\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"bytes\"},{\"indexed\":false,\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"LogSetBytes\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"uint256[2]\"},{\"indexed\":false,\"name\":\"n\",\"type\":\"uint256[2]\"}],\"name\":\"LogSetSArray\",\"type\":\"event\"}]";

    @Test
    public void testEventSubModule() {
        // Init event subscribe module.
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        EventSubscribe eventSubscribe = sdk.getEventSubscribe(client.getGroupId());
        eventSubscribe.start();
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

            // call function with event
            List<Object> paramsSetValues = Lists.newArrayList(20);
            String[] o = { "0x1", "0x2", "0x3" };
            List<String> a = Arrays.asList(o);
            paramsSetValues.add(a);
            paramsSetValues.add("set values 字符串");
            TransactionResponse transactionResponse =
                    manager.sendTransactionAndGetResponse(contractAddress, abi, "setValues", paramsSetValues);
            logger.info("transaction response : " + JsonUtils.toJson(transactionResponse));
        } catch (Exception e) {
            logger.error("exception:", e);
        }

        EventLogParams eventLogParams1 = new EventLogParams();
        eventLogParams1.setFromBlock("latest");
        eventLogParams1.setToBlock("latest");
        eventLogParams1.setAddresses(new ArrayList<>());
        ArrayList<Object> topics = new ArrayList<>();
        CryptoInterface invalidCryptoInterface = new CryptoInterface(client.getCryptoInterface().getCryptoTypeConfig());
        TopicTools topicTools = new TopicTools(invalidCryptoInterface);
        topics.add(topicTools.stringToTopic("LogSetValues(int256,address[],string)"));
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
                        logger.debug(" blockNumber:" + log.getBlockNumber() + ",txIndex:" + log.getTransactionIndex() + " data:"
                                + log.getData());
                        ABICodec abiCodec = new ABICodec(client.getCryptoInterface());
                        try {
                            List<Object> list = abiCodec.decodeEvent(abi, "LogSetValues", log.getData());
                            logger.debug("decode event log content, " + list);
                            Assert.assertEquals("20", list.get(0).toString());
                            Assert.assertEquals("set values 字符串", list.get(2).toString());
                        } catch (ABICodecException e) {
                            logger.error("decode event log error, " + e.getMessage());
                        }
                    }
                }
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

        try{
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
