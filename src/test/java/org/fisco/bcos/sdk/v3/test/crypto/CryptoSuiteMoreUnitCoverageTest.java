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

import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignature;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java (no node) coverage tests for {@link CryptoSuite} focusing on the ECDSA path: hash, sign,
 * verify, key-pair loading and accessors. The SM path is exercised where it works offline.
 */
public class CryptoSuiteMoreUnitCoverageTest {

    @Test
    public void testEcdsaImplsAndType() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        Assert.assertEquals(CryptoType.ECDSA_TYPE, cryptoSuite.getCryptoTypeConfig());
        Assert.assertTrue(cryptoSuite.getHashImpl() instanceof Keccak256);
        Assert.assertTrue(cryptoSuite.getSignatureImpl() instanceof ECDSASignature);
        Assert.assertNotNull(cryptoSuite.getCryptoKeyPair());
        Assert.assertNotNull(cryptoSuite.getKeyPairFactory());
        // config not set when constructed with the bare type ctor
        Assert.assertNull(cryptoSuite.getConfig());
    }

    @Test
    public void testHashStringAndBytes() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        String expected = "1c8aff950685c2ed4bc3174f3472287b56d9517b9c948127319a09a7a36deac8";
        Assert.assertEquals(expected, cryptoSuite.hash("hello"));
        byte[] bytesHash = cryptoSuite.hash("hello".getBytes());
        Assert.assertEquals(expected, Hex.toHexString(bytesHash));
    }

    @Test
    public void testSignVerifyRoundTripEcdsa() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();

        // message must be a digest
        String message = cryptoSuite.hash("a message to sign");
        SignatureResult signatureResult = cryptoSuite.sign(message, keyPair);
        Assert.assertNotNull(signatureResult);
        Assert.assertTrue(
                cryptoSuite.verify(
                        keyPair.getHexPublicKey(), message, signatureResult.convertToString()));

        // byte[] overloads
        byte[] msgBytes = Hex.decode(message);
        SignatureResult sigBytes = cryptoSuite.sign(msgBytes, keyPair);
        Assert.assertTrue(
                cryptoSuite.verify(
                        keyPair.getHexPublicKey(), message, sigBytes.convertToString()));
    }

    @Test
    public void testRecoverAddressEcdsa() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        String message = cryptoSuite.hash("recover me");
        SignatureResult signatureResult = cryptoSuite.sign(message, keyPair);
        String recovered = cryptoSuite.recoverAddress(message, signatureResult);
        Assert.assertEquals(keyPair.getAddress(), recovered);
    }

    @Test
    public void testLoadKeyPairFromHexPrivateKey() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        String hexPrivateKey = "bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd";
        CryptoKeyPair loaded = cryptoSuite.loadKeyPair(hexPrivateKey);
        Assert.assertNotNull(loaded);
        // the public key is deterministic for a given private key
        CryptoKeyPair reference =
                cryptoSuite.getKeyPairFactory().createKeyPair(hexPrivateKey);
        Assert.assertEquals(reference.getHexPublicKey(), loaded.getHexPublicKey());
        // loaded key pair is now the active one in the suite
        Assert.assertEquals(
                loaded.getHexPublicKey(), cryptoSuite.getCryptoKeyPair().getHexPublicKey());

        // sign / verify with the loaded pair
        String message = cryptoSuite.hash("loaded key");
        SignatureResult sig = cryptoSuite.sign(message, loaded);
        Assert.assertTrue(
                cryptoSuite.verify(loaded.getHexPublicKey(), message, sig.convertToString()));
    }

    @Test
    public void testSetAndGetCryptoKeyPair() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair fresh = new ECDSAKeyPair().generateKeyPair();
        cryptoSuite.setCryptoKeyPair(fresh);
        Assert.assertEquals(
                fresh.getHexPublicKey(), cryptoSuite.getCryptoKeyPair().getHexPublicKey());
    }

    @Test
    public void testGenerateRandomKeyPairProducesDistinctKeys() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair first = cryptoSuite.getCryptoKeyPair();
        CryptoKeyPair second = cryptoSuite.generateRandomKeyPair();
        Assert.assertNotNull(second);
        Assert.assertNotEquals(first.getHexPrivateKey(), second.getHexPrivateKey());
        // generateRandomKeyPair also updates the active key pair
        Assert.assertEquals(
                second.getHexPublicKey(), cryptoSuite.getCryptoKeyPair().getHexPublicKey());
    }

    @Test
    public void testConstructorWithKeyPair() {
        CryptoSuite base = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair keyPair = base.getCryptoKeyPair();
        CryptoSuite fromKeyPair = new CryptoSuite(CryptoType.ECDSA_TYPE, keyPair);
        Assert.assertEquals(
                keyPair.getHexPublicKey(), fromKeyPair.getCryptoKeyPair().getHexPublicKey());
        Assert.assertEquals(
                keyPair.getHexPrivateKey(), fromKeyPair.getCryptoKeyPair().getHexPrivateKey());
    }

    @Test
    public void testConstructorWithHexPrivateKey() {
        String hexPrivateKey = "bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd";
        CryptoSuite fromHex = new CryptoSuite(CryptoType.ECDSA_TYPE, hexPrivateKey);
        CryptoKeyPair reference =
                new ECDSAKeyPair().createKeyPair(hexPrivateKey);
        Assert.assertEquals(
                reference.getHexPublicKey(), fromHex.getCryptoKeyPair().getHexPublicKey());
    }

    @Test
    public void testDestroy() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        Assert.assertNotNull(cryptoSuite.getCryptoKeyPair());
        cryptoSuite.destroy();
        Assert.assertNull(cryptoSuite.getCryptoKeyPair());
        // destroy is idempotent
        cryptoSuite.destroy();
        Assert.assertNull(cryptoSuite.getCryptoKeyPair());
    }

    @Test
    public void testSmTypeOffline() {
        // SM_TYPE construction works offline (same as the existing HashTest).
        CryptoSuite smSuite = new CryptoSuite(CryptoType.SM_TYPE);
        Assert.assertEquals(CryptoType.SM_TYPE, smSuite.getCryptoTypeConfig());
        Assert.assertTrue(smSuite.getHashImpl() instanceof SM3Hash);
        Assert.assertTrue(smSuite.getSignatureImpl() instanceof SM2Signature);

        CryptoKeyPair keyPair = smSuite.getCryptoKeyPair();
        Assert.assertNotNull(keyPair);

        String message = smSuite.hash("sm message");
        SignatureResult sig = smSuite.sign(message, keyPair);
        Assert.assertTrue(
                smSuite.verify(keyPair.getHexPublicKey(), message, sig.convertToString()));
    }

    @Test
    public void testHashImplDirectUsage() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        Hash hashImpl = cryptoSuite.getHashImpl();
        Assert.assertEquals(cryptoSuite.hash("direct"), hashImpl.hash("direct"));
    }
}
