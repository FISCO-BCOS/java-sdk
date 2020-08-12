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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigException;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.fisco.bcos.sdk.service.GroupManagerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcosSDK {
    private static Logger logger = LoggerFactory.getLogger(BcosSDK.class);
    public static final String ECDSA_TYPE_STR = "ecdsa";
    public static final String SM_TYPE_STR = "sm";

    private final ConfigOption config;
    private final Channel channel;
    private final GroupManagerService groupManagerService;
    private ConcurrentHashMap<Integer, Client> groupToClient = new ConcurrentHashMap<>();
    private long maxWaitEstablishConnectionTime = 30000;
    // TODO: configure the thread pool
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Amop amop;

    public BcosSDK(String configPath) throws ConfigException {
        logger.info("create BcosSDK, configPath: {}", configPath);
        // load configuration file
        this.config = Config.load(configPath);
        logger.info("create BcosSDK, load configPath: {} succ", configPath);
        // create channel
        this.channel = Channel.build(this.config);
        channel.setThreadPool(threadPool);
        this.channel.start();
        logger.info("create BcosSDK, start channel succ");
        if (!waitForEstablishConnection()) {
            logger.error("create BcosSDK failed for the number of available peers is 0");
            throw new BcosSDKException(
                    "create BcosSDK failed for the number of available peers is 0");
        }
        // create GroupMangerService
        this.groupManagerService = new GroupManagerServiceImpl(this.channel);
        logger.info("create BcosSDK, create groupManagerService success");
        // init amop
        amop = Amop.build(groupManagerService, config);
        logger.info("create BcosSDK, create Amop success");
    }

    private boolean waitForEstablishConnection() {
        long startTime = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - startTime < maxWaitEstablishConnectionTime
                    && this.channel.getAvailablePeer().size() == 0) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.warn("waitForEstablishConnection exceptioned, error info: {}", e.getMessage());
        }
        return (this.channel.getAvailablePeer().size() > 0);
    }

    public Client getClient(Integer groupId) {
        if (!waitForEstablishConnection()) {
            logger.error("get client for group: {} failed for the number of available peers is 0");
            return null;
        }
        if (!groupToClient.contains(groupId)) {
            // create a new client for the specified group
            Client client = Client.build(this.groupManagerService, this.channel, groupId);
            if (client == null) {
                throw new BcosSDKException("create client for group " + groupId + " failed!");
            }
            groupToClient.put(groupId, client);
            logger.info("create client for group {} success", groupId);
        }
        return groupToClient.get(groupId);
    }

    public Channel getChannel() {
        return this.channel;
    }

    public GroupManagerService getGroupManagerService() {
        return this.groupManagerService;
    }

    public ConfigOption getConfig() {
        return this.config;
    }

    public Amop getAmop() {
        return amop;
    }
}
