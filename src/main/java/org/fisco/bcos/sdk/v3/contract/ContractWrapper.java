package org.fisco.bcos.sdk.v3.contract;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;

public class ContractWrapper {
    private final Contract contract;
    private Function function;
    private String nonce;
    private BigInteger blockLimit;
    private BigDecimal value;
    private byte[] extension;

    public ContractWrapper(Contract contract) {
        this.contract = contract;
    }

    public ContractWrapper(Contract contract, Function function) {
        this.contract = contract;
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public ContractWrapper setFunction(Function function) {
        this.function = function;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public ContractWrapper setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public BigInteger getBlockLimit() {
        return blockLimit;
    }

    public ContractWrapper setBlockLimit(BigInteger blockLimit) {
        this.blockLimit = blockLimit;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public ContractWrapper setValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public byte[] getExtension() {
        return extension;
    }

    public ContractWrapper setExtension(byte[] extension) {
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
