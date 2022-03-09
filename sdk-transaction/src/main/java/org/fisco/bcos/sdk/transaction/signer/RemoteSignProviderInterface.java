package org.fisco.bcos.sdk.transaction.signer;

import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

public interface RemoteSignProviderInterface {
    /**
     * request for signature provider service, and return the signature.
     *
     * @param dataToSign data to be signed
     * @param cryptoType: ECDSA=0,SM=1, or self defined
     * @return signature result
     */
    public SignatureResult requestForSign(byte[] dataToSign, int cryptoType);

    /**
     * request for signature provider service asynchronously
     *
     * @param dataToSign data to be signed
     * @param cryptoType: ECDSA=0,SM=1, or self defined
     * @param callback transaction sign callback
     */
    public void requestForSignAsync(
            byte[] dataToSign, int cryptoType, RemoteSignCallbackInterface callback);
}
