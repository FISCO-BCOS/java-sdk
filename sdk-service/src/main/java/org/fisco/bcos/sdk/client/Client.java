/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.client;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceiptsDecoder;
import org.fisco.bcos.sdk.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.client.protocol.response.Code;
import org.fisco.bcos.sdk.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.client.protocol.response.GenerateGroup;
import org.fisco.bcos.sdk.client.protocol.response.GroupList;
import org.fisco.bcos.sdk.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.client.protocol.response.NodeIDList;
import org.fisco.bcos.sdk.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.client.protocol.response.Peers;
import org.fisco.bcos.sdk.client.protocol.response.PendingTransactions;
import org.fisco.bcos.sdk.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.client.protocol.response.QueryGroupStatus;
import org.fisco.bcos.sdk.client.protocol.response.RecoverGroup;
import org.fisco.bcos.sdk.client.protocol.response.RemoveGroup;
import org.fisco.bcos.sdk.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.client.protocol.response.SendTransaction;
import org.fisco.bcos.sdk.client.protocol.response.StartGroup;
import org.fisco.bcos.sdk.client.protocol.response.StopGroup;
import org.fisco.bcos.sdk.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.client.protocol.response.TopicSubscribers;
import org.fisco.bcos.sdk.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.client.protocol.response.TransactionReceiptWithProof;
import org.fisco.bcos.sdk.client.protocol.response.TransactionWithProof;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.eventsub.EventResource;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the interface of client module.
 *
 * @author Maggie
 */
public interface Client {
    static Logger logger = LoggerFactory.getLogger(Client.class);

    /**
     * Build a client instance GroupId is identified, all interfaces are available
     *
     * @param channel the Channel instance
     * @param groupId the group id
     * @param groupManagerService the groupManagerService instance
     * @param eventResource the eventResource instance
     * @return a client instance
     */
    static Client build(
            GroupManagerService groupManagerService,
            Channel channel,
            EventResource eventResource,
            Integer groupId) {
        groupManagerService.fetchGroupList();
        groupManagerService.updateNodeVersion();
        // check the groupList
        Set<String> nodeList = groupManagerService.getGroupNodeList(groupId);
        if (nodeList == null || nodeList.size() == 0) {
            logger.warn("build client failed for no peers setup the group {}", groupId);
            return null;
        }
        // get cryptoType
        Integer cryptoType = null;
        NodeVersion nodeVersion = null;
        for (String node : nodeList) {
            cryptoType = groupManagerService.getCryptoType(node);
            if (cryptoType != null) {
                nodeVersion = groupManagerService.getNodeVersion(node);
                break;
            }
        }
        if (cryptoType == null || nodeVersion == null) {
            logger.warn(
                    "build client failed for get crypto type or nodeVersion failed, groupId: {}",
                    groupId);
            return null;
        }
        CryptoSuite cryptoSuite = new CryptoSuite(cryptoType, groupManagerService.getConfig());
        logger.info("build client success for group {}", groupId);
        return new ClientImpl(
                groupManagerService, channel, groupId, cryptoSuite, nodeVersion, eventResource);
    }

    static Client build(Channel channel) {
        return new ClientImpl(channel);
    }

    GroupManagerService getGroupManagerService();

    CryptoSuite getCryptoSuite();

    NodeVersion getClientNodeVersion();

    Integer getCryptoType();

    /**
     * get groupId of the client
     *
     * @return the groupId
     */
    Integer getGroupId();

    /**
     * Ledger operation: send transaction
     *
     * @param signedTransactionData transaction string
     * @return SendTransaction
     */
    SendTransaction sendRawTransaction(String signedTransactionData);

    /**
     * Ledger operation: async send transaction
     *
     * @param signedTransactionData transaction string
     * @param callback the callback that will be called when receive the response
     */
    void sendRawTransactionAsync(
            String signedTransactionData, RespCallback<SendTransaction> callback);

    /**
     * Ledger operation: send raw transaction and get proof
     *
     * @param signedTransactionData transaction string
     * @return a SendTransaction instance
     */
    SendTransaction sendRawTransactionAndGetProof(String signedTransactionData);

    /**
     * Ledger operation: async send transaction and get proof
     *
     * @param signedTransactionData transaction string
     * @param callback the callback that will be called when receive the response
     */
    void sendRawTransactionAndGetProofAsync(
            String signedTransactionData, RespCallback<SendTransaction> callback);

    /**
     * send transaction and get the receipt as the response
     *
     * @param signedTransactionData the transaction data sent to the node
     * @return the transaction receipt
     */
    TransactionReceipt sendRawTransactionAndGetReceipt(String signedTransactionData);

    /**
     * send transaction to the node, and calls TransactionCallback when get the transaction receipt
     * response
     *
     * @param signedTransactionData the transaction sent to the node
     * @param callback the TransactionCallback called after get the transaction receipt
     */
    void sendRawTransactionAndGetReceiptAsync(
            String signedTransactionData, TransactionCallback callback);

    /**
     * calls sendRawTransactionAndGetProof interface and get the transaction receipt
     *
     * @param signedTransactionData the transaction sent to the node
     * @return the transaction receipt
     */
    TransactionReceipt sendRawTransactionAndGetReceiptWithProof(String signedTransactionData);

    /**
     * calls sendRawTransactionAndGetProof interface, calls TransactionCallback when get the
     * transaction receipt
     *
     * @param signedTransactionData the transaction sent to the node
     * @param callback the TransactionCallback called after get the transaction receipt
     */
    void sendRawTransactionAndGetReceiptWithProofAsync(
            String signedTransactionData, TransactionCallback callback);

    /**
     * Ledger operation: call contract functions without sending transaction
     *
     * @param transaction transaction instance
     * @return Call
     */
    Call call(Transaction transaction);

    /**
     * Ledger operation: async call contract functions without sending transaction
     *
     * @param transaction transaction instance
     * @param callback the callback that will be called when receive the response
     */
    void callAsync(Transaction transaction, RespCallback<Call> callback);

    /**
     * Ledger operation: get block number
     *
     * @return block number
     */
    BlockNumber getBlockNumber();

    BlockNumber getBlockNumber(Integer groupId, String peerIpAndPort);

    /**
     * Ledger operation: async get block number
     *
     * @param callback the callback that will be called when receive the response
     */
    void getBlockNumberAsync(RespCallback<BlockNumber> callback);

    /**
     * Ledger operation: get code
     *
     * @param address the address string
     * @return a code instance
     */
    Code getCode(String address);

    /**
     * Ledger operation: async get code
     *
     * @param address the address string
     * @param callback the callback that will be called when receive the response
     */
    void getCodeAsync(String address, RespCallback<Code> callback);

    /**
     * Ledger operation: get total transaction count
     *
     * @return TotalTransactionCount
     */
    TotalTransactionCount getTotalTransactionCount();

    /**
     * Ledger operation: async get total transaction count
     *
     * @param callback the callback that will be called when receive the response
     */
    void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback);

    /**
     * Ledger operation: get block by hash
     *
     * @param blockHash the hashcode of the block
     * @param returnFullTransactionObjects the boolean define the tx is full or not
     * @return a block
     */
    BcosBlock getBlockByHash(String blockHash, boolean returnFullTransactionObjects);

    /**
     * Ledger operation: async get block by hash
     *
     * @param blockHash the hashcode of the block
     * @param returnFullTransactionObjects the boolean define the tx is full or not
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByHashAsync(
            String blockHash,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: get block by block number
     *
     * @param blockNumber the number of the block
     * @param returnFullTransactionObjects the boolean define the tx is full or not
     * @return block
     */
    BcosBlock getBlockByNumber(BigInteger blockNumber, boolean returnFullTransactionObjects);

    /**
     * Ledger operation: async get block by block number
     *
     * @param blockNumber the number of the block
     * @param returnFullTransactionObjects the boolean define the tx is full or not
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: get block hash by block number
     *
     * @param blockNumber the number of the block
     * @return block hash
     */
    BlockHash getBlockHashByNumber(BigInteger blockNumber);

    /**
     * Ledger operation: async get block hash by block number
     *
     * @param blockNumber the number of the block
     * @param callback the callback that will be called when receive the response
     */
    void getBlockHashByNumberAsync(BigInteger blockNumber, RespCallback<BlockHash> callback);

    /**
     * Ledger operation: get block header by block hash
     *
     * @param blockHash the hashcode of the block
     * @param returnSignatureList the boolean define the signature list is returned or not
     * @return block header
     */
    BcosBlockHeader getBlockHeaderByHash(String blockHash, boolean returnSignatureList);

    /**
     * Ledger operation: async get block header by block hash
     *
     * @param blockHash the hashcode of the block
     * @param returnSignatureList the boolean define the signature list is returned or not
     * @param callback the call back instance
     */
    void getBlockHeaderByHashAsync(
            String blockHash, boolean returnSignatureList, RespCallback<BcosBlockHeader> callback);

    /**
     * get block header by number
     *
     * @param blockNumber the number of the block
     * @param returnSignatureList the boolean define the signature list is returned or not
     * @return the block header response from the blockchain node
     */
    BcosBlockHeader getBlockHeaderByNumber(BigInteger blockNumber, boolean returnSignatureList);

    void getBlockHeaderByNumberAsync(
            BigInteger blockNumber,
            boolean returnSignatureList,
            RespCallback<BcosBlockHeader> callback);

    /**
     * Ledger operation: get trnasaction by hash
     *
     * @param transactionHash the hashcode of transaction
     * @return transaction
     */
    BcosTransaction getTransactionByHash(String transactionHash);

    /**
     * Ledger operation: async get trnasaction by hash
     *
     * @param transactionHash the hashcode of transaction
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionByHashAsync(String transactionHash, RespCallback<BcosTransaction> callback);

    /**
     * Ledger operation: get transaction and proof by hash
     *
     * @param transactionHash the hashcode of transaction
     * @return transaction with proof
     */
    TransactionWithProof getTransactionByHashWithProof(String transactionHash);

    /**
     * Ledger operation: async get transaction and proof by hash
     *
     * @param transactionHash the hashcode of transaction
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionByHashWithProofAsync(
            String transactionHash, RespCallback<TransactionWithProof> callback);

    /**
     * Ledger operation: get transaction by block number and index
     *
     * @param blockNumber the number of block
     * @param transactionIndex the index of transaction
     * @return transaction
     */
    BcosTransaction getTransactionByBlockNumberAndIndex(
            BigInteger blockNumber, BigInteger transactionIndex);

    /**
     * Ledger operation: async get transaction by block number and index
     *
     * @param blockNumber the number of block
     * @param transactionIndex the index of transaction
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionByBlockNumberAndIndexAsync(
            BigInteger blockNumber,
            BigInteger transactionIndex,
            RespCallback<BcosTransaction> callback);

    BcosTransaction getTransactionByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex);

    void getTransactionByBlockHashAndIndexAsync(
            String blockHash, BigInteger transactionIndex, RespCallback<BcosTransaction> callback);

    /**
     * Ledger operation: get transaction receipt by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @return transaction receipt
     */
    BcosTransactionReceipt getTransactionReceipt(String transactionHash);

    /**
     * Ledger operation: async get transaction receipt by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionReceiptAsync(
            String transactionHash, RespCallback<BcosTransactionReceipt> callback);

    /**
     * Ledger operation: get transaction receipt and proof by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @return receipt and proof
     */
    TransactionReceiptWithProof getTransactionReceiptByHashWithProof(String transactionHash);

    /**
     * Ledger operation: async get transaction receipt and proof by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionReceiptByHashWithProofAsync(
            String transactionHash, RespCallback<TransactionReceiptWithProof> callback);

    /**
     * Ledger operation: get pending transactions in transaction pool
     *
     * @return pending transactions
     */
    PendingTransactions getPendingTransaction();

    /**
     * Ledger operation: async get pending transactions in transaction pool
     *
     * @param callback the callback that will be called when receive the response
     */
    void getPendingTransactionAsync(RespCallback<PendingTransactions> callback);

    /**
     * Ledger operation: get pending transaction size
     *
     * @return PendingTxSize
     */
    PendingTxSize getPendingTxSize();

    /**
     * Ledger operation: async get pending transaction size
     *
     * @param callback the callback that will be called when receive the response
     */
    void getPendingTxSizeAsync(RespCallback<PendingTxSize> callback);

    /**
     * Get cached block height
     *
     * @return block number
     */
    BigInteger getBlockLimit();

    /**
     * Group operation: generate a new group
     *
     * @param groupId the group id
     * @param timestamp timestamp
     * @param enableFreeStorage enable free storage
     * @param nodeList give the ip string list of the nodes in the group
     * @param peerIpPort send to the specific peer
     * @return generate group reply message
     */
    GenerateGroup generateGroup(
            Integer groupId,
            long timestamp,
            boolean enableFreeStorage,
            List<String> nodeList,
            String peerIpPort);

    /**
     * Group operation: async generate a new group
     *
     * @param groupId the group id
     * @param timestamp timestamp
     * @param enableFreeStorage enable free storage
     * @param nodeList the list of the nodes in the group
     * @param peerIpPort send to the specific peer
     * @param callback the callback that will be called when receive the response
     */
    void generateGroupAsync(
            Integer groupId,
            long timestamp,
            boolean enableFreeStorage,
            List<String> nodeList,
            String peerIpPort,
            RespCallback<GenerateGroup> callback);

    /**
     * Group operation: start a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @return start group rpc reply
     */
    StartGroup startGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async start a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @param callback the callback that will be called when receive the response
     */
    void startGroupAsync(Integer groupId, String peerIpPort, RespCallback<StartGroup> callback);

    /**
     * Group operation: stop a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @return stop group rpc reply
     */
    StopGroup stopGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async stop a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @param callback the callback that will be called when receive the response
     */
    void stopGroupAsync(Integer groupId, String peerIpPort, RespCallback<StopGroup> callback);

    /**
     * Group operation: remove a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @return remove group rpc reply
     */
    RemoveGroup removeGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async remove a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @param callback the callback that will be called when receive the response
     */
    void removeGroupAsync(Integer groupId, String peerIpPort, RespCallback<RemoveGroup> callback);

    /**
     * Group operation: recover a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @return recover group rpc reply
     */
    RecoverGroup recoverGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async recover a group
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @param callback the callback that will be called when receive the response
     */
    void recoverGroupAsync(Integer groupId, String peerIpPort, RespCallback<RecoverGroup> callback);

    /**
     * Group operation: query group status
     *
     * @param groupId the group id
     * @return group status
     */
    QueryGroupStatus queryGroupStatus(Integer groupId);

    /**
     * Group operation: query group status
     *
     * @param groupId the group id
     * @param peerIpPort the node that the request sent to
     * @return group status
     */
    QueryGroupStatus queryGroupStatus(Integer groupId, String peerIpPort);

    /**
     * Group operation: async query group status
     *
     * @param groupId the group id
     * @param callback the callback that will be called when receive the response
     */
    void queryGroupStatusAsync(Integer groupId, RespCallback<QueryGroupStatus> callback);

    /**
     * Group operation: async query group status
     *
     * @param groupId the group that the request sent to
     * @param peerIpPort the node that the request sent to
     * @param callback the callback that will be called when receive the response
     */
    void queryGroupStatusAsync(
            Integer groupId, String peerIpPort, RespCallback<QueryGroupStatus> callback);

    /**
     * Group operation: get peer group list
     *
     * @return group list
     */
    GroupList getGroupList();

    /**
     * Group operation: get peer group list
     *
     * @param peerIpPort send to the specific peer
     * @return group list
     */
    GroupList getGroupList(String peerIpPort);

    /**
     * Group operation: async get peer group list
     *
     * @param callback the callback that will be called when receive the response
     */
    void getGroupListAsync(RespCallback<GroupList> callback);

    /**
     * Group operation: async get peer group list
     *
     * @param peerIpPort send to the specific peer
     * @param callback the callback that will be called when receive the response
     */
    void getGroupListAsync(String peerIpPort, RespCallback<GroupList> callback);

    /**
     * Group operation: get group peers
     *
     * @return group peers
     */
    GroupPeers getGroupPeers();

    /**
     * Group operation: get group peers
     *
     * @param peerIpPort the target node of the request
     * @return group peers
     */
    GroupPeers getGroupPeers(String peerIpPort);

    /**
     * Group operation: async get group peers
     *
     * @param callback the callback that will be called when receive the response
     */
    void getGroupPeersAsync(RespCallback<GroupPeers> callback);

    /**
     * Group operation: async get group peers
     *
     * @param peerIpPort the target node of the request
     * @param callback the callback that will be called when receive the response
     */
    void getGroupPeersAsync(String peerIpPort, RespCallback<GroupPeers> callback);

    /**
     * Peer operation: get connected peers
     *
     * @return peers
     */
    Peers getPeers();

    /**
     * Peer operation: get connected peers
     *
     * @param endpoint the target node that receive the request
     * @return peers
     */
    Peers getPeers(String endpoint);

    /**
     * Peer operation: async get connected peers
     *
     * @param callback the callback instance
     */
    void getPeersAsync(RespCallback<Peers> callback);

    /**
     * Peer operation: get node ids
     *
     * @return node id list
     */
    NodeIDList getNodeIDList();

    NodeIDList getNodeIDList(String endpoint);

    /**
     * Peer operation: async get node ids
     *
     * @param callback the callback instance
     */
    void getNodeIDListAsync(RespCallback<NodeIDList> callback);

    /**
     * Peer operation: get observer node list
     *
     * @return observer node list
     */
    ObserverList getObserverList();

    /**
     * Peer operation: async get observer node list
     *
     * @param callback the callback instance
     */
    void getObserverList(RespCallback<ObserverList> callback);

    /**
     * Peer operation: get sealer node list
     *
     * @return sealer node list
     */
    SealerList getSealerList();

    /**
     * Peer operation: async get sealer node list
     *
     * @param callback the callback instance
     */
    void getSealerListAsync(RespCallback<SealerList> callback);

    /**
     * Peer operation: get pbft view
     *
     * @return pbft view
     */
    PbftView getPbftView();

    /**
     * Peer operation: async get pbft view
     *
     * @param callback the callback instance
     */
    void getPbftViewAsync(RespCallback<PbftView> callback);

    NodeVersion getNodeVersion(String ipAndPort);

    /**
     * Peer operation: get node version
     *
     * @return node version
     */
    NodeVersion getNodeVersion();

    /**
     * Peer operation: get node version
     *
     * @param callback the callback instance
     */
    void getNodeVersion(RespCallback<NodeVersion> callback);

    /**
     * Get list of subscribers to a topic
     *
     * @param topicName the topic you want to query
     * @param peerIpPort the node you want to send to
     * @return List of subscribers
     */
    TopicSubscribers getAmopTopicSubscribers(String topicName, String peerIpPort);

    /**
     * Get list of subscribers to a topic
     *
     * @param topicName the topic you want to query
     * @param peerIpPort the node you want to send to
     * @param callback the topic you want to query
     */
    void getAmopTopicSubscribers(
            String topicName, String peerIpPort, RespCallback<TopicSubscribers> callback);

    /**
     * get receipt list according to the block number and the given range
     *
     * @param blockNumber the block number of the receipts
     * @param from the start index of the receipt list required
     * @param count the end index of the receipt list required
     * @return the receipt list
     */
    BcosTransactionReceiptsDecoder getBatchReceiptsByBlockNumberAndRange(
            BigInteger blockNumber, String from, String count);

    /**
     * get receipt list according to the block hash and the given range
     *
     * @param blockHash the block hash of the receipts
     * @param from the start index of the receipt list required
     * @param count the end index of the receipt list required
     * @return the receipt list
     */
    BcosTransactionReceiptsDecoder getBatchReceiptsByBlockHashAndRange(
            String blockHash, String from, String count);

    /**
     * Peer operation: get consensus status
     *
     * @return consensus status
     */
    ConsensusStatus getConsensusStatus();

    /**
     * Peer operation: async get consensus status
     *
     * @param callback the callback instance
     */
    void getConsensusStates(RespCallback<ConsensusStatus> callback);

    /**
     * Peer operation: get system config
     *
     * @param key the string of key
     * @return system config
     */
    SystemConfig getSystemConfigByKey(String key);

    /**
     * Peer operation: get system config
     *
     * @param key the string of key
     * @param peerIpPort the node that the request sent to
     * @return system config
     */
    SystemConfig getSystemConfigByKey(String key, String peerIpPort);

    /**
     * Peer operation: async get system config
     *
     * @param key the string of key
     * @param callback the callback instance
     */
    void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback);

    /**
     * Peer operation: async get system config
     *
     * @param key the string of key
     * @param peerIpPort the port string of
     * @param callback the callback instance
     */
    void getSystemConfigByKeyAsync(
            String key, String peerIpPort, RespCallback<SystemConfig> callback);

    /**
     * Peer operation: get sync status
     *
     * @return sync status
     */
    SyncStatus getSyncStatus();

    /**
     * Peer operation: async get sync status
     *
     * @param callback the callback instance
     */
    void getSyncStatus(RespCallback<SyncStatus> callback);

    /**
     * Get EventPushMsgHandler and FilterManager.
     *
     * @return EventResource
     */
    EventResource getEventResource();

    void stop();
}
