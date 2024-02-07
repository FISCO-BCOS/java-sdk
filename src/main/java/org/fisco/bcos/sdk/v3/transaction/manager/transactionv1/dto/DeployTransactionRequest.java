package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class DeployTransactionRequest extends BasicDeployRequest {

    private List<Object> params;

    public DeployTransactionRequest(
            String abi,
            String bin,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        super(abi, bin, value, gasPrice, gasLimit, eip1559Struct);
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
}
