package org.fisco.bcos.sdk.contract.auth.po;

import java.math.BigInteger;

public enum AuthType {
    WHITE_LIST(1),
    BLACK_LIST(2);

    private final int value;

    AuthType(int i) {
        value = i;
    }

    public final BigInteger getValue() {
        return BigInteger.valueOf(value);
    }
}
