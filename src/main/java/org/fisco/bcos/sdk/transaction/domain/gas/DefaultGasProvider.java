package org.fisco.bcos.sdk.transaction.domain.gas;

import java.math.BigInteger;
import org.fisco.bcos.sdk.transaction.domain.Contract;

public class DefaultGasProvider extends StaticGasProvider {
    public static final BigInteger GAS_LIMIT = Contract.GAS_LIMIT;
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);

    public DefaultGasProvider() {
        super(GAS_PRICE, GAS_LIMIT);
    }
}
