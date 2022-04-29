package org.fisco.bcos.sdk.crypto.signature;

import static org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair.PUBLIC_KEY_LENGTH_IN_HEX;
import static org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR;

import com.webank.blockchain.hsm.crypto.sdf.AlgorithmType;
import com.webank.blockchain.hsm.crypto.sdf.SDF;
import com.webank.blockchain.hsm.crypto.sdf.SDFCryptoResult;
import com.webank.wedpr.crypto.CryptoResult;
import com.webank.wedpr.crypto.NativeInterface;
import org.fisco.bcos.sdk.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SDFSM2KeyPair;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDFSM2Signature implements Signature {
    private static Logger logger = LoggerFactory.getLogger(SDFSM2Signature.class);

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
        CryptoResult hashResult =
                NativeInterface.sm2ComputeHashE(
                        Numeric.getHexKeyWithPrefix(
                                keyPair.getHexPublicKey(),
                                UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                                PUBLIC_KEY_LENGTH_IN_HEX),
                        Numeric.cleanHexPrefix(message));
        checkCryptoResult(hashResult);
        SDFCryptoResult signatureResult;
        if (keyPair instanceof SDFSM2KeyPair) {
            SDFSM2KeyPair sdfKeyPair = (SDFSM2KeyPair) keyPair;
            if (sdfKeyPair.isInternalKey()) {
                signatureResult =
                        SDF.SignWithInternalKey(
                                (sdfKeyPair.getKeyIndex() + 1) / 2,
                                sdfKeyPair.getPassword(),
                                AlgorithmType.SM2,
                                hashResult.hash);
                checkSDFCryptoResult(signatureResult);
                return signatureResult.getSignature();
            }
        }
        signatureResult =
                SDF.Sign(
                        Numeric.cleanHexPrefix(keyPair.getHexPrivateKey()),
                        AlgorithmType.SM2,
                        hashResult.hash);

        checkSDFCryptoResult(signatureResult);
        return signatureResult.getSignature();
    }

    public static void checkCryptoResult(CryptoResult result) {
        if (result.wedprErrorMessage != null && !result.wedprErrorMessage.isEmpty()) {
            throw new SignatureException("Sign with sdf sm2 failed:" + result.wedprErrorMessage);
        }
    }

    public static void checkSDFCryptoResult(SDFCryptoResult result) {
        if (result.getSdfErrorMessage() != null && !result.getSdfErrorMessage().isEmpty()) {
            throw new SignatureException("Sign with sdf sm2 failed:" + result.getSdfErrorMessage());
        }
    }

    @Override
    public boolean verify(String publicKey, String message, String signature) {
        return verifyMessage(publicKey, message, signature);
    }

    @Override
    public boolean verify(String publicKey, byte[] message, byte[] signature) {
        return verify(publicKey, Hex.toHexString(message), Hex.toHexString(signature));
    }

    public static boolean verifyMessage(String publicKey, String message, String signature) {
        CryptoResult hashResult =
                NativeInterface.sm2ComputeHashE(
                        Numeric.cleanHexPrefix(publicKey), Numeric.cleanHexPrefix(message));
        checkCryptoResult(hashResult);
        SDFCryptoResult verifyResult =
                SDF.Verify(
                        Numeric.getKeyNoPrefix(
                                UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                                publicKey,
                                PUBLIC_KEY_LENGTH_IN_HEX),
                        AlgorithmType.SM2,
                        hashResult.hash,
                        Numeric.cleanHexPrefix(signature));
        checkSDFCryptoResult(verifyResult);
        return verifyResult.getResult();
    }
}
