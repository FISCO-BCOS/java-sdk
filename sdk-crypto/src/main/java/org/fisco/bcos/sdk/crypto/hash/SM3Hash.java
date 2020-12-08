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
package org.fisco.bcos.sdk.crypto.hash;

import com.webank.wedpr.crypto.CryptoResult;
import com.webank.wedpr.crypto.NativeInterface;
import org.fisco.bcos.sdk.crypto.exceptions.HashException;
import org.fisco.bcos.sdk.utils.Hex;

public class SM3Hash implements Hash {
    @Override
    public String hash(final String inputData) {
        return calculateHash(inputData.getBytes());
    }

    @Override
    public String hashBytes(byte[] inputBytes) {
        return calculateHash(inputBytes);
    }

    @Override
    public byte[] hash(final byte[] inputBytes) {
        // Considering inefficient string conversion, this interface is not recommended
        return Hex.decode(calculateHash(inputBytes));
    }

    private String calculateHash(final byte[] inputBytes) {
        CryptoResult hashResult = NativeInterface.sm3(Hex.toHexString(inputBytes));
        // call sm3 failed
        if (hashResult.wedprErrorMessage != null && !hashResult.wedprErrorMessage.isEmpty()) {
            throw new HashException(
                    "calculate hash with sm3 failed, error message:"
                            + hashResult.wedprErrorMessage);
        }
        return hashResult.hash;
    }
}
