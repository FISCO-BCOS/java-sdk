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
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;

public class ECDSASignature implements Signature {
    private static int INPUT_MESSAGE_SIZE_IN_HEX = 64;

    @Override
    public SignatureResult sign(final String message, final CryptoKeyPair keyPair) {
        // convert signature string to SignatureResult struct
        return new ECDSASignatureResult(signWithStringSignature(message, keyPair));
    }

    @Override
    public SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair) {
        return sign(Hex.toHexString(message), keyPair);
    }

    private static void checkInputMessage(final String message) {
        if (message.length() != INPUT_MESSAGE_SIZE_IN_HEX) {
            throw new SignatureException(
                    "Invalid input message " + message + ", must be a hex string of length 64");
        }
    }

    public static String signMessage(final String message, final CryptoKeyPair keyPair) {

        if (!keyPair.getCurveName().equals(CryptoKeyPair.ECDSA_CURVE_NAME)) {
            throw new SignatureException("ecdsa sign with " + keyPair.getCurveName() + " keypair");
        }

        String inputMessage = Numeric.cleanHexPrefix(message);
        checkInputMessage(inputMessage);
        CryptoResult signatureResult =
                NativeInterface.secp256k1Sign(keyPair.getHexPrivateKey(), inputMessage);
        // call secp256k1Sign failed
        if (signatureResult.wedprErrorMessage != null
                && !signatureResult.wedprErrorMessage.isEmpty()) {
            throw new SignatureException(
                    "Sign with secp256k1 failed:" + signatureResult.wedprErrorMessage);
        }
        // convert signature string to SignatureResult struct
        return signatureResult.signature;
    }

    @Override
    public String signWithStringSignature(final String message, final CryptoKeyPair keyPair) {
        return signMessage(message, keyPair);
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
        String inputMessage = Numeric.cleanHexPrefix(message);
        checkInputMessage(inputMessage);
        String hexPubKeyWithPrefix =
                Numeric.getHexKeyWithPrefix(
                        publicKey,
                        CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                        CryptoKeyPair.PUBLIC_KEY_LENGTH_IN_HEX);
        CryptoResult verifyResult =
                NativeInterface.secp256k1Verify(hexPubKeyWithPrefix, inputMessage, signature);
        // call secp256k1verify failed
        if (verifyResult.wedprErrorMessage != null && !verifyResult.wedprErrorMessage.isEmpty()) {
            throw new SignatureException(
                    "Verify with secp256k1 failed:" + verifyResult.wedprErrorMessage);
        }
        return verifyResult.booleanResult;
    }
}
