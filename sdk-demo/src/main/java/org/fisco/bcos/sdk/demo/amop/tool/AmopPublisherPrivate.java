package org.fisco.bcos.sdk.demo.amop.tool;

import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.crypto.keystore.PEMManager;
import org.fisco.bcos.sdk.model.Response;

public class AmopPublisherPrivate {
    private static final int parameterNum = 6;
    private static String publisherFile =
            AmopPublisherPrivate.class
                    .getClassLoader()
                    .getResource("amop/config-publisher-for-test.toml")
                    .getPath();

    /**
     * @param args topicName, pubKey1, pubKey2, isBroadcast: true/false, content, count. if only one
     *     public key please fill pubKey2 with null
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < parameterNum) {
            System.out.println(
                    "param: opicName, pubKey1, pubKey2, isBroadcast: true/false, content, count");
            return;
        }
        String topicName = args[0];
        String pubkey1 = args[1];
        String pubkey2 = args[2];
        Boolean isBroadcast = Boolean.valueOf(args[3]);
        String content = args[4];
        Integer count = Integer.parseInt(args[5]);
        BcosSDK sdk = BcosSDK.build(publisherFile);
        Amop amop = sdk.getAmop();

        System.out.println("3s ...");
        Thread.sleep(1000);
        System.out.println("2s ...");
        Thread.sleep(1000);
        System.out.println("1s ...");
        Thread.sleep(1000);

        System.out.println("start test");
        System.out.println("===================================================================");
        System.out.println("set up private topic");
        List<KeyManager> kml = new ArrayList<>();
        KeyManager km1 = new PEMManager(pubkey1);
        kml.add(km1);
        if (!pubkey2.equals("null")) {
            KeyManager km2 = new PEMManager(pubkey2);
            kml.add(km2);
        }
        amop.publishPrivateTopic(topicName, kml);
        System.out.println("wait until finish private topic verify");
        System.out.println("3s ...");
        Thread.sleep(1000);
        System.out.println("2s ...");
        Thread.sleep(1000);
        System.out.println("1s ...");
        Thread.sleep(1000);

        for (Integer i = 0; i < count; ++i) {
            Thread.sleep(2000);
            AmopMsgOut out = new AmopMsgOut();
            out.setType(TopicType.PRIVATE_TOPIC);
            out.setContent(content.getBytes());
            out.setTimeout(6000);
            out.setTopic(topicName);
            ResponseCallback cb =
                    new ResponseCallback() {
                        @Override
                        public void onResponse(Response response) {

                            System.out.println(
                                    "Step 3:Get response, { errorCode:"
                                            + response.getErrorCode()
                                            + " error:"
                                            + response.getErrorMessage()
                                            + " seq:"
                                            + response.getMessageID()
                                            + " content:"
                                            + new String(response.getContentBytes())
                                            + " }");
                        }
                    };
            if (isBroadcast) {
                amop.broadcastAmopMsg(out);
                System.out.println(
                        "Step 1: Send out msg by broadcast, topic:"
                                + out.getTopic()
                                + " content:"
                                + new String(out.getContent()));
            } else {
                amop.sendAmopMsg(out, cb);
                System.out.println(
                        "Step 1: Send out msg, topic:"
                                + out.getTopic()
                                + " content:"
                                + new String(out.getContent()));
            }
        }
    }
}
