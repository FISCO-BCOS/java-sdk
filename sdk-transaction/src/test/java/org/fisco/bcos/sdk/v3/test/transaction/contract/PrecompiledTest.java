package org.fisco.bcos.sdk.v3.test.transaction.contract;

import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.contract.auth.manager.AuthManager;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSPrecompiled;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSService;
import org.fisco.bcos.sdk.v3.contract.precompiled.consensus.ConsensusService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTableService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrecompiledTest {
    private Client mockClient;
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
        authManager = new AuthManager(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
        bfsService = new BFSService(mockClient, mockClient.getCryptoSuite().getCryptoKeyPair());
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
                    transactionReceipt.setResult(mockReceipt);
                    return transactionReceipt;
                }
        );
    }

    public void mockGetNodeRequest(String... nodeID) {
        when(mockClient.getGroupPeers()).then(
                invocation -> {
                    GroupPeers groupPeers = new GroupPeers();
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

        mockSendTxRequest("0x0000000000000000000000000000000000000000000000000000000000000000", PrecompiledAddress.BFS_PRECOMPILED_ADDRESS, 0);
        RetCode link = bfsService.link("name", "ver", "add", "abi");
        Assert.assertEquals(link.getCode(), PrecompiledRetCode.CODE_SUCCESS.code);
    }

    @Test
    public void authTest() throws ContractException {
        mockGetNodeRequest("node1", "node2", "node3");
        mockGetSealerRequest("node1","node2");
        mockGetObserverRequest("node3");
        mockSendTxRequest("0x0000000000000000000000000000000000000000000000000000000000000001", PrecompiledAddress.CONTRACT_AUTH_ADDRESS, 0);
        BigInteger rmNodeProposal = authManager.createRmNodeProposal("node1");
        Assert.assertEquals(BigInteger.ONE, rmNodeProposal);

        BigInteger proposal = authManager.createSetConsensusWeightProposal("node3", BigInteger.ONE, true);
        Assert.assertEquals(BigInteger.ONE, proposal);
        // add exist
        try {
            Assert.assertThrows(ContractException.class,()-> authManager.createSetConsensusWeightProposal("node1", BigInteger.ONE, true));
            authManager.createSetConsensusWeightProposal("node1", BigInteger.ONE, true);
        }catch (ContractException e){
            Assert.assertEquals(PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST, e.getMessage());
        }

        BigInteger proposal1 = authManager.createSetConsensusWeightProposal("node1", BigInteger.ONE, false);
        Assert.assertEquals(BigInteger.ONE, proposal1);

        // update not exist, straight set on chain

        BigInteger proposal2 = authManager.createSetConsensusWeightProposal("node1", BigInteger.ZERO, true);
        Assert.assertEquals(BigInteger.ONE, proposal2);
        // add exist
        try {
            Assert.assertThrows(ContractException.class,()-> authManager.createSetConsensusWeightProposal("node3", BigInteger.ZERO, true));
            authManager.createSetConsensusWeightProposal("node1", BigInteger.ZERO, true);
        }catch (ContractException e){
            Assert.assertEquals(PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST, e.getMessage());
        }

        // update zero
        try {
            Assert.assertThrows(ContractException.class,()-> authManager.createSetConsensusWeightProposal("node3", BigInteger.ZERO, false));
            authManager.createSetConsensusWeightProposal("node3", BigInteger.ZERO, false);
        }catch (ContractException e){
            Assert.assertEquals(PrecompiledRetCode.CODE_INVALID_WEIGHT.getMessage(), e.getMessage());
        }
    }

}
