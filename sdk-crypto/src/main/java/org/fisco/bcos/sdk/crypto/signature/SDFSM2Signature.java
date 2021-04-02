package org.fisco.bcos.sdk.crypto.signature;

import com.webank.wedpr.crypto.hsm.sdf.AlgorithmType;
import com.webank.wedpr.crypto.hsm.sdf.SDFCrypto;
import com.webank.wedpr.crypto.hsm.sdf.SDFCryptoResult;
import org.fisco.bcos.sdk.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;

public class SDFSM2Signature implements Signature {
    @Override
    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        return new SM2SignatureResult(
                keyPair.getHexPublicKey(), signWithStringSignature(message, keyPair));
    }

    @Override
    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return sign(Hex.toHexString(message), keyPair);
    }

    @Override
    public String signWithStringSignature(final String message, final CryptoKeyPair keyPair) {
        return signMessage(message, keyPair);
    }

    public String signMessage(String message, CryptoKeyPair keyPair) {
        SDFCrypto crypto = new SDFCrypto();
        SDFCryptoResult hashResult =
                crypto.HashWithZ(
                        null,
                        AlgorithmType.SM2,
                        Numeric.cleanHexPrefix(message),
                        (Numeric.cleanHexPrefix(message).length() + 1) / 2);
        if (hashResult.getSdfErrorMessage() != null && !hashResult.getSdfErrorMessage().isEmpty()) {
            throw new SignatureException(
                    "Sign with sdf sm2 failed:" + hashResult.getSdfErrorMessage());
        }
        SDFCryptoResult signatureResult =
                crypto.Sign(
                        keyPair.getHexPrivateKey(), AlgorithmType.SM2, hashResult.getHash(), 32);
        if (signatureResult.getSdfErrorMessage() != null
                && !signatureResult.getSdfErrorMessage().isEmpty()) {
            throw new SignatureException(
                    "Sign with sdf sm2 failed:" + signatureResult.getSdfErrorMessage());
        }
        return signatureResult.getSignature();
    }

    @Override
    public boolean verify(String publicKey, String message, String signature) {
        return false;
    }

    @Override
    public boolean verify(String publicKey, byte[] message, byte[] signature) {
        return false;
    }
}
