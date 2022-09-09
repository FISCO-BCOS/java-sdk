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
import com.webank.wedpr.crypto.NativeInterface;
import java.math.BigInteger;
import java.security.KeyPair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.keypair.KeyPairJniObj;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ECDSAKeyPair extends CryptoKeyPair {
    private static final Logger logger = LoggerFactory.getLogger(SM2KeyPair.class);

    public static Hash DefaultHashAlgorithm = new Keccak256();

    public ECDSAKeyPair() {
        initECDSAKeyPair();
        CryptoKeyPair keyPair = this.generateKeyPair();
        this.hexPrivateKey = keyPair.getHexPrivateKey();
        this.hexPublicKey = keyPair.getHexPublicKey();
        this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
        this.initJniKeyPair();
    }

    public ECDSAKeyPair(KeyPair javaKeyPair) {
        super(javaKeyPair);
        this.initECDSAKeyPair();
        this.initJniKeyPair();
    }

    protected ECDSAKeyPair(final CryptoResult ecKeyPairInfo) {
        super(ecKeyPairInfo);
        this.initECDSAKeyPair();
        this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
        this.initJniKeyPair();
    }

    private void initECDSAKeyPair() {
        this.hashImpl = new Keccak256();
        this.curveName = ECDSA_CURVE_NAME;
        this.keyStoreSubDir = ECDSA_ACCOUNT_SUBDIR;
        this.signatureAlgorithm = ECDSA_SIGNATURE_ALGORITHM;
    }

    private void initJniKeyPair() {
        try {
            this.jniKeyPair =
                    KeyPairJniObj.createJniKeyPair(
                            CryptoType.ECDSA_TYPE, Hex.decode(this.hexPrivateKey));
        } catch (JniException e) {
            // TODO: handle jni exception
            logger.error("jni e: ", e);
        }
    }

    public static CryptoKeyPair createKeyPair() {
        return new ECDSAKeyPair(NativeInterface.secp256k1GenKeyPair());
    }

    /**
     * generate keyPair randomly
     *
     * @return the generated keyPair
     */
    @Override
    public CryptoKeyPair generateKeyPair() {
        return new ECDSAKeyPair(NativeInterface.secp256k1GenKeyPair());
    }

    @Override
    public CryptoKeyPair createKeyPair(KeyPair javaKeyPair) {
        return new ECDSAKeyPair(javaKeyPair);
    }

    public static String getAddressByPublicKey(String publicKey) {
        return getAddress(publicKey, ECDSAKeyPair.DefaultHashAlgorithm);
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
