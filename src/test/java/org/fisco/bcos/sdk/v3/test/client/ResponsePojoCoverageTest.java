/*
 * Copyright 2014-2020 [fisco-dev]
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
 */
package org.fisco.bcos.sdk.v3.test.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeGenesisInfo;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupStatus;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfoList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.client.protocol.response.Code;
import org.fisco.bcos.sdk.v3.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.v3.client.protocol.response.Peers;
import org.fisco.bcos.sdk.v3.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.model.NodeVersion;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

/**
 * Coverage-oriented unit tests that exercise constructors, getters/setters, and
 * toString/equals/hashCode of the response and model POJOs by constructing them directly (no
 * network / no node).
 */
public class ResponsePojoCoverageTest {

    // ---------------------- JsonRpcResponse base ----------------------
    @Test
    public void testJsonRpcResponseBase() {
        BlockNumber resp = new BlockNumber();
        resp.setId(42L);
        resp.setJsonrpc("2.0");
        resp.setResult("0x10");
        resp.setRawResponse("{\"raw\":true}");
        Assert.assertEquals(42L, resp.getId());
        Assert.assertEquals("2.0", resp.getJsonrpc());
        Assert.assertEquals("0x10", resp.getResult());
        Assert.assertEquals("{\"raw\":true}", resp.getRawResponse());
        Assert.assertFalse(resp.hasError());
        Assert.assertNull(resp.getError());

        JsonRpcResponse.Error error = new JsonRpcResponse.Error(7, "boom");
        error.setData("detail");
        Assert.assertEquals(7, error.getCode());
        Assert.assertEquals("boom", error.getMessage());
        Assert.assertEquals("detail", error.getData());
        error.setCode(8);
        error.setMessage("other");
        Assert.assertEquals(8, error.getCode());
        Assert.assertEquals("other", error.getMessage());

        resp.setError(error);
        Assert.assertTrue(resp.hasError());
        Assert.assertEquals(error, resp.getError());

        JsonRpcResponse.Error sameError = new JsonRpcResponse.Error(8, "other");
        sameError.setData("detail");
        Assert.assertEquals(error, sameError);
        Assert.assertEquals(error.hashCode(), sameError.hashCode());
        Assert.assertNotEquals(error, new JsonRpcResponse.Error(99, "diff"));
        Assert.assertNotEquals(error, "not-an-error");
    }

    // ---------------------- BlockNumber / PendingTxSize / PbftView ----------------------
    @Test
    public void testBlockNumberDecode() {
        BlockNumber blockNumber = new BlockNumber();
        blockNumber.setResult("0x100");
        Assert.assertEquals(BigInteger.valueOf(256), blockNumber.getBlockNumber());
    }

    @Test
    public void testPendingTxSizeDecode() {
        PendingTxSize pendingTxSize = new PendingTxSize();
        pendingTxSize.setResult("99");
        Assert.assertEquals(BigInteger.valueOf(99), pendingTxSize.getPendingTxSize());
    }

    @Test
    public void testPbftViewDecode() {
        PbftView pbftView = new PbftView();
        pbftView.setResult("0x7");
        Assert.assertEquals(BigInteger.valueOf(7), pbftView.getPbftView());
    }

    // ---------------------- BlockHash / Code ----------------------
    @Test
    public void testBlockHash() {
        BlockHash blockHash = new BlockHash();
        blockHash.setResult("0xdeadbeef");
        Assert.assertEquals("0xdeadbeef", blockHash.getBlockHashByNumber());
    }

    @Test
    public void testCode() {
        Code code = new Code();
        code.setResult("0x6060");
        Assert.assertEquals("0x6060", code.getCode());
    }

    // ---------------------- ObserverList / GroupPeers ----------------------
    @Test
    public void testObserverList() {
        ObserverList observerList = new ObserverList();
        List<String> nodes = Arrays.asList("node1", "node2");
        observerList.setResult(nodes);
        Assert.assertEquals(2, observerList.getObserverList().size());
        Assert.assertEquals("node1", observerList.getObserverList().get(0));
    }

    @Test
    public void testGroupPeers() {
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(Collections.singletonList("peer0"));
        Assert.assertEquals(1, groupPeers.getGroupPeers().size());
        Assert.assertEquals("peer0", groupPeers.getGroupPeers().get(0));
    }

    // ---------------------- Call ----------------------
    @Test
    public void testCallOutput() {
        Call.CallOutput out = new Call.CallOutput();
        out.setBlockNumber(13L);
        out.setOutput("0xabc");
        out.setStatus(0);
        Assert.assertEquals(13L, out.getBlockNumber());
        Assert.assertEquals("0xabc", out.getOutput());
        Assert.assertEquals(0, out.getStatus());
        Assert.assertNotNull(out.toString());

        Call.CallOutput same = new Call.CallOutput();
        same.setBlockNumber(13L);
        same.setOutput("0xabc");
        same.setStatus(0);
        Assert.assertEquals(out, same);
        Assert.assertEquals(out.hashCode(), same.hashCode());

        Call call = new Call();
        call.setResult(out);
        Assert.assertEquals(out, call.getCallResult());
    }

    // ---------------------- SystemConfig ----------------------
    @Test
    public void testSystemConfig() {
        SystemConfig.Config config = new SystemConfig.Config();
        config.setBlockNumber(100L);
        config.setValue("3000000000");
        Assert.assertEquals(100L, config.getBlockNumber());
        Assert.assertEquals("3000000000", config.getValue());
        Assert.assertNotNull(config.toString());

        SystemConfig.Config same = new SystemConfig.Config();
        same.setBlockNumber(100L);
        same.setValue("3000000000");
        Assert.assertEquals(config, same);
        Assert.assertEquals(config.hashCode(), same.hashCode());

        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setResult(config);
        Assert.assertEquals(config, systemConfig.getSystemConfig());
    }

    // ---------------------- TotalTransactionCount ----------------------
    @Test
    public void testTotalTransactionCount() {
        TotalTransactionCount.TransactionCountInfo info =
                new TotalTransactionCount.TransactionCountInfo();
        info.setBlockNumber("10");
        info.setTransactionCount("200");
        info.setFailedTransactionCount("3");
        Assert.assertEquals("10", info.getBlockNumber());
        Assert.assertEquals("200", info.getTransactionCount());
        Assert.assertEquals("3", info.getFailedTransactionCount());
        Assert.assertNotNull(info.toString());

        TotalTransactionCount.TransactionCountInfo same =
                new TotalTransactionCount.TransactionCountInfo();
        same.setBlockNumber("10");
        same.setTransactionCount("200");
        same.setFailedTransactionCount("3");
        Assert.assertEquals(info, same);
        Assert.assertEquals(info.hashCode(), same.hashCode());

        TotalTransactionCount txCount = new TotalTransactionCount();
        txCount.setResult(info);
        Assert.assertEquals(info, txCount.getTotalTransactionCount());
    }

    // ---------------------- SealerList ----------------------
    @Test
    public void testSealerList() {
        SealerList.Sealer sealer = new SealerList.Sealer();
        sealer.setNodeID("0xnode");
        sealer.setWeight(2);
        sealer.setTermWeight(5);
        Assert.assertEquals("0xnode", sealer.getNodeID());
        Assert.assertEquals(2, sealer.getWeight());
        Assert.assertEquals(5, sealer.getTermWeight());
        Assert.assertNotNull(sealer.toString());

        SealerList.Sealer same = new SealerList.Sealer();
        same.setNodeID("0xnode");
        same.setWeight(9); // equals only checks nodeID
        Assert.assertEquals(sealer, same);
        Assert.assertEquals("0xnode".hashCode() + 2, sealer.hashCode());

        SealerList list = new SealerList();
        list.setResult(Collections.singletonList(sealer));
        Assert.assertEquals(1, list.getSealerList().size());
        Assert.assertEquals("0xnode", list.getSealerList().get(0).getNodeID());
    }

    // ---------------------- Peers ----------------------
    @Test
    public void testPeers() {
        Peers.NodeIDInfo nodeIDInfo = new Peers.NodeIDInfo();
        nodeIDInfo.setGroup("group0");
        nodeIDInfo.setNodeIDList(Arrays.asList("idA", "idB"));
        Assert.assertEquals("group0", nodeIDInfo.getGroup());
        Assert.assertEquals(2, nodeIDInfo.getNodeIDList().size());
        Assert.assertNotNull(nodeIDInfo.toString());

        Peers.PeerInfo peerInfo = new Peers.PeerInfo();
        peerInfo.setEndPoint("127.0.0.1:30300");
        peerInfo.setP2pNodeID("p2p0");
        peerInfo.setGroupNodeIDInfo(Collections.singletonList(nodeIDInfo));
        Assert.assertEquals("127.0.0.1:30300", peerInfo.getEndPoint());
        Assert.assertEquals("p2p0", peerInfo.getP2pNodeID());
        Assert.assertEquals(1, peerInfo.getGroupNodeIDInfo().size());
        Assert.assertNotNull(peerInfo.toString());

        Peers.PeersInfo peersInfo = new Peers.PeersInfo();
        peersInfo.setEndPoint("0.0.0.0:30300");
        peersInfo.setP2pNodeID("p2pSelf");
        peersInfo.setGroupNodeIDInfo(Collections.singletonList(nodeIDInfo));
        peersInfo.setPeers(Collections.singletonList(peerInfo));
        Assert.assertEquals("0.0.0.0:30300", peersInfo.getEndPoint());
        Assert.assertEquals("p2pSelf", peersInfo.getP2pNodeID());
        Assert.assertEquals(1, peersInfo.getPeers().size());
        Assert.assertEquals(1, peersInfo.getGroupNodeIDInfo().size());
        Assert.assertNotNull(peersInfo.toString());

        Peers peers = new Peers();
        peers.setResult(peersInfo);
        Assert.assertEquals(peersInfo, peers.getPeers());
    }

    // ---------------------- SyncStatus ----------------------
    @Test
    public void testSyncStatus() {
        SyncStatus.PeersInfo peersInfo = new SyncStatus.PeersInfo();
        peersInfo.setNodeId("peerNode");
        peersInfo.setGenesisHash("genesis");
        peersInfo.setBlockNumber(5L);
        peersInfo.setLatestHash("latest");
        Assert.assertEquals("peerNode", peersInfo.getNodeId());
        Assert.assertEquals("genesis", peersInfo.getGenesisHash());
        Assert.assertEquals(5L, peersInfo.getBlockNumber());
        Assert.assertEquals("latest", peersInfo.getLatestHash());
        Assert.assertNotNull(peersInfo.toString());

        SyncStatus.PeersInfo samePeer = new SyncStatus.PeersInfo();
        samePeer.setNodeId("peerNode");
        samePeer.setGenesisHash("genesis");
        samePeer.setBlockNumber(5L);
        samePeer.setLatestHash("latest");
        Assert.assertEquals(peersInfo, samePeer);
        Assert.assertEquals(peersInfo.hashCode(), samePeer.hashCode());

        SyncStatus.SyncStatusInfo info = new SyncStatus.SyncStatusInfo();
        info.setIsSyncing(false);
        info.setProtocolId("1");
        info.setGenesisHash("g");
        info.setNodeId("self");
        info.setBlockNumber(3L);
        info.setLatestHash("lh");
        info.setKnownHighestNumber(3);
        info.setTxPoolSize("0");
        info.setKnownLatestHash("klh");
        info.setPeers(Collections.singletonList(peersInfo));
        Assert.assertEquals(Boolean.FALSE, info.getIsSyncing());
        Assert.assertEquals("1", info.getProtocolId());
        Assert.assertEquals("g", info.getGenesisHash());
        Assert.assertEquals("self", info.getNodeId());
        Assert.assertEquals(3L, info.getBlockNumber());
        Assert.assertEquals("lh", info.getLatestHash());
        Assert.assertEquals(3, info.getKnownHighestNumber());
        Assert.assertEquals("0", info.getTxPoolSize());
        Assert.assertEquals("klh", info.getKnownLatestHash());
        Assert.assertEquals(1, info.getPeers().size());
        Assert.assertNotNull(info.toString());

        SyncStatus.SyncStatusInfo same = new SyncStatus.SyncStatusInfo();
        same.setIsSyncing(false);
        same.setProtocolId("1");
        same.setGenesisHash("g");
        same.setNodeId("self");
        same.setBlockNumber(3L);
        same.setLatestHash("lh");
        same.setKnownHighestNumber(3);
        same.setTxPoolSize("0");
        same.setKnownLatestHash("klh");
        same.setPeers(Collections.singletonList(samePeer));
        Assert.assertEquals(info, same);
        Assert.assertEquals(info.hashCode(), same.hashCode());

        SyncStatus syncStatus = new SyncStatus();
        syncStatus.setResult(info);
        Assert.assertEquals(info, syncStatus.getSyncStatus());
    }

    // ---------------------- ConsensusStatus ----------------------
    @Test
    public void testConsensusStatus() {
        ConsensusStatus.ConsensusNodeInfo nodeInfo = new ConsensusStatus.ConsensusNodeInfo();
        nodeInfo.setNodeID("0xn");
        nodeInfo.setWeight(1);
        nodeInfo.setTermWeight(2);
        nodeInfo.setIndex(0);
        Assert.assertEquals("0xn", nodeInfo.getNodeID());
        Assert.assertEquals(Integer.valueOf(1), nodeInfo.getWeight());
        Assert.assertEquals(Integer.valueOf(2), nodeInfo.getTermWeight());
        Assert.assertEquals(Integer.valueOf(0), nodeInfo.getIndex());
        Assert.assertNotNull(nodeInfo.toString());

        ConsensusStatus.ConsensusNodeInfo same = new ConsensusStatus.ConsensusNodeInfo();
        same.setNodeID("0xn");
        same.setWeight(1);
        same.setIndex(0);
        // equals checks nodeID, weight, index only
        Assert.assertEquals(nodeInfo, same);
        Assert.assertEquals(nodeInfo.hashCode(), same.hashCode());

        ConsensusStatus.ConsensusStatusInfo info = new ConsensusStatus.ConsensusStatusInfo();
        info.setNodeID("self");
        info.setIndex("1");
        info.setLeaderIndex(1);
        info.setConsensusNodesNum(2);
        info.setMaxFaultyQuorum(0);
        info.setMinRequiredQuorum(2);
        info.setConsensusNode(true);
        info.setBlockNumber(3);
        info.setHash("0xhash");
        info.setTimeout(false);
        info.setChangeCycle(0);
        info.setView(3);
        info.setConnectedNodeList(2);
        info.setConsensusNodeInfos(Collections.singletonList(nodeInfo));
        Assert.assertEquals("self", info.getNodeID());
        Assert.assertEquals("1", info.getIndex());
        Assert.assertEquals(Integer.valueOf(1), info.getLeaderIndex());
        Assert.assertEquals(Integer.valueOf(2), info.getConsensusNodesNum());
        Assert.assertEquals(Integer.valueOf(0), info.getMaxFaultyQuorum());
        Assert.assertEquals(Integer.valueOf(2), info.getMinRequiredQuorum());
        Assert.assertTrue(info.isConsensusNode());
        Assert.assertEquals(Boolean.TRUE, info.getConsensusNode());
        Assert.assertEquals(Integer.valueOf(3), info.getBlockNumber());
        Assert.assertEquals("0xhash", info.getHash());
        Assert.assertEquals(Boolean.FALSE, info.getTimeout());
        Assert.assertEquals(Integer.valueOf(0), info.getChangeCycle());
        Assert.assertEquals(Integer.valueOf(3), info.getView());
        Assert.assertEquals(Integer.valueOf(2), info.getConnectedNodeList());
        Assert.assertEquals(1, info.getConsensusNodeInfos().size());
        Assert.assertNotNull(info.toString());

        // exercise the Boolean overload setter too
        info.setConsensusNode(Boolean.FALSE);
        Assert.assertFalse(info.isConsensusNode());

        ConsensusStatus consensusStatus = new ConsensusStatus();
        consensusStatus.setResult(info);
        Assert.assertEquals(info, consensusStatus.getConsensusStatus());
    }

    // ---------------------- BcosBlockHeader ----------------------
    @Test
    public void testBcosBlockHeader() {
        BcosBlockHeader.Signature signature = new BcosBlockHeader.Signature();
        signature.setIndex(0);
        signature.setSignature("0xsig");
        Assert.assertEquals(Integer.valueOf(0), signature.getIndex());
        Assert.assertEquals("0xsig", signature.getSignature());
        Assert.assertNotNull(signature.toString());

        BcosBlockHeader.Signature sameSig = new BcosBlockHeader.Signature();
        sameSig.setIndex(0);
        sameSig.setSignature("0xsig");
        Assert.assertEquals(signature, sameSig);
        Assert.assertEquals(signature.hashCode(), sameSig.hashCode());

        BcosBlockHeader.ParentInfo parentInfo = new BcosBlockHeader.ParentInfo();
        parentInfo.setBlockNumber(0L);
        parentInfo.setBlockHash("0xparent");
        Assert.assertEquals(0L, parentInfo.getBlockNumber());
        Assert.assertEquals("0xparent", parentInfo.getBlockHash());
        Assert.assertNotNull(parentInfo.toString());

        BcosBlockHeader.ParentInfo sameParent = new BcosBlockHeader.ParentInfo();
        sameParent.setBlockNumber(0L);
        sameParent.setBlockHash("0xparent");
        Assert.assertEquals(parentInfo, sameParent);
        Assert.assertEquals(parentInfo.hashCode(), sameParent.hashCode());

        BcosBlockHeader.BlockHeader header = new BcosBlockHeader.BlockHeader();
        header.setNumber(7L);
        header.setVersion(1);
        header.setHash("0xblock");
        header.setLogsBloom("0xbloom");
        header.setTransactionsRoot("0xtxroot");
        header.setReceiptsRoot("0xrroot");
        header.setStateRoot("0xstate");
        header.setSealer(0);
        header.setSealerList(Arrays.asList("s0", "s1"));
        header.setExtraData("0x");
        header.setGasUsed("1234");
        header.setTimestamp(1654587389123L);
        header.setParentInfo(Collections.singletonList(parentInfo));
        header.setSignatureList(Collections.singletonList(signature));
        header.setConsensusWeights(Arrays.asList(1L, 1L));
        Assert.assertEquals(7L, header.getNumber());
        Assert.assertEquals(1, header.getVersion());
        Assert.assertEquals("0xblock", header.getHash());
        Assert.assertEquals("0xbloom", header.getLogsBloom());
        Assert.assertEquals("0xtxroot", header.getTransactionsRoot());
        Assert.assertEquals("0xrroot", header.getReceiptsRoot());
        Assert.assertEquals("0xstate", header.getStateRoot());
        Assert.assertEquals(0, header.getSealer());
        Assert.assertEquals(2, header.getSealerList().size());
        Assert.assertEquals("0x", header.getExtraData());
        Assert.assertEquals("1234", header.getGasUsed());
        Assert.assertEquals(1654587389123L, header.getTimestamp());
        Assert.assertEquals(1, header.getParentInfo().size());
        Assert.assertEquals(1, header.getSignatureList().size());
        Assert.assertEquals(2, header.getConsensusWeights().size());
        Assert.assertNotNull(header.toString());

        BcosBlockHeader bcosBlockHeader = new BcosBlockHeader();
        bcosBlockHeader.setResult(header);
        Assert.assertEquals(header, bcosBlockHeader.getBlockHeader());
    }

    // ---------------------- BcosBlock ----------------------
    @Test
    public void testBcosBlockTransactionHashAndObject() {
        BcosBlock.TransactionHash txHash = new BcosBlock.TransactionHash("0xhash");
        Assert.assertEquals("0xhash", txHash.get());
        txHash.setValue("0xnewhash");
        Assert.assertEquals("0xnewhash", txHash.get());
        Assert.assertNotNull(txHash.toString());

        BcosBlock.TransactionHash sameHash = new BcosBlock.TransactionHash("0xnewhash");
        Assert.assertEquals(txHash, sameHash);
        Assert.assertEquals(txHash.hashCode(), sameHash.hashCode());

        BcosBlock.TransactionObject txObject = new BcosBlock.TransactionObject();
        txObject.setHash("0xtx");
        Assert.assertSame(txObject, txObject.get());
        Assert.assertEquals("0xtx", txObject.get().getHash());

        BcosBlock.Block block = new BcosBlock.Block();
        block.setNumber(1L);
        block.setHash("0xblockhash");
        List<BcosBlock.TransactionResult> txs = new ArrayList<>();
        txs.add(txHash);
        block.setTransactions(txs);
        Assert.assertEquals(1, block.getTransactions().size());
        Assert.assertEquals(1, block.getTransactionHashes().size());
        Assert.assertTrue(block.getTransactionObject().isEmpty());
        Assert.assertNotNull(block.toString());

        List<BcosBlock.TransactionResult> objTxs = new ArrayList<>();
        objTxs.add(txObject);
        block.setTransactions(objTxs);
        Assert.assertEquals(1, block.getTransactionObject().size());
        Assert.assertTrue(block.getTransactionHashes().isEmpty());

        BcosBlock bcosBlock = new BcosBlock();
        bcosBlock.setResult(block);
        Assert.assertEquals(block, bcosBlock.getBlock());
    }

    // ---------------------- JsonTransactionResponse / BcosTransaction ----------------------
    @Test
    public void testJsonTransactionResponse() {
        JsonTransactionResponse tx = new JsonTransactionResponse();
        tx.setVersion(0);
        tx.setHash("0xtxhash");
        tx.setNonce("123");
        tx.setBlockLimit(500L);
        tx.setTo("0xebf98be58e190cab7ebed61295b0321d55bb8123");
        tx.setFrom("0xfrom");
        tx.setAbi("[]");
        tx.setInput("0xinput");
        tx.setChainID("chain0");
        tx.setGroupID("group0");
        tx.setExtraData("0x");
        tx.setSignature("0xsig");
        tx.setImportTime(999L);
        tx.setValue("0");
        tx.setGasPrice("0x1");
        tx.setGasLimit(21000L);
        tx.setMaxFeePerGas("0x2");
        tx.setMaxPriorityFeePerGas("0x3");
        tx.setExtension(new byte[] {1, 2, 3});
        tx.setTxProof(Arrays.asList("p0", "p1"));

        Assert.assertEquals(Integer.valueOf(0), tx.getVersion());
        Assert.assertEquals("0xtxhash", tx.getHash());
        Assert.assertEquals("123", tx.getNonce());
        Assert.assertEquals(500L, tx.getBlockLimit());
        Assert.assertEquals("0xebf98be58e190cab7ebed61295b0321d55bb8123", tx.getTo());
        Assert.assertEquals("0xfrom", tx.getFrom());
        Assert.assertEquals("[]", tx.getAbi());
        Assert.assertEquals("0xinput", tx.getInput());
        Assert.assertEquals("chain0", tx.getChainID());
        Assert.assertEquals("group0", tx.getGroupID());
        Assert.assertEquals("0x", tx.getExtraData());
        Assert.assertEquals("0xsig", tx.getSignature());
        Assert.assertEquals(999L, tx.getImportTime());
        Assert.assertEquals("0", tx.getValue());
        Assert.assertEquals("0x1", tx.getGasPrice());
        Assert.assertEquals(21000L, tx.getGasLimit());
        Assert.assertEquals("0x2", tx.getMaxFeePerGas());
        Assert.assertEquals("0x3", tx.getMaxPriorityFeePerGas());
        Assert.assertArrayEquals(new byte[] {1, 2, 3}, tx.getExtension());
        Assert.assertEquals(2, tx.getTxProof().size());
        Assert.assertNull(tx.getTransactionProof());
        Assert.assertNotNull(tx.toString());

        JsonTransactionResponse same = new JsonTransactionResponse();
        same.setVersion(0);
        same.setHash("0xtxhash");
        same.setNonce("123");
        same.setBlockLimit(500L);
        same.setTo("0xebf98be58e190cab7ebed61295b0321d55bb8123");
        same.setFrom("0xfrom");
        same.setAbi("[]");
        same.setInput("0xinput");
        same.setChainID("chain0");
        same.setGroupID("group0");
        same.setExtraData("0x");
        same.setSignature("0xsig");
        same.setTxProof(Arrays.asList("p0", "p1"));
        Assert.assertEquals(tx, same);
        Assert.assertEquals(tx.hashCode(), same.hashCode());

        BcosTransaction bcosTransaction = new BcosTransaction();
        Assert.assertFalse(bcosTransaction.getTransaction().isPresent());
        bcosTransaction.setResult(tx);
        Assert.assertTrue(bcosTransaction.getTransaction().isPresent());
        Assert.assertEquals(tx, bcosTransaction.getTransaction().get());
    }

    // ---------------------- TransactionReceipt / BcosTransactionReceipt ----------------------
    @Test
    public void testTransactionReceipt() {
        TransactionReceipt.Logs logs = new TransactionReceipt.Logs();
        logs.setAddress("0xaddr");
        logs.setTopics(Collections.singletonList("0xtopic"));
        logs.setData("0xdata");
        logs.setBlockNumber("5");
        Assert.assertEquals("0xaddr", logs.getAddress());
        Assert.assertEquals(1, logs.getTopics().size());
        Assert.assertEquals("0xdata", logs.getData());
        Assert.assertEquals("5", logs.getBlockNumber());
        Assert.assertNotNull(logs.toString());
        Assert.assertNotNull(logs.toEventLog());
        Assert.assertEquals("0xaddr", logs.toEventLog().getAddress());

        TransactionReceipt.Logs sameLogs = new TransactionReceipt.Logs();
        sameLogs.setAddress("0xaddr");
        sameLogs.setTopics(Collections.singletonList("0xtopic"));
        sameLogs.setData("0xdata");
        Assert.assertEquals(logs, sameLogs);
        Assert.assertEquals(logs.hashCode(), sameLogs.hashCode());

        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setVersion(0);
        receipt.setContractAddress("0xcontract");
        receipt.setChecksumContractAddress("0xChecksum");
        receipt.setGasUsed("19413");
        receipt.setStatus(0);
        receipt.setBlockNumber(BigInteger.valueOf(2));
        receipt.setOutput("0xout");
        receipt.setTransactionHash("0xtxhash");
        receipt.setReceiptHash("0xreceipthash");
        receipt.setLogEntries(Collections.singletonList(logs));
        receipt.setInput("0xin");
        receipt.setFrom("0xfrom");
        receipt.setTo("0xto");
        receipt.setTxProof(Arrays.asList("a", "b"));
        receipt.setTxReceiptProof(Collections.singletonList("c"));
        receipt.setExtraData("0xextra");
        receipt.setMessage("ok");
        receipt.setEffectiveGasPrice("0x1");

        Assert.assertEquals(Integer.valueOf(0), receipt.getVersion());
        Assert.assertEquals("0xcontract", receipt.getContractAddress());
        Assert.assertEquals("0xChecksum", receipt.getChecksumContractAddress());
        Assert.assertEquals("19413", receipt.getGasUsed());
        Assert.assertEquals(0, receipt.getStatus());
        Assert.assertTrue(receipt.isStatusOK());
        Assert.assertEquals(BigInteger.valueOf(2), receipt.getBlockNumber());
        Assert.assertEquals("0xout", receipt.getOutput());
        Assert.assertEquals("0xtxhash", receipt.getTransactionHash());
        Assert.assertEquals("0xreceipthash", receipt.getReceiptHash());
        Assert.assertEquals(1, receipt.getLogEntries().size());
        Assert.assertEquals("0xin", receipt.getInput());
        Assert.assertEquals("0xfrom", receipt.getFrom());
        Assert.assertEquals("0xto", receipt.getTo());
        Assert.assertEquals(2, receipt.getTxProof().size());
        Assert.assertEquals(1, receipt.getTxReceiptProof().size());
        Assert.assertEquals("0xextra", receipt.getExtraData());
        Assert.assertEquals("ok", receipt.getMessage());
        Assert.assertEquals("0x1", receipt.getEffectiveGasPrice());
        Assert.assertNull(receipt.getTransactionProof());
        Assert.assertNull(receipt.getReceiptProof());
        Assert.assertNotNull(receipt.toString());

        receipt.setStatus(1);
        Assert.assertFalse(receipt.isStatusOK());

        BcosTransactionReceipt bcosReceipt = new BcosTransactionReceipt();
        bcosReceipt.setResult(receipt);
        Assert.assertEquals(receipt, bcosReceipt.getTransactionReceipt());
    }

    // ---------------------- GroupStatus ----------------------
    @Test
    public void testGroupStatus() {
        GroupStatus status = new GroupStatus();
        status.setCode("0");
        status.setMessage("success");
        status.setStatus("running");
        Assert.assertEquals("0", status.getCode());
        Assert.assertEquals("success", status.getMessage());
        Assert.assertEquals("running", status.getStatus());
        Assert.assertNotNull(status.toString());

        GroupStatus same = new GroupStatus();
        same.setCode("0");
        same.setMessage("success");
        same.setStatus("running");
        Assert.assertEquals(status, same);
        Assert.assertEquals(status.hashCode(), same.hashCode());
        Assert.assertNotEquals(status, new GroupStatus());
    }

    // ---------------------- NodeVersion ----------------------
    @Test
    public void testNodeVersion() {
        NodeVersion.ClientVersion version = new NodeVersion.ClientVersion();
        version.setVersion("3.0.0");
        version.setSupportedVersion("3.0.0");
        version.setChainId("chain0");
        version.setBuildTime("20220607");
        version.setBuildType("Linux");
        version.setGitBranch("master");
        version.setGitCommitHash("abc123");
        Assert.assertEquals("3.0.0", version.getVersion());
        Assert.assertEquals("3.0.0", version.getSupportedVersion());
        Assert.assertEquals("chain0", version.getChainId());
        Assert.assertEquals("20220607", version.getBuildTime());
        Assert.assertEquals("Linux", version.getBuildType());
        Assert.assertEquals("master", version.getGitBranch());
        Assert.assertEquals("abc123", version.getGitCommitHash());
        Assert.assertNotNull(version.toString());

        NodeVersion.ClientVersion same = new NodeVersion.ClientVersion();
        same.setVersion("3.0.0");
        same.setSupportedVersion("3.0.0");
        same.setChainId("chain0");
        same.setBuildTime("20220607");
        same.setBuildType("Linux");
        same.setGitBranch("master");
        same.setGitCommitHash("abc123");
        Assert.assertEquals(version, same);
        Assert.assertEquals(version.hashCode(), same.hashCode());

        NodeVersion nodeVersion = new NodeVersion();
        nodeVersion.setResult(version);
        Assert.assertEquals(version, nodeVersion.getNodeVersion());
    }

    // ---------------------- BcosGroupList / BcosGroupInfo / BcosGroupInfoList ----------------------
    @Test
    public void testBcosGroupList() {
        BcosGroupList.GroupList groupList = new BcosGroupList.GroupList();
        groupList.setCode(0);
        groupList.setMsg("ok");
        groupList.setGroupList(Arrays.asList("group0", "group1"));
        Assert.assertEquals(0, groupList.getCode());
        Assert.assertEquals("ok", groupList.getMsg());
        Assert.assertEquals(2, groupList.getGroupList().size());
        Assert.assertNotNull(groupList.toString());

        BcosGroupList resp = new BcosGroupList();
        resp.setResult(groupList);
        Assert.assertEquals(groupList, resp.getResult());
    }

    @Test
    public void testBcosGroupInfo() {
        BcosGroupInfo.GroupInfo groupInfo = new BcosGroupInfo.GroupInfo();
        groupInfo.setChainID("chain0");
        groupInfo.setGroupID("group0");
        groupInfo.setNodeList(new ArrayList<>());
        Assert.assertEquals("chain0", groupInfo.getChainID());
        Assert.assertEquals("group0", groupInfo.getGroupID());
        Assert.assertNotNull(groupInfo.getNodeList());
        Assert.assertNull(groupInfo.getGenesisConfig());
        Assert.assertNotNull(groupInfo.toString());

        BcosGroupInfo resp = new BcosGroupInfo();
        resp.setResult(groupInfo);
        Assert.assertEquals(groupInfo, resp.getResult());

        BcosGroupInfoList listResp = new BcosGroupInfoList();
        listResp.setResult(Collections.singletonList(groupInfo));
        Assert.assertEquals(1, listResp.getResult().size());
        Assert.assertEquals(groupInfo, listResp.getResult().get(0));
    }

    // ---------------------- GroupNodeGenesisInfo.Sealer ----------------------
    @Test
    public void testGroupNodeGenesisInfoSealer() {
        GroupNodeGenesisInfo.Sealer sealer = new GroupNodeGenesisInfo.Sealer();
        sealer.setNodeID("0xnode");
        sealer.setWeight(1);
        Assert.assertEquals("0xnode", sealer.getNodeID());
        Assert.assertEquals(Integer.valueOf(1), sealer.getWeight());
        Assert.assertNotNull(sealer.toString());
    }

    // ---------------------- RetCode ----------------------
    @Test
    public void testRetCode() {
        RetCode retCode = new RetCode(0, "Success");
        Assert.assertEquals(0, retCode.getCode());
        Assert.assertEquals("Success", retCode.getMessage());
        Assert.assertNotNull(retCode.toString());

        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(0);
        retCode.setTransactionReceipt(receipt);
        Assert.assertSame(receipt, retCode.getTransactionReceipt());

        RetCode same = new RetCode(0, "Success");
        Assert.assertEquals(retCode, same);
        Assert.assertEquals(retCode.hashCode(), same.hashCode());
        Assert.assertNotEquals(retCode, new RetCode(1, "Other"));
        Assert.assertNotEquals(retCode, "not-a-retcode");

        RetCode empty = new RetCode();
        empty.code = 5;
        Assert.assertEquals(5, empty.getCode());
        Assert.assertNull(empty.getMessage());
    }

    // ---------------------- CryptoType constants ----------------------
    @Test
    public void testCryptoTypeConstants() {
        Assert.assertEquals(0, CryptoType.ECDSA_TYPE);
        Assert.assertEquals(1, CryptoType.SM_TYPE);
        Assert.assertEquals(2, CryptoType.ED25519_VRF_TYPE);
        Assert.assertEquals(3, CryptoType.HSM_TYPE);
        Assert.assertNotNull(new CryptoType());
    }
}
