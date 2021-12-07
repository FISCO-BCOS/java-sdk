package org.fisco.bcos.sdk.contract.auth.po;

import java.math.BigInteger;

public class GovernorInfo {
    private String governorAddress;
    private BigInteger weight;

    public String getGovernorAddress() {
        return governorAddress;
    }

    public BigInteger getWeight() {
        return weight;
    }

    public GovernorInfo(String governorAddress, BigInteger weight) {
        this.governorAddress = governorAddress;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "GovernorInfo{"
                + "governorAddress='"
                + governorAddress
                + '\''
                + ", weight="
                + weight
                + '}';
    }
}
