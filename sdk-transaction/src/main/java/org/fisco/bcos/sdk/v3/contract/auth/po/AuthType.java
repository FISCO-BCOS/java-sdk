package org.fisco.bcos.sdk.v3.contract.auth.po;

import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public enum AuthType {
    WHITE_LIST(1),
    BLACK_LIST(2);

    private final int value;

    AuthType(int i) {
        value = i;
    }

    public static AuthType valueOf(int i) throws ContractException {
        switch (i) {
            case 1:
                return WHITE_LIST;
            case 2:
                return BLACK_LIST;
            default:
                throw new ContractException("Error Auth Type:" + i);
        }
    }

    public final BigInteger getValue() {
        return BigInteger.valueOf(value);
    }

    @Override
    public String toString() {
        switch (value) {
            case 1:
                return "WHITE_LIST";
            case 2:
                return "BLACK_LIST";
            default:
                return "UNKNOWN";
        }
    }
}
