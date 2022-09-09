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
package org.fisco.bcos.sdk.v3;

import org.fisco.bcos.sdk.jni.BcosSDKJniObj;
import org.fisco.bcos.sdk.jni.BlockNotifier;
import org.fisco.bcos.sdk.v3.amop.Amop;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.eventsub.EventSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcosSDK {
    private static final Logger logger = LoggerFactory.getLogger(BcosSDK.class);

    private final ConfigOption config;
    private final BcosSDKJniObj bcosSDKJniObj;

    public ConfigOption getConfig() {
        return config;
    }

    /**
     * Build BcosSDK instance from toml config path
     *
     * @param tomlConfigFilePath the Toml type config file
     * @return BcosSDK instance
     * @throws BcosSDKException create sdk instance failed
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
     * @throws BcosSDKException create sdk instance failed
     */
    public BcosSDK(ConfigOption configOption) throws BcosSDKException {
        try {
            this.config = configOption;
            this.bcosSDKJniObj = BcosSDKJniObj.build(this.config.getJniConfig());
        } catch (Exception e) {
            throw new BcosSDKException("create BcosSDK failed, error: " + e.getMessage(), e);
        }
    }

    /**
     * @param groupID group id
     * @param blockNotifier block notifier instance
     */
    public void registerBlockNotifier(String groupID, BlockNotifier blockNotifier) {
        this.bcosSDKJniObj.registerBlockNotifier(groupID, blockNotifier);
    }

    /**
     * Get a Client instance of a specific group
     *
     * @param groupId specific group id
     * @return Client
     */
    public Client getClient(String groupId) throws BcosSDKException {
        try {
            return Client.build(groupId, config, bcosSDKJniObj.getNativePointer());
        } catch (Exception e) {
            logger.warn("create client for failed, error: ", e);
            throw new BcosSDKException("get Client failed, e: " + e.getMessage(), e);
        }
    }

    /**
     * Get a Client instance of default group in config
     *
     * @return Client
     */
    public Client getClient() throws BcosSDKException {
        try {
            String groupId = config.getNetworkConfig().getDefaultGroup();
            if ((groupId == null) || groupId.isEmpty()) {
                throw new BcosSDKException(
                        "The default group is not set, please set it in config.toml: defaultGroup field");
            }
            return Client.build(
                    config.getNetworkConfig().getDefaultGroup(),
                    config,
                    bcosSDKJniObj.getNativePointer());
        } catch (Exception e) {
            logger.warn("create client for failed, error: ", e);
            throw new BcosSDKException("get Client failed, e: " + e.getMessage(), e);
        }
    }

    /**
     * Get an amop instance of a specific group
     *
     * @return Client
     */
    public Amop getAmop() throws BcosSDKException {
        try {
            return Amop.build(config);
        } catch (Exception e) {
            logger.error("create amop for failed, error: ", e);
            throw new BcosSDKException("get amop failed, e: " + e.getMessage());
        }
    }

    /**
     * Get an event subscribe instance of a specific group
     *
     * @param groupId specific group id
     * @return Client
     */
    public EventSubscribe getEventSubscribe(String groupId) throws BcosSDKException {
        try {
            return EventSubscribe.build(groupId, config);
        } catch (Exception e) {
            logger.warn("create event sub for failed, error: ", e);
            throw new BcosSDKException("get event sub failed, e: " + e.getMessage());
        }
    }

    /** Stop all module of BcosSDK */
    public void stopAll() {}
}
