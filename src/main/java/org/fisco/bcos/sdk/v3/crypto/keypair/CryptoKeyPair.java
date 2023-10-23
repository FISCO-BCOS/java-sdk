/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.crypto.keypair;

import com.webank.wedpr.crypto.CryptoResult;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import org.fisco.bcos.sdk.jni.utilities.keypair.KeyPairJniObj;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.crypto.exceptions.KeyPairException;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.v3.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.v3.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.exceptions.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CryptoKeyPair {
    protected static Logger logger = LoggerFactory.getLogger(CryptoKeyPair.class);
    public static final int ADDRESS_SIZE = 160;
    public static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;

    public static final int PUBLIC_KEY_SIZE = 64;
    public static final int PUBLIC_KEY_LENGTH_IN_HEX = PUBLIC_KEY_SIZE << 1;

    public static final int PRIVATE_KEY_SIZE = 32;
    public static final int PRIVATE_KEY_SIZE_IN_HEX = PRIVATE_KEY_SIZE << 1;

    public static final String ECDSA_CURVE_NAME = "secp256k1";
    public static final String SM2_CURVE_NAME = "sm2p256v1";
    public static final String PEM_FILE_POSTFIX = ".pem";
    public static final String P12_FILE_POSTFIX = ".p12";
    public static final String GM_ACCOUNT_SUBDIR = "gm";
    public static final String ECDSA_ACCOUNT_SUBDIR = "ecdsa";
    public static final String UNCOMPRESSED_PUBLICKEY_FLAG_STR = "04";

    protected static final String ECDSA_SIGNATURE_ALGORITHM = "SHA256WITHECDSA";
    protected static final String SM_SIGNATURE_ALGORITHM = "1.2.156.10197.1.501";

    protected String hexPrivateKey;
    protected String hexPublicKey;
    public KeyPair keyPair;

    protected Hash hashImpl;
    // Curve name corresponding to the KeyPair
    protected String curveName;
    protected String keyStoreSubDir = "";

    protected ConfigOption config;
    // The path to save the account pem file corresponding to the CryptoKeyPair
    protected String pemKeyStoreFilePath = "";
    // The path to save the account p12 file
    protected String p12KeyStoreFilePath = "";
    protected String signatureAlgorithm;

    // for jni transaction sign
    protected long jniKeyPair = 0;

    public CryptoKeyPair() {}

    /**
     * Init CryptoKeyPair from the keyPair
     *
     * @param keyPair the original keyPair
     */
    public CryptoKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        // init privateKey/publicKey from the keyPair
        this.hexPrivateKey = KeyTool.getHexedPrivateKey(keyPair.getPrivate());
        this.hexPublicKey = KeyTool.getHexedPublicKey(keyPair.getPublic());
    }
    /**
     * Get CryptoKeyPair information from CryptoResult
     *
     * @param nativeResult
     */
    CryptoKeyPair(final CryptoResult nativeResult) {
        this.hexPrivateKey = nativeResult.privateKey;
        this.hexPublicKey = getPublicKeyNoPrefix(nativeResult.publicKey);
    }

    public long getJniKeyPair() {
        return jniKeyPair;
    }

    /**
     * Get the configuration
     *
     * @param config ConfigOption type config
     */
    public void setConfig(ConfigOption config) {
        this.config = config;
    }

    /**
     * Get the private key
     *
     * @return hex string private key
     */
    public String getHexPrivateKey() {
        return hexPrivateKey;
    }

    /**
     * Get the hex string public key
     *
     * @return the hex string public key
     */
    public String getHexPublicKey() {
        return hexPublicKey;
    }

    /**
     * Get key pair
     *
     * @return KeyPair
     */
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    /**
     * Abstract function of generate keyPair randomly
     *
     * @return the generated keyPair
     */
    public abstract CryptoKeyPair generateKeyPair();

    public String getCurveName() {
        return this.curveName;
    }

    /**
     * Abstract function of create keyPair randomly
     *
     * @param keyPair KeyPair type key pair
     * @return CryptoKeyPair type key pair
     */
    public abstract CryptoKeyPair createKeyPair(KeyPair keyPair);

    /**
     * Create key pair
     *
     * @param privateKeyValue BigInteger type private key
     * @return CryptoKeyPair
     */
    public CryptoKeyPair createKeyPair(BigInteger privateKeyValue) {
        KeyPair keyPair = KeyTool.convertPrivateKeyToKeyPair(privateKeyValue, curveName);
        return createKeyPair(keyPair);
    }

    /**
     * Create key pair
     *
     * @param hexPrivateKey hex string of integer private key
     * @return CryptoKeyPair
     */
    public CryptoKeyPair createKeyPair(String hexPrivateKey) {
        KeyPair keyPair = KeyTool.convertHexedStringToKeyPair(hexPrivateKey, curveName);
        return createKeyPair(keyPair);
    }

    protected static String getPublicKeyNoPrefix(String publicKeyStr) {
        return Numeric.getKeyNoPrefix(
                UNCOMPRESSED_PUBLICKEY_FLAG_STR, publicKeyStr, PUBLIC_KEY_LENGTH_IN_HEX);
    }
    /**
     * get the address according to the public key
     *
     * @return the hexed address calculated from the publicKey
     */
    public String getAddress() {
        // Note: The generated publicKey is prefixed with 04, When calculate the address, need to
        // remove 04
        return getAddress(this.getHexPublicKey());
    }

    public String getAddress(String publicKey) {
        return getAddress(publicKey, hashImpl);
    }

    /**
     * calculate the address according to the given public key
     *
     * @param publicKey the Hexed publicKey that need to calculate address
     * @param hashInterface the hash implement, support SM3Hash and Keccak256 now
     * @return the account address
     */
    protected static String getAddress(String publicKey, Hash hashInterface) {
        try {
            String publicKeyNoPrefix =
                    Numeric.getKeyNoPrefix(
                            UNCOMPRESSED_PUBLICKEY_FLAG_STR, publicKey, PUBLIC_KEY_LENGTH_IN_HEX);
            // calculate hash for the public key
            String publicKeyHash =
                    Hex.toHexString(hashInterface.hash(Hex.decode(publicKeyNoPrefix)));
            // right most 160 bits
            return "0x" + publicKeyHash.substring(publicKeyHash.length() - ADDRESS_LENGTH_IN_HEX);
        } catch (DecoderException e) {
            throw new KeyPairException(
                    "getAddress for "
                            + publicKey
                            + "failed, the publicKey param must be hex string, error message: "
                            + e.getMessage(),
                    e);
        }
    }

    public byte[] getAddress(byte[] publicKey) {
        return Hex.decode(Numeric.cleanHexPrefix(getAddress(Hex.toHexString(publicKey))));
    }

    public byte[] getAddress(BigInteger publicKey) {
        byte[] publicKeyBytes = Numeric.toBytesPadded(publicKey, PUBLIC_KEY_SIZE);
        return getAddress(publicKeyBytes);
    }

    public void storeKeyPairWithPem(String keyStoreFilePath) {
        PEMKeyStore.storeKeyPairWithPemFormat(this.hexPrivateKey, keyStoreFilePath, curveName);
    }

    public void storeKeyPairWithPemFormat() {
        String pemKeyStoreFilePath = getPemKeyStoreFilePath();
        File file = new File(pemKeyStoreFilePath);
        if (file.exists()) {
            return;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        logger.debug("store account {} to pem file: {}", getAddress(), pemKeyStoreFilePath);
        storeKeyPairWithPem(pemKeyStoreFilePath);
    }

    public void storeKeyPairWithP12(String p12FilePath, String password) {
        P12KeyStore.storeKeyPairWithP12Format(
                this.hexPrivateKey, password, p12FilePath, curveName, signatureAlgorithm);
    }

    public void storeKeyPairWithP12Format(String password) {
        String p12KeyStoreFilePath = getP12KeyStoreFilePath();
        File file = new File(p12KeyStoreFilePath);
        if (file.exists()) {
            return;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        logger.debug("store account {} to p12 file: {}", getAddress(), p12KeyStoreFilePath);
        storeKeyPairWithP12(p12KeyStoreFilePath, password);
    }

    public String getKeyStoreSubDir() {
        return this.keyStoreSubDir;
    }

    public String getPemKeyStoreFilePath() {
        if (!pemKeyStoreFilePath.equals("")) {
            return pemKeyStoreFilePath;
        }
        pemKeyStoreFilePath = getPemKeyStoreFilePath(getAddress());
        return pemKeyStoreFilePath;
    }

    public String getPemKeyStoreFilePath(String address) {
        return getKeyStoreFilePath(address, PEM_FILE_POSTFIX);
    }

    public String getP12KeyStoreFilePath(String address) {
        return getKeyStoreFilePath(address, P12_FILE_POSTFIX);
    }

    public String getP12KeyStoreFilePath() {
        if (!p12KeyStoreFilePath.equals("")) {
            return p12KeyStoreFilePath;
        }
        p12KeyStoreFilePath = getP12KeyStoreFilePath(getAddress());
        return p12KeyStoreFilePath;
    }

    protected String getKeyStoreFilePath(String address, String postFix) {
        String keyStoreFileDir = "account";
        if (config != null) {
            keyStoreFileDir = config.getAccountConfig().getKeyStoreDir();
        }
        keyStoreFileDir = keyStoreFileDir + "/" + keyStoreSubDir + "/";
        return keyStoreFileDir + address + postFix;
    }

    public void destroy() {
        releaseJni();
    }

    public void releaseJni() {
        if (this.jniKeyPair != 0) {
            KeyPairJniObj.destroyJniKeyPair(this.jniKeyPair);
            if (logger.isTraceEnabled()) {
                logger.trace("finalize, jni key pair: {}", this.jniKeyPair);
            }

            this.jniKeyPair = 0;
        }
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
            releaseJni();
        } catch (Exception e) {

        } catch (Throwable throwable) {
            // throwable.printStackTrace();
        }
    }
}
