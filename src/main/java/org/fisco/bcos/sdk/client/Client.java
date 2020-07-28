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
import org.fisco.bcos.sdk.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.client.protocol.response.Code;
import org.fisco.bcos.sdk.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.client.protocol.response.GenerateGroup;
import org.fisco.bcos.sdk.client.protocol.response.GroupList;
import org.fisco.bcos.sdk.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.client.protocol.response.NodeIDList;
import org.fisco.bcos.sdk.client.protocol.response.NodeVersion;
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
import org.fisco.bcos.sdk.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.client.protocol.response.TransactionReceiptWithProof;
import org.fisco.bcos.sdk.client.protocol.response.TransactionWithProof;
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
     * @param channel
     * @param groupId
     * @return a client instance
     */
    static Client build(GroupManagerService groupManagerService, Channel channel, Integer groupId) {
        // check the groupList
        Set<String> groupList = groupManagerService.getGroupNodeList(groupId);
        if (groupList == null || groupList.size() == 0) {
            logger.warn("build client failed for no peers setup the group {}", groupId);
            return null;
        }
        return new ClientImpl(groupManagerService, channel, groupId);
    }

    static Client build(Channel channel) {
        return new ClientImpl(channel);
    }

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
     * @param signedTransactionData
     * @param callback
     */
    void sendRawTransactionAsync(
            String signedTransactionData, RespCallback<SendTransaction> callback);

    /**
     * Ledger operation: call contract functions without sending transaction
     *
     * @param transaction
     * @return Call
     */
    Call call(Transaction transaction);

    /**
     * Ledger operation: async call contract functions without sending transaction
     *
     * @param transaction
     * @param callback
     */
    void callAsync(Transaction transaction, RespCallback<Call> callback);

    /**
     * Ledger operation: send raw transaction and get proof
     *
     * @param signedTransactionData
     * @return a SendTransaction instance
     */
    SendTransaction sendRawTransactionAndGetProof(String signedTransactionData);

    /**
     * Ledger operation: async send transaction and get proof
     *
     * @param signedTransactionData
     * @param callback
     */
    void sendRawTransactionAndGetProofAsync(
            String signedTransactionData, RespCallback<SendTransaction> callback);

    /**
     * Ledger operation: get block number
     *
     * @return block number
     */
    BlockNumber getBlockNumber();

    /**
     * Ledger operation: async get block number
     *
     * @param callback
     */
    void getBlockNumberAsync(RespCallback<BlockNumber> callback);

    /**
     * Ledger operation: get code
     *
     * @param address
     * @return a code instance
     */
    Code getCode(String address);

    /**
     * Ledger operation: async get code
     *
     * @param address
     * @param callback
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
     * @param callback
     */
    void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback);

    /**
     * Ledger operation: get block by hash
     *
     * @param blockHash
     * @param returnFullTransactionObjects
     * @return a block
     */
    BcosBlock getBlockByHash(String blockHash, boolean returnFullTransactionObjects);

    /**
     * Ledger operation: async get block by hash
     *
     * @param blockHash
     * @param returnFullTransactionObjects
     * @param callback
     */
    void getBlockByHashAsync(
            String blockHash,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: get block by block number
     *
     * @param blockNumber
     * @param returnFullTransactionObjects
     * @return block
     */
    BcosBlock getBlockByNumber(BigInteger blockNumber, boolean returnFullTransactionObjects);

    /**
     * Ledger operation: async get block by block number
     *
     * @param blockNumber
     * @param returnFullTransactionObjects
     * @param callback
     */
    void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: get block hash by block number
     *
     * @param blockNumber
     * @return block hash
     */
    BlockHash getBlockHashByNumber(BigInteger blockNumber);

    /**
     * Ledger operation: async get block hash by block number
     *
     * @param blockNumber
     * @param callback
     */
    void getBlockHashByNumberAsync(BigInteger blockNumber, RespCallback<BlockHash> callback);

    /**
     * Ledger operation: get block header by block hash
     *
     * @param blockHash
     * @param returnSealerList
     * @return block header
     */
    BcosBlockHeader getBlockHeaderByHash(String blockHash, boolean returnSealerList);

    /**
     * Ledger operation: async get block header by block hash
     *
     * @param blockHash
     * @param returnSealerList
     * @param callback
     */
    void getBlockHeaderByHashAsync(
            String blockHash, boolean returnSealerList, RespCallback<BcosBlockHeader> callback);

    /**
     * Ledger operation: get trnasaction by hash
     *
     * @param transactionHash
     * @return transaction
     */
    BcosTransaction getTransactionByHash(String transactionHash);

    /**
     * Ledger operation: async get trnasaction by hash
     *
     * @param transactionHash
     * @param callback
     */
    void getTransactionByHashAsync(String transactionHash, RespCallback<BcosTransaction> callback);

    /**
     * Ledger operation: get transaction and proof by hash
     *
     * @param transactionHash
     * @return transaction with proof
     */
    TransactionWithProof getTransactionByHashWithProof(String transactionHash);

    /**
     * Ledger operation: async get transaction and proof by hash
     *
     * @param transactionHash
     * @param callback
     */
    void getTransactionByHashWithProofAsync(
            String transactionHash, RespCallback<TransactionWithProof> callback);

    /**
     * Ledger operation: get transaction by block number and index
     *
     * @param defaultBlockParameter
     * @param transactionIndex
     * @return transaction
     */
    BcosTransaction getTransactionByBlockNumberAndIndex(
            BigInteger blockNumber, BigInteger transactionIndex);

    /**
     * Ledger operation: async get transaction by block number and index
     *
     * @param defaultBlockParameter
     * @param transactionIndex
     * @param callback
     */
    void getTransactionByBlockNumberAndIndexAsync(
            BigInteger blockNumber,
            BigInteger transactionIndex,
            RespCallback<BcosTransaction> callback);

    /**
     * Ledger operation: get transaction receipt by transaction hash
     *
     * @param transactionHash
     * @return transaction receipt
     */
    BcosTransactionReceipt getTransactionReceipt(String transactionHash);

    /**
     * Ledger operation: async get transaction receipt by transaction hash
     *
     * @param transactionHash
     * @param callback
     */
    void getTransactionReceiptAsync(
            String transactionHash, RespCallback<BcosTransactionReceipt> callback);

    /**
     * Ledger operation: get transaction receipt and proof by transaction hash
     *
     * @param transactionHash
     * @return receipt and proof
     */
    TransactionReceiptWithProof getTransactionReceiptByHashWithProof(String transactionHash);

    /**
     * Ledger operation: async get transaction receipt and proof by transaction hash
     *
     * @param transactionHash
     * @param callback
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
     * @param callback
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
     * @param callback
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
     * @param groupId
     * @param timestamp
     * @param enableFreeStorage
     * @param nodeList
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
     * @param groupId
     * @param timestamp
     * @param enableFreeStorage
     * @param nodeList
     * @param peerIpPort send to the specific peer
     * @param callback
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
     * @param groupId
     * @param peerIpPort
     * @return start group rpc reply
     */
    StartGroup startGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async start a group
     *
     * @param groupId
     * @param peerIpPort
     * @param callback
     */
    void startGroupAsync(Integer groupId, String peerIpPort, RespCallback<StartGroup> callback);

    /**
     * Group operation: stop a group
     *
     * @param groupId
     * @param peerIpPort
     * @return stop group rpc reply
     */
    StopGroup stopGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async stop a group
     *
     * @param groupId
     * @param peerIpPort
     * @param callback
     */
    void stopGroupAsync(Integer groupId, String peerIpPort, RespCallback<StopGroup> callback);

    /**
     * Group operation: remove a group
     *
     * @param groupId
     * @param peerIpPort
     * @return remove group rpc reply
     */
    RemoveGroup removeGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async remove a group
     *
     * @param groupId
     * @param peerIpPort
     * @param callback
     */
    void removeGroupAsync(Integer groupId, String peerIpPort, RespCallback<RemoveGroup> callback);

    /**
     * Group operation: recover a group
     *
     * @param groupId
     * @param peerIpPort
     * @return recover group rpc reply
     */
    RecoverGroup recoverGroup(Integer groupId, String peerIpPort);

    /**
     * Group operation: async recover a group
     *
     * @param groupId
     * @param peerIpPort
     * @param callback
     */
    void recoverGroupAsync(Integer groupId, String peerIpPort, RespCallback<RecoverGroup> callback);

    /**
     * Group operation: query group status
     *
     * @param groupId
     * @return group status
     */
    QueryGroupStatus queryGroupStatus(Integer groupId);

    /**
     * Group operation: query group status
     *
     * @param groupId
     * @param peerIpPort
     * @return group status
     */
    QueryGroupStatus queryGroupStatus(Integer groupId, String peerIpPort);

    /**
     * Group operation: async query group status
     *
     * @param groupId
     * @param callback
     */
    void queryGroupStatusAsync(Integer groupId, RespCallback<QueryGroupStatus> callback);

    /**
     * Group operation: async query group status
     *
     * @param groupId
     * @param peerIpPort
     * @param callback
     */
    void queryGroupStatusAsync(
            Integer groupId, String peerIpPort, RespCallback<QueryGroupStatus> callback);

    /**
     * Group operation: get peer group list
     *
     * @return grouplist
     */
    GroupList getGroupList();

    /**
     * Group operation: get peer group list
     *
     * @param peerIpPort send to the specific peer
     * @return grouplist
     */
    GroupList getGroupList(String peerIpPort);

    /**
     * Group operation: async get peer group list
     *
     * @param callback
     */
    void getGroupListAsync(RespCallback<GroupList> callback);

    /**
     * Group operation: async get peer group list
     *
     * @param peerIpPort send to the specific peer
     * @param callback
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
     * @param peerIpPort
     * @return group peers
     */
    GroupPeers getGroupPeers(String peerIpPort);

    /**
     * Group operation: async get group peers
     *
     * @param callback
     */
    void getGroupPeersAsync(RespCallback<GroupPeers> callback);

    /**
     * Group operation: async get group peers
     *
     * @param peerIpPort
     * @param callback
     */
    void getGroupPeersAsync(String peerIpPort, RespCallback<GroupPeers> callback);

    /**
     * Peer operation: get connected peers
     *
     * @return peers
     */
    Peers getPeers();

    /**
     * Peer operation: async get connected peers
     *
     * @param callback
     */
    void getPeersAsync(RespCallback<Peers> callback);

    /**
     * Peer operation: get node ids
     *
     * @return node id list
     */
    NodeIDList getNodeIDList();

    /**
     * Peer operation: async get node ids
     *
     * @param callback
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
     * @param callback
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
     * @param callback
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
     * @param callback
     */
    void getPbftViewAsync(RespCallback<PbftView> callback);

    /**
     * Peer operation: get node version
     *
     * @return node version
     */
    NodeVersion getNodeVersion();

    /**
     * Peer operation: get node version
     *
     * @param callback
     */
    void getNodeVersion(RespCallback<NodeVersion> callback);

    /**
     * Peer operation: get consensus status
     *
     * @return consensus status
     */
    ConsensusStatus getConsensusStatus();

    /**
     * Peer operation: async get consensus status
     *
     * @param callback
     */
    void getConsensusStates(RespCallback<ConsensusStatus> callback);

    /**
     * Peer operation: get system config
     *
     * @param key
     * @return system config
     */
    SystemConfig getSystemConfigByKey(String key);

    /**
     * Peer operation: get system config
     *
     * @param key
     * @param peerIpPort
     * @return system config
     */
    SystemConfig getSystemConfigByKey(String key, String peerIpPort);

    /**
     * Peer operation: async get system config
     *
     * @param key
     * @param callback
     */
    void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback);

    /**
     * Peer operation: async get system config
     *
     * @param key
     * @param peerIpPort
     * @param callback
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
     * @param callback
     */
    void getSyncStatus(RespCallback<SyncStatus> callback);
}
