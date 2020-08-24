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

import java.security.KeyPair;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.model.AccountConfig;
import org.fisco.bcos.sdk.crypto.exceptions.LoadKeyStoreException;
import org.fisco.bcos.sdk.crypto.exceptions.UnsupportedCryptoTypeException;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.crypto.keystore.P12Manager;
import org.fisco.bcos.sdk.crypto.keystore.PEMManager;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignature;
import org.fisco.bcos.sdk.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoInterface {

    private static Logger logger = LoggerFactory.getLogger(CryptoInterface.class);

    public static final int ECDSA_TYPE = 0;
    public static final int SM_TYPE = 1;
    public final int cryptoTypeConfig;

    public final Signature signatureImpl;
    public final Hash hashImpl;
    private final CryptoKeyPair keyPairFactory;
    private CryptoKeyPair cryptoKeyPair;
    private ConfigOption config;

    public CryptoInterface(int cryptoTypeConfig, ConfigOption configOption) {
        this(cryptoTypeConfig);
        logger.info("init CryptoInterface, cryptoType: {}", cryptoTypeConfig);
        setConfig(configOption);
        // doesn't set the account name, generate the keyPair randomly
        if (configOption.getAccountConfig().getAccountAddress().equals("")) {
            createKeyPair();
            return;
        }
        loadAccount(configOption);
    }
    /**
     * init the common crypto implementation accordign to the crypto type
     *
     * @param cryptoTypeConfig
     */
    public CryptoInterface(int cryptoTypeConfig) {
        this.cryptoTypeConfig = cryptoTypeConfig;
        if (this.cryptoTypeConfig == ECDSA_TYPE) {
            this.signatureImpl = new ECDSASignature();
            this.hashImpl = new Keccak256();
            this.keyPairFactory = new ECDSAKeyPair();

        } else if (this.cryptoTypeConfig == SM_TYPE) {
            this.signatureImpl = new SM2Signature();
            this.hashImpl = new SM3Hash();
            this.keyPairFactory = new SM2KeyPair();

        } else {
            throw new UnsupportedCryptoTypeException(
                    "only support " + ECDSA_TYPE + "/" + SM_TYPE + " crypto type");
        }
        // create keyPair randomly
        createKeyPair();
    }

    private void loadAccount(ConfigOption configOption) {
        KeyManager keyManager;
        AccountConfig accountConfig = configOption.getAccountConfig();
        if (accountConfig.getAccountFileFormat().compareToIgnoreCase("p12") == 0) {
            keyManager =
                    new P12Manager(
                            keyPairFactory.getP12KeyStoreFilePath(
                                    accountConfig.getAccountAddress()),
                            accountConfig.getAccountPassword());
        } else if (accountConfig.getAccountFileFormat().compareToIgnoreCase("pem") == 0) {
            keyManager =
                    new PEMManager(
                            keyPairFactory.getPemKeyStoreFilePath(
                                    accountConfig.getAccountAddress()));
        } else {
            throw new LoadKeyStoreException(
                    "unsupported account file format : "
                            + accountConfig.getAccountFileFormat()
                            + ", current supported are p12 and pem");
        }
        createKeyPair(keyManager.getKeyPair());
    }

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

    public Hash getHashImpl() {
        return hashImpl;
    }

    public String hash(final String inputData) {
        return hashImpl.hash(inputData);
    }

    public byte[] hash(final byte[] inputBytes) {
        return hashImpl.hash(inputBytes);
    }

    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return signatureImpl.sign(message, keyPair);
    }

    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        return signatureImpl.sign(message, keyPair);
    }

    // for AMOP topic verify, generate signature
    public String sign(KeyManager keyManager, String message) {
        CryptoKeyPair cryptoKeyPair = this.keyPairFactory.createKeyPair(keyManager.getKeyPair());
        return signatureImpl.signWithStringSignature(message, cryptoKeyPair);
    }

    // for AMOP topic verify, verify the signature
    public boolean verify(KeyManager keyManager, String message, String signature) {
        return verify(keyManager.getHexedPublicKey(), message, signature);
    }

    public boolean verify(final String publicKey, final String message, final String signature) {
        return signatureImpl.verify(publicKey, message, signature);
    }

    public boolean verify(final String publicKey, final byte[] message, final byte[] signature) {
        return signatureImpl.verify(publicKey, message, signature);
    }

    public CryptoKeyPair createKeyPair() {
        this.cryptoKeyPair = this.keyPairFactory.generateKeyPair();
        this.cryptoKeyPair.setConfig(config);
        return this.cryptoKeyPair;
    }

    public CryptoKeyPair createKeyPair(KeyPair keyPair) {
        this.cryptoKeyPair = this.keyPairFactory.createKeyPair(keyPair);
        this.cryptoKeyPair.setConfig(config);
        return this.cryptoKeyPair;
    }

    public void setCryptoKeyPair(CryptoKeyPair cryptoKeyPair) {
        this.cryptoKeyPair = cryptoKeyPair;
    }

    public CryptoKeyPair getCryptoKeyPair() {
        return this.cryptoKeyPair;
    }
}
