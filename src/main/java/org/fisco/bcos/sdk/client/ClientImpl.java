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
package org.fisco.bcos.sdk.client;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.client.protocol.request.GenerateGroupParam;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcMethods;
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
import org.fisco.bcos.sdk.model.JsonRpcRequest;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientImpl implements Client {
    private static Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    private final JsonRpcService jsonRpcService;
    private final Integer groupId;

    ClientImpl(GroupManagerService groupManagerService, Channel channel, Integer groupId) {
        this.jsonRpcService = new JsonRpcService(groupManagerService, channel, groupId);
        this.groupId = groupId;
    }

    /**
     * Build a client instance GroupId is identified, all interfaces are available
     *
     * @param channel
     * @param groupIdStr
     * @return a client instance
     */
    @Override
    public Client build(
            GroupManagerService groupManagerService, Channel channel, String groupIdStr) {
        Integer groupId = Integer.valueOf(groupIdStr);
        if (groupId == null) {
            logger.warn("build client failed for invalid groupId, groupId: {}", groupIdStr);
            return null;
        }
        return new ClientImpl(groupManagerService, channel, groupId);
    }

    /**
     * Build a client inssendtance Can only call interfaces relate to group management and node
     * management
     *
     * @param channel
     * @return a client instance
     */
    @Override
    public Client build(GroupManagerService groupManagerService, Channel channel) {
        return new ClientImpl(groupManagerService, channel, 1);
    }

    @Override
    public SendTransaction sendRawTransaction(String signedTransactionData) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_RAWTRANSACTION,
                        Arrays.asList(this.groupId, signedTransactionData)),
                SendTransaction.class);
    }

    @Override
    public void sendRawTransactionAsync(
            String signedTransactionData, RespCallback<SendTransaction> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_RAWTRANSACTION,
                        Arrays.asList(this.groupId, signedTransactionData)),
                SendTransaction.class,
                callback);
    }

    @Override
    public Call call(Transaction transaction) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.CALL, Arrays.asList(this.groupId, transaction)),
                Call.class);
    }

    @Override
    public void callAsync(Transaction transaction, RespCallback<Call> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.CALL, Arrays.asList(this.groupId, transaction)),
                Call.class,
                callback);
    }

    @Override
    public SendTransaction sendRawTransactionAndGetProof(String signedTransactionData) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_RAWTRANSACTION_AND_GET_PROOF,
                        Arrays.asList(this.groupId, signedTransactionData)),
                SendTransaction.class);
    }

    @Override
    public void sendRawTransactionAndGetProofAsync(
            String signedTransactionData, RespCallback<SendTransaction> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_RAWTRANSACTION_AND_GET_PROOF,
                        Arrays.asList(this.groupId, signedTransactionData)),
                SendTransaction.class,
                callback);
    }

    @Override
    public BlockNumber getBlockNumber() {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.groupId));
        return this.jsonRpcService.sendRequestToGroup(request, BlockNumber.class);
    }

    @Override
    public void getBlockNumberAsync(RespCallback<BlockNumber> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.groupId)),
                BlockNumber.class,
                callback);
    }

    @Override
    public Code getCode(String address) {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(this.groupId));
        return this.jsonRpcService.sendRequestToGroup(request, Code.class);
    }

    @Override
    public void getCodeAsync(String address, RespCallback<Code> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(this.groupId)),
                Code.class,
                callback);
    }

    @Override
    public TotalTransactionCount getTotalTransactionCount() {
        // create request for getTotalTransactionCount
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList(this.groupId));
        return this.jsonRpcService.sendRequestToGroup(request, TotalTransactionCount.class);
    }

    @Override
    public void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList(this.groupId)),
                TotalTransactionCount.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByHash(String blockHash, boolean returnFullTransactionObjects) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(this.groupId, returnFullTransactionObjects)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByHashAsync(
            String blockHash,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(this.groupId, returnFullTransactionObjects)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByNumber(
            BigInteger blockNumber, boolean returnFullTransactionObjects) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(this.groupId, returnFullTransactionObjects)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(this.groupId, returnFullTransactionObjects)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BlockHash getBlockHashByNumber(BigInteger blockNumber) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER, Arrays.asList(this.groupId)),
                BlockHash.class);
    }

    @Override
    public void getBlockHashByNumberAsync(
            BigInteger blockNumber, RespCallback<BlockHash> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER, Arrays.asList(this.groupId)),
                BlockHash.class,
                callback);
    }

    @Override
    public BcosBlockHeader getBlockHeaderByHash(String blockHash, boolean returnSealerList) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHEADER_BY_HASH,
                        Arrays.asList(this.groupId, returnSealerList)),
                BcosBlockHeader.class);
    }

    @Override
    public void getBlockHeaderByHashAsync(
            String blockHash, boolean returnSealerList, RespCallback<BcosBlockHeader> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHEADER_BY_HASH,
                        Arrays.asList(this.groupId, returnSealerList)),
                BcosBlockHeader.class,
                callback);
    }

    @Override
    public BcosTransaction getTransactionByHash(String transactionHash) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransaction.class);
    }

    @Override
    public void getTransactionByHashAsync(
            String transactionHash, RespCallback<BcosTransaction> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransaction.class,
                callback);
    }

    @Override
    public TransactionWithProof getTransactionByHashWithProof(String transactionHash) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH_WITH_PROOF,
                        Arrays.asList(this.groupId, transactionHash)),
                TransactionWithProof.class);
    }

    @Override
    public void getTransactionByHashWithProofAsync(
            String transactionHash, RespCallback<TransactionWithProof> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH_WITH_PROOF,
                        Arrays.asList(this.groupId, transactionHash)),
                TransactionWithProof.class,
                callback);
    }

    @Override
    public BcosTransaction getTransactionByBlockNumberAndIndex(
            BigInteger blockNumber, BigInteger transactionIndex) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_BLOCKNUMBER_AND_INDEX,
                        Arrays.asList(
                                this.groupId,
                                String.valueOf(blockNumber),
                                Numeric.encodeQuantity(transactionIndex))),
                BcosTransaction.class);
    }

    @Override
    public void getTransactionByBlockNumberAndIndexAsync(
            BigInteger blockNumber,
            BigInteger transactionIndex,
            RespCallback<BcosTransaction> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_BLOCKNUMBER_AND_INDEX,
                        Arrays.asList(
                                this.groupId,
                                String.valueOf(blockNumber),
                                Numeric.encodeQuantity(transactionIndex))),
                BcosTransaction.class,
                callback);
    }

    @Override
    public BcosTransactionReceipt getTransactionReceipt(String transactionHash) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void getTransactionReceiptAsync(
            String transactionHash, RespCallback<BcosTransactionReceipt> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransactionReceipt.class,
                callback);
    }

    @Override
    public TransactionReceiptWithProof getTransactionReceiptByHashWithProof(
            String transactionHash) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_RECEIPT_BY_HASH_WITH_PROOF,
                        Arrays.asList(this.groupId, transactionHash)),
                TransactionReceiptWithProof.class);
    }

    @Override
    public void getTransactionReceiptByHashWithProofAsync(
            String transactionHash, RespCallback<TransactionReceiptWithProof> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_RECEIPT_BY_HASH_WITH_PROOF,
                        Arrays.asList(this.groupId, transactionHash)),
                TransactionReceiptWithProof.class,
                callback);
    }

    @Override
    public PendingTransactions getPendingTransaction() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PENDING_TRANSACTIONS, Arrays.asList(this.groupId)),
                PendingTransactions.class);
    }

    @Override
    public void getPendingTransactionAsync(RespCallback<PendingTransactions> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PENDING_TRANSACTIONS, Arrays.asList(this.groupId)),
                PendingTransactions.class,
                callback);
    }

    @Override
    public PendingTxSize getPendingTxSize() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupId)),
                PendingTxSize.class);
    }

    @Override
    public void getPendingTxSizeAsync(RespCallback<PendingTxSize> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupId)),
                PendingTxSize.class,
                callback);
    }

    @Override
    public BigInteger getBlockLimit() {
        Integer groupId = Integer.valueOf(this.groupId);
        if (this.jsonRpcService.getGroupManagerService().getBlockLimitByGroup(groupId)
                == BigInteger.ZERO) {
            BigInteger blockNumber = this.getBlockNumber().getBlockNumber();
            // update the blockNumber of groupManagerService
            this.jsonRpcService.getGroupManagerService().updateBlockNumber(groupId, blockNumber);
            return blockNumber;
        }
        return this.jsonRpcService.getGroupManagerService().getBlockLimitByGroup(groupId);
    }

    @Override
    public GenerateGroup generateGroup(
            int groupId,
            long timestamp,
            boolean enableFreeStorage,
            List<String> nodeList,
            String peerIpPort) {
        GenerateGroupParam generateGroupParam =
                new GenerateGroupParam(String.valueOf(timestamp), enableFreeStorage, nodeList);
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GENERATE_GROUP, Arrays.asList(groupId, generateGroupParam));
        return this.jsonRpcService.sendRequestToPeer(request, peerIpPort, GenerateGroup.class);
    }

    @Override
    public void generateGroupAsync(
            int groupId,
            long timestamp,
            boolean enableFreeStorage,
            List<String> nodeList,
            String peerIpPort,
            RespCallback<GenerateGroup> callback) {
        GenerateGroupParam generateGroupParam =
                new GenerateGroupParam(String.valueOf(timestamp), enableFreeStorage, nodeList);
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GENERATE_GROUP, Arrays.asList(groupId, generateGroupParam));
        this.jsonRpcService.asyncSendRequestToPeer(
                request, peerIpPort, GenerateGroup.class, callback);
    }

    @Override
    public StartGroup startGroup(int groupId, String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.START_GROUP, Arrays.asList(groupId)),
                peerIpPort,
                StartGroup.class);
    }

    @Override
    public void startGroupAsync(int groupId, String peerIpPort, RespCallback<StartGroup> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.START_GROUP, Arrays.asList(this.groupId)),
                peerIpPort,
                StartGroup.class,
                callback);
    }

    @Override
    public StopGroup stopGroup(int groupId, String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.STOP_GROUP, Arrays.asList(groupId)),
                peerIpPort,
                StopGroup.class);
    }

    @Override
    public void stopGroupAsync(int groupId, String peerIpPort, RespCallback<StopGroup> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.STOP_GROUP, Arrays.asList(this.groupId)),
                peerIpPort,
                StopGroup.class,
                callback);
    }

    @Override
    public RemoveGroup removeGroup(int groupId, String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.REMOVE_GROUP, Arrays.asList(groupId)),
                peerIpPort,
                RemoveGroup.class);
    }

    @Override
    public void removeGroupAsync(
            int groupId, String peerIpPort, RespCallback<RemoveGroup> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.REMOVE_GROUP, Arrays.asList(this.groupId)),
                peerIpPort,
                RemoveGroup.class,
                callback);
    }

    @Override
    public RecoverGroup recoverGroup(int groupId, String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.RECOVER_GROUP, Arrays.asList(groupId)),
                peerIpPort,
                RecoverGroup.class);
    }

    @Override
    public void recoverGroupAsync(
            int groupId, String peerIpPort, RespCallback<RecoverGroup> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.RECOVER_GROUP, Arrays.asList(this.groupId)),
                peerIpPort,
                RecoverGroup.class,
                callback);
    }

    @Override
    public QueryGroupStatus queryGroupStatus(int groupId) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.QUERY_GROUP_STATUS, Arrays.asList(groupId)),
                QueryGroupStatus.class);
    }

    @Override
    public QueryGroupStatus queryGroupStatus(int groupId, String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.QUERY_GROUP_STATUS, Arrays.asList(groupId)),
                peerIpPort,
                QueryGroupStatus.class);
    }

    @Override
    public void queryGroupStatusAsync(int groupId, RespCallback<QueryGroupStatus> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.QUERY_GROUP_STATUS, Arrays.asList(this.groupId)),
                QueryGroupStatus.class,
                callback);
    }

    @Override
    public void queryGroupStatusAsync(
            int groupId, String peerIpPort, RespCallback<QueryGroupStatus> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.QUERY_GROUP_STATUS, Arrays.asList(this.groupId)),
                peerIpPort,
                QueryGroupStatus.class,
                callback);
    }

    @Override
    public GroupList getGroupList() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_LIST, Arrays.asList()),
                GroupList.class);
    }

    @Override
    public GroupList getGroupList(String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_LIST, Arrays.asList()),
                peerIpPort,
                GroupList.class);
    }

    @Override
    public void getGroupListAsync(RespCallback<GroupList> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_LIST, Arrays.asList()),
                GroupList.class,
                callback);
    }

    @Override
    public void getGroupListAsync(String peerIpPort, RespCallback<GroupList> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_LIST, Arrays.asList()),
                peerIpPort,
                GroupList.class,
                callback);
    }

    @Override
    public GroupPeers getGroupPeers() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_PEERS, Arrays.asList(this.groupId)),
                GroupPeers.class);
    }

    @Override
    public GroupPeers getGroupPeers(String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_PEERS, Arrays.asList(this.groupId)),
                peerIpPort,
                GroupPeers.class);
    }

    @Override
    public void getGroupPeersAsync(RespCallback<GroupPeers> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_PEERS, Arrays.asList(this.groupId)),
                GroupPeers.class,
                callback);
    }

    @Override
    public void getGroupPeersAsync(String peerIpPort, RespCallback<GroupPeers> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_PEERS, Arrays.asList(this.groupId)),
                peerIpPort,
                GroupPeers.class,
                callback);
    }

    @Override
    public Peers getPeers() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList()), Peers.class);
    }

    @Override
    public void getPeersAsync(RespCallback<Peers> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList()),
                Peers.class,
                callback);
    }

    @Override
    public NodeIDList getNodeIDList() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_NODEIDLIST, Arrays.asList()),
                NodeIDList.class);
    }

    @Override
    public void getNodeIDListAsync(RespCallback<NodeIDList> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_NODEIDLIST, Arrays.asList()),
                NodeIDList.class,
                callback);
    }

    @Override
    public ObserverList getObserverList() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.groupId)),
                ObserverList.class);
    }

    @Override
    public void getObserverList(RespCallback<ObserverList> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.groupId)),
                ObserverList.class,
                callback);
    }

    @Override
    public SealerList getSealerList() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.groupId)),
                SealerList.class);
    }

    @Override
    public void getSealerListAsync(RespCallback<SealerList> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.groupId)),
                SealerList.class,
                callback);
    }

    @Override
    public PbftView getPbftView() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.groupId)),
                PbftView.class);
    }

    @Override
    public void getPbftViewAsync(RespCallback<PbftView> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.groupId)),
                PbftView.class,
                callback);
    }

    @Override
    public NodeVersion getNodeVersion() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_NODE_VERSION, Arrays.asList()),
                NodeVersion.class);
    }

    @Override
    public void getNodeVersion(RespCallback<NodeVersion> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_NODE_VERSION, Arrays.asList()),
                NodeVersion.class,
                callback);
    }

    @Override
    public ConsensusStatus getConsensusStatus() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CONSENSUS_STATUS, Arrays.asList(this.groupId)),
                ConsensusStatus.class);
    }

    @Override
    public void getConsensusStates(RespCallback<ConsensusStatus> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CONSENSUS_STATUS, Arrays.asList(this.groupId)),
                ConsensusStatus.class,
                callback);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String key) {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.groupId, key)),
                SystemConfig.class);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String key, String peerIpPort) {
        return this.jsonRpcService.sendRequestToPeer(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.groupId, key)),
                peerIpPort,
                SystemConfig.class);
    }

    @Override
    public void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.groupId)),
                SystemConfig.class,
                callback);
    }

    @Override
    public void getSystemConfigByKeyAsync(
            String key, String peerIpPort, RespCallback<SystemConfig> callback) {
        this.jsonRpcService.asyncSendRequestToPeer(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.groupId)),
                peerIpPort,
                SystemConfig.class,
                callback);
    }

    @Override
    public SyncStatus getSyncStatus() {
        return this.jsonRpcService.sendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupId)),
                SyncStatus.class);
    }

    @Override
    public void getSyncStatus(RespCallback<SyncStatus> callback) {
        this.jsonRpcService.asyncSendRequestToGroup(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupId)),
                SyncStatus.class,
                callback);
    }
}
