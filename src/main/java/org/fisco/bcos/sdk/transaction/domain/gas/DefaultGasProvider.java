package org.fisco.bcos.sdk.transaction.domain.gas;

import java.math.BigInteger;

import org.fisco.bcos.sdk.transaction.domain.Contract;
import org.fisco.bcos.sdk.transaction.domain.ManagedTransaction;

public class DefaultGasProvider extends StaticGasProvider {
    public static final BigInteger GAS_LIMIT = Contract.GAS_LIMIT;
    public static final BigInteger GAS_PRICE = ManagedTransaction.GAS_PRICE;

    public DefaultGasProvider() {
        super(GAS_PRICE, GAS_LIMIT);
    }
}
