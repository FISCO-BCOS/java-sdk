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
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.network.Connection;
import org.fisco.bcos.sdk.network.HttpConnection;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class BcosSDK {
  private static Logger logger = LoggerFactory.getLogger(BcosSDK.class);
  public static final String ECDSA_TYPE_STR = "ecdsa";
  public static final String SM_TYPE_STR = "sm";

  private final ConfigOption config;
  private final Connection connection;
  private ConcurrentHashMap<Integer, Client> groupToClient = new ConcurrentHashMap<>();
  private long maxWaitEstablishConnectionTime = 30000;
  private ThreadPoolService threadPoolService;

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
      // create GroupMangerService
      this.connection = new HttpConnection(this.config);
      logger.info("create BcosSDK, create connection success");
    } catch (ChannelException e) {
      stopAll();
      throw new BcosSDKException("create BcosSDK failed, error info: " + e.getMessage(), e);
    }
  }

  /**
   * Check whether have at least one node in the specific group
   *
   * @param groupId the target group id
   */
  public void checkGroupId(Integer groupId) {
    if (groupId < ConstantConfig.MIN_GROUPID || groupId > ConstantConfig.MAX_GROUPID) {
      throw new BcosSDKException(
          "create client for group "
              + groupId
              + " failed for invalid groupId! The groupID must be no smaller than "
              + ConstantConfig.MIN_GROUPID
              + " and no more than "
              + ConstantConfig.MAX_GROUPID);
    }
  }

  /**
   * Get a Client instance of a specific group
   *
   * @param groupId the group id
   * @return Client
   */
  public Client getClient(Integer groupId) {
    checkGroupId(groupId);
    if (!groupToClient.containsKey(groupId)) {
      // create a new client for the specified group
      Client client = Client.build(this.connection, groupId, this.config);
      if (client == null) {
        throw new BcosSDKException(
            "create client for group "
                + groupId
                + " failed! Please check the existence of group "
                + groupId
                + " of the connected node!");
      }
      groupToClient.put(groupId, client);
      logger.info("create client for group {} success", groupId);
    }
    return groupToClient.get(groupId);
  }

  /**
   * Get group manager service instance
   *
   * @return GroupManagerService
   */
  public Connection getConnection() {
    return this.connection;
  }

  /**
   * Get configuration
   *
   * @return ConfigOption
   */
  public ConfigOption getConfig() {
    return this.config;
  }

  /** Stop all module of BcosSDK */
  public void stopAll() {
    if (this.threadPoolService != null) {
      this.threadPoolService.stop();
    }
    if (this.connection != null) {
      this.connection.close();
    }
    // stop the client
    for (Integer groupId : groupToClient.keySet()) {
      groupToClient.get(groupId).stop();
    }
  }
}
