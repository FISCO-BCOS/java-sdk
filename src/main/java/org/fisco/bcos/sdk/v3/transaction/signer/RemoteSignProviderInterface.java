package org.fisco.bcos.sdk.v3.transaction.signer;

import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;

public interface RemoteSignProviderInterface {
    /**
     * request for signature provider service, and return the signature.
     *
     * @param dataToSign data to be signed
     * @param cryptoType ECDSA=0,SM=1, or self defined
     * @return signature result
     */
    SignatureResult requestForSign(byte[] dataToSign, int cryptoType);

    /**
     * request for signature provider service asynchronously
     *
     * @param dataToSign data to be signed
     * @param cryptoType ECDSA=0,SM=1, or self defined
     * @param callback transaction sign callback
     */
    void requestForSignAsync(
            byte[] dataToSign, int cryptoType, RemoteSignCallbackInterface callback);
}
