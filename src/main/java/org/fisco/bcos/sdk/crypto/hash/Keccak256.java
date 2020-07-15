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
import org.fisco.bcos.sdk.exceptions.HashException;
import org.fisco.bcos.sdk.utils.Hex;

public class Keccak256 implements Hash {

    @Override
    public String hash(final String inputData) {
        return calculateHash(inputData.getBytes());
    }

    @Override
    public byte[] hash(final byte[] inputBytes) {
        return Hex.decode(calculateHash(inputBytes));
    }

    private String calculateHash(final byte[] inputBytes) {
        // Note: the exceptions should be handled by the caller
        CryptoResult hashResult = NativeInterface.keccak256(Hex.toHexString(inputBytes));
        if (hashResult.wedprErrorMessage != null && !hashResult.wedprErrorMessage.isEmpty()) {
            throw new HashException(
                    "Calculate hash with keccak256 failed! error message:"
                            + hashResult.wedprErrorMessage);
        }
        return hashResult.hash;
    }
}
