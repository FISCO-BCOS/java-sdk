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
package org.fisco.bcos.sdk.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.PeerSelectRule;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;

public interface GroupManagerService {
    public static final BigInteger BLOCK_LIMIT = BigInteger.valueOf(500);

    /**
     * Update the group list information of the node
     *
     * @param peerIpAndPort: Node ip and port information
     * @param groupList: Group list of nodes
     */
    void updateGroupInfo(String peerIpAndPort, List<String> groupList);

    Channel getChannel();
    /**
     * update the block number information for the specified group
     *
     * @param groupId: the specified groupId
     * @param currentBlockNumber: the current blockNumber
     */
    void updateBlockNumberInfo(Integer groupId, String peerInfo, BigInteger currentBlockNumber);

    List<ConnectionInfo> getGroupConnectionInfo(Integer groupId);

    /**
     * get available ip and port info of specified group
     *
     * @param groupId: get the connection info of the group
     * @return: the available ip and port info of the group
     */
    List<String> getGroupAvailablePeers(Integer groupId);

    /**
     * Get block limit of specified group
     *
     * @param groupId: The specified groupId
     * @return: the blockLimit(needed by the transaction module)
     */
    BigInteger getBlockLimitByGroup(Integer groupId);

    /**
     * Get the node list of the specified group
     *
     * @param groupId: The group id
     * @return: The node list that started the group
     */
    Set<String> getGroupNodeList(Integer groupId);

    /**
     * Get the group list of specified node
     *
     * @param nodeAddress: The ip and port info of the node
     * @return: List of groups started by the node
     */
    List<String> getGroupInfoByNodeInfo(String nodeAddress);

    /**
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @return: response of the node located in the specified group
     */
    Response sendMessageToGroup(Integer groupId, Message message);

    /**
     * Send messages to nodes in the group according to specified rules (If multiple nodes are
     * filtered out, only select one of them to send the message)
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @param rule: Rule for filtering the target nodes
     * @return: callback to be called after receiving response
     */
    Response sendMessageToGroupByRule(Integer groupId, Message message, PeerSelectRule rule);

    /**
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @param callback: callback to be called after receiving response
     */
    void asyncSendMessageToGroup(Integer groupId, Message message, ResponseCallback callback);

    /**
     * Send messages to nodes in the group according to specified rules (If multiple nodes are
     * filtered out, only select one of them to send the message)
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @param rule: Rules for filtering the target nodes
     * @param callback:: Function to be called after receiving response
     */
    void asyncSendMessageToGroupByRule(
            Integer groupId, Message message, PeerSelectRule rule, ResponseCallback callback);

    /**
     * Broadcast messages to all the nodes of the specified group
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     */
    void broadcastMessageToGroup(Integer groupId, Message message);

    void asyncSendTransaction(
            Integer groupId,
            Message transactionData,
            TransactionCallback callback,
            ResponseCallback responseCallback);

    void eraseTransactionSeq(String seq);

    NodeVersion getNodeVersion(String peerInfo);

    Integer getCryptoType(String peerInfo);

    ConfigOption getConfig();
}
