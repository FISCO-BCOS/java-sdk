package org.fisco.bcos.sdk.demo.amop.tool;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.TopicSubscribers;

public class QueryAmopSubscribers {
    private static final int parameterNum = 2;
    private static String publisherFile =
            AmopPublisher.class.getClassLoader().getResource("config-example.toml").getPath();

    /** @param args topic, ipAndPort */
    public static void main(String[] args) {
        if (args.length < parameterNum) {
            System.out.println("Param: topic, ipAndPort");
            return;
        }
        String topic = args[0];
        String ipAndPort = args[1];
        BcosSDK sdk = BcosSDK.build(publisherFile);
        System.out.println("Query topic subscribers. Topic: " + topic + " Peer:" + ipAndPort);
        Client client = sdk.getClient(Integer.valueOf(1));
        TopicSubscribers ts = client.getAmopTopicSubscribers(topic, ipAndPort);
        System.out.println(ts.getTopicSubscribersInfo().toString());
    }
}
