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

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.contract.HelloWorld;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class BcosSDKTest {
    private static final String configFile =
            BcosSDKTest.class.getClassLoader().getResource(ConstantConfig.CONFIG_FILE_NAME).getPath();

    @Test
    public void testClient() throws ConfigException {
        BcosSDK sdk = BcosSDK.build(configFile);
        // get the client
        Client client = sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));

        // test getBlockNumber
        BlockNumber blockNumber = client.getBlockNumber();
        System.out.println("blockNumber=" + blockNumber.getBlockNumber());

        // test getBlockByNumber only header
        BcosBlock onlyHeader = client.getBlockByNumber(BigInteger.ZERO, true, false);
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
        NodeInfo.NodeInformation nodeInfo = client.getNodeInfo();
        System.out.println(nodeInfo);

        // get getSyncStatus
        SyncStatus syncStatus = client.getSyncStatus();
        System.out.println(syncStatus.getSyncStatus());
    }

    @Test
    public void testHelloWorld() {
        BcosSDK sdk = BcosSDK.build(configFile);
        // get the client
        Client client = sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        CryptoKeyPair keyPair = cryptoSuite.createKeyPair();
        HelloWorld helloWorld = null;
        try {
            helloWorld = HelloWorld.deploy(client, keyPair);
        } catch (ContractException e) {
            e.printStackTrace();
        }
        System.out.println("helloworld address :" + helloWorld.getContractAddress());
        try {
            String s = helloWorld.get();
            System.out.println("helloworld get :" + s);
            TransactionReceipt receipt = helloWorld.set("fisco hello");
            System.out.println("helloworld set :" + "fisco hello, status=" + receipt.getStatus());

            // getTransaction
            BcosTransaction transaction = client.getTransaction(receipt.getTransactionHash());
            System.out.println("getTransaction :" + transaction.getTransaction());
            // getTransactionReceipt
            BcosTransactionReceipt receipt1 = client.getTransactionReceipt(receipt.getTransactionHash());
            System.out.println("getTransactionReceipt :" + receipt1.getTransactionReceipt());
            // getCode
            Code code = client.getCode(helloWorld.getContractAddress());
            System.out.println("getCode :" + code.getCode());
            s = helloWorld.get();
            System.out.println("helloworld get :" + s);
        } catch (ContractException e) {
            e.printStackTrace();
        }
    }

}
