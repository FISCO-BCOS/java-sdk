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
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.jni.BcosSDKJniObj;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the interface of client module.
 *
 * @author Maggie
 */
public interface Client {
    static final Logger logger = LoggerFactory.getLogger(Client.class);

    /**
     * Build a client instance GroupId is identified, all interfaces are available
     *
     * @param configOption the config
     * @return a client instance
     */
    static Client build(ConfigOption configOption) throws JniException {
        logger.info("build, configOption: {}", configOption);
        return build(null, configOption);
    }

    /**
     * Build a client instance GroupId is identified, all interfaces are available
     *
     * @param groupId the group info
     * @param configOption the config
     * @return a client instance
     */
    static Client build(String groupId, ConfigOption configOption) throws JniException {
        logger.info("build, groupID: {}, configOption: {}", groupId, configOption);
        long nativePointer = BcosSDKJniObj.create(configOption.getJniConfig());
        return build(groupId, configOption, nativePointer);
    }

    /**
     * Build a client instance GroupId is identified, all interfaces are available, with specific
     * jniRPC
     *
     * @param groupId the group info
     * @param configOption the config
     * @param nativePointer jni impl native handler
     * @return a client instance
     */
    static Client build(String groupId, ConfigOption configOption, long nativePointer)
            throws JniException {
        logger.info(
                "build, groupID: {}, configOption: {}, nativePointer: {}",
                groupId,
                configOption,
                nativePointer);
        return new ClientImpl(groupId, configOption, nativePointer);
    }

    /** @return */
    long getNativePointer();

    /**
     * Get CryptoSuite
     *
     * @return the CryptoSuite
     */
    CryptoSuite getCryptoSuite();

    /**
     * Get crypto type
     *
     * @return the CryptoType, e.g. ECDSA_TYPE
     */
    Integer getCryptoType();

    /**
     * Whether is wasm exec env
     *
     * @return true when wasm exec env
     */
    Boolean isWASM();

    /**
     * get groupId of the client
     *
     * @return the groupId
     */
    String getGroup();

    /**
     * get groupId of the client
     *
     * @return the groupId
     */
    String getChainId();

    /** */
    ConfigOption getConfigOption();

    public boolean getDAG();

    public void setDAG(boolean dag);

    // ------------------------- rpc interface begin ------------------------------------------

    /**
     * Ledger operation: send transaction
     *
     * @param signedTransactionData transaction string
     * @return SendTransaction
     */
    BcosTransactionReceipt sendTransaction(String signedTransactionData, boolean withProof);

    /**
     * Ledger operation: send transaction
     *
     * @param node the node rpc request send to
     * @param signedTransactionData transaction string
     * @return SendTransaction
     */
    BcosTransactionReceipt sendTransaction(
            String node, String signedTransactionData, boolean withProof);

    /**
     * Ledger operation: async send transaction
     *
     * @param signedTransactionData transaction string
     * @param callback the callback that will be called when receive the response
     */
    void sendTransactionAsync(
            String signedTransactionData, boolean withProof, TransactionCallback callback);

    /**
     * Ledger operation: async send transaction
     *
     * @param node the node rpc request send to
     * @param signedTransactionData transaction string
     * @param callback the callback that will be called when receive the response
     */
    void sendTransactionAsync(
            String node,
            String signedTransactionData,
            boolean withProof,
            TransactionCallback callback);

    /**
     * Ledger operation: call contract functions without sending transaction
     *
     * @param transaction transaction instance
     * @return Call
     */
    Call call(Transaction transaction);

    /**
     * Ledger operation: call contract functions without sending transaction
     *
     * @param node the node rpc request send to
     * @param transaction transaction instance
     * @return Call
     */
    Call call(String node, Transaction transaction);

    /**
     * Ledger operation: async call contract functions without sending transaction
     *
     * @param transaction transaction instance
     * @param callback the callback that will be called when receive the response
     */
    void callAsync(Transaction transaction, RespCallback<Call> callback);

    /**
     * Ledger operation: async call contract functions without sending transaction
     *
     * @param node the node rpc request send to
     * @param transaction transaction instance
     * @param callback the callback that will be called when receive the response
     */
    void callAsync(String node, Transaction transaction, RespCallback<Call> callback);

    /**
     * Ledger operation: get block number
     *
     * @return block number
     */
    BlockNumber getBlockNumber();

    /**
     * Ledger operation: get block number
     *
     * @param node the node rpc request send to
     * @return block number
     */
    BlockNumber getBlockNumber(String node);

    /**
     * Ledger operation: async get block number
     *
     * @param callback the callback that will be called when receive the response
     */
    void getBlockNumberAsync(RespCallback<BlockNumber> callback);

    /**
     * Ledger operation: async get block number
     *
     * @param node the node rpc request send to
     * @param callback the callback that will be called when receive the response
     */
    void getBlockNumberAsync(String node, RespCallback<BlockNumber> callback);

    /**
     * Ledger operation: get code
     *
     * @param address the address string
     * @return a code instance
     */
    Code getCode(String address);

    /**
     * Ledger operation: get code
     *
     * @param node the node rpc request send to
     * @param address the address string
     * @return a code instance
     */
    Code getCode(String node, String address);

    /**
     * Ledger operation: async get code
     *
     * @param address the address string
     * @param callback the callback that will be called when receive the response
     */
    void getCodeAsync(String address, RespCallback<Code> callback);

    /**
     * Ledger operation: async get code
     *
     * @param node the node rpc request send to
     * @param address the address string
     * @param callback the callback that will be called when receive the response
     */
    void getCodeAsync(String node, String address, RespCallback<Code> callback);

    /**
     * Ledger operation: get total transaction coun
     *
     * @return TotalTransactionCount
     */
    TotalTransactionCount getTotalTransactionCount();

    /**
     * Ledger operation: get total transaction coun
     *
     * @param node the node rpc request send to
     * @return TotalTransactionCount
     */
    TotalTransactionCount getTotalTransactionCount(String node);

    /**
     * Ledger operation: async get total transaction count
     *
     * @param callback the callback that will be called when receive the response
     */
    void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback);

    /**
     * Ledger operation: async get total transaction count
     *
     * @param node the node rpc request send to
     * @param callback the callback that will be called when receive the response
     */
    void getTotalTransactionCountAsync(String node, RespCallback<TotalTransactionCount> callback);

    /**
     * Ledger operation: get block by hash
     *
     * @param blockHash the hashcode of the block
     * @param onlyTxHash the boolean define the tx is full or not
     * @return a block
     */
    BcosBlock getBlockByHash(String blockHash, boolean onlyHeader, boolean onlyTxHash);

    /**
     * Ledger operation: get block by hash
     *
     * @param node the node rpc request send to
     * @param blockHash the hashcode of the block
     * @param onlyTxHash the boolean define the tx is full or not
     * @return a block
     */
    BcosBlock getBlockByHash(String node, String blockHash, boolean onlyHeader, boolean onlyTxHash);

    /**
     * Ledger operation: async get block by hash
     *
     * @param blockHash the hashcode of the block
     * @param onlyTxHash the boolean define the tx is full or not
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByHashAsync(
            String blockHash,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: async get block by hash
     *
     * @param node the node rpc request send to
     * @param blockHash the hashcode of the block
     * @param onlyTxHash the boolean define the tx is full or not
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByHashAsync(
            String node,
            String blockHash,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: get block by block number
     *
     * @param blockNumber the number of the block
     * @param onlyHeader the boolean define if only return header
     * @param onlyTxHash the boolean define if only return tx hash
     * @return block
     */
    BcosBlock getBlockByNumber(BigInteger blockNumber, boolean onlyHeader, boolean onlyTxHash);

    /**
     * Ledger operation: get block by block number
     *
     * @param node the node rpc request send to
     * @param blockNumber the number of the block
     * @param onlyHeader the boolean define if only return header
     * @param onlyTxHash the boolean define if only return tx hash
     * @return block
     */
    BcosBlock getBlockByNumber(
            String node, BigInteger blockNumber, boolean onlyHeader, boolean onlyTxHash);

    /**
     * Ledger operation: async get block by block number
     *
     * @param blockNumber the number of the block
     * @param onlyHeader the boolean if only need header
     * @param onlyTxHash the boolean if you need all transactions
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: async get block by block number
     *
     * @param node the node rpc request send to
     * @param blockNumber the number of the block
     * @param onlyHeader the boolean if only need header
     * @param onlyTxHash the boolean if you need all transactions
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByNumberAsync(
            String node,
            BigInteger blockNumber,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: async get block hash by block number
     *
     * @param blockNumber the number of the block
     * @return BlockHash
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
     * Ledger operation: async get block hash by block number
     *
     * @param node the node rpc request send to
     * @param blockNumber the number of the block
     * @return BlockHash
     */
    BlockHash getBlockHashByNumber(String node, BigInteger blockNumber);

    /**
     * Ledger operation: async get block hash by block number
     *
     * @param node the node rpc request send to
     * @param blockNumber the number of the block
     * @param callback the callback that will be called when receive the response
     */
    void getBlockHashByNumberAsync(
            String node, BigInteger blockNumber, RespCallback<BlockHash> callback);

    /**
     * Ledger operation: get transaction by hash
     *
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction proof
     * @return transaction
     */
    BcosTransaction getTransaction(String transactionHash, Boolean withProof);

    /**
     * Ledger operation: get trnasaction by hash
     *
     * @param node the node rpc request send to
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction proof
     * @return transaction
     */
    BcosTransaction getTransaction(String node, String transactionHash, Boolean withProof);

    /**
     * Ledger operation: async get trnasaction by hash
     *
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction proof
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionAsync(
            String transactionHash, Boolean withProof, RespCallback<BcosTransaction> callback);

    /**
     * Ledger operation: async get trnasaction by hash
     *
     * @param node the node rpc request send to
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction proof
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionAsync(
            String node,
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransaction> callback);

    /**
     * Ledger operation: get transaction receipt by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction proof
     * @return transaction receipt
     */
    BcosTransactionReceipt getTransactionReceipt(String transactionHash, Boolean withProof);

    /**
     * Ledger operation: get transaction receipt by transaction hash
     *
     * @param node the node rpc request send to
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction receipt proof
     * @return transaction receipt
     */
    BcosTransactionReceipt getTransactionReceipt(
            String node, String transactionHash, Boolean withProof);

    /**
     * Ledger operation: async get transaction receipt by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction receipt proof
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionReceiptAsync(
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransactionReceipt> callback);

    /**
     * Ledger operation: async get transaction receipt by transaction hash
     *
     * @param node the node rpc request send to
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction receipt proof
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionReceiptAsync(
            String node,
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransactionReceipt> callback);

    /**
     * Ledger operation: get pending transaction size
     *
     * @param node the node rpc request send to
     * @return PendingTxSize
     */
    PendingTxSize getPendingTxSize(String node);

    /**
     * Ledger operation: async get pending transaction size
     *
     * @param callback the callback that will be called when receive the response
     */
    void getPendingTxSizeAsync(String node, RespCallback<PendingTxSize> callback);

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
     * Peer operation: get connected peers
     *
     * @return peers
     */
    Peers getPeers();

    /**
     * get the group peers
     *
     * @return the groupPeers
     */
    GroupPeers getGroupPeers();

    void getGroupPeersAsync(RespCallback<GroupPeers> callback);

    /**
     * Peer operation: async get connected peers
     *
     * @param callback the callback instance
     */
    void getPeersAsync(RespCallback<Peers> callback);

    /**
     * Peer operation: get observer node list
     *
     * @return observer node list
     */
    ObserverList getObserverList();

    /**
     * Peer operation: get observer node list
     *
     * @return observer node list
     */
    ObserverList getObserverList(String node);

    /**
     * Peer operation: async get observer node list
     *
     * @param callback the callback instance
     */
    void getObserverList(RespCallback<ObserverList> callback);

    /**
     * Peer operation: async get observer node list
     *
     * @param callback the callback instance
     */
    void getObserverList(String node, RespCallback<ObserverList> callback);

    /**
     * Peer operation: get sealer node list
     *
     * @return sealer node list
     */
    SealerList getSealerList();

    /**
     * Peer operation: get sealer node list
     *
     * @param node the node rpc request send to
     * @return sealer node list
     */
    SealerList getSealerList(String node);

    /**
     * Peer operation: async get sealer node list
     *
     * @param callback the callback instance
     */
    void getSealerListAsync(RespCallback<SealerList> callback);

    /**
     * Peer operation: async get sealer node list
     *
     * @param node the node rpc request send to
     * @param callback the callback instance
     */
    void getSealerListAsync(String node, RespCallback<SealerList> callback);

    /**
     * Peer operation: get pbft view
     *
     * @return pbft view
     */
    PbftView getPbftView();

    /**
     * Peer operation: get pbft view
     *
     * @param node the node rpc request send to
     * @return pbft view
     */
    PbftView getPbftView(String node);

    /**
     * Peer operation: async get pbft view
     *
     * @param callback the callback instance
     */
    void getPbftViewAsync(RespCallback<PbftView> callback);

    /**
     * Peer operation: async get pbft view
     *
     * @param node the node rpc request send to
     * @param callback the callback instance
     */
    void getPbftViewAsync(String node, RespCallback<PbftView> callback);

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
     * @param node the node rpc request send to
     * @param key the string of key
     * @return system config
     */
    SystemConfig getSystemConfigByKey(String node, String key);

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
     * @param node the node rpc request send to
     * @param key the string of key
     * @param callback the callback instance
     */
    void getSystemConfigByKeyAsync(String node, String key, RespCallback<SystemConfig> callback);

    /**
     * Peer operation: get sync status
     *
     * @return sync status
     */
    SyncStatus getSyncStatus(String node);

    /**
     * Peer operation: async get sync status
     *
     * @param node the node rpc request send to
     * @param callback the callback instance
     */
    void getSyncStatusAsync(String node, RespCallback<SyncStatus> callback);

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
    void getSyncStatusAsync(RespCallback<SyncStatus> callback);

    /**
     * async get consensus status
     *
     * @param node the node rpc request send to
     * @param callback the callback
     */
    void getConsensusStatusAsync(String node, RespCallback<ConsensusStatus> callback);

    /**
     * async get consensus status
     *
     * @param callback
     */
    void getConsensusStatusAsync(RespCallback<ConsensusStatus> callback);

    /**
     * sync get consensus status
     *
     * @param node the node rpc request send to
     * @return
     */
    ConsensusStatus getConsensusStatus(String node);

    /**
     * sync get consensus status
     *
     * @return
     */
    ConsensusStatus getConsensusStatus();

    /**
     * get group list
     *
     * @return
     */
    BcosGroupList getGroupList();

    void getGroupListAsync(RespCallback<BcosGroupList> callback);

    /**
     * get group info
     *
     * @return
     */
    BcosGroupInfo getGroupInfo();

    void getGroupInfoAsync(RespCallback<BcosGroupInfo> callback);

    /**
     * get group info list
     *
     * @return
     */
    BcosGroupInfoList getGroupInfoList();

    void getGroupInfoListAsync(RespCallback<BcosGroupInfoList> callback);

    /**
     * get group node info
     *
     * @return
     */
    BcosGroupNodeInfo getGroupNodeInfo(String node);

    void getGroupNodeInfoAsync(String node, RespCallback<BcosGroupNodeInfo> callback);

    // ------------------------- rpc interface end ------------------------------------------

    void start();

    void stop();

    void destroy();
}
