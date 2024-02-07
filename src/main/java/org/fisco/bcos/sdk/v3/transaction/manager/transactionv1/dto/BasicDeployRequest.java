package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;

public class BasicDeployRequest extends BasicRequest {
    private String bin;

    public BasicDeployRequest(
            String abi,
            String bin,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            EIP1559Struct eip1559Struct) {
        super(abi, "", "", value, gasPrice, gasLimit, eip1559Struct);
        this.bin = bin;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public boolean isTransactionEssentialSatisfy() {
        return super.isTransactionEssentialSatisfy() && bin != null;
    }

    @Override
    public String toString() {
        return "BasicDeployRequest{"
                + "base='"
                + super.toString()
                + '\''
                + "bin='"
                + bin
                + '\''
                + "} ";
    }
}
