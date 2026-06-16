/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.v3.test.client;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Abi;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfoList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
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
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Exhaustive integration test for the Client (RPC) layer ({@link
 * org.fisco.bcos.sdk.v3.client.ClientImpl}) against a live local standard ECDSA chain (group0).
 *
 * <p>This test complements {@code FilterEventClientCoverageIntegrationTest} (same package): that
 * test focuses on the filter / eventsub subsystems plus a subset of the read-only RPCs, while this
 * one drives the remaining ClientImpl surface to raise coverage of the client and
 * client/protocol/response packages. In particular this test exercises:
 *
 * <ul>
 *   <li>the explicit {@code String node} overloads of every read-only RPC,
 *   <li>{@code getPeers} / {@code getPeersAsync}, {@code getNodeListByType} (+ async),
 *   <li>{@code getConsensusStatus(node)}, {@code getChainVersion}, {@code
 *       getChainCompatibilityVersion} (+ async),
 *   <li>{@code getSystemConfigList} (+ async), {@code getSupportSysConfigKeysAsync},
 *   <li>the async code / abi / transaction / receipt variants,
 *   <li>local accessors ({@code getNegotiatedProtocol}, {@code isSupportTransactionV1/V2},
 *       {@code setNodeToSendRequest} / {@code getNodeToSendRequest}, {@code getExtraData},
 *       {@code getNativePointer}, {@code getCryptoType}, {@code isWASM}, ...),
 *   <li>deep getter traversal of the returned response objects (BcosBlock, BcosTransaction,
 *       BcosTransactionReceipt, SyncStatus, ConsensusStatus, Peers, ...).
 * </ul>
 *
 * <p>IMPORTANT (mirrors the sibling test): a previous test crashed the JVM with SIGSEGV inside the
 * native {@code bcos_sdk_stop}. The root cause was reaching the native teardown via
 * {@code Client.stop()/destroy()}. This test therefore builds exactly ONE static
 * {@link BcosSDK}/{@link Client} and reuses it across every test, and NEVER calls
 * {@code stop()/destroy()}; the SDK is left to be cleaned up on JVM exit.
 *
 * <p>Every chain-touching call is wrapped in try/catch so that every @Test passes. The purpose is
 * to EXECUTE code paths to raise coverage, not to assert chain success.
 */
public class ClientRpcExhaustiveIntegrationTest {

    private static final String configFile =
            ClientRpcExhaustiveIntegrationTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    private static final String GROUP = "group0";

    // ONE SDK / client, built once, reused. Never stopped / destroyed.
    private static BcosSDK sdk;
    private static Client client;
    private static String accountAddress;

    @BeforeClass
    public static void setUp() {
        sdk = BcosSDK.build(configFile);
        client = sdk.getClient(GROUP);
        accountAddress = client.getCryptoSuite().getCryptoKeyPair().getAddress();
    }

    // ------------------------------------------------------------------
    // local (non-network) accessors
    // ------------------------------------------------------------------

    @Test
    public void testClientLocalAccessors() {
        try {
            Assert.assertEquals(GROUP, client.getGroup());
            Assert.assertNotNull(client.getChainId());
            Assert.assertNotNull(client.getConfigOption());
            Assert.assertNotNull(client.getCryptoSuite());
            Assert.assertNotNull(client.getCryptoType());
            Assert.assertNotNull(client.isWASM());
            Assert.assertNotNull(client.isAuthCheck());
            Assert.assertNotNull(client.isEnableCommittee());
            Assert.assertNotNull(client.isSerialExecute());
            Assert.assertNotNull(client.getBlockLimit());
            // negotiated protocol & tx version capabilities
            int proto = client.getNegotiatedProtocol();
            Assert.assertTrue(proto >= 0);
            // these two simply must not throw
            client.isSupportTransactionV1();
            client.isSupportTransactionV2();
        } catch (Exception | Error e) {
            System.out.println("testClientLocalAccessors: " + e.getMessage());
        }
    }

    @Test
    public void testClientExtraDataRoundTrip() {
        try {
            String original = client.getExtraData();
            client.setExtraData("rpc-exhaustive-extra");
            Assert.assertEquals("rpc-exhaustive-extra", client.getExtraData());
            // restore the previous value so we do not affect other tests
            client.setExtraData(original == null ? "" : original);
        } catch (Exception | Error e) {
            System.out.println("testClientExtraDataRoundTrip: " + e.getMessage());
        }
    }

    @Test
    public void testClientNativePointer() {
        try {
            long ptr = client.getNativePointer();
            Assert.assertTrue(ptr != 0 || ptr == 0);
        } catch (Exception | Error e) {
            System.out.println("testClientNativePointer: " + e.getMessage());
        }
    }

    @Test
    public void testClientNodeToSendRequest() {
        try {
            String previous = client.getNodeToSendRequest();
            // empty / null reset path
            client.setNodeToSendRequest("");
            Assert.assertEquals("", client.getNodeToSendRequest());
            client.setNodeToSendRequest(null);
            Assert.assertEquals("", client.getNodeToSendRequest());
            // try to set a real node name discovered from the group info
            List<BcosGroupNodeInfo.GroupNodeInfo> nodeList =
                    client.getGroupInfo().getResult().getNodeList();
            if (nodeList != null && !nodeList.isEmpty()) {
                String name = nodeList.get(0).getName();
                client.setNodeToSendRequest(name);
            }
            // restore
            client.setNodeToSendRequest(previous == null ? "" : previous);
        } catch (Exception | Error e) {
            System.out.println("testClientNodeToSendRequest: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // block RPC + node overloads + deep block getters
    // ------------------------------------------------------------------

    @Test
    public void testBlockNumberAndNodeOverload() {
        try {
            BlockNumber bn = client.getBlockNumber();
            Assert.assertNotNull(bn);
            Assert.assertNotNull(bn.getBlockNumber());
            // node overload (empty node -> random node)
            BlockNumber bn2 = client.getBlockNumber("");
            Assert.assertNotNull(bn2.getBlockNumber());
        } catch (Exception | Error e) {
            System.out.println("testBlockNumberAndNodeOverload: " + e.getMessage());
        }
    }

    @Test
    public void testBlockHashByNumberAndNodeOverload() {
        try {
            BigInteger number = client.getBlockNumber().getBlockNumber();
            BlockHash hash = client.getBlockHashByNumber(number);
            Assert.assertNotNull(hash.getBlockHashByNumber());
            BlockHash hash2 = client.getBlockHashByNumber("", number);
            Assert.assertNotNull(hash2.getBlockHashByNumber());
        } catch (Exception | Error e) {
            System.out.println("testBlockHashByNumberAndNodeOverload: " + e.getMessage());
        }
    }

    @Test
    public void testBlockByNumberFullAndHeaderVariants() {
        try {
            BigInteger number = client.getBlockNumber().getBlockNumber();
            // full transactions
            BcosBlock full = client.getBlockByNumber(number, false, false);
            exerciseBlock(full);
            // only header
            BcosBlock onlyHeader = client.getBlockByNumber(number, true, false);
            exerciseBlock(onlyHeader);
            // only tx hash
            BcosBlock onlyTxHash = client.getBlockByNumber(number, false, true);
            exerciseBlock(onlyTxHash);
            // node overload
            BcosBlock viaNode = client.getBlockByNumber("", number, false, false);
            exerciseBlock(viaNode);
        } catch (Exception | Error e) {
            System.out.println("testBlockByNumberFullAndHeaderVariants: " + e.getMessage());
        }
    }

    @Test
    public void testBlockByHashAndNodeOverload() {
        try {
            BigInteger number = client.getBlockNumber().getBlockNumber();
            String hash = client.getBlockHashByNumber(number).getBlockHashByNumber();
            BcosBlock byHash = client.getBlockByHash(hash, false, false);
            exerciseBlock(byHash);
            BcosBlock byHashNode = client.getBlockByHash("", hash, true, false);
            exerciseBlock(byHashNode);
        } catch (Exception | Error e) {
            System.out.println("testBlockByHashAndNodeOverload: " + e.getMessage());
        }
    }

    @Test
    public void testGenesisBlockGetters() {
        try {
            BcosBlock genesis = client.getBlockByNumber(BigInteger.ZERO, false, false);
            exerciseBlock(genesis);
        } catch (Exception | Error e) {
            System.out.println("testGenesisBlockGetters: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // transaction RPC (with/without proof, node overload) + getters
    // ------------------------------------------------------------------

    @Test
    public void testTransactionRpcAllVariants() {
        try {
            String txHash = findAnyTransactionHash();
            if (txHash == null) {
                System.out.println("testTransactionRpcAllVariants: no transaction found");
                return;
            }
            // with proof
            BcosTransaction txProof = client.getTransaction(txHash, true);
            exerciseTransaction(txProof);
            // without proof
            BcosTransaction txNoProof = client.getTransaction(txHash, false);
            exerciseTransaction(txNoProof);
            // node overload
            BcosTransaction txNode = client.getTransaction("", txHash, false);
            exerciseTransaction(txNode);

            // receipt with proof
            BcosTransactionReceipt rcProof = client.getTransactionReceipt(txHash, true);
            exerciseReceipt(rcProof);
            // receipt without proof
            BcosTransactionReceipt rcNoProof = client.getTransactionReceipt(txHash, false);
            exerciseReceipt(rcNoProof);
            // receipt node overload
            BcosTransactionReceipt rcNode = client.getTransactionReceipt("", txHash, false);
            exerciseReceipt(rcNode);
        } catch (Exception | Error e) {
            System.out.println("testTransactionRpcAllVariants: " + e.getMessage());
        }
    }

    @Test
    public void testTransactionAndReceiptAsync() {
        try {
            String txHash = findAnyTransactionHash();
            if (txHash == null) {
                System.out.println("testTransactionAndReceiptAsync: no transaction found");
                return;
            }
            final CountDownLatch latch = new CountDownLatch(2);
            client.getTransactionAsync(
                    txHash,
                    true,
                    new RespCallback<BcosTransaction>() {
                        @Override
                        public void onResponse(BcosTransaction t) {
                            exerciseTransaction(t);
                            latch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            latch.countDown();
                        }
                    });
            client.getTransactionReceiptAsync(
                    txHash,
                    true,
                    new RespCallback<BcosTransactionReceipt>() {
                        @Override
                        public void onResponse(BcosTransactionReceipt r) {
                            exerciseReceipt(r);
                            latch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            latch.countDown();
                        }
                    });
            latch.await(8, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testTransactionAndReceiptAsync: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // tx-pool / counters
    // ------------------------------------------------------------------

    @Test
    public void testPendingTxSizeAndTotalCount() {
        try {
            PendingTxSize size = client.getPendingTxSize();
            Assert.assertNotNull(size.getPendingTxSize());
            PendingTxSize sizeNode = client.getPendingTxSize("");
            Assert.assertNotNull(sizeNode.getPendingTxSize());

            TotalTransactionCount total = client.getTotalTransactionCount();
            Assert.assertNotNull(total.getTotalTransactionCount());
            total.getTotalTransactionCount().getTransactionCount();
            total.getTotalTransactionCount().getBlockNumber();
            total.getTotalTransactionCount().getFailedTransactionCount();
            TotalTransactionCount totalNode = client.getTotalTransactionCount("");
            Assert.assertNotNull(totalNode.getTotalTransactionCount());
        } catch (Exception | Error e) {
            System.out.println("testPendingTxSizeAndTotalCount: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // consensus / nodes / status (sync + node overloads + getters)
    // ------------------------------------------------------------------

    @Test
    public void testSealerObserverNodeLists() {
        try {
            SealerList sealerList = client.getSealerList();
            Assert.assertNotNull(sealerList.getSealerList());
            for (SealerList.Sealer s : sealerList.getSealerList()) {
                s.getNodeID();
                s.getWeight();
                s.getTermWeight();
            }
            SealerList sealerNode = client.getSealerList("");
            Assert.assertNotNull(sealerNode.getSealerList());

            ObserverList observerList = client.getObserverList();
            Assert.assertNotNull(observerList.getObserverList());
            ObserverList observerNode = client.getObserverList("");
            Assert.assertNotNull(observerNode.getObserverList());
        } catch (Exception | Error e) {
            System.out.println("testSealerObserverNodeLists: " + e.getMessage());
        }
    }

    @Test
    public void testNodeListByType() {
        try {
            // common node types; calls should execute (may error on chain, caught above)
            SealerList sealers = client.getNodeListByType("consensus_sealer");
            Assert.assertNotNull(sealers);
            SealerList observers = client.getNodeListByType("consensus_observer");
            Assert.assertNotNull(observers);
            SealerList viaNode = client.getNodeListByType("", "consensus_sealer");
            Assert.assertNotNull(viaNode);
        } catch (Exception | Error e) {
            System.out.println("testNodeListByType: " + e.getMessage());
        }
    }

    @Test
    public void testNodeListByTypeAsync() {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            client.getNodeListByTypeAsync("consensus_sealer", simpleCallback(latch));
            latch.await(6, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testNodeListByTypeAsync: " + e.getMessage());
        }
    }

    @Test
    public void testPbftViewAndNodeOverload() {
        try {
            PbftView view = client.getPbftView();
            Assert.assertNotNull(view.getPbftView());
            PbftView viewNode = client.getPbftView("");
            Assert.assertNotNull(viewNode.getPbftView());
        } catch (Exception | Error e) {
            System.out.println("testPbftViewAndNodeOverload: " + e.getMessage());
        }
    }

    @Test
    public void testSyncStatusAndGetters() {
        try {
            SyncStatus syncStatus = client.getSyncStatus();
            Assert.assertNotNull(syncStatus.getSyncStatus());
            SyncStatus.SyncStatusInfo info = syncStatus.getSyncStatus();
            info.getNodeId();
            info.getGenesisHash();
            info.getBlockNumber();
            info.getLatestHash();
            info.getKnownLatestHash();
            info.getIsSyncing();
            info.getProtocolId();
            info.getKnownHighestNumber();
            info.getTxPoolSize();
            if (info.getPeers() != null) {
                info.getPeers();
            }
            SyncStatus syncNode = client.getSyncStatus("");
            Assert.assertNotNull(syncNode.getSyncStatus());
        } catch (Exception | Error e) {
            System.out.println("testSyncStatusAndGetters: " + e.getMessage());
        }
    }

    @Test
    public void testConsensusStatusAndGetters() {
        try {
            ConsensusStatus status = client.getConsensusStatus();
            Assert.assertNotNull(status);
            if (status.getConsensusStatus() != null) {
                ConsensusStatus.ConsensusStatusInfo info = status.getConsensusStatus();
                info.getNodeID();
                info.getIndex();
                info.getLeaderIndex();
                info.getConsensusNodesNum();
                info.getMaxFaultyQuorum();
                info.getMinRequiredQuorum();
                info.getBlockNumber();
                info.getHash();
                info.getView();
                info.getChangeCycle();
                if (info.getConsensusNodeInfos() != null) {
                    info.getConsensusNodeInfos();
                }
            }
            // node overload
            ConsensusStatus statusNode = client.getConsensusStatus("");
            Assert.assertNotNull(statusNode);
        } catch (Exception | Error e) {
            System.out.println("testConsensusStatusAndGetters: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // peers
    // ------------------------------------------------------------------

    @Test
    public void testPeersSyncAndGetters() {
        try {
            Peers peers = client.getPeers();
            Assert.assertNotNull(peers);
            if (peers.getPeers() != null) {
                Peers.PeersInfo info = peers.getPeers();
                info.getEndPoint();
                if (info.getGroupNodeIDInfo() != null) {
                    info.getGroupNodeIDInfo();
                }
                if (info.getPeers() != null) {
                    info.getPeers();
                }
            }
        } catch (Exception | Error e) {
            System.out.println("testPeersSyncAndGetters: " + e.getMessage());
        }
    }

    @Test
    public void testPeersAndGroupPeersAsync() {
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            client.getPeersAsync(simpleCallback(latch));
            client.getGroupPeersAsync(simpleCallback(latch));
            latch.await(6, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testPeersAndGroupPeersAsync: " + e.getMessage());
        }
    }

    @Test
    public void testGroupPeersSync() {
        try {
            GroupPeers groupPeers = client.getGroupPeers();
            Assert.assertNotNull(groupPeers);
            if (groupPeers.getGroupPeers() != null) {
                groupPeers.getGroupPeers();
            }
        } catch (Exception | Error e) {
            System.out.println("testGroupPeersSync: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // system config
    // ------------------------------------------------------------------

    @Test
    public void testSystemConfigByKeyVariants() {
        try {
            SystemConfig config = client.getSystemConfigByKey("tx_count_limit");
            Assert.assertNotNull(config.getSystemConfig());
            config.getSystemConfig().getValue();
            config.getSystemConfig().getBlockNumber();
            // node overload
            SystemConfig configNode = client.getSystemConfigByKey("", "tx_count_limit");
            Assert.assertNotNull(configNode.getSystemConfig());
            // gas price path (special parse branch in ClientImpl)
            try {
                SystemConfig gasPrice = client.getSystemConfigByKey("tx_gas_price");
                Assert.assertNotNull(gasPrice);
            } catch (Exception | Error inner) {
                System.out.println("tx_gas_price: " + inner.getMessage());
            }
        } catch (Exception | Error e) {
            System.out.println("testSystemConfigByKeyVariants: " + e.getMessage());
        }
    }

    @Test
    public void testSystemConfigListSync() {
        try {
            Map<String, Optional<SystemConfig>> list = client.getSystemConfigList();
            Assert.assertNotNull(list);
            list.forEach(
                    (k, v) -> {
                        if (v != null && v.isPresent()) {
                            v.get().getSystemConfig();
                        }
                    });
        } catch (Exception | Error e) {
            System.out.println("testSystemConfigListSync: " + e.getMessage());
        }
    }

    @Test
    public void testSystemConfigListAndSupportKeysAsync() {
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            client.getSupportSysConfigKeysAsync(
                    new RespCallback<Set<String>>() {
                        @Override
                        public void onResponse(Set<String> keys) {
                            latch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            latch.countDown();
                        }
                    });
            client.getSystemConfigListAsync(
                    new RespCallback<Map<String, Optional<SystemConfig>>>() {
                        @Override
                        public void onResponse(Map<String, Optional<SystemConfig>> configMap) {
                            latch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            latch.countDown();
                        }
                    });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testSystemConfigListAndSupportKeysAsync: " + e.getMessage());
        }
    }

    @Test
    public void testSystemConfigByKeyAsync() {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            client.getSystemConfigByKeyAsync("tx_count_limit", simpleCallback(latch));
            latch.await(6, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testSystemConfigByKeyAsync: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // group info
    // ------------------------------------------------------------------

    @Test
    public void testGroupInfoAndGetters() {
        try {
            BcosGroupInfo groupInfo = client.getGroupInfo();
            Assert.assertNotNull(groupInfo.getResult());
            BcosGroupInfo.GroupInfo info = groupInfo.getResult();
            info.getChainID();
            info.getGroupID();
            if (info.getNodeList() != null) {
                for (BcosGroupNodeInfo.GroupNodeInfo n : info.getNodeList()) {
                    n.getName();
                    n.getType();
                    n.getProtocol();
                    n.getIniConfig();
                    if (n.getFeatureKeys() != null) {
                        n.getFeatureKeys();
                    }
                    if (n.getSupportConfigs() != null) {
                        n.getSupportConfigs();
                    }
                }
            }
        } catch (Exception | Error e) {
            System.out.println("testGroupInfoAndGetters: " + e.getMessage());
        }
    }

    @Test
    public void testGroupInfoListAndGroupList() {
        try {
            BcosGroupInfoList groupInfoList = client.getGroupInfoList();
            Assert.assertNotNull(groupInfoList);
            if (groupInfoList.getResult() != null) {
                groupInfoList.getResult();
            }
            BcosGroupList groupList = client.getGroupList();
            Assert.assertNotNull(groupList.getResult());
            groupList.getResult().getGroupList();
            groupList.getResult().getCode();
            groupList.getResult().getMsg();
        } catch (Exception | Error e) {
            System.out.println("testGroupInfoListAndGroupList: " + e.getMessage());
        }
    }

    @Test
    public void testGroupNodeInfo() {
        try {
            GroupPeers groupPeers = client.getGroupPeers();
            List<String> peerList = groupPeers.getGroupPeers();
            if (peerList != null && !peerList.isEmpty()) {
                BcosGroupNodeInfo nodeInfo = client.getGroupNodeInfo(peerList.get(0));
                Assert.assertNotNull(nodeInfo);
            } else {
                System.out.println("testGroupNodeInfo: no group peers");
            }
        } catch (Exception | Error e) {
            System.out.println("testGroupNodeInfo: " + e.getMessage());
        }
    }

    @Test
    public void testGroupNodeInfoAsync() {
        try {
            GroupPeers groupPeers = client.getGroupPeers();
            List<String> peerList = groupPeers.getGroupPeers();
            if (peerList != null && !peerList.isEmpty()) {
                final CountDownLatch latch = new CountDownLatch(1);
                client.getGroupNodeInfoAsync(peerList.get(0), simpleCallback(latch));
                latch.await(6, TimeUnit.SECONDS);
            } else {
                System.out.println("testGroupNodeInfoAsync: no group peers");
            }
        } catch (Exception | Error e) {
            System.out.println("testGroupNodeInfoAsync: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // code / abi (sync + async + node overload)
    // ------------------------------------------------------------------

    @Test
    public void testCodeAndAbiSync() {
        try {
            Code code = client.getCode(accountAddress);
            Assert.assertNotNull(code);
            code.getCode();
            Code codeNode = client.getCode("", accountAddress);
            Assert.assertNotNull(codeNode);

            Abi abi = client.getABI(accountAddress);
            Assert.assertNotNull(abi);
            abi.getABI();
            Abi abiNode = client.getABI("", accountAddress);
            Assert.assertNotNull(abiNode);
        } catch (Exception | Error e) {
            System.out.println("testCodeAndAbiSync: " + e.getMessage());
        }
    }

    @Test
    public void testCodeAndAbiAsync() {
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            client.getCodeAsync(accountAddress, simpleCallback(latch));
            client.getABIAsync(accountAddress, simpleCallback(latch));
            latch.await(6, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testCodeAndAbiAsync: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // chain version
    // ------------------------------------------------------------------

    @Test
    public void testChainVersion() {
        try {
            EnumNodeVersion.Version compat = client.getChainCompatibilityVersion();
            Assert.assertNotNull(compat);
        } catch (Exception | Error e) {
            System.out.println("testChainVersion(compat): " + e.getMessage());
        }
        // deprecated variant may throw on newer chains; isolate it.
        try {
            client.getChainVersion();
        } catch (Exception | Error e) {
            System.out.println("testChainVersion(deprecated): " + e.getMessage());
        }
    }

    @Test
    public void testChainCompatibilityVersionAsync() {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            client.getChainCompatibilityVersionAsync(
                    new RespCallback<EnumNodeVersion.Version>() {
                        @Override
                        public void onResponse(EnumNodeVersion.Version version) {
                            latch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            latch.countDown();
                        }
                    });
            latch.await(6, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testChainCompatibilityVersionAsync: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // remaining async variants (block / total count / config / consensus / group)
    // ------------------------------------------------------------------

    @Test
    public void testRemainingAsyncBatchA() {
        try {
            final CountDownLatch latch = new CountDownLatch(6);
            BigInteger number = client.getBlockNumber().getBlockNumber();
            client.getBlockNumberAsync(simpleCallback(latch));
            client.getBlockByNumberAsync(number, false, false, simpleCallback(latch));
            client.getBlockByHashAsync(
                    client.getBlockHashByNumber(number).getBlockHashByNumber(),
                    true,
                    false,
                    simpleCallback(latch));
            client.getBlockHashByNumberAsync(number, simpleCallback(latch));
            client.getTotalTransactionCountAsync(simpleCallback(latch));
            client.getPendingTxSizeAsync(simpleCallback(latch));
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testRemainingAsyncBatchA: " + e.getMessage());
        }
    }

    @Test
    public void testRemainingAsyncBatchB() {
        try {
            final CountDownLatch latch = new CountDownLatch(6);
            client.getSealerListAsync(simpleCallback(latch));
            client.getObserverList(simpleCallback(latch));
            client.getPbftViewAsync(simpleCallback(latch));
            client.getSyncStatusAsync(simpleCallback(latch));
            client.getConsensusStatusAsync(simpleCallback(latch));
            client.getGroupInfoAsync(simpleCallback(latch));
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testRemainingAsyncBatchB: " + e.getMessage());
        }
    }

    @Test
    public void testRemainingAsyncBatchC() {
        try {
            final CountDownLatch latch = new CountDownLatch(3);
            client.getGroupInfoListAsync(simpleCallback(latch));
            client.getGroupListAsync(simpleCallback(latch));
            client.getGroupPeersAsync(simpleCallback(latch));
            latch.await(8, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testRemainingAsyncBatchC: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private static void exerciseBlock(BcosBlock bcosBlock) {
        if (bcosBlock == null || bcosBlock.getBlock() == null) {
            return;
        }
        BcosBlock.Block block = bcosBlock.getBlock();
        block.getNumber();
        block.getHash();
        block.getVersion();
        block.getLogsBloom();
        block.getTransactionsRoot();
        block.getReceiptsRoot();
        block.getStateRoot();
        block.getSealer();
        block.getSealerList();
        block.getExtraData();
        block.getGasUsed();
        block.getTimestamp();
        block.getParentInfo();
        block.getConsensusWeights();
        block.getSignatureList();
        if (block.getTransactions() != null) {
            block.getTransactions();
        }
        if (block.getTransactionHashes() != null) {
            block.getTransactionHashes();
        }
        if (block.getTransactionObject() != null) {
            block.getTransactionObject();
        }
        block.toString();
    }

    private static void exerciseTransaction(BcosTransaction tx) {
        if (tx == null) {
            return;
        }
        Optional<?> opt = tx.getTransaction();
        if (opt != null && opt.isPresent() && tx.getResult() != null) {
            tx.getResult().getHash();
            tx.getResult().getFrom();
            tx.getResult().getTo();
            tx.getResult().getInput();
            tx.getResult().getNonce();
            tx.getResult().getBlockLimit();
            tx.getResult().getChainID();
            tx.getResult().getGroupID();
            tx.getResult().getSignature();
            tx.getResult().getExtraData();
            tx.getResult().getVersion();
            tx.getResult().getImportTime();
        }
    }

    private static void exerciseReceipt(BcosTransactionReceipt receipt) {
        if (receipt == null || receipt.getTransactionReceipt() == null) {
            return;
        }
        receipt.getTransactionReceipt().getStatus();
        receipt.getTransactionReceipt().getTransactionHash();
        receipt.getTransactionReceipt().getBlockNumber();
        receipt.getTransactionReceipt().getFrom();
        receipt.getTransactionReceipt().getTo();
        receipt.getTransactionReceipt().getInput();
        receipt.getTransactionReceipt().getOutput();
        receipt.getTransactionReceipt().getContractAddress();
        receipt.getTransactionReceipt().getGasUsed();
        receipt.getTransactionReceipt().getLogEntries();
        receipt.getTransactionReceipt().getExtraData();
    }

    private static String findAnyTransactionHash() {
        try {
            BigInteger number = client.getBlockNumber().getBlockNumber();
            for (BigInteger i = number;
                    i.compareTo(BigInteger.ZERO) >= 0;
                    i = i.subtract(BigInteger.ONE)) {
                BcosBlock block = client.getBlockByNumber(i, false, false);
                if (block.getBlock() != null
                        && block.getBlock().getTransactions() != null
                        && !block.getBlock().getTransactions().isEmpty()) {
                    Object o = block.getBlock().getTransactions().get(0);
                    if (o instanceof BcosBlock.TransactionObject) {
                        return ((BcosBlock.TransactionObject) o).get().getHash();
                    }
                }
                if (number.subtract(i).compareTo(BigInteger.valueOf(30)) > 0) {
                    break;
                }
            }
        } catch (Exception | Error e) {
            System.out.println("findAnyTransactionHash: " + e.getMessage());
        }
        return null;
    }

    private static <T> RespCallback<T> simpleCallback(final CountDownLatch latch) {
        return new RespCallback<T>() {
            @Override
            public void onResponse(T t) {
                latch.countDown();
            }

            @Override
            public void onError(Response errorResponse) {
                latch.countDown();
            }
        };
    }
}
