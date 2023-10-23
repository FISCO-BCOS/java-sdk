package org.fisco.bcos.sdk.v3.utils;

import java.nio.ByteBuffer;
import java.util.List;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;

public class MerkleCalculator {

    private MerkleCalculator() {}

    public static String calculateMerkleRoot(List<String> merkleProof, String hash, Hash hashImpl)
            throws Exception {
        if (merkleProof == null || merkleProof.size() == 1) {
            return hash;
        }
        String result = hash;
        for (int beginFlag = 0; beginFlag < merkleProof.size(); ) {
            String nextLevelCount = merkleProof.get(beginFlag);
            int count = hashToNumber(nextLevelCount);
            beginFlag++;
            List<String> nextLevelProof = merkleProof.subList(beginFlag, beginFlag + count);
            if (!nextLevelProof.contains(result)) {
                throw new Exception("CalculateMerkleRoot failed, proof or hash mismatch.");
            }
            // hex combine
            String collect = String.join("", nextLevelProof);
            byte[] hashResult = hashImpl.hash(Hex.decode(collect));
            result = Hex.toHexString(hashResult);
            beginFlag += count;
        }
        return result;
    }

    private static int hashToNumber(String nextLevelCount) {
        byte[] decode = Hex.decode(nextLevelCount);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decode);
        return byteBuffer.getInt();
    }
}
