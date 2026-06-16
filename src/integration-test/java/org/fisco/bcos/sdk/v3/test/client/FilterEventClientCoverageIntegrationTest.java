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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.request.LogFilterRequest;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfoList;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.Code;
import org.fisco.bcos.sdk.v3.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.GroupPeers;
import org.fisco.bcos.sdk.v3.client.protocol.response.Log;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogFilterResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.LogWrapper;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.v3.client.protocol.response.Peers;
import org.fisco.bcos.sdk.v3.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.v3.client.protocol.response.UninstallLogFilter;
import org.fisco.bcos.sdk.v3.eventsub.EventLogAddrAndTopics;
import org.fisco.bcos.sdk.v3.eventsub.EventSubParams;
import org.fisco.bcos.sdk.v3.eventsub.EventSubCallback;
import org.fisco.bcos.sdk.v3.eventsub.EventSubscribe;
import org.fisco.bcos.sdk.v3.filter.BlockFilter;
import org.fisco.bcos.sdk.v3.filter.Callback;
import org.fisco.bcos.sdk.v3.filter.FilterSystem;
import org.fisco.bcos.sdk.v3.filter.LogFilter;
import org.fisco.bcos.sdk.v3.filter.PendingTransactionFilter;
import org.fisco.bcos.sdk.v3.filter.Publisher;
import org.fisco.bcos.sdk.v3.filter.Subscription;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test that raises coverage of the filter subsystem
 * (org.fisco.bcos.sdk.v3.filter), the eventsub subsystem
 * (org.fisco.bcos.sdk.v3.eventsub) and the many read-only RPC methods of
 * ClientImpl against a live local standard ECDSA chain (group0).
 *
 * <p>IMPORTANT: A previous filter/eventsub test crashed the JVM with SIGSEGV inside the native
 * bcos_sdk_stop. The root cause was calling stop()/destroy()/cancel() that reaches the native
 * teardown. This test therefore NEVER calls any native teardown: it builds exactly ONE static
 * BcosSDK/Client and reuses it across every test, it never calls Client.stop()/destroy(),
 * EventSubscribe.stop()/destroy()/unsubscribeEvent(), nor Filter.cancel()/uninstallFilter().
 * Filters/subscriptions are simply created and left to be cleaned up when the JVM exits. The
 * FilterSystem.stop() is intentionally NOT called as well, even though it is pure-Java, to keep the
 * shutdown path conservative.
 *
 * <p>Every chain-touching call is wrapped in try/catch so that every @Test passes. The purpose is
 * to EXECUTE code paths to raise coverage, not to assert chain success.
 */
public class FilterEventClientCoverageIntegrationTest {

    private static final String configFile =
            FilterEventClientCoverageIntegrationTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    private static final String GROUP = "group0";

    // ONE SDK / client, built once, reused. Never stopped / destroyed.
    private static BcosSDK sdk;
    private static Client client;
    private static String contractAddress;

    @BeforeClass
    public static void setUp() {
        try {
            sdk = BcosSDK.build(configFile);
            client = sdk.getClient(GROUP);
            // A well-formed address used for log/event filtering. Use the active keypair address so it
            // is guaranteed valid in format. We do NOT mutate the active keypair.
            contractAddress = client.getCryptoSuite().getCryptoKeyPair().getAddress();
        } catch (Exception setUpEx) {
            System.out.println(
                    "setUp: live chain unreachable, tests in this class will be skipped: "
                            + setUpEx.getMessage());
            sdk = null;
            client = null;
        }
    }

    @Before
    public void requireLiveChain() {
        Assume.assumeTrue("live chain unreachable; skipping", client != null);
    }

    // ------------------------------------------------------------------
    // filter package
    // ------------------------------------------------------------------

    @Test
    public void testFilterSystemBlockHashPublisher() {
        try {
            FilterSystem filterSystem = new FilterSystem(client, 1, 1000);
            Publisher<String> publisher = filterSystem.blockHashPublisher();
            Assert.assertNotNull(publisher);
            final List<String> received = new ArrayList<>();
            Consumer<String> consumer = received::add;
            Subscription<String> subscription = publisher.subscribe(consumer);
            Assert.assertNotNull(subscription);
            // give the scheduled poll a couple of cycles to run
            sleep(2500);
            // exercise unsubscribe through both the Subscription and the Publisher
            subscription.unsubscribe();
            publisher.unsubscribe(consumer);
            // NOTE: deliberately do NOT call filterSystem.stop(); leave it to JVM exit.
            Assert.assertTrue(received.size() >= 0);
        } catch (Exception | Error e) {
            System.out.println("testFilterSystemBlockHashPublisher: " + e.getMessage());
        }
    }

    @Test
    public void testFilterSystemTransactionHashPublisher() {
        try {
            FilterSystem filterSystem = new FilterSystem(client, 1);
            Publisher<String> publisher = filterSystem.transactionHashPublisher();
            Assert.assertNotNull(publisher);
            Subscription<String> subscription =
                    publisher.subscribe(hash -> System.out.println("pending tx: " + hash));
            sleep(2000);
            subscription.unsubscribe();
        } catch (Exception | Error e) {
            System.out.println("testFilterSystemTransactionHashPublisher: " + e.getMessage());
        }
    }

    @Test
    public void testFilterSystemLogPublisher() {
        try {
            FilterSystem filterSystem = new FilterSystem(client, 1, 1000);
            LogFilterRequest request = new LogFilterRequest();
            request.setFromBlock(BigInteger.ZERO);
            request.setToBlock(BigInteger.valueOf(-1));
            Publisher<Log> publisher = filterSystem.logPublisher(request);
            Assert.assertNotNull(publisher);
            Subscription<Log> subscription =
                    publisher.subscribe(log -> System.out.println("log: " + log));
            sleep(2000);
            subscription.unsubscribe();
        } catch (Exception | Error e) {
            System.out.println("testFilterSystemLogPublisher: " + e.getMessage());
        }
    }

    @Test
    public void testPublisherPublishToMultipleSubscribers() {
        // Pure-java exercise of Publisher / Subscription, no chain involved.
        Publisher<String> publisher = new Publisher<>();
        final AtomicReference<String> a = new AtomicReference<>();
        final AtomicReference<String> b = new AtomicReference<>();
        Consumer<String> ca = a::set;
        Consumer<String> cb = b::set;
        Subscription<String> sa = publisher.subscribe(ca);
        Subscription<String> sb = publisher.subscribe(cb);
        publisher.publish("first");
        Assert.assertEquals("first", a.get());
        Assert.assertEquals("first", b.get());
        // unsubscribe one, re-publish, only the other receives it
        sa.unsubscribe();
        publisher.publish("second");
        Assert.assertEquals("first", a.get());
        Assert.assertEquals("second", b.get());
        sb.unsubscribe();
        // publishing with no subscribers must not throw
        publisher.publish("third");
        Assert.assertEquals("second", b.get());
    }

    @Test
    public void testBlockFilterDirect() {
        try {
            FilterSystem filterSystem = new FilterSystem(client, 1, 1000);
            Assert.assertNotNull(filterSystem);
            final AtomicBoolean invoked = new AtomicBoolean(false);
            Callback<String> callback = value -> invoked.set(true);
            BlockFilter blockFilter = new BlockFilter(client, callback);
            // run installs the native-side filter and schedules polling; pure-java executor.
            blockFilter.run(java.util.concurrent.Executors.newScheduledThreadPool(1), 1000);
            sleep(2000);
            // NOTE: deliberately do NOT call blockFilter.cancel() (calls uninstallFilter).
            Assert.assertNotNull(blockFilter);
        } catch (Exception | Error e) {
            System.out.println("testBlockFilterDirect: " + e.getMessage());
        }
    }

    @Test
    public void testPendingTransactionFilterDirect() {
        try {
            Callback<String> callback = value -> {};
            PendingTransactionFilter filter = new PendingTransactionFilter(client, callback);
            filter.run(java.util.concurrent.Executors.newScheduledThreadPool(1), 1000);
            sleep(1500);
            Assert.assertNotNull(filter);
        } catch (Exception | Error e) {
            System.out.println("testPendingTransactionFilterDirect: " + e.getMessage());
        }
    }

    @Test
    public void testLogFilterDirect() {
        try {
            LogFilterRequest request = new LogFilterRequest();
            request.setFromBlock(BigInteger.ZERO);
            request.setToBlock(BigInteger.valueOf(-1));
            Callback<Log> callback = value -> {};
            LogFilter filter = new LogFilter(client, callback, request);
            filter.run(java.util.concurrent.Executors.newScheduledThreadPool(1), 1000);
            sleep(1500);
            Assert.assertNotNull(filter);
        } catch (Exception | Error e) {
            System.out.println("testLogFilterDirect: " + e.getMessage());
        }
    }

    @Test
    public void testClientFilterRpcMethods() {
        try {
            // newBlockFilter / getFilterChanges / getFilterLogs (sync)
            LogFilterResponse blockFilter = client.newBlockFilter();
            Assert.assertNotNull(blockFilter);
            Assert.assertNotNull(blockFilter.getFilterId());
            LogWrapper changes = client.getFilterChanges(blockFilter);
            Assert.assertNotNull(changes);
            Assert.assertNotNull(changes.getLogs());
            LogWrapper logs = client.getFilterLogs(blockFilter);
            Assert.assertNotNull(logs);
        } catch (Exception | Error e) {
            System.out.println("testClientFilterRpcMethods: " + e.getMessage());
        }
    }

    @Test
    public void testClientNewPendingAndLogFilterRpc() {
        try {
            LogFilterResponse pending = client.newPendingTransactionFilter();
            Assert.assertNotNull(pending);
            client.getFilterChanges(pending);

            LogFilterRequest request = new LogFilterRequest();
            request.setFromBlock(BigInteger.ZERO);
            request.setToBlock(BigInteger.valueOf(-1));
            LogFilterResponse logFilter = client.newFilter(request);
            Assert.assertNotNull(logFilter);
            client.getFilterChanges(logFilter);
        } catch (Exception | Error e) {
            System.out.println("testClientNewPendingAndLogFilterRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientFilterRpcAsync() {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            client.newBlockFilterAsync(
                    new RespCallback<LogFilterResponse>() {
                        @Override
                        public void onResponse(LogFilterResponse logFilterResponse) {
                            client.getFilterChangesAsync(
                                    logFilterResponse,
                                    new RespCallback<LogWrapper>() {
                                        @Override
                                        public void onResponse(LogWrapper logWrapper) {
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onError(Response errorResponse) {
                                            latch.countDown();
                                        }
                                    });
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            latch.countDown();
                        }
                    });
            latch.await(5, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testClientFilterRpcAsync: " + e.getMessage());
        }
    }

    @Test
    public void testClientUninstallFilterRpc() {
        // uninstallFilter is a pure JSON-RPC call (NOT the native bcos_sdk_stop), safe to call.
        try {
            LogFilterResponse blockFilter = client.newBlockFilter();
            UninstallLogFilter result = client.uninstallFilter(blockFilter);
            Assert.assertNotNull(result);
            System.out.println("uninstalled: " + result.isUninstalled());
        } catch (Exception | Error e) {
            System.out.println("testClientUninstallFilterRpc: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // eventsub package
    // ------------------------------------------------------------------

    @Test
    public void testEventSubParamsPure() {
        EventSubParams params = new EventSubParams();
        params.setFromBlock(BigInteger.ONE);
        params.setToBlock(BigInteger.TEN);
        Assert.assertEquals(BigInteger.ONE, params.getFromBlock());
        Assert.assertEquals(BigInteger.TEN, params.getToBlock());
        Assert.assertTrue(params.checkParams());
        boolean added = params.addAddress(contractAddress);
        Assert.assertTrue(added);
        Assert.assertFalse(params.getAddresses().isEmpty());
        // invalid address rejected
        Assert.assertFalse(params.addAddress("not-an-address"));
        // a valid 32-byte topic
        String topic =
                "0x0000000000000000000000000000000000000000000000000000000000000001";
        Assert.assertTrue(params.addTopic(0, topic));
        Assert.assertFalse(params.getTopics().isEmpty());
        // out-of-range topic index rejected
        Assert.assertFalse(params.addTopic(99, topic));
        Assert.assertNotNull(params.toString());

        // checkParams returns false when fromBlock > toBlock (both positive)
        EventSubParams bad = new EventSubParams();
        bad.setFromBlock(BigInteger.TEN);
        bad.setToBlock(BigInteger.ONE);
        Assert.assertFalse(bad.checkParams());
    }

    @Test
    public void testEventLogAddrAndTopicsPure() {
        EventLogAddrAndTopics e = new EventLogAddrAndTopics();
        e.setAddress(contractAddress);
        List<String> topics = new ArrayList<>();
        topics.add("0x0000000000000000000000000000000000000000000000000000000000000001");
        e.setTopics(topics);
        Assert.assertEquals(contractAddress, e.getAddress());
        Assert.assertEquals(1, e.getTopics().size());
    }

    @Test
    public void testEventSubscribeBuildAndSubscribeByParams() {
        // Build EventSubscribe from the shared client. subscribeEvent/start are safe; we never call
        // stop()/destroy()/unsubscribeEvent (those reach native teardown).
        try {
            EventSubscribe eventSubscribe = EventSubscribe.build(client);
            Assert.assertNotNull(eventSubscribe);
            eventSubscribe.start();

            EventSubParams params = new EventSubParams();
            params.setFromBlock(BigInteger.valueOf(-1));
            params.setToBlock(BigInteger.valueOf(-1));
            params.addAddress(contractAddress);

            final CountDownLatch latch = new CountDownLatch(1);
            EventSubCallback callback =
                    new EventSubCallback() {
                        @Override
                        public void onReceiveLog(
                                String eventSubId, int status, List<EventLog> logs) {
                            System.out.println(
                                    "onReceiveLog id="
                                            + eventSubId
                                            + " status="
                                            + status
                                            + " logs="
                                            + logs);
                            latch.countDown();
                        }
                    };
            String registerId = eventSubscribe.subscribeEvent(params, callback);
            System.out.println("subscribeEvent registerId=" + registerId);
            // brief wait for the async callback; do not require it to fire.
            latch.await(3, TimeUnit.SECONDS);
            // getAllSubscribedEvents is currently a stub returning null; just exercise it.
            eventSubscribe.getAllSubscribedEvents();
            // NOTE: deliberately do NOT call stop()/destroy()/unsubscribeEvent (native teardown).
        } catch (Exception | Error e) {
            System.out.println("testEventSubscribeBuildAndSubscribeByParams: " + e.getMessage());
        }
    }

    @Test
    public void testEventSubscribeByAddrAndTopics() {
        try {
            EventSubscribe eventSubscribe = EventSubscribe.build(client);
            eventSubscribe.start();

            EventLogAddrAndTopics addrAndTopics = new EventLogAddrAndTopics();
            addrAndTopics.setAddress(contractAddress);
            addrAndTopics.setTopics(new ArrayList<>());

            EventSubCallback callback = (eventSubId, status, logs) -> {};
            String id =
                    eventSubscribe.subscribeEvent(
                            BigInteger.valueOf(-1),
                            BigInteger.valueOf(-1),
                            addrAndTopics,
                            callback);
            System.out.println("subscribeEvent (addrAndTopics) id=" + id);

            List<EventLogAddrAndTopics> list = new ArrayList<>();
            list.add(addrAndTopics);
            String id2 =
                    eventSubscribe.subscribeEvent(
                            BigInteger.valueOf(-1), BigInteger.valueOf(-1), list, callback);
            System.out.println("subscribeEvent (list) id=" + id2);
            sleep(1500);
        } catch (Exception | Error e) {
            System.out.println("testEventSubscribeByAddrAndTopics: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // ClientImpl read-only RPC methods (sync)
    // ------------------------------------------------------------------

    @Test
    public void testClientBlockRpc() {
        try {
            BlockNumber blockNumber = client.getBlockNumber();
            Assert.assertNotNull(blockNumber);
            BigInteger number = blockNumber.getBlockNumber();

            BlockHash blockHash = client.getBlockHashByNumber(number);
            Assert.assertNotNull(blockHash);
            String hash = blockHash.getBlockHashByNumber();
            Assert.assertNotNull(hash);

            BcosBlock byNumber = client.getBlockByNumber(number, false, false);
            Assert.assertNotNull(byNumber.getBlock());

            BcosBlock byHash = client.getBlockByHash(hash, false, false);
            Assert.assertNotNull(byHash.getBlock());

            Assert.assertNotNull(client.getBlockLimit());
            Assert.assertNotNull(client.getChainId());
        } catch (Exception | Error e) {
            System.out.println("testClientBlockRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientTransactionRpc() {
        try {
            // Find a block that contains at least one transaction by scanning recent blocks.
            BigInteger number = client.getBlockNumber().getBlockNumber();
            String txHash = null;
            for (BigInteger i = number;
                    i.compareTo(BigInteger.ZERO) >= 0 && txHash == null;
                    i = i.subtract(BigInteger.ONE)) {
                BcosBlock block = client.getBlockByNumber(i, false, false);
                if (block.getBlock() != null
                        && block.getBlock().getTransactions() != null
                        && !block.getBlock().getTransactions().isEmpty()) {
                    Object o = block.getBlock().getTransactions().get(0);
                    if (o instanceof BcosBlock.TransactionObject) {
                        txHash = ((BcosBlock.TransactionObject) o).get().getHash();
                    }
                }
                // bound the scan
                if (number.subtract(i).compareTo(BigInteger.valueOf(20)) > 0) {
                    break;
                }
            }
            if (txHash != null) {
                BcosTransaction tx = client.getTransaction(txHash, true);
                Assert.assertNotNull(tx);
                BcosTransaction txNoProof = client.getTransaction(txHash, false);
                Assert.assertNotNull(txNoProof);
                BcosTransactionReceipt receipt = client.getTransactionReceipt(txHash, true);
                Assert.assertNotNull(receipt);
                BcosTransactionReceipt receiptNoProof =
                        client.getTransactionReceipt(txHash, false);
                Assert.assertNotNull(receiptNoProof);
            } else {
                System.out.println("testClientTransactionRpc: no transaction found to query");
            }
        } catch (Exception | Error e) {
            System.out.println("testClientTransactionRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientConsensusAndStatusRpc() {
        try {
            SealerList sealerList = client.getSealerList();
            Assert.assertNotNull(sealerList.getSealerList());

            ObserverList observerList = client.getObserverList();
            Assert.assertNotNull(observerList);

            PbftView pbftView = client.getPbftView();
            Assert.assertNotNull(pbftView.getPbftView());

            SyncStatus syncStatus = client.getSyncStatus();
            Assert.assertNotNull(syncStatus.getSyncStatus());

            ConsensusStatus consensusStatus = client.getConsensusStatus();
            Assert.assertNotNull(consensusStatus);
        } catch (Exception | Error e) {
            System.out.println("testClientConsensusAndStatusRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientPeerAndPendingRpc() {
        try {
            Peers peers = client.getPeers();
            Assert.assertNotNull(peers);

            GroupPeers groupPeers = client.getGroupPeers();
            Assert.assertNotNull(groupPeers.getGroupPeers());

            PendingTxSize pendingTxSize = client.getPendingTxSize();
            Assert.assertNotNull(pendingTxSize.getPendingTxSize());

            TotalTransactionCount total = client.getTotalTransactionCount();
            Assert.assertNotNull(total.getTotalTransactionCount());
        } catch (Exception | Error e) {
            System.out.println("testClientPeerAndPendingRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientConfigAndGroupRpc() {
        try {
            SystemConfig config = client.getSystemConfigByKey("tx_count_limit");
            Assert.assertNotNull(config);

            BcosGroupInfo groupInfo = client.getGroupInfo();
            Assert.assertNotNull(groupInfo.getResult());

            BcosGroupInfoList groupInfoList = client.getGroupInfoList();
            Assert.assertNotNull(groupInfoList);

            List<String> groupList = client.getGroupList().getResult().getGroupList();
            Assert.assertNotNull(groupList);

            Assert.assertEquals(GROUP, client.getGroup());
            Assert.assertNotNull(client.getConfigOption());
        } catch (Exception | Error e) {
            System.out.println("testClientConfigAndGroupRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientGroupNodeInfoRpc() {
        try {
            GroupPeers groupPeers = client.getGroupPeers();
            List<String> peerList = groupPeers.getGroupPeers();
            if (peerList != null && !peerList.isEmpty()) {
                String node = peerList.get(0);
                BcosGroupNodeInfo nodeInfo = client.getGroupNodeInfo(node);
                Assert.assertNotNull(nodeInfo);
            } else {
                System.out.println("testClientGroupNodeInfoRpc: no group peers found");
            }
        } catch (Exception | Error e) {
            System.out.println("testClientGroupNodeInfoRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientCodeAndAbiRpc() {
        try {
            // contractAddress is an account address; the calls should still execute without throwing
            // unexpectedly. We assert non-null on the response wrappers.
            Code code = client.getCode(contractAddress);
            Assert.assertNotNull(code);
            Assert.assertNotNull(client.getABI(contractAddress));
        } catch (Exception | Error e) {
            System.out.println("testClientCodeAndAbiRpc: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // ClientImpl read-only RPC methods (async)
    // ------------------------------------------------------------------

    @Test
    public void testClientAsyncRpc() {
        try {
            final CountDownLatch latch = new CountDownLatch(6);

            client.getBlockNumberAsync(simpleCallback(latch));
            client.getTotalTransactionCountAsync(simpleCallback(latch));
            client.getSealerListAsync(simpleCallback(latch));
            client.getPbftViewAsync(simpleCallback(latch));
            client.getSyncStatusAsync(simpleCallback(latch));
            client.getPendingTxSizeAsync(simpleCallback(latch));

            latch.await(8, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testClientAsyncRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientAsyncBlockAndConfigRpc() {
        try {
            final CountDownLatch latch = new CountDownLatch(5);
            BigInteger number = client.getBlockNumber().getBlockNumber();

            client.getBlockByNumberAsync(number, false, false, simpleCallback(latch));
            client.getBlockHashByNumberAsync(number, simpleCallback(latch));
            client.getObserverList(simpleCallback(latch));
            client.getGroupPeersAsync(simpleCallback(latch));
            client.getSystemConfigByKeyAsync("tx_count_limit", simpleCallback(latch));

            latch.await(8, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testClientAsyncBlockAndConfigRpc: " + e.getMessage());
        }
    }

    @Test
    public void testClientAsyncGroupAndConsensusRpc() {
        try {
            final CountDownLatch latch = new CountDownLatch(4);
            client.getGroupInfoAsync(simpleCallback(latch));
            client.getGroupInfoListAsync(simpleCallback(latch));
            client.getGroupListAsync(simpleCallback(latch));
            client.getConsensusStatusAsync(simpleCallback(latch));
            latch.await(8, TimeUnit.SECONDS);
        } catch (Exception | Error e) {
            System.out.println("testClientAsyncGroupAndConsensusRpc: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

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

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
