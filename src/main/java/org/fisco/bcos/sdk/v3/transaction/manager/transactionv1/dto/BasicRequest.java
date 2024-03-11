package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class BasicRequest {

    protected TransactionVersion version = TransactionVersion.V1;
    protected String abi;
    protected String method;
    protected BigInteger blockLimit;
    protected String nonce;
    // v2
    protected byte[] extension;

    protected String to;
    protected BigInteger value;
    protected BigInteger gasPrice;
    protected BigInteger gasLimit;
    protected EIP1559Struct eip1559Struct;

    public BasicRequest(
            String abi,
            String method,
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        this.abi = abi;
        this.method = method;
        this.to = to;
        this.value = value;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.eip1559Struct = eip1559Struct;
    }

    public BasicRequest(
            TransactionVersion version,
            String abi,
            String method,
            String to,
            BigInteger blockLimit,
            String nonce,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct,
            byte[] extension) {
        this.version = version;
        this.abi = abi;
        this.method = method;
        this.blockLimit = blockLimit;
        this.nonce = nonce;
        this.extension = extension;
        this.to = to;
        this.value = value;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.eip1559Struct = eip1559Struct;
    }

    public String getAbi() {
        return abi;
    }

    public String getMethod() {
        return method;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public EIP1559Struct getEip1559Struct() {
        return eip1559Struct;
    }

    public BigInteger getBlockLimit() {
        return blockLimit;
    }

    public void setBlockLimit(BigInteger blockLimit) {
        this.blockLimit = blockLimit;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public byte[] getExtension() {
        return extension;
    }

    public void setExtension(byte[] extension) {
        this.extension = extension;
    }

    public TransactionVersion getVersion() {
        return version;
    }

    public void setVersion(TransactionVersion version) {
        this.version = version;
    }

    public boolean isTransactionEssentialSatisfy() {
        return abi != null && method != null && to != null;
    }

    public boolean isEIP1559Enabled() {
        return eip1559Struct != null;
    }
}
