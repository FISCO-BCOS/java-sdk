package org.fisco.bcos.sdk.amop;

import java.util.concurrent.Semaphore;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateTopicVerifyTest {
    private static Logger logger = LoggerFactory.getLogger(PrivateTopicVerifyTest.class);
    private static final String senderConfig =
            PrivateTopicVerifyTest.class
                    .getClassLoader()
                    .getResource("amop/config-publisher-for-test.toml")
                    .getPath();
    private static final String subscriberConfig =
            PrivateTopicVerifyTest.class
                    .getClassLoader()
                    .getResource("amop/config-subscriber-for-test.toml")
                    .getPath();
    private Amop sender;
    private Amop subscriber;

    @Test
    public void testSendMsg() throws InterruptedException {
        prepareEnv();
        Thread.sleep(3000);
        AmopMsgOut out = new AmopMsgOut();
        out.setTimeout(2000);
        out.setTopic("test");
        out.setType(TopicType.NORMAL_TOPIC);
        out.setContent("Tell you th.".getBytes());
        class TestResponseCb extends AmopResponseCallback {
            public transient Semaphore semaphore = new Semaphore(1, true);

            public TestResponseCb() {
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onResponse(AmopResponse response) {
                semaphore.release();
                if (response.getAmopMsgIn() != null) {
                    Assert.assertEquals(
                            "Yes, I received.", new String(response.getAmopMsgIn().getContent()));
                }
            }
        }
        TestResponseCb cb = new TestResponseCb();
        sender.sendAmopMsg(out, cb);
        try {
            cb.semaphore.acquire(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void addPrivateTopic() throws InterruptedException {
        prepareEnv();
        logger.trace("Start private topic test");
        Thread.sleep(4000);
        AmopMsgOut out = new AmopMsgOut();
        out.setTimeout(10000);
        out.setContent("send private msg".getBytes());
        out.setType(TopicType.PRIVATE_TOPIC);
        out.setTopic("privTopic");
        final String[] content = new String[1];

        class TestResponseCb extends AmopResponseCallback {
            public transient Semaphore semaphore = new Semaphore(1, true);

            public TestResponseCb() {
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onResponse(AmopResponse response) {
                logger.trace(
                        "Receive response, seq:{} error:{} error msg: {} content:{}",
                        response.getMessageID(),
                        response.getErrorCode(),
                        response.getErrorMessage());
                if (response.getAmopMsgIn() != null) {
                    content[0] = new String(response.getAmopMsgIn().getContent());
                }
                semaphore.release();
            }
        }
        TestResponseCb cb = new TestResponseCb();
        sender.sendAmopMsg(out, cb);
        try {
            cb.semaphore.acquire(1);
            if (content[0].length() > 29) {
                Assert.assertEquals("Yes, I received.", content[0].substring(29));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void prepareEnv() throws InterruptedException {
        BcosSDK sdk2 = BcosSDK.build(subscriberConfig);
        Assert.assertTrue(sdk2.getChannel().getAvailablePeer().size() >= 1);

        BcosSDK sdk1 = BcosSDK.build(senderConfig);
        Assert.assertTrue(sdk1.getChannel().getAvailablePeer().size() >= 1);
        Thread.sleep(2000);
        sender = sdk1.getAmop();
        subscriber = sdk2.getAmop();
        TestAmopCallback defaultCb =
                new TestAmopCallback("#!$TopicNeedVerify_privTopic", "send private msg");
        subscriber.setCallback(defaultCb);
        subscriber.subscribeTopic("test", new TestAmopCallback("test", "Tell you th."));
    }

    public class TestAmopCallback extends AmopCallback {
        private String topic;
        private String content;

        public TestAmopCallback(String topic, String content) {
            this.topic = topic;
            this.content = content;
        }

        @Override
        public byte[] receiveAmopMsg(AmopMsgIn msg) {
            // Assert.assertEquals(topic,msg.getTopic());
            // Assert.assertEquals(content,new String(msg.getContent()));
            System.out.println("on subscribed topic msg");

            return "Yes, I received.".getBytes();
        }
    }
}
