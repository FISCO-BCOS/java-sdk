package org.fisco.bcos.sdk.v3.model;

public enum NodeType {
    CONSENSUS_SEALER("consensus_sealer"),
    CONSENSUS_OBSERVER("consensus_observer"),
    CONSENSUS_CANDIDATE_SEALER("consensus_candidate_sealer"),
    UNKNOWN("unknown");

    private final String type;

    NodeType(String t) {
        type = t;
    }
}
