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
import org.fisco.bcos.sdk.amop.exception.AmopException;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Amop implement
 *
 * @author Maggie
 */
public class AmopImp implements Amop {
    private static Logger logger = LoggerFactory.getLogger(AmopImp.class);
    private Channel ch;

    public AmopImp(Channel channel, ConfigOption config) throws AmopException {
        this.ch = channel;
        // todo load topics ConfigOption
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
    public void start() {}

    @Override
    public void stop() {}
}
