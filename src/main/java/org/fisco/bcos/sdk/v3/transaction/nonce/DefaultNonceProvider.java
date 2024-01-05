package org.fisco.bcos.sdk.v3.transaction.nonce;

import java.util.UUID;

public class DefaultNonceProvider implements NonceProvider {
    @Override
    public String getNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public void getNonceAsync(RemoteNonceCallbackInterface callback) {
        callback.handleNonce(getNonce());
    }
}
