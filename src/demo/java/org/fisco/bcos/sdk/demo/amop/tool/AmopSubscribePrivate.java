package org.fisco.bcos.sdk.demo.amop.tool;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.crypto.keystore.P12Manager;

public class AmopSubscribePrivate {
    private static String subscriberConfigFile =
            AmopSubscribe.class.getClassLoader().getResource("config-subscriber.toml").getPath();

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Param: topic");
            return;
        }
        String topic = args[0];
        String privateKeyFile = args[1];
        String password = args[2];
        BcosSDK sdk = new BcosSDK(subscriberConfigFile);
        Amop amop = sdk.getAmop();
        AmopCallback cb =
                new AmopCallback() {
                    @Override
                    public void receiveAmopMsg(AmopMsgIn msg) {
                        byte[] responseData = "Yes, I received!".getBytes();
                        System.out.println(
                                "Step 2:Receive msg, topic:"
                                        + msg.getTopic()
                                        + " content:"
                                        + new String(msg.getContent())
                                        + " if not broadcast will response response:"
                                        + new String(responseData));

                        msg.sendResponse(responseData);
                    }
                };
        System.out.println("Start test");
        amop.setCallback(cb);
        KeyManager km = new P12Manager(privateKeyFile, password);
        amop.subscribePrivateTopics(topic, km, cb);
        amop.subscribeTopic(topic, cb);
    }
}
