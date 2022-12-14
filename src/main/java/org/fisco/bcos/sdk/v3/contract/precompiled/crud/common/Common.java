package org.fisco.bcos.sdk.v3.contract.precompiled.crud.common;

import java.math.BigInteger;

public class Common {
    public static final String TABLE_PREFIX = "/tables/";

    private Common() {}

    public enum TableKeyOrder {
        Unknown(-1),
        Lexicographic(0),
        Numerical(1);
        private final int value;

        TableKeyOrder(int value) {
            this.value = value;
        }

        public BigInteger getBigValue() {
            return BigInteger.valueOf(value);
        }

        public static TableKeyOrder valueOf(int value) {
            switch (value) {
                case 0:
                    return Lexicographic;
                case 1:
                    return Numerical;
                default:
                    return Unknown;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case Lexicographic:
                    return "Lexicographic";
                case Numerical:
                    return "Numerical";
                case Unknown:
                default:
                    return "Unknown";
            }
        }
    }
}
