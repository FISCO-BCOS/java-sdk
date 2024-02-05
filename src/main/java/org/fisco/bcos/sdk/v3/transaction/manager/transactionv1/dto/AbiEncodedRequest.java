package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class AbiEncodedRequest extends BasicRequest {
    // required
    protected byte[] encodedData;
    // optional
    protected BigInteger blockLimit;
    // optional
    protected String nonce;

    public AbiEncodedRequest(
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        super("", "", to, value, gasPrice, gasLimit, eip1559Struct);
    }

    @Override
    public boolean isTransactionEssentialSatisfy() {
        return encodedData != null && to != null;
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

    public void setAbi(String abi) {
        this.abi = abi;
    }
}
