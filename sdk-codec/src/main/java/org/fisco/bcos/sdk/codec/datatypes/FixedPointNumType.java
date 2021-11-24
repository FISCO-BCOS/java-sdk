package org.fisco.bcos.sdk.codec.datatypes;

import java.math.BigDecimal;

public class FixedPointNumType extends FixedType{
    static final int DEFAULT_BIT_LENGTH = MAX_BIT_LENGTH >> 1;

    public FixedPointNumType(String typePrefix, int mBitSize, int nBitSize, BigDecimal value) {
        super(typePrefix + mBitSize + "x" + nBitSize, value, mBitSize, nBitSize);
        if (!valid(mBitSize, nBitSize, value)) {
            throw new UnsupportedOperationException(
                    "Bitsize must be 8 bit aligned, and in range 0 < bitSize <= 256");
        }
    }

    public FixedPointNumType(String typePrefix, int mBitSize, int nBitSize, String value) {
        super(typePrefix + mBitSize + "x" + nBitSize, value, mBitSize, nBitSize);
    }

    boolean valid(int mBitSize, int nBitSize, BigDecimal value) {
        return isValidBitSize(mBitSize, nBitSize);
    }

    static boolean isValidBitSize(int mBitSize, int nBitSize) {
        return mBitSize % 8 == 0 && nBitSize % 8 == 0 && mBitSize*nBitSize > 0 && mBitSize <= MAX_BIT_LENGTH;
    }
}
