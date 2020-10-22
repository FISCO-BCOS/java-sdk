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

package org.fisco.bcos.sdk.amop;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.fisco.bcos.sdk.amop.exception.AmopException;
import org.fisco.bcos.sdk.amop.topic.AmopMsgHandler;
import org.fisco.bcos.sdk.amop.topic.TopicManager;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.model.AmopTopic;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Amop implement
 *
 * @author Maggie
 */
public class AmopImp implements Amop {
    private static Logger logger = LoggerFactory.getLogger(AmopImp.class);
    private Channel channel;
    private TopicManager topicManager;
    private AmopMsgHandler amopMsgHandler;

    public AmopImp(Channel channel, ConfigOption config) {
        this.channel = channel;
        topicManager = new TopicManager();
        try {
            loadConfiguredTopics(config);
        } catch (AmopException e) {
            logger.error("Amop topic is not configured right, error:{}", e);
        }
        amopMsgHandler = new AmopMsgHandler(this.channel, topicManager);
        this.channel.addMessageHandler(MsgType.REQUEST_TOPICCERT, amopMsgHandler);
        this.channel.addMessageHandler(MsgType.AMOP_REQUEST, amopMsgHandler);
        this.channel.addMessageHandler(MsgType.AMOP_MULBROADCAST, amopMsgHandler);
        this.channel.addMessageHandler(MsgType.AMOP_RESPONSE, amopMsgHandler);
        this.channel.addEstablishHandler(amopMsgHandler);
    }

    @Override
    public void subscribeTopic(String topicName, AmopCallback callback) {
        logger.info("subscribe normal topic, topic:{}", topicName);
        topicManager.addTopic(topicName, callback);
        sendSubscribe();
    }

    @Override
    public void subscribePrivateTopics(
            String topicName, KeyTool privateKeyTool, AmopCallback callback) {
        logger.info("subscribe private topic, topic:{}", topicName);
        topicManager.addPrivateTopicSubscribe(topicName, privateKeyTool, callback);
        sendSubscribe();
    }

    @Override
    public void publishPrivateTopic(String topicName, List<KeyTool> publicKeyTools) {
        logger.info(
                "setup private topic, topic:{} pubKey len:{}", topicName, publicKeyTools.size());
        topicManager.addPrivateTopicSend(topicName, publicKeyTools);
        sendSubscribe();
    }

    @Override
    public void unsubscribeTopic(String topicName) {
        logger.info("unsubscribe topic, topic:{}", topicName);
        topicManager.removeTopic(topicName);
        sendSubscribe();
    }

    @Override
    public void sendAmopMsg(AmopMsgOut content, AmopResponseCallback callback) {
        if (!topicManager.canSendTopicMsg(content)) {
            logger.error(
                    "can not send this amop private msg out, you have not configured the public keys. topic:{}",
                    content.getTopic());
        }
        AmopMsg msg = new AmopMsg();
        msg.setResult(0);
        msg.setSeq(newSeq());
        msg.setType((short) MsgType.AMOP_REQUEST.getType());
        msg.setTopic(content.getTopic());
        msg.setData(content.getContent());
        Options ops = new Options();
        ops.setTimeout(content.getTimeout());
        ResponseCallback cb =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        AmopResponse amopResponse = new AmopResponse(response);
                        callback.onResponse(amopResponse);
                    }
                };
        this.channel.asyncSendToRandom(msg, cb, ops);
        logger.info(
                "send amop msg to a random peer, seq{} topic{}", msg.getSeq(), content.getTopic());
    }

    @Override
    public void broadcastAmopMsg(AmopMsgOut content) {
        if (!topicManager.canSendTopicMsg(content)) {
            logger.error(
                    "can not send this amop private msg out, you have not configured the public keys. topic:{}",
                    content.getTopic());
        }
        AmopMsg amopMsg = new AmopMsg();
        amopMsg.setResult(0);
        amopMsg.setSeq(newSeq());
        amopMsg.setType((short) MsgType.AMOP_MULBROADCAST.getType());
        amopMsg.setTopic(content.getTopic());
        amopMsg.setData(content.getContent());
        // Add broadcast callback
        this.channel.broadcast(amopMsg.getMessage());
        logger.info(
                "broadcast amop msg to peers, seq:{} topic:{}",
                amopMsg.getSeq(),
                amopMsg.getTopic());
    }

    @Override
    public Set<String> getSubTopics() {
        return topicManager.getTopicNames();
    }

    @Override
    public void setCallback(AmopCallback cb) {
        topicManager.setCallback(cb);
    }

    @Override
    public void start() {
        logger.info("amop module started");
        amopMsgHandler.setIsRunning(true);
        sendSubscribe();
    }

    @Override
    public void stop() {
        logger.info("amop module stopped");
        amopMsgHandler.setIsRunning(false);
        unSubscribeAll();
    }

    private void unSubscribeAll() {
        List<String> peers = this.channel.getAvailablePeer();
        logger.info("unsubscribe all topics, inform {} peers", peers.size());
        for (String peer : peers) {
            try {
                unSubscribeToPeer(peer);
            } catch (JsonProcessingException e) {
                logger.error("Unsubscribe failed", e);
            }
        }
    }

    @Override
    public void sendSubscribe() {
        topicManager.updatePrivateTopicUUID();
        List<String> peers = this.channel.getAvailablePeer();
        logger.info("update subscribe inform {} peers", peers.size());
        for (String peer : peers) {
            try {
                updateSubscribeToPeer(peer);
            } catch (JsonProcessingException e) {
                logger.error(
                        "update amop subscription to node {}, json processed error, error message: {}",
                        peer,
                        e.getMessage());
            }
        }
    }

    private void updateSubscribeToPeer(String peer) throws JsonProcessingException {
        byte[] topics = getSubData(topicManager.getSubByPeer(peer));
        Message msg = new Message();
        msg.setType((short) MsgType.AMOP_CLIENT_TOPICS.getType());
        msg.setResult(0);
        msg.setSeq(newSeq());
        msg.setData(topics);
        Options opt = new Options();
        this.channel.asyncSendToPeer(msg, peer, null, opt);
        logger.debug("update topics to node, node:{}, topics:{}", peer, new String(topics));
    }

    private void unSubscribeToPeer(String peer) throws JsonProcessingException {
        Message msg = new Message();
        msg.setType((short) MsgType.AMOP_CLIENT_TOPICS.getType());
        msg.setResult(0);
        msg.setSeq(newSeq());
        byte[] topics = getSubData(topicManager.getBlockNotifyByPeer(peer));
        msg.setData(topics);
        Options opt = new Options();
        this.channel.asyncSendToPeer(msg, peer, null, opt);
        logger.info(
                " send update topic message request, seq: {}, content: {}",
                msg.getSeq(),
                new String(msg.getData()));
    }

    private String newSeq() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private byte[] getSubData(Set<String> topics) throws JsonProcessingException {
        byte[] topicBytes =
                ObjectMapperFactory.getObjectMapper().writeValueAsBytes(topics.toArray());
        return topicBytes;
    }

    private void loadConfiguredTopics(ConfigOption config) throws AmopException {
        if (null == config.getAmopConfig() || null == config.getAmopConfig().getAmopTopicConfig()) {
            return;
        }
        List<AmopTopic> topics = config.getAmopConfig().getAmopTopicConfig();
        for (AmopTopic topic : topics) {
            if (null != topic.getPrivateKey()) {
                String privKeyFile = topic.getPrivateKey();
                KeyTool keyTool;

                if (privKeyFile.endsWith("p12")) {
                    keyTool = new P12KeyStore(privKeyFile, topic.getPassword());
                } else {
                    keyTool = new PEMKeyStore(privKeyFile);
                }
                topicManager.addPrivateTopicSubscribe(topic.getTopicName(), keyTool, null);
            } else if (null != topic.getPublicKeys()) {
                List<KeyTool> pubList = new ArrayList<>();
                for (String pubKey : topic.getPublicKeys()) {
                    KeyTool keyTool = new PEMKeyStore(pubKey);
                    pubList.add(keyTool);
                }
                topicManager.addPrivateTopicSend(topic.getTopicName(), pubList);
            } else {
                throw new AmopException(
                        "Amop private topic is not configured right, please check your config file. Topic name "
                                + topic.getTopicName()
                                + ", neither private key nor public key list configured.");
            }
        }
    }

    public Set<String> getAllTopics() {
        return topicManager.getAllTopics();
    }

    @Override
    public TopicManager getTopicManager() {
        return this.topicManager;
    }
}
