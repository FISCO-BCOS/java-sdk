package org.fisco.bcos.sdk.transaction.model.gas;

import java.math.BigInteger;

public interface ContractGasProvider {
    BigInteger getGasPrice(String contractFunc);

    BigInteger getGasLimit(String contractFunc);
}
