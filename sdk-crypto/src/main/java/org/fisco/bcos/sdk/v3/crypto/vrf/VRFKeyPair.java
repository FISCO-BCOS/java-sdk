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
package org.fisco.bcos.sdk.v3.crypto.vrf;

import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Numeric;

public class VRFKeyPair {
    private String vrfPublicKey;
    private String vrfPrivateKey;
    private VRFInterface vrfInterface;

    private VRFKeyPair() {}

    public VRFKeyPair(String vrfPrivateKey, String vrfPublicKey) {
        this.vrfPrivateKey =
                Numeric.getKeyNoPrefix(
                        CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                        vrfPrivateKey,
                        CryptoKeyPair.PRIVATE_KEY_SIZE_IN_HEX);
        this.vrfPublicKey = vrfPublicKey;
    }

    public VRFKeyPair(int vrfCryptoType, String vrfPrivateKey) {
        if (vrfCryptoType == CryptoType.ED25519_VRF_TYPE) {
            vrfInterface = new Curve25519VRF();
        } else {
            throw new VRFException(
                    "Invalid cryptoType, only support CryptoType.ED25519_VRF_TYPE = "
                            + CryptoType.ED25519_VRF_TYPE
                            + " now!");
        }
        this.vrfPrivateKey =
                Numeric.getKeyNoPrefix(
                        CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                        vrfPrivateKey,
                        CryptoKeyPair.PRIVATE_KEY_SIZE_IN_HEX);
        this.vrfPublicKey = vrfInterface.getPublicKeyFromPrivateKey(vrfPrivateKey);
    }

    public String getVrfPublicKey() {
        return vrfPublicKey;
    }

    public void setVrfPublicKey(String vrfPublicKey) {
        this.vrfPublicKey = vrfPublicKey;
    }

    public String getVrfPrivateKey() {
        return vrfPrivateKey;
    }

    public void setVrfPrivateKey(String vrfPrivateKey) {
        this.vrfPrivateKey = vrfPrivateKey;
    }
}
