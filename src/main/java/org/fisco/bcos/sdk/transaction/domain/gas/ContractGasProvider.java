package org.fisco.bcos.sdk.transaction.domain.gas;

import java.math.BigInteger;

public interface ContractGasProvider {
    BigInteger getGasPrice(String contractFunc);

    @Deprecated
    BigInteger getGasPrice();

    BigInteger getGasLimit(String contractFunc);

    @Deprecated
    BigInteger getGasLimit();
}
