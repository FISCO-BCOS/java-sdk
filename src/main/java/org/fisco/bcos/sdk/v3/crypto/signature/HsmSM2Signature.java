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

import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.signature.SignatureJniObj;
import org.fisco.bcos.sdk.v3.crypto.exceptions.SignatureException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.HsmSM2KeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmSM2Signature implements Signature {
    private static final Logger logger = LoggerFactory.getLogger(HsmSM2Signature.class);

    public static final int SIGNATURE_R_AND_S_LENGTH = 64;
    private String hsmLibPath;

    public String getHsmLibPath() {
        return hsmLibPath;
    }

    public void setHsmLibPath(String hsmLibPath) {
        this.hsmLibPath = hsmLibPath;
    }

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
        byte[] signMessageBytes = signMessage(message, keyPair);
        return Hex.toHexString(signMessageBytes, 0, SIGNATURE_R_AND_S_LENGTH);
    }

    public byte[] signMessage(String message, CryptoKeyPair keyPair) {
        if (!keyPair.getCurveName().equals(CryptoKeyPair.SM2_CURVE_NAME)) {
            throw new SignatureException(
                    "hsm sm2 sign with " + keyPair.getCurveName() + " keypair");
        }

        HsmSM2KeyPair hsmSM2KeyPair = (HsmSM2KeyPair) keyPair;
        try {
            return SignatureJniObj.sign(
                    hsmSM2KeyPair.getJniKeyPair(),
                    Numeric.cleanHexPrefix(message),
                    hsmSM2KeyPair.getHsmLibPath());

        } catch (JniException e) {
            logger.error("Sign with hsm sm2 failed, jni e: ", e);
            return null;
        }
    }

    @Override
    public boolean verify(final String publicKey, final String message, final String signature) {
        return verifyMessage(publicKey, message, signature);
    }

    @Override
    public boolean verify(final String publicKey, final byte[] message, final byte[] signature) {
        return verify(publicKey, Hex.toHexString(message), Hex.toHexString(signature));
    }

    public boolean verifyMessage(String publicKey, String message, String signature) {
        try {
            return SignatureJniObj.verify(
                    CryptoType.HSM_TYPE,
                    Hex.decode(publicKey),
                    Numeric.cleanHexPrefix(message),
                    Numeric.cleanHexPrefix(signature),
                    this.getHsmLibPath());
        } catch (JniException e) {
            logger.error("Verify with hsm sm2 failed, jni e: ", e);
            return false;
        }
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
        // String publicKey = getPubFromSignature(msgHash, signature);
        return "";
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
