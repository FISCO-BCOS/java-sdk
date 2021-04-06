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
package org.fisco.bcos.sdk.crypto.vrf;

import com.webank.wedpr.crypto.CryptoResult;
import com.webank.wedpr.crypto.NativeInterface;
import java.math.BigInteger;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.utils.Numeric;

public class Curve25519VRF implements VRFInterface {
    private final ECDSAKeyPair keyPairGenerator = new ECDSAKeyPair();

    @Override
    public VRFKeyPair createKeyPair() {
        // default use secp256k1 to create the private key
        CryptoKeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new VRFKeyPair(CryptoType.ED25519_VRF_TYPE, keyPair.getHexPrivateKey());
    }

    @Override
    public String generateVRFProof(String privateKey, String vrfInput) {
        CryptoResult result =
                NativeInterface.curve25519VrfProveUtf8(
                        Numeric.getKeyNoPrefix(
                                CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                                privateKey,
                                CryptoKeyPair.PRIVATE_KEY_SIZE_IN_HEX),
                        vrfInput);
        if (result.wedprErrorMessage != null && !result.wedprErrorMessage.isEmpty()) {
            throw new VRFException("generate VRF Proof failed: " + result.wedprErrorMessage);
        }
        return result.vrfProof;
    }

    @Override
    public boolean verify(String publicKey, String vrfInput, String vrfProof) {
        CryptoResult result =
                NativeInterface.curve25519VrfVerifyUtf8(publicKey, vrfInput, vrfProof);
        if (result.wedprErrorMessage != null && !result.wedprErrorMessage.isEmpty()) {
            return false;
        }
        return result.booleanResult;
    }

    @Override
    public String getPublicKeyFromPrivateKey(String privateKey) {
        CryptoResult result =
                NativeInterface.curve25519VrfDerivePublicKey(
                        Numeric.getKeyNoPrefix(
                                CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                                privateKey,
                                CryptoKeyPair.PRIVATE_KEY_SIZE_IN_HEX));
        if (result.wedprErrorMessage != null && !result.wedprErrorMessage.isEmpty()) {
            throw new VRFException("get VRF Proof failed: " + result.wedprErrorMessage);
        }
        return result.publicKey;
    }

    @Override
    public String vrfProofToHash(String vrfProof) {
        CryptoResult result = NativeInterface.curve25519VrfProofToHash(vrfProof);
        if (result.wedprErrorMessage != null && !result.wedprErrorMessage.isEmpty()) {
            throw new VRFException("convert VRF Proof to hash failed:" + result.wedprErrorMessage);
        }
        return result.hash;
    }

    @Override
    public BigInteger vrfProofToRandomValue(String vrfProof) {
        return new BigInteger(vrfProofToHash(vrfProof), 16);
    }

    @Override
    public boolean isValidVRFPublicKey(String vrfPublicKey) {
        CryptoResult result = NativeInterface.curve25519VrfIsValidPublicKey(vrfPublicKey);
        if (result.wedprErrorMessage != null && !result.wedprErrorMessage.isEmpty()) {
            return false;
        }
        return result.booleanResult;
    }
}
