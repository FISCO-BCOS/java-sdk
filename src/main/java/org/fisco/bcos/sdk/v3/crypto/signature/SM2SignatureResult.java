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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;

public class SM2SignatureResult extends SignatureResult {
    protected byte[] pub;

    public SM2SignatureResult(final String hexPublicKey, final String signatureString) {
        super(signatureString);
        // clean 04 prefix
        this.pub =
                Hex.decode(
                        Numeric.getKeyNoPrefix(
                                CryptoKeyPair.UNCOMPRESSED_PUBLICKEY_FLAG_STR,
                                hexPublicKey,
                                CryptoKeyPair.PUBLIC_KEY_LENGTH_IN_HEX));
    }

    public SM2SignatureResult(byte[] pub, byte[] r, byte[] s) {
        super(r, s);
        this.pub = pub;
    }

    /**
     * covert signatureResult into String
     *
     * @return the signature string with [r, s]
     */
    @Override
    public String convertToString() {
        byte[] SignatureBytes = new byte[64];
        System.arraycopy(this.r, 0, SignatureBytes, 0, 32);
        System.arraycopy(this.s, 0, SignatureBytes, 32, 32);
        return Hex.toHexString(SignatureBytes);
    }

    @Override
    public String toString() {
        return convertToString();
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            byteArrayOutputStream.write(this.r);
            byteArrayOutputStream.write(this.s);
            byteArrayOutputStream.write(this.pub);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getPub() {
        return this.pub;
    }

    public void setPub(byte[] pub) {
        this.pub = pub;
    }
}
