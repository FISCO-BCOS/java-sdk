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
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcMethods;
import org.fisco.bcos.sdk.client.protocol.request.JsonRpcRequest;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.network.Connection;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class ClientImpl implements Client {
    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    private final String groupId;
    private final String chainId;
    private final CryptoSuite cryptoSuite;
    private final NodeInfo nodeInfo;
    private final Connection connection;

    protected ClientImpl(Connection connection, CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
        this.connection = connection;
        // get node info by call getNodeInfo
        this.nodeInfo =
                this.callRemoteMethod(
                        new JsonRpcRequest(JsonRpcMethods.GET_NODE_INFO, Arrays.asList()),
                        NodeInfo.class);
        this.chainId = this.nodeInfo.getNodeInfo().getChainId() == null ? "test_chain" : this.nodeInfo.getNodeInfo().getChainId();
        this.groupId = this.nodeInfo.getNodeInfo().getGroupId() == null ? "test_group" : this.nodeInfo.getNodeInfo().getGroupId();
        // send request to the group, and get the blockNumber information
        this.getBlockLimit();
    }

    protected ClientImpl(Connection connection) {
        this.groupId = null;
        this.chainId = null;
        this.cryptoSuite = null;
        this.nodeInfo = null;
        this.connection = null;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    @Override
    public NodeInfo.NodeInformation getNodeInfo() {
        return this.nodeInfo.getNodeInfo();
    }

    @Override
    public Integer getCryptoType() {
        return this.cryptoSuite.getCryptoTypeConfig();
    }

    @Override
    public String getGroupId() {
        return this.groupId;
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
                        Arrays.asList(signedTransactionData, withProof)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void sendTransactionAsync(
            String signedTransactionData, boolean withProof, TransactionCallback callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(signedTransactionData, withProof)),
                BcosTransactionReceipt.class,
                new RespCallback<BcosTransactionReceipt>() {
                    @Override
                    public void onResponse(BcosTransactionReceipt transactionReceiptWithProof) {
                        callback.onResponse(transactionReceiptWithProof.getTransactionReceipt());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse.getErrorCode(), errorResponse.getErrorMessage());
                    }
                });
    }

    @Override
    public Call call(Transaction transaction) {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.CALL, Arrays.asList(transaction.getTo(), transaction.getData())),
                Call.class);
    }

    @Override
    public void callAsync(Transaction transaction, RespCallback<Call> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.CALL, Arrays.asList(transaction)),
                Call.class,
                callback);
    }

    @Override
    public BlockNumber getBlockNumber() {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList());
        return this.callRemoteMethod(request, BlockNumber.class);
    }

    @Override
    public void getBlockNumberAsync(RespCallback<BlockNumber> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList()),
                BlockNumber.class,
                callback);
    }

    @Override
    public Code getCode(String address) {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(address));
        return this.callRemoteMethod(request, Code.class);
    }

    @Override
    public void getCodeAsync(String address, RespCallback<Code> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(address)),
                Code.class,
                callback);
    }

    @Override
    public TotalTransactionCount getTotalTransactionCount() {
        // create request for getTotalTransactionCount
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList());
        return this.callRemoteMethod(request, TotalTransactionCount.class);
    }

    @Override
    public void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList()),
                TotalTransactionCount.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByHash(String blockHash, boolean onlyHeader, boolean returnFullTransactionObjects) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(blockHash, onlyHeader, returnFullTransactionObjects)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByHashAsync(
            String blockHash, boolean onlyHeader,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(blockHash, onlyHeader, returnFullTransactionObjects)),
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
                                blockNumber,
                                onlyHeader, fullTransactions)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByNumberAsync(
            BigInteger blockNumber,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(
                                blockNumber,
                                returnFullTransactionObjects)),
                BcosBlock.class,
                callback);
    }

    @Override
    public void getBlockHashByNumberAsync(
            BigInteger blockNumber, RespCallback<BlockHash> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER,
                        Arrays.asList(blockNumber)),
                BlockHash.class,
                callback);
    }

    @Override
    public BcosTransaction getTransaction(String transactionHash) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(transactionHash)),
                BcosTransaction.class);
    }

    @Override
    public void getTransactionAsync(
            String transactionHash, RespCallback<BcosTransaction> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(transactionHash)),
                BcosTransaction.class,
                callback);
    }

    @Override
    public BcosTransactionReceipt getTransactionReceipt(String transactionHash) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(transactionHash)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void getTransactionReceiptAsync(
            String transactionHash, RespCallback<BcosTransactionReceipt> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(transactionHash)),
                BcosTransactionReceipt.class,
                callback);
    }

    @Override
    public PendingTxSize getPendingTxSize() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList()),
                PendingTxSize.class);
    }

    @Override
    public void getPendingTxSizeAsync(RespCallback<PendingTxSize> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList()),
                PendingTxSize.class,
                callback);
    }

    @Override
    public BigInteger getBlockLimit() {
        return this.getBlockNumber().getBlockNumber().add(BigInteger.valueOf(500));
    }

    @Override
    public Peers getPeers() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList()),
                Peers.class);
    }

    @Override
    public void getPeersAsync(RespCallback<Peers> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList()),
                Peers.class,
                callback);
    }

    @Override
    public ObserverList getObserverList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList()),
                ObserverList.class);
    }

    @Override
    public void getObserverList(RespCallback<ObserverList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList()),
                ObserverList.class,
                callback);
    }

    @Override
    public SealerList getSealerList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList()),
                SealerList.class);
    }

    @Override
    public void getSealerListAsync(RespCallback<SealerList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList()),
                SealerList.class,
                callback);
    }

    @Override
    public PbftView getPbftView() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList()),
                PbftView.class);
    }

    @Override
    public void getPbftViewAsync(RespCallback<PbftView> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList()),
                PbftView.class,
                callback);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String key) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(key)),
                SystemConfig.class);
    }

    @Override
    public void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList()),
                SystemConfig.class,
                callback);
    }

    @Override
    public SyncStatus getSyncStatus() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList()),
                SyncStatus.class);
    }

    @Override
    public void getSyncStatus(RespCallback<SyncStatus> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList()),
                SyncStatus.class,
                callback);
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

    public <T extends JsonRpcResponse> void asyncSendTransactionToGroup(
            JsonRpcRequest request, TransactionCallback callback, Class<T> responseType) {
        try {
            this.connection.asyncCallMethod(
                    this.objectMapper.writeValueAsString(request),
                    new ResponseCallback() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                // decode the transaction
                                ClientImpl.this.parseResponseIntoJsonRpcResponse(request, response.getContent(), responseType);
                                // FIXME: call callback
                            } catch (ClientException e) {
                                // fake the transactionReceipt
                                callback.onError(e.getErrorCode(), e.getErrorMessage());
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends JsonRpcResponse> T callRemoteMethod(
            JsonRpcRequest request, Class<T> responseType) {

        String response = null;
        try {
            response = this.connection.callMethod(this.objectMapper.writeValueAsString(request));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null) {
            throw new ClientException(
                    "callRemoteMethod failed for select peers to send message failed, please make sure that the group exists");
        }
        // System.out.println("Executing response: " + response);
        return this.parseResponseIntoJsonRpcResponse(request, response, responseType);
    }

    public <T extends JsonRpcResponse> void asyncCallRemoteMethod(
            JsonRpcRequest request,
            Class<T> responseType,
            RespCallback<T> callback) {
        try {
            this.connection.asyncCallMethod(
                    this.objectMapper.writeValueAsString(request),
                    new ResponseCallback() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                // decode the transaction
                                T jsonRpcResponse =
                                        ClientImpl.this.parseResponseIntoJsonRpcResponse(request, response.getContent(), responseType);
                                callback.onResponse(jsonRpcResponse);
                            } catch (ClientException e) {
                                callback.onError(response);
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected <T extends JsonRpcResponse> T parseResponseIntoJsonRpcResponse(
            JsonRpcRequest request, String response, Class<T> responseType) {
        try {
            // parse the response into JsonRPCResponse
            T jsonRpcResponse = this.objectMapper.readValue(response, responseType);
            if (jsonRpcResponse.getError() != null) {
                logger.error(
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: {}, group: {},  retErrorMessage: {}, retErrorCode: {}",
                        request.getMethod(),

                        jsonRpcResponse.getError().getMessage(),
                        jsonRpcResponse.getError().getCode());
                throw new ClientException(
                        jsonRpcResponse.getError().getCode(),
                        jsonRpcResponse.getError().getMessage(),
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: "
                                + request.getMethod()
                                + " ,group: "
                                + this.groupId
                                + ",retErrorMessage: "
                                + jsonRpcResponse.getError().getMessage());
            }
            return jsonRpcResponse;


        } catch (JsonProcessingException e) {
            logger.error(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exception, errorMessage: {}, groupId: {}",
                    e.getMessage(),
                    this.groupId);
            throw new ClientException(
                    "parseResponseIntoJsonRpcResponse failed for decode the message exceptioned, error message:"
                            + e.getMessage(),
                    e);
        }
    }
}
