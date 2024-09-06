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
package org.fisco.bcos.sdk.v3.client.protocol.request;

public class JsonRpcMethods {
    /** define the method name for all jsonRPC interfaces */
    public static final String GET_BLOCK_NUMBER = "getBlockNumber";

    public static final String GET_PBFT_VIEW = "getPbftView";
    public static final String GET_CONSENSUS_STATUS = "getConsensusStatus";
    public static final String GET_SEALER_LIST = "getSealerList";
    public static final String GET_NODE_LIST_BY_TYPE = "getNodeListByType";
    public static final String GET_SYSTEM_CONFIG_BY_KEY = "getSystemConfigByKey";
    public static final String GET_OBSERVER_LIST = "getObserverList";
    public static final String GET_SYNC_STATUS = "getSyncStatus";
    public static final String GET_BLOCK_BY_HASH = "getBlockByHash";
    public static final String GET_BLOCK_BY_NUMBER = "getBlockByNumber";
    public static final String GET_BLOCKHASH_BY_NUMBER = "getBlockHashByNumber";
    public static final String GET_TRANSACTION_BY_HASH = "getTransaction";
    public static final String GET_TRANSACTION_RECEIPT = "getTransactionReceipt";
    public static final String GET_PENDING_TX_SIZE = "getPendingTxSize";
    public static final String CALL = "call";
    public static final String SEND_TRANSACTION = "sendTransaction";
    public static final String GET_CODE = "getCode";
    public static final String GET_ABI = "getABI";
    public static final String GET_TOTAL_TRANSACTION_COUNT = "getTotalTransactionCount";
    public static final String GET_NODE_INFO = "getNodeInfo";
    public static final String GET_PEERS = "getPeers";
    public static final String GET_GROUP_PEERS = "getGroupPeers";

    public static final String GET_GROUP_LIST = "getGroupList";
    public static final String GET_GROUP_INFO = "getGroupInfo";
    public static final String GET_GROUP_INFO_LIST = "getGroupInfoList";
    public static final String GET_GROUP_NODE_INFO = "getGroupNodeInfo";

    // event related interface
    public static final String NEW_BLOCK_FILTER = "newBlockFilter";
    public static final String NEW_PENDING_TX_FILTER = "newPendingTransactionFilter";
    public static final String NEW_FILTER = "newFilter";
    public static final String GET_FILTER_CHANGES = "getFilterChanges";
    public static final String UNINSTALL_FILTER = "uninstallFilter";
    public static final String GET_LOGS = "getLogs";
    public static final String GET_FILTER_LOGS = "getFilterLogs";

    private JsonRpcMethods() {}
}
