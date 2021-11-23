package org.fisco.bcos.sdk.codec.datatypes.generated;

import java.math.BigDecimal;
import org.fisco.bcos.sdk.codec.datatypes.Fixed;

public class Fixed80x16 extends Fixed {
    public static final Fixed80x16 Default = new Fixed80x16(BigDecimal.ZERO);

    public Fixed80x16(BigDecimal value) {
        super(80, 16, value);
    }
}
