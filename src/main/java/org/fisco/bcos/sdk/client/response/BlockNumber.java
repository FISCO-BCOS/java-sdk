package org.fisco.bcos.sdk.client.response;

import java.math.BigInteger;
import org.fisco.bcos.sdk.client.RPCResponse;
import org.fisco.bcos.sdk.utils.Numeric;

/** getblockNumber. */
public class BlockNumber extends RPCResponse<String> {
    public BigInteger getBlockNumber() {
        return Numeric.decodeQuantity(getResult());
    }
}
