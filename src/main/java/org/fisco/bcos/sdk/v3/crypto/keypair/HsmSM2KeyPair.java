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

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.keypair.KeyPairJniObj;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;

public class HsmSM2KeyPair extends CryptoKeyPair {
    private static final Logger logger = LoggerFactory.getLogger(HsmSM2KeyPair.class);

    public static Hash DefaultHashAlgorithm = new SM3Hash();
    private String hsmLibPath;

    public HsmSM2KeyPair(String hsmLibPath) {
        this.hsmLibPath = hsmLibPath;
        initHsmSM2KeyPairObject();
    }

    public HsmSM2KeyPair(String hsmLibPath, KeyPair javaKeyPair) {
        super(javaKeyPair);
        this.hsmLibPath = hsmLibPath;
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
            HsmSM2KeyPair hsmSM2KeyPair = new HsmSM2KeyPair(this.hsmLibPath);
            hsmSM2KeyPair.jniKeyPair = KeyPairJniObj.createHsmKeyPair(this.hsmLibPath);
            return hsmSM2KeyPair;
        } catch (JniException e) {
            // TODO: handle jni exception
            logger.error("hsm generateKeyPair exception, jni e: ", e);
            return null;
        }
    }

    @Override
    public CryptoKeyPair createKeyPair(KeyPair javaKeyPair) {
        try {
            HsmSM2KeyPair hsmSM2KeyPair = new HsmSM2KeyPair(this.hsmLibPath, javaKeyPair);
            hsmSM2KeyPair.jniKeyPair = KeyPairJniObj.createHsmKeyPair(javaKeyPair.getPrivate().getEncoded(), this.hsmLibPath);
            return hsmSM2KeyPair;

        } catch (JniException e) {
            // TODO: handle jni exception
            logger.error("hsm createKeyPair exception, jni e: ", e);
            return null;
        }
    }

    public CryptoKeyPair useKeyPair(int keyIndex, String password) {
        try {
            HsmSM2KeyPair hsmSM2KeyPair = new HsmSM2KeyPair(this.hsmLibPath);
            hsmSM2KeyPair.jniKeyPair = KeyPairJniObj.useHsmKeyPair(keyIndex, password, this.hsmLibPath);
            return hsmSM2KeyPair;
        } catch (JniException e) {
            // TODO: handle jni exception
            logger.error("hsm useKeyPair exception, jni e: ", e);
            return null;
        }
    }

    public String getHsmLibPath() {
        return hsmLibPath;
    }

    public void setHsmLibPath(String hsmLibPath) {
        this.hsmLibPath = hsmLibPath;
    }
}
