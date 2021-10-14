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

// TODO:
/*
public class BlockNumberNotifyHandler implements MsgHandler {
    private static Logger logger = LoggerFactory.getLogger(BlockNumberNotifyHandler.class);
    private final OnReceiveBlockNotifyFunc blockNumberUpdater;
    private final Consumer<String> disconnectHandler;

    public BlockNumberNotifyHandler(
            OnReceiveBlockNotifyFunc blockNumberUpdater, Consumer<String> disconnectHandler) {
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
        // get version
        ChannelProtocol protocol = null;
        if (ctx.channel()
                                .attr(
                                        AttributeKey.valueOf(
                                                EnumSocketChannelAttributeKey.CHANNEL_PROTOCOL_KEY
                                                        .getKey()))
                        != null
                && ctx.channel()
                                .attr(
                                        AttributeKey.valueOf(
                                                EnumSocketChannelAttributeKey.CHANNEL_PROTOCOL_KEY
                                                        .getKey()))
                                .get()
                        != null) {
            protocol =
                    (ChannelProtocol)
                            (ctx.channel()
                                    .attr(
                                            AttributeKey.valueOf(
                                                    EnumSocketChannelAttributeKey
                                                            .CHANNEL_PROTOCOL_KEY
                                                            .getKey()))
                                    .get());
        }
        // default use version 1
        EnumChannelProtocolVersion channelProtocolVersion = EnumChannelProtocolVersion.VERSION_1;
        if (protocol != null) {
            channelProtocolVersion = protocol.getEnumProtocol();
        }
        // get host
        String peerIpAndPort = ChannelVersionNegotiation.getPeerHost(ctx);
        // get block notification data
        AmopMsg amopMsg = new AmopMsg(msg);
        amopMsg.decodeAmopBody(msg.getData());
        // update block number information
        blockNumberUpdater.OnReceiveBlockNotify(channelProtocolVersion, peerIpAndPort, amopMsg);
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
*/
