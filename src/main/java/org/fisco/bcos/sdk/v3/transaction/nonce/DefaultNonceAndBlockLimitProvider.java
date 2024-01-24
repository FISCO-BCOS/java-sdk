package org.fisco.bcos.sdk.v3.transaction.nonce;

import java.math.BigInteger;
import java.util.UUID;
import org.fisco.bcos.sdk.v3.client.Client;

public class DefaultNonceAndBlockLimitProvider implements NonceAndBlockLimitProvider {
    @Override
    public String getNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public void getNonceAsync(RemoteNonceCallbackInterface callback) {
        callback.handleNonce(getNonce());
    }

    @Override
    public BigInteger getBlockLimit(Client client) {
        return client.getBlockLimit();
    }

    @Override
    public void getBlockLimitAsync(Client client, RemoteBlockLimitCallbackInterface callback) {
        callback.handleBlockLimit(getBlockLimit(client));
    }
}
