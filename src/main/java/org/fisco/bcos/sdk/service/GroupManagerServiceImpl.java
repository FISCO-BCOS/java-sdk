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
package org.fisco.bcos.sdk.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.PeerSelectRule;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.response.GroupList;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.service.model.BlockNumberMessageDecoder;
import org.fisco.bcos.sdk.service.model.BlockNumberNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupManagerServiceImpl implements GroupManagerService {
    private static Logger logger = LoggerFactory.getLogger(GroupManagerServiceImpl.class);
    private final Channel channel;
    private final GroupServiceFactory groupServiceFactory;
    private ConcurrentHashMap<Integer, GroupService> groupIdToService = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<String>> nodeToGroupIDList = new ConcurrentHashMap<>();
    private final BlockNumberMessageDecoder blockNumberMessageDecoder;
    private Client groupInfoGetter;
    // TODO: get the fetchGroupListIntervalMs from the configuration
    private long fetchGroupListIntervalMs = 60000;
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    AtomicBoolean running = new AtomicBoolean(false);

    public GroupManagerServiceImpl(Channel channel) {
        this.channel = channel;
        this.groupServiceFactory = new GroupServiceFactory();
        this.groupInfoGetter = Client.build(channel);
        fetchGroupList();
        this.blockNumberMessageDecoder = new BlockNumberMessageDecoder(channel.getVersion());
        this.start();
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    protected void finalize() {
        stop();
    }

    /** Stop group list fetching thread */
    protected void stop() {
        if (!running.get()) {
            logger.warn("GroupManagerService has already been stopped!");
            return;
        }
        logger.debug("stop GroupManagerService...");
        scheduledExecutorService.shutdown();
        try {
            while (!scheduledExecutorService.isTerminated()) {
                scheduledExecutorService.awaitTermination(10, TimeUnit.MILLISECONDS);
            }
            scheduledExecutorService.shutdownNow();
        } catch (InterruptedException ex) {
            scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        running.set(false);
    }

    /** start the thread to obtain group list information periodically */
    protected void start() {
        if (running.get()) {
            logger.warn("GroupManagerService has already been started!");
            return;
        }
        logger.debug("start GroupManagerService...");
        running.set(true);
        // heartbeat: 3 s
        scheduledExecutorService.scheduleAtFixedRate(
                () -> fetchGroupList(), 0, fetchGroupListIntervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateGroupInfo(String peerIpAndPort, List<String> groupList) {
        nodeToGroupIDList.put(peerIpAndPort, groupList);
        for (String groupIdStr : groupList) {
            Integer groupId = Integer.valueOf(groupIdStr);
            if (groupId == null) {
                continue;
            }
            // create groupService for the new groupId
            if (tryToCreateGroupService(peerIpAndPort, groupId)) {
                return;
            }
            // update the group information
            groupIdToService.get(groupId).insertNode(peerIpAndPort);
        }
        logger.debug("update groupInfo for {}, groupList: {}", peerIpAndPort, groupList);
    }

    @Override
    public void updateBlockNumberInfo(String peerIpAndPort, Message blockNumberNotifyMessage) {
        BlockNumberNotification blockNumberInfo =
                blockNumberMessageDecoder.decode(blockNumberNotifyMessage);
        if (blockNumberInfo == null) {
            return;
        }
        if (!StringUtils.isNumeric(blockNumberInfo.getGroupId())
                || !StringUtils.isNumeric(blockNumberInfo.getBlockNumber())) {
            logger.warn(
                    "updateBlockNumberInfo for invalid block number info, peer:{}, groupId: {}, blockNumber:{}",
                    peerIpAndPort,
                    blockNumberInfo.getGroupId(),
                    blockNumberInfo.getBlockNumber());
            return;
        }
        // set the block number
        updateBlockNumberInfo(
                Integer.valueOf(blockNumberInfo.getGroupId()),
                peerIpAndPort,
                new BigInteger(blockNumberInfo.getBlockNumber()));
    }

    @Override
    public void updateBlockNumberInfo(
            Integer groupId, String peerInfo, BigInteger currentBlockNumber) {
        tryToCreateGroupService(peerInfo, groupId);
        // update the blockNumber Info for the group
        GroupService groupService = groupIdToService.get(groupId);
        groupService.updatePeersBlockNumberInfo(peerInfo, currentBlockNumber);
    }

    private boolean tryToCreateGroupService(String peerIpAndPort, Integer groupId) {
        // create groupService for the new groupId
        if (!groupIdToService.containsKey(groupId)) {
            groupIdToService.put(
                    groupId, this.groupServiceFactory.createGroupSerivce(groupId, peerIpAndPort));
            return true;
        }
        return false;
    }

    @Override
    public BigInteger getBlockLimitByGroup(Integer groupId) {
        if (!groupIdToService.containsKey(groupId)) {
            return BLOCK_LIMIT;
        }
        return groupIdToService.get(groupId).getLastestBlockNumber().add(BLOCK_LIMIT);
    }

    @Override
    public Set<String> getGroupNodeList(Integer groupId) {
        if (!groupIdToService.containsKey(groupId)) {
            return new HashSet<>();
        }
        return groupIdToService.get(groupId).getGroupNodesInfo();
    }

    @Override
    public List<String> getGroupInfoByNodeInfo(String nodeAddress) {
        if (!nodeToGroupIDList.containsKey(nodeAddress)) {
            return new ArrayList<>();
        }
        return nodeToGroupIDList.get(nodeAddress);
    }

    private boolean checkGroupStatus(Integer groupId) {
        if (!groupIdToService.containsKey(groupId)) {
            logger.warn("checkGroupStatus failed for group {} doesn't exist", groupId);
            return false;
        }
        return true;
    }

    @Override
    public Response sendMessageToGroup(Integer groupId, Message message) {
        if (!checkGroupStatus(groupId)) {
            return null;
        }
        // get the node with the latest block number
        String targetNode = groupIdToService.get(groupId).getNodeWithTheLatestBlockNumber();
        logger.trace(
                "g:{}, sendMessageToGroup, selectedPeer: {}, message type: {}, seq: {}, length:{}",
                groupId,
                targetNode,
                message.getType(),
                message.getSeq(),
                message.getLength());
        return this.channel.sendToPeer(message, targetNode);
    }

    @Override
    public void asyncSendMessageToGroup(
            Integer groupId, Message message, ResponseCallback callback) {
        if (!checkGroupStatus(groupId)) {
            return;
        }
        // get the node with the latest block number
        String targetNode = groupIdToService.get(groupId).getNodeWithTheLatestBlockNumber();
        logger.trace(
                "g:{}, asyncSendMessageToGroup, selectedPeer:{}, message type: {}, seq: {}, length:{}",
                groupId,
                targetNode,
                message.getType(),
                message.getSeq(),
                message.getLength());
        this.channel.asyncSendToPeer(message, targetNode, callback, new Options());
    }

    @Override
    public Response sendMessageToGroupByRule(
            Integer groupId, Message message, PeerSelectRule rule) {
        String selectedPeer = selectGroupPeersByRule(groupId, rule);
        if (selectedPeer == null) {
            logger.warn(
                    "g:{}, sendMessageToGroupByRule, no peer is selected by the rule, message type: {}, seq: {}, length:{}",
                    groupId,
                    message.getType(),
                    message.getSeq(),
                    message.getLength());
            return null;
        }
        logger.debug(
                "g:{}, sendMessageToGroupByRule, send message to {}, selectedPeer: {}, message type: {}, seq: {}, length:{}",
                groupId,
                selectedPeer,
                selectedPeer,
                message.getType(),
                message.getSeq(),
                message.getLength());
        return this.channel.sendToPeer(message, selectedPeer);
    }

    private String selectGroupPeersByRule(Integer groupId, PeerSelectRule rule) {
        if (!checkGroupStatus(groupId)) {
            return null;
        }
        // select nodes with rule
        List<ConnectionInfo> groupConnnectionInfos = getGroupConnectionInfo(groupId);
        return rule.select(groupConnnectionInfos);
    }

    @Override
    public List<ConnectionInfo> getGroupConnectionInfo(Integer groupId) {
        if (!checkGroupStatus(groupId)) {
            return new ArrayList<>();
        }
        return getGroupConnectionInfo(groupIdToService.get(groupId));
    }

    private List<ConnectionInfo> getGroupConnectionInfo(GroupService groupService) {
        List<ConnectionInfo> connectionInfos = this.channel.getConnectionInfo();
        List<ConnectionInfo> groupConnectionInfos = new ArrayList<>();
        for (ConnectionInfo connectionInfo : connectionInfos) {
            if (groupService.existPeer(connectionInfo.getEndPoint())) {
                groupConnectionInfos.add(connectionInfo);
            }
        }
        return groupConnectionInfos;
    }

    @Override
    public List<String> getGroupAvailablePeers(Integer groupId) {
        if (!checkGroupStatus(groupId)) {
            return new ArrayList<>();
        }
        return getGroupAvailablePeers(groupIdToService.get(groupId));
    }

    private List<String> getGroupAvailablePeers(GroupService groupService) {
        List<String> availablePeers = this.channel.getAvailablePeer();
        List<String> groupAvailablePeers = new ArrayList<>();
        // filter the available peers of the given group
        for (String peer : availablePeers) {
            if (groupService.existPeer(peer)) {
                groupAvailablePeers.add(peer);
            }
        }
        return groupAvailablePeers;
    }

    @Override
    public void asyncSendMessageToGroupByRule(
            Integer groupId, Message message, PeerSelectRule rule, ResponseCallback callback) {
        String errorMessage;
        if (!checkGroupStatus(groupId)) {
            errorMessage =
                    "asyncSendMessageToGroupByRule to "
                            + groupId
                            + " failed for the group doesn't exit, message seq: "
                            + message.getSeq();
            callback.onError(errorMessage);
        }
        // select nodes with rule
        String selectedPeer = selectGroupPeersByRule(groupId, rule);
        if (selectedPeer == null) {
            logger.warn(
                    "g:{}, asyncSendMessageToGroup, no peer is selected by the rule, message type: {}, seq: {}, length:{}",
                    groupId,
                    message.getType(),
                    message.getSeq(),
                    message.getLength());
            errorMessage =
                    "asyncSendMessageToGroupByRule to "
                            + groupId
                            + " failed for no peer is selected by the rule";
            callback.onError(errorMessage);
            return;
        }
        logger.trace(
                "g:{}, asyncSendMessageToGroupByRule, selectedPeer: {}, message type: {}, seq: {}, length:{}",
                groupId,
                selectedPeer,
                message.getType(),
                message.getSeq(),
                message.getLength());
        this.channel.asyncSendToPeer(message, selectedPeer, callback, new Options());
    }

    @Override
    public void broadcastMessageToGroup(Integer groupId, Message message) {
        // get the group connections
        List<ConnectionInfo> groupConnnectionInfos = getGroupConnectionInfo(groupId);
        if (groupConnnectionInfos == null) {
            logger.warn(
                    "g:{}, broadcastMessageToGroup,  broadcast message failed for the group has no connected peers, message type: {}, seq: {}, length:{}",
                    groupId,
                    message.getType(),
                    message.getSeq(),
                    message.getLength());
            return;
        }
        for (ConnectionInfo connectionInfo : groupConnnectionInfos) {
            this.channel.asyncSendToPeer(
                    message, connectionInfo.getEndPoint(), null, new Options());
        }
    }

    // fetch the groupIDList from all the peers
    protected void fetchGroupList() {
        List<String> peers = this.channel.getAvailablePeer();
        for (String peerEndPoint : peers) {
            try {
                GroupList groupList = this.groupInfoGetter.getGroupList(peerEndPoint);
                this.updateGroupInfo(peerEndPoint, groupList.getGroupList());
            } catch (ClientException e) {
                logger.warn("fetchGroupList from failed, error info: {}", e.getMessage());
            }
        }
    }
}
