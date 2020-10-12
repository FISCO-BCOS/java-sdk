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
import com.webank.wedpr.crypto.NativeInterface;
import java.security.KeyPair;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;

public class SM2KeyPair extends CryptoKeyPair {
    public SM2KeyPair() {
        initSM2KeyPairObject();
    }

    public SM2KeyPair(KeyPair javaKeyPair) {
        super(javaKeyPair);
        initSM2KeyPairObject();
    }

    protected SM2KeyPair(CryptoResult sm2keyPairInfo) {
        super(sm2keyPairInfo);
        initSM2KeyPairObject();
    }

    private void initSM2KeyPairObject() {
        this.keyStoreSubDir = GM_ACCOUNT_SUBDIR;
        this.hashImpl = new SM3Hash();
        this.curveName = CryptoKeyPair.SM2_CURVE_NAME;
        this.signatureAlgorithm = SM_SIGNATURE_ALGORITHM;
    }

    /**
     * generate keyPair randomly
     *
     * @return the generated keyPair
     */
    @Override
    public CryptoKeyPair generateKeyPair() {
        return new SM2KeyPair(NativeInterface.sm2keyPair());
    }

    @Override
    public CryptoKeyPair createKeyPair(KeyPair javaKeyPair) {
        return new SM2KeyPair(javaKeyPair);
    }
}
