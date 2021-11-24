package org.fisco.bcos.sdk.codec.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

import org.fisco.bcos.sdk.codec.Utils;

public class FixedType implements Type<BigDecimal>{
    private String type;
    BigDecimal value;
    private BigInteger nValue;
    private BigDecimal dValue;
    private BigDecimal decimalValue;
    private int bitSize;
    private int nbitSize;
    private int sig;

    public FixedType(String type, BigDecimal value, int mbitSize, int nbitSize) {
        this.type = type;
        this.value = value;
        this.decimalValue = BigDecimal.valueOf(value.doubleValue());
        this.sig = (this.value.signum() < 0) ? -1 : 0;
        this.nValue = Utils.divideFixed(value).getKey();
        this.dValue = Utils.divideFixed(value).getValue();
        this.bitSize = mbitSize;
        this.nbitSize = nbitSize;
    }

    public FixedType(String type, String value, int mbitSize, int nbitSize) {
        this.type = type;
        this.nValue = new BigInteger(value.split("\\.")[0]);
        if (value.split("\\.")[1].startsWith("-")) {
            this.sig = -1;
            this.nValue = new BigInteger(value.split("\\.")[1].substring(1));
        } else {
            this.sig = 0;
            this.nValue = new BigInteger(value.split("\\.")[1]);
        }
        this.bitSize = mbitSize;
        this.nbitSize = nbitSize;
    }

    @Override
    public String getTypeAsString() {
        return type;
    }

    @Override
    public BigDecimal getValue() {
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

        FixedType that = (FixedType) o;

        if (!type.equals(that.type)) {
            return false;
        }
        if (this.nbitSize == 0) {
            return Objects.equals(nValue, that.nValue);
        } else {
            if (value.scale() < that.value.scale()) {
                return (value.equals(that.value.setScale(value.scale(), RoundingMode.HALF_UP)));
            } else {
                return (value.setScale(that.value.scale(), RoundingMode.HALF_UP).equals(that.value));
            }
        }
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

    public int getSig() {
        return sig;
    }

    public int getNBitSize() {
        return nbitSize;
    }

    public BigDecimal getDValue() {
        return dValue;
    }

    public BigInteger getNValue() {
        return nValue;
    }

    public BigDecimal getDecimal() {
        return decimalValue;
    }
}
