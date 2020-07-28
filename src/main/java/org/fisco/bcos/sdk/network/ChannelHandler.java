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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import org.fisco.bcos.sdk.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Channel handler process inbound message. */
@Sharable
public class ChannelHandler extends SimpleChannelInboundHandler<Message> {
    private static Logger logger = LoggerFactory.getLogger(ChannelHandler.class);
    private MsgHandler msgHandler;
    private ConnectionManager connectionManager;
    private ExecutorService msgHandleThreadPool;

    public void setMsgHandleThreadPool(ExecutorService msgHandleThreadPool) {
        this.msgHandleThreadPool = msgHandleThreadPool;
    }

    public ChannelHandler(ConnectionManager connManager, MsgHandler msgHandler) {
        this.msgHandler = msgHandler;
        this.connectionManager = connManager;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String host = ((SocketChannel) ctx.channel()).remoteAddress().getAddress().getHostAddress();
        Integer port = ((SocketChannel) ctx.channel()).remoteAddress().getPort();

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    logger.error(
                            " idle state event:{} connect{}:{} long time Inactive, disconnect",
                            e.state(),
                            host,
                            port);
                    channelInactive(ctx);
                    ctx.disconnect();
                    ctx.close();
                    break;
                default:
                    break;
            }
        } else if (evt instanceof SslHandshakeCompletionEvent) {
            SslHandshakeCompletionEvent e = (SslHandshakeCompletionEvent) evt;
            if (e.isSuccess()) {
                logger.info(
                        " handshake success, host: {}, port: {}, ctx: {}",
                        host,
                        port,
                        System.identityHashCode(ctx));
                ChannelHandlerContext oldCtx =
                        connectionManager.addConnectionContext(host, port, ctx);
                msgHandler.onConnect(ctx);

                if (Objects.nonNull(oldCtx)) {
                    oldCtx.close();
                    oldCtx.disconnect();

                    logger.warn(
                            " disconnect old connection, host: {}, port: {}, ctx: {}",
                            host,
                            port,
                            System.identityHashCode(ctx));
                }
            } else {
                logger.error(
                        " handshake failed, host: {}, port: {}, message: {}, cause: {} ",
                        host,
                        port,
                        e.cause().getMessage(),
                        e.cause());

                ctx.disconnect();
                ctx.close();
            }

        } else if (evt instanceof SslCloseCompletionEvent) {
            logger.info(
                    " ssl close completion event, host: {}, port: {}, ctx: {} ",
                    host,
                    port,
                    System.identityHashCode(ctx));
        } else {
            logger.info(
                    " userEventTriggered event, host: {}, port: {}, evt: {}, ctx: {} ",
                    host,
                    port,
                    evt,
                    System.identityHashCode(ctx));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            // lost the connection, get ip info
            String host =
                    ((SocketChannel) ctx.channel()).remoteAddress().getAddress().getHostAddress();
            Integer port = ((SocketChannel) ctx.channel()).remoteAddress().getPort();

            logger.debug(
                    " channelInactive, disconnect "
                            + host
                            + ":"
                            + String.valueOf(port)
                            + " ,"
                            + String.valueOf(ctx.channel().isActive()));

            connectionManager.removeConnectionContext(host, port, ctx);
            msgHandler.onDisconnect(ctx);

        } catch (Exception e) {
            logger.error("error ", e);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        final ChannelHandlerContext ctxF = ctx;
        final Message in = (Message) msg;

        if (msgHandleThreadPool == null) {
            msgHandler.onMessage(ctxF, in);
        } else {
            msgHandleThreadPool.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            msgHandler.onMessage(ctxF, in);
                        }
                    });
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        final ChannelHandlerContext ctxF = ctx;
        msgHandler.onMessage(ctxF, msg);
    }
}
