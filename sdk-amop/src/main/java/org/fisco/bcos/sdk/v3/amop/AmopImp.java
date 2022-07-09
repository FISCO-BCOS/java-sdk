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

import java.util.HashSet;
import java.util.Set;
import org.fisco.bcos.sdk.jni.BcosSDKJniObj;
import org.fisco.bcos.sdk.jni.amop.AmopJniObj;
import org.fisco.bcos.sdk.jni.amop.AmopRequestCallback;
import org.fisco.bcos.sdk.jni.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class AmopImp implements Amop {
    private static final Logger logger = LoggerFactory.getLogger(AmopImp.class);

    private AmopJniObj amopJni;

    public AmopImp(long nativePointer) throws JniException {
        this.amopJni = AmopJniObj.build(nativePointer);

        logger.info("newAmop, nativePointer: {}", nativePointer);
        start();
    }

    @Override
    public void subscribeTopic(Set<String> topics) {
        amopJni.subscribeTopic(topics);
    }

    @Override
    public void subscribeTopic(String topicName, AmopRequestCallback callback) {
        amopJni.subscribeTopic(topicName, callback);
    }

    @Override
    public void unsubscribeTopic(String topicName) {
        Set<String> topics = new HashSet<>();
        topics.add(topicName);
        amopJni.unsubscribeTopic(topics);
    }

    @Override
    public void sendAmopMsg(
            String topic, byte[] content, int timeout, AmopResponseCallback callback) {
        amopJni.sendAmopMsg(topic, content, timeout, callback);
    }

    @Override
    public void sendResponse(String endpoint, String seq, byte[] content) {
        amopJni.sendResponse(endpoint, seq, content);
    }

    @Override
    public void broadcastAmopMsg(String topic, byte[] content) {
        amopJni.broadcastAmopMsg(topic, content);
    }

    @Override
    public Set<String> getSubTopics() {
        return amopJni.getSubTopics();
    }

    @Override
    public void setCallback(AmopRequestCallback cb) {
        amopJni.setCallback(cb);
    }

    @Override
    public void start() {
        if (amopJni != null) {
            amopJni.start();
        }
    }

    @Override
    public void stop() {
        if (amopJni != null) {
            amopJni.stop();
        }
    }

    @Override
    public void destroy() {
        if (amopJni != null) {
            BcosSDKJniObj.destroy(amopJni.getNativePointer());
            amopJni = null;
        }
    }
}
