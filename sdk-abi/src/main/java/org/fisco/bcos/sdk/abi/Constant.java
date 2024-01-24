package org.fisco.bcos.sdk.abi;

import java.math.BigInteger;

public class Constant {
    public static final BigInteger MAX_UINT256 =
            new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    public static final BigInteger MAX_INT256 =
            new BigInteger("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    public static final BigInteger MIN_INT256 =
            new BigInteger("-7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    public static final BigInteger MAX_UINT128 =
            new BigInteger("ffffffffffffffffffffffffffffffff", 16);
    public static final BigInteger MAX_INT128 =
            new BigInteger("7fffffffffffffffffffffffffffffff", 16);
    public static final BigInteger MIN_INT128 = MAX_INT128.negate();

    public static String NO_APPROPRIATE_ABI_METHOD =
            "Cann't encode in encodeMethodFromObject with appropriate interface ABI, please check your method name or ABI file";
}
