/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.amop.topic;

import static org.fisco.bcos.sdk.amop.topic.TopicManager.verifyChannelPrefix;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmopMsgHandler implements MsgHandler {
    private static Logger logger = LoggerFactory.getLogger(AmopMsgHandler.class);
    private TopicManager topicManager;
    private Channel channel;
    private long defaultTimeout = 5000;
    private Map<String, ResponseCallback> seq2Callback = new ConcurrentHashMap<>();
    private boolean isRunning = false;

    public AmopMsgHandler(Channel channel, TopicManager topicManager) {
        this.topicManager = topicManager;
        this.channel = channel;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        if (!isRunning) {
            logger.warn("Amop on connect, amop is not running, exit.");
            return;
        }

        String host = ((SocketChannel) ctx.channel()).remoteAddress().getAddress().getHostAddress();
        Integer port = ((SocketChannel) ctx.channel()).remoteAddress().getPort();
        String ipAndPort = host + ":" + port;
        logger.info("Node connected, update topics to node. node:" + ipAndPort);
        try {
            Set<String> topics = topicManager.getSubByPeer(ipAndPort);
            byte[] topicBytes =
                    ObjectMapperFactory.getObjectMapper().writeValueAsBytes(topics.toArray());
            Message msg = new Message();
            msg.setType((short) MsgType.AMOP_CLIENT_TOPICS.getType());
            msg.setResult(0);
            msg.setSeq(newSeq());
            msg.setData(topicBytes);
            ctx.writeAndFlush(msg);
        } catch (JsonProcessingException e) {
            logger.warn("Amop on connect, subscribe error: {}", e.getMessage());
        }
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {
        logger.trace(
                "receive msg, msg type:{}, content:{}", msg.getType(), new String(msg.getData()));
        if (!isRunning) {
            logger.warn("Amop on msg, amop is not running, exit.");
        }

        if (msg.getType() == (short) MsgType.AMOP_RESPONSE.getType()) {
            // Receive a signed Amop message for authorization.
            onAmopResponse(ctx, msg);
            return;
        }

        if (msg.getType() == (short) MsgType.REQUEST_TOPICCERT.getType()) {
            // As amop private topic message sender
            onVerifyRequest(ctx, msg);
        } else if (msg.getType() == (short) MsgType.AMOP_REQUEST.getType()
                || msg.getType() == (short) MsgType.AMOP_MULBROADCAST.getType()) {
            AmopMsg amopMsg = new AmopMsg(msg);
            try {
                amopMsg.decodeAmopBody(msg.getData());
            } catch (Exception e) {
                logger.error(
                        "Receive an invalid message, msg type:{}, seq:{}",
                        msg.getType(),
                        msg.getSeq());
                return;
            }
            if (isVerifyingPrivateTopic(amopMsg)) {
                // Receive a private topic authorization message.
                onPrivateTopicRandomValue(ctx, amopMsg);
            } else {
                // Receive an Amop message.
                onAmopMsg(ctx, amopMsg);
            }
        } else {
            logger.error(
                    "amop module receive a not supported type message, type:{}", msg.getType());
        }
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {}

    public void onVerifyRequest(ChannelHandlerContext ctx, Message msg) {
        logger.trace(
                "private topic verify step 1: node request random number. seq:{} type:{}, content:{}",
                msg.getSeq(),
                msg.getType(),
                new String(msg.getData()));

        // Response to node at the first time.
        responseVerifyRequest(ctx, msg);

        // Start a verify procedure
        // Read message data to RequestVerifyData
        String content = new String(msg.getData());
        RequestVerifyData data;
        try {
            data =
                    ObjectMapperFactory.getObjectMapper()
                            .readValue(content, RequestVerifyData.class);
        } catch (JsonProcessingException e) {
            logger.error(
                    "receive request start private topic verify message, message is invalid, seq:{} msgtype:{}",
                    msg.getSeq(),
                    msg.getType());
            return;
        }
        String topic = data.getTopic();
        String nodeId = data.getNodeId();

        logger.trace(
                "private topic verify step 1: node request random number. seq:{} topic:{} nodeId:{}",
                msg.getSeq(),
                topic,
                nodeId);

        // Reply random value to node
        String rmdString = UUID.randomUUID().toString().replaceAll("-", "");
        AmopMsg respMsg = new AmopMsg();
        respMsg.setType((short) MsgType.AMOP_REQUEST.getType());
        respMsg.setSeq(Amop.newSeq());
        respMsg.setResult(0);
        respMsg.setData(rmdString.getBytes());
        respMsg.setTopic(data.getTopicForCert());
        // send message out
        Options opt = new Options();
        opt.setTimeout(defaultTimeout);
        ResponseCallback callback =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        if (0 != response.getErrorCode()) {
                            logger.error(
                                    "get random value signature of amop private topic failed :{}:{}",
                                    response.getErrorCode(),
                                    response.getErrorMessage());
                            return;
                        }
                        AmopMsg amopMsg = new AmopMsg();
                        amopMsg.decodeAmopBody(response.getContentBytes());
                        int valid = checkSignature(topic, rmdString.getBytes(), amopMsg.getData());
                        try {
                            sendUpdateTopicStatus(valid, topic, nodeId, ctx);
                        } catch (JsonProcessingException e) {
                            logger.error("update topic status error: {}", e.getMessage());
                        }
                    }
                };
        logger.trace(
                "private topic verify step 2: send out random number. seq:{} topic:{} data:{}",
                respMsg.getSeq(),
                respMsg.getTopic(),
                new String(respMsg.getData()));
        channel.asyncSendToRandom(respMsg.getMessage(), callback, opt);
    }

    public void responseVerifyRequest(ChannelHandlerContext ctx, Message msg) {
        Message response = new Message();
        response.setSeq(msg.getSeq());
        response.setResult(0);
        response.setType((short) MsgType.REQUEST_TOPICCERT.getType());
        response.setData("".getBytes());
        ctx.writeAndFlush(response);
    }

    public int checkSignature(String topic, byte[] randomValue, byte[] signature) {
        List<KeyTool> pubKeys = topicManager.getPublicKeysByTopic(topic);
        Iterator<KeyTool> pks = pubKeys.iterator();
        while (pks.hasNext()) {
            KeyTool keyTool = pks.next();
            CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
            if (cryptoSuite.verify(
                    keyTool,
                    Hex.toHexString(cryptoSuite.hash(randomValue)),
                    Hex.toHexString(signature))) {
                return 0;
            }
        }
        return 1;
    }

    private boolean isVerifyingPrivateTopic(AmopMsg amopMsg) {
        return amopMsg.getTopic().length() > verifyChannelPrefix.length()
                && verifyChannelPrefix.equals(
                        amopMsg.getTopic().substring(0, verifyChannelPrefix.length()));
    }

    private String getSimpleTopic(String fullTopic) {
        return fullTopic.substring(verifyChannelPrefix.length(), fullTopic.length() - 33);
    }

    public void onPrivateTopicRandomValue(ChannelHandlerContext ctx, AmopMsg msg) {
        logger.trace(
                "private topic verify step 2: receive random value, seq:{} type:{} topic:{} data:{}",
                msg.getSeq(),
                msg.getType(),
                msg.getTopic(),
                new String(msg.getData()));
        byte[] randValue = msg.getData();
        String topic = msg.getTopic();
        KeyTool keyTool = topicManager.getPrivateKeyByTopic(getSimpleTopic(topic));
        String signature = "";
        if (null == keyTool) {
            logger.error("topic:{} not subscribed, reject message", getSimpleTopic(topic));
            return;
        } else {
            CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
            try {
                signature = cryptoSuite.sign(keyTool, Hex.toHexString(cryptoSuite.hash(randValue)));
            } catch (Exception e) {
                logger.error(
                        "please check the public key of topic {} is correct configured, error {}",
                        topic,
                        e.getMessage());
            }
        }
        sendSignedRandomNumber(signature, topic, msg.getSeq(), ctx);
    }

    public void onAmopMsg(ChannelHandlerContext ctx, AmopMsg amopMsg) {
        logger.debug(
                "receive a Amop message. seq:{} msgtype:{}", amopMsg.getSeq(), amopMsg.getType());
        if (!topicManager.isSubTopic(amopMsg.getTopic())) {
            logger.warn(
                    "receive an amop msg which is not subscribed, topic:{}", amopMsg.getTopic());
            return;
        }
        AmopCallback callback = topicManager.getCallback(amopMsg.getTopic());
        if (callback == null) {
            logger.error(
                    "can not process Amop message, callback for topic {} is not found",
                    amopMsg.getTopic());
            return;
        }
        AmopMsgIn msgIn = new AmopMsgIn();
        msgIn.setTopic(amopMsg.getTopic());
        msgIn.setMessageID(amopMsg.getSeq());
        msgIn.setContent(amopMsg.getData());
        msgIn.setResult(amopMsg.getResult());
        msgIn.setCtx(ctx);
        msgIn.setType(amopMsg.getType());
        byte[] content = callback.receiveAmopMsg(msgIn);

        // Response the amop msg
        if (amopMsg.getType() == (short) MsgType.AMOP_MULBROADCAST.getType()) {
            // If received a broadcast msg, do not response.
            return;
        }
        amopMsg.setResult(0);
        amopMsg.setType((short) MsgType.AMOP_RESPONSE.getType());
        amopMsg.setData(content);
        logger.trace(
                "Send response, seq:{} topic:{} content:{}",
                amopMsg.getSeq(),
                amopMsg.getTopic(),
                new String(content));
        ctx.writeAndFlush(amopMsg.getMessage());
    }

    public void onAmopResponse(ChannelHandlerContext ctx, Message msg) {
        logger.debug("receive amop response. seq:{} msgtype:{} ", msg.getSeq(), msg.getType());
        ResponseCallback callback = seq2Callback.get(msg.getSeq());
        if (null != callback) {
            Response resp = new Response();
            resp.setMessageID(msg.getSeq());
            resp.setErrorCode(msg.getResult());
            if (msg.getResult() != 0) {
                resp.setErrorMessage("response errors");
            }
            // 103: the AMOP_requests or the AMOP_multicast_requests have been rejected due to
            // over bandwidth limit
            if (msg.getResult()
                    == AmopRespError.REJECT_AMOP_REQ_FOR_OVER_BANDWIDTHLIMIT.getError()) {
                logger.error(
                        "AMOP request was rejected due to over bandwidth limit, message: {}",
                        msg.getSeq());
                resp.setErrorMessage("AMOP request was rejected due to over bandwidth limit");
            }

            if (msg.getResult() == AmopRespError.NO_AVAILABLE_SESSION.getError()) {
                logger.error(
                        "AMOP request was rejected due to over bandwidth limit, message: {}",
                        msg.getSeq());
                resp.setErrorMessage("AMOP request was rejected due to over bandwidth limit");
            }

            if (msg.getData() != null) {
                AmopMsg amopMsg = new AmopMsg();
                amopMsg.decodeAmopBody(msg.getData());
                resp.setContent(new String(amopMsg.getData()));
            }
            callback.onResponse(resp);
        } else {
            logger.error("can not found response callback, timeout:{}", msg.getData());
            return;
        }
    }

    private void sendSignedRandomNumber(
            String signature, String topic, String seq, ChannelHandlerContext ctx) {
        AmopMsg msg = new AmopMsg();
        msg.setTopic(topic);
        msg.setResult(0);
        msg.setSeq(seq);
        msg.setType((short) MsgType.AMOP_RESPONSE.getType());
        msg.setData(Hex.decode(signature));
        logger.trace(
                "private topic verify step 3: sign on random value and send out, seq:{} type:{} topic:{} data:{}",
                msg.getSeq(),
                msg.getType(),
                msg.getTopic(),
                new String(msg.getData()));
        ctx.writeAndFlush(msg.getMessage());
    }

    public void sendUpdateTopicStatus(
            int valid, String topic, String nodeId, ChannelHandlerContext ctx)
            throws JsonProcessingException {
        UpdateTopicStatus updateTopicStatus = new UpdateTopicStatus();
        updateTopicStatus.setCheckResult(valid);
        updateTopicStatus.setNodeId(nodeId);
        updateTopicStatus.setTopic(topic);
        String jsonStr =
                ObjectMapperFactory.getObjectMapper().writeValueAsString(updateTopicStatus);

        Message msg = new Message();
        msg.setData(jsonStr.getBytes());
        msg.setSeq(newSeq());
        msg.setResult(0);
        msg.setType((short) MsgType.UPDATE_TOPIICSTATUS.getType());
        logger.info(
                "private topic verify step4: finish signature verify, send out msg to update topic status, seq:{} topic:{} valid:{}",
                msg.getSeq(),
                topic,
                valid);
        ctx.writeAndFlush(msg);
    }

    public void addCallback(String seq, ResponseCallback callback) {
        seq2Callback.put(seq, callback);
    }

    private String newSeq() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
