package org.fisco.bcos.sdk.v3.transaction.gasProvider;

import java.math.BigInteger;

public interface ContractGasProvider {
    BigInteger getGasPrice(String methodId);

    BigInteger getGasPrice(byte[] methodId);

    BigInteger getGasLimit(String methodId);

    BigInteger getGasLimit(byte[] methodId);

    boolean isEIP1559Enabled();

    EIP1559Struct getEIP1559Struct(String methodId);

    EIP1559Struct getEIP1559Struct(byte[] methodId);
}
