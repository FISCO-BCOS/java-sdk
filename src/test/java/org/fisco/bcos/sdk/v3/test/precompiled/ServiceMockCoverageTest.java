package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeIniInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.contract.precompiled.balance.BalancePrecompiled;
import org.fisco.bcos.sdk.v3.contract.precompiled.balance.BalanceService;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.bfs.BFSService;
import org.fisco.bcos.sdk.v3.contract.precompiled.consensus.ConsensusService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTableService;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.v3.contract.precompiled.sharding.ShardingService;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * Unit tests that exercise the chain-dependent precompiled service / manager classes by mocking
 * {@link Client}. Read-call paths flow through {@code client.call(...)} and are fully mockable;
 * send-transaction paths ultimately invoke static native JNI (TransactionBuilderJniObj) and are not
 * exercised here. Validation / guard branches that throw before reaching JNI are covered.
 */
public class ServiceMockCoverageTest {

    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);

    /** Builds a Client mock wired with the minimal surface every Contract/Service constructor uses. */
    private Client baseMockClient() {
        Client client = mock(Client.class);
        when(client.getCryptoSuite()).thenReturn(cryptoSuite);
        when(client.isWASM()).thenReturn(false);
        when(client.getChainId()).thenReturn("chain0");
        when(client.getGroup()).thenReturn("group0");
        when(client.getBlockLimit()).thenReturn(BigInteger.valueOf(500));
        when(client.getExtraData()).thenReturn("");
        when(client.getNativePointer()).thenReturn(0L);
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_2_0.toVersionObj());
        return client;
    }

    private CryptoKeyPair keyPair() {
        return cryptoSuite.getCryptoKeyPair();
    }

    /** A getGroupInfo() response whose first node carries the given compatibility version. */
    private void stubGroupInfo(Client client, long compatibilityVersion) {
        BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
        protocol.setCompatibilityVersion(compatibilityVersion);
        BcosGroupNodeInfo.GroupNodeInfo nodeInfo = new BcosGroupNodeInfo.GroupNodeInfo();
        nodeInfo.setProtocol(protocol);
        BcosGroupInfo.GroupInfo groupInfo = new BcosGroupInfo.GroupInfo();
        groupInfo.setNodeList(Collections.singletonList(nodeInfo));
        BcosGroupInfo bcosGroupInfo = new BcosGroupInfo();
        bcosGroupInfo.setResult(groupInfo);
        when(client.getGroupInfo()).thenReturn(bcosGroupInfo);
    }

    /** Make every client.call(...) return a success Call with the given encoded output. */
    private void stubCall(Client client, String output) {
        stubCall(client, output, 0);
    }

    private void stubCall(Client client, String output, int status) {
        when(client.call(any()))
                .then(
                        (Answer<Call>)
                                invocation -> {
                                    Call call = new Call();
                                    Call.CallOutput callOutput = new Call.CallOutput();
                                    callOutput.setOutput(output);
                                    callOutput.setStatus(status);
                                    call.setResult(callOutput);
                                    return call;
                                });
    }

    // ------------------------------------------------------------------
    // BalanceService / BalancePrecompiled
    // ------------------------------------------------------------------

    @Test
    public void testBalanceServiceConstructAndVersion() {
        Client client = baseMockClient();
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_6_0.toVersionObj());
        BalanceService service = new BalanceService(client, keyPair());
        Assert.assertNotNull(service.getBalancePrecompiled());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_6_0.toVersionObj(), service.getCurrentVersion());
    }

    @Test
    public void testBalanceServiceGetBalance() throws ContractException {
        Client client = baseMockClient();
        // uint256 = 12345
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000003039");
        BalanceService service = new BalanceService(client, keyPair());
        BigInteger balance = service.getBalance("0x0000000000000000000000000000000000000001");
        Assert.assertEquals(BigInteger.valueOf(12345), balance);
    }

    @Test
    public void testBalancePrecompiledGetBalanceDirect() throws ContractException {
        Client client = baseMockClient();
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000000064");
        BalancePrecompiled precompiled =
                BalancePrecompiled.load(
                        "0x0000000000000000000000000000000000001011", client, keyPair());
        Assert.assertEquals(BigInteger.valueOf(100), precompiled.getBalance("0xabc"));
    }

    @Test
    public void testBalanceServiceGetBalanceNonZeroStatusThrows() {
        Client client = baseMockClient();
        // non-zero call status -> ContractException
        stubCall(client, "0x", 15);
        BalanceService service = new BalanceService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () -> service.getBalance("0x0000000000000000000000000000000000000001"));
    }

    @Test
    public void testBalanceServiceListCallerEmpty() throws ContractException {
        Client client = baseMockClient();
        // empty dynamic address array: offset 0x20, length 0
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000000");
        BalanceService service = new BalanceService(client, keyPair());
        List<String> callers = service.listCaller();
        Assert.assertNotNull(callers);
        Assert.assertTrue(callers.isEmpty());
    }

    @Test
    public void testBalancePrecompiledGetAddBalanceInputDecode() {
        Client client = baseMockClient();
        BalancePrecompiled precompiled =
                BalancePrecompiled.load(
                        "0x0000000000000000000000000000000000001011", client, keyPair());
        // synthesize an input: methodId(10 chars) + address + uint256(7)
        String input =
                "0xaabbccdd"
                        + "0000000000000000000000000000000000000000000000000000000000000abc"
                        + "0000000000000000000000000000000000000000000000000000000000000007";
        org.fisco.bcos.sdk.v3.model.TransactionReceipt receipt =
                new org.fisco.bcos.sdk.v3.model.TransactionReceipt();
        receipt.setInput(input);
        Assert.assertEquals(
                BigInteger.valueOf(7), precompiled.getAddBalanceInput(receipt).getValue2());
    }

    // ------------------------------------------------------------------
    // BFSService / BFSPrecompiled
    // ------------------------------------------------------------------

    @Test
    public void testBfsServiceConstruct() {
        Client client = baseMockClient();
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_2_0.toVersionObj());
        BFSService service = new BFSService(client, keyPair());
        Assert.assertNotNull(service.getBfsPrecompiled());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_2_0.toVersionObj(), service.getCurrentVersion());
    }

    @Test
    public void testBfsServiceReadlink() throws ContractException {
        Client client = baseMockClient();
        // address return value
        stubCall(
                client,
                "0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338");
        BFSService service = new BFSService(client, keyPair());
        String addr = service.readlink("/apps/Hello");
        Assert.assertEquals("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338", addr);
    }

    @Test
    public void testBfsServiceIsExistSystemPath() throws ContractException {
        Client client = baseMockClient();
        // version must be >= 3.1.0 for LS_PAGE_VERSION check
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_2_0.toVersionObj());
        BFSService service = new BFSService(client, keyPair());
        // "/apps" is a BFS system path -> directory, no call needed
        BFSInfo info = service.isExist("/apps");
        Assert.assertNotNull(info);
        Assert.assertEquals("directory", info.getFileType());
    }

    @Test
    public void testBfsServiceVersionGuardThrows() {
        Client client = baseMockClient();
        // too-low version: LS_PAGE_VERSION (3.1.0) check should fail at 3.0.0
        when(client.getChainCompatibilityVersion())
                .thenReturn(EnumNodeVersion.BCOS_3_0_0.toVersionObj());
        BFSService service = new BFSService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () -> service.list("/apps", BigInteger.ZERO, BigInteger.TEN));
    }

    // ------------------------------------------------------------------
    // ShardingService / ShardingPrecompiled
    // ------------------------------------------------------------------

    @Test
    public void testShardingServiceConstructAndVersion() {
        Client client = baseMockClient();
        stubGroupInfo(client, EnumNodeVersion.BCOS_3_3_0.toVersionObj().toCompatibilityVersion());
        ShardingService service = new ShardingService(client, keyPair());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_3_0.toVersionObj().toCompatibilityVersion(),
                service.getCurrentVersion());
    }

    @Test
    public void testShardingServiceVersionGuardThrows() {
        Client client = baseMockClient();
        // 3.0.0 < SHARDING_MIN_SUPPORT_VERSION(3.3.0) -> guard throws
        stubGroupInfo(client, EnumNodeVersion.BCOS_3_0_0.toVersionObj().toCompatibilityVersion());
        ShardingService service = new ShardingService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () -> service.getContractShard("0x0000000000000000000000000000000000000001"));
    }

    @Test
    public void testShardingServiceGetContractShardError() {
        Client client = baseMockClient();
        stubGroupInfo(client, EnumNodeVersion.BCOS_3_3_0.toVersionObj().toCompatibilityVersion());
        // getContractShard returns tuple (int32 code != 0, string shard) -> service throws
        stubCall(
                client,
                "0x0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000000");
        ShardingService service = new ShardingService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () -> service.getContractShard("0x0000000000000000000000000000000000000001"));
    }

    // ------------------------------------------------------------------
    // KVTableService
    // ------------------------------------------------------------------

    @Test
    public void testKvTableServiceConstruct() {
        Client client = baseMockClient();
        KVTableService service = new KVTableService(client, keyPair());
        Assert.assertNotNull(service);
    }

    @Test
    public void testKvTableServiceCheckKeyOk() throws ContractException {
        Client client = baseMockClient();
        KVTableService service = new KVTableService(client, keyPair());
        // short key under TABLE_KEY_MAX_LENGTH -> no throw
        service.checkKey("shortKey");
    }

    @Test
    public void testKvTableServiceCheckKeyTooLongThrows() {
        Client client = baseMockClient();
        KVTableService service = new KVTableService(client, keyPair());
        StringBuilder longKey = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longKey.append('x');
        }
        Assert.assertThrows(
                ContractException.class, () -> service.checkKey(longKey.toString()));
    }

    // ------------------------------------------------------------------
    // TableCRUDService
    // ------------------------------------------------------------------

    @Test
    public void testTableCrudServiceConstructAndVersion() {
        Client client = baseMockClient();
        stubGroupInfo(client, EnumNodeVersion.BCOS_3_2_0.toVersionObj().toCompatibilityVersion());
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Assert.assertEquals(
                EnumNodeVersion.BCOS_3_2_0.toVersionObj().toCompatibilityVersion(),
                service.getCurrentVersion());
    }

    @Test
    public void testTableCrudServiceV320GuardThrows() {
        Client client = baseMockClient();
        // 3.1.0 < V320_CRUD_VERSION(3.2.0) -> descWithKeyOrder guard throws
        stubGroupInfo(client, EnumNodeVersion.BCOS_3_1_0.toVersionObj().toCompatibilityVersion());
        TableCRUDService service = new TableCRUDService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.descWithKeyOrder("t_test"));
    }

    // ------------------------------------------------------------------
    // ConsensusService - validation/guard branches (pre-JNI)
    // ------------------------------------------------------------------

    @Test
    public void testConsensusServiceConstruct() {
        Client client = baseMockClient();
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertNotNull(service);
    }

    @Test
    public void testConsensusAddSealerNotInNodeListThrows() {
        Client client = baseMockClient();
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>()); // empty group peers
        when(client.getGroupPeers()).thenReturn(groupPeers);
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.addSealer("node-not-present", BigInteger.ONE));
    }

    @Test
    public void testConsensusAddSealerAlreadyInSealerListThrows() {
        Client client = baseMockClient();
        String nodeId = "nodeAAA";
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>(Arrays.asList(nodeId)));
        when(client.getGroupPeers()).thenReturn(groupPeers);

        SealerList sealerList = new SealerList();
        SealerList.Sealer sealer = new SealerList.Sealer();
        sealer.setNodeID(nodeId);
        sealer.setWeight(1);
        sealerList.setResult(new ArrayList<>(Arrays.asList(sealer)));
        when(client.getSealerList()).thenReturn(sealerList);

        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.addSealer(nodeId, BigInteger.ONE));
    }

    @Test
    public void testConsensusAddSealerNotInObserverListThrows() {
        Client client = baseMockClient();
        String nodeId = "nodeBBB";
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>(Arrays.asList(nodeId)));
        when(client.getGroupPeers()).thenReturn(groupPeers);

        SealerList sealerList = new SealerList();
        sealerList.setResult(new ArrayList<>()); // not in sealer list
        when(client.getSealerList()).thenReturn(sealerList);

        ObserverList observerList = new ObserverList();
        observerList.setResult(new ArrayList<>()); // not in observer list -> throws
        when(client.getObserverList()).thenReturn(observerList);

        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.addSealer(nodeId, BigInteger.ONE));
    }

    @Test
    public void testConsensusAddObserverNotInNodeListThrows() {
        Client client = baseMockClient();
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>());
        when(client.getGroupPeers()).thenReturn(groupPeers);
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.addObserver("absent-node"));
    }

    @Test
    public void testConsensusAddObserverAlreadyObserverThrows() {
        Client client = baseMockClient();
        String nodeId = "nodeCCC";
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>(Arrays.asList(nodeId)));
        when(client.getGroupPeers()).thenReturn(groupPeers);

        ObserverList observerList = new ObserverList();
        observerList.setResult(new ArrayList<>(Arrays.asList(nodeId))); // already observer -> throws
        when(client.getObserverList()).thenReturn(observerList);

        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.addObserver(nodeId));
    }

    @Test
    public void testConsensusSetTermWeightVersionGuardThrows() {
        Client client = baseMockClient();
        // 3.2.0 < TERM_WEIGHT_MIN_SUPPORT_VERSION(3.12.0) -> guard throws
        stubGroupInfo(client, EnumNodeVersion.BCOS_3_2_0.toVersionObj().toCompatibilityVersion());
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.setTermWeight("node", BigInteger.ONE));
    }

    // ------------------------------------------------------------------
    // SystemConfigService - constructor + static / pure-logic methods
    // ------------------------------------------------------------------

    @Test
    public void testSystemConfigServiceConstruct() {
        Client client = baseMockClient();
        SystemConfigService service = new SystemConfigService(client, keyPair());
        Assert.assertNotNull(service);
    }

    @Test
    public void testSystemConfigCheckSysNumberValueValidation() {
        // tx_count_limit must be >= 1
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_COUNT_LIMIT, "10"));
        Assert.assertFalse(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_COUNT_LIMIT, "0"));
        // unknown key -> always true
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation("unknown_key", "abc"));
        // non-numeric for a known key -> false
        Assert.assertFalse(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_GAS_LIMIT, "not-a-number"));
    }

    @Test
    public void testSystemConfigTxGasLimitBoundary() {
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_GAS_LIMIT, "100000"));
        Assert.assertFalse(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_GAS_LIMIT, "99999"));
    }

    @Test
    public void testSystemConfigIsCheckableAndKeys() {
        Assert.assertTrue(
                SystemConfigService.isCheckableInValueValidation(
                        SystemConfigService.CONSENSUS_PERIOD));
        Assert.assertFalse(SystemConfigService.isCheckableInValueValidation("not_a_config"));
        Set<String> keys = SystemConfigService.getConfigKeys();
        Assert.assertTrue(keys.contains(SystemConfigService.TX_GAS_PRICE));
        Assert.assertTrue(keys.contains(SystemConfigService.AUTH_STATUS));
    }

    @Test
    public void testSystemConfigCheckCompatibilityVersion() {
        Client client = baseMockClient();
        // node at 3.2.0; setting version 3.2.0 should be allowed (<=)
        stubGroupInfoWithBinaryVersion(client, "3.2.0");
        Assert.assertTrue(SystemConfigService.checkCompatibilityVersion(client, "3.2.0"));
        // setting a higher version than the node -> not supported
        Assert.assertFalse(SystemConfigService.checkCompatibilityVersion(client, "9.9.9"));
        // garbage version string -> caught -> false
        Assert.assertFalse(SystemConfigService.checkCompatibilityVersion(client, "garbage"));
    }

    /** getGroupInfo() whose first node iniConfig.binaryInfo.version is the given string. */
    private void stubGroupInfoWithBinaryVersion(Client client, String version) {
        BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
        protocol.setCompatibilityVersion(
                EnumNodeVersion.getClassVersion(version).toCompatibilityVersion());
        GroupNodeIniInfo iniInfo = new GroupNodeIniInfo();
        GroupNodeIniInfo.BinaryInfo binaryInfo = new GroupNodeIniInfo.BinaryInfo();
        binaryInfo.setVersion(version);
        iniInfo.setBinaryInfo(binaryInfo);
        BcosGroupNodeInfo.GroupNodeInfo nodeInfo = new BcosGroupNodeInfo.GroupNodeInfo();
        nodeInfo.setProtocol(protocol);
        nodeInfo.setIniConfig(iniInfo);
        BcosGroupInfo.GroupInfo groupInfo = new BcosGroupInfo.GroupInfo();
        groupInfo.setNodeList(Collections.singletonList(nodeInfo));
        BcosGroupInfo bcosGroupInfo = new BcosGroupInfo();
        bcosGroupInfo.setResult(groupInfo);
        when(client.getGroupInfo()).thenReturn(bcosGroupInfo);
    }
}
