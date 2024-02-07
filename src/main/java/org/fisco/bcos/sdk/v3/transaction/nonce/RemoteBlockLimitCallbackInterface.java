package org.fisco.bcos.sdk.v3.transaction.nonce;

import java.math.BigInteger;

public interface RemoteBlockLimitCallbackInterface {
    void handleBlockLimit(BigInteger nonce);
}
