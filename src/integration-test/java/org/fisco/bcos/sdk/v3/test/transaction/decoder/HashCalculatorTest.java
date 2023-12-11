package org.fisco.bcos.sdk.v3.test.transaction.decoder;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosBlockHeader;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.MerkleProofUtility;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import static org.fisco.bcos.sdk.v3.utils.Numeric.toBytesPadded;

public class HashCalculatorTest {
    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private final Client client;
    private final BcosBlock.Block block;
    private JsonTransactionResponse transactionResponse = null;

    public HashCalculatorTest() {
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        client = sdk.getClient("group0");
        BlockNumber blockNumber = client.getBlockNumber();
        block = client.getBlockByNumber(blockNumber.getBlockNumber(), false, false).getBlock();
        if (!block.getTransactions().isEmpty()) {
            BcosBlock.TransactionObject transactionObject = (BcosBlock.TransactionObject) block.getTransactions().get(0);
            JsonTransactionResponse transactionResponse1 = transactionObject.get();
            transactionResponse = client.getTransaction(transactionResponse1.getHash(), true).getTransaction().get();
        }
    }

    @Test
    public void testTxHashCalculate() throws IOException {
        if (transactionResponse == null && block.getNumber() == 0) {
            return;
        }
        String chainId = transactionResponse.getChainID();
        String groupId = transactionResponse.getGroupID();
        long blockLimit = transactionResponse.getBlockLimit();
        String nonce = transactionResponse.getNonce();
        String contractAddress = transactionResponse.getTo();
        String encodedAbi = transactionResponse.getInput();
        String abi = transactionResponse.getAbi();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // version
        byteArrayOutputStream.write(toBytesPadded(BigInteger.ZERO, 4));
        // chainId
        byteArrayOutputStream.write(chainId.getBytes());
        // groupId
        byteArrayOutputStream.write(groupId.getBytes());
        // blockLimit
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(blockLimit), 8));
        // nonce
        String version = client.getGroupInfo().getResult().getNodeList().get(0).getIniConfig().getBinaryInfo().getVersion();
        System.out.println("node bin version: " + version);
        if (EnumNodeVersion.getClassVersion(version).compareTo(EnumNodeVersion.BCOS_3_3_0.toVersionObj()) >= 0) {
            System.out.println("use hex decode nonce");
            byteArrayOutputStream.write(Hex.decode(nonce));
        } else {
            System.out.println("use string nonce");
            byteArrayOutputStream.write(nonce.getBytes());
        }
        // to
        byteArrayOutputStream.write(contractAddress.getBytes());
        // input
        byteArrayOutputStream.write(Hex.decode(encodedAbi));
        // abi
        byteArrayOutputStream.write(abi.getBytes());

        String hash = "";
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            hash = Keccak256.calculateHash(byteArrayOutputStream.toByteArray());
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.SM_TYPE) {
            hash = SM3Hash.calculateHash((byteArrayOutputStream.toByteArray()));
        }
        hash = Hex.addPrefix(hash);
        Assert.assertEquals(hash, transactionResponse.getHash());
        if (EnumNodeVersion.getClassVersion(version).compareTo(EnumNodeVersion.BCOS_3_3_0.toVersionObj()) < 0) {
            String jniHash = transactionResponse.calculateHash(client.getCryptoSuite());
            Assert.assertEquals(jniHash, transactionResponse.getHash());
        }
    }

    @Test
    public void testBlock() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // version
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(block.getVersion()), 4));
        // parentInfo
        for (BcosBlockHeader.ParentInfo parentInfo : block.getParentInfo()) {
            byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(parentInfo.getBlockNumber()), 8));
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
    }

    @Test
    public void testTxProof() {
        if (transactionResponse == null && block.getNumber() == 0) {
            return;
        }
        if (client.getChainCompatibilityVersion().compareTo(EnumNodeVersion.BCOS_3_2_0.toVersionObj()) < 0) {
            return;
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            boolean verifyMerkle = MerkleProofUtility.verifyMerkle(block.getTransactionsRoot(), transactionResponse.getTxProof(), transactionResponse.getHash(), new Keccak256());
            Assert.assertTrue(verifyMerkle);
        }
        if (client.getCryptoSuite().cryptoTypeConfig == CryptoType.SM_TYPE) {
            boolean verifyMerkle = MerkleProofUtility.verifyMerkle(block.getTransactionsRoot(), transactionResponse.getTxProof(), transactionResponse.getHash(), new SM3Hash());
            Assert.assertTrue(verifyMerkle);
        }
    }
}
