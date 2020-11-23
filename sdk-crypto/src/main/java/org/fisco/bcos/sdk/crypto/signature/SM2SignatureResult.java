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

import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.rlp.RlpString;
import org.fisco.bcos.sdk.rlp.RlpType;
import org.fisco.bcos.sdk.utils.Hex;

public class SM2SignatureResult extends SignatureResult {
    protected byte[] pub;

    public SM2SignatureResult(final String hexPublicKey, final String signatureString) {
        super(signatureString);
        this.pub = Hex.decode(hexPublicKey.substring(2));
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
    public List<RlpType> encode() {
        List<RlpType> encodeResult = new ArrayList<>();
        encodeResult.add(RlpString.create(this.pub));
        super.encodeCommonField(encodeResult);
        return encodeResult;
    }

    public byte[] getPub() {
        return pub;
    }

    public void setPub(byte[] pub) {
        this.pub = pub;
    }
}
