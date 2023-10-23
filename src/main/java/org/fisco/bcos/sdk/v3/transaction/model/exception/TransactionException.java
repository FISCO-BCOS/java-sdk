package org.fisco.bcos.sdk.v3.transaction.model.exception;

import java.math.BigInteger;
import java.util.Optional;

public class TransactionException extends Exception {
    private static final long serialVersionUID = -2204228001512046284L;
    private Optional<String> transactionHash = Optional.empty();
    private int status;
    private BigInteger gasUsed;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public void setTransactionHash(Optional<String> transactionHash) {
        this.transactionHash = transactionHash;
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, String transactionHash) {
        super(message);
        this.transactionHash = Optional.ofNullable(transactionHash);
    }

    public TransactionException(String message, int status) {
        super(message);
        this.status = status;
    }

    public TransactionException(
            String message, int status, BigInteger gasUsed, String transactionHash) {
        super(message);
        this.status = status;
        this.gasUsed = gasUsed;
        this.transactionHash = Optional.ofNullable(transactionHash);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

    /**
     * Obtain the transaction hash .
     *
     * @return optional transaction hash .
     */
    public Optional<String> getTransactionHash() {
        return transactionHash;
    }
}
