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
package org.fisco.bcos.sdk.v3.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.fisco.bcos.sdk.jni.BcosSDKJniObj;
import org.fisco.bcos.sdk.jni.rpc.RpcJniObj;
import org.fisco.bcos.sdk.v3.client.exceptions.ClientException;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeIniConfig;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeIniInfo;
import org.fisco.bcos.sdk.v3.client.protocol.request.JsonRpcMethods;
import org.fisco.bcos.sdk.v3.client.protocol.request.JsonRpcRequest;
import org.fisco.bcos.sdk.v3.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.Abi;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfoList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.client.protocol.response.Code;
import org.fisco.bcos.sdk.v3.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.v3.client.protocol.response.Peers;
import org.fisco.bcos.sdk.v3.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.ResponseCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientImpl implements Client {
    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    private static final int BLOCK_LIMIT_RANGE = 500;

    // ------------basic group info --------------
    private String groupID = "";
    private String chainID;
    private Boolean wasm;
    private Boolean authCheck = false;
    private boolean serialExecute;
    private Boolean smCrypto;
    private String extraData = "";
    // ------------basic group info --------------

    // ------------ runtime info -----------------

    private long blockNumber = 0;

    private final ConfigOption configOption;
    private BcosGroupInfo.GroupInfo groupInfo;
    private GroupNodeIniConfig groupNodeIniConfig;
    private CryptoSuite cryptoSuite;
    private RpcJniObj rpcJniObj;

    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    protected void initGroupInfo() {
        this.groupInfo = getGroupInfo().getResult();
        if (groupInfo == null) {
            logger.error("The group not exist, groupID: {}", groupID);
            throw new ClientException(
                    "The group not exist, please check the groupID, groupID: " + this.groupID);
        }

        List<BcosGroupNodeInfo.GroupNodeInfo> nodeList = groupInfo.getNodeList();
        if (nodeList == null || nodeList.isEmpty()) {
            logger.error(
                    "There has no nodes in the group, groupID: {}, groupInfo: {}",
                    groupID,
                    groupInfo);
            throw new ClientException(
                    "There has no nodes in the group, maybe the group has been removed, groupID: "
                            + this.groupID);
        }

        BcosGroupNodeInfo.GroupNodeInfo groupNodeInfo = groupInfo.getNodeList().get(0);
        GroupNodeIniInfo nodeIniConfig = groupNodeInfo.getIniConfig();
        long compatibilityVersion = groupNodeInfo.getProtocol().getCompatibilityVersion();

        this.groupNodeIniConfig = GroupNodeIniConfig.newIniConfig(nodeIniConfig);
        this.chainID = groupNodeIniConfig.getChain().getChainID();
        this.wasm = groupNodeIniConfig.getExecutor().isWasm();
        this.serialExecute = groupNodeIniConfig.getExecutor().isSerialExecute();

        this.authCheck = groupNodeIniConfig.getExecutor().isAuthCheck();
        if (EnumNodeVersion.valueOf((int) compatibilityVersion)
                        .toVersionObj()
                        .compareTo(EnumNodeVersion.BCOS_3_3_0.toVersionObj())
                >= 0) {
            this.authCheck = true;
        }
        this.smCrypto = groupNodeIniConfig.getChain().isSmCrypto();
        this.blockNumber = this.getBlockNumber().getBlockNumber().longValue();

        logger.info(
                "init group info in rpc, chainID: {}, smCrypto: {}, wasm: {}, authCheck:{}, serialExecute:{}, blockNumber: {}, GroupNodeIniConfig: {}",
                chainID,
                smCrypto,
                wasm,
                authCheck,
                serialExecute,
                blockNumber,
                groupNodeIniConfig);
    }

    protected ClientImpl(String groupID, ConfigOption configOption, long nativePointer) {
        this.groupID = groupID;
        this.configOption = configOption;
        this.rpcJniObj = RpcJniObj.build(nativePointer);

        // start rpc
        start();

        // groupID is set, init group basic info, eg: chain_id, sm_crypto, is_wasm
        if (Objects.nonNull(groupID) && !groupID.isEmpty()) {

            initGroupInfo();

            // init crypto suite
            if (smCrypto) {
                // init HSM crypto suite
                if (configOption.getCryptoMaterialConfig() != null
                        && configOption.getCryptoMaterialConfig().getEnableHsm()) {
                    this.cryptoSuite = new CryptoSuite(CryptoType.HSM_TYPE, configOption);
                } else {
                    this.cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE, configOption);
                }
            } else {
                this.cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE, configOption);
            }
        }

        logger.info(
                "ClientImpl constructor, groupID: {}, nativePointer: {}, smCrypto: {}, wasm: {}",
                groupID,
                nativePointer,
                smCrypto,
                isWASM());
    }

    @Override
    public String getExtraData() {
        return extraData;
    }

    @Override
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    @Override
    public long getNativePointer() {
        return rpcJniObj.getNativePointer();
    }

    @Override
    public ConfigOption getConfigOption() {
        return this.configOption;
    }

    @Override
    public String getGroup() {
        return this.groupID;
    }

    @Override
    public String getChainId() {
        return this.chainID;
    }

    public Boolean getSmCrypto() {
        return this.smCrypto;
    }

    @Override
    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    @Override
    public Integer getCryptoType() {
        return this.cryptoSuite.getCryptoTypeConfig();
    }

    @Override
    public Boolean isWASM() {
        return this.wasm;
    }

    @Override
    public Boolean isAuthCheck() {
        return this.authCheck;
    }

    @Override
    public Boolean isSerialExecute() {
        return this.serialExecute;
    }

    @Override
    public BcosTransactionReceipt sendTransaction(String signedTransactionData, boolean withProof) {
        return this.sendTransaction("", signedTransactionData, withProof);
    }

    @Override
    public BcosTransactionReceipt sendTransaction(
            String node, String signedTransactionData, boolean withProof) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(this.groupID, node, signedTransactionData, withProof)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void sendTransactionAsync(
            String signedTransactionData, boolean withProof, TransactionCallback callback) {
        this.sendTransactionAsync("", signedTransactionData, withProof, callback);
    }

    @Override
    public void sendTransactionAsync(
            String node,
            String signedTransactionData,
            boolean withProof,
            TransactionCallback callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(this.groupID, node, signedTransactionData, withProof)),
                BcosTransactionReceipt.class,
                new RespCallback<BcosTransactionReceipt>() {
                    @Override
                    public void onResponse(BcosTransactionReceipt transactionReceiptWithProof) {
                        callback.onResponse(transactionReceiptWithProof.getTransactionReceipt());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(
                                errorResponse.getErrorCode(), errorResponse.getErrorMessage());
                    }
                });
    }

    @Override
    public Call call(Transaction transaction) {
        return this.call("", transaction);
    }

    @Override
    public Call call(String node, Transaction transaction) {
        node = Objects.isNull(node) ? "" : node;
        if (logger.isTraceEnabled()) {
            logger.trace("client call, to:{}, data:{}", transaction.getTo(), transaction.getData());
        }
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.CALL,
                        Arrays.asList(
                                this.groupID,
                                node,
                                Hex.trimPrefix(transaction.getTo()),
                                Hex.toHexString(transaction.getData()))),
                Call.class);
    }

    @Override
    public void callAsync(Transaction transaction, RespCallback<Call> callback) {
        this.callAsync("", transaction, callback);
    }

    @Override
    public void callAsync(String node, Transaction transaction, RespCallback<Call> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.CALL,
                        Arrays.asList(
                                this.groupID,
                                node,
                                Hex.trimPrefix(transaction.getTo()),
                                Hex.toHexString(transaction.getData()))),
                Call.class,
                callback);
    }

    @Override
    public BlockNumber getBlockNumber() {
        return this.getBlockNumber("");
    }

    @Override
    public BlockNumber getBlockNumber(String node) {
        node = Objects.isNull(node) ? "" : node;
        // create request
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.groupID, node)),
                BlockNumber.class);
    }

    @Override
    public void getBlockNumberAsync(RespCallback<BlockNumber> callback) {
        this.getBlockNumberAsync("", callback);
    }

    @Override
    public void getBlockNumberAsync(String node, RespCallback<BlockNumber> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.groupID, node)),
                BlockNumber.class,
                callback);
    }

    @Override
    public Code getCode(String address) {
        return this.getCode("", address);
    }

    @Override
    public Code getCode(String node, String address) {
        node = Objects.isNull(node) ? "" : node;

        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_CODE,
                        Arrays.asList(this.groupID, node, Hex.trimPrefix(address))),
                Code.class);
    }

    @Override
    public void getCodeAsync(String address, RespCallback<Code> callback) {
        this.getCodeAsync("", address, callback);
    }

    @Override
    public void getCodeAsync(String node, String address, RespCallback<Code> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_CODE,
                        Arrays.asList(this.groupID, node, Hex.trimPrefix(address))),
                Code.class,
                callback);
    }

    @Override
    public Abi getABI(String address) {
        return this.getABI("", address);
    }

    @Override
    public Abi getABI(String node, String address) {
        node = Objects.isNull(node) ? "" : node;

        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_ABI,
                        Arrays.asList(this.groupID, node, Hex.trimPrefix(address))),
                Abi.class);
    }

    @Override
    public void getABIAsync(String address, RespCallback<Abi> callback) {
        this.getABIAsync("", address, callback);
    }

    @Override
    public void getABIAsync(String node, String address, RespCallback<Abi> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_ABI,
                        Arrays.asList(this.groupID, node, Hex.trimPrefix(address))),
                Abi.class,
                callback);
    }

    @Override
    public TotalTransactionCount getTotalTransactionCount() {
        return this.getTotalTransactionCount("");
    }

    @Override
    public TotalTransactionCount getTotalTransactionCount(String node) {
        node = Objects.isNull(node) ? "" : node;
        // create request for getTotalTransactionCount
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT,
                        Arrays.asList(this.groupID, node)),
                TotalTransactionCount.class);
    }

    @Override
    public void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback) {
        this.getTotalTransactionCountAsync("", callback);
    }

    @Override
    public void getTotalTransactionCountAsync(
            String node, RespCallback<TotalTransactionCount> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT,
                        Arrays.asList(this.groupID, node)),
                TotalTransactionCount.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByHash(String blockHash, boolean onlyHeader, boolean onlyTxHash) {
        return this.getBlockByHash("", blockHash, onlyHeader, onlyTxHash);
    }

    @Override
    public BcosBlock getBlockByHash(
            String node, String blockHash, boolean onlyHeader, boolean onlyTxHash) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(this.groupID, node, blockHash, onlyHeader, onlyTxHash)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByHashAsync(
            String blockHash,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback) {
        this.getBlockByHashAsync("", blockHash, onlyHeader, onlyTxHash, callback);
    }

    @Override
    public void getBlockByHashAsync(
            String node,
            String blockHash,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(this.groupID, node, blockHash, onlyHeader, onlyTxHash)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByNumber(
            BigInteger blockNumber, boolean onlyHeader, boolean onlyTxHash) {
        return this.getBlockByNumber("", blockNumber, onlyHeader, onlyTxHash);
    }

    @Override
    public BcosBlock getBlockByNumber(
            String node, BigInteger blockNumber, boolean onlyHeader, boolean onlyTxHash) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(this.groupID, node, blockNumber, onlyHeader, onlyTxHash)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback) {
        this.getBlockByNumberAsync("", blockNumber, onlyHeader, onlyTxHash, callback);
    }

    @Override
    public void getBlockByNumberAsync(
            String node,
            BigInteger blockNumber,
            boolean onlyHeader,
            boolean onlyTxHash,
            RespCallback<BcosBlock> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(this.groupID, node, blockNumber, onlyHeader, onlyTxHash)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BlockHash getBlockHashByNumber(BigInteger blockNumber) {
        return this.getBlockHashByNumber("", blockNumber);
    }

    @Override
    public BlockHash getBlockHashByNumber(String node, BigInteger blockNumber) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER,
                        Arrays.asList(this.groupID, node, blockNumber)),
                BlockHash.class);
    }

    @Override
    public void getBlockHashByNumberAsync(
            BigInteger blockNumber, RespCallback<BlockHash> callback) {
        this.getBlockHashByNumberAsync("", blockNumber, callback);
    }

    @Override
    public void getBlockHashByNumberAsync(
            String node, BigInteger blockNumber, RespCallback<BlockHash> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER,
                        Arrays.asList(this.groupID, node, blockNumber)),
                BlockHash.class,
                callback);
    }

    @Override
    public BcosTransaction getTransaction(String transactionHash, Boolean withProof) {
        return this.getTransaction("", transactionHash, withProof);
    }

    @Override
    public BcosTransaction getTransaction(String node, String transactionHash, Boolean withProof) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupID, node, transactionHash, withProof)),
                BcosTransaction.class);
    }

    @Override
    public void getTransactionAsync(
            String transactionHash, Boolean withProof, RespCallback<BcosTransaction> callback) {
        this.getTransactionAsync("", transactionHash, withProof, callback);
    }

    @Override
    public void getTransactionAsync(
            String node,
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransaction> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupID, node, transactionHash)),
                BcosTransaction.class,
                callback);
    }

    @Override
    public BcosTransactionReceipt getTransactionReceipt(String transactionHash, Boolean withProof) {
        return this.getTransactionReceipt("", transactionHash, withProof);
    }

    @Override
    public BcosTransactionReceipt getTransactionReceipt(
            String node, String transactionHash, Boolean withProof) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_TRANSACTION_RECEIPT,
                        Arrays.asList(this.groupID, node, transactionHash, withProof)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void getTransactionReceiptAsync(
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransactionReceipt> callback) {
        this.getTransactionReceiptAsync("", transactionHash, withProof, callback);
    }

    @Override
    public void getTransactionReceiptAsync(
            String node,
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransactionReceipt> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_TRANSACTION_RECEIPT,
                        Arrays.asList(this.groupID, node, transactionHash, withProof)),
                BcosTransactionReceipt.class,
                callback);
    }

    @Override
    public PendingTxSize getPendingTxSize() {
        return this.getPendingTxSize("");
    }

    @Override
    public PendingTxSize getPendingTxSize(String node) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupID, node)),
                PendingTxSize.class);
    }

    @Override
    public void getPendingTxSizeAsync(RespCallback<PendingTxSize> callback) {
        this.getPendingTxSizeAsync("", callback);
    }

    @Override
    public void getPendingTxSizeAsync(String node, RespCallback<PendingTxSize> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupID, node)),
                PendingTxSize.class,
                callback);
    }

    @Override
    public BigInteger getBlockLimit() {
        BigInteger blockLimit = BigInteger.valueOf(this.rpcJniObj.getBlockLimit(this.groupID));
        if (logger.isDebugEnabled()) {
            logger.debug("getBlockLimit, group: {}, blockLimit: {}", groupID, blockLimit);
        }

        if (blockLimit.compareTo(BigInteger.ZERO) <= 0) {
            blockLimit = BigInteger.valueOf(blockNumber).add(BigInteger.valueOf(BLOCK_LIMIT_RANGE));
        }

        return blockLimit;
    }

    @Override
    public GroupPeers getGroupPeers() {
        return this.callRemoteMethod(
                this.groupID,
                "",
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_GROUP_PEERS, Arrays.asList(this.groupID, "")),
                GroupPeers.class);
    }

    @Override
    public void getGroupPeersAsync(RespCallback<GroupPeers> callback) {
        this.asyncCallRemoteMethod(
                this.groupID,
                "",
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_GROUP_PEERS, Arrays.asList(this.groupID, "")),
                GroupPeers.class,
                callback);
    }

    @Override
    public Peers getPeers() {
        return this.callRemoteMethod(
                "",
                "",
                new JsonRpcRequest<>(JsonRpcMethods.GET_PEERS, Collections.emptyList()),
                Peers.class);
    }

    @Override
    public void getPeersAsync(RespCallback<Peers> callback) {
        this.asyncCallRemoteMethod(
                "",
                "",
                new JsonRpcRequest<>(JsonRpcMethods.GET_PEERS, Collections.emptyList()),
                Peers.class,
                callback);
    }

    @Override
    public ObserverList getObserverList() {
        return this.getObserverList("");
    }

    @Override
    public ObserverList getObserverList(String node) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.groupID, node)),
                ObserverList.class);
    }

    @Override
    public void getObserverList(RespCallback<ObserverList> callback) {
        this.getObserverList("", callback);
    }

    @Override
    public void getObserverList(String node, RespCallback<ObserverList> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.groupID, node)),
                ObserverList.class,
                callback);
    }

    @Override
    public SealerList getSealerList() {
        return this.getSealerList("");
    }

    @Override
    public SealerList getSealerList(String node) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.groupID, node)),
                SealerList.class);
    }

    @Override
    public void getSealerListAsync(RespCallback<SealerList> callback) {
        this.getSealerListAsync("", callback);
    }

    @Override
    public void getSealerListAsync(String node, RespCallback<SealerList> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.groupID, node)),
                SealerList.class,
                callback);
    }

    @Override
    public PbftView getPbftView() {
        return this.getPbftView("");
    }

    @Override
    public void getPbftViewAsync(RespCallback<PbftView> callback) {
        this.getPbftViewAsync("", callback);
    }

    @Override
    public PbftView getPbftView(String node) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.groupID, node)),
                PbftView.class);
    }

    @Override
    public void getPbftViewAsync(String node, RespCallback<PbftView> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.groupID, node)),
                PbftView.class,
                callback);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String key) {
        return this.getSystemConfigByKey("", key);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String node, String key) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY,
                        Arrays.asList(this.groupID, node, key)),
                SystemConfig.class);
    }

    @Override
    public void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback) {
        this.getSystemConfigByKeyAsync("", key, callback);
    }

    @Override
    public void getSystemConfigByKeyAsync(
            String node, String key, RespCallback<SystemConfig> callback) {
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY,
                        Arrays.asList(this.groupID, node, key)),
                SystemConfig.class,
                callback);
    }

    @Override
    public SyncStatus getSyncStatus(String node) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupID, node)),
                SyncStatus.class);
    }

    @Override
    public SyncStatus getSyncStatus() {
        return getSyncStatus("");
    }

    @Override
    public void getSyncStatusAsync(RespCallback<SyncStatus> callback) {
        this.getSyncStatusAsync("", callback);
    }

    @Override
    public void getSyncStatusAsync(String node, RespCallback<SyncStatus> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupID, node)),
                SyncStatus.class,
                callback);
    }

    @Override
    public void getConsensusStatusAsync(String node, RespCallback<ConsensusStatus> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_CONSENSUS_STATUS, Arrays.asList(this.groupID, node)),
                ConsensusStatus.class,
                callback);
    }

    @Override
    public void getConsensusStatusAsync(RespCallback<ConsensusStatus> callback) {
        this.getConsensusStatusAsync("", callback);
    }

    @Override
    public ConsensusStatus getConsensusStatus(String node) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_CONSENSUS_STATUS, Arrays.asList(this.groupID, node)),
                ConsensusStatus.class);
    }

    @Override
    public ConsensusStatus getConsensusStatus() {
        return getConsensusStatus("");
    }

    @Override
    public BcosGroupList getGroupList() {
        return this.callRemoteMethod(
                "",
                "",
                new JsonRpcRequest<>(JsonRpcMethods.GET_GROUP_LIST, Collections.emptyList()),
                BcosGroupList.class);
    }

    @Override
    public void getGroupListAsync(RespCallback<BcosGroupList> callback) {
        this.asyncCallRemoteMethod(
                "",
                "",
                new JsonRpcRequest<>(JsonRpcMethods.GET_GROUP_LIST, Collections.emptyList()),
                BcosGroupList.class,
                callback);
    }

    @Override
    public BcosGroupInfo getGroupInfo() {
        try {
            CompletableFuture<Response> future = new CompletableFuture<>();

            this.rpcJniObj.getGroupInfo(
                    groupID,
                    resp -> {
                        Response response = new Response();
                        response.setErrorCode(resp.getErrorCode());
                        response.setErrorMessage(resp.getErrorMessage());
                        response.setContent(resp.getData());

                        if (logger.isDebugEnabled()) {
                            logger.debug("getGroupInfo onResponse: {}", response);
                        }

                        future.complete(response);
                    });
            Response response = future.get();
            return ClientImpl.parseResponseIntoJsonRpcResponse(
                    JsonRpcMethods.GET_GROUP_INFO, response, BcosGroupInfo.class);
        } catch (ClientException e) {
            logger.error("e: ", e);
            throw new ClientException(
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    "getGroupInfo failed for decode the message exception, error message:"
                            + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("e: ", e);
            throw new ClientException(
                    "getGroupInfo failed for decode the message exception, error message:"
                            + e.getMessage(),
                    e);
        }
    }

    @Override
    public void getGroupInfoAsync(RespCallback<BcosGroupInfo> callback) {
        rpcJniObj.getGroupInfo(
                this.groupID,
                resp -> {
                    Response response = new Response();
                    response.setErrorCode(resp.getErrorCode());
                    response.setErrorMessage(resp.getErrorMessage());
                    response.setContent(resp.getData());

                    ResponseCallback responseCallback =
                            createResponseCallback(
                                    JsonRpcMethods.GET_GROUP_INFO, BcosGroupInfo.class, callback);

                    if (logger.isDebugEnabled()) {
                        logger.debug("getGroupInfo onResponse: {}", response);
                    }

                    responseCallback.onResponse(response);
                });
    }

    @Override
    public BcosGroupInfoList getGroupInfoList() {
        return this.callRemoteMethod(
                "",
                "",
                new JsonRpcRequest<>(JsonRpcMethods.GET_GROUP_INFO_LIST, Collections.emptyList()),
                BcosGroupInfoList.class);
    }

    @Override
    public void getGroupInfoListAsync(RespCallback<BcosGroupInfoList> callback) {
        this.asyncCallRemoteMethod(
                "",
                "",
                new JsonRpcRequest<>(JsonRpcMethods.GET_GROUP_INFO_LIST, Collections.emptyList()),
                BcosGroupInfoList.class,
                callback);
    }

    @Override
    public BcosGroupNodeInfo getGroupNodeInfo(String node) {
        node = Objects.isNull(node) ? "" : node;
        return this.callRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_GROUP_NODE_INFO, Arrays.asList(groupID, node)),
                BcosGroupNodeInfo.class);
    }

    @Override
    public void getGroupNodeInfoAsync(String node, RespCallback<BcosGroupNodeInfo> callback) {
        node = Objects.isNull(node) ? "" : node;
        this.asyncCallRemoteMethod(
                this.groupID,
                node,
                new JsonRpcRequest<>(
                        JsonRpcMethods.GET_GROUP_NODE_INFO, Arrays.asList(groupID, node)),
                BcosGroupNodeInfo.class,
                callback);
    }

    @Override
    public EnumNodeVersion.Version getChainVersion() {
        List<BcosGroupNodeInfo.GroupNodeInfo> nodeList = getGroupInfo().getResult().getNodeList();
        if (nodeList == null || nodeList.isEmpty()) {
            throw new IllegalStateException("Empty node list in group info.");
        }
        long compatibilityVersion = nodeList.get(0).getProtocol().getCompatibilityVersion();
        return EnumNodeVersion.convertToVersion((int) compatibilityVersion);
    }

    @Override
    public void getChainVersionAsync(RespCallback<EnumNodeVersion.Version> versionRespCallback) {
        getGroupInfoAsync(
                new RespCallback<BcosGroupInfo>() {
                    @Override
                    public void onResponse(BcosGroupInfo bcosGroupInfo) {
                        List<BcosGroupNodeInfo.GroupNodeInfo> nodeList =
                                bcosGroupInfo.getResult().getNodeList();
                        if (nodeList == null || nodeList.isEmpty()) {
                            versionRespCallback.onError(
                                    new Response(-1, "Empty node list in group info."));
                            return;
                        }
                        long compatibilityVersion =
                                nodeList.get(0).getProtocol().getCompatibilityVersion();
                        versionRespCallback.onResponse(
                                EnumNodeVersion.convertToVersion((int) compatibilityVersion));
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        versionRespCallback.onError(errorResponse);
                    }
                });
    }

    @Override
    public void start() {
        if (rpcJniObj != null) {
            rpcJniObj.start();
        }
    }

    @Override
    public void stop() {
        if (rpcJniObj != null) {
            rpcJniObj.stop();
        }
    }

    @Override
    public void destroy() {
        if (rpcJniObj != null) {
            BcosSDKJniObj.destroy(rpcJniObj.getNativePointer());
            rpcJniObj = null;
        }
        if (cryptoSuite != null) {
            cryptoSuite.destroy();
            cryptoSuite = null;
        }
    }

    public static <T extends JsonRpcResponse<?>> ResponseCallback createResponseCallback(
            String method, Class<T> responseType, RespCallback<T> callback) {
        return new ResponseCallback() {
            @Override
            public void onResponse(Response response) {
                try {
                    // decode the transaction
                    T jsonRpcResponse =
                            ClientImpl.parseResponseIntoJsonRpcResponse(
                                    method, response, responseType);
                    callback.onResponse(jsonRpcResponse);
                } catch (ClientException e) {
                    response.setErrorCode(e.getErrorCode());
                    response.setErrorMessage(e.getErrorMessage());
                    callback.onError(response);
                }
            }
        };
    }

    public <T extends JsonRpcResponse<?>> T callRemoteMethod(
            String groupID, String node, JsonRpcRequest<?> request, Class<T> responseType) {
        try {
            CompletableFuture<Response> future = new CompletableFuture<>();

            String data = this.objectMapper.writeValueAsString(request);
            this.rpcJniObj.genericMethod(
                    groupID,
                    node,
                    data,
                    resp -> {
                        Response response = new Response();
                        response.setErrorCode(resp.getErrorCode());
                        response.setErrorMessage(resp.getErrorMessage());
                        response.setContent(resp.getData());

                        if (logger.isTraceEnabled()) {
                            logger.trace(
                                    " callRemoteMethod ===>>> request: {}, response: {}",
                                    request,
                                    response);
                        }

                        future.complete(response);
                    });
            Response response = future.get();
            return ClientImpl.parseResponseIntoJsonRpcResponse(
                    request.getMethod(), response, responseType);
        } catch (ClientException e) {
            throw new ClientException(
                    e.getErrorCode(),
                    e.getErrorMessage(),
                    "callRemoteMethod failed for decode the message exception, error message:"
                            + e.getMessage());
        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
            logger.error("e: ", e);
            throw new ClientException(
                    "callRemoteMethod failed for decode the message exception, error message:"
                            + e.getMessage(),
                    e);
        }
    }

    public <T extends JsonRpcResponse<?>> void asyncCallRemoteMethod(
            String groupID,
            String node,
            JsonRpcRequest<?> request,
            Class<T> responseType,
            RespCallback<T> callback) {

        try {
            this.rpcJniObj.genericMethod(
                    groupID,
                    node,
                    this.objectMapper.writeValueAsString(request),
                    resp -> {
                        Response response = new Response();
                        response.setErrorCode(resp.getErrorCode());
                        response.setErrorMessage(resp.getErrorMessage());
                        response.setContent(resp.getData());

                        if (logger.isTraceEnabled()) {
                            logger.trace(
                                    " ===>>> asyncCallRemoteMethod, group: {}, node: {}, request: {}, response: {}",
                                    groupID,
                                    node,
                                    request,
                                    response);
                        }

                        ResponseCallback responseCallback =
                                createResponseCallback(request.getMethod(), responseType, callback);
                        responseCallback.onResponse(response);
                    });
        } catch (JsonProcessingException e) {
            logger.error("e: ", e);
        }
    }

    public static <T extends JsonRpcResponse<?>> T parseResponseIntoJsonRpcResponse(
            String method, Response response, Class<T> responseType) throws ClientException {
        try {
            if (response.getErrorCode() == 0) {
                // parse the response into JsonRPCResponse
                T jsonRpcResponse =
                        ObjectMapperFactory.getObjectMapper()
                                .readValue(response.getContent(), responseType);
                if (jsonRpcResponse.getError() != null) {
                    logger.error(
                            "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: {}, retErrorMessage: {}, retErrorCode: {}",
                            method,
                            jsonRpcResponse.getError().getMessage(),
                            jsonRpcResponse.getError().getCode());
                    throw new ClientException(
                            jsonRpcResponse.getError().getCode(),
                            jsonRpcResponse.getError().getMessage(),
                            "ErrorMessage: " + jsonRpcResponse.getError().getMessage());
                }
                return jsonRpcResponse;
            } else {
                logger.error(
                        "parseResponseIntoJsonRpcResponse failed, method: {}, group: {}, retErrorMessage: {}, retErrorCode: {}",
                        method,
                        response.getErrorMessage(),
                        response.getErrorCode());
                throw new ClientException(
                        response.getErrorCode(),
                        response.getErrorMessage(),
                        "get response failed, errorCode: "
                                + response.getErrorCode()
                                + ", error message: "
                                + response.getErrorMessage());
            }
        } catch (ClientException e) {
            logger.error(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exception, errorMessage: {}",
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exception, errorMessage: {}",
                    e.getMessage());
            throw new ClientException(e.getMessage(), e);
        }
    }
}
