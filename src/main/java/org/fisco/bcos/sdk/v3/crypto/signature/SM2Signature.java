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
package org.fisco.bcos.sdk.v3.crypto.signature;

import com.webank.wedpr.crypto.CryptoResult;
import com.webank.wedpr.crypto.NativeInterface;
import org.fisco.bcos.sdk.v3.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;

public class SM2Signature implements Signature {
    @Override
    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        return new SM2SignatureResult(
                keyPair.getHexPublicKey(), signWithStringSignature(message, keyPair));
    }

    @Override
    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return sign(Hex.toHexString(message), keyPair);
    }

    @Override
    public String signWithStringSignature(final String message, final CryptoKeyPair keyPair) {
        return signMessage(message, keyPair);
    }

    public String signMessage(String message, CryptoKeyPair keyPair) {

        if (!keyPair.getCurveName().equals(CryptoKeyPair.SM2_CURVE_NAME)) {
            throw new SignatureException("sm2 sign with " + keyPair.getCurveName() + " keypair");
        }
        CryptoResult signatureResult =
                NativeInterface.sm2SignFast(
                        keyPair.getHexPrivateKey(),
                        keyPair.getHexPublicKey(),
                        Numeric.cleanHexPrefix(message));
        if (signatureResult.wedprErrorMessage != null
                && !signatureResult.wedprErrorMessage.isEmpty()) {
            throw new SignatureException(
                    "Sign with sm2 failed:" + signatureResult.wedprErrorMessage);
        }
        return signatureResult.signature;
    }

    @Override
    public boolean verify(final String publicKey, final String message, final String signature) {
        return verifyMessage(publicKey, message, signature);
    }

    @Override
    public boolean verify(final String publicKey, final byte[] message, final byte[] signature) {
        return verify(publicKey, Hex.toHexString(message), Hex.toHexString(signature));
    }

    public static boolean verifyMessage(String publicKey, String message, String signature) {
        CryptoResult verifyResult =
                NativeInterface.sm2Verify(publicKey, Numeric.cleanHexPrefix(message), signature);
        if (verifyResult.wedprErrorMessage != null && !verifyResult.wedprErrorMessage.isEmpty()) {
            throw new SignatureException(
                    "Verify with sm2 failed:" + verifyResult.wedprErrorMessage);
        }
        return verifyResult.booleanResult;
    }

    @Override
    public String recoverAddress(final String msgHash, final SignatureResult signature) {
        return ecrecoverSignature(msgHash, signature);
    }

    @Override
    public String recoverAddress(final byte[] msgHash, final SignatureResult signature) {
        return recoverAddress(Hex.toHexString(msgHash), signature);
    }

    public static String ecrecoverSignature(String msgHash, SignatureResult signature) {
        String publicKey = getPubFromSignature(msgHash, signature);
        return SM2KeyPair.getAddressByPublicKey(publicKey);
    }

    @Override
    public String recoverPublicKey(final String msgHash, final SignatureResult signature) {
        return getPubFromSignature(msgHash, signature);
    }

    @Override
    public String recoverPublicKey(final byte[] msgHash, final SignatureResult signature) {
        return recoverPublicKey(Hex.toHexString(msgHash), signature);
    }

    public static String getPubFromSignature(String msgHash, SignatureResult signature) {
        return Hex.toHexString(signature.getPub());
    }
}
