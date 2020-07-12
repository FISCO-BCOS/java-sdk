package org.fisco.bcos.sdk.client.response;

import java.math.BigInteger;

import org.fisco.bcos.sdk.client.RPCResponse;
import org.fisco.bcos.sdk.utils.Numeric;


/** getPendingTxSize */
public class PendingTxSize extends RPCResponse<String> {
    public BigInteger getPendingTxSize() {
        return Numeric.decodeQuantity(getResult());
    }
}
