package org.fisco.bcos.sdk.v3.transaction.gasProvider;

import java.math.BigInteger;

public interface ContractGasProvider {
    BigInteger getGasPrice(String methodId);

    BigInteger getGasLimit(String methodId);

    boolean isEIP1559Enabled();

    EIP1559Struct getEIP1559Struct(String methodId);
}
