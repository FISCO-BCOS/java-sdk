package org.fisco.bcos.sdk.codec.datatypes.generated;

import java.math.BigDecimal;
import org.fisco.bcos.sdk.codec.datatypes.FixedPointNumType;

public class Fixed64x16 extends FixedPointNumType {
    public static final Fixed64x16 Default = new Fixed64x16(BigDecimal.ZERO);
    public static final String TYPE_NAME = "fixed";

    public Fixed64x16(BigDecimal value) {
        this(64, 16, value);
    }

    public Fixed64x16(String value) {
        this(new BigDecimal(value));
    }

    protected Fixed64x16(int mBitSize, int nBitSize, BigDecimal value) {
        super(TYPE_NAME, mBitSize, nBitSize, value);
    }

    protected Fixed64x16(int mBitSize, int nBitSize, String value) {
        super(TYPE_NAME, mBitSize, nBitSize, value);
    }
}
