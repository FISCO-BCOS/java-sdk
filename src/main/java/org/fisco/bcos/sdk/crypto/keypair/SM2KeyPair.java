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

public class SM2KeyPair extends CryptoKeyPair {
    public SM2KeyPair() {}

    protected SM2KeyPair(CryptoResult sm2keyPairInfo) {
        super(sm2keyPairInfo);
    }

    /**
     * generate keyPair randomly
     *
     * @return: the generated keyPair
     */
    @Override
    public CryptoKeyPair generateKeyPair() {
        return new SM2KeyPair(NativeInterface.sm2keyPair());
    }
}
