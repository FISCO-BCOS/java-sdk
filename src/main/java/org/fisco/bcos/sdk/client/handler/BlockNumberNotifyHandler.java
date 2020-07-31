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

package org.fisco.bcos.sdk.client.handler;

import io.netty.channel.ChannelHandlerContext;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.fisco.bcos.sdk.channel.ChannelVersionNegotiation;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockNumberNotifyHandler implements MsgHandler {
    private static Logger logger = LoggerFactory.getLogger(BlockNumberNotifyHandler.class);
    private final BiConsumer<String, Message> blockNumberUpdater;
    private final Consumer<String> disconnectHandler;

    public BlockNumberNotifyHandler(
            BiConsumer<String, Message> blockNumberUpdater, Consumer<String> disconnectHandler) {
        this.blockNumberUpdater = blockNumberUpdater;
        this.disconnectHandler = disconnectHandler;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        logger.debug("set BlockNumberNotifyHandler");
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {
        if (msg.getType() != MsgType.BLOCK_NOTIFY.getType()) {
            return;
        }
        // get host
        String peerIpAndPort = ChannelVersionNegotiation.getPeerHost(ctx);
        // update block number information
        blockNumberUpdater.accept(peerIpAndPort, msg);
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        String peerIpAndPort = ChannelVersionNegotiation.getPeerHost(ctx);
        if (disconnectHandler != null) {
            disconnectHandler.accept(peerIpAndPort);
        }
        logger.debug("onDisconnect, endpoint: {}", peerIpAndPort);
    }
}
