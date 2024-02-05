package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.AbiEncodedRequest;

public class AbiEncodedRequestBuilder {
    private byte[] encodedData;
    private String to;
    private String abi;
    private BigInteger blockLimit;
    private String nonce;
    private BigInteger value;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private EIP1559Struct eip1559Struct;

    public AbiEncodedRequestBuilder(byte[] encodedData, String to) {
        this.encodedData = encodedData;
        this.to = to;
    }

    public AbiEncodedRequestBuilder(byte[] encodedData, String to, String abi) {
        this.encodedData = encodedData;
        this.to = to;
        this.abi = abi;
    }

    public AbiEncodedRequestBuilder setEncodedData(byte[] encodedData) {
        this.encodedData = encodedData;
        return this;
    }

    public AbiEncodedRequestBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public AbiEncodedRequestBuilder setAbi(String abi) {
        this.abi = abi;
        return this;
    }

    public AbiEncodedRequestBuilder setBlockLimit(BigInteger blockLimit) {
        this.blockLimit = blockLimit;
        return this;
    }

    public AbiEncodedRequestBuilder setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public AbiEncodedRequestBuilder setValue(BigInteger value) {
        this.value = value;
        return this;
    }

    public AbiEncodedRequestBuilder setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
        return this;
    }

    public AbiEncodedRequestBuilder setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
        return this;
    }

    public AbiEncodedRequestBuilder setEIP1559Struct(EIP1559Struct eip1559Struct) {
        this.eip1559Struct = eip1559Struct;
        return this;
    }

    public AbiEncodedRequest build() {
        return new AbiEncodedRequest(
                encodedData, to, abi, blockLimit, nonce, value, gasPrice, gasLimit, eip1559Struct);
    }
}
