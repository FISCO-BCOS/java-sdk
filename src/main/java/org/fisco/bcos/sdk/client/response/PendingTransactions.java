package org.fisco.bcos.sdk.client.response;

import java.util.List;
import org.fisco.bcos.sdk.client.RPCResponse;

/** getPendingTransactions */
public class PendingTransactions extends RPCResponse<List<Transaction>> {
    public List<Transaction> getPendingTransactions() {
        return getResult();
    }
}
