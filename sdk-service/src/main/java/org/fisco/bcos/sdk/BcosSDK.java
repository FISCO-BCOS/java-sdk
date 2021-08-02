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

import io.netty.channel.ChannelException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.network.NetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

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
        try {
            this.config = configOption;
            for (String endpoint : this.config.getNetworkConfig().getPeers()) {
                // create all clients
                Client client = Client.build(endpoint, this.config);
                this.groupToClient.put(client.getGroupId(), client);
            }
            logger.info("create BcosSDK, create connection success");
        } catch (ChannelException | NetworkException e) {
            this.stopAll();
            throw new BcosSDKException("create BcosSDK failed, error info: " + e.getMessage(), e);
        }
    }

    /**
     * Get a Client instance of a specific group
     *
     * @param groupId the group id
     * @return Client
     */
    public Client getClientByGroupID(String groupId) {
        if (!this.groupToClient.containsKey(groupId)) {
            // create a new client for the specified group
            logger.info(
                    "client for group " + groupId + " is not exist! Please check the existence of group "
                            + groupId + " of the connected node!");
            return null;
        }
        return this.groupToClient.get(groupId);
    }

    /**
     * Get a Client instance of a specific group
     *
     * @param endpoint
     * @return Client
     */
    public Client getClientByEndpoint(String endpoint) {
        for (Client client : this.groupToClient.values()) {
            if (client.getConnection().getUri().contains(endpoint)) {
                return client;
            }
        }
        return null;
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
     * Stop all module of BcosSDK
     */
    public void stopAll() {
        // stop the client
        for (String groupId : this.groupToClient.keySet()) {
            this.groupToClient.get(groupId).stop();
        }
    }
}
