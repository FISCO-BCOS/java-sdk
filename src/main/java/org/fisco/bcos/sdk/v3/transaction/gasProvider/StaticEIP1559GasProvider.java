package org.fisco.bcos.sdk.v3.transaction.gasProvider;

import java.math.BigInteger;

public class StaticEIP1559GasProvider implements ContractGasProvider {
    private long chainId;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private BigInteger gasLimit;

    public StaticEIP1559GasProvider(
            long chainId,
            BigInteger maxFeePerGas,
            BigInteger maxPriorityFeePerGas,
            BigInteger gasLimit) {
        this.chainId = chainId;
        this.maxFeePerGas = maxFeePerGas;
        this.maxPriorityFeePerGas = maxPriorityFeePerGas;
        this.gasLimit = gasLimit;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        return maxFeePerGas;
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        return gasLimit;
    }

    @Override
    public boolean isEIP1559Enabled() {
        return true;
    }

    @Override
    public EIP1559Struct getEIP1559Struct(String methodId) {
        return new EIP1559Struct(maxFeePerGas, maxPriorityFeePerGas, gasLimit);
    }
}
