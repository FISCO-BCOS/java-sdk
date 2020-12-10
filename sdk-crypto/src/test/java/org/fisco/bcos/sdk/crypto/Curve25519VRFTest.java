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

import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.crypto.vrf.Curve25519VRF;
import org.fisco.bcos.sdk.crypto.vrf.VRFInterface;
import org.fisco.bcos.sdk.crypto.vrf.VRFKeyPair;
import org.junit.Assert;
import org.junit.Test;

public class Curve25519VRFTest {
    @Test
    public void testCurve25519VRF()
    {
        // the valid case
        VRFInterface vrfInterface = new Curve25519VRF();
        Hash sm3Hash = new SM3Hash();
        Hash keccak256Hash = new Keccak256();
        CryptoKeyPair ecdsaCryptoKeyPair = (new ECDSAKeyPair());
        CryptoKeyPair sm2CryptoKeyPair = (new SM2KeyPair());
        testCurve25519VRFProve(vrfInterface, ecdsaCryptoKeyPair.getHexPrivateKey(), keccak256Hash, new ECDSAKeyPair().getHexPrivateKey());
        testCurve25519VRFProve(vrfInterface, sm2CryptoKeyPair.getHexPrivateKey(), sm3Hash, new SM2KeyPair().getHexPrivateKey());

        // generate the VRFKeyPair
        VRFKeyPair vrfKeyPair = vrfInterface.createKeyPair();
        //testCurve25519VRFProve(vrfInterface, vrfKeyPair.getVrfPrivateKey(), keccak256Hash, (vrfInterface.createKeyPair()).getVrfPrivateKey());
        testCurve25519VRFProve(vrfInterface, vrfKeyPair.getVrfPrivateKey(), sm3Hash, (vrfInterface.createKeyPair()).getVrfPrivateKey());
    }

    public void testCurve25519VRFProve(VRFInterface vrfInterface, String privateKey, Hash hashImpl, String anotherPrivateKey)
    {
        for(int i = 0; i < 10; i++) {
            String input = "abcde" + String.valueOf(i);
            testCurve25519VRFProve(vrfInterface, privateKey, input, anotherPrivateKey);
            String hash = hashImpl.hash(input);
            testCurve25519VRFProve(vrfInterface, privateKey, hash, anotherPrivateKey);
        }
    }

    public void testCurve25519VRFProve(VRFInterface vrfInterface, String privateKey, String vrfInput, String fakePrivateKey)
    {
        // valid case
        String publicKey = vrfInterface.getPublicKeyFromPrivateKey(privateKey);
        Assert.assertTrue(vrfInterface.isValidVRFPublicKey(publicKey));

        String vrfProof = vrfInterface.generateVRFProof(privateKey, vrfInput);
        Assert.assertTrue(vrfInterface.verify(publicKey, vrfInput, vrfProof));

        // invalid case
        // case1: invalid public key
        String InvalidPublicKey = "abc";
        Assert.assertTrue(vrfInterface.isValidVRFPublicKey(InvalidPublicKey) == false);
        Assert.assertTrue(vrfInterface.verify(InvalidPublicKey, vrfInput, vrfProof) == false);

        // case2: inconsistent vrf message
        Assert.assertTrue(vrfInterface.verify(publicKey, vrfInput + "_wrong", vrfProof) == false);

        // case3: fake private key
        String fakePublicKey = vrfInterface.getPublicKeyFromPrivateKey(fakePrivateKey);
        Assert.assertTrue(vrfInterface.isValidVRFPublicKey(fakePublicKey));
        Assert.assertTrue(vrfInterface.verify(fakePublicKey, vrfInput + "_wrong", vrfProof) == false);
    }
}