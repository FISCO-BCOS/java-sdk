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
package org.fisco.bcos.sdk.crypto.keystore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.fisco.bcos.sdk.crypto.exceptions.LoadKeyStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KeyManager {
    protected static Logger logger = LoggerFactory.getLogger(KeyManager.class);

    protected final String keyStoreFile;
    protected final String password;
    protected KeyStore keyStore;
    private String hexedPublicKey = "";

    /**
     * constructor for the P12: with password
     *
     * @param keyStoreFile: the path of the keystore file
     * @param password: password to read the keystore file
     */
    public KeyManager(final String keyStoreFile, final String password) {
        this.keyStoreFile = keyStoreFile;
        this.password = password;
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        load();
    }

    /**
     * constructor for PEM: without password
     *
     * @param keyStoreFile:the path of the keystore file
     */
    public KeyManager(final String keyStoreFile) {
        this(keyStoreFile, null);
    }

    protected abstract PrivateKey getPrivateKey();

    public final String getKeyStoreFile() {
        return this.keyStoreFile;
    }

    /**
     * get keyPair loaded from the keyStore file
     *
     * @return: the keyPair
     */
    public KeyPair getKeyPair() {
        PrivateKey privateKey = getPrivateKey();
        PublicKey publicKey = getPublicKeyFromPrivateKey();
        return new KeyPair(publicKey, privateKey);
    }

    protected abstract PublicKey getPublicKey();

    public static String getHexedPublicKey(PublicKey publicKey) {
        byte[] publicKeyBytes = ((BCECPublicKey) publicKey).getQ().getEncoded(false);
        BigInteger publicKeyValue =
                new BigInteger(1, Arrays.copyOfRange(publicKeyBytes, 1, publicKeyBytes.length));
        return ("04" + publicKeyValue.toString(16));
    }

    public String getHexedPublicKey() {
        if (!"".equals(hexedPublicKey)) {
            return this.hexedPublicKey;
        }
        this.hexedPublicKey = getHexedPublicKey(getPublicKey());
        return this.hexedPublicKey;
    }

    public static String getHexedPrivateKey(PrivateKey privateKey) {
        return (((BCECPrivateKey) privateKey).getD()).toString(16);
    }

    protected abstract void load(InputStream in);

    /** load information from the keyStoreFile */
    protected void load() {
        try {
            InputStream keyStoreFileInputStream = new FileInputStream(keyStoreFile);
            this.load(keyStoreFileInputStream);
        } catch (FileNotFoundException | org.bouncycastle.util.encoders.DecoderException e) {
            String errorMessage =
                    "load keys from "
                            + keyStoreFile
                            + "failed for FileNotFoundException, error message:"
                            + e.getMessage();
            logger.error(errorMessage);
            throw new LoadKeyStoreException(errorMessage, e);
        }
    }

    protected PublicKey getPublicKeyFromPrivateKey() {
        try {
            ECPrivateKey privateKey = (ECPrivateKey) getPrivateKey();
            ECParameterSpec params = privateKey.getParams();

            org.bouncycastle.jce.spec.ECParameterSpec bcSpec = EC5Util.convertSpec(params, false);
            org.bouncycastle.math.ec.ECPoint q = bcSpec.getG().multiply(privateKey.getS());
            org.bouncycastle.math.ec.ECPoint bcW =
                    bcSpec.getCurve().decodePoint(q.getEncoded(false));
            ECPoint w =
                    new ECPoint(
                            bcW.getAffineXCoord().toBigInteger(),
                            bcW.getAffineYCoord().toBigInteger());
            ECPublicKeySpec keySpec = new ECPublicKeySpec(w, tryFindNamedCurveSpec(params));
            return (PublicKey)
                    KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME)
                            .generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            String errorMessage =
                    "get publicKey from "
                            + keyStoreFile
                            + "failed, error message:"
                            + e.getMessage();
            logger.error(errorMessage);
            throw new LoadKeyStoreException(errorMessage, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static ECParameterSpec tryFindNamedCurveSpec(ECParameterSpec params) {
        org.bouncycastle.jce.spec.ECParameterSpec bcSpec = EC5Util.convertSpec(params, false);
        for (Object name : Collections.list(ECNamedCurveTable.getNames())) {
            ECNamedCurveParameterSpec bcNamedSpec =
                    ECNamedCurveTable.getParameterSpec((String) name);
            if (bcNamedSpec.getN().equals(bcSpec.getN())
                    && bcNamedSpec.getH().equals(bcSpec.getH())
                    && bcNamedSpec.getCurve().equals(bcSpec.getCurve())
                    && bcNamedSpec.getG().equals(bcSpec.getG())) {
                return new ECNamedCurveSpec(
                        bcNamedSpec.getName(),
                        bcNamedSpec.getCurve(),
                        bcNamedSpec.getG(),
                        bcNamedSpec.getN(),
                        bcNamedSpec.getH(),
                        bcNamedSpec.getSeed());
            }
        }
        return params;
    }
}
