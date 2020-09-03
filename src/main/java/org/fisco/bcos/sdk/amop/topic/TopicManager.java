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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.fisco.bcos.sdk.amop.AmopCallback;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicManager {
    private static Logger logger = LoggerFactory.getLogger(TopicManager.class);
    private Map<String, KeyManager> topic2PrivateKey = new ConcurrentHashMap<>();
    private Map<String, List<KeyManager>> topic2PublicKeys = new ConcurrentHashMap<>();
    private Map<String, String> topicName2FullName = new ConcurrentHashMap<>();
    private Map<String, AmopCallback> topic2Callback = new ConcurrentHashMap<>();
    private Set<String> topics = new HashSet<>();
    private Map<String, Set<String>> peer2BlockNotify = new ConcurrentHashMap<>();
    private AmopCallback callback;

    public static final String verifyChannelPrefix = "#!$VerifyChannel_";
    public static final String pushChannelPrefix = "#!$PushChannel_";
    public static final String topicNeedVerifyPrefix = "#!$TopicNeedVerify_";

    public void addTopic(String topicString, AmopCallback callback) {
        topics.add(topicString);
        topicName2FullName.put(topicString, topicString);
        if (callback != null) {
            topic2Callback.put(topicString, callback);
        }
    }

    public void addPrivateTopicSubscribe(
            String topicName, KeyManager privateKeyStore, AmopCallback callback) {
        String fullNameToSendToNode = makeVerifyChannelPrefixTopic(topicName);
        logger.trace(
                "add private topic subscribe, topic:{} full name:{}",
                topicName,
                fullNameToSendToNode);
        topics.add(fullNameToSendToNode);
        topics.add(addNeedVerifyTopicPrefix(topicName));
        topic2PrivateKey.put(addNeedVerifyTopicPrefix(topicName), privateKeyStore);
        topicName2FullName.put(topicName, fullNameToSendToNode);
        if (callback != null) {
            topic2Callback.put(addNeedVerifyTopicPrefix(topicName), callback);
        }
    }

    public void addPrivateTopicSend(String topicName, List<KeyManager> publicKeyManagers) {
        String fullNameToSendToNode = makePushChannelPrefixTopic(topicName);
        logger.trace(
                "add private topic to send, topic:{} full name:{}",
                topicName,
                fullNameToSendToNode);
        topics.add(fullNameToSendToNode);
        topic2PublicKeys.put(addNeedVerifyTopicPrefix(topicName), publicKeyManagers);
        topicName2FullName.put(topicName, fullNameToSendToNode);
    }

    public void addPrivateTopicCallback(String topicName, AmopCallback callback) {
        logger.trace("add private topic callback, topic:{}", topicName);
        topic2Callback.put(addNeedVerifyTopicPrefix(topicName), callback);
    }

    /** Make sure do not use same name of a normal and a private topic */
    public void removeTopic(String topicName) {
        logger.trace("remove topic, topic:{}", topicName);
        String fullName = topicName2FullName.get(topicName);
        if (null != fullName) {
            topics.remove(fullName);
            topics.remove(addNeedVerifyTopicPrefix(topicName));
            topicName2FullName.remove(topicName);
            topic2PublicKeys.remove(addNeedVerifyTopicPrefix(topicName));
            topic2PrivateKey.remove(addNeedVerifyTopicPrefix(topicName));
            if (fullName.length() > topicName.length()) {
                topic2Callback.remove(addNeedVerifyTopicPrefix(topicName));
            } else {
                topic2Callback.remove(topicName);
            }
            logger.trace("success remove topic, topic:{}", topicName);
        }
    }

    public Set<String> getSubByPeer(String peerIpPort) {
        Set<String> notify = peer2BlockNotify.get(peerIpPort);
        Set<String> peerSub = new HashSet<>();
        if (topics != null) {
            peerSub.addAll(topics);
        }
        if (notify != null) {
            peerSub.addAll(notify);
        }
        logger.trace("get sub by peer, peer:{}, sub:{}", peerIpPort, peerSub.size());
        return peerSub;
    }

    public Set<String> getTopicNames() {
        return topicName2FullName.keySet();
    }

    public void addBlockNotify(String peerIpPort, List<String> groupInfo) {
        logger.trace("add block notify, peer{}, groupInfo:{}", peerIpPort, groupInfo.size());
        Set<String> pnf = peer2BlockNotify.get(peerIpPort);
        if (null == pnf) {
            pnf = new HashSet<>();
            for (String group : groupInfo) {
                pnf.add("_block_notify_" + group);
                logger.trace(
                        "add block notify, peer{}, topic:{}", peerIpPort, "_block_notify_" + group);
            }
            peer2BlockNotify.put(peerIpPort, pnf);
        } else {
            for (String group : groupInfo) {
                pnf.add("_block_notify_" + group);
            }
        }
    }

    public AmopCallback getCallback(String topicName) {
        if (topic2Callback.get(topicName) != null) {
            return topic2Callback.get(topicName);
        } else {
            return callback;
        }
    }

    public String getFullTopicString(String topicName) {
        return topicName2FullName.get(topicName);
    }

    public void setCallback(AmopCallback cb) {
        this.callback = cb;
    }

    public List<KeyManager> getPublicKeysByTopic(String topic) {
        return topic2PublicKeys.get(topic);
    }

    public KeyManager getPrivateKeyByTopic(String topic) {
        return topic2PrivateKey.get(topic);
    }

    public boolean isSubTopic(String topic) {
        return topics.contains(topic);
    }

    public boolean canSendTopicMsg(AmopMsgOut out) {
        if (out.getType() == TopicType.NORMAL_TOPIC) {
            return true;
        } else {
            return topic2PublicKeys.keySet().contains(out.getTopic());
        }
    }

    public void updatePrivateTopicUUID() {
        for (Map.Entry<String, String> topic : topicName2FullName.entrySet()) {
            if (topic.getValue().contains(verifyChannelPrefix)) {
                topics.remove(topic.getValue());
                String newFullname = makeVerifyChannelPrefixTopic(topic.getKey());
                topics.add(newFullname);
                topicName2FullName.put(topic.getKey(), newFullname);
                logger.trace("update uuid, old:{} new:{}", topic.getValue(), newFullname);
            }
        }
    }

    public Set<String> getAllTopics() {
        return topics;
    }

    private String addNeedVerifyTopicPrefix(String topicName) {
        StringBuilder sb = new StringBuilder();
        sb.append(topicNeedVerifyPrefix);
        sb.append(topicName);
        return sb.toString();
    }

    private String makeVerifyChannelPrefixTopic(String topicName) {
        StringBuilder sb = new StringBuilder();
        sb.append(verifyChannelPrefix).append(addNeedVerifyTopicPrefix(topicName)).append('_');
        sb.append(UUID.randomUUID().toString().replaceAll("-", ""));
        return sb.toString();
    }

    private String makePushChannelPrefixTopic(String topicName) {
        StringBuilder sb = new StringBuilder();
        sb.append(pushChannelPrefix).append(addNeedVerifyTopicPrefix(topicName));
        return sb.toString();
    }
}
