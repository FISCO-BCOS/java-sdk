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
package org.fisco.bcos.sdk.test.crypto;

import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignature;
import org.fisco.bcos.sdk.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.junit.Assert;
import org.junit.Test;

public class SignatureTest {
    @Test
    public void testCryptoInterfaceForECDSA() {
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoInterface.ECDSA_TYPE);
        // generate keyPair
        CryptoKeyPair keyPair = cryptoInterface.createKeyPair();
        // test signature
        testSignature(cryptoInterface, keyPair);
    }

    @Test
    public void testCryptoInterfaceForSM2() {
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoInterface.SM_TYPE);
        // generate keyPair
        CryptoKeyPair keyPair = cryptoInterface.createKeyPair();
        // test signature
        testSignature(cryptoInterface, keyPair);
    }

    @Test
    public void testECDSASignature() {
        Signature ecdsaSignature = new ECDSASignature();
        CryptoKeyPair keyPair = (new ECDSAKeyPair()).generateKeyPair();
        testSignature(ecdsaSignature, keyPair);
    }

    @Test
    public void testSM2Signature() {
        Signature sm2Signature = new SM2Signature();
        CryptoKeyPair keyPair = (new SM2KeyPair()).generateKeyPair();
        testSignature(sm2Signature, keyPair);
    }

    private void testSignature(Signature signature, CryptoKeyPair keyPair) {
        String message = "abcde";
        // check valid case
        for (int i = 0; i < 10; i++) {
            message = "abcd----" + Integer.toString(i);
            // sign
            SignatureResult signResult = signature.sign(message, keyPair);
            // verify
            Assert.assertTrue(
                    signature.verify(
                            keyPair.getHexPublicKey(), message, signResult.convertToString()));
            signResult = signature.sign(message.getBytes(), keyPair);
            Assert.assertTrue(
                    signature.verify(
                            keyPair.getHexPublicKey(), message, signResult.convertToString()));
        }

        // check invalid case
        for (int i = 0; i < 10; i++) {
            message = "abcd----" + Integer.toString(i);
            String invaidMessage = "abcd---" + Integer.toString(i + 1);
            // sign
            SignatureResult signResult = signature.sign(message, keyPair);
            // verify
            Assert.assertEquals(
                    false,
                    signature.verify(
                            keyPair.getHexPublicKey(),
                            invaidMessage,
                            signResult.convertToString()));
            signResult = signature.sign(message.getBytes(), keyPair);
            Assert.assertEquals(
                    false,
                    signature.verify(
                            keyPair.getHexPublicKey(),
                            invaidMessage,
                            signResult.convertToString()));
        }
    }

    private void testSignature(CryptoInterface signature, CryptoKeyPair keyPair) {
        String message = "abcde";
        // check valid case
        for (int i = 0; i < 10; i++) {
            message = "abcd----" + Integer.toString(i);
            // sign
            SignatureResult signResult = signature.sign(message, keyPair);
            // verify
            Assert.assertTrue(
                    signature.verify(
                            keyPair.getHexPublicKey(), message, signResult.convertToString()));
            signResult = signature.sign(message.getBytes(), keyPair);
            Assert.assertTrue(
                    signature.verify(
                            keyPair.getHexPublicKey(), message, signResult.convertToString()));
        }

        // check invalid case
        for (int i = 0; i < 10; i++) {
            message = "abcd----" + Integer.toString(i);
            String invaidMessage = "abcd---" + Integer.toString(i + 1);
            // sign
            SignatureResult signResult = signature.sign(message, keyPair);
            // verify
            Assert.assertEquals(
                    false,
                    signature.verify(
                            keyPair.getHexPublicKey(),
                            invaidMessage,
                            signResult.convertToString()));
            signResult = signature.sign(message.getBytes(), keyPair);
            Assert.assertEquals(
                    false,
                    signature.verify(
                            keyPair.getHexPublicKey(),
                            invaidMessage,
                            signResult.convertToString()));
        }
    }
}
