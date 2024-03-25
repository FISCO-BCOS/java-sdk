package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
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

    public TransactionRequestWithStringParams(
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
        super(
                version,
                abi,
                method,
                to,
                blockLimit,
                nonce,
                value,
                gasPrice,
                gasLimit,
                eip1559Struct,
                extension);
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
