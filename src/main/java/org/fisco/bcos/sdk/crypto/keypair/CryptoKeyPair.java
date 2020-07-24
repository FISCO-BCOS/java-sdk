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
import org.fisco.bcos.sdk.crypto.exceptions.KeyPairException;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.StringUtils;
import org.fisco.bcos.sdk.utils.exceptions.DecoderException;

public abstract class CryptoKeyPair {
    public static final int ADDRESS_SIZE = 160;
    public static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;

    public static final int PUBLIC_KEY_SIZE = 64;
    public static final int PUBLIC_KEY_LENGTH_IN_HEX = PUBLIC_KEY_SIZE << 1;

    private BigInteger privateKey;
    protected BigInteger publicKey;

    protected String hexPrivateKey;
    protected String hexPublicKey;
    public KeyPair keyPair;

    protected Hash hashImpl;

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
        // Note: In the current version of sm2 verification, the public key prefix must contain 04,
        // otherwise an error will be reported
        this.hexPublicKey = "04" + this.hexPublicKey;
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
        // Note: The generated publicKey is prefixed with 04, When converting it to BigInteger, need
        // to remove 04
        this.publicKey = new BigInteger(this.hexPublicKey.substring(2), 16);
    }

    private void calculateHexedKeyPair() {
        this.hexPrivateKey = this.privateKey.toString(16);
        this.hexPublicKey = this.publicKey.toString(16);
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

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

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

    /**
     * generate keyPair randomly
     *
     * @return: the generated keyPair
     */
    public abstract CryptoKeyPair generateKeyPair();

    public abstract CryptoKeyPair createKeyPair(KeyPair keyPair);

    protected String getPublicKeyNoPrefix(String publicKeyStr) {
        String publicKeyNoPrefix = Numeric.cleanHexPrefix(publicKeyStr);
        // Hexadecimal public key length is less than 128, add 0 in front
        if (publicKeyNoPrefix.length() < PUBLIC_KEY_LENGTH_IN_HEX) {
            publicKeyNoPrefix =
                    StringUtils.zeros(PUBLIC_KEY_LENGTH_IN_HEX - publicKeyNoPrefix.length())
                            + publicKeyNoPrefix;
        }
        return publicKeyNoPrefix;
    }
    /**
     * get the address according to the public key
     *
     * @return: the hexed address calculated from the publicKey
     */
    public String getAddress() {
        // Note: The generated publicKey is prefixed with 04, When calculate the address, need to
        // remove 04
        return getAddress(this.getHexPublicKey().substring(2));
    }
    /**
     * calculate the address according to the given public key
     *
     * @param publicKey: the Hexed publicKey that need to calculate address
     * @return
     */
    public String getAddress(String publicKey) {
        try {
            String publicKeyNoPrefix = getPublicKeyNoPrefix(publicKey);
            // calculate hash for the public key
            String publicKeyHash = Hex.toHexString(hashImpl.hash(Hex.decode(publicKeyNoPrefix)));
            // right most 160 bits
            return publicKeyHash.substring(publicKeyHash.length() - ADDRESS_LENGTH_IN_HEX);
        } catch (DecoderException e) {
            throw new KeyPairException(
                    "getAddress for "
                            + publicKey
                            + "failed, the publicKey param must be hex string, error message: "
                            + e.getMessage(),
                    e);
        }
    }

    public byte[] getAddress(byte[] publicKey) {
        byte[] hash = hashImpl.hash(publicKey);
        return Arrays.copyOfRange(hash, hash.length - 20, hash.length); // right most 160 bits
    }

    public byte[] getAddress(BigInteger publicKey) {
        byte[] publicKeyBytes = Numeric.toBytesPadded(publicKey, PUBLIC_KEY_SIZE);
        return getAddress(publicKeyBytes);
    }
}
