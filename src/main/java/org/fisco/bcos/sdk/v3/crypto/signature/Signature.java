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

/** interface for sign/verify functions */
package org.fisco.bcos.sdk.v3.crypto.signature;

import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;

public interface Signature {
    /**
     * Sign message with the given keyPair
     *
     * @param message the byte array message to be signed, must be a digest
     * @param keyPair the keyPair used to generate the signature
     * @return the signature result
     */
    SignatureResult sign(final byte[] message, final CryptoKeyPair keyPair);

    /**
     * Sign message with the given keyPair
     *
     * @param message the string type message to be signed, must be a digest
     * @param keyPair the keyPair used to generate the signature
     * @return the signature result
     */
    SignatureResult sign(final String message, final CryptoKeyPair keyPair);

    /**
     * Sign message with the given keyPair
     *
     * @param message the string type message to be signed, must be a digest
     * @param keyPair the keyPair used to generate the signature
     * @return the string type signature result
     */
    String signWithStringSignature(final String message, final CryptoKeyPair keyPair);

    /**
     * verify signature
     *
     * @param publicKey the public key
     * @param message the message, must be a digest
     * @param signature the signature to be verified
     * @return true/false
     */
    boolean verify(final String publicKey, final String message, final String signature);

    /**
     * verify signature
     *
     * @param publicKey the publickey
     * @param message the byte array type message, must be a digest
     * @param signature the byte array signature to be verified
     * @return true/false
     */
    boolean verify(final String publicKey, final byte[] message, final byte[] signature);
}
