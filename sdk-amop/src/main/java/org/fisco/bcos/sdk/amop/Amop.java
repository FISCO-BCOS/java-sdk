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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.fisco.bcos.sdk.amop.topic.TopicManager;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;

/**
 * AMOP module interface.
 *
 * @author Maggie
 */
public interface Amop {
    /**
     * Create a Amop object.
     *
     * @param channel
     * @param config
     * @return Amop instance
     */
    static Amop build(Channel channel, ConfigOption config) {
        return new AmopImp(channel, config);
    }

    /**
     * Subscribe a normal topic.
     *
     * @param topicName
     * @param callback callback is called when receive a msg relate to this topic
     */
    void subscribeTopic(String topicName, AmopCallback callback);

    /**
     * Subscribe a private topic which need verify.
     *
     * @param topicName
     * @param privateKeyTool the private key you used to prove your identity.
     * @param callback callback is called when receive a msg relate to this topic
     */
    void subscribePrivateTopics(String topicName, KeyTool privateKeyTool, AmopCallback callback);

    /**
     * Config a topic which is need verification, after that user can send message to verified
     * subscriber.
     *
     * @param topicName
     * @param publicKeyTools the public keys of the target organizations that you want to
     *     communicate with
     */
    void publishPrivateTopic(String topicName, List<KeyTool> publicKeyTools);

    /**
     * Unsubscribe a topic.
     *
     * @param topicName
     */
    void unsubscribeTopic(String topicName);

    /**
     * Send amop msg
     *
     * @param content
     * @param callback
     */
    void sendAmopMsg(AmopMsgOut content, ResponseCallback callback);

    /**
     * Send amop msg
     *
     * @param content
     */
    void broadcastAmopMsg(AmopMsgOut content);

    /**
     * Get all subscribe topics.
     *
     * @return topic name list
     */
    Set<String> getSubTopics();

    /**
     * Get list of subscribers to a topic
     *
     * @param topicName the topic you want to query
     * @return List of subscribers
     */
    List<String> getTopicSubscribers(String topicName);

    /**
     * set amop default callback
     *
     * @param cb the amop callback
     */
    void setCallback(AmopCallback cb);

    /** Start. */
    void start();

    /** Stop. */
    void stop();

    /** If configured private topic, wait until finish verify */
    void waitFinishPrivateTopicVerify();

    /**
     * generate message sequence string
     *
     * @return Sequence string
     */
    static String newSeq() {
        String seq = UUID.randomUUID().toString().replaceAll("-", "");
        return seq;
    }

    TopicManager getTopicManager();

    void sendSubscribe();
}
