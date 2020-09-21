package org.fisco.bcos.sdk.amop;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.amop.topic.AmopMsgHandler;
import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
import org.fisco.bcos.sdk.amop.topic.RequestVerifyData;
import org.fisco.bcos.sdk.amop.topic.TopicManager;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.crypto.keystore.P12Manager;
import org.fisco.bcos.sdk.crypto.keystore.PEMManager;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

public class AmopMsgHandlerTest {
    // Sender
    private AmopMsgHandler msgHandlerSender;
    private TopicManager topicManagerSender;
    private MockChannel chSender = new MockChannel();

    // Receiver
    private AmopMsgHandler msgHandlerSub;
    private MockChannel chSub = new MockChannel();

    // Mock
    private MockChannelHandlerContext ctxSender = new MockChannelHandlerContext();
    private MockChannelHandlerContext ctxSub = new MockChannelHandlerContext();

    @Test
    public void testGetVerifyRequest() {
        initEnv();
        String topic = "test";
        byte[] data = "Tell you something.".getBytes();
        topicManagerSender.addTopic(topic, null);
        TestAmopCallback cb1 = new TestAmopCallback(topic, data);

        topicManagerSender.setCallback(cb1);
        msgHandlerSender.onMessage(ctxSender, getAmopMsg(topic, data).getMessage());
        AmopMsgIn in = cb1.getMsg();
        Assert.assertEquals(new String(in.getContent()), new String(data));
    }

    @Test
    public void testRequestVerify() throws JsonProcessingException {
        initEnv();

        // node make request
        RequestVerifyData rvd = new RequestVerifyData();
        rvd.setNodeId("127.0.0.1:8000");
        rvd.setTopic("#!$TopicNeedVerify_priv1");
        rvd.setTopicForCert(
                "#!$VerifyChannel_#!$TopicNeedVerify_priv1_5e14c53197adbcb719d915fb93342c25");
        String jsonStr = ObjectMapperFactory.getObjectMapper().writeValueAsString(rvd);
        Message msg = new Message();
        msg.setType((short) MsgType.REQUEST_TOPICCERT.getType());
        msg.setSeq(Amop.newSeq());
        msg.setResult(0);
        msg.setData(jsonStr.getBytes());

        // sender receive message from node
        msgHandlerSender.onMessage(ctxSender, msg);
        Message outMsg = chSender.getMsg();
        Assert.assertNotNull(outMsg);
        Assert.assertTrue(outMsg.getType() == (short) MsgType.AMOP_REQUEST.getType());
        AmopMsg outAmopMsg = new AmopMsg(outMsg);
        outAmopMsg.decodeAmopBody(outMsg.getData());
        Assert.assertEquals(32, new String(outAmopMsg.getData()).length());
    }

    @Test
    public void testSignRandom() throws JsonProcessingException {
        // init env
        initEnv();

        // sender generate random number
        senderGenRandom();

        // subscriber sign on random number
        byte[] randomValue = suberSignOnRamdom();

        // verify signature
        Message signedRandom = ctxSub.getMsg();
        AmopMsg amopMsg = new AmopMsg(signedRandom);

        amopMsg.decodeAmopBody(signedRandom.getData());
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoType.ECDSA_TYPE);
        Assert.assertTrue(amopMsg.getType() == (short) MsgType.AMOP_RESPONSE.getType());
        Assert.assertEquals(
                "#!$VerifyChannel_#!$TopicNeedVerify_priv1_5e14c53197adbcb719d915fb93342c25",
                amopMsg.getTopic());
        String keyFile =
                AmopMsgHandlerTest.class
                        .getClassLoader()
                        .getResource(
                                "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.public.pem")
                        .getPath();
        KeyManager km = new PEMManager(keyFile);
        Assert.assertTrue(
                cryptoInterface.verify(
                        km,
                        Hex.toHexString(cryptoInterface.hash(randomValue)),
                        Hex.toHexString(amopMsg.getData())));
    }

    @Test
    public void testVerifySign() throws JsonProcessingException {
        // init env
        initEnv();

        // sender generate random number
        senderGenRandom();

        // subscriber sign on random number
        suberSignOnRamdom();

        // response random to sender
        ResponseCallback cb = chSender.getCallback();
        Message signedRandomMsg = ctxSub.getMsg();
        Response response = new Response();
        if (signedRandomMsg.getResult() != 0) {
            response.setErrorMessage("Response error");
        }
        response.setErrorCode(signedRandomMsg.getResult());
        response.setMessageID(signedRandomMsg.getSeq());
        response.setContentBytes(signedRandomMsg.getData());
        response.setCtx(ctxSender);
        cb.onResponse(response);

        // check result
        Message respMsg = ctxSub.getMsg();
        System.out.println(respMsg == null);
        Assert.assertTrue(respMsg.getType() == (short) MsgType.UPDATE_TOPIICSTATUS.getType());
        Assert.assertEquals(0, respMsg.getResult().intValue());
    }

    private byte[] suberSignOnRamdom() {
        Message outMsg = chSender.getMsg();
        msgHandlerSub.onMessage(ctxSub, outMsg);
        AmopMsg amopOutMsg = new AmopMsg(outMsg);
        amopOutMsg.decodeAmopBody(outMsg.getData());
        byte[] randomValue = amopOutMsg.getData();
        return randomValue;
    }

    private void senderGenRandom() throws JsonProcessingException {
        RequestVerifyData rvd = new RequestVerifyData();
        rvd.setNodeId("127.0.0.1:8000");
        rvd.setTopic("#!$TopicNeedVerify_priv1");
        rvd.setTopicForCert(
                "#!$VerifyChannel_#!$TopicNeedVerify_priv1_5e14c53197adbcb719d915fb93342c25");
        String jsonStr = ObjectMapperFactory.getObjectMapper().writeValueAsString(rvd);
        Message msg = new Message();
        msg.setType((short) MsgType.REQUEST_TOPICCERT.getType());
        msg.setSeq(Amop.newSeq());
        msg.setResult(0);
        msg.setData(jsonStr.getBytes());
        msgHandlerSender.onMessage(ctxSub, msg);
    }

    private void initEnv() {
        topicManagerSender = new TopicManager();
        msgHandlerSender = new AmopMsgHandler(chSender, topicManagerSender);
        List<KeyManager> list = new ArrayList<>();
        String keyFile =
                AmopMsgHandlerTest.class
                        .getClassLoader()
                        .getResource(
                                "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.public.pem")
                        .getPath();
        list.add(new PEMManager(keyFile));
        topicManagerSender.addPrivateTopicSend("priv1", list);

        TopicManager topicManagerSub;
        topicManagerSub = new TopicManager();
        msgHandlerSub = new AmopMsgHandler(chSub, topicManagerSub);
        String privKey =
                AmopMsgHandlerTest.class
                        .getClassLoader()
                        .getResource(
                                "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.p12")
                        .getPath();
        topicManagerSub.addPrivateTopicSubscribe("priv1", new P12Manager(privKey, "123456"), null);
    }

    private AmopMsg getAmopMsg(String topic, byte[] content) {
        AmopMsg msg = new AmopMsg();
        msg.setSeq(Amop.newSeq());
        msg.setType((short) MsgType.AMOP_REQUEST.getType());
        msg.setResult(0);
        msg.setTopic(topic);
        msg.setData(content);
        return msg;
    }

    public class TestAmopCallback extends AmopCallback {
        private byte[] content;
        private String topic;
        private AmopMsgIn msg;

        public TestAmopCallback(String topic, byte[] content) {
            this.content = content;
            this.topic = topic;
        }

        @Override
        public byte[] receiveAmopMsg(AmopMsgIn msg) {
            this.msg = msg;
            Assert.assertTrue(msg.getTopic().equals(topic));
            Assert.assertEquals(msg.getContent().length, content.length);
            Assert.assertTrue(new String(msg.getContent()).equals(new String(content)));
            return "I received".getBytes();
        }

        public AmopMsgIn getMsg() {
            return msg;
        }
    }
}
