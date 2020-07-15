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

import com.webank.pkeysign.service.SM2SignService;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.exceptions.SignatureException;

public class SM2Signature implements Signature {
    public static final SM2SignService sm2SignService = new SM2SignService();

    @Override
    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        String signature = sm2SignService.sign(message, keyPair.getHexPrivateKey());
        if (signature == null) {
            throw new SignatureException("Sign with sm2 failed");
        }
        return new SM2SignatureResult(keyPair.getHexPublicKey(), signature);
    }

    @Override
    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return sign(new String(message), keyPair);
    }

    @Override
    public boolean verify(final String publicKey, final String message, final String signature) {
        return sm2SignService.verify(message, signature, publicKey);
    }

    @Override
    public boolean verify(final String publicKey, final byte[] message, final byte[] signature) {
        return verify(publicKey, new String(message), new String(signature));
    }
}
