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

import java.io.File;
import java.math.BigInteger;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.crypto.exceptions.KeyPairException;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.crypto.keystore.KeyManager;
import org.fisco.bcos.sdk.crypto.keystore.P12Manager;
import org.fisco.bcos.sdk.crypto.keystore.PEMManager;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignature;
import org.fisco.bcos.sdk.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.crypto.signature.Signature;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

public class SignatureTest {
    private static final String configFile =
            SignatureTest.class.getClassLoader().getResource("config-example.yaml").getPath();

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
        Hash hasher = new Keccak256();
        testSignature(hasher, ecdsaSignature, keyPair);
    }

    @Test
    public void testSM2Signature() {
        Signature sm2Signature = new SM2Signature();
        CryptoKeyPair keyPair = (new SM2KeyPair()).generateKeyPair();
        Hash hasher = new SM3Hash();
        testSignature(hasher, sm2Signature, keyPair);
    }

    @Test
    public void testValidGetAddressForECDSA() {
        CryptoKeyPair keyPair = (new ECDSAKeyPair()).generateKeyPair();
        String hexPublicKey =
                "77a8f8d2f786f079bd661e774da3a9f430c76b9acbcd71f9976bff7456bb136a80cb97335cc929a531791970f8ce10c0ca6ffb391e9ef241a48cbd8f3db1a82e";
        String expectedHash = "0xdeaa5343178c2be2cb5e9b13000ed951e302c15d";

        String hexPublicKey2 = "00000";
        String expectedHash2 = "0x3f17f1962b36e491b30a40b2405849e597ba5fb5";
        testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2);

        // create keyPair with cryptoInterface
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoInterface.ECDSA_TYPE);
        keyPair = cryptoInterface.createKeyPair();
        testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2);

        // test getAddress with generated KeyPair
        keyPair.getAddress();
    }

    @Test
    public void testValidGetAddressForSM() {
        CryptoKeyPair keyPair = (new SM2KeyPair()).generateKeyPair();
        String hexPublicKey =
                "77a8f8d2f786f079bd661e774da3a9f430c76b9acbcd71f9976bff7456bb136a80cb97335cc929a531791970f8ce10c0ca6ffb391e9ef241a48cbd8f3db1a82e";
        String expectedHash = "0x4b99a949a24f3dc8dc54b02d51ec0ae4c8bb7018";

        String hexPublicKey2 = "00000";
        String expectedHash2 = "0x0ec7f82b659cc8c6b753f26d4e9ec85bc91c231e";
        testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2);

        // create keyPair with cryptoInterface
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoInterface.SM_TYPE);
        keyPair = cryptoInterface.createKeyPair();
        testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2);

        // test getAddress with generated keyPair
        keyPair.getAddress();
    }

    private void testValidGetAddressForKeyPair(
            CryptoKeyPair keyPair,
            String hexPublicKey,
            String expectedHash,
            String hexPublicKey2,
            String expectedHash2) {
        // case1: input public key is hexed string, without 0x prefix
        testKeyPair(keyPair, hexPublicKey, expectedHash);

        // case2: input public key is bytes, without 0x prefix
        byte[] publicKeyBytes = Hex.decode(hexPublicKey);
        testKeyPair(keyPair, publicKeyBytes, expectedHash);

        // case3: input public key is hexed string, with 0x prefix
        String hexPublicKeyWithPrefix = "0x" + hexPublicKey;
        testKeyPair(keyPair, hexPublicKeyWithPrefix, expectedHash);

        // case4: input public key is bytes, with 0x prefix
        publicKeyBytes = Hex.decode(hexPublicKey);
        testKeyPair(keyPair, publicKeyBytes, expectedHash);

        // case5: input public key is bigInteger
        BigInteger publicKeyValue = new BigInteger(hexPublicKey, 16);
        testKeyPair(keyPair, publicKeyValue, expectedHash);

        // case6: input is 0
        testKeyPair(keyPair, hexPublicKey2, expectedHash2);
        testKeyPair(keyPair, hexPublicKey2 + "00000", expectedHash2);
        testKeyPair(keyPair, new BigInteger("0", 16), expectedHash2);
    }

    private void testKeyPair(CryptoKeyPair keyPair, String publicKey, String expectedAddress) {
        Assert.assertEquals(expectedAddress, keyPair.getAddress(publicKey));
    }

    private void testKeyPair(CryptoKeyPair keyPair, BigInteger publicKey, String expectedAddress) {
        Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(keyPair.getAddress(publicKey)));
    }

    private void testKeyPair(CryptoKeyPair keyPair, byte[] publicKey, String expectedAddress) {
        Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(keyPair.getAddress(publicKey)));
    }

    @Test(expected = KeyPairException.class)
    public void testInvalidCaseForSM2KeyPair() {
        CryptoKeyPair keyPair = (new SM2KeyPair()).generateKeyPair();
        testInvalidPublicKey(keyPair);
    }

    @Test(expected = KeyPairException.class)
    public void testInvalidCaseForECDSAKeyPair() {
        CryptoKeyPair keyPair = (new ECDSAKeyPair()).generateKeyPair();
        testInvalidPublicKey(keyPair);
    }

    @Test(expected = KeyPairException.class)
    public void testInvalidCaseForECDSACryptoInterface() {
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoInterface.ECDSA_TYPE);
        CryptoKeyPair keyPair = cryptoInterface.createKeyPair();
        testInvalidPublicKey(keyPair);
    }

    @Test(expected = KeyPairException.class)
    public void testInvalidCaseForSM2CryptoInterface() {
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoInterface.SM_TYPE);
        CryptoKeyPair keyPair = cryptoInterface.createKeyPair();
        testInvalidPublicKey(keyPair);
    }

    private void testInvalidPublicKey(CryptoKeyPair keyPair) {
        // input is invalid hex string
        keyPair.getAddress("123xyz");
    }

    public void testSignature(Hash hasher, Signature signature, CryptoKeyPair keyPair) {
        String message = "abcde";

        // check valid case
        for (int i = 0; i < 10; i++) {
            message = hasher.hash("abcd----" + Integer.toString(i));
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
            message = hasher.hash("abcd----" + Integer.toString(i));
            String invaidMessage = hasher.hash("abcd---" + Integer.toString(i + 1));
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

    public void testSignature(CryptoInterface signature, CryptoKeyPair keyPair) {
        String message = "abcde";
        // check valid case
        for (int i = 0; i < 10; i++) {
            // Note: the message must be hash
            message = signature.hash("abcd----" + Integer.toString(i));
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
            message = signature.hash("abcd----" + Integer.toString(i));
            String invaidMessage = signature.hash("abcd---" + Integer.toString(i + 1));
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

    @Test
    public void testSignAndVerifyWithKeyManager() {
        String publicKeyPem =
                "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.public.pem";
        KeyManager verifykeyManager =
                new PEMManager(getClass().getClassLoader().getResource(publicKeyPem).getPath());

        String keyPairPem = "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.p12";
        KeyManager signKeyManager =
                new P12Manager(
                        getClass().getClassLoader().getResource(keyPairPem).getPath(), "123456");
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoInterface.ECDSA_TYPE);
        // sign and verify message with keyManager
        for (int i = 0; i < 10; i++) {
            String message = cryptoInterface.hash("abcd----" + Integer.toString(i));
            String signature = cryptoInterface.sign(signKeyManager, message);
            Assert.assertTrue(cryptoInterface.verify(verifykeyManager, message, signature));
            String invalidMessage = cryptoInterface.hash("abcde----" + Integer.toString(i));
            Assert.assertTrue(!cryptoInterface.verify(verifykeyManager, invalidMessage, signature));
        }
    }

    public String getKeyStoreFilePath(
            CryptoInterface cryptoInterface, ConfigOption configOption, String postFix) {
        return configOption.getAccountConfig().getKeyStoreDir()
                + File.separator
                + cryptoInterface.getCryptoKeyPair().getKeyStoreSubDir()
                + File.separator
                + cryptoInterface.getCryptoKeyPair().getAddress()
                + postFix;
    }

    @Test
    public void testSMLoadAndStoreKeyPairWithPEM() throws ConfigException {
        testLoadAndStoreKeyPairWithPEM(CryptoInterface.SM_TYPE);
    }

    @Test
    public void testECDSALoadAndStoreKeyPairWithPEM() throws ConfigException {
        testLoadAndStoreKeyPairWithPEM(CryptoInterface.ECDSA_TYPE);
    }

    @Test
    public void testSMLoadAndStoreKeyPairWithP12() throws ConfigException {
        testLoadAndStoreKeyPairWithP12(CryptoInterface.SM_TYPE);
    }

    @Test
    public void testECDSALoadAndStoreKeyPairWithP12() throws ConfigException {
        testLoadAndStoreKeyPairWithP12(CryptoInterface.ECDSA_TYPE);
    }

    public void testLoadAndStoreKeyPairWithPEM(int cryptoType) throws ConfigException {
        ConfigOption configOption = Config.load(configFile, CryptoInterface.ECDSA_TYPE);
        CryptoInterface cryptoInterface = new CryptoInterface(cryptoType);
        cryptoInterface.getCryptoKeyPair().setConfig(configOption);
        cryptoInterface.getCryptoKeyPair().storeKeyPairWithPemFormat();
        CryptoKeyPair orgKeyPair = cryptoInterface.getCryptoKeyPair();

        // get pem file path
        String pemFilePath =
                getKeyStoreFilePath(cryptoInterface, configOption, CryptoKeyPair.PEM_FILE_POSTFIX);
        // load pem file
        KeyManager pemManager = new PEMManager(pemFilePath);
        CryptoKeyPair decodedCryptoKeyPair = cryptoInterface.createKeyPair(pemManager.getKeyPair());

        System.out.println("PEM   orgKeyPair   pub: " + orgKeyPair.getHexPublicKey());
        System.out.println("PEM decodedKeyPair pub: " + decodedCryptoKeyPair.getHexPublicKey());

        System.out.println("PEM   orgKeyPair   pri: " + orgKeyPair.getHexPrivateKey());
        System.out.println("PEM decodedKeyPair pr: " + decodedCryptoKeyPair.getHexPrivateKey());

        // test sign and verify message with
        String publicPemPath = pemFilePath + ".pub";
        KeyManager verifyKeyManager = new PEMManager(publicPemPath);

        checkSignAndVerifyWithKeyManager(
                pemManager, decodedCryptoKeyPair, verifyKeyManager, cryptoInterface);
    }

    public void testLoadAndStoreKeyPairWithP12(int cryptoType) throws ConfigException {
        ConfigOption configOption = Config.load(configFile, CryptoInterface.ECDSA_TYPE);
        CryptoInterface cryptoInterface = new CryptoInterface(cryptoType);
        cryptoInterface.getCryptoKeyPair().setConfig(configOption);
        String password = "123";
        cryptoInterface.getCryptoKeyPair().storeKeyPairWithP12Format(password);
        CryptoKeyPair orgKeyPair = cryptoInterface.getCryptoKeyPair();

        // get p12 file path
        String p12FilePath =
                getKeyStoreFilePath(cryptoInterface, configOption, CryptoKeyPair.P12_FILE_POSTFIX);
        // load p12 file
        KeyManager p12Manager = new P12Manager(p12FilePath, password);
        CryptoKeyPair decodedCryptoKeyPair = cryptoInterface.createKeyPair(p12Manager.getKeyPair());
        // check the keyPair
        System.out.println("P12   orgKeyPair   pub: " + orgKeyPair.getHexPublicKey());
        System.out.println("P12 decodedKeyPair pub: " + decodedCryptoKeyPair.getHexPublicKey());

        System.out.println("P12   orgKeyPair   pri: " + orgKeyPair.getHexPrivateKey());
        System.out.println("P12 decodedKeyPair pr: " + decodedCryptoKeyPair.getHexPrivateKey());

        Assert.assertTrue(
                orgKeyPair.getHexPrivateKey().equals(decodedCryptoKeyPair.getHexPrivateKey()));
        Assert.assertTrue(
                orgKeyPair.getHexPublicKey().equals(decodedCryptoKeyPair.getHexPublicKey()));

        // test sign and verify message with
        String publicP12Path = p12FilePath + ".pub";
        KeyManager verifyKeyManager = new PEMManager(publicP12Path);
        checkSignAndVerifyWithKeyManager(
                p12Manager, decodedCryptoKeyPair, verifyKeyManager, cryptoInterface);
    }

    private void checkSignAndVerifyWithKeyManager(
            KeyManager pemManager,
            CryptoKeyPair cryptoKeyPair,
            KeyManager verifyKeyManager,
            CryptoInterface cryptoInterface) {
        // sign and verify message with cryptoKeyPair
        for (int i = 0; i < 10; i++) {
            String message = cryptoInterface.hash("abcd----" + Integer.toString(i));
            SignatureResult signature = cryptoInterface.sign(message, cryptoKeyPair);
            Assert.assertTrue(
                    cryptoInterface.verify(
                            cryptoKeyPair.getHexPublicKey(), message, signature.convertToString()));
            String invalidMessage = cryptoInterface.hash("abcde----" + Integer.toString(i));
            Assert.assertTrue(
                    !cryptoInterface.verify(
                            cryptoKeyPair.getHexPublicKey(),
                            invalidMessage,
                            signature.convertToString()));
        }
        for (int i = 0; i < 10; i++) {
            String message = cryptoInterface.hash("abcd----" + Integer.toString(i));
            String signature = cryptoInterface.sign(pemManager, message);
            Assert.assertTrue(cryptoInterface.verify(verifyKeyManager, message, signature));
            String invalidMessage = cryptoInterface.hash("abcde----" + Integer.toString(i));
            Assert.assertTrue(!cryptoInterface.verify(verifyKeyManager, invalidMessage, signature));
        }
    }
}
