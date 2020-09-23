package org.fisco.bcos.sdk.demo.amop.tool;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.crypto.keystore.PEMKeyStore;

public class AmopSubscriberPrivate {
    private static String subscriberConfigFile =
            AmopSubscriberPrivate.class
                    .getClassLoader()
                    .getResource("amop/config-subscriber-for-test.toml")
                    .getPath();

    /**
     * @param args topic, privateKeyFile, password(Option)
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Param: topic, privateKeyFile, password");
            return;
        }
        String topic = args[0];
        String privateKeyFile = args[1];
        BcosSDK sdk = BcosSDK.build(subscriberConfigFile);
        Amop amop = sdk.getAmop();
        AmopCallback cb = new DemoAmopCallback();

        System.out.println("Start test");
        amop.setCallback(cb);

        KeyTool km;
        if (privateKeyFile.endsWith("p12")) {
            String password = args[2];
            km = new P12KeyStore(privateKeyFile, password);
        } else {
            km = new PEMKeyStore(privateKeyFile);
        }
        amop.subscribePrivateTopics(topic, km, cb);
        amop.subscribeTopic(topic, cb);
    }
}
