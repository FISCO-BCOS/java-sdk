package org.fisco.bcos.sdk.v3.transaction.nonce;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.client.Client;

public interface NonceAndBlockLimitProvider {
    String getNonce();

    void getNonceAsync(RemoteNonceCallbackInterface callback);

    BigInteger getBlockLimit(Client client);

    void getBlockLimitAsync(Client client, RemoteBlockLimitCallbackInterface callback);
}
