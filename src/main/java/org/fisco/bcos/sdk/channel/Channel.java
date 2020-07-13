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

package org.fisco.bcos.sdk.channel;

import java.util.List;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.network.MsgHandler;

/**
 * The channel module interface.
 *
 * @author Maggie
 */
public interface Channel {
    /**
     * Init channel module
     *
     * @param filepath config file path.
     * @return a channel instance
     */
    static Channel build(String filepath) {
        return null;
    }

    /**
     * Add a message handler to handle specific type messages. When one message comes the handler
     * will be notified, handler.onMessage(ChannleHandlerContext ctx, Message msg) called.
     *
     * @param type
     * @param handler
     */
    void addMessageHandler(MsgType type, MsgHandler handler);

    /**
     * Add a connect handler, when one connect success, call handler.onConnect(ChannleHandlerContext
     * ctx)is called
     *
     * @param handler
     */
    void addConnectHandler(MsgHandler handler);

    /**
     * Add a disconnect handler, when one connection disconnect,
     * handler.onDisconnect(ChannleHandlerContext ctx) is called
     *
     * @param handler
     */
    void addDisconnectHandler(MsgHandler handler);

    /**
     * Send message to peer
     *
     * @param out message
     * @param peerIpPort the peer to send to
     * @param callback response callback
     */
    void sendToPeer(Message out, String peerIpPort, ResponseCallback callback);

    /**
     * Send to a best peer with highest block height in Group
     *
     * @param out
     * @param groupId
     * @param callback
     */
    void sendToGroup(Message out, String groupId, ResponseCallback callback);

    /**
     * Broadcast to all peer
     *
     * @param out
     * @param callback
     */
    void broadcast(Message out, ResponseCallback callback);

    /**
     * Send to an random peer
     *
     * @param out
     * @param callback
     */
    void sendToRandom(Message out, ResponseCallback callback);

    /**
     * Get connection information
     *
     * @return List of connection information
     */
    List<ConnectionInfo> getConnectionInfo();
}
