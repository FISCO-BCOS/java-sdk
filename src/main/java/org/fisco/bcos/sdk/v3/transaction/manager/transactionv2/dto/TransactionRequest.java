package org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class TransactionRequest {
    private String abi;
    private String method;
    protected String to;
    private List<Object> params;
    private BigInteger value;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private EIP1559Struct eip1559Struct;

    public TransactionRequest(
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

    public void setParams(List<Object> params) {
        this.params = params;
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

    public List<Object> getParams() {
        return params;
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

    public boolean isTransactionEssentialSatisfy() {
        return abi != null && method != null && to != null;
    }

    @Override
    public String toString() {
        return "TransactionRequest{"
                + "abi='"
                + abi
                + '\''
                + ", method='"
                + method
                + '\''
                + ", to='"
                + to
                + '\''
                + ", params="
                + params
                + ", value="
                + value
                + ", gasPrice="
                + gasPrice
                + ", gasLimit="
                + gasLimit
                + ", eip1559Struct="
                + eip1559Struct
                + '}';
    }
}
