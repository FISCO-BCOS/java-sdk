package org.fisco.bcos.sdk.codec.datatypes.generated;

import java.math.BigDecimal;
import org.fisco.bcos.sdk.codec.datatypes.FixedPointNumType;

public class Fixed80x24 extends FixedPointNumType {
    public static final Fixed80x24 Default = new Fixed80x24(BigDecimal.ZERO);
    public static final String TYPE_NAME = "fixed";

    public Fixed80x24(BigDecimal value) {
        this(80, 24, value);
    }

    public Fixed80x24(String value) {
        this(new BigDecimal(value));
    }

    protected Fixed80x24(int mBitSize, int nBitSize, BigDecimal value) {
        super(TYPE_NAME, mBitSize, nBitSize, value);
    }

    protected Fixed80x24(int mBitSize, int nBitSize, String value) {
        super(TYPE_NAME, mBitSize, nBitSize, value);
    }
}
