package org.fisco.bcos.sdk.v3.transaction.nonce;

public interface RemoteNonceCallbackInterface {
    void handleNonce(String nonce);
}
