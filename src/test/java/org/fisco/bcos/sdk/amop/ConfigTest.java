package org.fisco.bcos.sdk.amop;

import java.util.Set;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {
    private static final String senderConfig =
            ConfigTest.class.getClassLoader().getResource("config-sender.toml").getPath();
    private static final String subscriberConfig =
            ConfigTest.class.getClassLoader().getResource("config-subscriber.toml").getPath();

    @Test
    public void testConfigSenderAmop() throws ConfigException {
        System.out.println(senderConfig);
        ConfigOption configOption = Config.load(senderConfig, CryptoInterface.ECDSA_TYPE);
        AmopImp amopImp = new AmopImp(new MockGroupManager(new MockChannel()), configOption);

        Set<String> topics = amopImp.getAllTopics();
        for (String topic : topics) {
            if (topic.length() > 29) {
                Assert.assertEquals("#!$PushChannel_#!$TopicNeedVerify_privTopic", topic);
            } else {
                Assert.assertEquals("#!$TopicNeedVerify_privTopic", topic);
            }
        }
    }

    @Test
    public void testConfigSubscriberAmop() throws ConfigException {
        ConfigOption configOption = Config.load(subscriberConfig, CryptoInterface.ECDSA_TYPE);
        AmopImp amopImp = new AmopImp(new MockGroupManager(new MockChannel()), configOption);

        Set<String> topics = amopImp.getAllTopics();
        for (String topic : topics) {
            if (topic.length() > 29) {
                Assert.assertEquals(
                        "#!$VerifyChannel_#!$TopicNeedVerify_privTopic", topic.substring(0, 45));
            } else {
                Assert.assertEquals("#!$TopicNeedVerify_privTopic", topic);
            }
        }
    }
}
