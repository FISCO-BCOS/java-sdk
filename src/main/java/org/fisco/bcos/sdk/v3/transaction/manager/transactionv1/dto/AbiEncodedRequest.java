package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class AbiEncodedRequest {
    // required
    protected byte[] encodedData;
    // required
    protected String to;
    // optional
    protected String abi;
    // optional
    protected BigInteger blockLimit;
    // optional
    protected String nonce;
    // optional
    protected BigInteger value;
    // optional
    protected BigInteger gasPrice;
    // optional
    protected BigInteger gasLimit;
    // optional
    protected EIP1559Struct eip1559Struct;

    public AbiEncodedRequest(byte[] encodedData, String to) {
        this.encodedData = encodedData;
        this.to = to;
    }

    public AbiEncodedRequest(
            byte[] encodedData,
            String to,
            String abi,
            BigInteger blockLimit,
            String nonce,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        this.encodedData = encodedData;
        this.to = to;
        this.abi = abi;
        this.blockLimit = blockLimit;
        this.nonce = nonce;
        this.value = value;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.eip1559Struct = eip1559Struct;
    }

    public boolean isTransactionEssentialSatisfy() {
        return encodedData != null && to != null;
    }

    public boolean isCreate() {
        return this.abi != null && !this.abi.isEmpty();
    }

    public byte[] getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(byte[] encodedData) {
        this.encodedData = encodedData;
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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public EIP1559Struct getEip1559Struct() {
        return eip1559Struct;
    }

    public void setEip1559Struct(EIP1559Struct eip1559Struct) {
        this.eip1559Struct = eip1559Struct;
    }
}
