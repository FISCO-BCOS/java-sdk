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

import java.security.KeyPair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.keypair.KeyPairJniObj;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.crypto.keystore.KeyTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmSM2KeyPair extends CryptoKeyPair {
    private static final Logger logger = LoggerFactory.getLogger(HsmSM2KeyPair.class);

    public static Hash DefaultHashAlgorithm = new SM3Hash();
    private String hsmLibPath;
    private int hsmKeyIndex;
    private String hsmPassword;

    public HsmSM2KeyPair(String hsmLibPath, int hsmKeyIndex, String hsmPassword) {
        this.hsmLibPath = hsmLibPath;
        this.hsmKeyIndex = hsmKeyIndex;
        this.hsmPassword = hsmPassword;
        initHsmSM2KeyPairObject();
    }

    private void initHsmSM2KeyPairObject() {
        this.keyStoreSubDir = GM_ACCOUNT_SUBDIR;
        this.hashImpl = new SM3Hash();
        this.curveName = SM2_CURVE_NAME;
        this.signatureAlgorithm = SM_SIGNATURE_ALGORITHM;
    }

    /**
     * generate keyPair randomly
     *
     * @return the generated keyPair
     */
    @Override
    public CryptoKeyPair generateKeyPair() {
        try {
            this.jniKeyPair = KeyPairJniObj.createHsmKeyPair(this.hsmLibPath);
            this.hexPublicKey = KeyPairJniObj.getJniKeyPairPubKey(this.jniKeyPair);
            this.hexPrivateKey = KeyPairJniObj.getJniKeyPairPrivateKey(this.jniKeyPair);
            this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
            return this;
        } catch (JniException e) {
            logger.error("hsm generateKeyPair exception, jni e: ", e);
            return null;
        }
    }

    @Override
    public CryptoKeyPair createKeyPair(KeyPair javaKeyPair) {
        try {
            this.jniKeyPair =
                    KeyPairJniObj.createHsmKeyPair(
                            javaKeyPair.getPrivate().getEncoded(), this.hsmLibPath);
            this.hexPublicKey = KeyPairJniObj.getJniKeyPairPubKey(this.jniKeyPair);
            this.hexPrivateKey = KeyPairJniObj.getJniKeyPairPrivateKey(this.jniKeyPair);
            this.keyPair = KeyTool.convertHexedStringToKeyPair(this.hexPrivateKey, curveName);
            return this;
        } catch (JniException e) {
            logger.error("hsm createKeyPair exception, jni e: ", e);
            return null;
        }
    }

    public CryptoKeyPair useKeyPair() {
        try {
            this.jniKeyPair =
                    KeyPairJniObj.useHsmKeyPair(
                            this.hsmKeyIndex, this.hsmPassword, this.hsmLibPath);
            this.hexPublicKey = KeyPairJniObj.getJniKeyPairPubKey(this.jniKeyPair);
            return this;
        } catch (JniException e) {
            logger.error("hsm useKeyPair exception, jni e: ", e);
            return null;
        }
    }

    public String getHsmLibPath() {
        return hsmLibPath;
    }

    public int getHsmKeyIndex() {
        return hsmKeyIndex;
    }

    public String getHsmPassword() {
        return hsmPassword;
    }
}
