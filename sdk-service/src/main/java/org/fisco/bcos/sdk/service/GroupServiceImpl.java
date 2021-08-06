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
    private final String groupId;
    private AtomicLong latestBlockNumber = new AtomicLong(0);
    private List<String> nodeWithLatestBlockNumber = new CopyOnWriteArrayList<String>();

    public GroupServiceImpl(String groupId) {
        this.groupId = groupId;
    }

    public GroupServiceImpl(String groupId, String groupNodeAddress) {
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
        if (this.groupNodeToBlockNumber.containsKey(nodeAddress)) {
            this.groupNodeToBlockNumber.remove(nodeAddress);
            shouldResetLatestBlockNumber = true;
        }
        if (this.nodeWithLatestBlockNumber.contains(nodeAddress)) {
            this.nodeWithLatestBlockNumber.remove(nodeAddress);
            shouldResetLatestBlockNumber = true;
        }
        if (shouldResetLatestBlockNumber) {
            this.resetLatestBlockNumber();
        }
        logger.debug(
                "g:{}, removeNode={}, blockNumberInfoSize={}, latestBlockNumber:{}",
                this.groupId,
                nodeAddress,
                this.groupNodeToBlockNumber.size(),
                this.latestBlockNumber);
        if (this.groupNodeSet.contains(nodeAddress)) {
            this.groupNodeSet.remove(nodeAddress);
            return true;
        }
        return false;
    }

    @Override
    public boolean insertNode(String nodeAddress) {
        if (!this.groupNodeSet.contains(nodeAddress)) {
            this.groupNodeSet.add(nodeAddress);
            logger.debug(
                    "g:{}, insertNode={}, nodeSize={}, blockNumberInfoSize={}",
                    this.groupId,
                    nodeAddress,
                    this.groupNodeSet.size(),
                    this.groupNodeToBlockNumber.size());
            return true;
        }
        if (!this.groupNodeToBlockNumber.containsKey(nodeAddress)) {
            this.groupNodeToBlockNumber.put(nodeAddress, BigInteger.valueOf(0));
        }
        return false;
    }

    @Override
    public void updatePeersBlockNumberInfo(String peerIpAndPort, BigInteger blockNumber) {
        // Note: In order to ensure that the cache information is updated in time when the node is
        // restarted, the block height information of the node must be directly updated
        if (!this.groupNodeToBlockNumber.containsKey(peerIpAndPort)
                || !this.groupNodeToBlockNumber.get(peerIpAndPort).equals(blockNumber)) {
            logger.debug(
                    "updatePeersBlockNumberInfo for {}, updated blockNumber: {}, groupId: {}",
                    peerIpAndPort,
                    blockNumber,
                    this.groupId);
            this.groupNodeToBlockNumber.put(peerIpAndPort, blockNumber);
        }
        if (!this.groupNodeSet.contains(peerIpAndPort)) {
            this.groupNodeSet.add(peerIpAndPort);
        }
        this.updateLatestBlockNumber(peerIpAndPort, blockNumber);
    }

    private void updateLatestBlockNumber(String peerIpAndPort, BigInteger blockNumber) {
        if (blockNumber.longValue() == this.latestBlockNumber.get()
                && !this.nodeWithLatestBlockNumber.contains(peerIpAndPort)) {
            this.nodeWithLatestBlockNumber.add(peerIpAndPort);
        }
        if (blockNumber.longValue() > this.latestBlockNumber.get()) {
            this.latestBlockNumber.getAndSet(blockNumber.longValue());
            this.nodeWithLatestBlockNumber.clear();
            this.nodeWithLatestBlockNumber.add(peerIpAndPort);
        }
        logger.debug(
                "g:{}, updateLatestBlockNumber, latestBlockNumber: {}, nodeWithLatestBlockNumber:{}",
                this.groupId,
                this.latestBlockNumber.get(),
                this.nodeWithLatestBlockNumber.toString());
    }

    private void resetLatestBlockNumber() {
        BigInteger maxBlockNumber = null;
        if (this.groupNodeToBlockNumber.size() == 0) {
            this.latestBlockNumber.getAndSet(BigInteger.ZERO.longValue());
            return;
        }
        for (String groupNode : this.groupNodeToBlockNumber.keySet()) {
            BigInteger blockNumber = this.groupNodeToBlockNumber.get(groupNode);
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
        this.latestBlockNumber.getAndSet(maxBlockNumber.longValue());
        this.nodeWithLatestBlockNumber.clear();
        for (String groupNode : this.groupNodeToBlockNumber.keySet()) {
            BigInteger blockNumber = this.groupNodeToBlockNumber.get(groupNode);
            if (this.latestBlockNumber.equals(blockNumber)) {
                this.nodeWithLatestBlockNumber.add(groupNode);
            }
        }
        logger.debug(
                "g:{}, resetLatestBlockNumber, latestBlockNumber: {}, nodeWithLatestBlockNumber:{}, maxBlockNumber: {}",
                this.groupId,
                this.latestBlockNumber.get(),
                this.nodeWithLatestBlockNumber.toString(),
                maxBlockNumber);
    }

    @Override
    public BigInteger getLatestBlockNumber() {
        return BigInteger.valueOf(this.latestBlockNumber.get());
    }

    @Override
    public String getNodeWithTheLatestBlockNumber() {
        try {
            // in case of nodeWithLatestBlockNumber modified
            final List<String> tmpNodeWithLatestBlockNumber =
                    new ArrayList<>(this.nodeWithLatestBlockNumber);
            // the case that the sdk is allowed to access all the connected node, select the first
            // connected node to send the request
            if (tmpNodeWithLatestBlockNumber.size() > 0) {
                // Note: when the nodeWithLatestBlockNumber modified, and the random value
                // calculated after the modification, and the  nodeWithLatestBlockNumber.get is
                // called after the modification, this function will throw
                // ArrayIndexOutOfBoundsException
                int random = (int) (Math.random() * (tmpNodeWithLatestBlockNumber.size()));
                return tmpNodeWithLatestBlockNumber.get(random);
            }
        } catch (Exception e) {
            logger.warn(
                    "getNodeWithTheLatestBlockNumber failed for {}, select the node to send message randomly",
                    e);
        }
        // select the first element
        if (!this.groupNodeSet.isEmpty()) {
            return this.groupNodeSet.iterator().next();
        }
        return null;
    }

    @Override
    public boolean existPeer(String peer) {
        return this.groupNodeSet.contains(peer);
    }
}
