package org.fisco.bcos.sdk.crypto.hash;

import com.webank.blockchain.hsm.crypto.sdf.AlgorithmType;
import com.webank.blockchain.hsm.crypto.sdf.SDF;
import com.webank.blockchain.hsm.crypto.sdf.SDFCryptoResult;
import org.fisco.bcos.sdk.crypto.exceptions.HashException;
import org.fisco.bcos.sdk.utils.Hex;

public class SDFSM3Hash implements Hash {
    @Override
    public String hash(String inputData) {
        return calculateHash(inputData.getBytes());
    }

    @Override
    public String hashBytes(byte[] inputBytes) {
        return calculateHash(inputBytes);
    }

    @Override
    public byte[] hash(byte[] inputBytes) {
        return Hex.decode(calculateHash(inputBytes));
    }

    public static String calculateHash(final byte[] inputBytes) {
        SDFCryptoResult hashResult = SDF.Hash(null, AlgorithmType.SM3, Hex.toHexString(inputBytes));
        if (hashResult.getSdfErrorMessage() != null && !hashResult.getSdfErrorMessage().isEmpty()) {
            throw new HashException(
                    "calculate hash with sdf sm3 failed, error message:"
                            + hashResult.getSdfErrorMessage());
        }
        return hashResult.getHash();
    }
}
