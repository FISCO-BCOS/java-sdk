/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.client.protocol.response.GroupList;
import org.fisco.bcos.sdk.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.client.protocol.response.Peers;
import org.fisco.bcos.sdk.client.protocol.response.PendingTransactions;
import org.fisco.bcos.sdk.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.junit.Assert;
import org.junit.Test;

public class BcosSDKTest
{
    private static final String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.yaml").getPath();
    @Test
    public void testClient()
    {
        BcosSDK sdk = new BcosSDK(configFile);
        // check groupList
        Assert.assertTrue(sdk.getChannel().getAvailablePeer().size() >= 1);
        for(String endPoint: sdk.getChannel().getAvailablePeer())
        {
            List<String> groupInfo = sdk.getGroupManagerService().getGroupInfoByNodeInfo(endPoint);
            Assert.assertEquals(1, groupInfo.size());
            Assert.assertEquals("1", groupInfo.get(0));
            Assert.assertTrue( sdk.getGroupManagerService().getGroupNodeList(1).contains(endPoint));
        }
        Assert.assertEquals(sdk.getChannel().getAvailablePeer().size(), sdk.getGroupManagerService().getGroupNodeList(1).size());
        // get the client
        Client client = sdk.getClient(Integer.valueOf(1));

        // test getBlockNumber
        BlockNumber blockNumber = client.getBlockNumber();
        Assert.assertEquals(BigInteger.valueOf(0), blockNumber.getBlockNumber());

        // test getBlockByNumber
        BcosBlock block = client.getBlockByNumber(blockNumber.getBlockNumber(), false);
        Assert.assertEquals(blockNumber.getBlockNumber(), block.getBlock().getNumber());
        // the genesis block with zero transactions
        Assert.assertEquals(0, block.getBlock().getTransactions().size());
        // the genesis block with 0 sealer
        Assert.assertEquals(0, block.getBlock().getSealerList().size());
        Assert.assertEquals("0x0", block.getBlock().getSealer());
        Assert.assertEquals("0x0000000000000000000000000000000000000000000000000000000000000000", block.getBlock().getParentHash());
        Assert.assertEquals("0x0000000000000000000000000000000000000000000000000000000000000000", block.getBlock().getDbHash());
        Assert.assertEquals("0x0000000000000000000000000000000000000000000000000000000000000000", block.getBlock().getStateRoot());

        // test getBlockByHash
        BcosBlock block2 = client.getBlockByHash(block.getBlock().getHash(), false);
        Assert.assertEquals(block2.getBlock().getHash(), block.getBlock().getHash());

        // get blockHash
        BlockHash blockHash = client.getBlockHashByNumber(blockNumber.getBlockNumber());
        Assert.assertEquals(blockHash.getBlockHashByNumber(), block.getBlock().getHash());

        // Note: FISCO BCOS supported_version >= v2.6.0 has this RPC interface
        // get blockHeader
        BcosBlockHeader blockHeader = client.getBlockHeaderByHash(blockHash.getBlockHashByNumber(), true);
        if(blockHeader.getError() == null) {
            Assert.assertEquals(blockNumber.getBlockNumber(), blockHeader.getBlockHeader().getNumber());
            Assert.assertEquals(block.getBlock().getHash(), blockHeader.getBlockHeader().getHash());

            BcosBlockHeader blockHeader2 = client.getBlockHeaderByNumber(blockNumber.getBlockNumber(), true);
            Assert.assertEquals(blockHeader.getBlockHeader(), blockHeader2.getBlockHeader());
        }



        // get SealerList
        SealerList sealerList = client.getSealerList();
        Assert.assertEquals(4, sealerList.getSealerList().size());

        // get observerList
        ObserverList observerList = client.getObserverList();
        Assert.assertEquals(0, observerList.getObserverList().size());

        // getPeers
        Peers peers = client.getPeers();
        Assert.assertEquals("agency", peers.getPeers().get(0).getAgency());
        Set<String> sealerSet = new HashSet<String>(sealerList.getSealerList());
        for(int i = 0; i < peers.getPeers().size(); i++)
        {
            Assert.assertTrue(sealerSet.contains(peers.getPeers().get(i).getNodeID()));
        }

        // get NodeVersion
        NodeVersion nodeVersion = client.getNodeVersion();
        Assert.assertTrue(nodeVersion.getNodeVersion() != null);

        // getSystemConfig
        SystemConfig systemConfig = client.getSystemConfigByKey("tx_count_limit");
        Assert.assertEquals("1000", systemConfig.getSystemConfig());
        systemConfig = client.getSystemConfigByKey("tx_gas_limit");
        Assert.assertEquals("300000000", systemConfig.getSystemConfig());

        // get groupPeers
        GroupPeers groupPeers = client.getGroupPeers();
        Assert.assertEquals(4, groupPeers.getGroupPeers().size());
        for(String peer : groupPeers.getGroupPeers())
        {
            Assert.assertTrue(sealerSet.contains(peer));
        }
        // get PendingTxSize
        PendingTxSize pendingTxSize = client.getPendingTxSize();
        Assert.assertEquals(BigInteger.valueOf(0), pendingTxSize.getPendingTxSize());

        // get pendingTransactions
        PendingTransactions pendingTransactions = client.getPendingTransaction();
        Assert.assertEquals(0, pendingTransactions.getPendingTransactions().size());

        // get pbftView
        client.getPbftView();

        // getSyncStatus
        SyncStatus syncStatus = client.getSyncStatus();
        Assert.assertEquals("0", syncStatus.getSyncStatus().getTxPoolSize());
        Assert.assertEquals(blockHash.getBlockHashByNumber(), "0x" + syncStatus.getSyncStatus().getLatestHash());
        Assert.assertEquals(blockHash.getBlockHashByNumber(), "0x" + syncStatus.getSyncStatus().getGenesisHash());
        Assert.assertEquals( blockHash.getBlockHashByNumber(), "0x" + syncStatus.getSyncStatus().getKnownLatestHash());
        Assert.assertEquals(blockNumber.getBlockNumber(), new BigInteger(syncStatus.getSyncStatus().getKnownHighestNumber()));
        Assert.assertEquals(peers.getPeers().size(), syncStatus.getSyncStatus().getPeers().size());

        BigInteger blockLimit = client.getBlockLimit();
        Assert.assertEquals(blockNumber.getBlockNumber().add(GroupManagerService.BLOCK_LIMIT), blockLimit);

        // test getGroupList
        GroupList groupList = client.getGroupList();
        Assert.assertEquals(1, groupList.getGroupList().size());
        Assert.assertEquals("1", groupList.getGroupList().get(0));

        // test getConsensusStatus
        ConsensusStatus consensusStatus = client.getConsensusStatus();
        Assert.assertTrue(consensusStatus.getConsensusStatus().getViewInfos().size() > 0);
        Assert.assertEquals(4, consensusStatus.getConsensusStatus().getBaseConsensusInfo().getSealerList().size());
        for(String sealer : consensusStatus.getConsensusStatus().getBaseConsensusInfo().getSealerList())
        {
            Assert.assertTrue(sealerSet.contains(sealer));
        }
        Assert.assertEquals("1", consensusStatus.getConsensusStatus().getBaseConsensusInfo().getAccountType());
        Assert.assertEquals("1", consensusStatus.getConsensusStatus().getBaseConsensusInfo().getMaxFaultyNodeNum());
        Assert.assertEquals("true", consensusStatus.getConsensusStatus().getBaseConsensusInfo().getAllowFutureBlocks());
        Assert.assertEquals("true", consensusStatus.getConsensusStatus().getBaseConsensusInfo().getOmitEmptyBlock());
        Assert.assertEquals(blockNumber.getBlockNumber(), new BigInteger(consensusStatus.getConsensusStatus().getBaseConsensusInfo().getHighestblockNumber()));
        Assert.assertEquals(blockHash.getBlockHashByNumber(), consensusStatus.getConsensusStatus().getBaseConsensusInfo().getHighestblockHash());
    }
}