package org.fisco.bcos.sdk.demo.amop.tool;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopSubscribe {
    private static final Logger logger = LoggerFactory.getLogger(AmopSubscribe.class);
    private static String subscriberConfigFile =
            AmopSubscribe.class.getClassLoader().getResource("config-subscriber.toml").getPath();

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Param: topic");
            return;
        }
        String topic = args[0];
        logger.debug("init subscriber");
        BcosSDK sdk = new BcosSDK(subscriberConfigFile);
        Amop amop = sdk.getAmop();
        AmopCallback cb =
                new AmopCallback() {
                    @Override
                    public void onSubscribedTopicMsg(AmopMsgIn msg) {
                        System.out.println(
                                "Receive msg, topic:"
                                        + msg.getTopic()
                                        + " content:"
                                        + new String(msg.getContent()));
                    }
                };
        amop.subscribeTopic(topic, cb);
    }
}
