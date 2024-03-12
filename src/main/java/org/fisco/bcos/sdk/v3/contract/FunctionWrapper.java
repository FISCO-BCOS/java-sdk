package org.fisco.bcos.sdk.v3.contract;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;

public class FunctionWrapper {
    private final Contract contract;
    private Function function;
    private String nonce;
    private BigInteger blockLimit;
    private BigDecimal value;
    private byte[] extension;

    public FunctionWrapper(Contract contract) {
        this.contract = contract;
    }

    public FunctionWrapper(Contract contract, Function function) {
        this.contract = contract;
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public FunctionWrapper setFunction(Function function) {
        this.function = function;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public FunctionWrapper setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public BigInteger getBlockLimit() {
        return blockLimit;
    }

    public FunctionWrapper setBlockLimit(BigInteger blockLimit) {
        this.blockLimit = blockLimit;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public FunctionWrapper setValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public byte[] getExtension() {
        return extension;
    }

    public FunctionWrapper setExtension(byte[] extension) {
        this.extension = extension;
        return this;
    }

    public TransactionReceipt send() {
        return contract.executeTransaction(this);
    }

    public String asyncSend(TransactionCallback callback) {
        return contract.asyncExecuteTransaction(this, callback);
    }
}
