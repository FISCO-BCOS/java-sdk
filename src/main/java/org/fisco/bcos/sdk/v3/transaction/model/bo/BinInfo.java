package org.fisco.bcos.sdk.v3.transaction.model.bo;

import java.util.Map;

public class BinInfo {
    private Map<String, String> bins;

    /** @param bins */
    /**
     * the binary information of the contracts
     *
     * @param bins maps between contract name and the binary
     */
    public BinInfo(Map<String, String> bins) {
        super();
        this.bins = bins;
    }

    public String getBin(String contract) {
        return this.bins.get(contract);
    }
}
