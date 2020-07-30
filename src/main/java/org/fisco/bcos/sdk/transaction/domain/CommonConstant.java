package org.fisco.bcos.sdk.transaction.domain;

import java.math.BigInteger;

public class CommonConstant {
    public static final String BIN = "binary";
    public static final String ABI = "abi";

    public static final String ABI_CONSTRUCTOR = "constructor";
    public static final String ABI_FUNCTION = "function";

    public static final BigInteger GAS_PRICE = new BigInteger("30000000");
    public static final BigInteger GAS_LIMIT = new BigInteger("30000000");
}
