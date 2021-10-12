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

import java.util.Set;
import org.fisco.bcos.sdk.jni.amop.AmopCallback;
import org.fisco.bcos.sdk.jni.amop.AmopResponseCallback;
import org.fisco.bcos.sdk.jni.common.ConfigOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class AmopImp implements Amop {
    private static Logger logger = LoggerFactory.getLogger(AmopImp.class);

    private org.fisco.bcos.sdk.jni.amop.Amop amopJni;

    public AmopImp(ConfigOption config) {
        // TODO:
        // this.amopJni = new org.fisco.bcos.sdk.jni.amop.Amop(config);
    }

    @Override
    public void subscribeTopic(String topicName, AmopCallback callback) {
        amopJni.subscribeTopic(topicName, callback);
    }

    @Override
    public void unsubscribeTopic(String topicName) {
        amopJni.unsubscribeTopic(topicName);
    }

    @Override
    public void sendAmopMsg(byte[] content, AmopResponseCallback callback) {
        amopJni.sendAmopMsg(content, callback);
    }

    @Override
    public void broadcastAmopMsg(byte[] content) {
        amopJni.broadcastAmopMsg(content);
    }

    @Override
    public Set<String> getSubTopics() {
        return amopJni.getSubTopics();
    }

    @Override
    public void setCallback(AmopCallback cb) {
        amopJni.setCallback(cb);
    }

    @Override
    public void start() {
        amopJni.start();
    }

    @Override
    public void stop() {
        amopJni.stop();
    }
}
