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

import java.util.concurrent.ConcurrentHashMap;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.network.NetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcosSDK {
    private static Logger logger = LoggerFactory.getLogger(BcosSDK.class);
    public static final String ECDSA_TYPE_STR = "ecdsa";
    public static final String SM_TYPE_STR = "sm";

    private final ConfigOption config;
    private ConcurrentHashMap<String, Client> groupToClient = new ConcurrentHashMap<>();

    /**
     * Build BcosSDK instance
     *
     * @param tomlConfigFilePath the Toml type config file
     * @return BcosSDK instance
     * @throws BcosSDKException
     */
    public static BcosSDK build(String tomlConfigFilePath) throws BcosSDKException {
        try {
            ConfigOption configOption = Config.load(tomlConfigFilePath);
            logger.info("create BcosSDK, configPath: {}", tomlConfigFilePath);
            return new BcosSDK(configOption);
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
    public BcosSDK(ConfigOption configOption) throws BcosSDKException {
        this.config = configOption;
        try {
            // create sdk
            return;
        } catch (Exception e) {
            logger.warn("create client for failed, error info: {}", e.getMessage());
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

    /**
     * Check whether group id in valid
     *
     * @param groupId the target group id
     */
    private void checkGroupId(String groupId) {
        // check group string
        String regex = "[A-Za-z0-9_\\-\\\\\\\\u4e00-\\\\\\\\u9fa5]+";
        if (!groupId.matches(regex)) {

            throw new BcosSDKException(
                    "create client for group "
                            + groupId
                            + " failed for invalid group name! The string regex must match "
                            + regex);
        }
    }

    /**
     * Get a Client instance of a specific group
     *
     * @param groupId the group id
     * @return Client
     */
    public Client getClient(String groupId) throws NetworkException {
        checkGroupId(groupId);
        if (!groupToClient.containsKey(groupId)) {
            // create a new client for the specified group
            Client client = Client.build(groupId, this.config);
            groupToClient.put(groupId, client);
            logger.info("create client for group {} success", groupId);
        }
        return groupToClient.get(groupId);
    }

    /** Stop all module of BcosSDK */
    public void stopAll() {
        // stop all client
        groupToClient.forEach(
                (String groupId, Client client) -> {
                    logger.info("Stopping client for group {}.", groupId);
                    client.stop();
                });
    }
}
