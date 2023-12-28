package org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class TransactionRequestWithStringParams extends BasicRequest {

    private List<String> stringParams;

    public TransactionRequestWithStringParams(
            String abi,
            String method,
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        super(abi, method, to, value, gasPrice, gasLimit, eip1559Struct);
    }

    public void setStringParams(List<String> params) {
        this.stringParams = params;
    }

    public List<String> getStringParams() {
        return stringParams;
    }

    @Override
    public boolean isTransactionEssentialSatisfy() {
        return super.isTransactionEssentialSatisfy() && stringParams != null;
    }

    @Override
    public String toString() {
        return "TransactionRequestWithStringParams{"
                + "base="
                + super.toString()
                + ", params="
                + stringParams
                + "} ";
    }
}
