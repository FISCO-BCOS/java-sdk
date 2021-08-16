package org.fisco.bcos.sdk.codec.abi;

import java.math.BigInteger;

public class Constant {
    public static final BigInteger MAX_UINT256 =
            new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    public static final BigInteger MAX_INT256 =
            new BigInteger("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    public static final BigInteger MIN_INT256 =
            new BigInteger("-7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);

    public static String NO_APPROPRIATE_ABI_METHOD =
            "Cann't encode in encodeMethodFromObject with appropriate interface ABI, please check your method name or ABI file";
}
