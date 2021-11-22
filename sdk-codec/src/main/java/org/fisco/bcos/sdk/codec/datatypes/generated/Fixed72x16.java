package org.fisco.bcos.sdk.codec.datatypes.generated;

import java.math.BigDecimal;
import org.fisco.bcos.sdk.codec.datatypes.Fixed;

public class Fixed72x16 extends Fixed {
    public static final Fixed72x16 Default = new Fixed72x16(BigDecimal.ZERO);

    public Fixed72x16(BigDecimal value) {
        super(72, 16, value);
    }

    public Fixed72x16(String value) {
        super(72, 16, value);
    }
}
