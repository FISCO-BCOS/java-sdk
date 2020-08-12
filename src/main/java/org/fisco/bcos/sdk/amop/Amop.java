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
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.model.AmopMsg;
import org.fisco.bcos.sdk.service.GroupManagerService;

/**
 * AMOP module interface.
 *
 * @author Maggie
 */
public interface Amop {
    /**
     * Create a Amop object.
     *
     * @param groupManager
     * @param config
     * @return Amop instance
     */
    static Amop build(GroupManagerService groupManager, ConfigOption config) {
        return new AmopImp(groupManager, config);
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
     * @param privateKeyManager the private key you used to prove your identity.
     * @param callback callback is called when receive a msg relate to this topic
     */
    void subscribePrivateTopics(
            String topicName, KeyManager privateKeyManager, AmopCallback callback);

    /**
     * Config a topic which is need verification, after that user can send message to verified
     * subscriber.
     *
     * @param topicName
     * @param publicKeyManagers the public keys of the target organizations that you want to
     *     communicate with
     */
    void setupPrivateTopic(String topicName, List<KeyManager> publicKeyManagers);

    /**
     * Unsubscribe a topic.
     *
     * @param topicName
     */
    void unsubscribeTopic(String topicName);

    /**
     * Send amop msg
     *
     * @param msg
     * @param callback
     */
    void sendAmopMsg(AmopMsg msg, AmopCallback callback);

    /**
     * Get all subscribe topics.
     *
     * @return topic name list
     */
    List<String> getSubTopics();

    /** Start. */
    void start();

    /** Stop. */
    void stop();
}
