package org.fisco.bcos.sdk.amop;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.model.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Semaphore;

public class PrivateTopicVerifyTest {
    private static final String senderConfig = PrivateTopicVerifyTest.class.getClassLoader().getResource("config-sender.toml").getPath();
    private static final String subscriberConfig = PrivateTopicVerifyTest.class.getClassLoader().getResource("config-subscriber.toml").getPath();
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
        class TestResponseCb extends  ResponseCallback{
            public transient Semaphore semaphore = new Semaphore(1, true);
            public TestResponseCb(){
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            @Override
            public void onResponse(Response response) {
                semaphore.release();
                Assert.assertEquals("Yes, I received.",response.getContent().substring(5));
            }
        }
        TestResponseCb cb = new TestResponseCb();
        sender.sendAmopMsg(out,cb);
        try {
            cb.semaphore.acquire(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void addPrivateTopic() throws InterruptedException {
        prepareEnv();
        System.out.println("Start test");
        Thread.sleep(3000);
        AmopMsgOut out = new AmopMsgOut();
        out.setTimeout(10000);
        out.setContent("send private msg".getBytes());
        out.setType(TopicType.PRIVATE_TOPIC);
        out.setTopic("privTopic");

        class TestResponseCb extends  ResponseCallback{
            public transient Semaphore semaphore = new Semaphore(1, true);
            public TestResponseCb(){
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            @Override
            public void onResponse(Response response) {
                System.out.println("Response content:"+response.getContent());
                System.out.println("Response error code:"+response.getErrorCode());
                System.out.println("Response error:"+response.getErrorMessage());
                System.out.println("Response seq:"+response.getMessageID());
                semaphore.release();
                Assert.assertEquals("Yes, I received.",response.getContent().substring(29));
            }
        }
        TestResponseCb cb = new TestResponseCb();
        sender.sendAmopMsg(out,cb);
        try {
            cb.semaphore.acquire(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void prepareEnv() throws InterruptedException {
        BcosSDK sdk2 = new BcosSDK(subscriberConfig);
        Assert.assertTrue(sdk2.getChannel().getAvailablePeer().size() >= 1);
        Thread.sleep(2000);

        BcosSDK sdk1 = new BcosSDK(senderConfig);
        Assert.assertTrue(sdk1.getChannel().getAvailablePeer().size() >= 1);

        sender = sdk1.getAmop();
        subscriber = sdk2.getAmop();
        TestAmopCallback defaultCb = new TestAmopCallback("#!$TopicNeedVerify_privTopic","send private msg");
        subscriber.setCallback(defaultCb);
        subscriber.subscribeTopic("test",new TestAmopCallback("test","Tell you th."));
    }

    public class TestAmopCallback extends AmopCallback{
        private String topic;
        private String content;
        public TestAmopCallback(String topic, String content) {
            this.topic=topic;
            this.content = content;
        }

        @Override
        public void onSubscribedTopicMsg(AmopMsgIn msg) {
            //Assert.assertEquals(topic,msg.getTopic());
            //Assert.assertEquals(content,new String(msg.getContent()));
            System.out.println("on subscribed topic msg");
            msg.sendResponse("Yes, I received.".getBytes());
        }
    }
}
