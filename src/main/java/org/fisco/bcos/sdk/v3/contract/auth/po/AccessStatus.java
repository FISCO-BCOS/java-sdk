package org.fisco.bcos.sdk.v3.contract.auth.po;

import java.math.BigInteger;

public enum AccessStatus {
    Normal(0),
    Freeze(1),
    Abolish(2),
    Unknown(-1);

    private final int status;

    AccessStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public BigInteger getBigIntStatus() {
        return BigInteger.valueOf(status);
    }

    public static AccessStatus getAccessStatus(int status) {
        if (status < 0) {
            return Unknown;
        }
        switch (status) {
            case 0:
                return Normal;
            case 1:
                return Freeze;
            default:
                return Unknown;
        }
    }
}
