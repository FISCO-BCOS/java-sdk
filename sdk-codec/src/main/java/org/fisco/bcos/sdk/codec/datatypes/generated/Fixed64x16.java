package org.fisco.bcos.sdk.codec.datatypes.generated;

import java.math.BigDecimal;
import org.fisco.bcos.sdk.codec.datatypes.Fixed;

public class Fixed64x16 extends Fixed {
    public static final Fixed64x16 Default = new Fixed64x16(BigDecimal.ZERO);

    public Fixed64x16(BigDecimal value) {
        super(64, 16, value);
    }
}
