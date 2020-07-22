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
import org.fisco.bcos.sdk.model.Response;
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
        return new ChannelImp(filepath);
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
     * Send a message to the given group, only send
     *
     * @param out: Message to be sent
     * @param groupId: ID of the group receiving the message packet
     */
    void broadcastToGroup(Message out, String groupId);

    /**
     * Broadcast to all peer, only send
     *
     * @param out: Message to be sent
     */
    void broadcast(Message out);

    /**
     * Synchronize interface, send a message to the given peer, and get the response
     *
     * @param out: Message to be sent
     * @param peerIpPort: Remote ip:port information
     * @return: Remote reply
     */
    Response sendToPeer(Message out, String peerIpPort);

    /**
     * Synchronize interface, randomly select nodes to send messages
     *
     * @param out: Message to be sent
     * @return: Remote reply
     */
    Response sendToRandom(Message out);

    /**
     * Synchronize interface, send message to peer select by client`s rule
     *
     * @param out: Message to be sent
     * @param rule: Rule set by client
     * @return: Remote reply
     */
    Response sendToPeerByRule(Message out, PeerSelectRule rule);

    /**
     * Asynchronous interface, send message to peer
     *
     * @param out: Message to be sent
     * @param peerIpPort: Remote ip:port information
     * @param callback: Response callback
     */
    void asyncSendToPeer(Message out, String peerIpPort, ResponseCallback callback);

    /**
     * Asynchronous interface, send to an random peer
     *
     * @param out: Message to be sent
     * @param callback: Response callback
     */
    void asyncSendToRandom(Message out, ResponseCallback callback);

    /**
     * Asynchronous interface, send message to peer select by client`s rule
     *
     * @param out: Message to be sent
     * @param rule: Rule set by client
     * @param callback: Response callback
     */
    void asyncSendToPeerByRule(Message out, PeerSelectRule rule, ResponseCallback callback);

    /**
     * Get connection information
     *
     * @return List of connection information
     */
    List<ConnectionInfo> getConnectionInfo();

    /**
     * Get available peer information
     *
     * @return List of available peer
     */
    List<String> getAvailablePeer();

    Response sendToGroup(Message out, String groupId);

    void asyncSendToGroup(Message out, String groupId, ResponseCallback callback);
}
