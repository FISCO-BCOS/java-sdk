package org.fisco.bcos.sdk.demo.amop.tool;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.model.Response;

public class AmopPublisherPrivate {

    private static final int parameterNum = 5;
    private static String publisherFile =
            AmopSubscribe.class.getClassLoader().getResource("config-sender.toml").getPath();

    public static void main(String[] args) throws Exception {
        if (args.length < parameterNum) {
            System.out.println("param: target topic total number of request");
            return;
        }
        String topicName = args[0];
        String privateKeyList = args[1];
        Boolean isBroadcast = Boolean.valueOf(args[2]);
        String content = args[3];
        Integer count = Integer.parseInt(args[4]);
        BcosSDK sdk = new BcosSDK(publisherFile);
        Amop amop = sdk.getAmop();
        // todo setup topic

        System.out.println("3s ...");
        Thread.sleep(1000);
        System.out.println("2s ...");
        Thread.sleep(1000);
        System.out.println("1s ...");
        Thread.sleep(1000);

        System.out.println("start test");
        System.out.println("===================================================================");

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
                        "Send out msg, topic:" + out.getTopic() + " content:" + out.getContent());
            }
        }
    }
}
