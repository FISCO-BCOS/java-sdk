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
    public static final String GET_BLOCK_NUMBER = "getBlockNumber";

    public static final String GET_PBFT_VIEW = "getPbftView";
    public static final String GET_CONSENSUS_STATUS = "getConsensusStatus";
    public static final String GET_SEALER_LIST = "getSealerList";
    public static final String GET_SYSTEM_CONFIG_BY_KEY = "getSystemConfigByKey";
    public static final String GET_OBSERVER_LIST = "getObserverList";
    public static final String GET_SYNC_STATUS = "getSyncStatus";
    public static final String GET_BLOCK_BY_HASH = "getBlockByHash";
    public static final String GET_BLOCK_BY_NUMBER = "getBlockByNumber";
    public static final String GET_BLOCKHASH_BY_NUMBER = "getBlockHashByNumber";
    public static final String GET_TRANSACTION_BY_HASH = "getTransaction";
    public static final String GET_TRANSACTIONRECEIPT = "getTransactionReceipt";
    public static final String GET_PENDING_TX_SIZE = "getPendingTxSize";
    public static final String CALL = "call";
    public static final String SEND_TRANSACTION = "sendTransaction";
    public static final String GET_CODE = "getCode";
    public static final String GET_TOTAL_TRANSACTION_COUNT = "getTotalTransactionCount";

    // the interface related to the node
    public static final String GET_NODE_INFO = "getNodeInfo";
    public static final String GET_PEERS = "getPeers";

    // the interface related to the group
    public static final String CREATE_GROUP = "createGroup";
    public static final String EXPAND_GROUP_NODE = "expandGroupNode";
    public static final String REMOVE_GROUP = "removeGroup";
    public static final String REMOVE_GROUP_NODE = "removeGroupNode";
    public static final String RECOVER_GROUP = "recoverGroup";
    public static final String RECOVER_GROUP_NODE = "recoverGroupNode";
    public static final String START_NODE = "startNode";
    public static final String STOP_NODE = "stopNode";
    public static final String GET_GROUP_LIST = "getGroupList";
    public static final String GET_GROUP_INFO = "getGroupInfo";
    public static final String GET_GROUP_NODE_INFO = "getGroupNodeInfo";

    private JsonRpcMethods() {}
}
