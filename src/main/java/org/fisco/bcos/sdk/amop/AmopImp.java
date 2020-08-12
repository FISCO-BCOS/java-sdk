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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.fisco.bcos.sdk.amop.topic.TopicManager;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.service.GroupManagerService;
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
    private GroupManagerService groupManager;
    private TopicManager topicManager;

    public AmopImp(GroupManagerService groupManager, ConfigOption config) {
        this.groupManager = groupManager;
        topicManager = new TopicManager();
        List<String> peers = groupManager.getChannel().getAvailablePeer();
        for (String peer : peers) {
            List<String> groupInfo = groupManager.getGroupInfoByNodeInfo(peer);
            topicManager.addBlockNotify(peer, groupInfo);
        }
        // todo load topics ConfigOption
        sendSubscribe();
    }

    @Override
    public void subscribeTopic(String topicName, AmopCallback callback) {}

    @Override
    public void subscribePrivateTopics(
            String topicName, KeyManager privateKeyManager, AmopCallback callback) {}

    @Override
    public void setupPrivateTopic(String topicName, List<KeyManager> publicKeyManagers) {}

    @Override
    public void unsubscribeTopic(String topicName) {}

    @Override
    public void sendAmopMsg(AmopMsg msg, AmopCallback callback) {}

    @Override
    public List<String> getSubTopics() {
        return null;
    }

    @Override
    public void start() {};

    @Override
    public void stop() {}

    private void sendSubscribe() {
        List<String> peers = groupManager.getChannel().getAvailablePeer();
        logger.debug("send subscribe to {} peers", peers.size());
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
        Message msg = new Message();
        msg.setType((short) MsgType.AMOP_CLIENT_TOPICS.getType());
        msg.setResult(0);
        msg.setSeq(newSeq());
        msg.setData(getSubData(topicManager.getSubByPeer(peer)));
        ResponseCallback callback =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        logger.info(
                                "amop response, seq : {}, error: {}, content: {}",
                                response.getMessageID(),
                                response.getErrorCode(),
                                response.getContent());
                        // todo
                    }
                };
        Options opt = new Options();
        groupManager.getChannel().asyncSendToPeer(msg, peer, callback, opt);
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
        /*int b = 1 + topicBytes.length;
        byte length = (byte)b;
        byte[]  content =  new byte[1+topicBytes.length];
        content[0] = length;
        System.arraycopy(topicBytes, 0, content, 1, topicBytes.length);*/
        return topicBytes;
    }
}
