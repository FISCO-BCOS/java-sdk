package org.fisco.bcos.sdk.codec.datatypes;

import java.math.BigInteger;

/** Common integer properties. */
public abstract class IntType extends NumericType {

    public IntType(String typePrefix, int bitSize, BigInteger value) {
        super(typePrefix + bitSize, value, bitSize);
        if (!valid(bitSize, value)) {
            throw new UnsupportedOperationException(
                    "Bit size must be 8 bit aligned, and the bitLength must be no larger than "
                            + bitSize
                            + ". Current bitLength:"
                            + value.bitLength()
                            + ", value:"
                            + value);
        }
    }

    boolean valid(int bitSize, BigInteger value) {
        return isValidBitSize(bitSize) && isValidBitCount(bitSize, value);
    }

    static boolean isValidBitSize(int bitSize) {
        return bitSize % 8 == 0 && bitSize > 0 && bitSize <= MAX_BIT_LENGTH;
    }

    private static boolean isValidBitCount(int bitSize, BigInteger value) {
        return value.bitLength() <= bitSize;
    }
}
