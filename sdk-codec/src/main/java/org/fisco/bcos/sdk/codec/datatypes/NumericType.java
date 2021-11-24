package org.fisco.bcos.sdk.codec.datatypes;

import java.math.BigInteger;
import java.util.Objects;

/** Common numeric type. */
public abstract class NumericType implements Type<BigInteger> {

    private String type;
    BigInteger value;
    private int bitSize;

    public NumericType(String type, BigInteger value, int bitSize) {
        this.type = type;
        this.value = value;
        this.bitSize = bitSize;
    }

    @Override
    public String getTypeAsString() {
        return type;
    }

    @Override
    public BigInteger getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NumericType that = (NumericType) o;

        if (!type.equals(that.type)) {
            return false;
        }

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public int getBitSize() {
        return bitSize;
    }

    public void setBitSize(int bitSize) {
        this.bitSize = bitSize;
    }
}
