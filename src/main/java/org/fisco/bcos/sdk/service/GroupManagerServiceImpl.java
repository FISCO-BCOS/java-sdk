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

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.io.IOException;
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
import java.util.function.Consumer;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.PeerSelectRule;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.ChannelMessageError;
import org.fisco.bcos.sdk.channel.model.EnumChannelProtocolVersion;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.handler.BlockNumberNotifyHandler;
import org.fisco.bcos.sdk.client.handler.GetNodeVersionHandler;
import org.fisco.bcos.sdk.client.handler.OnReceiveBlockNotifyFunc;
import org.fisco.bcos.sdk.client.handler.TransactionNotifyHandler;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.GroupList;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.service.model.BlockNumberMessageDecoder;
import org.fisco.bcos.sdk.service.model.BlockNumberNotification;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupManagerServiceImpl implements GroupManagerService {
    public static final String SM_CRYPTO_STR = "gm";

    private static Logger logger = LoggerFactory.getLogger(GroupManagerServiceImpl.class);
    private final Channel channel;
    private final BlockNumberMessageDecoder blockNumberMessageDecoder;
    private final GroupServiceFactory groupServiceFactory;
    private ConcurrentHashMap<Integer, GroupService> groupIdToService = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<String>> nodeToGroupIDList = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, NodeVersion> nodeToNodeVersion = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, TransactionCallback> seq2TransactionCallback =
            new ConcurrentHashMap<>();
    private final Timer timeoutHandler = new HashedWheelTimer();

    private Client groupInfoGetter;
    private long fetchGroupListIntervalMs = 60000;
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    // the thread pool is used to handle the block_notify message and transaction_notify message
    private final ThreadPoolService threadPool;
    AtomicBoolean running = new AtomicBoolean(false);

    private final ConfigOption config;

    public GroupManagerServiceImpl(Channel channel, ConfigOption configOption) {
        this.channel = channel;
        this.config = configOption;
        this.threadPool =
                new ThreadPoolService(
                        "GroupManagerServiceImpl",
                        configOption.getThreadPoolConfig().getReceiptProcessorThreadSize(),
                        configOption.getThreadPoolConfig().getMaxBlockingQueueSize());
        this.blockNumberMessageDecoder = new BlockNumberMessageDecoder();
        this.groupServiceFactory = new GroupServiceFactory();
        this.groupInfoGetter = Client.build(channel);
        // Note: must register the handlers at first
        registerGetNodeVersionHandler();
        registerBlockNumberNotifyHandler();
        registerTransactionNotifyHandler();
        fetchGroupList();
        updateNodeVersion();
        this.start();
    }

    @Override
    public ConfigOption getConfig() {
        return this.config;
    }

    @Override
    public Integer getCryptoType(String peerInfo) {
        if (!nodeToNodeVersion.containsKey(peerInfo)) {
            return null;
        }
        NodeVersion nodeVersion = nodeToNodeVersion.get(peerInfo);
        if (nodeVersion.getNodeVersion().getVersion().contains(SM_CRYPTO_STR)) {
            return CryptoInterface.SM_TYPE;
        }
        return CryptoInterface.ECDSA_TYPE;
    }

    @Override
    public NodeVersion getNodeVersion(String peerInfo) {
        if (!nodeToNodeVersion.containsKey(peerInfo)) {
            return null;
        }
        return nodeToNodeVersion.get(peerInfo);
    }

    @Override
    public void updateNodeVersion() {
        List<String> peers = this.channel.getAvailablePeer();
        for (String peer : peers) {
            updateNodeVersion(peer);
        }
    }

    private void updateNodeVersion(String peerIpAndPort) {
        try {
            NodeVersion nodeVersion = groupInfoGetter.getNodeVersion(peerIpAndPort);
            nodeToNodeVersion.put(peerIpAndPort, nodeVersion);
        } catch (Exception e) {
            logger.error(
                    "updateNodeVersion for {} failed, error message: {}",
                    peerIpAndPort,
                    e.getMessage());
        }
    }

    public void registerGetNodeVersionHandler() {
        GetNodeVersionHandler handler =
                new GetNodeVersionHandler(
                        new Consumer<String>() {
                            @Override
                            public void accept(String peerIpAndPort) {
                                threadPool
                                        .getThreadPool()
                                        .execute(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        fetchGroupList(peerIpAndPort);
                                                        updateNodeVersion(peerIpAndPort);
                                                    }
                                                });
                            }
                        });
        this.channel.addEstablishHandler(handler);
    }

    private void onDisconnect(String peerIpAndPort) {
        nodeToNodeVersion.remove(peerIpAndPort);
        if (!nodeToGroupIDList.containsKey(peerIpAndPort)) {
            return;
        }
        List<String> groupList = nodeToGroupIDList.get(peerIpAndPort);
        for (String group : groupList) {
            GroupService groupService = groupIdToService.get(Integer.valueOf(group));
            if (groupService == null) {
                continue;
            }
            groupService.removeNode(peerIpAndPort);
        }
    }

    public void registerBlockNumberNotifyHandler() {
        OnReceiveBlockNotifyFunc onReceiveBlockNotifyFunc =
                (version, peerIpAndPort, blockNumberNotifyMessage) ->
                        threadPool
                                .getThreadPool()
                                .execute(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                onReceiveBlockNotifyImpl(
                                                        version,
                                                        peerIpAndPort,
                                                        blockNumberNotifyMessage);
                                            }
                                        });
        BlockNumberNotifyHandler handler =
                new BlockNumberNotifyHandler(
                        onReceiveBlockNotifyFunc,
                        new Consumer<String>() {
                            @Override
                            public void accept(String disconnectedEndpoint) {
                                threadPool
                                        .getThreadPool()
                                        .execute(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        onDisconnect(disconnectedEndpoint);
                                                    }
                                                });
                            }
                        });
        this.channel.addMessageHandler(MsgType.BLOCK_NOTIFY, handler);
        this.channel.addDisconnectHandler(handler);
        logger.info("registerBlockNumberNotifyHandler");
    }

    public void registerTransactionNotifyHandler() {
        TransactionNotifyHandler handler =
                new TransactionNotifyHandler(
                        new Consumer<Message>() {
                            @Override
                            public void accept(Message message) {
                                threadPool
                                        .getThreadPool()
                                        .execute(
                                                new Runnable() {
                                                    // decode the message into transaction
                                                    @Override
                                                    public void run() {
                                                        onReceiveTransactionNotify(message);
                                                    }
                                                });
                            }
                        });
        this.channel.addMessageHandler(MsgType.TRANSACTION_NOTIFY, handler);
        logger.info("registerTransactionNotifyHandler");
    }

    /**
     * Get the blockNumber notify message from the AMOP module, parse the package and update the
     * latest block height of each group
     *
     * @param peerIpAndPort: Node ip and port
     * @param blockNumberNotifyMessage: the blockNumber notify message
     */
    protected void onReceiveBlockNotifyImpl(
            EnumChannelProtocolVersion version,
            String peerIpAndPort,
            Message blockNumberNotifyMessage) {
        try {
            BlockNumberNotification blockNumberInfo =
                    blockNumberMessageDecoder.decode(version, blockNumberNotifyMessage);
            if (blockNumberInfo == null) {
                return;
            }

            // set the block number
            updateBlockNumberInfo(
                    Integer.valueOf(blockNumberInfo.getGroupId()),
                    peerIpAndPort,
                    new BigInteger(blockNumberInfo.getBlockNumber()));
        } catch (Exception e) {
            logger.error("onReceiveBlockNotify failed, error message: {}", e.getMessage());
        }
    }

    /**
     * calls the transaction callback when receive the transaction notify
     *
     * @param message: the message contains the transactionReceipt
     */
    protected void onReceiveTransactionNotify(Message message) {
        String seq = message.getSeq();
        // get the transaction callback
        TransactionCallback callback = seq2TransactionCallback.get(seq);
        // remove the callback
        seq2TransactionCallback.remove(seq);
        if (callback == null) {
            logger.error("transaction callback is null, seq: {}", seq);
            return;
        }
        callback.cancelTimeout();
        // decode the message into receipt
        TransactionReceipt receipt = null;
        try {
            receipt =
                    ObjectMapperFactory.getObjectMapper()
                            .readValue(message.getData(), TransactionReceipt.class);
        } catch (IOException e) {
            // fake the receipt
            receipt = new TransactionReceipt();
            receipt.setStatus(String.valueOf(ChannelMessageError.MESSAGE_DECODE_ERROR.getError()));
            receipt.setMessage(
                    "Decode receipt error, seq: " + seq + ", reason: " + e.getLocalizedMessage());
        }
        callback.onResponse(receipt);
    }

    @Override
    public void asyncSendTransaction(
            Integer groupId,
            Message transactionMessage,
            TransactionCallback callback,
            ResponseCallback responseCallback) {
        if (callback.getTimeout() > 0) {
            callback.setTimeoutHandler(
                    timeoutHandler.newTimeout(
                            new TimerTask() {
                                @Override
                                public void run(Timeout timeout) throws Exception {
                                    callback.onTimeout();
                                    logger.info(
                                            "Transaction timeout: {}", transactionMessage.getSeq());
                                    seq2TransactionCallback.remove(transactionMessage.getSeq());
                                }
                            },
                            callback.getTimeout(),
                            TimeUnit.MILLISECONDS));
        }
        seq2TransactionCallback.put(transactionMessage.getSeq(), callback);
        asyncSendMessageToGroup(groupId, transactionMessage, responseCallback);
    }

    @Override
    public void eraseTransactionSeq(String seq) {
        if (seq != null && seq2TransactionCallback.containsKey(seq)) {
            seq2TransactionCallback.remove(seq);
        }
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    /** Stop group list fetching thread */
    @Override
    public void stop() {
        if (!running.get()) {
            logger.warn("GroupManagerService has already been stopped!");
            return;
        }
        logger.debug("stop GroupManagerService...");
        timeoutHandler.stop();
        ThreadPoolService.stopThreadPool(scheduledExecutorService);
        threadPool.stop();
        logger.debug("stop GroupManagerService succ...");
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
        List<String> orgGroupList = nodeToGroupIDList.get(peerIpAndPort);
        if (orgGroupList != null) {
            for (int i = 0; i < orgGroupList.size(); i++) {
                Integer groupId = Integer.valueOf(orgGroupList.get(i));
                if (!groupList.contains(orgGroupList.get(i))
                        && groupIdToService.containsKey(groupId)) {
                    groupIdToService.get(groupId).removeNode(peerIpAndPort);
                    logger.info("remove group {} from {}", orgGroupList.get(i), peerIpAndPort);
                }
            }
        }
        nodeToGroupIDList.put(peerIpAndPort, groupList);

        for (String groupIdStr : groupList) {
            Integer groupId = Integer.valueOf(groupIdStr);
            if (groupId == null) {
                continue;
            }
            // create groupService for the new groupId
            if (tryToCreateGroupService(peerIpAndPort, groupId)) {
                // fetch the block number information for the group
                getBlockLimitByGroup(groupId);
                continue;
            }
            // update the group information
            groupIdToService.get(groupId).insertNode(peerIpAndPort);
        }
        logger.trace("update groupInfo for {}, groupList: {}", peerIpAndPort, groupList);
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
        if (groupIdToService.containsKey(groupId)
                && groupIdToService.get(groupId).getLastestBlockNumber().equals(BigInteger.ZERO)) {
            Pair<String, BigInteger> blockNumberInfo = getBlockNumberByRandom(groupId);
            if (blockNumberInfo == null) {
                logger.warn(
                        "GetBlockNumber for group {} failed, set blockLimit to {}",
                        groupId,
                        GroupManagerService.BLOCK_LIMIT);
                return GroupManagerService.BLOCK_LIMIT;
            }
            // update the block number information
            updateBlockNumberInfo(groupId, blockNumberInfo.getKey(), blockNumberInfo.getValue());
            logger.debug(
                    "update the blockNumber information, groupId: {}, peer:{}, blockNumber: {}",
                    groupId,
                    blockNumberInfo.getKey(),
                    blockNumberInfo.getValue());
        }
        return groupIdToService.get(groupId).getLastestBlockNumber().add(BLOCK_LIMIT);
    }

    private Pair<String, BigInteger> getBlockNumberByRandom(Integer groupId) {
        List<String> availablePeers = getGroupAvailablePeers(groupId);
        for (String peer : availablePeers) {
            try {
                BlockNumber blockNumber = this.groupInfoGetter.getBlockNumber(groupId, peer);
                return new MutablePair<>(peer, blockNumber.getBlockNumber());
            } catch (ClientException e) {
                logger.error(
                        "GetBlockNumber from {} failed, error information:{}",
                        peer,
                        e.getMessage());
                continue;
            }
        }
        return null;
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
        if (targetNode == null) {
            logger.error(
                    "sendMessageToGroup message failed for get the node with the latest block number failed, groupId: {}, seq: {}, type: {}",
                    groupId,
                    message.getSeq(),
                    message.getType());
            return null;
        }
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
        if (targetNode == null) {
            logger.warn(
                    "g:{}, asyncSendMessageToGroup, selectedPeer failed, seq: {}, type: {}",
                    groupId,
                    message.getSeq(),
                    message.getType());
            throw new ClientException(
                    "asyncSendMessageToGroup to "
                            + groupId
                            + " failed for selectPeer failed, messageSeq: "
                            + message.getSeq());
        }
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
    @Override
    public void fetchGroupList() {
        List<String> peers = this.channel.getAvailablePeer();
        for (String peerEndPoint : peers) {
            fetchGroupList(peerEndPoint);
        }
    }

    private void fetchGroupList(String peerEndPoint) {
        try {
            GroupList groupList = this.groupInfoGetter.getGroupList(peerEndPoint);
            this.updateGroupInfo(peerEndPoint, groupList.getGroupList());
        } catch (ClientException e) {
            logger.warn(
                    "fetchGroupList from {} failed, error info: {}", peerEndPoint, e.getMessage());
        }
    }

    @Override
    public void resetLatestNodeInfo(Integer groupId) {
        GroupService groupService = this.groupIdToService.get(groupId);
        if (groupService != null) {
            groupService.resetLatestNodeInfo();
        }
    }
}
