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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import java.util.concurrent.ExecutorService;
import org.fisco.bcos.sdk.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Channel handler process inbound message. */
@Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private final MsgHandler msgHandler;
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private ExecutorService msgHandleThreadPool;
    CompositeByteBuf frameByteBufCache = null;

    public void setMsgHandleThreadPool(ExecutorService msgHandleThreadPool) {
        this.msgHandleThreadPool = msgHandleThreadPool;
    }

    public WebSocketHandler(WebSocketClientHandshaker handshaker, MsgHandler msgHandler) {
        this.handshaker = handshaker;
        this.msgHandler = msgHandler;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("WebSocket Client to {} disconnected!", ctx.channel().remoteAddress());
        ctx.channel().close();
        this.msgHandler.onDisconnect(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!this.handshaker.isHandshakeComplete()) {
            try {
                this.handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                logger.info("WebSocket Client connected! endpoint:{}", ch.remoteAddress());
                this.handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                logger.info("WebSocket Client failed to connect {}", ch.remoteAddress());
                this.handshakeFuture.setFailure(e);
            }
            return;
        }

        if (!(msg instanceof WebSocketFrame)) {
            // message not websocket frame, why???
            logger.error("Client received not websocket message: {}", msg);
            ch.close();
            return;
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            if (logger.isTraceEnabled()) {
                logger.trace("WebSocket Client received message: " + textFrame.text());
            }
        } else if (frame instanceof PongWebSocketFrame) {
            if (logger.isTraceEnabled()) {
                logger.trace("WebSocket Client received PongWebSocketFrame");
            }
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
            ByteBuf content = binaryWebSocketFrame.content().copy();

            if (frameByteBufCache == null) {
                frameByteBufCache = Unpooled.compositeBuffer();
            }
            frameByteBufCache.addComponent(true, content);
            if (logger.isTraceEnabled()) {
                logger.trace("WebSocket received BinaryWebSocketFrame: {}", content);
            }
        } else if (frame instanceof ContinuationWebSocketFrame) {
            ContinuationWebSocketFrame continuationWebSocketFrame =
                    (ContinuationWebSocketFrame) frame;
            ByteBuf content = continuationWebSocketFrame.content().copy();
            if (logger.isTraceEnabled()) {
                logger.trace("WebSocket received ContinuationWebSocketFrame: {}", content);
            }

            if (frameByteBufCache == null) {
                frameByteBufCache = Unpooled.compositeBuffer();
            }
            frameByteBufCache.addComponent(true, content);
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.info("WebSocket Client received close frame, endpoint:{}", ch.remoteAddress());
            ch.close();
            this.msgHandler.onDisconnect(ctx);
            return;
        } else {
            logger.warn("WebSocket received unknown frame: {}", frame);
            ch.close();
            return;
        }

        if (frame.isFinalFragment()) {
            try {
                Message message = new Message(frameByteBufCache);
                frameByteBufCache.release();
                if (this.msgHandleThreadPool == null) {
                    this.msgHandler.onMessage(ctx, message);
                } else {
                    this.msgHandleThreadPool.execute(
                            () -> WebSocketHandler.this.msgHandler.onMessage(ctx, message));
                }
            } catch (Exception e) {
                logger.warn("channelRead0, e: ", e);
            }

            frameByteBufCache = null;
        }
    }
}
