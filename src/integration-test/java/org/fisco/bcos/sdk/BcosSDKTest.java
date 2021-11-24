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

package org.fisco.bcos.sdk;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.RespCallback;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;

public class BcosSDKTest {
    private static final String configFile =
            BcosSDKTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    private static final String GROUP = "group";

    @Test
    public void testClient() throws ConfigException, JniException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        // test getBlockNumber
        BlockNumber blockNumber = client.getBlockNumber();
        System.out.println("blockNumber=" + blockNumber.getBlockNumber());

        // test getBlockByNumber only header
        BcosBlock onlyHeader = client.getBlockByNumber(BigInteger.ZERO, false, false);
        System.out.println("genesis header=" + onlyHeader.getBlock());

        // test getBlockByNumber
        BcosBlock block = client.getBlockByNumber(BigInteger.ZERO, false, false);
        System.out.println("genesis block=" + block.getBlock());
        // getBlockByHash
        BcosBlock block0 = client.getBlockByHash(block.getBlock().getHash(), false, false);
        System.out.println("genesis block=" + block0.getBlock());
        Assert.assertEquals(block.getBlock(), block0.getBlock());

        // get SealerList
        SealerList sealerList = client.getSealerList();
        System.out.println(sealerList.getSealerList());

        // get observerList
        ObserverList observerList = client.getObserverList();
        System.out.println(observerList.getObserverList());

        // get pbftView
        PbftView pbftView = client.getPbftView();
        System.out.println(pbftView.getPbftView());

        // get getPendingTxSize
        PendingTxSize pendingTxSize = client.getPendingTxSize();
        System.out.println(pendingTxSize.getPendingTxSize());

        // get getSystemConfigByKey
        SystemConfig tx_count_limit = client.getSystemConfigByKey("tx_count_limit");
        System.out.println(tx_count_limit.getSystemConfig());

        // get getTotalTransactionCount
        TotalTransactionCount totalTransactionCount = client.getTotalTransactionCount();
        System.out.println(totalTransactionCount.getTotalTransactionCount());

        // get getPeers
        Peers peers = client.getPeers();
        System.out.println(peers.getPeers());

        // get NodeInfo
        BcosGroupInfo.GroupInfo groupInfo = client.getGroupInfo().getResult();
        System.out.println(groupInfo);

        // get getSyncStatus
        SyncStatus syncStatus = client.getSyncStatus();
        System.out.println(syncStatus.getSyncStatus().toString());
    }

    @Test
    public void testClientAsync() throws ConfigException, JniException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        // test getBlockByNumber only header
        String[] genesisHash = {null};

        client.getBlockByNumberAsync(
                BigInteger.ZERO,
                true,
                true,
                new RespCallback<BcosBlock>() {
                    @Override
                    public void onResponse(BcosBlock bcosBlock) {
                        System.out.println("getBlockByNumberAsync=" + bcosBlock.getBlock());
                        genesisHash[0] = bcosBlock.getBlock().getHash();
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        System.out.printf(
                                "getBlockByNumberAsync failed: {}",
                                errorResponse.getErrorMessage());
                    }
                });
        // test getBlockByNumber
        client.getBlockByNumberAsync(
                BigInteger.ZERO,
                false,
                false,
                new RespCallback<BcosBlock>() {
                    @Override
                    public void onResponse(BcosBlock bcosBlock) {
                        System.out.println("getBlockByNumberAsync=" + bcosBlock.getBlock());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        System.out.printf(
                                "getBlockByNumberAsync failed: {}",
                                errorResponse.getErrorMessage());
                    }
                });

        // getBlockByHash
        client.getBlockByHashAsync(
                genesisHash[0],
                true,
                true,
                new RespCallback<BcosBlock>() {
                    @Override
                    public void onResponse(BcosBlock bcosBlock) {
                        System.out.println("genesis block=" + bcosBlock.getBlock());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        System.out.printf(
                                "getBlockByHashAsync failed: {}", errorResponse.getErrorMessage());
                    }
                });

        // get SealerList
        SealerList sealerList = client.getSealerList();
        System.out.println(sealerList.getSealerList());

        // get observerList
        ObserverList observerList = client.getObserverList();
        System.out.println(observerList.getObserverList());

        // get pbftView
        PbftView pbftView = client.getPbftView();
        System.out.println(pbftView.getPbftView());

        // get getPendingTxSize
        PendingTxSize pendingTxSize = client.getPendingTxSize();
        System.out.println(pendingTxSize.getPendingTxSize());

        // get getSystemConfigByKey
        SystemConfig tx_count_limit = client.getSystemConfigByKey("tx_count_limit");
        System.out.println(tx_count_limit.getSystemConfig());

        // get getTotalTransactionCount
        TotalTransactionCount totalTransactionCount = client.getTotalTransactionCount();
        System.out.println(totalTransactionCount.getTotalTransactionCount());

        // get getPeers
        Peers peers = client.getPeers();
        System.out.println(peers.getPeers());

        // get NodeInfo
        //        NodeInfo NodeInfo = client.getNodeInfo();
        //        System.out.println(NodeInfo);

        // get getSyncStatus
        SyncStatus syncStatus = client.getSyncStatus();
        System.out.println(syncStatus.getSyncStatus());

        // test getBlockNumber
        CompletableFuture<BigInteger> future = new CompletableFuture<>();
        client.getBlockNumberAsync(
                new RespCallback<BlockNumber>() {
                    @Override
                    public void onResponse(BlockNumber blockNumber) {
                        System.out.println("getBlockNumberAsync=" + blockNumber.getBlockNumber());
                        future.complete(blockNumber.getBlockNumber());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        System.out.printf(
                                "getBlockNumberAsync failed: {}", errorResponse.getErrorMessage());
                        future.complete(BigInteger.valueOf(-1));
                    }
                });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHelloWorldInSolidity() throws ConfigException, JniException, ContractException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoSuite cryptoSuite = client.getCryptoSuite();
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        BigInteger blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);
        org.fisco.bcos.sdk.contract.solidity.HelloWorld helloWorld = null;
        helloWorld = org.fisco.bcos.sdk.contract.solidity.HelloWorld.deploy(client, keyPair);
        assert helloWorld != null;
        System.out.println("helloworld address :" + helloWorld.getContractAddress());
        BlockNumber blockNumber = client.getBlockNumber();
        BcosBlock block1 = client.getBlockByNumber(blockNumber.getBlockNumber(), false, false);
        System.out.println("block=" + block1.getBlock());
        String s = helloWorld.get();
        System.out.println("helloworld get :" + s);
        TransactionReceipt receipt = helloWorld.set("fisco hello");
        System.out.println("helloworld set : fisco hello, status=" + receipt.getStatus());
        System.out.println(receipt);
        // get 2nd block
        block1 =
                client.getBlockByNumber(
                        blockNumber.getBlockNumber().add(BigInteger.ONE), false, false);
        System.out.println("1st header=" + block1.getBlock());
        // getTransaction
        BcosTransaction transaction = client.getTransaction(receipt.getTransactionHash(), true);
        Assert.assertTrue(transaction.getTransaction() != null);
        System.out.println("getTransaction :" + transaction.getTransaction());
        // getTransactionReceipt
        BcosTransactionReceipt receipt1 =
                client.getTransactionReceipt(receipt.getTransactionHash(), true);
        Assert.assertTrue(receipt1.getTransactionReceipt() != null);
        System.out.println("getTransactionReceipt :" + receipt1.getTransactionReceipt());
        // getCode
        //            Code code = client.getCode(helloWorld.getContractAddress());
        //            Assert.assertNotNull(code.getResult());
        //            System.out.println("getCode :" + code.getCode());
        s = helloWorld.get();
        System.out.println("helloworld get :" + s);

        blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);
    }

    @Test
    public void testHelloWorldInLiquid() throws ConfigException, JniException, ContractException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoSuite cryptoSuite = client.getCryptoSuite();
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        BigInteger blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);
        org.fisco.bcos.sdk.contract.liquid.HelloWorld helloWorld = null;
        helloWorld =
                org.fisco.bcos.sdk.contract.liquid.HelloWorld.deploy(
                        client,
                        keyPair,
                        "/usr/bin/HelloWorld" + new Random().nextInt(1000),
                        "alice");

        System.out.println("helloworld address :" + helloWorld.getContractAddress());
        BlockNumber blockNumber = client.getBlockNumber();
        BcosBlock block1 = client.getBlockByNumber(blockNumber.getBlockNumber(), false, false);
        System.out.println("block=" + block1.getBlock());
        String s = helloWorld.get();
        System.out.println("helloworld get :" + s);
        TransactionReceipt receipt = helloWorld.set("fisco hello");
        System.out.println("helloworld set : fisco hello, status=" + receipt.getStatus());
        System.out.println(receipt);
        // get 2nd block
        block1 =
                client.getBlockByNumber(
                        blockNumber.getBlockNumber().add(BigInteger.ONE), false, false);
        System.out.println("1st header=" + block1.getBlock());
        // getTransaction
        BcosTransaction transaction = client.getTransaction(receipt.getTransactionHash(), true);
        Assert.assertTrue(transaction.getTransaction() != null);
        System.out.println("getTransaction :" + transaction.getTransaction());
        // getTransactionReceipt
        BcosTransactionReceipt receipt1 =
                client.getTransactionReceipt(receipt.getTransactionHash(), true);
        Assert.assertTrue(receipt1.getTransactionReceipt() != null);
        System.out.println("getTransactionReceipt :" + receipt1.getTransactionReceipt());
        // getCode
        //        Code code = client.getCode(helloWorld.getContractAddress());
        //        Assert.assertNotNull(code.getResult());
        //        System.out.println("getCode :" + code.getCode());
        s = helloWorld.get();
        System.out.println("helloworld get :" + s);

        blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);

        org.fisco.bcos.sdk.contract.liquid.HelloWorld2 helloWorld2 = null;

        // FIXME: this liquid call liquid will core dump
        helloWorld2 =
                org.fisco.bcos.sdk.contract.liquid.HelloWorld2.deploy(
                        client,
                        keyPair,
                        "/usr/bin/HelloWorld2_" + new Random().nextInt(1000),
                        helloWorld.getContractAddress());
        String s2 = helloWorld2.get();
        System.out.println("helloworld2 get :" + s2);
        TransactionReceipt receipt2 = helloWorld2.set("fisco bye");
        System.out.println("helloworld2 set : fisco hello, status=" + receipt2.getStatus());
        System.out.println(receipt2);
        s2 = helloWorld2.get();
        System.out.println("helloworld2 get :" + s2);
        s2 = helloWorld.get();
        System.out.println("helloworld get :" + s2);
    }

    @Test
    public void testGetGroupList() throws ConfigException, JniException {
        ConfigOption configOption = Config.load(configFile);

        System.out.println("configOption: " + configOption);

        Client clientWithoutGroupId = Client.build(configOption);
        System.out.println("build clientWithoutGroupId");
        List<String> groupList = clientWithoutGroupId.getGroupList().getResult().getGroupList();
        System.out.println("getGroupList: " + groupList);

        BcosSDK bcosSDK = new BcosSDK(configOption);
        for(String groupId: groupList) {
            Client client = bcosSDK.getClient(groupId);
            System.out.println("build client, groupId: " + groupId);
            System.out.println("getBlockNumber, blk: " + client.getBlockNumber().getBlockNumber());
        }
    }
}
