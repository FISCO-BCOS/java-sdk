package org.fisco.bcos.sdk.v3.test.amop;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

        final int pubCount = 5;
        CountDownLatch receiveLatch = new CountDownLatch(pubCount);
        AtomicReference<String> recvError = new AtomicReference<>();

        // subscribe once; the callback stays registered for the whole test
        subAmop.subscribeTopic(
                topic,
                (endpoint, seq, data) -> {
                    System.out.println(" ==> receive message from client");
                    System.out.println(" \t==> endpoint: " + endpoint);
                    System.out.println(" \t==> seq: " + seq);
                    System.out.println(" \t==> data: " + new String(data));
                    if (!message.equals(new String(data))) {
                        recvError.compareAndSet(
                                null, "unexpected message: " + new String(data));
                    }
                    subAmop.sendResponse(endpoint, seq, data);
                    receiveLatch.countDown();
                });

        // the subscription is pushed to the node asynchronously; broadcasts sent
        // before it takes effect are dropped silently, so wait for it first
        Thread.sleep(3000);

        for (int i = 0; i < pubCount; i++) {
            System.out.println(" ====== AMOP broadcast, topic: " + topic + " ,msg: " + message);
            amopBroadCast.broadcastAmopMsg(topic, message.getBytes());
            Thread.sleep(1000);
        }

        // fail the test if not all broadcasts are received, instead of swallowing
        // a TimeoutException
        boolean allReceived = receiveLatch.await(15, TimeUnit.SECONDS);
        amopBroadCast.stop();
        subAmop.stop();
        amopBroadCast.destroy();
        subAmop.destroy();
        Assert.assertNull(recvError.get(), recvError.get());
        Assert.assertTrue(
                "only received " + (pubCount - receiveLatch.getCount()) + "/" + pubCount
                        + " broadcast messages",
                allReceived);
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

        Thread.sleep(1000);

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

        Thread.sleep(1000);

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

        Thread.sleep(1000);

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
