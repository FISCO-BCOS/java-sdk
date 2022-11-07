package org.fisco.bcos.sdk.v3.utils;

import java.util.List;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.MerkleProofUnit;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerkleProofUtility {
    private static final Logger logger = LoggerFactory.getLogger(MerkleProofUtility.class);

    private MerkleProofUtility() {}

    /**
     * Verify transaction merkle proof
     *
     * @param transactionRoot tx root
     * @param transWithProof tx with proof
     * @param cryptoSuite hash
     */
    public static boolean verifyTransaction(
            String transactionRoot,
            JsonTransactionResponse transWithProof,
            CryptoSuite cryptoSuite) {
        return verifyMerkle(
                transactionRoot,
                transWithProof.getTransactionProof(),
                transWithProof.getHash(),
                cryptoSuite);
    }

    /**
     * Verify transaction receipt merkle proof
     *
     * @param receiptRoot receipt root
     * @param receiptWithProof receipt
     * @param cryptoSuite hash
     */
    public static boolean verifyTransactionReceipt(
            String receiptRoot, TransactionReceipt receiptWithProof, CryptoSuite cryptoSuite) {

        return verifyMerkle(
                receiptRoot,
                receiptWithProof.getReceiptProof(),
                receiptWithProof.getReceiptHash(),
                cryptoSuite);
    }

    public static boolean verifyMerkle(
            String merkleRoot,
            List<MerkleProofUnit> merkleProof,
            String verifyHash,
            CryptoSuite cryptoSuite) {
        String proof = MerkleCalculator.calculateMerkleRoot(merkleProof, verifyHash, cryptoSuite);
        logger.debug(" verifyMerkle hash: {}, root: {}, proof: {}", verifyHash, merkleRoot, proof);

        return proof.equals(merkleRoot);
    }
}
