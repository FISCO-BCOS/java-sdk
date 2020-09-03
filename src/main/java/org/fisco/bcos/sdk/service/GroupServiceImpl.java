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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupServiceImpl implements GroupService {

    private static Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    private ConcurrentHashMap<String, BigInteger> groupNodeToBlockNumber =
            new ConcurrentHashMap<>();
    private Set<String> groupNodeSet = Collections.synchronizedSet(new HashSet<>());
    private final Integer groupId;
    private AtomicLong latestBlockNumber = new AtomicLong(0);
    private String nodeWithLatestBlockNumber;

    public GroupServiceImpl(Integer groupId) {
        this.groupId = groupId;
    }

    public GroupServiceImpl(Integer groupId, String groupNodeAddress) {
        this.groupId = groupId;
        this.groupNodeSet.add(groupNodeAddress);
    }

    @Override
    public Set<String> getGroupNodesInfo() {
        return this.groupNodeSet;
    }

    @Override
    public void removeNode(String nodeAddress) {
        if (groupNodeSet.contains(nodeAddress)) {
            groupNodeSet.remove(nodeAddress);
        }
        if (groupNodeToBlockNumber.containsKey(nodeAddress)) {
            groupNodeToBlockNumber.remove(nodeAddress);
        }
        resetLatestBlockNumber();
        logger.debug(
                "g:{}, removeNode={}, nodeSize={}, blockNumberInfoSize={}, latestBlockNumber:{}",
                groupId,
                nodeAddress,
                this.groupNodeSet.size(),
                this.groupNodeToBlockNumber.size(),
                latestBlockNumber);
    }

    @Override
    public void insertNode(String nodeAddress) {
        if (!groupNodeSet.contains(nodeAddress)) {
            groupNodeSet.add(nodeAddress);
            logger.debug(
                    "g:{}, insertNode={}, nodeSize={}, blockNumberInfoSize={}",
                    groupId,
                    nodeAddress,
                    this.groupNodeSet.size(),
                    this.groupNodeToBlockNumber.size());
        }
        if (!groupNodeToBlockNumber.containsKey(nodeAddress)) {
            groupNodeToBlockNumber.put(nodeAddress, BigInteger.valueOf(0));
        }
    }

    @Override
    public void updatePeersBlockNumberInfo(String peerIpAndPort, BigInteger blockNumber) {
        // Note: In order to ensure that the cache information is updated in time when the node is
        // restarted, the block height information of the node must be directly updated
        if (!groupNodeToBlockNumber.containsKey(peerIpAndPort)
                || !groupNodeToBlockNumber.get(peerIpAndPort).equals(blockNumber)) {
            logger.debug(
                    "updatePeersBlockNumberInfo for {}, updated blockNumber: {}",
                    peerIpAndPort,
                    blockNumber);
            groupNodeToBlockNumber.put(peerIpAndPort, blockNumber);
        }
        if (!groupNodeSet.contains(peerIpAndPort)) {
            groupNodeSet.add(peerIpAndPort);
        }
        // calculate the latestBlockNumber
        resetLatestBlockNumber();
    }

    private void resetLatestBlockNumber() {
        BigInteger maxBlockNumber = null;
        String maxBlockNumberNode = "";
        for (String groupNode : groupNodeToBlockNumber.keySet()) {
            BigInteger blockNumber = groupNodeToBlockNumber.get(groupNode);
            if (blockNumber == null) {
                continue;
            }
            if (maxBlockNumber == null || blockNumber.compareTo(maxBlockNumber) > 0) {
                maxBlockNumber = blockNumber;
                maxBlockNumberNode = groupNode;
            }
        }
        if (maxBlockNumber != null && !maxBlockNumberNode.equals("")) {
            // for select the node with the highest blockNumber to send requests randomly
            if (latestBlockNumber.equals(maxBlockNumber)) {
                nodeWithLatestBlockNumber = maxBlockNumberNode;
            }
            if (nodeWithLatestBlockNumber == null || !latestBlockNumber.equals(maxBlockNumber)) {
                logger.debug(
                        "g:{}, resetLatestBlockNumber, latestBlockNumber: {}, nodeWithLatestBlockNumber:{},  maxBlockNumber: {}",
                        groupId,
                        maxBlockNumber,
                        maxBlockNumberNode,
                        maxBlockNumber);
                latestBlockNumber.getAndSet(maxBlockNumber.longValue());
                nodeWithLatestBlockNumber = maxBlockNumberNode;
            }
        }
    }

    @Override
    public BigInteger getLastestBlockNumber() {
        return BigInteger.valueOf(this.latestBlockNumber.get());
    }

    @Override
    public String getNodeWithTheLatestBlockNumber() {
        // the case that the sdk is allowed to access all the connected node, select the first
        // connected node to send the request
        if (nodeWithLatestBlockNumber != null) {
            return nodeWithLatestBlockNumber;
        }
        // select the first element
        if (!groupNodeSet.isEmpty()) {
            return groupNodeSet.iterator().next();
        }
        return null;
    }

    @Override
    public boolean existPeer(String peer) {
        return groupNodeSet.contains(peer);
    }

    @Override
    public void resetLatestNodeInfo() {
        List<String> nodesWithHighestBlockNumber = new ArrayList<>();
        for (String node : groupNodeToBlockNumber.keySet()) {
            BigInteger blockNumber = groupNodeToBlockNumber.get(node);
            if (blockNumber != null
                    && blockNumber.compareTo(BigInteger.valueOf(latestBlockNumber.get())) >= 0) {
                nodesWithHighestBlockNumber.add(node);
            }
        }
        int random = (int) (Math.random() * (nodesWithHighestBlockNumber.size()));
        String selectedNode = nodesWithHighestBlockNumber.get(random);
        if (selectedNode != null) {
            if (selectedNode.equals(nodeWithLatestBlockNumber)) {
                nodeWithLatestBlockNumber =
                        nodesWithHighestBlockNumber.get(
                                (random + 1) % (nodesWithHighestBlockNumber.size()));
            }
            nodeWithLatestBlockNumber = selectedNode;
        }
    }
}
