package org.fisco.bcos.sdk.v3.transaction.gasProvider;

import java.math.BigInteger;

public class EIP1559Struct {
    private final BigInteger maxFeePerGas;
    private final BigInteger maxPriorityFeePerGas;
    private final BigInteger gasLimit;

    public EIP1559Struct(
            BigInteger maxFeePerGas, BigInteger maxPriorityFeePerGas, BigInteger gasLimit) {
        this.maxFeePerGas = maxFeePerGas;
        this.maxPriorityFeePerGas = maxPriorityFeePerGas;
        this.gasLimit = gasLimit;
    }

    public BigInteger getMaxFeePerGas() {
        return maxFeePerGas;
    }

    public BigInteger getMaxPriorityFeePerGas() {
        return maxPriorityFeePerGas;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }
}
