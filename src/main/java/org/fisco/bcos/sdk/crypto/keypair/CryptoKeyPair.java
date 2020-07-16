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
package org.fisco.bcos.sdk.crypto.keypair;

import com.webank.wedpr.crypto.CryptoResult;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Objects;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

public abstract class CryptoKeyPair {
    private BigInteger privateKey;
    protected BigInteger publicKey;

    protected String hexPrivateKey;
    protected String hexPublicKey;
    public KeyPair keyPair;

    public CryptoKeyPair() {}

    public CryptoKeyPair(final BigInteger privateKey) {
        this.privateKey = privateKey;
        /**
         * todo: get publicKey according to privateKey this.publicKey =
         * privateKeyToPublic(privateKey);
         */
        this.keyPair = null;
        calculateHexedKeyPair();
    }

    public CryptoKeyPair(final BigInteger privateKey, final BigInteger publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.keyPair = null;
        calculateHexedKeyPair();
    }

    /**
     * init CryptoKeyPair from the keyPair
     *
     * @param keyPair
     */
    public CryptoKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        // init privateKey/publicKey from the keyPair
        this.privateKey = ((BCECPrivateKey) keyPair.getPrivate()).getD();
        byte[] publicKeyBytes = ((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false);
        this.publicKey =
                new BigInteger(1, Arrays.copyOfRange(publicKeyBytes, 1, publicKeyBytes.length));
        calculateHexedKeyPair();
    }

    private void calculateHexedKeyPair() {
        this.hexPrivateKey = this.privateKey.toString(16);
        this.hexPublicKey = this.publicKey.toString(16);
    }

    /**
     * get CryptoKeyPair information from CryptoResult
     *
     * @param nativeResult
     */
    CryptoKeyPair(final CryptoResult nativeResult) {
        this.hexPrivateKey = nativeResult.privteKey;
        this.hexPublicKey = nativeResult.publicKey;
        this.privateKey = new BigInteger(this.hexPrivateKey, 16);
        this.publicKey = new BigInteger(this.hexPublicKey, 16);
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public String getHexPrivateKey() {
        return hexPrivateKey;
    }

    public String getHexPublicKey() {
        return hexPublicKey;
    }

    /**
     * todo: get the public key from the given private key
     *
     * @param privateKey
     * @return: the public key calculated from the private key public abstract BigInteger
     *     privateKeyToPublic(BigInteger privateKey);
     */

    /**
     * generate keyPair randomly
     *
     * @return: the generated keyPair
     */
    public abstract CryptoKeyPair generateKeyPair();

    public abstract CryptoKeyPair createKeyPair(KeyPair keyPair);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoKeyPair comparedKeyPair = (CryptoKeyPair) o;
        return Objects.equals(this.privateKey, comparedKeyPair.privateKey)
                && Objects.equals(this.publicKey, comparedKeyPair.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privateKey, publicKey);
    }
}
