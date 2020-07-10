package org.fisco.bcos.sdk.channel;

import io.netty.channel.ChannelHandlerContext;
import org.fisco.bcos.sdk.model.Message;

/**
 * Message handler interface
 * Each module which would like to get notified by the "network" module should implement this interface
 */
public interface MsgHandler {

    /**
     * OnConnect action.
     * Called when connect success.
     * @param ctx
     */
    void onConnect(ChannelHandlerContext ctx);

    /**
     * OnMessage action.
     * Called when one message comes from the network.
     * @param ctx
     */
    void onMessage(ChannelHandlerContext ctx, Message msg);

    /**
     * OnDisconnect action
     * Called when one connection disconnect.
     * @param ctx
     */
    void onDisconnect(ChannelHandlerContext ctx);
}
