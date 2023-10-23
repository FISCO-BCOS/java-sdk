package org.fisco.bcos.sdk.v3.utils;

import java.util.List;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerkleProofUtility {
    private static final Logger logger = LoggerFactory.getLogger(MerkleProofUtility.class);

    private MerkleProofUtility() {}

    public static boolean verifyMerkle(
            String merkleRoot, List<String> merkleProof, String verifyHash, Hash hashImpl) {
        try {
            String proof =
                    MerkleCalculator.calculateMerkleRoot(
                            merkleProof, Hex.trimPrefix(verifyHash), hashImpl);
            logger.debug(
                    " verifyMerkle hash: {}, root: {}, proof: {}", verifyHash, merkleRoot, proof);
            return proof.equals(Hex.trimPrefix(merkleRoot));
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean verifyMerkle(
            String merkleRoot,
            List<String> merkleProof,
            String verifyHash,
            CryptoSuite cryptoSuite) {
        return verifyMerkle(merkleRoot, merkleProof, verifyHash, cryptoSuite.getHashImpl());
    }
}
