/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.network;

import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.model.Message;

/** Network interface Modules interact with the network module through this interface. */
public interface Network {
    /**
     * Init network module
     *
     * @param configFilePath the path of the yaml config file
     * @param handler message handler
     * @return a Network implementation instance
     */
    static Network build(String configFilePath, MsgHandler handler) throws ConfigException {
        return new NetworkImp(configFilePath, handler);
    }

    ConfigOption getConfigOption();

    int getSslCryptoType();

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
    void sendToPeer(Message out, String peerIpPort) throws NetworkException;

    /**
     * Get connection information
     *
     * @return list of connection information
     */
    List<ConnectionInfo> getConnectionInfo();

    /**
     * Start connect peers
     *
     * @throws NetworkException
     */
    void start() throws NetworkException;

    /**
     * Get available connection context
     *
     * @return Map<String, ChannelHandlerContext> String for the IP:Port of a peer
     */
    Map<String, ChannelHandlerContext> getAvailableConnections();

    /**
     * Remove the connection if version negotiation failed
     *
     * @param peerIpPort
     */
    void removeConnection(String peerIpPort);

    /**
     * Set thread pool
     *
     * @param threadPool
     */
    void setMsgHandleThreadPool(ExecutorService threadPool);

    /** Exit gracefully */
    void stop();
}
