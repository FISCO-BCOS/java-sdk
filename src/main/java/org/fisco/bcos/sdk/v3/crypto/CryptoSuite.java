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
package org.fisco.bcos.sdk.v3.crypto;

import java.security.KeyPair;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.model.AccountConfig;
import org.fisco.bcos.sdk.v3.crypto.exceptions.LoadKeyStoreException;
import org.fisco.bcos.sdk.v3.crypto.exceptions.UnsupportedCryptoTypeException;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.HsmSM2KeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.v3.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.v3.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.v3.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignature;
import org.fisco.bcos.sdk.v3.crypto.signature.HsmSM2Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoSuite {

    private static final Logger logger = LoggerFactory.getLogger(CryptoSuite.class);

    public int cryptoTypeConfig;
    public Signature signatureImpl;
    public Hash hashImpl;
    private CryptoKeyPair keyPairFactory;
    private CryptoKeyPair cryptoKeyPair;
    private ConfigOption config;

    public CryptoSuite(int cryptoTypeConfig, CryptoKeyPair cryptoKeyPair) {
        this(cryptoTypeConfig);
        this.cryptoKeyPair = cryptoKeyPair;
    }

    public CryptoSuite(int cryptoTypeConfig, String hexedPrivateKey) {
        this(cryptoTypeConfig);
        this.cryptoKeyPair = this.keyPairFactory.createKeyPair(hexedPrivateKey);
    }

    /**
     * init CryptoSuite
     *
     * @param cryptoTypeConfig the crypto type, e.g. ECDSA_TYPE or SM_TYPE
     * @param configOption the configuration of account.
     */
    public CryptoSuite(int cryptoTypeConfig, ConfigOption configOption) {
        logger.info("init CryptoSuite, cryptoType: {}", cryptoTypeConfig);
        this.setConfig(configOption);
        this.initCryptoSuite(cryptoTypeConfig);
        // doesn't set the account name, generate the keyPair randomly
        if (!configOption.getAccountConfig().isAccountConfigured()) {
            this.generateRandomKeyPair();
            return;
        }
        this.loadAccount(configOption);
    }

    /**
     * Init the common crypto implementation according to the crypto type
     *
     * @param cryptoTypeConfig the crypto type config number
     */
    public CryptoSuite(int cryptoTypeConfig) {
        initCryptoSuite(cryptoTypeConfig);
    }

    public void initCryptoSuite(int cryptoTypeConfig) {
        this.cryptoTypeConfig = cryptoTypeConfig;
        if (this.cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            this.signatureImpl = new ECDSASignature();
            this.hashImpl = new Keccak256();
            this.keyPairFactory = new ECDSAKeyPair();

        } else if (this.cryptoTypeConfig == CryptoType.SM_TYPE) {
            this.signatureImpl = new SM2Signature();
            this.hashImpl = new SM3Hash();
            this.keyPairFactory = new SM2KeyPair();

        } else if(this.cryptoTypeConfig == CryptoType.HSM_TYPE) {
            HsmSM2Signature hsmSM2Signature = new HsmSM2Signature();
            hsmSM2Signature.setHsmLibPath(this.config.getCryptoMaterialConfig().getHsmLibPath());
            this.signatureImpl = hsmSM2Signature;
            this.hashImpl = new SM3Hash();
            this.keyPairFactory = new HsmSM2KeyPair(this.config.getCryptoMaterialConfig().getHsmLibPath());
        } else {
            throw new UnsupportedCryptoTypeException(
                    "only support "
                            + CryptoType.ECDSA_TYPE
                            + "/"
                            + CryptoType.SM_TYPE
                            + "/"
                            + CryptoType.HSM_TYPE
                            + " crypto type");
        }
        // create keyPair randomly
        this.generateRandomKeyPair();
    }

    /**
     * Load account from file
     *
     * @param accountFileFormat file format, e.g. p21, pem
     * @param accountFilePath file path
     * @param password password of the key file
     */
    public void loadAccount(String accountFileFormat, String accountFilePath, String password) {
        KeyTool keyTool = null;
        if (accountFileFormat.compareToIgnoreCase("p12") == 0) {
            keyTool = new P12KeyStore(accountFilePath, password);
        } else if (accountFileFormat.compareToIgnoreCase("pem") == 0) {
            keyTool = new PEMKeyStore(accountFilePath);
        } else {
            throw new LoadKeyStoreException(
                    "unsupported account file format : "
                            + accountFileFormat
                            + ", current supported are p12 and pem");
        }
        logger.debug("Load account from {}", accountFilePath);
        this.loadKeyPair(keyTool.getKeyPair());
    }

    /**
     * Load account from ConfigOption object
     *
     * @param configOption config loaded from config file
     */
    private void loadAccount(ConfigOption configOption) {
        AccountConfig accountConfig = configOption.getAccountConfig();
        String accountFilePath = accountConfig.getAccountFilePath();
        if (accountFilePath == null || accountFilePath.equals("")) {
            if (accountConfig.getAccountFileFormat().compareToIgnoreCase("p12") == 0) {
                accountFilePath =
                        this.keyPairFactory.getP12KeyStoreFilePath(
                                accountConfig.getAccountAddress());
            } else if (accountConfig.getAccountFileFormat().compareToIgnoreCase("pem") == 0) {
                accountFilePath =
                        this.keyPairFactory.getPemKeyStoreFilePath(
                                accountConfig.getAccountAddress());
            }
        }
        this.loadAccount(
                accountConfig.getAccountFileFormat(),
                accountFilePath,
                accountConfig.getAccountPassword());
    }

    /**
     * Set config
     *
     * @param config ConfigOption type configuration
     */
    public void setConfig(ConfigOption config) {
        this.config = config;
    }

    public int getCryptoTypeConfig() {
        return this.cryptoTypeConfig;
    }

    public Signature getSignatureImpl() {
        return this.signatureImpl;
    }

    /**
     * Get hash function, which is relate to the configured CryptoType
     *
     * @return the hash function
     */
    public Hash getHashImpl() {
        return this.hashImpl;
    }

    /**
     * Call hash function
     *
     * @param inputData string type input data
     * @return the hash digest of input data
     */
    public String hash(final String inputData) {
        return this.hashImpl.hash(inputData);
    }

    /**
     * Call hash function
     *
     * @param inputBytes byte array type input data
     * @return the hashed string
     */
    public byte[] hash(final byte[] inputBytes) {
        return this.hashImpl.hash(inputBytes);
    }

    /**
     * Do signature
     *
     * @param message byte array type input string, must be a digest
     * @param keyPair key pair used to do signature
     * @return the signature errorCode
     */
    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return this.signatureImpl.sign(message, keyPair);
    }

    /**
     * Do signature
     *
     * @param message string type input message, must be a digest
     * @param keyPair key pair used to do signature
     * @return the signature errorCode
     */
    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        return this.signatureImpl.sign(message, keyPair);
    }

    // for AMOP topic verify, generate signature

    /**
     * Do signature, used in AMOP private topic verification procedure
     *
     * @param keyTool the key
     * @param message the string type input message, must be a digest
     * @return the string type signature
     */
    public String sign(KeyTool keyTool, String message) {
        CryptoKeyPair cryptoKeyPair = this.keyPairFactory.createKeyPair(keyTool.getKeyPair());
        return this.signatureImpl.signWithStringSignature(message, cryptoKeyPair);
    }

    /**
     * Verify signature, used in AMOP private topic verification procedure
     *
     * @param keyTool the key
     * @param message the string type input message, must be a digest
     * @param signature the string type signature
     * @return the verify errorCode
     */
    public boolean verify(KeyTool keyTool, String message, String signature) {
        return this.verify(keyTool.getHexedPublicKey(), message, signature);
    }

    /**
     * Verify signature, used in AMOP private topic verification procedure
     *
     * @param keyTool the key
     * @param message the byte array type input message, must be a digest
     * @param signature the byte array type signature
     * @return the verify errorCode
     */
    public boolean verify(KeyTool keyTool, byte[] message, byte[] signature) {
        return this.verify(keyTool.getHexedPublicKey(), message, signature);
    }

    /**
     * Verify signature
     *
     * @param publicKey the string type public key
     * @param message the string type input message, must be a digest
     * @param signature the string type signature
     * @return the verify errorCode
     */
    public boolean verify(final String publicKey, final String message, final String signature) {
        return this.signatureImpl.verify(publicKey, message, signature);
    }

    /**
     * Verify signature
     *
     * @param publicKey the string type public key
     * @param message the byte array type input message, must be a digest
     * @param signature the byte array type signature
     * @return the verify errorCode
     */
    public boolean verify(final String publicKey, final byte[] message, final byte[] signature) {
        return this.signatureImpl.verify(publicKey, message, signature);
    }

    /**
     * Create key pair
     *
     * @return a generated key pair
     */
    public CryptoKeyPair generateRandomKeyPair() {
        this.cryptoKeyPair = this.keyPairFactory.generateKeyPair();
        this.cryptoKeyPair.setConfig(this.config);
        return this.cryptoKeyPair;
    }

    /**
     * Create CryptoKeyPair type key pair from KeyPair type key pair
     *
     * @param keyPair key pair
     * @return CryptoKeyPair type key pair
     */
    public CryptoKeyPair loadKeyPair(KeyPair keyPair) {
        this.cryptoKeyPair = this.keyPairFactory.createKeyPair(keyPair);
        this.cryptoKeyPair.setConfig(this.config);
        return this.cryptoKeyPair;
    }

    /**
     * Create key pair from a private key string
     *
     * @param hexedPrivateKey a hex string of private key
     * @return CryptoKeyPair type key pair
     */
    public CryptoKeyPair loadKeyPair(String hexedPrivateKey) {
        this.cryptoKeyPair = this.keyPairFactory.createKeyPair(hexedPrivateKey);
        this.cryptoKeyPair.setConfig(this.config);
        return this.cryptoKeyPair;
    }

    /**
     * Set the key pair in CryptoSuite
     *
     * @param cryptoKeyPair set the CryptoKeyPair object
     */
    public void setCryptoKeyPair(CryptoKeyPair cryptoKeyPair) {
        this.cryptoKeyPair = cryptoKeyPair;
        this.cryptoKeyPair.setConfig(this.config);
    }

    /**
     * Get the key pair of the CryptoSuite
     *
     * @return CrytoKeyPair type key pair
     */
    public CryptoKeyPair getCryptoKeyPair() {
        return this.cryptoKeyPair;
    }

    /**
     * Get configuration
     *
     * @return ConfigOption
     */
    public ConfigOption getConfig() {
        return this.config;
    }

    /**
     * Get key pair factory
     *
     * @return CryptoKeyPair
     */
    public CryptoKeyPair getKeyPairFactory() {
        return this.keyPairFactory;
    }

    public void destroy() {
        if (cryptoKeyPair != null) {
            cryptoKeyPair.destroy();
            cryptoKeyPair = null;
        }
    }
}
