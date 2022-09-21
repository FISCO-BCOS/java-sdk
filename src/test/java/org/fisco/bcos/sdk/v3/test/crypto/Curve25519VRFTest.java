
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
package org.fisco.bcos.sdk.v3.test.crypto;

import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.v3.crypto.vrf.Curve25519VRF;
import org.fisco.bcos.sdk.v3.crypto.vrf.VRFInterface;
import org.fisco.bcos.sdk.v3.crypto.vrf.VRFKeyPair;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class Curve25519VRFTest {
    @Test
    public void testCurve25519VRF()
    {
        // the valid case
        Curve25519VRF vrfInterface = new Curve25519VRF();
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

    public void testCurve25519VRFProve(Curve25519VRF vrfInterface, String privateKey, Hash hashImpl, String anotherPrivateKey)
    {
        for(int i = 0; i < 10; i++) {
            String input = "abcde" + String.valueOf(i);
            testCurve25519VRFProve(vrfInterface, privateKey, input, anotherPrivateKey);
            String hash = hashImpl.hash(input);
            testCurve25519VRFProve(vrfInterface, privateKey, hash, anotherPrivateKey);
        }
    }

    public void testCurve25519VRFProve(Curve25519VRF vrfInterface, String privateKey, String vrfInput, String fakePrivateKey)
    {
        // valid case
        System.out.println(privateKey);
        String publicKey = vrfInterface.getPublicKeyFromPrivateKey(privateKey);
        Assert.assertTrue(vrfInterface.isValidVRFPublicKey(publicKey));

        String vrfProof = vrfInterface.generateVRFProof(privateKey, vrfInput);
        System.out.println("#### vrfPublicKey: " + publicKey + ", vrfProof: " + vrfProof + ", vrfInput: " + vrfInput);
        Assert.assertTrue(vrfInterface.verify(publicKey, vrfInput, vrfProof));

        Numeric.toHexStringWithPrefixZeroPadded(vrfInterface.vrfProofToRandomValue(vrfProof), 128);

        // invalid case
        // case1: invalid public key
        String InvalidPublicKey = "abc";
        Assert.assertFalse(vrfInterface.isValidVRFPublicKey(InvalidPublicKey));
        Assert.assertFalse(vrfInterface.verify(InvalidPublicKey, vrfInput, vrfProof));

        // case2: inconsistent vrf message
        Assert.assertFalse(vrfInterface.verify(publicKey, vrfInput + "_wrong", vrfProof));

        // case3: fake private key
        String fakePublicKey = vrfInterface.getPublicKeyFromPrivateKey(fakePrivateKey);
        Assert.assertTrue(vrfInterface.isValidVRFPublicKey(fakePublicKey));
        Assert.assertFalse(vrfInterface.verify(fakePublicKey, vrfInput + "_wrong", vrfProof));
    }
}