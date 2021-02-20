package org.fisco.bcos.sdk.transaction.signer;

public interface RemoteSignCallbackInterface {
    /**
     * receive the signature,and execute the callback function later.
     *
     * @param signature
     * @return result code
     */
    public int handleSignedTransaction(String signature);
}
