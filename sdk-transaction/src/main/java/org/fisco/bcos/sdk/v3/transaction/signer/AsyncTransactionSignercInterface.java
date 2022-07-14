package org.fisco.bcos.sdk.v3.transaction.signer;

public interface AsyncTransactionSignercInterface {
    /**
     * sign raw transaction hash string and get signatrue result
     *
     * @param hash raw transaction hash string to be signed
     * @param transactionSignCallback after signed, callback hook
     */
    void signAsync(byte[] hash, RemoteSignCallbackInterface transactionSignCallback);
}
