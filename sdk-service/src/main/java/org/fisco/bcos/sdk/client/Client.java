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
import org.fisco.bcos.sdk.channel.model.NodeInfo;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.network.NetworkException;
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
     * @param groupID the group info
     * @return a client instance
     */
    static Client build(String groupID, ConfigOption configOption) throws NetworkException {
        return new ClientImpl(groupID, configOption);
    }

    /**
     * Get CryptoSuite
     *
     * @return the CryptoSuite
     */
    CryptoSuite getCryptoSuite();

    /**
     * Get connected node info
     *
     * @return the info of the connected node
     */
    NodeInfo getNodeInfo();

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

    /**
     * Ledger operation: send transaction
     *
     * @param signedTransactionData transaction string
     * @return SendTransaction
     */
    BcosTransactionReceipt sendTransaction(String signedTransactionData, boolean withProof);

    /**
     * Ledger operation: async send transaction
     *
     * @param signedTransactionData transaction string
     * @param callback the callback that will be called when receive the response
     */
    void sendTransactionAsync(
            String signedTransactionData, boolean withProof, TransactionCallback callback);

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
     * Ledger operation: get total transaction coun
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
    BcosBlock getBlockByHash(
            String blockHash, boolean onlyHeader, boolean returnFullTransactionObjects);

    /**
     * Ledger operation: async get block by hash
     *
     * @param blockHash the hashcode of the block
     * @param returnFullTransactionObjects the boolean define the tx is full or not
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByHashAsync(
            String blockHash,
            boolean onlyHeader,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: get block by block number
     *
     * @param blockNumber the number of the block
     * @param onlyHeader the boolean define if only return header
     * @return block
     */
    BcosBlock getBlockByNumber(
            BigInteger blockNumber, boolean onlyHeader, boolean fullTransactions);

    /**
     * Ledger operation: async get block by block number
     *
     * @param blockNumber the number of the block
     * @param onlyHeader the boolean if only need header
     * @param fullTransactions the boolean if need all transactions
     * @param callback the callback that will be called when receive the response
     */
    void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean onlyHeader,
            boolean fullTransactions,
            RespCallback<BcosBlock> callback);

    /**
     * Ledger operation: async get block hash by block number
     *
     * @param blockNumber the number of the block
     * @param callback the callback that will be called when receive the response
     */
    void getBlockHashByNumberAsync(BigInteger blockNumber, RespCallback<BlockHash> callback);

    /**
     * Ledger operation: get trnasaction by hash
     *
     * @param transactionHash the hashcode of transaction
     * @param withProof with the transaction proof
     * @return transaction
     */
    BcosTransaction getTransaction(String transactionHash, Boolean withProof);

    /**
     * Ledger operation: async get trnasaction by hash
     *
     * @param transactionHash the hashcode of transaction
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionAsync(
            String transactionHash, Boolean withProof, RespCallback<BcosTransaction> callback);

    /**
     * Ledger operation: get transaction receipt by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @return transaction receipt
     */
    BcosTransactionReceipt getTransactionReceipt(String transactionHash, Boolean withProof);

    /**
     * Ledger operation: async get transaction receipt by transaction hash
     *
     * @param transactionHash the hashcode of transaction
     * @param callback the callback that will be called when receive the response
     */
    void getTransactionReceiptAsync(
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransactionReceipt> callback);

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

    /**
     * Peer operation: get system config
     *
     * @param key the string of key
     * @return system config
     */
    SystemConfig getSystemConfigByKey(String key);

    /**
     * Peer operation: async get system config
     *
     * @param key the string of key
     * @param callback the callback instance
     */
    void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback);

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

    // TODO: getConsensusStatus

    // TODO: createGroup

    // TODO: expandGroupNode

    // TODO: removeGroup

    // TODO: removeGroupNode
    //
    // TODO: recoverGroup
    //
    // TODO: recoverGroupNode
    //
    // TODO: startNode
    //
    // TODO: stopNode
    //
    // TODO: getGroupList
    //
    // TODO: getGroupInfo
    //
    // TODO: getGroupNodeInfo

    void stop();
}
