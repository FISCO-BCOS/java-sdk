package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class DeployTransactionRequestWithStringParams extends BasicDeployRequest {

    private List<String> stringParams;

    public DeployTransactionRequestWithStringParams(
            String abi,
            String bin,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        super(abi, bin, value, gasPrice, gasLimit, eip1559Struct);
    }

    public DeployTransactionRequestWithStringParams(
            String abi,
            String bin,
            BigInteger blockLimit,
            String nonce,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct,
            byte[] extension) {
        super(abi, bin, blockLimit, nonce, value, gasPrice, gasLimit, eip1559Struct, extension);
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
        return "DeployTransactionRequestWithStringParams{"
                + "base="
                + super.toString()
                + ", params="
                + stringParams
                + "} ";
    }
}
