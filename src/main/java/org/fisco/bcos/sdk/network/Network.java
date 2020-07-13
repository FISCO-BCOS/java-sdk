package org.fisco.bcos.sdk.network;

import java.util.List;
import org.fisco.bcos.sdk.model.Message;

/** Network interface Modules interact with the network module through this interface. */
public interface Network {
    /**
     * Init network module
     *
     * @param configFile
     * @return a Network implementation instance
     */
    static Network build(String configFile, MsgHandler handler) {
        return null;
    }

    /**
     * Broadcast message
     *
     * @param out
     */
    void broadcast(Message out);

    /**
     * Send to peer
     *
     * @param out
     * @param peerIpPort
     */
    void sendToPeer(Message out, String peerIpPort);

    /**
     * Get connection information
     *
     * @return list of connection information
     */
    List<ConnectionInfo> getConnectionInfo();
}
