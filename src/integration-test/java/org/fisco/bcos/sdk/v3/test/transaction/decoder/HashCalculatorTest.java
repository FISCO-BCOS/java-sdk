package org.fisco.bcos.sdk.v3.test.transaction.decoder;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.contract.solidity.EventSubDemo;
import org.fisco.bcos.sdk.v3.test.contract.solidity.Incremental;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.MerkleProofUtility;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import static org.fisco.bcos.sdk.v3.utils.Numeric.toBytesPadded;

public class HashCalculatorTest {
    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private final Client client;
    private BcosBlock.Block block;
    private JsonTransactionResponse transactionResponse = null;
    private JsonTransactionResponse transactionResponseV1 = null;
    private JsonTransactionResponse transactionResponseV2 = null;

    private TransactionReceipt transactionReceipt = null;
    private TransactionReceipt transactionReceiptV1 = null;
    private TransactionReceipt transactionReceiptV2 = null;

    public HashCalculatorTest() throws ContractException {
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        client = sdk.getClient("group0");
        if (client.isSupportTransactionV1()) {
            Incremental incremental =
                    Incremental.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
            String nonce = UUID.randomUUID().toString().replace("-", "");
            transactionReceiptV1 = incremental.buildMethodInc(nonce).setNonce(nonce).send();
            transactionResponseV1 =
                    client.getTransaction(transactionReceiptV1.getTransactionHash(), true)
                            .getResult();
        }

        if (client.isSupportTransactionV2()) {
            Incremental incremental =
                    Incremental.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
            String nonce = UUID.randomUUID().toString().replace("-", "");
            transactionReceiptV2 =
                    incremental
                            .buildMethodInc(nonce)
                            .setNonce(nonce)
                            .setExtension(nonce.getBytes())
                            .send();
            transactionResponseV2 =
                    client.getTransaction(transactionReceiptV2.getTransactionHash(), true)
                            .getResult();
        }

        EventSubDemo eventSubDemo =
                EventSubDemo.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
        transactionReceipt = eventSubDemo.echo(BigInteger.TEN, BigInteger.valueOf(1000), "Hello");
        transactionResponse =
                client.getTransaction(transactionReceipt.getTransactionHash(), true).getResult();
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
            client.stop();
            client.destroy();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTxHashCalculate() throws IOException {
        if (transactionResponse == null) {
            return;
        }
        block =
                client.getBlockByNumber(transactionReceipt.getBlockNumber(), false, false)
                        .getBlock();

        String version =
                client.getGroupInfo()
                        .getResult()
                        .getNodeList()
                        .get(0)
                        .getIniConfig()
                        .getBinaryInfo()
                        .getVersion();
        System.out.println("node bin version: " + version);
        if (EnumNodeVersion.getClassVersion(version)
                        .compareTo(EnumNodeVersion.BCOS_3_3_0.toVersionObj())
                >= 0) {
            System.out.println("use hex decode nonce");
            String calculateTxHashInNative =
                    transactionResponse.calculateTxHashInNative(client.getCryptoSuite().hashImpl);
            Assert.assertEquals(calculateTxHashInNative, transactionResponse.getHash());
            String hash1 =
                    transactionResponse.calculateHash(client.getCryptoSuite().cryptoTypeConfig);
            Assert.assertEquals(hash1, transactionResponse.getHash());
        } else {
            System.out.println("use string nonce");
            String jniHash = transactionResponse.calculateHash(client.getCryptoSuite());
            Assert.assertEquals(jniHash, transactionResponse.getHash());
        }

        if (client.getChainCompatibilityVersion()
                        .compareTo(EnumNodeVersion.BCOS_3_2_0.toVersionObj())
                < 0) {
            return;
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getTransactionsRoot(),
                            transactionResponse.getTxProof(),
                            transactionResponse.getHash(),
                            new Keccak256());
            Assert.assertTrue(verifyMerkle);
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.SM_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getTransactionsRoot(),
                            transactionResponse.getTxProof(),
                            transactionResponse.getHash(),
                            new SM3Hash());
            Assert.assertTrue(verifyMerkle);
        }
    }

    @Test
    public void testTxV1HashCalculate() throws IOException {
        if (transactionResponseV1 == null) {
            return;
        }
        block =
                client.getBlockByNumber(transactionReceiptV1.getBlockNumber(), false, false)
                        .getBlock();
        String version =
                client.getGroupInfo()
                        .getResult()
                        .getNodeList()
                        .get(0)
                        .getIniConfig()
                        .getBinaryInfo()
                        .getVersion();
        System.out.println("node bin version: " + version);

        String jniHash =
                transactionResponseV1.calculateHash(client.getCryptoSuite().cryptoTypeConfig);
        Assert.assertEquals(jniHash, transactionResponseV1.getHash());
        String nativeHash =
                transactionResponseV1.calculateTxHashInNative(client.getCryptoSuite().hashImpl);
        Assert.assertEquals(nativeHash, transactionResponseV1.getHash());

        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getTransactionsRoot(),
                            transactionResponseV1.getTxProof(),
                            transactionResponseV1.getHash(),
                            new Keccak256());
            Assert.assertTrue(verifyMerkle);
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.SM_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getTransactionsRoot(),
                            transactionResponseV1.getTxProof(),
                            transactionResponseV1.getHash(),
                            new SM3Hash());
            Assert.assertTrue(verifyMerkle);
        }
    }

    @Test
    public void testTxV2HashCalculate() throws IOException {
        if (transactionResponseV2 == null) {
            return;
        }
        block =
                client.getBlockByNumber(transactionReceiptV2.getBlockNumber(), false, false)
                        .getBlock();
        String version =
                client.getGroupInfo()
                        .getResult()
                        .getNodeList()
                        .get(0)
                        .getIniConfig()
                        .getBinaryInfo()
                        .getVersion();
        System.out.println("node bin version: " + version);
        String jniHash =
                transactionResponseV2.calculateHash(client.getCryptoSuite().cryptoTypeConfig);
        Assert.assertEquals(jniHash, transactionResponseV2.getHash());
        String nativeHash =
                transactionResponseV2.calculateTxHashInNative(client.getCryptoSuite().hashImpl);
        Assert.assertEquals(nativeHash, transactionResponseV2.getHash());

        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getTransactionsRoot(),
                            transactionResponseV2.getTxProof(),
                            transactionResponseV2.getHash(),
                            new Keccak256());
            Assert.assertTrue(verifyMerkle);
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.SM_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getTransactionsRoot(),
                            transactionResponseV2.getTxProof(),
                            transactionResponseV2.getHash(),
                            new SM3Hash());
            Assert.assertTrue(verifyMerkle);
        }
    }

    @Test
    public void testBlock() throws IOException {

        if (block == null) {
            block =
                    client.getBlockByNumber(transactionReceipt.getBlockNumber(), false, false)
                            .getBlock();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // version
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(block.getVersion()), 4));
        // parentInfo
        for (BcosBlockHeader.ParentInfo parentInfo : block.getParentInfo()) {
            byteArrayOutputStream.write(
                    toBytesPadded(BigInteger.valueOf(parentInfo.getBlockNumber()), 8));
            byteArrayOutputStream.write(Hex.decode(parentInfo.getBlockHash()));
        }
        // tx root
        byteArrayOutputStream.write(Hex.decode(block.getTransactionsRoot()));
        // receipt root
        byteArrayOutputStream.write(Hex.decode(block.getReceiptsRoot()));
        // stateRoot
        byteArrayOutputStream.write(Hex.decode(block.getStateRoot()));
        // number
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(block.getNumber()), 8));
        // gasUsed
        byteArrayOutputStream.write(block.getGasUsed().getBytes());
        // time
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(block.getTimestamp()), 8));
        // sealer
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(block.getSealer()), 8));
        // sealer list
        for (String sealer : block.getSealerList()) {
            byteArrayOutputStream.write(Hex.decode(sealer));
        }
        // extraData
        byteArrayOutputStream.write(Hex.decode(block.getExtraData()));
        // consensusWeight
        for (Long consensusWeight : block.getConsensusWeights()) {
            byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(consensusWeight), 8));
        }

        String hash = block.getHash();
        String calculateHash = "";
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            calculateHash = Keccak256.calculateHash(byteArrayOutputStream.toByteArray());
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.SM_TYPE) {
            calculateHash = SM3Hash.calculateHash(byteArrayOutputStream.toByteArray());
        }
        Assert.assertEquals(Hex.addPrefix(calculateHash), hash);

        Assert.assertEquals(
                block.getHash(), block.calculateBlockHeaderHash(client.getCryptoSuite().hashImpl));
    }

    @Test
    public void testTransactionReceipt() throws IOException {
        checkTransactionReceipt(transactionReceipt);
    }

    @Test
    public void testTransactionReceiptV1() throws IOException {
        checkTransactionReceipt(transactionReceiptV1);
    }

    @Test
    public void testTransactionReceiptV2() throws IOException {
        checkTransactionReceipt(transactionReceiptV2);
    }

    private void checkTransactionReceipt(TransactionReceipt transactionReceipt) throws IOException {
        if (transactionReceipt == null) {
            return;
        }
        block =
                client.getBlockByNumber(transactionReceipt.getBlockNumber(), false, false)
                        .getBlock();
        String hash =
                transactionReceipt.calculateReceiptHashInNative(client.getCryptoSuite().hashImpl);
        Assert.assertEquals(hash, transactionReceipt.getReceiptHash());
        if (client.getChainCompatibilityVersion()
                        .compareTo(EnumNodeVersion.BCOS_3_2_0.toVersionObj())
                < 0) {
            return;
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getReceiptsRoot(),
                            transactionReceipt.getTxReceiptProof(),
                            transactionReceipt.getReceiptHash(),
                            new Keccak256());
            Assert.assertTrue(verifyMerkle);
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.SM_TYPE) {
            boolean verifyMerkle =
                    MerkleProofUtility.verifyMerkle(
                            block.getReceiptsRoot(),
                            transactionReceipt.getTxReceiptProof(),
                            transactionReceipt.getReceiptHash(),
                            new SM3Hash());
            Assert.assertTrue(verifyMerkle);
        }
    }
}
