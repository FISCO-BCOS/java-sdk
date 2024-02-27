package org.fisco.bcos.sdk.v3.transaction.gasProvider;

import java.math.BigInteger;

public class StaticGasProvider implements ContractGasProvider {
    private final BigInteger gasPrice;
    private final BigInteger gasLimit;

    public StaticGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        return gasPrice;
    }

    @Override
    public BigInteger getGasPrice(byte[] methodId) {
        return gasPrice;
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        return gasLimit;
    }

    @Override
    public BigInteger getGasLimit(byte[] methodId) {
        return gasLimit;
    }

    @Override
    public boolean isEIP1559Enabled() {
        return false;
    }

    @Override
    public EIP1559Struct getEIP1559Struct(String methodId) {
        return new EIP1559Struct(BigInteger.ZERO, BigInteger.ZERO, gasLimit);
    }

    @Override
    public EIP1559Struct getEIP1559Struct(byte[] methodId) {
        return new EIP1559Struct(BigInteger.ZERO, BigInteger.ZERO, gasLimit);
    }
}
