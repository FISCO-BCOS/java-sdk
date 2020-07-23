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
import org.fisco.bcos.sdk.channel.PeerSelectRule;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.Response;

public interface GroupManagerService {

    /**
     * Update the group list information of the node
     *
     * @param peerIpAndPort: Node ip and port information
     * @param groupList: Group list of nodes
     */
    void updateGroupInfo(String peerIpAndPort, List<String> groupList);

    /**
     * Get the blockNumber notify message from the AMOP module, parse the package and update the
     * latest block height of each group
     *
     * @param peerIpAndPort: Node ip and port
     * @param blockNumberNotifyMessage: the blockNumber notify message
     */
    void updateBlockNumberInfo(String peerIpAndPort, Message blockNumberNotifyMessage);

    /**
     * update the block number information for the specified group
     *
     * @param groupId: the specified groupId
     * @param currentBlockNumber: the current blockNumber
     */
    void updateBlockNumber(int groupId, BigInteger currentBlockNumber);

    /**
     * Get block limit of specified group
     *
     * @param groupId: The specified groupId
     * @return: the blockLimit(needed by the transaction module)
     */
    BigInteger getBlockLimitByGroup(int groupId);

    /**
     * Get the node list of the specified group
     *
     * @param groupId: The group id
     * @return: The node list that started the group
     */
    Set<String> getGroupNodeList(int groupId);

    /**
     * Get the group list of specified node
     *
     * @param nodeAddress: The ip and port info of the node
     * @return: List of groups started by the node
     */
    Set<Integer> getGroupInfoByNodeInfo(String nodeAddress);

    /**
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @return: response of the node located in the specified group
     */
    Response sendMessageToGroup(int groupId, Message message);

    /**
     * Send messages to nodes in the group according to specified rules (If multiple nodes are
     * filtered out, only select one of them to send the message)
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @param rule: Rule for filtering the target nodes
     * @return: callback to be called after receiving response
     * @param callback:
     */
    Response sendMessageToGroupByRule(
            int groupId, Message message, PeerSelectRule rule, ResponseCallback callback);

    /**
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @param callback: callback to be called after receiving response
     */
    void asyncSendMessageToGroup(int groupId, Message message, ResponseCallback callback);

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
            int groupId, Message message, PeerSelectRule rule, ResponseCallback callback);

    /**
     * Send messages to nodes in the group according to specified rules
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     * @param rule: Rules for filtering the target nodes
     */
    void multicastMessageToGroup(int groupId, Message message, PeerSelectRule rule);

    /**
     * Broadcast messages to all the nodes of the specified group
     *
     * @param groupId: The group the message is sent to
     * @param message: The message to be sent
     */
    void broadcastMessageToGroup(int groupId, Message message);
}
