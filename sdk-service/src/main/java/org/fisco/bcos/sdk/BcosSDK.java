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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.eventsub.EventResource;
import org.fisco.bcos.sdk.eventsub.EventSubscribe;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.fisco.bcos.sdk.service.GroupManagerServiceImpl;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
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
    private Amop amop;
    private EventResource eventResource;
    private ThreadPoolService threadPoolService;

    public static BcosSDK build(String tomlConfigFilePath) throws BcosSDKException {
        try {
            ConfigOption configOption = Config.load(tomlConfigFilePath);
            logger.info("create BcosSDK, configPath: {}", tomlConfigFilePath);
            return new BcosSDK(configOption);
        } catch (ConfigException e) {
            throw new BcosSDKException("create BcosSDK failed, error info: " + e.getMessage(), e);
        }
    }

    public BcosSDK(ConfigOption configOption) throws BcosSDKException {
        try {
            // create channel and load configuration file
            this.channel = Channel.build(configOption);
            this.channel.start();
            this.config = this.channel.getNetwork().getConfigOption();
            logger.info(
                    "create BcosSDK, start channel success, cryptoType: {}",
                    this.channel.getNetwork().getSslCryptoType());

            threadPoolService =
                    new ThreadPoolService(
                            "channelProcessor",
                            this.config.getThreadPoolConfig().getChannelProcessorThreadSize(),
                            this.config.getThreadPoolConfig().getMaxBlockingQueueSize());
            channel.setThreadPool(threadPoolService.getThreadPool());
            logger.info(
                    "create BcosSDK, start channel succ, channelProcessorThreadSize: {}, receiptProcessorThreadSize: {}",
                    config.getThreadPoolConfig().getChannelProcessorThreadSize(),
                    config.getThreadPoolConfig().getReceiptProcessorThreadSize());
            if (!waitForEstablishConnection()) {
                logger.error("create BcosSDK failed for the number of available peers is 0");
                throw new BcosSDKException(
                        "create BcosSDK failed for the number of available peers is 0");
            }
            // create GroupMangerService
            this.groupManagerService = new GroupManagerServiceImpl(this.channel, this.config);
            logger.info("create BcosSDK, create groupManagerService success");
            // init amop
            amop = Amop.build(this.channel, config);
            this.groupManagerService.setAmop(amop);
            amop.start();
            logger.info("create BcosSDK, create Amop success");
            // new EventResource
            eventResource = new EventResource();
        } catch (ChannelException | ConfigException e) {
            stopAll();
            throw new BcosSDKException("create BcosSDK failed, error info: " + e.getMessage(), e);
        }
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

    public Client getClient(Integer groupId) {
        checkGroupId(groupId);
        if (!waitForEstablishConnection()) {
            logger.error(
                    "get client for group: {} failed for the number of available peers is 0",
                    groupId);
            throw new BcosSDKException(
                    "get client for group "
                            + groupId
                            + " failed for the number of available peers is 0");
        }
        if (!groupToClient.containsKey(groupId)) {
            // create a new client for the specified group
            Client client =
                    Client.build(
                            this.groupManagerService, this.channel, this.eventResource, groupId);
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
        groupManagerService.fetchGroupList();
        Set<String> nodeList = groupManagerService.getGroupNodeList(groupId);
        if (nodeList.size() == 0) {
            groupToClient.remove(groupId);
            throw new BcosSDKException(
                    "create client for group "
                            + groupId
                            + " failed for no peers set up the group!");
        }
        return groupToClient.get(groupId);
    }

    public int getSSLCryptoType() {
        return this.channel.getNetwork().getSslCryptoType();
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

    public EventResource getEventResource() {
        return eventResource;
    }

    public EventSubscribe getEventSubscribe(Integer groupId) {
        return EventSubscribe.build(this.groupManagerService, this.eventResource, groupId);
    }

    public Channel getChannel() {
        return channel;
    }

    public void stopAll() {
        if (this.channel != null) {
            this.channel.stop();
        }
        if (this.threadPoolService != null) {
            this.threadPoolService.stop();
        }
        if (this.groupManagerService != null) {
            this.groupManagerService.stop();
        }
        if (this.amop != null) {
            this.amop.stop();
        }
        // stop the client
        for (Integer groupId : groupToClient.keySet()) {
            groupToClient.get(groupId).stop();
            EventSubscribe event = this.getEventSubscribe(groupId);
            if (event != null) {
                event.stop();
            }
        }
    }
}
