package org.fisco.bcos.sdk.v3.test.amop;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.amop.Amop;
import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.utils.ThreadPoolService;
import org.junit.Assert;
import org.junit.Test;

public class AmopTest {
    private static final String configFile =
            AmopTest.class.getClassLoader().getResource(ConstantConfig.CONFIG_FILE_NAME).getPath();
    private static final String GROUP = "group0";

    @Test
    public void amopAsyncSubTest() throws ConfigException, JniException, InterruptedException {
        String publishConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-publisher-for-test.toml")
                        .getPath();
        String subConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-subscriber-for-test.toml")
                        .getPath();
        ConfigOption publishConfig = Config.load(publishConfigFile);
        Amop amopBroadCast = Amop.build(publishConfig);

        String topic = "topic";
        String message = "message";
        ConfigOption subConfig = Config.load(subConfigFile);
        Amop subAmop = Amop.build(subConfig);

        amopBroadCast.start();
        subAmop.start();

        ThreadPoolService threadPoolService = new ThreadPoolService("amop", 1000);

        threadPoolService
                .getThreadPool()
                .execute(
                        () -> {
                            int count = 5;
                            while (count-- > 0) {
                                System.out.println(
                                        " ====== AMOP broadcast, topic: "
                                                + topic
                                                + " ,msg: "
                                                + message);
                                amopBroadCast.broadcastAmopMsg(topic, message.getBytes());
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

        threadPoolService
                .getThreadPool()
                .execute(
                        () -> {
                            int count = 5;
                            while (count-- > 0) {
                                CompletableFuture<Boolean> future = new CompletableFuture<>();
                                subAmop.subscribeTopic(
                                        topic,
                                        (endpoint, seq, data) -> {
                                            System.out.println(" ==> receive message from client");
                                            System.out.println(" \t==> endpoint: " + endpoint);
                                            System.out.println(" \t==> seq: " + seq);
                                            System.out.println(" \t==> data: " + new String(data));
                                            Assert.assertEquals(new String(data), message);
                                            subAmop.sendResponse(endpoint, seq, data);
                                            future.complete(false);
                                        });
                                try {
                                    future.get(10, TimeUnit.SECONDS);
                                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        Thread.sleep(10000);
        threadPoolService.stop();
        amopBroadCast.stop();
        subAmop.stop();
        amopBroadCast.destroy();
        subAmop.destroy();
    }

    @Test
    public void amopSubAsyncTest()
            throws ConfigException, JniException, InterruptedException, ExecutionException, TimeoutException {
        String publishConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-publisher-for-test.toml")
                        .getPath();
        String subConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-subscriber-for-test.toml")
                        .getPath();
        ConfigOption publishConfig = Config.load(publishConfigFile);
        Amop pubAmop = Amop.build(publishConfig);
        String topic = "topic";
        String message = "message";
        String message2 = "message2";
        ConfigOption subConfig = Config.load(subConfigFile);
        Amop subAmop = Amop.build(subConfig);
        pubAmop.start();
        subAmop.start();
        ThreadPoolService threadPoolService = new ThreadPoolService("amop", 1000);
        final int pubTime = 5;
        threadPoolService
                .getThreadPool()
                .execute(
                        () ->
                                subAmop.subscribeTopic(
                                        topic,
                                        (endpoint, seq, data) -> {
                                            Assert.assertEquals(new String(data), message);
                                            System.out.println(
                                                    " ====== AMOP sub, topic: "
                                                            + topic
                                                            + " ,msg: "
                                                            + new String(data));
                                            subAmop.sendResponse(
                                                    endpoint, seq, message2.getBytes());
                                        }));

        Thread.sleep(2000);

        AtomicInteger countResponse = new AtomicInteger(pubTime);
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        pubTopic(
                threadPoolService,
                pubTime,
                topic,
                message,
                pubAmop,
                message2,
                countResponse,
                0,
                future);
        subAmop.stop();
        pubAmop.stop();
        subAmop.destroy();
        pubAmop.destroy();
    }

    @Test
    public void amopSubTest()
            throws ConfigException, JniException, InterruptedException, ExecutionException, TimeoutException {
        String publishConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-publisher-for-test.toml")
                        .getPath();
        String subConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-subscriber-for-test.toml")
                        .getPath();
        ConfigOption publishConfig = Config.load(publishConfigFile);
        Amop pubAmop = Amop.build(publishConfig);
        String topic = "topic";
        Set<String> topicSet = new HashSet<>();
        topicSet.add(topic);
        String message = "message";
        String message2 = "message2";
        ConfigOption subConfig = Config.load(subConfigFile);
        Amop subAmop = Amop.build(subConfig);
        pubAmop.start();
        subAmop.start();
        ThreadPoolService threadPoolService = new ThreadPoolService("amop", 1000);
        final int pubTime = 5;

        subAmop.subscribeTopic(topicSet);
        subAmop.setCallback(
                (endpoint, seq, data) -> {
                    Assert.assertEquals(new String(data), message);
                    System.out.println(
                            " ====== AMOP sub, topic: " + topic + " ,msg: " + new String(data));
                    subAmop.sendResponse(endpoint, seq, message2.getBytes());
                });

        Thread.sleep(2000);

        AtomicInteger countResponse = new AtomicInteger(pubTime);
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        pubTopic(
                threadPoolService,
                pubTime,
                topic,
                message,
                pubAmop,
                message2,
                countResponse,
                1,
                future);
        subAmop.stop();
        pubAmop.stop();
        subAmop.destroy();
        pubAmop.destroy();
    }

    @Test
    public void amopUnsubTest()
            throws ConfigException, JniException, InterruptedException, ExecutionException, TimeoutException {
        String publishConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-publisher-for-test.toml")
                        .getPath();
        String subConfigFile =
                AmopTest.class
                        .getClassLoader()
                        .getResource("amop/config-subscriber-for-test.toml")
                        .getPath();
        ConfigOption publishConfig = Config.load(publishConfigFile);
        Amop pubAmop = Amop.build(publishConfig);
        String topic = "topic";
        Set<String> topicSet = new HashSet<>();
        topicSet.add(topic);
        String message = "message";
        String message2 = "message2";
        ConfigOption subConfig = Config.load(subConfigFile);
        Amop subAmop = Amop.build(subConfig);
        pubAmop.start();
        subAmop.start();
        ThreadPoolService threadPoolService = new ThreadPoolService("amop", 1000);
        final int pubTime = 5;

        subAmop.subscribeTopic(topicSet);
        subAmop.setCallback(
                (endpoint, seq, data) -> {
                    Assert.assertEquals(new String(data), message);
                    System.out.println(
                            " ====== AMOP sub, topic: " + topic + " ,msg: " + new String(data));
                    subAmop.sendResponse(endpoint, seq, message2.getBytes());
                });
        subAmop.getSubTopics();
        subAmop.unsubscribeTopic(topic);

        Thread.sleep(2000);

        AtomicInteger countResponse = new AtomicInteger(pubTime);
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        pubTopic(
                threadPoolService,
                pubTime,
                topic,
                message,
                pubAmop,
                message2,
                countResponse,
                1,
                future);
        subAmop.stop();
        pubAmop.stop();
        subAmop.destroy();
        pubAmop.destroy();
    }

    private void pubTopic(
            ThreadPoolService threadPoolService,
            int pubTime,
            String topic,
            String message,
            Amop pubAmop,
            String message2,
            AtomicInteger countResponse,
            int x,
            CompletableFuture<Boolean> future)
            throws InterruptedException, ExecutionException, TimeoutException {
        threadPoolService
                .getThreadPool()
                .execute(
                        () -> {
                            int count = pubTime;
                            while (count-- > 0) {
                                System.out.println(
                                        " ====== AMOP pub, topic: " + topic + " ,msg: " + message);
                                pubAmop.sendAmopMsg(
                                        topic,
                                        message.getBytes(),
                                        10000,
                                        response -> {
                                            System.out.println(
                                                    " ====== AMOP response, topic: "
                                                            + topic
                                                            + " ,msg: "
                                                            + new String(response.getData()));

                                            Assert.assertEquals(
                                                    new String(response.getData()), message2);
                                            if (countResponse.decrementAndGet() == x) {
                                                future.complete(true);
                                            }
                                        });
                            }
                        });
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
