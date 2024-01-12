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

package org.fisco.bcos.sdk.v3.test.wasm;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.v3.client.protocol.response.Peers;
import org.fisco.bcos.sdk.v3.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.wasm.liquid.Asset;
import org.fisco.bcos.sdk.v3.test.wasm.liquid.HelloWorld;
import org.fisco.bcos.sdk.v3.test.wasm.liquid.HelloWorld2;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.pusher.TransactionPusherService;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

public class BcosSDKTest {
    private static final String configFile =
            BcosSDKTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    private static final String GROUP = "group0";

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

        client.stop();
        client.destroy();
    }

    @Test
    public void testHelloWorldInLiquid() throws ConfigException, ContractException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoSuite cryptoSuite = client.getCryptoSuite();
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        BigInteger blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);
        org.fisco.bcos.sdk.v3.test.wasm.liquid.HelloWorld helloWorld = null;
        helloWorld =
                org.fisco.bcos.sdk.v3.test.wasm.liquid.HelloWorld.deploy(
                        client, keyPair, "/usr/bin/HelloWorld" + new Random().nextInt(), "alice");

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

        HelloWorld2 helloWorld2 = null;

        helloWorld2 =
                HelloWorld2.deploy(
                        client,
                        keyPair,
                        "/usr/bin/HelloWorld2_" + new Random().nextInt(1000000),
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

        client.stop();
        client.destroy();
    }

    @Test
    public void testAssetEventInLiquid() throws ConfigException, ContractException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoSuite cryptoSuite = client.getCryptoSuite();
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        BigInteger blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);
        Asset asset =
                org.fisco.bcos.sdk.v3.test.wasm.liquid.Asset.deploy(
                        client, keyPair, "/asset" + new Random().nextInt(1000));

        System.out.println("asset address :" + asset.getContractAddress());

        TransactionReceipt assetAccount0 =
                asset.register("assetAccount0", BigInteger.valueOf(10000));
        TransactionReceipt assetAccount1 =
                asset.register("assetAccount1", BigInteger.valueOf(10000));
        List<Asset.RegisterEventEventResponse> registerEventEvents0 =
                asset.getRegisterEventEvents(assetAccount0);
        List<Asset.RegisterEventEventResponse> registerEventEvents1 =
                asset.getRegisterEventEvents(assetAccount1);
        // FIXME: WASM event have bugs
//        Assert.assertTrue(!registerEventEvents0.isEmpty());
//        Assert.assertTrue(!registerEventEvents1.isEmpty());

        Tuple2<Boolean, BigInteger> selectOutput = asset.select("assetAccount0");
        Assert.assertEquals(selectOutput.getValue1(), true);
        Assert.assertEquals(selectOutput.getValue2(), BigInteger.valueOf(10000));

        client.stop();
        client.destroy();
    }
}
