package org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class DeployTransactionRequestWithStringParams extends DeployTransactionRequest {

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
                + "to='"
                + to
                + '\''
                + "} "
                + super.toString();
    }
}
