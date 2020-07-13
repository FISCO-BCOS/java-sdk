package org.fisco.bcos.sdk.channel;

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
