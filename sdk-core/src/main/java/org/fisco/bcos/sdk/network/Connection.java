package org.fisco.bcos.sdk.network;

import java.io.IOException;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.model.Response;

public interface Connection {
    /** @description close connection */
    void close();

    /**
     * @return true if connected
     * @description connect to node
     */
    Boolean connect();

    /**
     * call rpc method
     *
     * @param request jsonrpc format string
     * @return response
     * @throws IOException
     */
    Response callMethod(String request) throws IOException;

    String getUri();

    String getEndPoint();

    /**
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param request The request to be sent
     * @param callback callback to be called after receiving response
     */
    void asyncCallMethod(String request, ResponseCallback callback) throws IOException;
}
