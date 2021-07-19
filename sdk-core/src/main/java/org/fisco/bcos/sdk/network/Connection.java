package org.fisco.bcos.sdk.network;

import java.io.IOException;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.model.Message;

public interface Connection {
    /** @description close connection */
    void close();

    /**
     * @description connect to node
     * @return true if connected
     */
    Boolean connect();

    /**
     * call rpc method
     *
     * @param request jsonrpc format string
     * @return response
     * @throws IOException
     */
    String callMethod(String request) throws IOException;

    String getUri();

    /**
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param request The request to be sent
     * @param callback callback to be called after receiving response
     */
    void asyncCallMethod(String request, ResponseCallback callback) throws IOException;
}
