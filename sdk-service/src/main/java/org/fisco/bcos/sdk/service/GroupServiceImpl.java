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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupServiceImpl implements GroupService {

    private static Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    private ConcurrentHashMap<String, BigInteger> groupNodeToBlockNumber =
            new ConcurrentHashMap<>();
    private Set<String> groupNodeSet = new CopyOnWriteArraySet<>();
    private final Integer groupId;
    private AtomicLong latestBlockNumber = new AtomicLong(0);
    private List<String> nodeWithLatestBlockNumber = new CopyOnWriteArrayList<String>();

    public GroupServiceImpl(Integer groupId) {
        this.groupId = groupId;
    }

    public GroupServiceImpl(Integer groupId, String groupNodeAddress) {
        this.groupId = groupId;
        this.groupNodeSet.add(groupNodeAddress);
        logger.debug("insert group: {} for peer {}", groupId, groupNodeAddress);
    }

    @Override
    public Set<String> getGroupNodesInfo() {
        return this.groupNodeSet;
    }

    @Override
    public boolean removeNode(String nodeAddress) {
        boolean shouldResetLatestBlockNumber = false;
        if (groupNodeToBlockNumber.containsKey(nodeAddress)) {
            groupNodeToBlockNumber.remove(nodeAddress);
            shouldResetLatestBlockNumber = true;
        }
        if (nodeWithLatestBlockNumber.contains(nodeAddress)) {
            nodeWithLatestBlockNumber.remove(nodeAddress);
            shouldResetLatestBlockNumber = true;
        }
        if (shouldResetLatestBlockNumber) {
            resetLatestBlockNumber();
        }
        logger.debug(
                "g:{}, removeNode={}, blockNumberInfoSize={}, latestBlockNumber:{}",
                groupId,
                nodeAddress,
                this.groupNodeToBlockNumber.size(),
                latestBlockNumber);
        if (groupNodeSet.contains(nodeAddress)) {
            groupNodeSet.remove(nodeAddress);
            return true;
        }
        return false;
    }

    @Override
    public boolean insertNode(String nodeAddress) {
        if (!groupNodeSet.contains(nodeAddress)) {
            groupNodeSet.add(nodeAddress);
            logger.debug(
                    "g:{}, insertNode={}, nodeSize={}, blockNumberInfoSize={}",
                    groupId,
                    nodeAddress,
                    this.groupNodeSet.size(),
                    this.groupNodeToBlockNumber.size());
            return true;
        }
        if (!groupNodeToBlockNumber.containsKey(nodeAddress)) {
            groupNodeToBlockNumber.put(nodeAddress, BigInteger.valueOf(0));
        }
        return false;
    }

    @Override
    public void updatePeersBlockNumberInfo(String peerIpAndPort, BigInteger blockNumber) {
        // Note: In order to ensure that the cache information is updated in time when the node is
        // restarted, the block height information of the node must be directly updated
        if (!groupNodeToBlockNumber.containsKey(peerIpAndPort)
                || !groupNodeToBlockNumber.get(peerIpAndPort).equals(blockNumber)) {
            logger.debug(
                    "updatePeersBlockNumberInfo for {}, updated blockNumber: {}, groupId: {}",
                    peerIpAndPort,
                    blockNumber,
                    groupId);
            groupNodeToBlockNumber.put(peerIpAndPort, blockNumber);
        }
        if (!groupNodeSet.contains(peerIpAndPort)) {
            groupNodeSet.add(peerIpAndPort);
        }
        updateLatestBlockNumber(peerIpAndPort, blockNumber);
    }

    private void updateLatestBlockNumber(String peerIpAndPort, BigInteger blockNumber) {
        if (blockNumber.longValue() == latestBlockNumber.get()
                && !nodeWithLatestBlockNumber.contains(peerIpAndPort)) {
            nodeWithLatestBlockNumber.add(peerIpAndPort);
        }
        if (blockNumber.longValue() > latestBlockNumber.get()) {
            latestBlockNumber.getAndSet(blockNumber.longValue());
            nodeWithLatestBlockNumber.clear();
            nodeWithLatestBlockNumber.add(peerIpAndPort);
        }
        logger.debug(
                "g:{}, updateLatestBlockNumber, latestBlockNumber: {}, nodeWithLatestBlockNumber:{}",
                groupId,
                latestBlockNumber.get(),
                nodeWithLatestBlockNumber.toString());
    }

    private void resetLatestBlockNumber() {
        BigInteger maxBlockNumber = null;
        if (groupNodeToBlockNumber.size() == 0) {
            latestBlockNumber.getAndSet(BigInteger.ZERO.longValue());
            return;
        }
        for (String groupNode : groupNodeToBlockNumber.keySet()) {
            BigInteger blockNumber = groupNodeToBlockNumber.get(groupNode);
            if (blockNumber == null) {
                continue;
            }
            if (maxBlockNumber == null || blockNumber.compareTo(maxBlockNumber) > 0) {
                maxBlockNumber = blockNumber;
            }
        }

        if (maxBlockNumber == null) {
            return;
        }
        latestBlockNumber.getAndSet(maxBlockNumber.longValue());
        nodeWithLatestBlockNumber.clear();
        for (String groupNode : groupNodeToBlockNumber.keySet()) {
            BigInteger blockNumber = groupNodeToBlockNumber.get(groupNode);
            if (latestBlockNumber.equals(blockNumber)) {
                nodeWithLatestBlockNumber.add(groupNode);
            }
        }
        logger.debug(
                "g:{}, resetLatestBlockNumber, latestBlockNumber: {}, nodeWithLatestBlockNumber:{}, maxBlockNumber: {}",
                groupId,
                latestBlockNumber.get(),
                nodeWithLatestBlockNumber.toString(),
                maxBlockNumber);
    }

    @Override
    public BigInteger getLatestBlockNumber() {
        return BigInteger.valueOf(this.latestBlockNumber.get());
    }

    @Override
    public String getNodeWithTheLatestBlockNumber() {
        try {
            // the case that the sdk is allowed to access all the connected node, select the first
            // connected node to send the request
            if (nodeWithLatestBlockNumber.size() > 0) {
                // Note: when the nodeWithLatestBlockNumber modified, and the random value
                // calculated after the modification, and the  nodeWithLatestBlockNumber.get is
                // called after the modification, this function will throw
                // ArrayIndexOutOfBoundsException
                int random = (int) (Math.random() * (nodeWithLatestBlockNumber.size()));
                return nodeWithLatestBlockNumber.get(random);
            }
        } catch (Exception e) {
            logger.error(
                    "getNodeWithTheLatestBlockNumber for {}, select the node to send message randomly",
                    e.getMessage());
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
}
