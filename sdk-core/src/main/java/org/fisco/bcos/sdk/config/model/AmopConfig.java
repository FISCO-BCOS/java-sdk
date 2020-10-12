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

package org.fisco.bcos.sdk.config.model;

import java.io.File;
import java.util.List;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

public class AmopConfig {
    // AMOP topic related config
    private List<AmopTopic> amopTopicConfig;

    public AmopConfig(ConfigProperty configProperty) throws ConfigException {
        this.amopTopicConfig = configProperty.getAmop();
        if (amopTopicConfig == null) {
            return;
        }
        // Check Amop configure
        // checkFileExist();

    }

    private void checkFileExist() throws ConfigException {
        for (AmopTopic topic : amopTopicConfig) {
            if (null != topic.getPrivateKey()) {
                File privateKeyFile = new File(topic.getPrivateKey());
                if (!privateKeyFile.exists()) {
                    throw new ConfigException(
                            "Invalid configuration, " + topic.getPrivateKey() + " file not exist");
                }
            } else if (null != topic.getPublicKeys()) {
                for (String pubKey : topic.getPublicKeys()) {
                    File pubKeyFile = new File(pubKey);
                    if (!pubKeyFile.exists()) {
                        throw new ConfigException(
                                "Invalid configuration, " + pubKey + " file not exist");
                    }
                }
            } else {
                throw new ConfigException(
                        "Amop private topic is not configured right, please check your config file. Topic name "
                                + topic.getTopicName()
                                + ", neither private key nor public key list configured.");
            }
        }
    }

    public List<AmopTopic> getAmopTopicConfig() {
        return amopTopicConfig;
    }

    public void setAmopTopicConfig(List<AmopTopic> amopTopicConfig) {
        this.amopTopicConfig = amopTopicConfig;
    }
}
