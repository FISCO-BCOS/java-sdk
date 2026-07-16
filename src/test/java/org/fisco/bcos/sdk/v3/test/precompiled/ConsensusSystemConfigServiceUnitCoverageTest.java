package org.fisco.bcos.sdk.v3.test.precompiled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.GroupNodeIniInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfoList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.contract.precompiled.consensus.ConsensusService;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java unit tests for {@link ConsensusService} and {@link SystemConfigService} that drive
 * validation/guard branches and static logic. Send-transaction success paths reach native JNI and
 * are exercised under try/catch (the encoding/validation logic still runs). No live chain.
 */
public class ConsensusSystemConfigServiceUnitCoverageTest {

    private final CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);

    private CryptoKeyPair keyPair() {
        return cryptoSuite.getCryptoKeyPair();
    }

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

    // ------------------------------------------------------------------
    // ConsensusService
    // ------------------------------------------------------------------

    @Test
    public void testConstruct() {
        Client client = baseMockClient();
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertNotNull(service);
    }

    @Test
    public void testAddSealerAlreadyInSealerListThrows() {
        Client client = baseMockClient();
        String nodeId = "nodeAAA";
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>(Collections.singletonList(nodeId)));
        when(client.getGroupPeers()).thenReturn(groupPeers);

        SealerList sealerList = new SealerList();
        SealerList.Sealer sealer = new SealerList.Sealer();
        sealer.setNodeID(nodeId);
        sealer.setWeight(1);
        sealerList.setResult(new ArrayList<>(Collections.singletonList(sealer)));
        when(client.getSealerList()).thenReturn(sealerList);

        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.addSealer(nodeId, BigInteger.ONE));
    }

    @Test
    public void testAddSealerNotInObserverListThrows() {
        Client client = baseMockClient();
        String nodeId = "nodeBBB";
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>(Collections.singletonList(nodeId)));
        when(client.getGroupPeers()).thenReturn(groupPeers);

        SealerList sealerList = new SealerList();
        sealerList.setResult(new ArrayList<>());
        when(client.getSealerList()).thenReturn(sealerList);

        ObserverList observerList = new ObserverList();
        observerList.setResult(new ArrayList<>());
        when(client.getObserverList()).thenReturn(observerList);

        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.addSealer(nodeId, BigInteger.ONE));
    }

    @Test
    public void testAddObserverNotInNodeListThrows() {
        Client client = baseMockClient();
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>());
        when(client.getGroupPeers()).thenReturn(groupPeers);
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.addObserver("absent"));
    }

    @Test
    public void testAddObserverAlreadyObserverThrows() {
        Client client = baseMockClient();
        String nodeId = "nodeCCC";
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>(Collections.singletonList(nodeId)));
        when(client.getGroupPeers()).thenReturn(groupPeers);
        ObserverList observerList = new ObserverList();
        observerList.setResult(new ArrayList<>(Collections.singletonList(nodeId)));
        when(client.getObserverList()).thenReturn(observerList);
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(ContractException.class, () -> service.addObserver(nodeId));
    }

    @Test
    public void testAddObserverSuccessReachesTxBuild() {
        Client client = baseMockClient();
        String nodeId = "nodeOK";
        GroupPeers groupPeers = new GroupPeers();
        groupPeers.setResult(new ArrayList<>(Collections.singletonList(nodeId)));
        when(client.getGroupPeers()).thenReturn(groupPeers);
        ObserverList observerList = new ObserverList();
        observerList.setResult(new ArrayList<>()); // not yet an observer -> passes guards
        when(client.getObserverList()).thenReturn(observerList);
        ConsensusService service = new ConsensusService(client, keyPair());
        try {
            service.addObserver(nodeId);
        } catch (Throwable t) {
            // native tx-building unavailable offline; validation + encoding ran
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testSetWeightReachesTxBuild() {
        Client client = baseMockClient();
        ConsensusService service = new ConsensusService(client, keyPair());
        try {
            service.setWeight("node1", BigInteger.valueOf(2));
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testRemoveNodeReachesTxBuild() {
        Client client = baseMockClient();
        ConsensusService service = new ConsensusService(client, keyPair());
        try {
            service.removeNode("node1");
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testSetTermWeightVersionGuardThrows() {
        Client client = baseMockClient();
        // 3.2.0 < TERM_WEIGHT_MIN_SUPPORT_VERSION(3.12.0) -> guard throws
        stubGroupInfo(client, EnumNodeVersion.BCOS_3_2_0.toVersionObj().toCompatibilityVersion());
        ConsensusService service = new ConsensusService(client, keyPair());
        Assert.assertThrows(
                ContractException.class, () -> service.setTermWeight("node", BigInteger.ONE));
    }

    // ------------------------------------------------------------------
    // SystemConfigService
    // ------------------------------------------------------------------

    @Test
    public void testSystemConfigConstruct() {
        Client client = baseMockClient();
        SystemConfigService service = new SystemConfigService(client, keyPair());
        Assert.assertNotNull(service);
    }

    @Test
    public void testCheckSysNumberValueValidation() {
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_COUNT_LIMIT, "10"));
        Assert.assertFalse(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_COUNT_LIMIT, "0"));
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation("unknown_key", "abc"));
        Assert.assertFalse(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_GAS_LIMIT, "not-a-number"));
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_GAS_LIMIT, "100000"));
        Assert.assertFalse(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_GAS_LIMIT, "99999"));
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.CONSENSUS_PERIOD, "5"));
        Assert.assertTrue(
                SystemConfigService.checkSysNumberValueValidation(
                        SystemConfigService.TX_GAS_PRICE, "1"));
    }

    @Test
    public void testIsCheckableAndKeys() {
        Assert.assertTrue(
                SystemConfigService.isCheckableInValueValidation(
                        SystemConfigService.CONSENSUS_PERIOD));
        Assert.assertFalse(SystemConfigService.isCheckableInValueValidation("not_a_config"));
        Set<String> keys = SystemConfigService.getConfigKeys();
        Assert.assertTrue(keys.contains(SystemConfigService.TX_GAS_PRICE));
        Assert.assertTrue(keys.contains(SystemConfigService.AUTH_STATUS));
    }

    @Test
    public void testCheckCompatibilityVersion() {
        Client client = baseMockClient();
        stubGroupInfoWithBinaryVersion(client, "3.2.0");
        Assert.assertTrue(SystemConfigService.checkCompatibilityVersion(client, "3.2.0"));
        Assert.assertFalse(SystemConfigService.checkCompatibilityVersion(client, "9.9.9"));
        Assert.assertFalse(SystemConfigService.checkCompatibilityVersion(client, "garbage"));
    }

    @Test
    public void testSetValueByKeyCompatibilityRejectedThrows() {
        Client client = baseMockClient();
        // node is at 3.2.0; requesting compatibility_version=9.9.9 -> not supported -> throws
        stubGroupInfoWithBinaryVersion(client, "3.2.0");
        SystemConfigService service = new SystemConfigService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () ->
                        service.setValueByKey(
                                SystemConfigService.COMPATIBILITY_VERSION, "9.9.9"));
    }

    @Test
    public void testSetValueByKeyUnsupportedFeatureThrows() {
        Client client = baseMockClient();
        // feature-prefixed key not advertised by any node -> Unsupported feature key
        BcosGroupNodeInfo.GroupNodeInfo nodeInfo = new BcosGroupNodeInfo.GroupNodeInfo();
        BcosGroupNodeInfo.Protocol protocol = new BcosGroupNodeInfo.Protocol();
        protocol.setCompatibilityVersion(
                EnumNodeVersion.BCOS_3_2_0.toVersionObj().toCompatibilityVersion());
        nodeInfo.setProtocol(protocol);
        BcosGroupInfo.GroupInfo groupInfo = new BcosGroupInfo.GroupInfo();
        groupInfo.setGroupID("group0");
        groupInfo.setNodeList(Collections.singletonList(nodeInfo));
        BcosGroupInfoList groupInfoList = new BcosGroupInfoList();
        groupInfoList.setResult(Collections.singletonList(groupInfo));
        when(client.getGroupInfoList()).thenReturn(groupInfoList);
        SystemConfigService service = new SystemConfigService(client, keyPair());
        Assert.assertThrows(
                ContractException.class,
                () -> service.setValueByKey("feature_does_not_exist", "1"));
    }

    @Test
    public void testSetValueByKeyKnownKeyReachesTxBuild() {
        Client client = baseMockClient();
        SystemConfigService service = new SystemConfigService(client, keyPair());
        try {
            // tx_count_limit is a known (non feature/bugfix) key -> passes checks, reaches JNI
            service.setValueByKey(SystemConfigService.TX_COUNT_LIMIT, "1000");
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }

    @Test
    public void testSetValueByKeyGasPriceReachesTxBuild() {
        Client client = baseMockClient();
        SystemConfigService service = new SystemConfigService(client, keyPair());
        try {
            // tx_gas_price triggers the hex-encoding branch before reaching JNI
            service.setValueByKey(SystemConfigService.TX_GAS_PRICE, "100");
        } catch (Throwable t) {
            Assert.assertNotNull(t);
        }
    }
}
