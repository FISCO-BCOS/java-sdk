package org.fisco.bcos.sdk.contract.auth.po;

public enum ProposalStatus {
    NOT_ENOUGH_VOTE("notEnoughVotes"),
    FINISHED("finished"),
    FAILED("failed"),
    REVOKE("revoke"),
    UNKNOWN("unknown");

    private final String value;

    ProposalStatus(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }

    public static ProposalStatus fromInt(int status) {
        switch (status) {
            case 1:
                return NOT_ENOUGH_VOTE;
            case 2:
                return FINISHED;
            case 3:
                return FAILED;
            case 4:
                return REVOKE;
            default:
                return UNKNOWN;
        }
    }
}
