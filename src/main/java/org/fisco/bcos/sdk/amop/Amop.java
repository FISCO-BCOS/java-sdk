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
import org.fisco.bcos.sdk.channel.Channel;

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
     * @param configFile
     * @return Amop instance
     */
    static Amop build(Channel channel, String configFile) {
        return null;
    }

    /**
     * Subscribe a normal topic.
     *
     * @param topicName
     */
    void subscribeTopic(String topicName);

    /**
     * Subscribe a topic which need verify.
     *
     * @param topicName
     * @param privateKey
     */
    void subscribeNeedVerifyTopics(String topicName, String privateKey);

    /**
     * Config a topic which is need verification, after that user can send message to verified
     * subscriber.
     *
     * @param topicName
     * @param publicKeys
     */
    void addNeedVerifyTopics(String topicName, List<String> publicKeys);

    /**
     * Unsubscribe a topic.
     *
     * @param topicName
     */
    void unsubscribeTopic(String topicName);

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
