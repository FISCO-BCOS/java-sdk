package org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class TransactionRequest extends BasicRequest {
    private List<Object> params;

    public TransactionRequest(
            String abi,
            String method,
            String to,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        super(abi, method, to, value, gasPrice, gasLimit, eip1559Struct);
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public List<Object> getParams() {
        return params;
    }

    @Override
    public boolean isTransactionEssentialSatisfy() {
        return super.isTransactionEssentialSatisfy() && params != null;
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
