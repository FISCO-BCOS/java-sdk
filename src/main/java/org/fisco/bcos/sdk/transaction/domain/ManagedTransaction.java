package org.fisco.bcos.sdk.transaction.domain;

import java.io.IOException;
import java.math.BigInteger;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.exception.TransactionException;
import org.fisco.bcos.sdk.transaction.manager.TransactionManager;

/** Generic transaction manager. */
public abstract class ManagedTransaction {

    public static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);

    protected Client web3j;

    protected TransactionManager transactionManager;

    protected ManagedTransaction(Client web3j, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.web3j = web3j;
    }

    protected TransactionReceipt send(
            String to, String data, BigInteger value, BigInteger gasPrice, BigInteger gasLimit)
            throws IOException, TransactionException {

        return transactionManager.executeTransaction(gasPrice, gasLimit, to, data, value, null);
    }

    protected void sendOnly(
            String to,
            String data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            ResponseCallback callback)
            throws IOException, TransactionException {
        transactionManager.sendTransaction(gasPrice, gasLimit, to, data, value, null, callback);
    }

    protected String createSeq(
            String to, String data, BigInteger value, BigInteger gasPrice, BigInteger gasLimit)
            throws IOException {
        RawTransaction rawTransaction =
                transactionManager.createTransaction(gasPrice, gasLimit, to, data, value, null);
        return transactionManager.sign(rawTransaction);
    }
}
