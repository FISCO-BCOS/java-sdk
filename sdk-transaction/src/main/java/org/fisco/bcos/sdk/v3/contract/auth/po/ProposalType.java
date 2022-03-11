package org.fisco.bcos.sdk.v3.contract.auth.po;

public enum ProposalType {
    SET_WEIGHT("setWeight"),
    SET_RATE("setRate"),
    SET_DEPLOY_AUTH_TYPE("setDeployAuthType"),
    MODIFY_DEPLOY_AUTH("modifyDeployAuth"),
    RESET_ADMIN("resetAdmin"),
    UNKNOWN("unknown");

    private final String value;

    ProposalType(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }

    public static ProposalType fromInt(int type) {
        switch (type) {
            case 11:
                return ProposalType.SET_WEIGHT;
            case 12:
                return ProposalType.SET_RATE;
            case 21:
                return ProposalType.SET_DEPLOY_AUTH_TYPE;
            case 22:
                return ProposalType.MODIFY_DEPLOY_AUTH;
            case 31:
                return ProposalType.RESET_ADMIN;
            default:
                return ProposalType.UNKNOWN;
        }
    }
}
