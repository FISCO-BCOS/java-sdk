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

import java.security.KeyPair;

import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.exceptions.LoadKeyStoreException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.v3.crypto.keystore.P12KeyStore;
import org.fisco.bcos.sdk.v3.crypto.keystore.PEMKeyStore;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.junit.Assert;
import org.junit.Test;

public class KeyToolTest {
    @Test
    public void testECDSALoadPEMFile() {
        System.out.println("12");
        String keyStoreFile = "keystore/ecdsa/0x0fc3c4bb89bd90299db4c62be0174c4966286c00.pem";
        CryptoKeyPair cryptoKeyPair =
                testLoadPEMFile(
                        keyStoreFile,
                        CryptoType.ECDSA_TYPE,
                        "0x0fc3c4bb89bd90299db4c62be0174c4966286c00");
        // check the public key and the privateKey
        Assert.assertEquals(
                "dbbfee4f76f5a3bc3dbc2e6127c4a1f50b7614bff4138a44a79aed3d42f67f9c7aa70570205f9b60a5888c6415b6a830012677b4415a79ccd1533fe5637861df",
                cryptoKeyPair.getHexPublicKey());
        Assert.assertEquals(
                "bc516b2600eec3a216f457dc14cf83a01ed22d0fc2149fc911dc2ec486fe57a3",
                cryptoKeyPair.getHexPrivateKey());
    }

    @Test
    public void testSMLoadPEMFile() {
        String keyStoreFile = "keystore/gm/0x40b3558746e8f9a47a474774e8c4a9e67d4e3174.pem";
        CryptoKeyPair cryptoKeyPair =
                testLoadPEMFile(
                        keyStoreFile,
                        CryptoType.SM_TYPE,
                        "0x40b3558746e8f9a47a474774e8c4a9e67d4e3174");
        Assert.assertEquals(
                "3b72cd28244c856d3d89b67d1c5ff22e1f26835bafcd63e9a4ad3424a2a57f2b759149f46c696df08b9d9473686675fc6dade744d0c82bdc5598d759e015fd96",
                cryptoKeyPair.getHexPublicKey());
        Assert.assertEquals(
                "901744c34e2adffc9fd7fb12e8cba2d88a79aaf54be9b4e11660153287489f13",
                cryptoKeyPair.getHexPrivateKey());
    }

    @Test(expected = LoadKeyStoreException.class)
    public void testECDSALoadInvalidPEMFile() {
        String keyStoreFile = "keystore/ecdsa/invalid.pem";
        testLoadPEMFile(
                keyStoreFile, CryptoType.ECDSA_TYPE, "0x0fc3c4bb89bd90299db4c62be0174c4966286c00");
    }

    @Test(expected = LoadKeyStoreException.class)
    public void testSMLoadInvalidPEMFile() {
        String keyStoreFile = "keystore/gm/invalid.pem";
        testLoadPEMFile(
                keyStoreFile, CryptoType.SM_TYPE, "0x40b3558746e8f9a47a474774e8c4a9e67d4e3174");
    }

    @Test
    public void testLoadECDSAP12File() {
        String keyStoreFile = "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.p12";
        CryptoKeyPair cryptoKeyPair =
                testLoadP12File(
                        keyStoreFile,
                        CryptoType.ECDSA_TYPE,
                        "123456",
                        "0x45e14c53197adbcb719d915fb93342c25600faaf");
        Assert.assertEquals(
                "d7b9e00f56d3f79305359fa2d7db166021e73086bdcd2e7a28d6ed27345e1f2ddecf85db7438e8457fd474ef9c4ceb89abb7d5fa60a22f2902ec26dca52ad5e5",
                cryptoKeyPair.getHexPublicKey());
        Assert.assertEquals(
                "c0c8b4d96aa4aefaeeafa157789d528b6010f65059dee796d8757e1171bbcd2c",
                cryptoKeyPair.getHexPrivateKey());
    }

    @Test
    public void testLoadSMP12File() {
        String keyStoreFile = "keystore/gm/0x6f68461309925093236df82b51df630a55d32377.p12";
        CryptoKeyPair cryptoKeyPair =
                testLoadP12File(
                        keyStoreFile,
                        CryptoType.SM_TYPE,
                        "abcd123",
                        "0x6f68461309925093236df82b51df630a55d32377");
        Assert.assertEquals(
                "a809a0176dc24432490697b6ed74995a6716a122a0fa5c73429a259cd73f14995934522288f226a049bbbb803d78f296289bee8fb4f5d7821514e731a57c9f2f",
                cryptoKeyPair.getHexPublicKey());
        Assert.assertEquals(
                "d0cbcdfea24e206688ce6c1a63171a24d9e1e0cf5331151ed5406e07fdb38256",
                cryptoKeyPair.getHexPrivateKey());
    }

    @Test(expected = LoadKeyStoreException.class)
    public void testInvalidECDSAP12Case() {
        // error password
        String keyStoreFile = "keystore/ecdsa/0x45e14c53197adbcb719d915fb93342c25600faaf.p12";
        testLoadP12File(
                keyStoreFile,
                CryptoType.ECDSA_TYPE,
                "13456",
                "0x45e14c53197adbcb719d915fb93342c25600faaf");
    }

    @Test(expected = LoadKeyStoreException.class)
    public void testInvalidSMP12Case() {
        String keyStoreFile = "keystore/gm/0x6f68461309925093236df82b51df630a55d32377.p12";
        testLoadP12File(
                keyStoreFile,
                CryptoType.SM_TYPE,
                "abcd12e",
                "0x6f68461309925093236df82b51df630a55d32377");
    }

    @Test(expected = LoadKeyStoreException.class)
    public void testInvalidP12FileForECDSA() {
        String keyStoreFile = "keystore/ecdsa/invalid.p12";
        testLoadP12File(
                keyStoreFile,
                CryptoType.ECDSA_TYPE,
                "abcd123",
                "0x6f68461309925093236df82b51df630a55d32377");
    }

    @Test(expected = LoadKeyStoreException.class)
    public void testInvalidP12FileForSM() {
        String keyStoreFile = "keystore/gm/invalid.p12";
        testLoadP12File(
                keyStoreFile,
                CryptoType.SM_TYPE,
                "123456",
                "0x45e14c53197adbcb719d915fb93342c25600faaf");
    }

    private String getFilePath(String fileName) {
        return getClass().getClassLoader().getResource(fileName).getPath();
    }

    private CryptoKeyPair testLoadPEMFile(
            String pemFileName, int cryptoType, String expectedAccount) {
        // get KeyPair from the pem
        PEMKeyStore pem = new PEMKeyStore(getFilePath(pemFileName));
        KeyPair keyPair = pem.getKeyPair();
        testSignature(keyPair, cryptoType, expectedAccount);

        pem = new PEMKeyStore(getClass().getClassLoader().getResourceAsStream(pemFileName));
        keyPair = pem.getKeyPair();
        return testSignature(keyPair, cryptoType, expectedAccount);
    }

    private CryptoKeyPair testLoadP12File(
            String p12FileName, int cryptoType, String password, String expectedAccount) {
        P12KeyStore p12 = new P12KeyStore(getFilePath(p12FileName), password);
        KeyPair keyPair = p12.getKeyPair();
        testSignature(keyPair, cryptoType, expectedAccount);

        p12 = new P12KeyStore(getClass().getClassLoader().getResourceAsStream(p12FileName), password);
        keyPair = p12.getKeyPair();
        return testSignature(keyPair, cryptoType, expectedAccount);
    }

    private CryptoKeyPair testSignature(KeyPair keyPair, int cryptoType, String expectedAccount) {
        CryptoSuite cryptoSuite = new CryptoSuite(cryptoType);
        CryptoKeyPair cryptoKeyPair = cryptoSuite.loadKeyPair(keyPair);
        // check account
        Assert.assertEquals(expectedAccount, cryptoKeyPair.getAddress());
        // test signature
        SignatureTest signatureTest = new SignatureTest();
        if(cryptoSuite.cryptoTypeConfig == CryptoType.SM_TYPE) {
            signatureTest.testSMSignature(cryptoSuite, cryptoKeyPair);
        }else if(cryptoSuite.cryptoTypeConfig == CryptoType.ECDSA_TYPE){
            signatureTest.testECDSASignature(cryptoSuite, cryptoKeyPair);
        }
        return cryptoKeyPair;
    }
}
