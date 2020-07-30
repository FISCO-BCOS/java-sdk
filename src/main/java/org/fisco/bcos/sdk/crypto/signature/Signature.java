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
package org.fisco.bcos.sdk.crypto.signature;

import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.domain.RawTransaction;

public interface Signature {
    SignatureResult sign(final RawTransaction rawTransaction, final CryptoKeyPair keyPair);

    SignatureResult sign(final String message, final CryptoKeyPair keyPair);

    boolean verify(final String publicKey, final String message, final String signature);

    boolean verify(final String publicKey, final byte[] message, final byte[] signature);
}
