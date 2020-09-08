package org.fisco.bcos.sdk.amop;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.PeerSelectRule;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.fisco.bcos.sdk.service.callback.BlockNumberNotifyCallback;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;

public class MockGroupManager implements GroupManagerService {
    private Channel ch;

    public MockGroupManager(Channel ch) {
        this.ch = ch;
    }

    @Override
    public void updateGroupInfo(String peerIpAndPort, List<String> groupList) {
        // do nothing
    }

    @Override
    public Channel getChannel() {
        return ch;
    }

    @Override
    public void updateBlockNumberInfo(
            Integer groupId, String peerInfo, BigInteger currentBlockNumber) {
        // do nothing
    }

    @Override
    public List<ConnectionInfo> getGroupConnectionInfo(Integer groupId) {
        return null;
    }

    @Override
    public List<String> getGroupAvailablePeers(Integer groupId) {
        return null;
    }

    @Override
    public BigInteger getBlockLimitByGroup(Integer groupId) {
        return null;
    }

    @Override
    public Set<String> getGroupNodeList(Integer groupId) {
        return null;
    }

    @Override
    public List<String> getGroupInfoByNodeInfo(String nodeAddress) {
        return null;
    }

    @Override
    public Response sendMessageToGroup(Integer groupId, Message message) {
        return null;
    }

    @Override
    public Response sendMessageToGroupByRule(
            Integer groupId, Message message, PeerSelectRule rule) {
        return null;
    }

    @Override
    public void asyncSendMessageToGroup(
            Integer groupId, Message message, ResponseCallback callback) {
        // do nothing
    }

    @Override
    public void asyncSendMessageToGroupByRule(
            Integer groupId, Message message, PeerSelectRule rule, ResponseCallback callback) {
        // do nothing
    }

    @Override
    public void broadcastMessageToGroup(Integer groupId, Message message) {
        // do nothing
    }

    @Override
    public void asyncSendTransaction(
            Integer groupId,
            Message transactionData,
            TransactionCallback callback,
            ResponseCallback responseCallback) {
        // do nothing
    }

    @Override
    public void eraseTransactionSeq(String seq) {
        // do nothing
    }

    @Override
    public NodeVersion getNodeVersion(String peerInfo) {
        return null;
    }

    @Override
    public Integer getCryptoType(String peerInfo) {
        return null;
    }

    @Override
    public ConfigOption getConfig() {
        return null;
    }

    @Override
    public void stop() {
        return;
    }

    @Override
    public void updateNodeVersion() {
        return;
    }

    @Override
    public void fetchGroupList() {
        return;
    }

    @Override
    public void resetLatestNodeInfo(Integer groupId) {
        return;
    }

    @Override
    public BigInteger getLatestBlockNumberByGroup(Integer groupId) {
        return BigInteger.ZERO;
    }

    @Override
    public String registerBlockNotifyCallback(BlockNumberNotifyCallback callback) {
        return "";
    }

    @Override
    public void eraseBlockNotifyCallback(String registerId) {
        return;
    }
}
