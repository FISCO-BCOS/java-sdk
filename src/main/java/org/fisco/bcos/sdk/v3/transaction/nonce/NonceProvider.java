package org.fisco.bcos.sdk.v3.transaction.nonce;

public interface NonceProvider {
    String getNonce();

    void getNonceAsync(RemoteNonceCallbackInterface callback);
}
