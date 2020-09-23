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
import java.util.concurrent.ExecutorService;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.fisco.bcos.sdk.network.Network;

/**
 * The channel module interface.
 *
 * @author Maggie
 */
public interface Channel {
    /**
     * Init channel module
     *
     * @param configOption config file path.
     * @return a channel instance
     * @throws ConfigException the configuration exception
     */
    static Channel build(ConfigOption configOption) throws ConfigException {
        return new ChannelImp(configOption);
    }

    Network getNetwork();

    void start();

    void stop();

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
     * Add a establish handler, when the SDK establishes a connection with the node, call the
     * handler
     *
     * @param handler
     */
    void addEstablishHandler(MsgHandler handler);

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
     * @param out Message to be sent
     * @param groupId ID of the group receiving the message packet
     */
    void broadcastToGroup(Message out, String groupId);

    /**
     * Broadcast to all peer, only send
     *
     * @param out Message to be sent
     */
    void broadcast(Message out);

    /**
     * Synchronize interface, send a message to the given peer, and get the response
     *
     * @param out Message to be sent
     * @param peerIpPort Remote ip:port information
     * @return Remote reply
     */
    Response sendToPeer(Message out, String peerIpPort);

    /**
     * Synchronize interface with timeout, send a message to the given peer, and get the response
     *
     * @param out Message to be sent
     * @param peerIpPort Remote ip:port information
     * @param options Include timeout
     * @return Remote reply
     */
    Response sendToPeerWithTimeOut(Message out, String peerIpPort, Options options);

    /**
     * Synchronize interface with timeout, randomly select nodes to send messages
     *
     * @param out Message to be sent
     * @param options Include timeout
     * @return Remote reply
     */
    Response sendToRandomWithTimeOut(Message out, Options options);

    /**
     * Synchronize interface with timeout, send message to peer select by client`s rule
     *
     * @param out Message to be sent
     * @param rule Rule set by client
     * @param options Include timeout
     * @return Remote reply
     */
    Response sendToPeerByRuleWithTimeOut(Message out, PeerSelectRule rule, Options options);

    /**
     * Asynchronous interface, send message to peer
     *
     * @param out Message to be sent
     * @param peerIpPort Remote ip:port information
     * @param callback Response callback
     * @param options Include timeout
     */
    void asyncSendToPeer(
            Message out, String peerIpPort, ResponseCallback callback, Options options);

    /**
     * Asynchronous interface, send to an random peer
     *
     * @param out Message to be sent
     * @param callback Response callback
     * @param options Include timeout
     */
    void asyncSendToRandom(Message out, ResponseCallback callback, Options options);

    /**
     * Asynchronous interface, send message to peer select by client`s rule
     *
     * @param out Message to be sent
     * @param rule Rule set by client
     * @param callback Response callback
     * @param options Include timeout
     */
    void asyncSendToPeerByRule(
            Message out, PeerSelectRule rule, ResponseCallback callback, Options options);

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

    void setThreadPool(ExecutorService threadPool);
}
