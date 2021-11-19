package org.fisco.bcos.sdk.codec.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

/** Common fixed-point type properties. */
public abstract class FixedPointType extends NumericType {

    static final int DEFAULT_BIT_LENGTH = MAX_BIT_LENGTH >> 1;

    public FixedPointType(String typePrefix, int mBitSize, int nBitSize, BigInteger value) {
        super(typePrefix + mBitSize + "x" + nBitSize, value, nBitSize);
        if (!valid(mBitSize, nBitSize, value)) {
            throw new UnsupportedOperationException(
                    "Bitsize must be 8 bit aligned, and in range 0 < bitSize <= 256");
        }
    }

    public FixedPointType(String typePrefix, int mBitSize, int nBitSize, BigDecimal value) {
        super(typePrefix + mBitSize + "x" + nBitSize, value, mBitSize, nBitSize);
        if (!valid(mBitSize, nBitSize, value)) {
            throw new UnsupportedOperationException(
                    "Bitsize must be 8 bit aligned, and in range 0 < bitSize <= 256");
        }
    }

    public FixedPointType(String typePrefix, int mBitSize, int nBitSize, String value) {
        super(typePrefix + mBitSize + "x" + nBitSize, value, mBitSize, nBitSize);
    }


    boolean valid(int mBitSize, int nBitSize, BigInteger value) {
        return isValidBitSize(mBitSize, nBitSize) && isValidBitCount(mBitSize, nBitSize, value);
    }

    boolean valid(int mBitSize, int nBitSize, BigDecimal value) {
        return isValidBitSize(mBitSize);
    }

    static boolean isValidBitSize(int mBitSize) {
        int bitSize = mBitSize;
        return mBitSize % 8 == 0 && bitSize > 0 && bitSize <= MAX_BIT_LENGTH;
    }

    static boolean isValidBitSize(int mBitSize, int nBitSize) {
        int bitSize = mBitSize + nBitSize;
        return mBitSize % 8 == 0 && nBitSize % 8 == 0 && bitSize > 0 && bitSize <= MAX_BIT_LENGTH;
    }

    private static boolean isValidBitCount(int mBitSize, int nBitSize, BigInteger value) {
        return value.bitCount() <= mBitSize + nBitSize;
    }

    static BigInteger convert(BigInteger m, BigInteger n) {
        return convert(DEFAULT_BIT_LENGTH, DEFAULT_BIT_LENGTH, m, n);
    }

    static BigInteger convert(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        BigInteger mPadded = m.shiftLeft(nBitSize);
        int nBitLength = n.bitLength();

        // find next multiple of 4
        int shift = (nBitLength + 3) & ~0x03;
        return mPadded.or(n.shiftLeft(nBitSize - shift));
    }
}
