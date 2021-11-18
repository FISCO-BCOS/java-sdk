package org.fisco.bcos.sdk.codec.datatypes.generated;
import java.math.BigDecimal;

import org.fisco.bcos.sdk.codec.datatypes.Fixed;

public class Fixed80x24 extends Fixed {
    public static final Fixed80x24 Default = new Fixed80x24(BigDecimal.ZERO);
    
    public Fixed80x24(BigDecimal value) {
        super(80,24,value);
    }
}