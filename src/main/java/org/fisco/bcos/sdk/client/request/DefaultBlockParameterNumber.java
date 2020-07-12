package org.fisco.bcos.sdk.client.request;

import com.fasterxml.jackson.annotation.JsonValue;
import org.fisco.bcos.sdk.utils.Numeric;

import java.math.BigInteger;

/** DefaultBlockParameter implementation that takes a numeric value. */
public class DefaultBlockParameterNumber implements DefaultBlockParameter {

    private BigInteger blockNumber;

    public DefaultBlockParameterNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public DefaultBlockParameterNumber(long blockNumber) {
        this(BigInteger.valueOf(blockNumber));
    }

    @Override
    @JsonValue
    public String getValue() {
        return Numeric.encodeQuantity(blockNumber);
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }
}

