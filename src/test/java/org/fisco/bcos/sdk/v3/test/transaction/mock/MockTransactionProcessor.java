package org.fisco.bcos.sdk.v3.test.transaction.mock;

import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessor;

import java.util.ArrayList;

public class MockTransactionProcessor extends TransactionProcessor {
    private String txHash;
    private String output;
    private int status;

    public MockTransactionProcessor(Client client, CryptoKeyPair cryptoKeyPair, String groupId, String chainId, String txHash, int status, String output) {
        super(client, cryptoKeyPair, groupId, chainId);
        this.txHash = txHash;
        this.output = output;
        this.status = status;
    }

    @Override
    public String sendTransactionAsync(
            String to,
            byte[] data,
            CryptoKeyPair cryptoKeyPair,
            int txAttribute,
            TransactionCallback callback) {
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setStatus(status);
        transactionReceipt.setOutput(this.output);
        transactionReceipt.setTransactionHash(this.txHash);
        transactionReceipt.setLogEntries(new ArrayList<>());
        callback.onResponse(transactionReceipt);

        return this.txHash;
    }

    @Override
    public TransactionReceipt sendTransactionAndGetReceipt(String to, byte[] data, CryptoKeyPair cryptoKeyPair, int txAttribute) {
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setStatus(status);
        transactionReceipt.setOutput(this.output);
        transactionReceipt.setTransactionHash(this.txHash);
        transactionReceipt.setLogEntries(new ArrayList<>());
        return transactionReceipt;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
