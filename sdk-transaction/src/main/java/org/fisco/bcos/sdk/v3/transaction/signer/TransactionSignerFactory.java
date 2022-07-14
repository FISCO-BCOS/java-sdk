package org.fisco.bcos.sdk.v3.transaction.signer;

import org.fisco.bcos.sdk.v3.crypto.signature.Signature;

public class TransactionSignerFactory {

    private TransactionSignerFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static TransactionSignerInterface createTransactionSigner(Signature signature) {
        return new TransactionSignerService(signature);
    }

    public static TransactionSignerInterface createTransactionSigner(
            RemoteSignProviderInterface transactionSignProvider, int encryptType) {
        return new TransactionSignerWithRemoteSignService(transactionSignProvider, encryptType);
    }
}
