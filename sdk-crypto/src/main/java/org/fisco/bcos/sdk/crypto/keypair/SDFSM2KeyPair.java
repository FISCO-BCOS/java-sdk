package org.fisco.bcos.sdk.crypto.keypair;

import com.webank.wedpr.crypto.hsm.sdf.AlgorithmType;
import com.webank.wedpr.crypto.hsm.sdf.SDFCrypto;
import com.webank.wedpr.crypto.hsm.sdf.SDFCryptoResult;
import java.math.BigInteger;
import java.security.KeyPair;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.SDFSM3Hash;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;

public class SDFSM2KeyPair extends CryptoKeyPair {
    public static Hash DefaultHashAlgorithm = new SDFSM3Hash();

    public SDFSM2KeyPair() {
        initSM2KeyPairObject();
        CryptoKeyPair keyPair = this.generateKeyPair();
        this.hexPrivateKey = keyPair.getHexPrivateKey();
        this.hexPublicKey = keyPair.getHexPublicKey();
        this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
    }

    public SDFSM2KeyPair(KeyPair javaKeyPair) {
        super(javaKeyPair);
        initSM2KeyPairObject();
    }

    protected SDFSM2KeyPair(SDFCryptoResult sm2keyPairInfo) {
        super(sm2keyPairInfo);
        initSM2KeyPairObject();
        this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
    }

    private void initSM2KeyPairObject() {
        this.keyStoreSubDir = GM_ACCOUNT_SUBDIR;
        this.hashImpl = new SDFSM3Hash();
        this.curveName = CryptoKeyPair.SM2_CURVE_NAME;
        this.signatureAlgorithm = SM_SIGNATURE_ALGORITHM;
    }

    @Override
    public CryptoKeyPair generateKeyPair() {
        SDFCrypto crypto = new SDFCrypto();
        return new SDFSM2KeyPair(crypto.KeyGen(AlgorithmType.SM2));
    }

    @Override
    public CryptoKeyPair createKeyPair(KeyPair keyPair) {
        return new SDFSM2KeyPair(keyPair);
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
