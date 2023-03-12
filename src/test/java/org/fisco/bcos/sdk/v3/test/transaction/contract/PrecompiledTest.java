package org.fisco.bcos.sdk.v3.test.transaction.contract;

import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager;
import org.fisco.bcos.sdk.v3.contract.auth.po.AccessStatus;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSService;
import org.fisco.bcos.sdk.v3.contract.precompiled.consensus.ConsensusService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTableService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.transaction.mock.MockTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrecompiledTest {
    private Client mockClient;
    private TransactionProcessor mockTransactionProcessor;
    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
    private AuthManager authManager;
    private BFSService bfsService;
    private ConsensusService consensusService;
    private SystemConfigService systemConfigService;
    private TableCRUDService tableCRUDService;
    private KVTableService kvTableService;

    public PrecompiledTest() {
        mockClient = mock(Client.class);
        when(mockClient.getChainId()).thenReturn("chain0");
        when(mockClient.getGroup()).thenReturn("group0");
        when(mockClient.getCryptoSuite()).thenReturn(cryptoSuite);
        when(mockClient.isWASM()).thenReturn(false);
        when(mockClient.getBlockLimit()).thenReturn(BigInteger.valueOf(500));
        when(mockClient.getGroupInfo()).then((Answer<BcosGroupInfo>) invocation -> {
            BcosGroupInfo bcosGroupInfo = new BcosGroupInfo();
            BcosGroupInfo.GroupInfo groupInfo = new BcosGroupInfo.GroupInfo();
            BcosGroupNodeInfo.GroupNodeInfo groupNodeInfo = new BcosGroupNodeInfo.GroupNodeInfo();
            BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
            protocol.setCompatibilityVersion(EnumNodeVersion.BCOS_3_1_0.getVersion());
            groupNodeInfo.setProtocol(protocol);
            groupInfo.setNodeList(Collections.singletonList(groupNodeInfo));
            bcosGroupInfo.setResult(groupInfo);
            return bcosGroupInfo;
        });

        authManager = new AuthManager(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
        bfsService = new BFSService(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
        bfsService.getBfsPrecompiled().setTransactionProcessor(mockTransactionProcessor);

        consensusService = new ConsensusService(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
        systemConfigService = new SystemConfigService(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
        tableCRUDService = new TableCRUDService(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
        kvTableService = new KVTableService(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
    }

    public void mockCallRequest(String output, int status) {
        when(mockClient.call(any())).then((Answer<Call>) invocation -> {
            Call call = new Call();
            Call.CallOutput callOutput = new Call.CallOutput();
            callOutput.setOutput(output);
            callOutput.setStatus(status);
            call.setResult(callOutput);
            return call;
        });
    }

    public void mockSendTxRequest(String output, String to, int status) {
        when(mockClient.sendTransaction(any(), anyBoolean())).then(
                invocation -> {
                    BcosTransactionReceipt transactionReceipt = new BcosTransactionReceipt();
                    TransactionReceipt mockReceipt = new TransactionReceipt();
                    mockReceipt.setTo(to);
                    mockReceipt.setOutput(output);
                    mockReceipt.setStatus(status);
                    mockReceipt.setLogEntries(new ArrayList<>());
                    transactionReceipt.setResult(mockReceipt);
                    return transactionReceipt;
                }
        );
    }

    public void mockGetNodeRequest(boolean isEmpty, String... nodeID) {
        when(mockClient.getGroupPeers()).then(
                invocation -> {
                    GroupPeers groupPeers = new GroupPeers();
                    if (isEmpty) {
                        groupPeers.setResult(null);
                        return groupPeers;
                    }
                    List<String> nodeList = new ArrayList<>(Arrays.asList(nodeID));
                    groupPeers.setResult(nodeList);
                    return groupPeers;
                }
        );
    }

    public void mockGetSealerRequest(String... nodeID) {
        when(mockClient.getSealerList()).then(
                invocation -> {
                    SealerList sealerList = new SealerList();
                    List<SealerList.Sealer> sealers = new ArrayList<>();
                    for (String node : nodeID) {
                        SealerList.Sealer sealer = new SealerList.Sealer();
                        sealer.setWeight(1);
                        sealer.setNodeID(node);
                        sealers.add(sealer);
                    }
                    sealerList.setResult(sealers);
                    return sealerList;
                }
        );
    }

    public void mockGetBlockNumberRequest(String number) {
        when(mockClient.getBlockNumber()).then(
                invocation -> {
                    BlockNumber blockNumber = new BlockNumber();
                    blockNumber.setResult(number);
                    return blockNumber;
                }
        );
    }

    public void mockGetSyncStatusRequest(long number, String... nodeIds) {
        when(mockClient.getSyncStatus()).then(
                invocation -> {
                    SyncStatus syncStatus = new SyncStatus();
                    SyncStatus.SyncStatusInfo syncStatusInfo = new SyncStatus.SyncStatusInfo();
                    List<SyncStatus.PeersInfo> peersInfos = new ArrayList<>();
                    for (String nodeId : nodeIds) {
                        SyncStatus.PeersInfo peersInfo = new SyncStatus.PeersInfo();
                        peersInfo.setBlockNumber(number);
                        peersInfo.setNodeId(nodeId);
                        peersInfos.add(peersInfo);
                    }
                    syncStatusInfo.setPeers(peersInfos);
                    syncStatus.setResult(syncStatusInfo);
                    return syncStatus;
                }
        );
    }

    public void mockGetObserverRequest(String... nodeID) {
        when(mockClient.getObserverList()).then(
                invocation -> {
                    ObserverList observerList = new ObserverList();
                    List<String> nodeList = new ArrayList<>(Arrays.asList(nodeID));
                    observerList.setResult(nodeList);
                    return observerList;
                }
        );
    }

    @Test
    public void bfsTest() throws ContractException {
        mockTransactionProcessor = new MockTransactionProcessor(mockClient, cryptoSuite.getCryptoKeyPair(), "group0", "chain0", "", 0,"0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff30f700000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000000");
        bfsService.getBfsPrecompiled().setTransactionProcessor(mockTransactionProcessor);

        mockCallRequest("0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338", 0);
        String readlink = bfsService.readlink("");
        Assert.assertEquals("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338", readlink);

        // not exist
        try {
            mockCallRequest("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff30f700000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000000", 0);
            bfsService.list("/");
        } catch (ContractException e) {
            Assert.assertEquals(e.getErrorCode(), PrecompiledRetCode.CODE_FILE_NOT_EXIST.code);
        }

        // mockSendTxRequest("0x0000000000000000000000000000000000000000000000000000000000000000", PrecompiledAddress.BFS_PRECOMPILED_ADDRESS, 0);
        mockTransactionProcessor = new MockTransactionProcessor(mockClient, cryptoSuite.getCryptoKeyPair(), "group0", "chain0", "", 0,"0x0000000000000000000000000000000000000000000000000000000000000000");
        bfsService.getBfsPrecompiled().setTransactionProcessor(mockTransactionProcessor);

        RetCode link = bfsService.link("name", "ver", "add", "abi");
        Assert.assertEquals(link.getCode(), PrecompiledRetCode.CODE_SUCCESS.code);
    }

    @Test
    public void authTest() throws ContractException {
        mockGetNodeRequest(false, "node1", "node2", "node3");
        mockGetSealerRequest("node1", "node2");
        mockGetObserverRequest("node3");
        mockSendTxRequest("0x0000000000000000000000000000000000000000000000000000000000000001", PrecompiledAddress.CONTRACT_AUTH_ADDRESS, 0);
        mockGetBlockNumberRequest("10");
        mockGetSyncStatusRequest(10, "node1", "node2", "node3");

        mockTransactionProcessor = new MockTransactionProcessor(mockClient, cryptoSuite.getCryptoKeyPair(), "group0", "chain0", "", 0,"0x0000000000000000000000000000000000000000000000000000000000000001");
        authManager.getCommitteeManager().setTransactionProcessor(mockTransactionProcessor);

        BigInteger rmNodeProposal = authManager.createRmNodeProposal("node1");
        Assert.assertEquals(BigInteger.ONE, rmNodeProposal);

        BigInteger proposal = authManager.createSetConsensusWeightProposal("node3", BigInteger.ONE, true);
        Assert.assertEquals(BigInteger.ONE, proposal);
        // add exist
        try {
            Assert.assertThrows(ContractException.class, () -> authManager.createSetConsensusWeightProposal("node1", BigInteger.ONE, true));
            authManager.createSetConsensusWeightProposal("node1", BigInteger.ONE, true);
        } catch (ContractException e) {
            Assert.assertEquals(PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST, e.getMessage());
        }

        BigInteger proposal1 = authManager.createSetConsensusWeightProposal("node1", BigInteger.ONE, false);
        Assert.assertEquals(BigInteger.ONE, proposal1);

        // update not exist, straight set on chain

        BigInteger proposal2 = authManager.createSetConsensusWeightProposal("node1", BigInteger.ZERO, true);
        Assert.assertEquals(BigInteger.ONE, proposal2);
        // add exist
        try {
            Assert.assertThrows(ContractException.class, () -> authManager.createSetConsensusWeightProposal("node3", BigInteger.ZERO, true));
            authManager.createSetConsensusWeightProposal("node1", BigInteger.ZERO, true);
        } catch (ContractException e) {
            Assert.assertEquals(PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST, e.getMessage());
        }

        // update zero
        try {
            Assert.assertThrows(ContractException.class, () -> authManager.createSetConsensusWeightProposal("node3", BigInteger.ZERO, false));
            authManager.createSetConsensusWeightProposal("node3", BigInteger.ZERO, false);
        } catch (ContractException e) {
            Assert.assertEquals(PrecompiledRetCode.CODE_INVALID_WEIGHT.getMessage(), e.getMessage());
        }

        BigInteger proposal3 = authManager.createSetSysConfigProposal(SystemConfigService.TX_GAS_LIMIT, String.valueOf(100000));
        Assert.assertEquals(BigInteger.ONE, proposal3);

        BigInteger proposal4 = authManager.createSetSysConfigProposal(SystemConfigService.TX_COUNT_LIMIT, String.valueOf(100000));
        Assert.assertEquals(BigInteger.ONE, proposal4);

        BigInteger proposal5 = authManager.createSetSysConfigProposal(SystemConfigService.CONSENSUS_PERIOD, String.valueOf(100000));
        Assert.assertEquals(BigInteger.ONE, proposal5);

        Assert.assertThrows(ContractException.class, () -> authManager.createSetSysConfigProposal(SystemConfigService.TX_GAS_LIMIT, String.valueOf(100000 - 1)));
        Assert.assertThrows(ContractException.class, () -> authManager.createSetSysConfigProposal(SystemConfigService.TX_COUNT_LIMIT, String.valueOf(-1)));
        Assert.assertThrows(ContractException.class, () -> authManager.createSetSysConfigProposal(SystemConfigService.CONSENSUS_PERIOD, String.valueOf(-1)));

        BigInteger errorKey = authManager.createSetSysConfigProposal("errorKey", String.valueOf(100000));
        Assert.assertEquals(BigInteger.ONE, errorKey);

        mockTransactionProcessor = new MockTransactionProcessor(mockClient, cryptoSuite.getCryptoKeyPair(), "group0", "chain0", "", 0,"0x0000000000000000000000000000000000000000000000000000000000000001");
        authManager.getContractAuthPrecompiled().setTransactionProcessor(mockTransactionProcessor);

        RetCode retCode = authManager.setContractStatus("1234567890123456789012345678901234567890", true);
        Assert.assertEquals(1, retCode.getCode());

        Assert.assertThrows(ContractException.class, () -> authManager.setContractStatus("1234567890123456789012345678901234567890", AccessStatus.Abolish));
        try {
            authManager.setContractStatus("1234567890123456789012345678901234567890", AccessStatus.Abolish);
        } catch (ContractException e) {
            Assert.assertEquals(e.getErrorCode(), -1);
        }

        when(mockClient.getGroupInfo()).then((Answer<BcosGroupInfo>) invocation -> {
            BcosGroupInfo bcosGroupInfo = new BcosGroupInfo();
            BcosGroupInfo.GroupInfo groupInfo = new BcosGroupInfo.GroupInfo();
            BcosGroupNodeInfo.GroupNodeInfo groupNodeInfo = new BcosGroupNodeInfo.GroupNodeInfo();
            BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
            protocol.setCompatibilityVersion(EnumNodeVersion.BCOS_3_2_0.getVersion());
            groupNodeInfo.setProtocol(protocol);
            groupInfo.setNodeList(Collections.singletonList(groupNodeInfo));
            bcosGroupInfo.setResult(groupInfo);
            return bcosGroupInfo;
        });

        RetCode retCode1 = authManager.setContractStatus("1234567890123456789012345678901234567890", AccessStatus.Abolish);
        Assert.assertEquals(1, retCode1.getCode());
    }

    @Test
    public void consensusTest() throws ContractException {
        // empty group test
        mockGetNodeRequest(true);
        Assert.assertThrows(PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST,
                ContractException.class, () -> consensusService.addObserver("node0"));
    }
}
