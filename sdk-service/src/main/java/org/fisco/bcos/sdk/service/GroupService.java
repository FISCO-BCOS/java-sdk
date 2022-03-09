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
import java.util.Set;

public interface GroupService {
    /**
     * Get the node information of the group
     *
     * @return Nodes' ip and port list
     */
    Set<String> getGroupNodesInfo();

    /**
     * remove node from the group
     *
     * @param nodeAddress the ip and port of the removed node
     * @return if nodes in the original list that needed to be removed return True, else false.
     */
    boolean removeNode(String nodeAddress);

    /**
     * add nodeInfo to the group
     *
     * @param nodeAddress the node ip and port
     * @return if nodes in the original list that needed to be inserted return True, else false.
     */
    boolean insertNode(String nodeAddress);

    /**
     * update the latest block number of the specified group
     *
     * @param peerIpAndPort the node that notify the block number info
     * @param blockNumber the notified block number
     */
    void updatePeersBlockNumberInfo(String peerIpAndPort, BigInteger blockNumber);

    /**
     * Get latest block number of this group
     *
     * @return block number
     */
    BigInteger getLatestBlockNumber();

    /**
     * Get node which has the latest block number
     *
     * @return the node
     */
    String getNodeWithTheLatestBlockNumber();

    /**
     * Check the node is exit in the group
     *
     * @param peer ip and port
     * @return whether peer exit
     */
    boolean existPeer(String peer);
}
