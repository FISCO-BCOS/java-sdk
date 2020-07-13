package org.fisco.bcos.sdk.channel;

import java.util.List;
import org.fisco.bcos.sdk.network.ConnectionInfo;

public interface PeerSelectRule {
    /**
     * PeerSelectRule Costomize a rule to select a peer to send message to
     *
     * @param conns
     * @return
     */
    String select(List<ConnectionInfo> conns);
}
