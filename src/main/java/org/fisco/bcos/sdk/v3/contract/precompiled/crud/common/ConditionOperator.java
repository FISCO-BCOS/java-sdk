package org.fisco.bcos.sdk.v3.contract.precompiled.crud.common;

import java.math.BigInteger;

public enum ConditionOperator {
    GT(0),
    GE(1),
    LT(2),
    LE(3),
    EQ(4),
    NE(5),
    STARTS_WITH(6),
    ENDS_WITH(7),
    CONTAINS(8);
    private final int value;

    private ConditionOperator(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public BigInteger getBigIntValue() {
        return BigInteger.valueOf(value);
    }

    @Override
    public String toString() {
        switch (value) {
            case 0:
                return "GT";
            case 1:
                return "GE";
            case 2:
                return "LT";
            case 3:
                return "LE";
            case 4:
                return "EQ";
            case 5:
                return "NE";
            case 6:
                return "STARTS_WITH";
            case 7:
                return "ENDS_WITH";
            case 8:
                return "CONTAINS";
            default:
                return "";
        }
    }
}
