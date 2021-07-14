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
package org.fisco.bcos.sdk.crypto;

import static org.fisco.bcos.sdk.model.CryptoProviderType.HSM;

import java.security.KeyPair;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.model.AccountConfig;
import org.fisco.bcos.sdk.crypto.exceptions.LoadKeyStoreException;
import org.fisco.bcos.sdk.crypto.exceptions.UnsupportedCryptoTypeException;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.crypto.hash.SDFSM3Hash;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SDFSM2KeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignature;
import org.fisco.bcos.sdk.crypto.signature.SDFSM2Signature;
import org.fisco.bcos.sdk.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.CryptoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoSuite {

    private static Logger logger = LoggerFactory.getLogger(CryptoSuite.class);

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
        this.config = configOption;
        int cryptoType = cryptoTypeConfig;
        if (cryptoTypeConfig == CryptoType.SM_TYPE) {
            if (configOption != null
                    && configOption.getCryptoMaterialConfig().getCryptoProvider() != null
                    && configOption
                            .getCryptoMaterialConfig()
                            .getCryptoProvider()
                            .equalsIgnoreCase(HSM)) {
                cryptoType = CryptoType.SM_HSM_TYPE;
            }
        }
        initCryptoSuite(cryptoType);
        // doesn't set the account name, generate the keyPair randomly
        if (configOption.getCryptoMaterialConfig().getCryptoProvider().equalsIgnoreCase(HSM)) {
            loadAccount(configOption);
        }
        if (configOption == null || !configOption.getAccountConfig().isAccountConfigured()) {
            createKeyPair();
            return;
        }
        loadAccount(configOption);
    }

    /**
     * Init the common crypto implementation according to the crypto type
     *
     * @param cryptoTypeConfig the crypto type config number
     */
    public CryptoSuite(int cryptoTypeConfig) {
        initCryptoSuite(cryptoTypeConfig);
    }

    protected void initCryptoSuite(int cryptoTypeConfig) {
        this.cryptoTypeConfig = cryptoTypeConfig;
        if (cryptoTypeConfig == CryptoType.SM_TYPE) {
            this.signatureImpl = new SM2Signature();
            this.hashImpl = new SM3Hash();
            this.keyPairFactory = new SM2KeyPair();
        } else if (cryptoTypeConfig == CryptoType.ECDSA_TYPE) {
            this.signatureImpl = new ECDSASignature();
            this.hashImpl = new Keccak256();
            this.keyPairFactory = new ECDSAKeyPair();
        } else if (cryptoTypeConfig == CryptoType.SM_HSM_TYPE) {
            logger.info("Use hsm crypto");
            this.signatureImpl = new SDFSM2Signature();
            this.hashImpl = new SDFSM3Hash();
            this.keyPairFactory = new SDFSM2KeyPair();
            this.cryptoTypeConfig = CryptoType.SM_TYPE;
        } else {
            throw new UnsupportedCryptoTypeException(
                    "only support "
                            + CryptoType.ECDSA_TYPE
                            + "/"
                            + CryptoType.SM_TYPE
                            + "/"
                            + CryptoType.SM_HSM_TYPE
                            + " crypto type");
        }
        // create keyPair randomly
        createKeyPair();
    }

    /** Load sdf internal account */
    public void loadSDFInternalAccount(String accountKeyIndex, String password) {
        logger.info("using hsm internal key, key index = " + accountKeyIndex);
        long index = Long.parseLong(accountKeyIndex);
        SDFSM2KeyPair factory = (SDFSM2KeyPair) keyPairFactory;
        SDFSM2KeyPair keyPair = factory.createKeyPair(index, password);
        setCryptoKeyPair(keyPair);
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
        logger.info("Load account from {}", accountFilePath);
        createKeyPair(keyTool.getKeyPair());
    }

    /**
     * Load account from ConfigOption object
     *
     * @param configOption config loaded from config file
     */
    private void loadAccount(ConfigOption configOption) {
        AccountConfig accountConfig = configOption.getAccountConfig();
        String cryptoType = configOption.getCryptoMaterialConfig().getCryptoProvider();
        logger.debug("cryptoType = " + cryptoType);
        if (cryptoType != null && cryptoType.equalsIgnoreCase(HSM)) {
            logger.debug("use hsm key");
            String accountKeyIndex = accountConfig.getAccountKeyIndex();
            if (accountKeyIndex != null) {
                loadSDFInternalAccount(accountKeyIndex, accountConfig.getAccountPassword());
                logger.debug("Load sdf internal account, keyIndex = ", accountKeyIndex);
                return;
            }
        }

        String accountFilePath = accountConfig.getAccountFilePath();
        if (accountFilePath == null || accountFilePath.equals("")) {
            if (accountConfig.getAccountFileFormat().compareToIgnoreCase("p12") == 0) {
                accountFilePath =
                        keyPairFactory.getP12KeyStoreFilePath(accountConfig.getAccountAddress());
            } else if (accountConfig.getAccountFileFormat().compareToIgnoreCase("pem") == 0) {
                accountFilePath =
                        keyPairFactory.getPemKeyStoreFilePath(accountConfig.getAccountAddress());
            }
        }
        loadAccount(
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
        this.keyPairFactory.setConfig(config);
    }

    public int getCryptoTypeConfig() {
        return cryptoTypeConfig;
    }

    public Signature getSignatureImpl() {
        return signatureImpl;
    }

    /**
     * Get hash function, which is relate to the configured CryptoType
     *
     * @return the hash function
     */
    public Hash getHashImpl() {
        return hashImpl;
    }

    /**
     * Call hash function
     *
     * @param inputData string type input data
     * @return the hash digest of input data
     */
    public String hash(final String inputData) {
        return hashImpl.hash(inputData);
    }

    /**
     * Call hash function
     *
     * @param inputBytes byte array type input data
     * @return the hashed string
     */
    public byte[] hash(final byte[] inputBytes) {
        return hashImpl.hash(inputBytes);
    }

    /**
     * Do signature
     *
     * @param message byte array type input string, must be a digest
     * @param keyPair key pair used to do signature
     * @return the signature result
     */
    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return signatureImpl.sign(message, keyPair);
    }

    /**
     * Do signature
     *
     * @param message string type input message, must be a digest
     * @param keyPair key pair used to do signature
     * @return the signature result
     */
    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        return signatureImpl.sign(message, keyPair);
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
        return signatureImpl.signWithStringSignature(message, cryptoKeyPair);
    }

    /**
     * Verify signature, used in AMOP private topic verification procedure
     *
     * @param keyTool the key
     * @param message the string type input message, must be a digest
     * @param signature the string type signature
     * @return the verify result
     */
    public boolean verify(KeyTool keyTool, String message, String signature) {
        return verify(keyTool.getHexedPublicKey(), message, signature);
    }

    /**
     * Verify signature, used in AMOP private topic verification procedure
     *
     * @param keyTool the key
     * @param message the byte array type input message, must be a digest
     * @param signature the byte array type signature
     * @return the verify result
     */
    public boolean verify(KeyTool keyTool, byte[] message, byte[] signature) {
        return verify(keyTool.getHexedPublicKey(), message, signature);
    }

    /**
     * Verify signature
     *
     * @param publicKey the string type public key
     * @param message the string type input message, must be a digest
     * @param signature the string type signature
     * @return the verify result
     */
    public boolean verify(final String publicKey, final String message, final String signature) {
        return signatureImpl.verify(publicKey, message, signature);
    }

    /**
     * Verify signature
     *
     * @param publicKey the string type public key
     * @param message the byte array type input message, must be a digest
     * @param signature the byte array type signature
     * @return the verify result
     */
    public boolean verify(final String publicKey, final byte[] message, final byte[] signature) {
        return signatureImpl.verify(publicKey, message, signature);
    }

    /**
     * Create key pair
     *
     * @return a generated key pair
     */
    public CryptoKeyPair createKeyPair() {
        this.cryptoKeyPair = this.keyPairFactory.generateKeyPair();
        this.cryptoKeyPair.setConfig(config);
        return this.cryptoKeyPair;
    }

    /**
     * Create CryptoKeyPair type key pair from KeyPair type key pair
     *
     * @param keyPair key pair
     * @return CryptoKeyPair type key pair
     */
    public CryptoKeyPair createKeyPair(KeyPair keyPair) {
        this.cryptoKeyPair = this.keyPairFactory.createKeyPair(keyPair);
        this.cryptoKeyPair.setConfig(config);
        return this.cryptoKeyPair;
    }

    /**
     * Create key pair from a private key string
     *
     * @param hexedPrivateKey a hex string of private key
     * @return CryptoKeyPair type key pair
     */
    public CryptoKeyPair createKeyPair(String hexedPrivateKey) {
        this.cryptoKeyPair = this.keyPairFactory.createKeyPair(hexedPrivateKey);
        this.cryptoKeyPair.setConfig(config);
        return this.cryptoKeyPair;
    }

    /**
     * Set the key pair in CryptoSuite
     *
     * @param cryptoKeyPair set the CryptoKeyPair object
     */
    public void setCryptoKeyPair(CryptoKeyPair cryptoKeyPair) {
        this.cryptoKeyPair = cryptoKeyPair;
        this.cryptoKeyPair.setConfig(config);
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
}
