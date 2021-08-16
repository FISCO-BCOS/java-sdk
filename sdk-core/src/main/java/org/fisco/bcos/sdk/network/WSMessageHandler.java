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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.fisco.bcos.sdk.channel.ChannelVersionNegotiation;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.ChannelMessageError;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of channel.
 *
 * @author chaychen
 */
public class WSMessageHandler implements MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(WSMessageHandler.class);
    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private final List<MsgHandler> msgDisconnectHandleList = new CopyOnWriteArrayList<MsgHandler>();
    private final Map<Integer, MsgHandler> msgHandlers = new ConcurrentHashMap<>();
    private final Map<String, ResponseCallback> seq2Callback = new ConcurrentHashMap<>();
    private final ReentrantLock seq2CallbackLock = new ReentrantLock();

    public void addMessageHandler(MsgType type, MsgHandler handler) {
        this.msgHandlers.put(type.getType(), handler);
    }

    public void addDisconnectHandler(MsgHandler handler) {
        this.msgDisconnectHandleList.add(handler);
    }

    public void addSeq2CallBack(String seq, ResponseCallback callback) {
        this.seq2CallbackLock.lock();
        this.seq2Callback.put(seq, callback);
        this.seq2CallbackLock.unlock();
    }

    private ResponseCallback getAndRemoveSeq(String seq) {
        ResponseCallback callback = this.seq2Callback.get(seq);
        this.seq2Callback.remove(seq);
        return callback;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        logger.debug(
                "onConnect in ChannelMsgHandler called, host : {}",
                ChannelVersionNegotiation.getPeerHost(ctx));
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {
        logger.debug(
                "onMessage in wsMessageHandler called, host : {}, seq : {}, msgType : {}",
                ChannelVersionNegotiation.getPeerHost(ctx),
                msg.getSeq(),
                (int) msg.getType());
        ResponseCallback callback = this.getAndRemoveSeq(msg.getSeq());
        if (callback != null) {
            callback.cancelTimeout();
            logger.trace(
                    " call registered callback, seq: {}, type: {} ,errorCode: {}",
                    msg.getSeq(),
                    msg.getType(),
                    msg.getErrorCode());
            Response response = new Response();
            if (msg.getErrorCode() != 0) {
                response.setErrorMessage("Response error");
            }
            response.setErrorCode(msg.getErrorCode().intValue());
            response.setMessageID(msg.getSeq());
            response.setContentBytes(msg.getData());
            response.setCtx(ctx);
            callback.onResponse(response);
        } else {
            MsgHandler msgHandler = this.msgHandlers.get(msg.getType().intValue());
            if (msgHandler != null) {
                logger.trace(
                        " receive message, no callback, call handler, seq:{} , type: {}, errorCode: {}",
                        msg.getSeq(),
                        (int) msg.getType(),
                        msg.getErrorCode());
                msgHandler.onMessage(ctx, msg);
            }
        }
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        logger.debug(
                "onDisconnect in ChannelMsgHandler called, host:{}", ctx.channel().remoteAddress());
        this.seq2CallbackLock.lock();
        for (String seq : this.seq2Callback.keySet()) {
            logger.debug("send message with seq {} failed ", seq);
            ResponseCallback callback = this.seq2Callback.get(seq);
            Response response = new Response();
            response.setErrorCode(ChannelMessageError.CONNECTION_INVALID.getError());
            response.setErrorMessage(
                    String.format("connection to {} lost", ctx.channel().remoteAddress()));
            response.setMessageID(seq);
            callback.onResponse(response);
        }
        this.seq2CallbackLock.unlock();
    }
}
