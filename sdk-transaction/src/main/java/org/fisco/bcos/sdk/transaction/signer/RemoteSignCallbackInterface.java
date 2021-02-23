package org.fisco.bcos.sdk.transaction.signer;

import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

public interface RemoteSignCallbackInterface {
    /**
     * receive the signature,and execute the callback function later.
     *
     * @param signature
     * @return result code
     */
    public int handleSignedTransaction(SignatureResult signature);
}
