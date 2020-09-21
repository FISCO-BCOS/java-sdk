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
import org.fisco.bcos.sdk.crypto.exceptions.UnsupportedCryptoTypeException;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

public class HashTest {
    @Test
    public void testCryptoInterfaceForSMHash() {
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoType.SM_TYPE);
        // check sm3 hash for "abcde"
        checkHashWithCryptoInterface(
                cryptoInterface,
                "abcde",
                "afe4ccac5ab7d52bcae36373676215368baf52d3905e1fecbe369cc120e97628");

        // check sm3 hash for "hello"
        checkHashWithCryptoInterface(
                cryptoInterface,
                "hello",
                "becbbfaae6548b8bf0cfcad5a27183cd1be6093b1cceccc303d9c61d0a645268");

        // check sm3 hash for empty string
        checkHashWithCryptoInterface(
                cryptoInterface,
                "",
                "1ab21d8355cfa17f8e61194831e81a8f22bec8c728fefb747ed035eb5082aa2b");
    }

    @Test
    public void testCryptoInterfaceForKeccak256Hash() {
        CryptoInterface cryptoInterface = new CryptoInterface(CryptoType.ECDSA_TYPE);
        // check keccak256 for "abcde"
        checkHashWithCryptoInterface(
                cryptoInterface,
                "abcde",
                "6377c7e66081cb65e473c1b95db5195a27d04a7108b468890224bedbe1a8a6eb");

        // check keccak256 for "hello"
        checkHashWithCryptoInterface(
                cryptoInterface,
                "hello",
                "1c8aff950685c2ed4bc3174f3472287b56d9517b9c948127319a09a7a36deac8");

        // check keccak256 for empty string
        checkHashWithCryptoInterface(
                cryptoInterface,
                "",
                "c5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470");
    }

    @Test(expected = UnsupportedCryptoTypeException.class)
    public void testUnsupportedCryptoType() {
        CryptoInterface cryptoInterface = new CryptoInterface(3);
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

    private void checkHashWithCryptoInterface(
            CryptoInterface cryptoInterface, String message, String expectedHash) {
        String calculatedHash = cryptoInterface.hash(message);
        Assert.assertEquals(true, calculatedHash.equals(expectedHash));
        byte[] calculatedHashBytes = cryptoInterface.hash(message.getBytes());
        Assert.assertEquals(true, Hex.toHexString(calculatedHashBytes).equals(expectedHash));
    }
}
