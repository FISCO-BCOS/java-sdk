package org.fisco.bcos.sdk.demo.amop.tool;

import static org.fisco.bcos.sdk.demo.amop.tool.FileToByteArrayHelper.byteCat;
import static org.fisco.bcos.sdk.demo.amop.tool.FileToByteArrayHelper.getFileByteArray;
import static org.fisco.bcos.sdk.demo.amop.tool.FileToByteArrayHelper.intToByteArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.model.Response;

public class AmopPublisherPrivateFile {
    private static final int parameterNum = 6;
    private static String publisherFile =
            AmopPublisherPrivateFile.class
                    .getClassLoader()
                    .getResource("amop/config-publisher-for-test.toml")
                    .getPath();

    /**
     * @param args topicName, pubKey1, pubKey2, isBroadcast: true/false, fileName, count. if only
     *     one public key please fill pubKey2 with null
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < parameterNum) {
            System.out.println("param: target topic total number of request");
            return;
        }
        String topicName = args[0];
        String pubkey1 = args[1];
        String pubkey2 = args[2];
        Boolean isBroadcast = Boolean.valueOf(args[3]);
        String fileName = args[4];
        Integer count = Integer.parseInt(args[5]);
        BcosSDK sdk = BcosSDK.build(publisherFile);
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
        System.out.println("set up private topic");
        List<KeyManager> kml = new ArrayList<>();
        KeyManager km1 = new PEMKeyStore(pubkey1);
        kml.add(km1);
        if (!pubkey2.equals("null")) {
            KeyManager km2 = new PEMKeyStore(pubkey2);
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

        int flag = -128;
        byte[] byteflag = intToByteArray(flag);
        int filelength = fileName.length();
        byte[] bytelength = intToByteArray(filelength);
        byte[] bytefilename = fileName.getBytes();
        byte[] contentfile = getFileByteArray(new File(fileName));
        byte[] content = byteCat(byteCat(byteCat(byteflag, bytelength), bytefilename), contentfile);

        for (Integer i = 0; i < count; ++i) {
            Thread.sleep(2000);
            AmopMsgOut out = new AmopMsgOut();
            out.setType(TopicType.PRIVATE_TOPIC);
            out.setContent(content);
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
            } else {
                amop.sendAmopMsg(out, cb);
            }
            System.out.println(
                    "Step 1: Send out msg, topic:" + out.getTopic() + " content: file " + fileName);
        }
    }
}
