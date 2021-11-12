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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.model.GroupNodeIniConfig;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcMethods;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcRequest;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.rpc.Rpc;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientImpl implements Client {
    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);

    // ------------basic group info --------------
    private String groupID = "";
    private String chainID;
    private Boolean wasm;
    private Boolean smCrypto;
    // ------------basic group info --------------

    private long blockNumber = 0;

    private final String defaultNode = "";

    private BcosGroupInfo.GroupInfo groupInfo;
    private GroupNodeIniConfig groupNodeIniConfig;

    private CryptoSuite cryptoSuite;
    private final Rpc jniRpcImpl;

    private final ConfigOption configOption;

    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    protected void initGroupInfo() {
        this.groupInfo = getGroupInfo().getResult();

        BcosGroupNodeInfo.GroupNodeInfo groupNodeInfo = groupInfo.getNodeList().get(0);
        String nodeIniConfig = groupNodeInfo.getIniConfig();

        this.groupNodeIniConfig = GroupNodeIniConfig.newIniConfig(nodeIniConfig);
        this.chainID = groupNodeIniConfig.getChain().getChainID();
        this.wasm = groupNodeIniConfig.getExecutor().isWasm();
        this.smCrypto = groupNodeIniConfig.getChain().isSmCrypto();
        this.blockNumber = this.getBlockNumber().getBlockNumber().longValue();

        logger.info(
                "init rpc, chainID: {}, smCrypto: {}, wasm: {}, blockNumber: {}, GroupNodeIniConfig: {}",
                chainID,
                smCrypto,
                wasm,
                blockNumber,
                groupNodeIniConfig);
    }

    protected ClientImpl(String groupID, ConfigOption configOption) throws JniException {
        this.configOption = configOption;
        // init jni sdk
        this.jniRpcImpl = Rpc.build(configOption.getJniConfig());
        // start rpc
        start();

        // set group id
        this.groupID = groupID;

        // init group basic info, eg: chain_id, sm_crypto, is_wasm
        initGroupInfo();

        // init crypto suite
        if (smCrypto) {
            this.cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE, configOption);

        } else {
            this.cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE, configOption);
        }

        logger.info("new ClientImpl end");
    }

    protected ClientImpl(ConfigOption configOption) throws JniException {
        this.configOption = configOption;
        // init jni sdk
        this.jniRpcImpl = Rpc.build(configOption.getJniConfig());
        // start rpc
        start();
        logger.info("new ClientImpl end, group not set");
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

    public Boolean getWasm() {
        return this.wasm;
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
    public BcosTransactionReceipt sendTransaction(String signedTransactionData, boolean withProof) {

        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(
                                this.groupID, this.defaultNode, signedTransactionData, withProof)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void sendTransactionAsync(
            String signedTransactionData, boolean withProof, TransactionCallback callback) {
        this.asyncCallRemoteMethodWithTimeout(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(
                                this.groupID, this.defaultNode, signedTransactionData, withProof)),
                BcosTransactionReceipt.class,
                new RespCallback<BcosTransactionReceipt>() {
                    @Override
                    public void onResponse(BcosTransactionReceipt transactionReceiptWithProof) {
                        callback.onResponse(
                                transactionReceiptWithProof.getTransactionReceipt().get());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(
                                errorResponse.getErrorCode(), errorResponse.getErrorMessage());
                    }
                },
                callback.getTimeout());
    }

    @Override
    public Boolean isWASM() {
        return this.wasm;
    }

    @Override
    public Call call(Transaction transaction) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.CALL,
                        Arrays.asList(
                                this.groupID,
                                this.defaultNode,
                                transaction.getTo(),
                                transaction.getData())),
                Call.class);
    }

    @Override
    public void callAsync(Transaction transaction, RespCallback<Call> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.CALL,
                        Arrays.asList(this.groupID, this.defaultNode, transaction)),
                Call.class,
                callback);
    }

    @Override
    public BlockNumber getBlockNumber() {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_NUMBER,
                        Arrays.asList(this.groupID, this.defaultNode));
        return this.callRemoteMethod(request, BlockNumber.class);
    }

    @Override
    public void getBlockNumberAsync(RespCallback<BlockNumber> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_NUMBER,
                        Arrays.asList(this.groupID, this.defaultNode)),
                BlockNumber.class,
                callback);
    }

    @Override
    public Code getCode(String address) {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CODE,
                        Arrays.asList(this.groupID, this.defaultNode, address));
        return this.callRemoteMethod(request, Code.class);
    }

    @Override
    public void getCodeAsync(String address, RespCallback<Code> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CODE,
                        Arrays.asList(this.groupID, this.defaultNode, address)),
                Code.class,
                callback);
    }

    @Override
    public TotalTransactionCount getTotalTransactionCount() {
        // create request for getTotalTransactionCount
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT,
                        Arrays.asList(this.groupID, this.defaultNode));
        return this.callRemoteMethod(request, TotalTransactionCount.class);
    }

    @Override
    public void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT,
                        Arrays.asList(this.groupID, this.defaultNode)),
                TotalTransactionCount.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByHash(
            String blockHash, boolean onlyHeader, boolean returnFullTransactionObjects) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(
                                this.groupID,
                                this.defaultNode,
                                blockHash,
                                onlyHeader,
                                returnFullTransactionObjects)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByHashAsync(
            String blockHash,
            boolean onlyHeader,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(
                                this.groupID,
                                this.defaultNode,
                                blockHash,
                                onlyHeader,
                                returnFullTransactionObjects)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByNumber(
            BigInteger blockNumber, boolean onlyHeader, boolean fullTransactions) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(
                                this.groupID,
                                this.defaultNode,
                                blockNumber,
                                onlyHeader,
                                fullTransactions)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean onlyHeader,
            boolean fullTransactions,
            RespCallback<BcosBlock> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(
                                this.groupID,
                                this.defaultNode,
                                blockNumber,
                                onlyHeader,
                                fullTransactions)),
                BcosBlock.class,
                callback);
    }

    @Override
    public void getBlockHashByNumberAsync(
            BigInteger blockNumber, RespCallback<BlockHash> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER,
                        Arrays.asList(this.groupID, this.defaultNode, blockNumber)),
                BlockHash.class,
                callback);
    }

    @Override
    public BcosTransaction getTransaction(String transactionHash, Boolean withProof) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupID, this.defaultNode, transactionHash, withProof)),
                BcosTransaction.class);
    }

    @Override
    public void getTransactionAsync(
            String transactionHash, Boolean withProof, RespCallback<BcosTransaction> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupID, this.defaultNode, transactionHash)),
                BcosTransaction.class,
                callback);
    }

    @Override
    public BcosTransactionReceipt getTransactionReceipt(String transactionHash, Boolean withProof) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(this.groupID, this.defaultNode, transactionHash, withProof)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void getTransactionReceiptAsync(
            String transactionHash,
            Boolean withProof,
            RespCallback<BcosTransactionReceipt> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(this.groupID, this.defaultNode, transactionHash, withProof)),
                BcosTransactionReceipt.class,
                callback);
    }

    @Override
    public PendingTxSize getPendingTxSize(String node) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupID, node)),
                PendingTxSize.class);
    }

    @Override
    public void getPendingTxSizeAsync(String node, RespCallback<PendingTxSize> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupID, node)),
                PendingTxSize.class,
                callback);
    }

    @Override
    public PendingTxSize getPendingTxSize() {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PENDING_TX_SIZE,
                        Arrays.asList(this.groupID, this.defaultNode)),
                PendingTxSize.class);
    }

    @Override
    public void getPendingTxSizeAsync(RespCallback<PendingTxSize> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PENDING_TX_SIZE,
                        Arrays.asList(this.groupID, this.defaultNode)),
                PendingTxSize.class,
                callback);
    }

    @Override
    public BigInteger getBlockLimit() {
        /*
        // Notice: add impl in cpp-sdk
        */
        long blk = getBlockNumber().getBlockNumber().longValue();
        if (blk == 0) {
            blk = blockNumber;
        }

        return BigInteger.valueOf(blk).add(BigInteger.valueOf(500));
    }

    @Override
    public GroupPeers getGroupPeers(String groupID) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_GROUP_PEERS,
                        Arrays.asList(this.groupID, this.defaultNode)),
                GroupPeers.class);
    }

    @Override
    public void getGroupPeersAsync(RespCallback<GroupPeers> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_GROUP_PEERS,
                        Arrays.asList(this.groupID, this.defaultNode)),
                GroupPeers.class,
                callback);
    }

    @Override
    public Peers getPeers() {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PEERS, Arrays.asList(this.groupID, this.defaultNode)),
                Peers.class);
    }

    @Override
    public void getPeersAsync(RespCallback<Peers> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PEERS, Arrays.asList(this.groupID, this.defaultNode)),
                Peers.class,
                callback);
    }

    @Override
    public ObserverList getObserverList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_OBSERVER_LIST,
                        Arrays.asList(this.groupID, this.defaultNode)),
                ObserverList.class);
    }

    @Override
    public void getObserverList(RespCallback<ObserverList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_OBSERVER_LIST,
                        Arrays.asList(this.groupID, this.defaultNode)),
                ObserverList.class,
                callback);
    }

    @Override
    public SealerList getSealerList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SEALER_LIST,
                        Arrays.asList(this.groupID, this.defaultNode)),
                SealerList.class);
    }

    @Override
    public void getSealerListAsync(RespCallback<SealerList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SEALER_LIST,
                        Arrays.asList(this.groupID, this.defaultNode)),
                SealerList.class,
                callback);
    }

    @Override
    public PbftView getPbftView() {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PBFT_VIEW,
                        Arrays.asList(this.groupID, this.defaultNode)),
                PbftView.class);
    }

    @Override
    public void getPbftViewAsync(RespCallback<PbftView> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_PBFT_VIEW,
                        Arrays.asList(this.groupID, this.defaultNode)),
                PbftView.class,
                callback);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String key) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY,
                        Arrays.asList(this.groupID, this.defaultNode, key)),
                SystemConfig.class);
    }

    @Override
    public void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY,
                        Arrays.asList(this.groupID, this.defaultNode, key)),
                SystemConfig.class,
                callback);
    }

    @Override
    public SyncStatus getSyncStatus(String node) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupID, node)),
                SyncStatus.class);
    }

    @Override
    public void getSyncStatusAsync(String node, RespCallback<SyncStatus> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupID, node)),
                SyncStatus.class,
                callback);
    }

    @Override
    public SyncStatus getSyncStatus() {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYNC_STATUS,
                        Arrays.asList(this.groupID, this.defaultNode)),
                SyncStatus.class);
    }

    @Override
    public void getSyncStatusAsync(RespCallback<SyncStatus> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYNC_STATUS,
                        Arrays.asList(this.groupID, this.defaultNode)),
                SyncStatus.class,
                callback);
    }

    @Override
    public void getConsensusStatusAsync(String node, RespCallback<ConsensusStatus> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CONSENSUS_STATUS, Arrays.asList(this.groupID, node)),
                ConsensusStatus.class,
                callback);
    }

    @Override
    public void getConsensusStatusAsync(RespCallback<ConsensusStatus> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CONSENSUS_STATUS,
                        Arrays.asList(this.groupID, this.defaultNode)),
                ConsensusStatus.class,
                callback);
    }

    @Override
    public ConsensusStatus getConsensusStatus(String node) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CONSENSUS_STATUS, Arrays.asList(this.groupID, node)),
                ConsensusStatus.class);
    }

    @Override
    public ConsensusStatus getConsensusStatus() {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_CONSENSUS_STATUS,
                        Arrays.asList(this.groupID, this.defaultNode)),
                ConsensusStatus.class);
    }

    @Override
    public BcosGroupList getGroupList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_LIST, Arrays.asList()),
                BcosGroupList.class);
    }

    @Override
    public void getGroupListAsync(RespCallback<BcosGroupList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_LIST, Arrays.asList()),
                BcosGroupList.class,
                callback);
    }

    @Override
    public BcosGroupInfo getGroupInfo() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_INFO, Arrays.asList(groupID)),
                BcosGroupInfo.class);
    }

    @Override
    public void getGroupInfoAsync(RespCallback<BcosGroupInfo> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_INFO, Arrays.asList(groupID)),
                BcosGroupInfo.class,
                callback);
    }

    @Override
    public BcosGroupInfoList getGroupInfoList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_INFO_LIST, Arrays.asList()),
                BcosGroupInfoList.class);
    }

    @Override
    public void getGroupInfoListAsync(RespCallback<BcosGroupInfoList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_GROUP_INFO_LIST, Arrays.asList()),
                BcosGroupInfoList.class,
                callback);
    }

    @Override
    public BcosGroupNodeInfo getGroupNodeInfo(String node) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_GROUP_NODE_INFO, Arrays.asList(groupID, node)),
                BcosGroupNodeInfo.class);
    }

    @Override
    public void getGroupNodeInfoAsync(String node, RespCallback<BcosGroupNodeInfo> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_GROUP_NODE_INFO, Arrays.asList(groupID, node)),
                BcosGroupNodeInfo.class,
                callback);
    }

    @Override
    public void start() {
        if (jniRpcImpl != null) {
            jniRpcImpl.start();
        }
    }

    @Override
    public void stop() {
        if (jniRpcImpl != null) {
            jniRpcImpl.stop();
        }
        Thread.currentThread().interrupt();
    }

    public <T extends JsonRpcResponse> T callRemoteMethod(
            JsonRpcRequest request, Class<T> responseType) {
        try {
            CompletableFuture<Response> future = new CompletableFuture<>();
            String data = this.objectMapper.writeValueAsString(request);
            this.jniRpcImpl.genericMethod(
                    this.groupID,
                    this.defaultNode,
                    data,
                    (resp) -> {
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
            return this.parseResponseIntoJsonRpcResponse(request, response, responseType);
        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
            logger.error("e: ", e);
            throw new ClientException(
                    "callRemoteMethod failed for decode the message exception, error message:"
                            + e.getMessage(),
                    e);
        }
    }

    private <T extends JsonRpcResponse> ResponseCallback createResponseCallback(
            JsonRpcRequest request, Class<T> responseType, RespCallback<T> callback) {
        return new ResponseCallback() {
            @Override
            public void onResponse(Response response) {
                try {
                    // decode the transaction
                    T jsonRpcResponse =
                            ClientImpl.this.parseResponseIntoJsonRpcResponse(
                                    request, response, responseType);
                    callback.onResponse(jsonRpcResponse);
                } catch (ClientException e) {
                    callback.onError(response);
                }
            }
        };
    }

    public <T extends JsonRpcResponse> void asyncCallRemoteMethodWithTimeout(
            JsonRpcRequest request,
            Class<T> responseType,
            RespCallback<T> callback,
            long timeoutValue) {
        try {
            ResponseCallback responseCallback =
                    createResponseCallback(request, responseType, callback);
            responseCallback.setTimeoutValue(timeoutValue);

            this.jniRpcImpl.genericMethod(
                    this.groupID,
                    this.defaultNode,
                    this.objectMapper.writeValueAsString(request),
                    (resp) -> {
                        Response response = new Response();
                        response.setErrorCode(resp.getErrorCode());
                        response.setErrorMessage(resp.getErrorMessage());
                        response.setContent(resp.getData());

                        if (logger.isTraceEnabled()) {
                            logger.trace(
                                    " ===>>> asyncCallRemoteMethodWithTimeout request: {}, response: {}",
                                    request,
                                    response);
                        }

                        ResponseCallback responseCallback1 =
                                createResponseCallback(request, responseType, callback);
                        responseCallback1.onResponse(response);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends JsonRpcResponse> void asyncCallRemoteMethod(
            JsonRpcRequest request, Class<T> responseType, RespCallback<T> callback) {

        try {
            this.jniRpcImpl.genericMethod(
                    this.groupID,
                    this.defaultNode,
                    this.objectMapper.writeValueAsString(request),
                    (resp) -> {
                        Response response = new Response();
                        response.setErrorCode(resp.getErrorCode());
                        response.setErrorMessage(resp.getErrorMessage());
                        response.setContent(resp.getData());

                        if (logger.isTraceEnabled()) {
                            logger.trace(
                                    " ===>>> asyncCallRemoteMethod request: {}, response: {}",
                                    request,
                                    response);
                        }

                        ResponseCallback responseCallback =
                                createResponseCallback(request, responseType, callback);
                        responseCallback.onResponse(response);
                    });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    protected <T extends JsonRpcResponse> T parseResponseIntoJsonRpcResponse(
            JsonRpcRequest request, Response response, Class<T> responseType)
            throws ClientException {
        try {
            if (response.getErrorCode() != 0) {
                throw new ClientException(
                        response.getErrorCode(),
                        response.getErrorMessage(),
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: "
                                + request.getMethod()
                                + " ,group: "
                                + this.groupID
                                + ",retErrorMessage: "
                                + response.getErrorMessage());
            }
            byte[] content = response.getContent();
            // parse the response into JsonRPCResponse
            T jsonRpcResponse = this.objectMapper.readValue(content, responseType);
            if (jsonRpcResponse.getError() != null) {
                logger.error(
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: {}, group: {},  retErrorMessage: {}, retErrorCode: {}",
                        request.getMethod(),
                        this.groupID,
                        jsonRpcResponse.getError().getMessage(),
                        jsonRpcResponse.getError().getCode());
            }
            return jsonRpcResponse;
        } catch (Exception e) {
            logger.error(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exception, errorMessage: {}, groupId: {}",
                    e.getMessage(),
                    this.groupID);
            throw new ClientException(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exceptioned, error message:"
                            + e.getMessage(),
                    e);
        }
    }
}
