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
package org.fisco.bcos.sdk.v3.crypto;

import org.fisco.bcos.sdk.v3.crypto.exceptions.UnsupportedCryptoTypeException;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

public class HashTest {
    @Test
    public void testCryptoSuiteForSMHash() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // check sm3 hash for "abcde"
        checkHashWithCryptoSuite(
                cryptoSuite,
                "abcde",
                "afe4ccac5ab7d52bcae36373676215368baf52d3905e1fecbe369cc120e97628");

        // check sm3 hash for "hello"
        checkHashWithCryptoSuite(
                cryptoSuite,
                "hello",
                "becbbfaae6548b8bf0cfcad5a27183cd1be6093b1cceccc303d9c61d0a645268");

        // check sm3 hash for empty string
        checkHashWithCryptoSuite(
                cryptoSuite,
                "",
                "1ab21d8355cfa17f8e61194831e81a8f22bec8c728fefb747ed035eb5082aa2b");
    }

    @Test
    public void testCryptoSuiteForKeccak256Hash() {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // check keccak256 for "abcde"
        checkHashWithCryptoSuite(
                cryptoSuite,
                "abcde",
                "6377c7e66081cb65e473c1b95db5195a27d04a7108b468890224bedbe1a8a6eb");

        // check keccak256 for "hello"
        checkHashWithCryptoSuite(
                cryptoSuite,
                "hello",
                "1c8aff950685c2ed4bc3174f3472287b56d9517b9c948127319a09a7a36deac8");

        // check keccak256 for empty string
        checkHashWithCryptoSuite(
                cryptoSuite,
                "",
                "c5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470");
    }

    @Test(expected = UnsupportedCryptoTypeException.class)
    public void testUnsupportedCryptoType() {
        new CryptoSuite(3);
    }

    @Test
    public void testKeccak256() {
        Hash hasher = new Keccak256();
        testKeccak256(hasher);
    }

    @Test
    public void testSM3() {
        Hash sm3Hasher = new SM3Hash();
        testSM3(sm3Hasher);
    }

    private void testKeccak256(Hash hasher) {

        // check keccak256 for "abcde"
        checkHash(
                hasher,
                "abcde",
                "6377c7e66081cb65e473c1b95db5195a27d04a7108b468890224bedbe1a8a6eb");

        // check keccak256 for "hello"
        checkHash(
                hasher,
                "hello",
                "1c8aff950685c2ed4bc3174f3472287b56d9517b9c948127319a09a7a36deac8");

        // check keccak256 for empty string
        checkHash(hasher, "", "c5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470");
    }

    private void testSM3(Hash hasher) {
        // check sm3 hash for "abcde"
        checkHash(
                hasher,
                "abcde",
                "afe4ccac5ab7d52bcae36373676215368baf52d3905e1fecbe369cc120e97628");

        // check sm3 hash for "hello"
        checkHash(
                hasher,
                "hello",
                "becbbfaae6548b8bf0cfcad5a27183cd1be6093b1cceccc303d9c61d0a645268");

        // check sm3 hash for empty string
        checkHash(hasher, "", "1ab21d8355cfa17f8e61194831e81a8f22bec8c728fefb747ed035eb5082aa2b");
    }

    private void checkHash(Hash hasher, String message, String expectedHash) {
        String calculatedHash = hasher.hash(message);
        Assert.assertEquals(true, calculatedHash.equals(expectedHash));
        byte[] calculatedHashBytes = hasher.hash(message.getBytes());
        Assert.assertEquals(true, Hex.toHexString(calculatedHashBytes).equals(expectedHash));
    }

    private void checkHashWithCryptoSuite(
            CryptoSuite cryptoSuite, String message, String expectedHash) {
        String calculatedHash = cryptoSuite.hash(message);
        Assert.assertEquals(true, calculatedHash.equals(expectedHash));
        byte[] calculatedHashBytes = cryptoSuite.hash(message.getBytes());
        Assert.assertEquals(true, Hex.toHexString(calculatedHashBytes).equals(expectedHash));
    }
}
