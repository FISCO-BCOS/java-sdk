package org.fisco.bcos.sdk.transaction.domain;

import java.util.Map;

public class BinInfo {
    private Map<String, String> bins;

    /** @param bins */
    public BinInfo(Map<String, String> bins) {
        super();
        this.bins = bins;
    }

    public String getBin(String contract) {
        return this.bins.get(contract);
    }
}
