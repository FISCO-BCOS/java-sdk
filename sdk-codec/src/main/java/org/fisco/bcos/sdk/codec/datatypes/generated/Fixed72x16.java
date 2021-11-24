package org.fisco.bcos.sdk.codec.datatypes.generated;

import java.math.BigDecimal;
import org.fisco.bcos.sdk.codec.datatypes.FixedPointNumType;

public class Fixed72x16 extends FixedPointNumType {
    public static final Fixed72x16 Default = new Fixed72x16(BigDecimal.ZERO);
    public static final String TYPE_NAME = "Fixed";

    public Fixed72x16(BigDecimal value) {
        this(72, 16, value);
    }

    public Fixed72x16(String value) {
        this(new BigDecimal(value));
    }

    protected Fixed72x16(int mBitSize, int nBitSize, BigDecimal value) {
        super(TYPE_NAME, mBitSize, nBitSize, value);
    }

    protected Fixed72x16(int mBitSize, int nBitSize, String value) {
        super(TYPE_NAME, mBitSize, nBitSize, value);
    }
}
