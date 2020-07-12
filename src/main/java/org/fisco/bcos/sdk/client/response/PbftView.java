package org.fisco.bcos.sdk.client.response;

import java.math.BigInteger;
import org.fisco.bcos.sdk.client.RPCResponse;
import org.fisco.bcos.sdk.utils.Numeric;

/** getPbftView */
public class PbftView extends RPCResponse<String> {

    public BigInteger getPbftView() {
        return Numeric.decodeQuantity(getResult());
    }
}
