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

import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.exceptions.KeyPairException;
import org.fisco.bcos.sdk.v3.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.v3.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.v3.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.v3.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignature;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.Signature;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class SignatureTest {
    private static final String configFile =
            SignatureTest.class
                    .getClassLoader()
                    .getResource("config/config-example.toml")
                    .getPath();

    @Test
    public void testCryptoSuiteForECDSA() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // generate keyPair
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        CryptoSuite cryptoSuite2 = new CryptoSuite(CryptoType.ECDSA_TYPE, keyPair);
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPublicKey(), cryptoSuite.getCryptoKeyPair().getHexPublicKey());
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPrivateKey(), cryptoSuite.getCryptoKeyPair().getHexPrivateKey());

        cryptoSuite2 = new CryptoSuite(CryptoType.ECDSA_TYPE, cryptoSuite.getCryptoKeyPair().getHexPrivateKey());
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPublicKey(), cryptoSuite.getCryptoKeyPair().getHexPublicKey());
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPrivateKey(), cryptoSuite.getCryptoKeyPair().getHexPrivateKey());

        // test signature
        this.testECDSASignature(cryptoSuite, keyPair);

        // load KeyPair from the given privateKey
        String privateKeyStr =
                "47300381232944006945664493109832654111051142806262820216166278362539860431476";
        String publicKeyStr =
                "2179819159336280954262570523402774481036769289289277534998346117714415641803934346338726829054711133487295949018624582253372411779380507548447040213240521";
        String hexedPublicKey = new BigInteger(publicKeyStr).toString(16);
        BigInteger privateKey = new BigInteger(privateKeyStr);
        keyPair = cryptoSuite.getKeyPairFactory().createKeyPair(privateKey);
        // check publicKey
        System.out.println("hexedPublicKey: " + hexedPublicKey);
        System.out.println("keyPair.getHexPublicKey(): " + keyPair.getHexPublicKey());
        System.out.println("keyPair.getHexPrivate(): " + keyPair.getHexPrivateKey());
        Assert.assertEquals(hexedPublicKey, keyPair.getHexPublicKey());
        this.testECDSASignature(cryptoSuite, keyPair);

        String hexedPrivateKeyStr = "bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd";
        keyPair = cryptoSuite.getKeyPairFactory().createKeyPair(hexedPrivateKeyStr);
        this.testECDSASignature(cryptoSuite, keyPair);
    }

    @Test
    public void testCryptoSuiteForSM2() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // generate keyPair
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();

        CryptoSuite cryptoSuite2 = new CryptoSuite(CryptoType.SM_TYPE, keyPair);
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPublicKey(), cryptoSuite.getCryptoKeyPair().getHexPublicKey());
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPrivateKey(), cryptoSuite.getCryptoKeyPair().getHexPrivateKey());

        cryptoSuite2 = new CryptoSuite(CryptoType.SM_TYPE, cryptoSuite.getCryptoKeyPair().getHexPrivateKey());
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPublicKey(), cryptoSuite.getCryptoKeyPair().getHexPublicKey());
        Assert.assertEquals(cryptoSuite2.getCryptoKeyPair().getHexPrivateKey(), cryptoSuite.getCryptoKeyPair().getHexPrivateKey());
        // test signature
        this.testSMSignature(cryptoSuite, keyPair);
    }

    @Test
    public void testECDSASignature() {
        ECDSASignature ecdsaSignature = new ECDSASignature();
        CryptoKeyPair keyPair = (new ECDSAKeyPair()).generateKeyPair();
        Hash hasher = new Keccak256();
        this.testSignature(hasher, ecdsaSignature, keyPair);

        keyPair = ECDSAKeyPair.createKeyPair();
        this.testSignature(hasher, ecdsaSignature, keyPair);
    }

    @Test
    public void testSM2Signature() {
        SM2Signature sm2Signature = new SM2Signature();
        CryptoKeyPair keyPair = (new SM2KeyPair()).generateKeyPair();
        Hash hasher = new SM3Hash();
        this.testSignature(hasher, sm2Signature, keyPair);

        keyPair = SM2KeyPair.createKeyPair();
        this.testSignature(hasher, sm2Signature, keyPair);
    }

    @Test
    public void testValidGetAddressForECDSA() {
        CryptoKeyPair keyPair = (new ECDSAKeyPair()).generateKeyPair();
        String hexPublicKey =
                "77a8f8d2f786f079bd661e774da3a9f430c76b9acbcd71f9976bff7456bb136a80cb97335cc929a531791970f8ce10c0ca6ffb391e9ef241a48cbd8f3db1a82e";
        String expectedHash = "0xdeaa5343178c2be2cb5e9b13000ed951e302c15d";

        String hexPublicKey2 = "00000";
        String expectedHash2 = "0x3f17f1962b36e491b30a40b2405849e597ba5fb5";
        this.testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2, false);

        // create keyPair with cryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        keyPair = cryptoSuite.getCryptoKeyPair();
        this.testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2, false);

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
        this.testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2, true);

        // create keyPair with cryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        keyPair = cryptoSuite.getCryptoKeyPair();
        this.testValidGetAddressForKeyPair(
                keyPair, hexPublicKey, expectedHash, hexPublicKey2, expectedHash2, true);

        // test getAddress with generated keyPair
        keyPair.getAddress();
    }

    private void testValidGetAddressForKeyPair(
            CryptoKeyPair keyPair,
            String hexPublicKey,
            String expectedHash,
            String hexPublicKey2,
            String expectedHash2, boolean smCrypto) {
        // case1: input public key is hexed string, without 0x prefix
        this.testKeyPair(keyPair, hexPublicKey, expectedHash, smCrypto);

        // case2: input public key is bytes, without 0x prefix
        byte[] publicKeyBytes = Hex.decode(hexPublicKey);
        this.testKeyPair(keyPair, publicKeyBytes, expectedHash);

        // case3: input public key is hexed string, with 0x prefix
        String hexPublicKeyWithPrefix = "0x" + hexPublicKey;
        this.testKeyPair(keyPair, hexPublicKeyWithPrefix, expectedHash, smCrypto);

        // case4: input public key is bytes, with 0x prefix
        publicKeyBytes = Hex.decode(hexPublicKey);
        this.testKeyPair(keyPair, publicKeyBytes, expectedHash);

        // case5: input public key is bigInteger
        BigInteger publicKeyValue = new BigInteger(hexPublicKey, 16);
        this.testKeyPair(keyPair, publicKeyValue, expectedHash);

        // case6: input is 0
        this.testKeyPair(keyPair, hexPublicKey2, expectedHash2, smCrypto);
        this.testKeyPair(keyPair, hexPublicKey2 + "00000", expectedHash2, smCrypto);
        this.testKeyPair(keyPair, new BigInteger("0", 16), expectedHash2);
    }

    private void testKeyPair(CryptoKeyPair keyPair, String publicKey, String expectedAddress, boolean smCrypto) {
        Assert.assertEquals(expectedAddress, keyPair.getAddress(publicKey));
        String uncompressedPublicKey = publicKey;
        boolean contain0x = false;
        if (publicKey.startsWith("0x")) {
            contain0x = true;
            uncompressedPublicKey = publicKey.substring(2);
        }
        // Hexadecimal public key length is less than 128, add 0 in front
        if (uncompressedPublicKey.length() < CryptoKeyPair.PUBLIC_KEY_LENGTH_IN_HEX) {
            uncompressedPublicKey =
                    StringUtils.zeros(CryptoKeyPair.PUBLIC_KEY_LENGTH_IN_HEX - uncompressedPublicKey.length())
                            + uncompressedPublicKey;
        }
        String prefix = "04";
        if (contain0x) {
            prefix = "0x04";
        }
        Assert.assertEquals(expectedAddress, keyPair.getAddress(prefix + uncompressedPublicKey));
        // convert the publicKey into BigInteger
        BigInteger uncompressedPublicKeyValue = new BigInteger(uncompressedPublicKey, 16);
        Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(keyPair.getAddress(uncompressedPublicKeyValue)));
        // convert the publicKey into bytes
        byte[] uncompressedPublicKeyBytes = Hex.decode(Numeric.cleanHexPrefix(uncompressedPublicKey));
        Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(keyPair.getAddress(uncompressedPublicKeyBytes)));

        if (smCrypto) {
            Assert.assertEquals(expectedAddress, SM2KeyPair.getAddressByPublicKey(publicKey));
            Assert.assertEquals(expectedAddress, SM2KeyPair.getAddressByPublicKey(uncompressedPublicKey));
            Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(SM2KeyPair.getAddressByPublicKey(uncompressedPublicKeyValue)));
            Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(SM2KeyPair.getAddressByPublicKey(uncompressedPublicKeyBytes)));

        } else {
            Assert.assertEquals(expectedAddress, ECDSAKeyPair.getAddressByPublicKey(publicKey));
            Assert.assertEquals(expectedAddress, ECDSAKeyPair.getAddressByPublicKey(uncompressedPublicKey));
            Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(ECDSAKeyPair.getAddressByPublicKey(uncompressedPublicKeyValue)));
            Assert.assertEquals(expectedAddress, "0x" + Hex.toHexString(ECDSAKeyPair.getAddressByPublicKey(uncompressedPublicKeyBytes)));
        }
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
        this.testInvalidPublicKey(keyPair);
    }

    @Test(expected = KeyPairException.class)
    public void testInvalidCaseForECDSAKeyPair() {
        CryptoKeyPair keyPair = (new ECDSAKeyPair()).generateKeyPair();
        this.testInvalidPublicKey(keyPair);
    }

    @Test(expected = KeyPairException.class)
    public void testInvalidCaseForECDSACryptoSuite() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        this.testInvalidPublicKey(keyPair);
    }

    @Test(expected = KeyPairException.class)
    public void testInvalidCaseForSM2CryptoSuite() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        CryptoKeyPair keyPair = cryptoSuite.getCryptoKeyPair();
        this.testInvalidPublicKey(keyPair);
    }

    private void testInvalidPublicKey(CryptoKeyPair keyPair) {
        // input is invalid hex string
        keyPair.getAddress("123xyz");
    }

    public void testSignature(Hash hasher, Signature signature, CryptoKeyPair keyPair) {
        String message = "abcde";
        byte[] messageBytes = message.getBytes();
        // check valid case
        for (int i = 0; i < 10; i++) {
            String plainText = "abcd----" + i;
            message = hasher.hash(plainText);
            messageBytes = hasher.hash(plainText.getBytes());
            Assert.assertTrue(message.equals(Hex.toHexString(messageBytes)));
            // sign
            SignatureResult signResult = signature.sign(message, keyPair);
            // verify
            Assert.assertTrue(
                    signature.verify(
                            keyPair.getHexPublicKey(), message, signResult.convertToString()));
            String hexMessageWithPrefix = "0x" + message;
            // sign
            signResult = signature.sign(hexMessageWithPrefix, keyPair);
            Assert.assertTrue(
                    signature.verify(
                            keyPair.getHexPublicKey(), hexMessageWithPrefix, signResult.convertToString()));
            //verify
            String hexPublicKeyWithoutPrefix = keyPair.getHexPublicKey();
            Assert.assertTrue(
                    signature.verify(
                            hexPublicKeyWithoutPrefix, message, signResult.convertToString()));
            signResult = signature.sign(messageBytes, keyPair);
            Assert.assertTrue(
                    signature.verify(
                            keyPair.getHexPublicKey(), message, signResult.convertToString()));
        }

//        // check invalid case
//        for (int i = 0; i < 10; i++) {
//            message = hasher.hash("abcd----" + Integer.toString(i));
//            String plainText = "abcd---" + Integer.toString(i + 1);
//            String invalidMessage = hasher.hash(plainText);
//            byte[] invalidBytes = hasher.hash(plainText.getBytes());
//            Assert.assertEquals(invalidMessage, Hex.toHexString(invalidBytes));
//            // sign
//            SignatureResult signResult = signature.sign(message, keyPair);
//            // verify
//            Assert.assertEquals(
//                    false,
//                    signature.verify(
//                            keyPair.getHexPublicKey(),
//                            invalidMessage,
//                            signResult.convertToString()));
//            signResult = signature.sign(messageBytes, keyPair);
//            Assert.assertFalse(signature.verify(
//                    keyPair.getHexPublicKey(),
//                    invalidMessage,
//                    signResult.convertToString()));
//        }

        // invalid input
        try {
            String invalidMessage = "0xb3b9ce5a0725c1457b8c7872d05accb3887ecc09a50dc7619b53837e4d9f";
            SignatureResult signResult = signature.sign(invalidMessage, keyPair);
        } catch (SignatureException e) {
            System.out.println("Sign error for " + e.getMessage());
        }

        try {
            String invalidMessage = "";
            for (int i = 0; i < 64; i++) {
                invalidMessage += "1";
            }
            SignatureResult signResult = signature.sign(invalidMessage, keyPair);
        } catch (SignatureException e) {
            System.out.println("Sign error for " + e.getMessage());
        }
    }

    public void testECDSASignature(CryptoSuite cryptoSuite, CryptoKeyPair keyPair) {
        String message = "abcde";
        byte[] messageBytes = message.getBytes();
        // check valid case
        for (int i = 0; i < 10; i++) {
            // Note: the message must be hash
            String plainText = "abcd----" + i;
            message = cryptoSuite.hash(plainText);
            messageBytes = cryptoSuite.hash(plainText.getBytes());
            Assert.assertEquals(message, Hex.toHexString(messageBytes));
            // sign
            ECDSASignatureResult signatureResult = (ECDSASignatureResult) cryptoSuite.sign(message, keyPair);
            //ecrecover
            Assert.assertEquals(keyPair.getAddress(), cryptoSuite.ecrecover(message, signatureResult));
            // verify
            Assert.assertTrue(
                    cryptoSuite.verify(
                            keyPair.getHexPublicKey(), message, signatureResult.convertToString()));
            signatureResult = (ECDSASignatureResult) cryptoSuite.sign(messageBytes, keyPair);
            Assert.assertTrue(
                    cryptoSuite.verify(
                            keyPair.getHexPublicKey(), message, signatureResult.convertToString()));

            // new sign
            ECDSASignatureResult newSign = new ECDSASignatureResult(signatureResult.getV(), signatureResult.getR(), signatureResult.getS());
            Assert.assertEquals(keyPair.getAddress(), cryptoSuite.ecrecover(message, newSign));
            Assert.assertTrue(
                    cryptoSuite.verify(
                            keyPair.getHexPublicKey(), message, newSign.convertToString()));
            String completeSignStr = Hex.toHexString(newSign.encode());
            Assert.assertEquals(completeSignStr, signatureResult.toString());
        }

        // check invalid case
        // Note: it will cause wedpr stderr
//        for (int i = 0; i < 10; i++) {
//            message = cryptoSuite.hash("abcd----" + i);
//            String plainText = "abcd---" + (i + 1);
//            String invalidMessage = cryptoSuite.hash(plainText);
//            byte[] invalidMessageBytes = cryptoSuite.hash(plainText.getBytes());
//            Assert.assertEquals(invalidMessage, Hex.toHexString(invalidMessageBytes));
//            // sign
//            SignatureResult signResult = cryptoSuite.sign(message, keyPair);
//            // verify
//            Assert.assertFalse(cryptoSuite.verify(
//                    keyPair.getHexPublicKey(),
//                    invalidMessage,
//                    signResult.convertToString()));
//            signResult = cryptoSuite.sign(messageBytes, keyPair);
//            Assert.assertFalse(cryptoSuite.verify(
//                    keyPair.getHexPublicKey(),
//                    invalidMessage,
//                    signResult.convertToString()));
//        }
    }

    public void testSMSignature(CryptoSuite cryptoSuite, CryptoKeyPair keyPair) {
        String message = "abcde";
        byte[] messageBytes = message.getBytes();
        // check valid case
        for (int i = 0; i < 10; i++) {
            // Note: the message must be hash
            String plainText = "abcd----" + Integer.toString(i);
            message = cryptoSuite.hash(plainText);
            messageBytes = cryptoSuite.hash(plainText.getBytes());
            Assert.assertEquals(message, Hex.toHexString(messageBytes));
            // sign
            SM2SignatureResult signatureResult = (SM2SignatureResult) cryptoSuite.sign(message, keyPair);
            //ecrecover
            Assert.assertEquals(keyPair.getAddress(), cryptoSuite.ecrecover(message, signatureResult));
            // verify
            Assert.assertTrue(
                    cryptoSuite.verify(
                            keyPair.getHexPublicKey(), message, signatureResult.convertToString()));
            signatureResult = (SM2SignatureResult) cryptoSuite.sign(messageBytes, keyPair);
            Assert.assertTrue(
                    cryptoSuite.verify(
                            keyPair.getHexPublicKey(), message, signatureResult.convertToString()));

            // new sign
            SM2SignatureResult newSign = new SM2SignatureResult(signatureResult.getPub(), signatureResult.getR(), signatureResult.getS());
            Assert.assertEquals(keyPair.getAddress(), cryptoSuite.ecrecover(message, newSign));
            Assert.assertTrue(
                    cryptoSuite.verify(
                            keyPair.getHexPublicKey(), message, newSign.convertToString()));
            String completeSignStr = Hex.toHexString(newSign.encode());
            Assert.assertEquals(completeSignStr.substring(0,128), signatureResult.toString());
            Assert.assertEquals(completeSignStr.substring(128), Hex.toHexString(signatureResult.getPub()));
        }

        // check invalid case
        for (int i = 0; i < 10; i++) {
            message = cryptoSuite.hash("abcd----" + Integer.toString(i));
            String plainText = "abcd---" + Integer.toString(i + 1);
            String invalidMessage = cryptoSuite.hash(plainText);
            byte[] invalidMessageBytes = cryptoSuite.hash(plainText.getBytes());
            Assert.assertEquals(invalidMessage, Hex.toHexString(invalidMessageBytes));
            // sign
            SignatureResult signResult = cryptoSuite.sign(message, keyPair);
            // verify
            Assert.assertFalse(cryptoSuite.verify(
                    keyPair.getHexPublicKey(),
                    invalidMessage,
                    signResult.convertToString()));
//            signResult = cryptoSuite.sign(messageBytes, keyPair);
//            Assert.assertFalse(cryptoSuite.verify(
//                    keyPair.getHexPublicKey(),
//                    invalidMessage,
//                    signResult.convertToString()));
        }
    }

    @Test
    public void testSignAndVerifyWithKeyManager() {
        String publicKeyPem =
                "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.public.pem";
        KeyTool verifykeyTool =
                new PEMKeyStore(this.getClass().getClassLoader().getResource(publicKeyPem).getPath());

        String keyPairPem = "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.p12";
        KeyTool signKeyTool =
                new P12KeyStore(
                        this.getClass().getClassLoader().getResource(keyPairPem).getPath(), "123456");
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // sign and verify message with keyManager
        for (int i = 0; i < 10; i++) {
            String message = cryptoSuite.hash("abcd----" + i);
            String signature = cryptoSuite.sign(signKeyTool, message);
            Assert.assertTrue(cryptoSuite.verify(verifykeyTool, message, signature));
//            String invalidMessage = cryptoSuite.hash("abcde----" + i);
//            Assert.assertFalse(cryptoSuite.verify(verifykeyTool, invalidMessage, signature));
        }
    }

    public String getKeyStoreFilePath(
            CryptoSuite cryptoSuite, ConfigOption configOption, String postFix) {
        return configOption.getAccountConfig().getKeyStoreDir()
                + "/"
                + cryptoSuite.getCryptoKeyPair().getKeyStoreSubDir()
                + "/"
                + cryptoSuite.getCryptoKeyPair().getAddress()
                + postFix;
    }

    @Test
    public void testSMLoadAndStoreKeyPairWithPEM() throws ConfigException {
        this.testLoadAndStoreKeyPairWithPEM(CryptoType.SM_TYPE);
    }

    @Test
    public void testECDSALoadAndStoreKeyPairWithPEM() throws ConfigException {
        this.testLoadAndStoreKeyPairWithPEM(CryptoType.ECDSA_TYPE);
    }

    @Test
    public void testSMLoadAndStoreKeyPairWithP12() throws ConfigException {
        this.testLoadAndStoreKeyPairWithP12(CryptoType.SM_TYPE);
    }

    @Test
    public void testECDSALoadAndStoreKeyPairWithP12() throws ConfigException {
        this.testLoadAndStoreKeyPairWithP12(CryptoType.ECDSA_TYPE);
    }

    public void testLoadAndStoreKeyPairWithPEM(int cryptoType) throws ConfigException {
        ConfigOption configOption = Config.load(configFile);
        CryptoSuite cryptoSuite = new CryptoSuite(cryptoType);
        cryptoSuite.getCryptoKeyPair().setConfig(configOption);
        cryptoSuite.getCryptoKeyPair().storeKeyPairWithPemFormat();
        CryptoKeyPair orgKeyPair = cryptoSuite.getCryptoKeyPair();

        // get pem file path
        String pemFilePath =
                this.getKeyStoreFilePath(cryptoSuite, configOption, CryptoKeyPair.PEM_FILE_POSTFIX);
        // load pem file
        KeyTool pemManager = new PEMKeyStore(pemFilePath);
        CryptoKeyPair decodedCryptoKeyPair = cryptoSuite.loadKeyPair(pemManager.getKeyPair());

//        System.out.println("PEM   orgKeyPair   pub: " + orgKeyPair.getHexPublicKey());
//        System.out.println("PEM decodedKeyPair pub: " + decodedCryptoKeyPair.getHexPublicKey());
//        System.out.println("PEM   orgKeyPair   pri: " + orgKeyPair.getHexPrivateKey());
//        System.out.println("PEM decodedKeyPair pr: " + decodedCryptoKeyPair.getHexPrivateKey());

        // test sign and verify message with
        String publicPemPath = pemFilePath + ".pub";
        KeyTool verifyKeyTool = new PEMKeyStore(publicPemPath);

        this.checkSignAndVerifyWithKeyManager(
                pemManager, decodedCryptoKeyPair, verifyKeyTool, cryptoSuite);
    }

    public void testLoadAndStoreKeyPairWithP12(int cryptoType) throws ConfigException {
        ConfigOption configOption = Config.load(configFile);
        CryptoSuite cryptoSuite = new CryptoSuite(cryptoType);
        cryptoSuite.getCryptoKeyPair().setConfig(configOption);
        String password = "123";
        cryptoSuite.getCryptoKeyPair().storeKeyPairWithP12Format(password);
        CryptoKeyPair orgKeyPair = cryptoSuite.getCryptoKeyPair();

        // get p12 file path
        String p12FilePath =
                this.getKeyStoreFilePath(cryptoSuite, configOption, CryptoKeyPair.P12_FILE_POSTFIX);
        // load p12 file
        KeyTool p12Manager = new P12KeyStore(p12FilePath, password);
        CryptoKeyPair decodedCryptoKeyPair = cryptoSuite.loadKeyPair(p12Manager.getKeyPair());
        // check the keyPair
//        System.out.println("P12   orgKeyPair   pub: " + orgKeyPair.getHexPublicKey());
//        System.out.println("P12 decodedKeyPair pub: " + decodedCryptoKeyPair.getHexPublicKey());
//        System.out.println("P12   orgKeyPair   pri: " + orgKeyPair.getHexPrivateKey());
//        System.out.println("P12 decodedKeyPair pr: " + decodedCryptoKeyPair.getHexPrivateKey());

        Assert.assertEquals(orgKeyPair.getHexPrivateKey(), decodedCryptoKeyPair.getHexPrivateKey());
        Assert.assertEquals(orgKeyPair.getHexPublicKey(), decodedCryptoKeyPair.getHexPublicKey());

        // test sign and verify message with
        String publicP12Path = p12FilePath + ".pub";
        KeyTool verifyKeyTool = new PEMKeyStore(publicP12Path);
        this.checkSignAndVerifyWithKeyManager(
                p12Manager, decodedCryptoKeyPair, verifyKeyTool, cryptoSuite);
    }

    private void checkSignAndVerifyWithKeyManager(
            KeyTool pemManager,
            CryptoKeyPair cryptoKeyPair,
            KeyTool verifyKeyTool,
            CryptoSuite cryptoSuite) {
        // sign and verify message with cryptoKeyPair
        for (int i = 0; i < 10; i++) {
            String message = cryptoSuite.hash("abcd----" + i);
            SignatureResult signature = cryptoSuite.sign(message, cryptoKeyPair);
            Assert.assertTrue(
                    cryptoSuite.verify(
                            cryptoKeyPair.getHexPublicKey(), message, signature.convertToString()));

            Assert.assertTrue(
                    cryptoSuite.verify(
                            cryptoKeyPair.getHexPublicKey(),
                            Hex.decode(message),
                            Hex.decode(signature.convertToString())));

//            String invalidMessage = cryptoSuite.hash("abcde----" + i);
//            Assert.assertFalse(cryptoSuite.verify(
//                    cryptoKeyPair.getHexPublicKey(),
//                    invalidMessage,
//                    signature.convertToString()));
        }
        for (int i = 0; i < 10; i++) {
            String message = cryptoSuite.hash("abcd----" + i);
            String signature = cryptoSuite.sign(pemManager, message);
            Assert.assertTrue(cryptoSuite.verify(verifyKeyTool, message, signature));
            Assert.assertTrue(
                    cryptoSuite.verify(
                            verifyKeyTool, Hex.decode(message), Hex.decode(signature)));
//            String invalidMessage = cryptoSuite.hash("abcde----" + i);
//            Assert.assertFalse(cryptoSuite.verify(verifyKeyTool, invalidMessage, signature));
        }
    }
}
