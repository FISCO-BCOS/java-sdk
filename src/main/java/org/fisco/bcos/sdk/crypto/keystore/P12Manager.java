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
package org.fisco.bcos.sdk.crypto.keystore;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import org.fisco.bcos.sdk.crypto.exceptions.LoadKeyStoreException;

public class P12Manager extends KeyManager {
    private static final String NAME = "key";
    private KeyStore keyStore;

    public P12Manager(final String keyStoreFile, final String password) {
        super(keyStoreFile, password);
    }

    @Override
    public PublicKey getPublicKey() {
        try {
            Certificate certificate = keyStore.getCertificate(NAME);
            return certificate.getPublicKey();
        } catch (KeyStoreException e) {
            throw new LoadKeyStoreException(
                    "getPublicKey from p12 file "
                            + keyStoreFile
                            + " failed, error message: "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * load keyPair from the given input stream
     *
     * @param in: the input stream that should used to load keyPair
     */
    protected void load(InputStream in) {
        try {
            keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(in, this.password.toCharArray());
        } catch (IOException
                | CertificateException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | KeyStoreException e) {
            String errorMessage =
                    "load keys from p12 file "
                            + keyStoreFile
                            + " failed, error message:"
                            + e.getMessage();
            logger.error(errorMessage);
            throw new LoadKeyStoreException(errorMessage, e);
        }
    }

    /**
     * get private key from the keyStore
     *
     * @return: the private key
     */
    protected PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(NAME, password.toCharArray());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            String errorMessage =
                    "get private key from "
                            + keyStoreFile
                            + " failed for UnrecoverableKeyException, error message"
                            + e.getMessage();
            logger.error(errorMessage);
            throw new LoadKeyStoreException(errorMessage, e);
        }
    }
}
