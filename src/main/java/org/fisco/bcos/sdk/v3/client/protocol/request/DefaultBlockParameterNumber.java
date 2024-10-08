package org.fisco.bcos.sdk.v3.client.protocol.request;

import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.utils.Numeric;

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

    @Override
    public boolean isLatest() {
        return false;
    }

    @Override
    public boolean isEarliest() {
        return false;
    }
}
