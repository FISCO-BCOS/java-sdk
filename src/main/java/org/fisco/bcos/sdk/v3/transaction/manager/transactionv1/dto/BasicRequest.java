package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class BasicRequest {

    protected String abi;
    protected String method;

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

    public boolean isTransactionEssentialSatisfy() {
        return abi != null && method != null && to != null;
    }
}
