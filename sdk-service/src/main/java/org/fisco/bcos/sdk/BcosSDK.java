/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcosSDK {
    private static Logger logger = LoggerFactory.getLogger(BcosSDK.class);
    public static final String ECDSA_TYPE_STR = "ecdsa";
    public static final String SM_TYPE_STR = "sm";

    private final ConfigOption config;
    private Client client;

    /**
     * Build BcosSDK instance
     *
     * @param tomlConfigFilePath the Toml type config file
     * @return BcosSDK instance
     * @throws BcosSDKException
     */
    public static BcosSDK build(String groupID, String tomlConfigFilePath) throws BcosSDKException {
        try {
            ConfigOption configOption = Config.load(tomlConfigFilePath);
            logger.info("create BcosSDK, configPath: {}", tomlConfigFilePath);
            return new BcosSDK(groupID, configOption);
        } catch (ConfigException e) {
            throw new BcosSDKException("create BcosSDK failed, error info: " + e.getMessage(), e);
        }
    }

    /**
     * Constructor, init by ConfigOption
     *
     * @param configOption the ConfigOption
     * @throws BcosSDKException
     */
    public BcosSDK(String groupID, ConfigOption configOption) throws BcosSDKException {
        this.config = configOption;
        try {
            // create group client
            this.client = Client.build(groupID, configOption);
        } catch (Exception e) {
            logger.warn(
                    "create client for group {} failed, error info: {}", groupID, e.getMessage());
        }
        if (this.client != null) {
            logger.info("create BcosSDK, create connection success, group id: {}", groupID);
            return;
        }
        throw new BcosSDKException("create BcosSDK failed for all connect failed");
    }

    /**
     * Get configuration
     *
     * @return ConfigOption
     */
    public ConfigOption getConfig() {
        return this.config;
    }

    public Client getClient() {
        return client;
    }

    /** Stop all module of BcosSDK */
    public void stopAll() {
        // stop the client
        this.client.stop();
    }
}
