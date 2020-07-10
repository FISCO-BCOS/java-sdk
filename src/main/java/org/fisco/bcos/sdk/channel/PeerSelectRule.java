package org.fisco.bcos.sdk.channel;

import java.util.List;

public interface PeerSelectRule {
    /**
     * PeerSelectRule
     * Costomize a rule to select a peer to send message to
     * @param conns
     * @return
     */
    String select(List<ConnectionInfo> conns);
}
