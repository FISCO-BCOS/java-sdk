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
import org.fisco.bcos.sdk.model.Message;

/**
 * Message handler interface Each module which would like to get notified by the "network" module
 * should implement this interface.
 */
public interface MsgHandler {

    /**
     * OnConnect action. Called when connect success.
     *
     * @param ctx ChannelHandlerContext of the connection from netty
     */
    void onConnect(ChannelHandlerContext ctx);

    /**
     * OnMessage action. Called when one message comes from the network.
     *
     * @param ctx ChannelHandlerContext of the connection from netty
     * @param msg Message from the network
     */
    void onMessage(ChannelHandlerContext ctx, Message msg);

    /**
     * OnDisconnect action Called when one connection disconnect.
     *
     * @param ctx ChannelHandlerContext of the connection from netty
     */
    void onDisconnect(ChannelHandlerContext ctx);
}
