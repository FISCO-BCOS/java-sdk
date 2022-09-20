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

package org.fisco.bcos.sdk.v3.amop;

import java.util.Set;
import org.fisco.bcos.sdk.jni.BcosSDKJniObj;
import org.fisco.bcos.sdk.jni.amop.AmopRequestCallback;
import org.fisco.bcos.sdk.jni.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AMOP module interface.
 *
 * @author Maggie
 */
public interface Amop {
    Logger logger = LoggerFactory.getLogger(Amop.class);

    /**
     * Create a Amop object.
     *
     * @param configOption the config object
     * @return Amop instance
     * @throws JniException throw when build AMOP service failed
     */
    static Amop build(ConfigOption configOption) throws JniException {
        long nativePointer = BcosSDKJniObj.create(configOption.getJniConfig());
        logger.info("build AMOP, configOption: {}", configOption);
        return build(nativePointer);
    }

    /**
     * Create an Amop object.
     *
     * @param nativePointer the
     * @return Amop instance
     * @throws JniException throw when build AMOP service failed
     */
    static Amop build(long nativePointer) throws JniException {
        return new AmopImp(nativePointer);
    }

    /**
     * Subscribe a normal topic.
     *
     * @param topics the topic name
     */
    void subscribeTopic(Set<String> topics);

    /**
     * Subscribe a normal topic.
     *
     * @param topicName the topic name
     * @param callback callback is called when receive a msg relate to this topic
     */
    void subscribeTopic(String topicName, AmopRequestCallback callback);

    /**
     * Unsubscribe a topic.
     *
     * @param topicName the topic name
     */
    void unsubscribeTopic(String topicName);

    /**
     * Send amop msg
     *
     * @param topic topic name
     * @param timeout timeout config
     * @param content the message be sent
     * @param callback the callback that will be called when receive the AMOP response
     */
    void sendAmopMsg(String topic, byte[] content, int timeout, AmopResponseCallback callback);

    /**
     * Send response msg
     *
     * @param endpoint send endpoint
     * @param seq response seq
     * @param content response content
     */
    void sendResponse(String endpoint, String seq, byte[] content);

    /**
     * Send amop msg
     *
     * @param topic topic name
     * @param content the AMOP message to be broadcast
     */
    void broadcastAmopMsg(String topic, byte[] content);

    /**
     * Get all subscribe topics.
     *
     * @return topic name list
     */
    Set<String> getSubTopics();

    /**
     * set amop default callback
     *
     * @param cb the amop callback
     */
    void setCallback(AmopRequestCallback cb);

    /** Start. */
    void start();

    /** Stop. */
    void stop();

    /** Destroy amop object */
    void destroy();
}
