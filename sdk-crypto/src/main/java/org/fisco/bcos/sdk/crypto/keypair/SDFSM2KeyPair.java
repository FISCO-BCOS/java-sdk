package org.fisco.bcos.sdk.crypto.keypair;

import com.webank.blockchain.hsm.crypto.sdf.AlgorithmType;
import com.webank.blockchain.hsm.crypto.sdf.SDF;
import com.webank.blockchain.hsm.crypto.sdf.SDFCryptoResult;
import com.webank.wedpr.crypto.CryptoResult;
import java.math.BigInteger;
import java.security.KeyPair;
import org.fisco.bcos.sdk.crypto.exceptions.KeyPairException;
import org.fisco.bcos.sdk.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.SDFSM3Hash;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDFSM2KeyPair extends CryptoKeyPair {
    protected static Logger logger = LoggerFactory.getLogger(SDFSM2KeyPair.class);
    public static Hash DefaultHashAlgorithm = new SDFSM3Hash();
    public long keyIndex;
    public String password;
    public boolean isInternalKey = false;

    public SDFSM2KeyPair() {
        initSM2KeyPairObject();
        CryptoKeyPair keyPair = this.generateKeyPair();
        this.hexPrivateKey = keyPair.getHexPrivateKey();
        this.hexPublicKey = keyPair.getHexPublicKey();
        logger.info("*** this.hexPrivateKey " + keyPair.getHexPrivateKey());
        this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
    }

    public SDFSM2KeyPair(KeyPair javaKeyPair) {
        super(javaKeyPair);
        initSM2KeyPairObject();
    }

    protected SDFSM2KeyPair(CryptoResult result) {
        super(result);
        initSM2KeyPairObject();
        logger.info(
                "*** this.hexPrivateKey SDFSM2KeyPair(SDFCryptoResult sm2keyPairInfo) "
                        + this.hexPrivateKey);
        this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
    }

    protected SDFSM2KeyPair(long keyIndex, String password) {
        initSM2KeyPairObject();
        this.keyIndex = keyIndex;
        this.password = password;
        SDFCryptoResult pkResult = SDF.ExportInternalPublicKey(keyIndex, AlgorithmType.SM2);
        if (pkResult.getSdfErrorMessage() != null && !pkResult.getSdfErrorMessage().equals("")) {
            throw new KeyPairException(
                    "get sdf sm2 internal key public key failed:" + pkResult.getSdfErrorMessage());
        }
        this.hexPublicKey =
                Numeric.getHexKeyWithPrefix(
                        pkResult.getPublicKey(),
                        CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                        CryptoKeyPair.PUBLIC_KEY_LENGTH_IN_HEX);
        this.isInternalKey = true;
    }

    private void initSM2KeyPairObject() {
        this.keyStoreSubDir = GM_ACCOUNT_SUBDIR;
        this.hashImpl = new SDFSM3Hash();
        this.curveName = CryptoKeyPair.SM2_CURVE_NAME;
        this.signatureAlgorithm = SM_SIGNATURE_ALGORITHM;
    }

    @Override
    public CryptoKeyPair generateKeyPair() {
        SDFCryptoResult sdfResult = SDF.KeyGen(AlgorithmType.SM2);
        checkSDFCryptoResult(sdfResult);
        CryptoResult result = new CryptoResult();
        result.privateKey = sdfResult.getPrivateKey();
        result.publicKey =
                Numeric.getHexKeyWithPrefix(
                        sdfResult.getPublicKey(),
                        CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                        CryptoKeyPair.PUBLIC_KEY_LENGTH_IN_HEX);
        return new SDFSM2KeyPair(result);
    }

    public static void checkSDFCryptoResult(SDFCryptoResult result) {
        if (result.getSdfErrorMessage() != null && !result.getSdfErrorMessage().isEmpty()) {
            throw new SignatureException("Sign with sdf sm2 failed:" + result.getSdfErrorMessage());
        }
    }

    @Override
    public CryptoKeyPair createKeyPair(KeyPair keyPair) {
        return new SDFSM2KeyPair(keyPair);
    }

    public SDFSM2KeyPair createKeyPair(long keyIndex, String password) {
        return new SDFSM2KeyPair(keyIndex, password);
    }

    public boolean isInternalKey() {
        return isInternalKey;
    }

    public long getKeyIndex() {
        return keyIndex;
    }

    public String getPassword() {
        return password;
    }

    public static String getAddressByPublicKey(String publicKey) {
        return getAddress(publicKey, SDFSM2KeyPair.DefaultHashAlgorithm);
    }

    public static byte[] getAddressByPublicKey(byte[] publicKey) {
        return Hex.decode(
                Numeric.cleanHexPrefix(getAddressByPublicKey(Hex.toHexString(publicKey))));
    }

    public static byte[] getAddressByPublicKey(BigInteger publicKey) {
        byte[] publicKeyBytes = Numeric.toBytesPadded(publicKey, PUBLIC_KEY_SIZE);
        return getAddressByPublicKey(publicKeyBytes);
    }
}
