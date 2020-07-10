package org.fisco.bcos.sdk.channel;

import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import java.util.Collection;
import java.util.Map;

public interface Channel {
    /**
     * Init channel module
     *
     * @param filepath config file path.
     * @return
     */
    Channel build(String filepath);

    /**
     * Add a message handler to handle specific type messages.
     * When one message comes the handler will be notified, handler.onMessage(ChannleHandlerContext ctx, Message msg) called.
     *
     * @param type
     * @param handler
     */
    void addMessageHandler(MsgType type, MsgHandler handler);

    /**
     * Add a connect handler, when one connect success, call handler.onConnect(ChannleHandlerContext ctx)is called
     *
     * @param handler
     */
    void addConnectHandler(MsgHandler handler);

    /**
     * Add a disconnect handler, when one connection disconnect, handler.onDisconnect(ChannleHandlerContext ctx) is called
     *
     * @param handler
     */
    void addDisconnectHandler(MsgHandler handler);

    /**
     * Send message to peer
     *
     * @param out        message
     * @param peerIpPort the peer to send to
     * @param callback   response callback
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
}
