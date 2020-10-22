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
package org.fisco.bcos.sdk.client.protocol.request;

public class JsonRpcMethods {
    /** define the method name for all jsonRPC interfaces */
    // the interface related to the group
    public static final String GET_BLOCK_NUMBER = "getBlockNumber";

    public static final String GET_NODE_VERSION = "getClientVersion";
    public static final String GET_TOPIC_SUBSCRIBERS = "getAmopTopicSubscribers";
    public static final String GET_PBFT_VIEW = "getPbftView";
    public static final String GET_SEALER_LIST = "getSealerList";
    public static final String GET_SYSTEM_CONFIG_BY_KEY = "getSystemConfigByKey";
    public static final String GET_OBSERVER_LIST = "getObserverList";
    public static final String GET_CONSENSUS_STATUS = "getConsensusStatus";
    public static final String GET_SYNC_STATUS = "getSyncStatus";
    public static final String GET_GROUP_PEERS = "getGroupPeers";
    public static final String GET_BLOCK_BY_HASH = "getBlockByHash";
    public static final String GET_BLOCKHEADER_BY_HASH = "getBlockHeaderByHash";
    public static final String GET_BLOCK_BY_NUMBER = "getBlockByNumber";
    public static final String GET_BLOCKHEADER_BY_NUMBER = "getBlockHeaderByNumber";
    public static final String GET_BLOCKHASH_BY_NUMBER = "getBlockHashByNumber";
    public static final String GET_TRANSACTION_BY_HASH = "getTransactionByHash";
    public static final String GET_TRANSACTION_BY_BLOCKHASH_AND_INDEX =
            "getTransactionByBlockHashAndIndex";
    public static final String GET_TRANSACTION_BY_BLOCKNUMBER_AND_INDEX =
            "getTransactionByBlockNumberAndIndex";
    public static final String GET_TRANSACTIONRECEIPT = "getTransactionReceipt";
    public static final String GET_PENDING_TX_SIZE = "getPendingTxSize";
    public static final String GET_PENDING_TRANSACTIONS = "getPendingTransactions";
    public static final String CALL = "call";
    public static final String SEND_RAWTRANSACTION = "sendRawTransaction";
    public static final String SEND_RAWTRANSACTION_AND_GET_PROOF = "sendRawTransactionAndGetProof";
    public static final String GET_CODE = "getCode";
    public static final String GET_TOTAL_TRANSACTION_COUNT = "getTotalTransactionCount";
    public static final String GET_TRANSACTION_BY_HASH_WITH_PROOF = "getTransactionByHashWithProof";
    public static final String GET_TRANSACTION_RECEIPT_BY_HASH_WITH_PROOF =
            "getTransactionReceiptByHashWithProof";

    // the interface related to the node
    public static final String GET_CLIENT_VERSION = "getClientVersion";
    public static final String GET_PEERS = "getPeers";
    public static final String GET_GROUP_LIST = "getGroupList";
    public static final String GET_NODEIDLIST = "getNodeIDList";

    // the interface related to group-runtime-manager
    public static final String GENERATE_GROUP = "generateGroup";
    public static final String START_GROUP = "startGroup";
    public static final String STOP_GROUP = "stopGroup";
    public static final String REMOVE_GROUP = "removeGroup";
    public static final String RECOVER_GROUP = "recoverGroup";
    public static final String QUERY_GROUP_STATUS = "queryGroupStatus";

    public static final String GET_BATCH_RECEIPT_BY_BLOCK_NUMBER_AND_RANGE =
            "getBatchReceiptsByBlockNumberAndRange";
    public static final String GET_BATCH_RECEIPT_BY_BLOCK_HASH_AND_RANGE =
            "getBatchReceiptsByBlockHashAndRange";

    private JsonRpcMethods() {}
}
