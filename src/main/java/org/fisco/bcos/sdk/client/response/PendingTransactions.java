package org.fisco.bcos.sdk.client.response;

import org.fisco.bcos.sdk.client.RPCResponse;

import java.util.List;

/** getPendingTransactions */
public class PendingTransactions extends RPCResponse<List<Transaction>> {
    public List<Transaction> getPendingTransactions() {
        return getResult();
    }
}
