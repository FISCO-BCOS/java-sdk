package org.fisco.bcos.sdk.demo.amop.tool;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.NodeInfo;
import org.fisco.bcos.sdk.client.protocol.response.Peers;

public class QueryAmopSubscribers {
    private static final int parameterNum = 1;
    private static String publisherFile =
            AmopPublisher.class.getClassLoader().getResource("config-example.toml").getPath();

    /** @param args ipAndPort */
    public static void main(String[] args) {
        if (args.length < parameterNum) {
            System.out.println("Param: ipAndPort");
            return;
        }
        String ipAndPort = args[0];
        BcosSDK sdk = BcosSDK.build(publisherFile);
        System.out.println("Query topic subscribers. Peer:" + ipAndPort);
        Client client = sdk.getClient(Integer.valueOf(1));
        NodeInfo info = client.getNodeInfo(ipAndPort);
        System.out.println(info.getNodeInfo().toString());
        Peers peers = client.getPeers(ipAndPort);
        System.out.println(peers.getPeers().toString());
    }
}
