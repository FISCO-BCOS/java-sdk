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
package org.fisco.bcos.sdk.crypto.keypair;

import com.webank.wedpr.crypto.CryptoResult;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.exceptions.KeyPairException;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.StringUtils;
import org.fisco.bcos.sdk.utils.exceptions.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CryptoKeyPair {
    protected static Logger logger = LoggerFactory.getLogger(CryptoKeyPair.class);
    public static final int ADDRESS_SIZE = 160;
    public static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;

    public static final int PUBLIC_KEY_SIZE = 64;
    public static final int PUBLIC_KEY_LENGTH_IN_HEX = PUBLIC_KEY_SIZE << 1;

    public static final String ECDSA_CURVE_NAME = "secp256k1";
    public static final String SM2_CURVE_NAME = "sm2p256v1";
    public static final String PEM_FILE_POSTFIX = ".pem";
    public static final String P12_FILE_POSTFIX = ".p12";
    public static final String GM_ACCOUNT_SUBDIR = "gm";
    public static final String ECDSA_ACCOUNT_SUBDIR = "ecdsa";

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

    public CryptoKeyPair() {}

    /**
     * init CryptoKeyPair from the keyPair
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
     * get CryptoKeyPair information from CryptoResult
     *
     * @param nativeResult
     */
    CryptoKeyPair(final CryptoResult nativeResult) {
        this.hexPrivateKey = nativeResult.privteKey;
        this.hexPublicKey = nativeResult.publicKey;
    }

    public void setConfig(ConfigOption config) {
        this.config = config;
    }

    public String getHexPrivateKey() {
        return hexPrivateKey;
    }

    public String getHexPublicKey() {
        return hexPublicKey;
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    /**
     * generate keyPair randomly
     *
     * @return the generated keyPair
     */
    public abstract CryptoKeyPair generateKeyPair();

    public abstract CryptoKeyPair createKeyPair(KeyPair keyPair);

    public CryptoKeyPair createKeyPair(BigInteger privateKeyValue) {
        PrivateKey privateKey = KeyTool.convertHexedStringToPrivateKey(privateKeyValue, curveName);
        PublicKey publicKey = KeyTool.getPublicKeyFromPrivateKey(privateKey);
        KeyPair keyPair = new KeyPair(publicKey, privateKey);
        return createKeyPair(keyPair);
    }

    public CryptoKeyPair createKeyPair(String hexPrivateKey) {
        PrivateKey privateKey = KeyTool.convertHexedStringToPrivateKey(hexPrivateKey, curveName);
        PublicKey publicKey = KeyTool.getPublicKeyFromPrivateKey(privateKey);
        KeyPair keyPair = new KeyPair(publicKey, privateKey);
        return createKeyPair(keyPair);
    }

    protected String getPublicKeyNoPrefix(String publicKeyStr) {
        String publicKeyNoPrefix = Numeric.cleanHexPrefix(publicKeyStr);
        // Hexadecimal public key length is less than 128, add 0 in front
        if (publicKeyNoPrefix.length() < PUBLIC_KEY_LENGTH_IN_HEX) {
            publicKeyNoPrefix =
                    StringUtils.zeros(PUBLIC_KEY_LENGTH_IN_HEX - publicKeyNoPrefix.length())
                            + publicKeyNoPrefix;
        }
        return publicKeyNoPrefix;
    }
    /**
     * get the address according to the public key
     *
     * @return the hexed address calculated from the publicKey
     */
    public String getAddress() {
        // Note: The generated publicKey is prefixed with 04, When calculate the address, need to
        // remove 04
        return getAddress(this.getHexPublicKey().substring(2));
    }
    /**
     * calculate the address according to the given public key
     *
     * @param publicKey the Hexed publicKey that need to calculate address
     * @return the account address
     */
    public String getAddress(String publicKey) {
        try {
            String publicKeyNoPrefix = getPublicKeyNoPrefix(publicKey);
            // calculate hash for the public key
            String publicKeyHash = Hex.toHexString(hashImpl.hash(Hex.decode(publicKeyNoPrefix)));
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
        byte[] hash = hashImpl.hash(publicKey);
        return Arrays.copyOfRange(hash, hash.length - 20, hash.length); // right most 160 bits
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
        keyStoreFileDir = keyStoreFileDir + File.separator + keyStoreSubDir + File.separator;
        return keyStoreFileDir + address + postFix;
    }
}
