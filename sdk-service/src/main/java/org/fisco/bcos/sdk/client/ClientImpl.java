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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

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

public class ClientImpl implements Client {
    protected final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    private final Integer groupId;
    private final Integer DefaultGroupId = Integer.valueOf(1);
    private final CryptoSuite cryptoSuite;
    private final NodeInfo nodeInfo;
    private final Connection connection;

    protected ClientImpl(Connection connection, Integer groupId, CryptoSuite cryptoSuite) {
        this.groupId = groupId;
        this.cryptoSuite = cryptoSuite;
        this.nodeInfo =
                this.callRemoteMethod(
                        new JsonRpcRequest(JsonRpcMethods.GET_NODE_INFO, Arrays.asList()),
                        NodeInfo.class);
        // FIXME: get node info by call getNodeInfo
        //        nodeVersion;
        this.connection = connection;
        // send request to the group, and get the blockNumber information
        getBlockLimit();
    }

    protected ClientImpl(Connection connection) {
        this.groupId = null;
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
    public Integer getGroupId() {
        return this.groupId;
    }

    @Override
    public TransactionReceiptWithProof sendRawTransaction(String signedTransactionData, boolean withProof) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(signedTransactionData, withProof)),
                TransactionReceiptWithProof.class);
    }

    @Override
    public void sendRawTransactionAsync(
            String signedTransactionData, boolean withProof, TransactionCallback callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.SEND_TRANSACTION,
                        Arrays.asList(signedTransactionData, withProof)),
                TransactionReceiptWithProof.class,
                new RespCallback<TransactionReceiptWithProof>() {
                    @Override
                    public void onResponse(TransactionReceiptWithProof transactionReceiptWithProof) {
                        callback.onResponse(transactionReceiptWithProof.getReceiptAndProof().getReceipt());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        callback.onError(errorResponse.getErrorCode(),errorResponse.getErrorMessage());
                    }
                });
    }

    @Override
    public Call call(Transaction transaction) {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.CALL, Arrays.asList(this.groupId, transaction)),
                Call.class);
    }

    @Override
    public void callAsync(Transaction transaction, RespCallback<Call> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.CALL, Arrays.asList(this.groupId, transaction)),
                Call.class,
                callback);
    }

    @Override
    public BlockNumber getBlockNumber() {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.groupId));
        return this.callRemoteMethod(request, BlockNumber.class);
    }

    @Override
    public void getBlockNumberAsync(RespCallback<BlockNumber> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_BLOCK_NUMBER, Arrays.asList(this.groupId)),
                BlockNumber.class,
                callback);
    }

    @Override
    public Code getCode(String address) {
        // create request
        JsonRpcRequest request =
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(this.groupId, address));
        return this.callRemoteMethod(request, Code.class);
    }

    @Override
    public void getCodeAsync(String address, RespCallback<Code> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_CODE, Arrays.asList(this.groupId, address)),
                Code.class,
                callback);
    }

    @Override
    public TotalTransactionCount getTotalTransactionCount() {
        // create request for getTotalTransactionCount
        JsonRpcRequest request =
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList(this.groupId));
        return this.callRemoteMethod(request, TotalTransactionCount.class);
    }

    @Override
    public void getTotalTransactionCountAsync(RespCallback<TotalTransactionCount> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TOTAL_TRANSACTION_COUNT, Arrays.asList(this.groupId)),
                TotalTransactionCount.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByHash(String blockHash, boolean returnFullTransactionObjects) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(this.groupId, blockHash, returnFullTransactionObjects)),
                BcosBlock.class);
    }

    @Override
    public void getBlockByHashAsync(
            String blockHash,
            boolean returnFullTransactionObjects,
            RespCallback<BcosBlock> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_HASH,
                        Arrays.asList(this.groupId, blockHash, returnFullTransactionObjects)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BcosBlock getBlockByNumber(
            BigInteger blockNumber, boolean returnFullTransactionObjects) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCK_BY_NUMBER,
                        Arrays.asList(
                                this.groupId,
                                String.valueOf(blockNumber),
                                returnFullTransactionObjects)),
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
                                this.groupId,
                                String.valueOf(blockNumber),
                                returnFullTransactionObjects)),
                BcosBlock.class,
                callback);
    }

    @Override
    public BlockHash getBlockHashByNumber(BigInteger blockNumber) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER,
                        Arrays.asList(this.groupId, String.valueOf(blockNumber))),
                BlockHash.class);
    }

    @Override
    public void getBlockHashByNumberAsync(
            BigInteger blockNumber, RespCallback<BlockHash> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHASH_BY_NUMBER,
                        Arrays.asList(this.groupId, String.valueOf(blockNumber))),
                BlockHash.class,
                callback);
    }

    @Override
    public BcosBlockHeader getBlockHeaderByHash(String blockHash, boolean returnSignatureList) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHEADER_BY_HASH,
                        Arrays.asList(this.groupId, blockHash, returnSignatureList)),
                BcosBlockHeader.class);
    }

    @Override
    public void getBlockHeaderByHashAsync(
            String blockHash, boolean returnSignatureList, RespCallback<BcosBlockHeader> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHEADER_BY_HASH,
                        Arrays.asList(this.groupId, blockHash, returnSignatureList)),
                BcosBlockHeader.class,
                callback);
    }

    @Override
    public BcosBlockHeader getBlockHeaderByNumber(
            BigInteger blockNumber, boolean returnSignatureList) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHEADER_BY_NUMBER,
                        Arrays.asList(
                                this.groupId, String.valueOf(blockNumber), returnSignatureList)),
                BcosBlockHeader.class);
    }

    @Override
    public void getBlockHeaderByNumberAsync(
            BigInteger blockNumber,
            boolean returnSignatureList,
            RespCallback<BcosBlockHeader> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_BLOCKHEADER_BY_NUMBER,
                        Arrays.asList(
                                this.groupId, String.valueOf(blockNumber), returnSignatureList)),
                BcosBlockHeader.class,
                callback);
    }

    @Override
    public BcosTransaction getTransactionByHash(String transactionHash) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransaction.class);
    }

    @Override
    public void getTransactionByHashAsync(
            String transactionHash, RespCallback<BcosTransaction> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTION_BY_HASH,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransaction.class,
                callback);
    }

    @Override
    public BcosTransactionReceipt getTransactionReceipt(String transactionHash) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransactionReceipt.class);
    }

    @Override
    public void getTransactionReceiptAsync(
            String transactionHash, RespCallback<BcosTransactionReceipt> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_TRANSACTIONRECEIPT,
                        Arrays.asList(this.groupId, transactionHash)),
                BcosTransactionReceipt.class,
                callback);
    }

    @Override
    public PendingTxSize getPendingTxSize() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupId)),
                PendingTxSize.class);
    }

    @Override
    public void getPendingTxSizeAsync(RespCallback<PendingTxSize> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PENDING_TX_SIZE, Arrays.asList(this.groupId)),
                PendingTxSize.class,
                callback);
    }

    @Override
    public BigInteger getBlockLimit() {
        Integer groupId = Integer.valueOf(this.groupId);
        return this.getBlockNumber().getBlockNumber().add(BigInteger.valueOf(500));
    }

    @Override
    public Peers getPeers() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList(DefaultGroupId)),
                Peers.class);
    }

    @Override
    public void getPeersAsync(RespCallback<Peers> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PEERS, Arrays.asList(this.groupId)),
                Peers.class,
                callback);
    }

    @Override
    public ObserverList getObserverList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.groupId)),
                ObserverList.class);
    }

    @Override
    public void getObserverList(RespCallback<ObserverList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_OBSERVER_LIST, Arrays.asList(this.groupId)),
                ObserverList.class,
                callback);
    }

    @Override
    public SealerList getSealerList() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.groupId)),
                SealerList.class);
    }

    @Override
    public void getSealerListAsync(RespCallback<SealerList> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SEALER_LIST, Arrays.asList(this.groupId)),
                SealerList.class,
                callback);
    }

    @Override
    public PbftView getPbftView() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.groupId)),
                PbftView.class);
    }

    @Override
    public void getPbftViewAsync(RespCallback<PbftView> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_PBFT_VIEW, Arrays.asList(this.groupId)),
                PbftView.class,
                callback);
    }

    @Override
    public SystemConfig getSystemConfigByKey(String key) {
        return this.callRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.groupId, key)),
                SystemConfig.class);
    }

    @Override
    public void getSystemConfigByKeyAsync(String key, RespCallback<SystemConfig> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(
                        JsonRpcMethods.GET_SYSTEM_CONFIG_BY_KEY, Arrays.asList(this.groupId)),
                SystemConfig.class,
                callback);
    }

    @Override
    public SyncStatus getSyncStatus() {
        return this.callRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupId)),
                SyncStatus.class);
    }

    @Override
    public void getSyncStatus(RespCallback<SyncStatus> callback) {
        this.asyncCallRemoteMethod(
                new JsonRpcRequest(JsonRpcMethods.GET_SYNC_STATUS, Arrays.asList(this.groupId)),
                SyncStatus.class,
                callback);
    }

    class SynchronousTransactionCallback extends TransactionCallback {
        public TransactionReceipt receipt;
        public Semaphore semaphore = new Semaphore(1, true);

        SynchronousTransactionCallback() {
            try {
                semaphore.acquire(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void onTimeout() {
            super.onTimeout();
            semaphore.release();
        }

        // wait until get the transactionReceipt
        @Override
        public void onResponse(TransactionReceipt receipt) {
            this.receipt = receipt;
            semaphore.release();
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
                    objectMapper.writeValueAsString(request),
                    new ResponseCallback() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                // decode the transaction
                                parseResponseIntoJsonRpcResponse(request, response.getContent(), responseType);
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
            response = this.connection.callMethod(objectMapper.writeValueAsString(request));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null) {
            throw new ClientException(
                    "callRemoteMethod to "
                            + this.groupId
                            + " failed for select peers to send message failed, please make sure that the group exists");
        }
        return this.parseResponseIntoJsonRpcResponse(request, response, responseType);
    }

    public <T extends JsonRpcResponse> void asyncCallRemoteMethod(
            JsonRpcRequest request,
            Class<T> responseType,
            RespCallback<T> callback) {
        try {
            this.connection.asyncCallMethod(
                    objectMapper.writeValueAsString(request),
                    new ResponseCallback() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                // decode the transaction
                                T jsonRpcResponse =
                                        parseResponseIntoJsonRpcResponse(request, response.getContent(), responseType);
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
            T jsonRpcResponse = objectMapper.readValue(response, responseType);
            if (jsonRpcResponse.getError() != null) {
                logger.error(
                        "parseResponseIntoJsonRpcResponse failed for non-empty error message, method: {}, group: {},  retErrorMessage: {}, retErrorCode: {}",
                        request.getMethod(),
                        this.groupId,
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
