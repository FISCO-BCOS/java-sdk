package org.fisco.bcos.sdk.codec.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import org.fisco.bcos.sdk.codec.Utils;

/** Common numeric type. */
public abstract class NumericType implements Type<BigInteger> {

    private String type;
    BigInteger value;
    BigInteger nValue;
    private BigDecimal dValue;
    private BigDecimal decimalValue;
    private int bitSize;
    private int nbitSize;
    private int sig;

    public NumericType(String type, BigInteger value, int bitSize) {
        this.type = type;
        this.value = value;
        this.bitSize = bitSize;
    }

    public NumericType(String type, BigDecimal value, int mbitSize, int nbitSize) {
        this.type = type;
        this.decimalValue = BigDecimal.valueOf(value.doubleValue());
        this.sig = (this.decimalValue.signum() < 0) ? -1 : 0;
        this.value = Utils.divideFixed(value).getKey();
        this.dValue = Utils.divideFixed(value).getValue();
        this.bitSize = mbitSize;
        this.nbitSize = nbitSize;
    }

    public NumericType(String type, String value,int mbitSize, int nbitSize) {
        this.type = type; 
        this.value = new BigInteger(value.split("\\.")[0]);
        if(value.split("\\.")[1].startsWith("-")) {
            this.sig = -1;
            this.nValue = new BigInteger(value.split("\\.")[1].substring(1));
        }else {
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
        if(this.nbitSize==0) {
            return Objects.equals(value, that.value);
        }else {
            if(decimalValue.scale()<that.decimalValue.scale()){
                return (dValue.compareTo(that.dValue.setScale(dValue.scale(),BigDecimal.ROUND_HALF_UP))==0&&value.equals(that.value));
            }else {
                return (dValue.setScale(that.dValue.scale(),BigDecimal.ROUND_HALF_UP).compareTo(that.dValue)==0&&value.equals(that.value));
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
}
