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
package org.fisco.bcos.sdk.crypto.signature;

import com.webank.wedpr.crypto.CryptoResult;
import com.webank.wedpr.crypto.NativeInterface;
import org.fisco.bcos.sdk.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;

public class SM2Signature implements Signature {
    @Override
    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        CryptoResult signatureResult = NativeInterface.sm2Sign(keyPair.getHexPrivateKey(), message);
        if (signatureResult.wedprErrorMessage != null
                && !signatureResult.wedprErrorMessage.isEmpty()) {
            throw new SignatureException(
                    "Sign with sm2 failed:" + signatureResult.wedprErrorMessage);
        }
        return new SM2SignatureResult(keyPair.getHexPublicKey(), signatureResult.signature);
    }

    @Override
    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return sign(new String(message), keyPair);
    }

    @Override
    public boolean verify(final String publicKey, final String message, final String signature) {
        CryptoResult verifyResult = NativeInterface.sm2verify(publicKey, message, signature);
        if (verifyResult.wedprErrorMessage != null && !verifyResult.wedprErrorMessage.isEmpty()) {
            throw new SignatureException(
                    "Verify with sm2 failed:" + verifyResult.wedprErrorMessage);
        }
        return verifyResult.result;
    }

    @Override
    public boolean verify(final String publicKey, final byte[] message, final byte[] signature) {
        return verify(publicKey, new String(message), new String(signature));
    }
}
