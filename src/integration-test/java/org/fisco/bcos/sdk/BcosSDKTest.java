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
import java.security.SecureRandom;
import org.fisco.bcos.sdk.channel.model.ChannelPrococolExceiption;
import org.fisco.bcos.sdk.channel.model.EnumNodeVersion;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransaction;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceiptsDecoder;
import org.fisco.bcos.sdk.client.protocol.response.BlockHash;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.ConsensusStatus;
import org.fisco.bcos.sdk.client.protocol.response.GroupList;
import org.fisco.bcos.sdk.client.protocol.response.Peers;
import org.fisco.bcos.sdk.client.protocol.response.PendingTransactions;
import org.fisco.bcos.sdk.client.protocol.response.PendingTxSize;
import org.fisco.bcos.sdk.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.contract.EvidenceVerify;
import org.fisco.bcos.sdk.contract.HelloWorld;
import org.fisco.bcos.sdk.contract.SM2EvidenceVerify;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.service.GroupManagerService;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.Numeric;
import org.junit.Assert;
import org.junit.Test;

public class BcosSDKTest {
    private static final String configFile =
            BcosSDKTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();

    @Test
    public void testClient() throws ConfigException {
        BcosSDK sdk = BcosSDK.build(configFile);
        // check groupList
        Assert.assertTrue(sdk.getChannel().getAvailablePeer().size() >= 1);
        // get the client
        Client client = sdk.getClient(Integer.valueOf(1));

        // test getBlockNumber
        BlockNumber blockNumber = client.getBlockNumber();

        // test getBlockByNumber
        BcosBlock block = client.getBlockByNumber(BigInteger.ZERO, false);
        Assert.assertEquals(BigInteger.ZERO, block.getBlock().getNumber());
        // the genesis block with zero transactions
        Assert.assertEquals(0, block.getBlock().getTransactions().size());
        // the genesis block with 0 sealer
        Assert.assertEquals(0, block.getBlock().getSealerList().size());
        Assert.assertEquals("0x0", block.getBlock().getSealer());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000000",
                block.getBlock().getParentHash());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000000",
                block.getBlock().getDbHash());
        Assert.assertEquals(
                "0x0000000000000000000000000000000000000000000000000000000000000000",
                block.getBlock().getStateRoot());

        // test getBlockByHash
        BcosBlock block2 = client.getBlockByHash(block.getBlock().getHash(), false);
        Assert.assertEquals(block2.getBlock().getHash(), block.getBlock().getHash());

        // get blockHash
        BlockHash blockHash = client.getBlockHashByNumber(BigInteger.ZERO);
        Assert.assertEquals(blockHash.getBlockHashByNumber(), block.getBlock().getHash());

        try {
            // Note: FISCO BCOS supported_version >= v2.6.0 has this RPC interface
            // get blockHeader
            BcosBlockHeader blockHeader =
                    client.getBlockHeaderByHash(blockHash.getBlockHashByNumber(), true);
            if (blockHeader.getError() == null) {
                Assert.assertEquals(BigInteger.ZERO, blockHeader.getBlockHeader().getNumber());
                Assert.assertEquals(
                        block.getBlock().getHash(), blockHeader.getBlockHeader().getHash());

                BcosBlockHeader blockHeader2 = client.getBlockHeaderByNumber(BigInteger.ZERO, true);
                Assert.assertEquals(blockHeader.getBlockHeader(), blockHeader2.getBlockHeader());
            }
        } catch (ClientException e) {
            System.out.println("getBlockHeaderByHash failed, error information: " + e.getMessage());
        }

        // get SealerList
        SealerList sealerList = client.getSealerList();
        Assert.assertTrue(sealerList.getSealerList().size() > 0);

        // get observerList
        client.getObserverList();

        // getPeers
        Peers peers = client.getPeers();
        Assert.assertTrue(peers.getPeers().get(0).getAgency() != null);

        // get NodeVersion
        NodeVersion nodeVersion = client.getNodeVersion();
        Assert.assertTrue(nodeVersion.getNodeVersion() != null);

        // getSystemConfig
        client.getSystemConfigByKey("tx_count_limit");
        client.getSystemConfigByKey("tx_gas_limit");

        // get groupPeers
        client.getGroupPeers();
        // get PendingTxSize
        PendingTxSize pendingTxSize = client.getPendingTxSize();
        Assert.assertEquals(BigInteger.valueOf(0), pendingTxSize.getPendingTxSize());

        // get pendingTransactions
        PendingTransactions pendingTransactions = client.getPendingTransaction();
        Assert.assertEquals(0, pendingTransactions.getPendingTransactions().size());

        // get pbftView
        client.getPbftView();

        // getSyncStatus
        BlockHash latestHash = client.getBlockHashByNumber(blockNumber.getBlockNumber());
        SyncStatus syncStatus = client.getSyncStatus();
        Assert.assertEquals("0", syncStatus.getSyncStatus().getTxPoolSize());
        Assert.assertEquals(
                latestHash.getBlockHashByNumber(),
                "0x" + syncStatus.getSyncStatus().getLatestHash());
        Assert.assertEquals(
                blockHash.getBlockHashByNumber(),
                "0x" + syncStatus.getSyncStatus().getGenesisHash());
        Assert.assertEquals(
                latestHash.getBlockHashByNumber(),
                "0x" + syncStatus.getSyncStatus().getKnownLatestHash());
        Assert.assertEquals(
                blockNumber.getBlockNumber(),
                new BigInteger(syncStatus.getSyncStatus().getKnownHighestNumber()));

        BigInteger blockLimit = client.getBlockLimit();
        Assert.assertEquals(
                blockNumber.getBlockNumber().add(GroupManagerService.BLOCK_LIMIT), blockLimit);

        // test getGroupList
        GroupList groupList = client.getGroupList();
        Assert.assertTrue(groupList.getGroupList().size() >= 1);
        // test getConsensusStatus
        ConsensusStatus consensusStatus = client.getConsensusStatus();
        Assert.assertTrue(consensusStatus.getConsensusStatus().getViewInfos().size() > 0);

        for (String sealer :
                consensusStatus.getConsensusStatus().getBaseConsensusInfo().getSealerList()) {
            Assert.assertTrue(sealerList.getResult().contains(sealer));
        }
        Assert.assertEquals(
                "true",
                consensusStatus.getConsensusStatus().getBaseConsensusInfo().getAllowFutureBlocks());
        Assert.assertEquals(
                "true",
                consensusStatus.getConsensusStatus().getBaseConsensusInfo().getOmitEmptyBlock());
        Assert.assertEquals(
                blockNumber.getBlockNumber(),
                new BigInteger(
                        consensusStatus
                                .getConsensusStatus()
                                .getBaseConsensusInfo()
                                .getHighestblockNumber()));
        Assert.assertEquals(
                latestHash.getBlockHashByNumber(),
                consensusStatus.getConsensusStatus().getBaseConsensusInfo().getHighestblockHash());

        try {
            // test calculateHash interface for the transaction response
            BcosTransaction transaction =
                    client.getTransactionByBlockNumberAndIndex(
                            blockNumber.getBlockNumber(), BigInteger.ZERO);
            if (transaction.getTransaction().get() != null) {
                System.out.println(
                        "### transactionHash:" + transaction.getTransaction().get().getHash());
                System.out.println(
                        "### calculated transactionHash:"
                                + transaction
                                        .getTransaction()
                                        .get()
                                        .calculateHash(client.getCryptoSuite()));
                Assert.assertEquals(
                        transaction.getTransaction().get().calculateHash(client.getCryptoSuite()),
                        transaction.getTransaction().get().getHash());
            }

            // test calculateHash interface for the getBlockHeader response
            BcosBlockHeader blockHeader =
                    client.getBlockHeaderByNumber(blockNumber.getBlockNumber(), true);
            String calculatedHash =
                    blockHeader.getBlockHeader().calculateHash(client.getCryptoSuite());
            System.out.println("### blockHeader calculatedHash : " + calculatedHash);
            System.out.println(
                    "### blockHeader expectedHash: " + blockHeader.getBlockHeader().getHash());
            Assert.assertEquals(blockHeader.getBlockHeader().getHash(), calculatedHash);

            // test calculateHash interface for the block response
            block = client.getBlockByNumber(blockNumber.getBlockNumber(), false);
            calculatedHash = block.getBlock().calculateHash(client.getCryptoSuite());
            Assert.assertEquals(block.getBlock().getHash(), calculatedHash);
            testSendTransactions();
        } catch (ClientException | ContractException e) {
            System.out.println("testClient exception, error info: " + e.getMessage());
        }
    }

    private void checkReceipt(
            HelloWorld helloWorld,
            Client client,
            BigInteger expectedBlockNumber,
            TransactionReceipt receipt,
            boolean checkTo) {
        // check block number
        System.out.println("blockNumber: " + Numeric.decodeQuantity(receipt.getBlockNumber()));
        System.out.println("expected: " + expectedBlockNumber);
        Assert.assertTrue(
                Numeric.decodeQuantity(receipt.getBlockNumber()).compareTo(expectedBlockNumber)
                        >= 0);
        // check hash
        // Assert.assertTrue(receipt.getBlockHash().equals(client.getBlockHashByNumber(expectedBlockNumber).getBlockHashByNumber()));
        Assert.assertEquals(null, receipt.getReceiptProof());
        Assert.assertEquals(null, receipt.getTxProof());
        System.out.println(
                "getCurrentExternalAccountAddress: "
                        + helloWorld.getCurrentExternalAccountAddress()
                        + ", receipt.getFrom()"
                        + receipt.getFrom());
        Assert.assertEquals(helloWorld.getCurrentExternalAccountAddress(), receipt.getFrom());
        if (checkTo) {
            Assert.assertEquals(helloWorld.getContractAddress(), receipt.getTo());
        }
    }

    public void testSendTransactions() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Integer groupId = Integer.valueOf(1);
            Client client = sdk.getClient(groupId);
            BigInteger blockLimit = sdk.getGroupManagerService().getBlockLimitByGroup(groupId);
            BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
            // deploy the HelloWorld contract
            HelloWorld helloWorld =
                    HelloWorld.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
            checkReceipt(
                    helloWorld,
                    client,
                    blockNumber.add(BigInteger.ONE),
                    helloWorld.getDeployReceipt(),
                    false);

            // check the blockLimit has been modified
            // wait the block number notification
            Thread.sleep(1000);
            Assert.assertTrue(
                    sdk.getGroupManagerService()
                                    .getBlockLimitByGroup(groupId)
                                    .compareTo(blockLimit.add(BigInteger.ONE))
                            >= 0);
            Assert.assertTrue(helloWorld != null);
            Assert.assertTrue(helloWorld.getContractAddress() != null);

            // send transaction
            String settedString = "Hello, FISCO";
            TransactionReceipt receipt = helloWorld.set(settedString);
            Assert.assertTrue(receipt != null);
            checkReceipt(helloWorld, client, blockNumber.add(BigInteger.valueOf(2)), receipt, true);
            // wait the blocknumber notification
            Thread.sleep(1000);
            System.out.println(
                    sdk.getGroupManagerService().getBlockLimitByGroup(groupId)
                            + "  "
                            + blockLimit.add(BigInteger.valueOf(2)));
            Assert.assertTrue(
                    sdk.getGroupManagerService()
                                    .getBlockLimitByGroup(groupId)
                                    .compareTo(blockLimit.add(BigInteger.valueOf(2)))
                            >= 0);
            // get the modified value
            String getValue = helloWorld.get();
            Assert.assertTrue(getValue.equals(settedString));

            // load contract from the contract address
            HelloWorld helloWorld2 =
                    HelloWorld.load(
                            helloWorld.getContractAddress(),
                            client,
                            client.getCryptoSuite().getCryptoKeyPair());
            Assert.assertTrue(
                    helloWorld2.getContractAddress().equals(helloWorld.getContractAddress()));
            settedString = "Hello, Fisco2";
            TransactionReceipt receipt2 = helloWorld2.set(settedString);
            checkReceipt(
                    helloWorld2, client, blockNumber.add(BigInteger.valueOf(3)), receipt2, true);
            Assert.assertTrue(helloWorld.get().equals(settedString));
            Assert.assertTrue(helloWorld2.get().equals(settedString));

            // test getBatchReceiptsByBlockHashAndRange(added after v2.7.0)
            BcosTransactionReceiptsDecoder bcosTransactionReceiptsDecoder =
                    client.getBatchReceiptsByBlockHashAndRange(
                            client.getBlockHashByNumber(client.getBlockNumber().getBlockNumber())
                                    .getBlockHashByNumber(),
                            "0",
                            "-1");
            System.out.println(
                    "### bcosTransactionReceiptsDecoder1: "
                            + bcosTransactionReceiptsDecoder
                                    .decodeTransactionReceiptsInfo()
                                    .toString());
            // test getBatchReceiptsByBlockNumberAndRange
            bcosTransactionReceiptsDecoder =
                    client.getBatchReceiptsByBlockNumberAndRange(
                            client.getBlockNumber().getBlockNumber(), "0", "-1");
            System.out.println(
                    "### bcosTransactionReceiptsDecoder2: "
                            + bcosTransactionReceiptsDecoder
                                    .decodeTransactionReceiptsInfo()
                                    .toString());
            testECRecover();
            testSM2Recover();
        } catch (ContractException
                | ClientException
                | InterruptedException
                | ChannelPrococolExceiption e) {
            System.out.println("testSendTransactions exceptioned, error info:" + e.getMessage());
        }
    }

    public void testECRecover() throws ContractException {
        System.out.println("### testECRecover");
        BcosSDK sdk = BcosSDK.build(configFile);
        Integer groupId = Integer.valueOf(1);
        Client client = sdk.getClient(groupId);
        // test EvidenceVerify(ecRecover)
        EvidenceVerify evidenceVerify =
                EvidenceVerify.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
        System.out.println("### address of evidenceVerify:" + evidenceVerify.getContractAddress());
        CryptoSuite ecdsaCryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        String evi = "test";
        String evInfo = "test_info";
        int random = new SecureRandom().nextInt(50000);
        String eviId = String.valueOf(random);
        // sign to evi
        byte[] message = ecdsaCryptoSuite.hash(evi.getBytes());
        CryptoKeyPair cryptoKeyPair = ecdsaCryptoSuite.createKeyPair();
        // sign with secp256k1
        ECDSASignatureResult signatureResult =
                (ECDSASignatureResult) ecdsaCryptoSuite.sign(message, cryptoKeyPair);
        String signAddr = cryptoKeyPair.getAddress();
        TransactionReceipt insertReceipt =
                evidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        message,
                        BigInteger.valueOf(signatureResult.getV() + 27),
                        signatureResult.getR(),
                        signatureResult.getS());
        Assert.assertEquals(insertReceipt.getStatus(), "0x0");
        // case wrong signature
        insertReceipt =
                evidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        message,
                        BigInteger.valueOf(signatureResult.getV()),
                        signatureResult.getR(),
                        signatureResult.getS());
        Assert.assertEquals(insertReceipt.getStatus(), "0x16");
        // case wrong message
        byte[] fakedMessage = ecdsaCryptoSuite.hash(evInfo.getBytes());
        insertReceipt =
                evidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        fakedMessage,
                        BigInteger.valueOf(signatureResult.getV() + 27),
                        signatureResult.getR(),
                        signatureResult.getS());
        Assert.assertEquals(insertReceipt.getStatus(), "0x16");
        // case wrong sender
        signAddr = ecdsaCryptoSuite.createKeyPair().getAddress();
        insertReceipt =
                evidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        message,
                        BigInteger.valueOf(signatureResult.getV() + 27),
                        signatureResult.getR(),
                        signatureResult.getS());

        Assert.assertEquals(insertReceipt.getStatus(), "0x16");
    }

    public void testSM2Recover() throws ContractException, ChannelPrococolExceiption {
        System.out.println("### testSM2Recover");
        BcosSDK sdk = BcosSDK.build(configFile);
        Integer groupId = Integer.valueOf(1);
        Client client = sdk.getClient(groupId);
        // test SM2EvidenceVerify(sm2Verify)
        SM2EvidenceVerify sm2EvidenceVerify =
                SM2EvidenceVerify.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
        System.out.println(
                "### address of sm2EvidenceVerify:" + sm2EvidenceVerify.getContractAddress());
        CryptoSuite smCryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        String evi = "test";
        String evInfo = "test_info";
        int random = new SecureRandom().nextInt(50000);
        String eviId = String.valueOf(random);
        // sign to evi
        byte[] message = smCryptoSuite.hash(evi.getBytes());
        CryptoKeyPair cryptoKeyPair = smCryptoSuite.createKeyPair();
        // sign
        SM2SignatureResult signatureResult =
                (SM2SignatureResult) smCryptoSuite.sign(message, cryptoKeyPair);
        String signAddr = cryptoKeyPair.getAddress();
        TransactionReceipt insertReceipt =
                sm2EvidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        message,
                        signatureResult.getPub(),
                        signatureResult.getR(),
                        signatureResult.getS());
        String currentVersion = client.getNodeVersion().getNodeVersion().getSupportedVersion();
        EnumNodeVersion.Version supportedVersion = EnumNodeVersion.getClassVersion(currentVersion);
        // support sm2Verify after v2.8.0
        if (supportedVersion.getMinor() < 8) {
            Assert.assertEquals(insertReceipt.getStatus(), "0x16");
            return;
        }
        Assert.assertEquals(insertReceipt.getStatus(), "0x0");
        // case wrong signature
        insertReceipt =
                sm2EvidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        message,
                        message,
                        signatureResult.getR(),
                        signatureResult.getS());
        Assert.assertEquals(insertReceipt.getStatus(), "0x16");
        // case wrong message
        byte[] fakedMessage = smCryptoSuite.hash(evInfo.getBytes());
        insertReceipt =
                sm2EvidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        fakedMessage,
                        signatureResult.getPub(),
                        signatureResult.getR(),
                        signatureResult.getS());
        Assert.assertEquals(insertReceipt.getStatus(), "0x16");
        // case wrong sender
        signAddr = smCryptoSuite.createKeyPair().getAddress();
        insertReceipt =
                sm2EvidenceVerify.insertEvidence(
                        evi,
                        evInfo,
                        eviId,
                        signAddr,
                        message,
                        signatureResult.getPub(),
                        signatureResult.getR(),
                        signatureResult.getS());
        Assert.assertEquals(insertReceipt.getStatus(), "0x16");
    }
}
