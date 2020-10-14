package org.fisco.bcos.sdk.demo.amop.tool;

import java.net.URL;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopCallback;

public class AmopSubscriber {

    public static void main(String[] args) throws Exception {
        URL configUrl =
                AmopSubscriber.class
                        .getClassLoader()
                        .getResource("amop/config-subscriber-for-test.toml");
        if (args.length < 1) {
            System.out.println("Param: topic");
            return;
        }
        String topic = args[0];
        BcosSDK sdk = BcosSDK.build(configUrl.getPath());
        Amop amop = sdk.getAmop();
        AmopCallback cb = new DemoAmopCallback();
        System.out.println("Start test");
        amop.subscribeTopic(topic, cb);
    }
}
