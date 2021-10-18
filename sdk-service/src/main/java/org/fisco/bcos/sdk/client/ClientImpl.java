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
import java.util.concurrent.Semaphore;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.NodeInfo;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcMethods;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcRequest;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientImpl implements Client {
    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    private final String group;
    private final String chainId;
    private final Boolean wasm;
    private final Boolean smCrypto;
    private final CryptoSuite cryptoSuite;
    private final NodeInfoResponse nodeInfoResponse;
    private long blockNumber;

    protected ClientImpl(ConfigOption configOption) {
        // get node info by call getNodeInfo
        this.nodeInfoResponse =
                this.callRemoteMethod(
                        new JsonRpcRequest(JsonRpcMethods.GET_NODE_INFO, Arrays.asList()),
                        NodeInfoResponse.class);
        this.chainId = this.nodeInfoResponse.getNodeInfo().getChainId();
        this.group = this.nodeInfoResponse.getNodeInfo().getGroupId();
        this.wasm = this.nodeInfoResponse.getNodeInfo().getWasm();
        this.smCrypto = this.nodeInfoResponse.getNodeInfo().getSmCrypto();
        if (configOption.getCryptoMaterialConfig().getUseSmCrypto()) {
            this.cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE, configOption);
            logger.info("create client for sm_type: {}", true);
        } else {
            this.cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE, configOption);
            logger.info("create client for sm_type: {}", false);
        }

        this.blockNumber = this.getBlockNumber().getBlockNumber().longValue();
        logger.info("ClientImpl blockNumber: {}", this.blockNumber);
    }

    protected ClientImpl() {
        this.group = null;
        this.chainId = null;
        this.cryptoSuite = null;
        this.nodeInfoResponse = null;
        this.wasm = false;
        this.smCrypto = null;
    }

    @Override
    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    @Override
    public NodeInfo getNodeInfo() {
        return this.nodeInfoResponse.getNodeInfo();
    }

    @Override
    public Integer getCryptoType() {
        return this.cryptoSuite.getCryptoTypeConfig();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public String getChainId() {
        return this.chainId;
    }

    @Override
    public BcosTransactionReceipt sendTransaction(String signedTransactionData, boolean withProof) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(this.group, signedTransactionData, withProof)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void sendTransactionAsync(
            String signedTransactionData, boolean withProof, TransactionCallback callback) {
        this.asyncCallRemoteMethodWithTimeout(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(this.group, signedTransactionData, withProof)),
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
        logger.info("call remote method {}", Hex.toHexString(transaction.getData()));
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.CALL,
                        Arrays.asList(this.group, transaction.getTo(), transaction.getData())),
                Call.class);
    }

    @Override
    public void callAsync(Transaction transaction, RespCallback<Call> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.CALL, Arrays.asList(this.group, transaction)),
                Call.class,
                callback);
    }

    @Override
    public BlockNumber getBlockNumber() {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.group));
        return this.callRemoteMethod(request, BlockNumber.class);
    }

    @Override
    public void getBlockNumberAsync(RespCallback<BlockNumber> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.group)),
                BlockNumber.class,
                callback);
    }

    @Override
    public Code getCode(String address) {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(this.group, address));
        return this.callRemoteMethod(request, Code.class);
    }

    @Override
    public void getCodeAsync(String address, RespCallback<Code> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(this.group, address)),
                Code.class,
                callback);
    }

    @Override
    public TotalTransactionCount getTotalTransactionCount() {
        // create request for getTotalTransactionCount
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList(this.group));
        return this.callRemoteMethod(request, TotalTransactionCount.class);
    }

    @Override
    public void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList(this.group)),
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
                                this.group, blockHash, onlyHeader, returnFullTransactionObjects)),
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
                                this.group, blockHash, onlyHeader, returnFullTransactionObjects)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByNumber(
            BigInteger blockNumber, boolean onlyHeader, boolean fullTransactions) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(this.group, blockNumber, onlyHeader, fullTransactions)),
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
                        Arrays.asList(this.group, blockNumber, onlyHeader, fullTransactions)),
                BcosBlock.class,
                callback);
    }

    @Override
    public void getBlockHashByNumberAsync(
            BigInteger blockNumber, RespCallback<BlockHash> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER,
                        Arrays.asList(this.group, blockNumber)),
                BlockHash.class,
                callback);
    }

    @Override
    public BcosTransaction getTransaction(String transactionHash, Boolean withProof) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.group, transactionHash, withProof)),
                BcosTransaction.class);
    }

    @Override
    public void getTransactionAsync(
            String transactionHash, Boolean withProof, RespCallback<BcosTransaction> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.group, transactionHash)),
                BcosTransaction.class,
                callback);
    }

    @Override
    public BcosTransactionReceipt getTransactionReceipt(String transactionHash, Boolean withProof) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(this.group, transactionHash, withProof)),
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
                        Arrays.asList(this.group, transactionHash, withProof)),
                BcosTransactionReceipt.class,
                callback);
    }

    @Override
    public PendingTxSize getPendingTxSize() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.group)),
                PendingTxSize.class);
    }

    @Override
    public void getPendingTxSizeAsync(RespCallback<PendingTxSize> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.group)),
                PendingTxSize.class,
                callback);
    }

    @Override
    public BigInteger getBlockLimit() {
        // TODO:
        /*
        long blk = connection.getBlockNumber();
        if (blk == 0) {
            blk = blockNumber;
        }
        return BigInteger.valueOf(blk).add(BigInteger.valueOf(500));
        */
        return BigInteger.ZERO;
    }

    @Override
    public Peers getPeers() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList(this.group)),
                Peers.class);
    }

    @Override
    public void getPeersAsync(RespCallback<Peers> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList(this.group)),
                Peers.class,
                callback);
    }

    @Override
    public ObserverList getObserverList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.group)),
                ObserverList.class);
    }

    @Override
    public void getObserverList(RespCallback<ObserverList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.group)),
                ObserverList.class,
                callback);
    }

    @Override
    public SealerList getSealerList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.group)),
                SealerList.class);
    }

    @Override
    public void getSealerListAsync(RespCallback<SealerList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.group)),
                SealerList.class,
                callback);
    }

    @Override
    public PbftView getPbftView() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.group)),
                PbftView.class);
    }

    @Override
    public void getPbftViewAsync(RespCallback<PbftView> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.group)),
                PbftView.class,
                callback);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String key) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.group, key)),
                SystemConfig.class);
    }

    @Override
    public void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.group, key)),
                SystemConfig.class,
                callback);
    }

    @Override
    public SyncStatus getSyncStatus() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.group)),
                SyncStatus.class);
    }

    @Override
    public void getSyncStatus(RespCallback<SyncStatus> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.group)),
                SyncStatus.class,
                callback);
    }

    public Boolean getWasm() {
        return this.wasm;
    }

    public Boolean getSmCrypto() {
        return this.smCrypto;
    }

    class SynchronousTransactionCallback extends TransactionCallback {
        public TransactionReceipt receipt;
        public Semaphore semaphore = new Semaphore(1, true);

        SynchronousTransactionCallback() {
            try {
                this.semaphore.acquire(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void onTimeout() {
            super.onTimeout();
            this.semaphore.release();
        }

        // wait until get the transactionReceipt
        @Override
        public void onResponse(TransactionReceipt receipt) {
            this.receipt = receipt;
            this.semaphore.release();
        }
    }

    @Override
    public void stop() {
        Thread.currentThread().interrupt();
    }

    public <T extends JsonRpcResponse> T callRemoteMethod(
            JsonRpcRequest request, Class<T> responseType) {

        Response response = null;
        // TODO:
        /*
        try {
            response = this.connection.callMethod(this.objectMapper.writeValueAsString(request));
        } catch (IOException e) {
            logger.warn("callRemoteMethod failed, " + e.getMessage());
            throw new ClientException("RPC call failed" + e.getMessage());
        }
        */
        if (response == null) {
            throw new ClientException("RPC call failed, please try again");
        }
        return this.parseResponseIntoJsonRpcResponse(request, response, responseType);
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
            // TODO:
            /*
            this.connection.asyncCallMethod(
                    this.objectMapper.writeValueAsString(request), responseCallback);
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends JsonRpcResponse> void asyncCallRemoteMethod(
            JsonRpcRequest request, Class<T> responseType, RespCallback<T> callback) {
        try {
            ResponseCallback responseCallback =
                    createResponseCallback(request, responseType, callback);
            // TODO:
            /*
            this.connection.asyncCallMethod(
                    this.objectMapper.writeValueAsString(request), responseCallback);
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected <T extends JsonRpcResponse> T parseResponseIntoJsonRpcResponse(
            JsonRpcRequest request, Response response, Class<T> responseType) {
        try {
            if (response.getErrorCode() != 0) {
                throw new ClientException(
                        response.getErrorCode(),
                        response.getErrorMessage(),
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: "
                                + request.getMethod()
                                + " ,group: "
                                + this.group
                                + ",retErrorMessage: "
                                + response.getErrorMessage());
            }
            String responseContent = response.getContent();
            // parse the response into JsonRPCResponse
            T jsonRpcResponse = this.objectMapper.readValue(responseContent, responseType);
            if (jsonRpcResponse.getError() != null) {
                logger.error(
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: {}, group: {},  retErrorMessage: {}, retErrorCode: {}",
                        request.getMethod(),
                        this.group,
                        jsonRpcResponse.getError().getMessage(),
                        jsonRpcResponse.getError().getCode());
                throw new ClientException(
                        jsonRpcResponse.getError().getCode(),
                        jsonRpcResponse.getError().getMessage(),
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: "
                                + request.getMethod()
                                + " ,group: "
                                + this.group
                                + ",retErrorMessage: "
                                + jsonRpcResponse.getError().getMessage());
            }
            return jsonRpcResponse;
        } catch (JsonProcessingException e) {
            logger.error(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exception, errorMessage: {}, groupId: {}",
                    e.getMessage(),
                    this.group);
            throw new ClientException(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exceptioned, error message:"
                            + e.getMessage(),
                    e);
        }
    }
}
