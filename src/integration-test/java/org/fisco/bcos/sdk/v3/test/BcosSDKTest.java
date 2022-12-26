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

package org.fisco.bcos.sdk.v3.test;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.BcosSDKException;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.RespCallback;
import org.fisco.bcos.sdk.v3.client.protocol.response.Abi;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.client.protocol.response.Code;
import org.fisco.bcos.sdk.v3.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.v3.client.protocol.response.PbftView;
import org.fisco.bcos.sdk.v3.client.protocol.response.Peers;
import org.fisco.bcos.sdk.v3.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.contract.solidity.HelloWorld;
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
    public void testClient() {

        BcosSDK sdk = BcosSDK.build(configFile);

        Client client = sdk.getClient();
        Assert.assertThrows(BcosSDKException.class,
                () -> sdk.getClient("errorClient"));

        sdk.registerBlockNotifier(GROUP,
                (groupId, blockNumber) ->
                        System.out.println("New block, group: " + groupId + ", blockNumber: " + blockNumber));

        // test getBlockNumber
        BlockNumber blockNumber = client.getBlockNumber();
        System.out.println("blockNumber=" + blockNumber.getBlockNumber());

        BlockHash blockHashByNumber = client.getBlockHashByNumber(blockNumber.getBlockNumber());
        System.out.println("blockHash=" + blockHashByNumber.getBlockHashByNumber());

        // test getBlockByNumber only header
        BcosBlock onlyHeader = client.getBlockByNumber(blockNumber.getBlockNumber(), false, false);
        System.out.println("block header=" + onlyHeader.getBlock());
        Assert.assertEquals(onlyHeader.getBlock().getHash(), blockHashByNumber.getBlockHashByNumber());

        // test getBlockByNumber
        BcosBlock block = client.getBlockByNumber(blockNumber.getBlockNumber(), false, false);
        System.out.println("block block=" + block.getBlock());
        // getBlockByHash

        BcosBlock block0 = client.getBlockByHash(block.getBlock().getHash(), false, false);
        System.out.println("block block=" + block0.getBlock());
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
        SystemConfig txCountLimit = client.getSystemConfigByKey("tx_count_limit");
        System.out.println(txCountLimit.getSystemConfig());

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
    public void testClientAsync()
            throws ConfigException, ExecutionException, InterruptedException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        // test getBlockByNumber only header
        String[] genesisHash = {null};

        CompletableFuture<String> hashFuture = new CompletableFuture<>();
        client.getBlockByNumberAsync(
                BigInteger.ZERO,
                true,
                true,
                new RespCallback<BcosBlock>() {
                    @Override
                    public void onResponse(BcosBlock bcosBlock) {
                        System.out.println("getBlockByNumberAsync=" + bcosBlock.getBlock());
                        hashFuture.complete(bcosBlock.getBlock().getHash());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        System.out.println(
                                "getBlockByNumberAsync failed: " +
                                        errorResponse.getErrorMessage());
                    }
                });
        genesisHash[0] = hashFuture.get();

        // test getBlockByNumber
        client.getBlockByNumberAsync(
                BigInteger.ZERO,
                false,
                false,
                new RespCallback<BcosBlock>() {
                    @Override
                    public void onResponse(BcosBlock bcosBlock) {
                        System.out.println("getBlockByNumberAsync=" + bcosBlock.getBlock());
                        client.getBlockHashByNumberAsync(BigInteger.valueOf(bcosBlock.getBlock().getNumber()), new RespCallback<BlockHash>() {
                            @Override
                            public void onResponse(BlockHash blockHash) {
                                System.out.println("getBlockHashByNumberAsync=" + blockHash.getBlockHashByNumber());
                            }

                            @Override
                            public void onError(Response errorResponse) {
                                System.out.println(errorResponse);
                            }
                        });
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        System.out.println("getBlockByNumberAsync failed: " + errorResponse.getErrorMessage());
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
                        System.out.println(
                                "getBlockByHashAsync failed: " + errorResponse.getErrorMessage());
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

        // get getSyncStatus
        SyncStatus syncStatus = client.getSyncStatus();
        System.out.println(syncStatus.getSyncStatus());

        client.getTotalTransactionCountAsync(new RespCallback<TotalTransactionCount>() {
            @Override
            public void onResponse(TotalTransactionCount totalTransactionCount) {
                System.out.println("totalTransactionCount=" + totalTransactionCount.getTotalTransactionCount());
            }

            @Override
            public void onError(Response errorResponse) {
                System.out.println(errorResponse);
            }
        });

        // test getBlockNumber
        client.getBlockNumberAsync(
                new RespCallback<BlockNumber>() {
                    @Override
                    public void onResponse(BlockNumber blockNumber) {
                        System.out.println("getBlockNumberAsync=" + blockNumber.getBlockNumber());
                    }

                    @Override
                    public void onError(Response errorResponse) {
                        System.out.printf(
                                "getBlockNumberAsync failed: " + errorResponse.getErrorMessage());
                    }
                });
    }

    @Test
    public void testHelloWorldInSolidity() throws ConfigException, JniException, ContractException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        String extraData = "HelloWorld ExtraData";
        client.setExtraData(extraData);

        Assert.assertTrue(extraData.equals(client.getExtraData()));

        // CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        CryptoSuite cryptoSuite = client.getCryptoSuite();

        CryptoKeyPair cryptoKeyPair = cryptoSuite.getCryptoKeyPair();

        System.out.println("account address:" + cryptoKeyPair.getAddress());

        BigInteger blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);
        HelloWorld helloWorld = null;
        helloWorld = HelloWorld.deploy(client, cryptoKeyPair);
        System.out.println("helloworld address :" + helloWorld.getContractAddress());

        client.getABIAsync(helloWorld.getContractAddress(), new RespCallback<Abi>() {
            @Override
            public void onResponse(Abi abi) {
                Assert.assertEquals(HelloWorld.getABI(), abi.getABI());
            }

            @Override
            public void onError(Response errorResponse) {
                System.out.println(errorResponse);
            }
        });

        BlockNumber blockNumber = client.getBlockNumber();
        BcosBlock block1 = client.getBlockByNumber(blockNumber.getBlockNumber(), false, false);
        System.out.println("block=" + block1.getBlock());
        String s = helloWorld.get();
        System.out.println("helloworld get :" + s);
        TransactionReceipt receipt = helloWorld.set("fisco hello");
        System.out.println("helloworld set : fisco hello, status=" + receipt.getStatus());
        System.out.println(receipt);
        Assert.assertTrue(receipt.isStatusOK());

        String txHash = receipt.getTransactionHash();
        BcosTransaction transaction1 = client.getTransaction(txHash, false);
        Assert.assertEquals(extraData, transaction1.getResult().getExtraData());
        Assert.assertTrue(extraData.equals(transaction1.getResult().getExtraData()));

        BcosTransactionReceipt transactionReceipt = client.getTransactionReceipt(txHash, false);
        Assert.assertTrue(extraData.equals(transactionReceipt.getResult().getExtraData()));

        // get 2nd block
        block1 =
                client.getBlockByNumber(
                        blockNumber.getBlockNumber().add(BigInteger.ONE), false, false);
        System.out.println("1st header=" + block1.getBlock());
        // getTransaction
        BcosTransaction transaction = client.getTransaction(receipt.getTransactionHash(), true);
        Assert.assertTrue(transaction.getTransaction() != null);
        // getTransactionReceipt
        BcosTransactionReceipt receipt1 =
                client.getTransactionReceipt(receipt.getTransactionHash(), true);
        Assert.assertTrue(receipt1.getTransactionReceipt() != null);
        // getCode
        Code code = client.getCode(helloWorld.getContractAddress());
        Assert.assertNotNull(code.getResult());

        // getABI
        Abi abi = client.getABI(helloWorld.getContractAddress());
        Assert.assertNotNull(abi.getABI());
        System.out.println(abi.getABI());
        s = helloWorld.get();
        System.out.println("helloworld get :" + s);

        blockLimit = client.getBlockLimit();
        System.out.println("blockLimit:" + blockLimit);
    }

    @Test
    public void testTransactionAssemble() throws ConfigException, JniException, ContractException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        HelloWorld helloWorld =
                HelloWorld.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
        TransactionReceipt receipt = helloWorld.set("fisco hello");
        Assert.assertEquals(receipt.getStatus(), 0);
        String input = receipt.getInput();

        long transactionData =
                TransactionBuilderJniObj.createTransactionData(
                        GROUP,
                        client.getChainId(),
                        helloWorld.getContractAddress(),
                        input,
                        "",
                        client.getBlockLimit().longValue());

        String transactionDataHash =
                TransactionBuilderJniObj.calcTransactionDataHash(
                        client.getCryptoType(), transactionData);

        SignatureResult sign =
                client.getCryptoSuite()
                        .sign(transactionDataHash, client.getCryptoSuite().getCryptoKeyPair());

        String transactionDataHashSignedData2 = Hex.toHexString(sign.encode());
        String extraData = "extraData";
        String signedMessage =
                TransactionBuilderJniObj.createSignedTransaction(
                        transactionData, transactionDataHashSignedData2, transactionDataHash, 0, extraData);

        TransactionPusherService txPusher = new TransactionPusherService(client);
        TransactionReceipt receipt2 = txPusher.push(signedMessage);

        TransactionDecoderService txDecoder =
                new TransactionDecoderService(client.getCryptoSuite(), client.isWASM());
        String receiptMsg = txDecoder.decodeReceiptStatus(receipt2).getReceiptMessages();
        receipt2.setMessage(receiptMsg);

        System.out.println(receipt2);
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
        for (String groupId : groupList) {
            Client client = bcosSDK.getClient(groupId);
            System.out.println("build client, groupId: " + groupId);
            System.out.println("getBlockNumber, blk: " + client.getBlockNumber().getBlockNumber());
        }
    }
}
