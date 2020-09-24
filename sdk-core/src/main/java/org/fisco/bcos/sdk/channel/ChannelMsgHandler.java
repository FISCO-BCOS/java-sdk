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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.fisco.bcos.sdk.channel.model.ChannelHandshake;
import org.fisco.bcos.sdk.channel.model.ChannelMessageError;
import org.fisco.bcos.sdk.channel.model.ChannelPrococolExceiption;
import org.fisco.bcos.sdk.channel.model.ChannelProtocol;
import org.fisco.bcos.sdk.channel.model.ChannelRequest;
import org.fisco.bcos.sdk.channel.model.EnumChannelProtocolVersion;
import org.fisco.bcos.sdk.channel.model.EnumNodeVersion;
import org.fisco.bcos.sdk.channel.model.EnumSocketChannelAttributeKey;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.fisco.bcos.sdk.utils.ChannelUtils;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of channel.
 *
 * @author chaychen
 */
public class ChannelMsgHandler implements MsgHandler {

    private static Logger logger = LoggerFactory.getLogger(ChannelImp.class);
    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private List<MsgHandler> msgConnectHandlerList = new CopyOnWriteArrayList<MsgHandler>();
    private List<MsgHandler> msgDisconnectHandleList = new CopyOnWriteArrayList<MsgHandler>();
    private Map<Integer, MsgHandler> msgHandlers = new ConcurrentHashMap<>();
    private List<MsgHandler> msgEstablishHandlerList = new CopyOnWriteArrayList<MsgHandler>();

    private Map<String, ResponseCallback> seq2Callback = new ConcurrentHashMap<>();
    private Map<String, ChannelHandlerContext> availablePeer = new ConcurrentHashMap<>();

    public Map<String, ChannelHandlerContext> getAvailablePeer() {
        return availablePeer;
    }

    public void addConnectHandler(MsgHandler handler) {
        msgConnectHandlerList.add(handler);
    }

    public void addEstablishHandler(MsgHandler handler) {
        msgEstablishHandlerList.add(handler);
    }

    public void addMessageHandler(MsgType type, MsgHandler handler) {
        msgHandlers.put(type.getType(), handler);
    }

    public void addDisconnectHandler(MsgHandler handler) {
        msgDisconnectHandleList.add(handler);
    }

    public void addSeq2CallBack(String seq, ResponseCallback callback) {
        seq2Callback.put(seq, callback);
    }

    public void removeSeq(String seq) {
        seq2Callback.remove(seq);
    }

    private void addAvailablePeer(String host, ChannelHandlerContext ctx) {
        availablePeer.put(host, ctx);
        for (MsgHandler msgHandler : msgEstablishHandlerList) {
            msgHandler.onConnect(ctx);
        }
    }

    private void removeAvailablePeers(String host) {
        if (availablePeer.containsKey(host)) {
            availablePeer.remove(host);
        }
    }

    private ResponseCallback getAndRemoveSeq(String seq) {
        ResponseCallback callback = seq2Callback.get(seq);
        seq2Callback.remove(seq);
        return callback;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        logger.debug(
                "onConnect in ChannelMsgHandler called, host : {}",
                ChannelVersionNegotiation.getPeerHost(ctx));
        queryNodeVersion(ctx);
        for (MsgHandler msgHandler : msgConnectHandlerList) {
            msgHandler.onConnect(ctx);
        }
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {
        logger.trace(
                "onMessage in ChannelMsgHandler called, host : {}, seq : {}, msgType : {}",
                ChannelVersionNegotiation.getPeerHost(ctx),
                msg.getSeq(),
                (int) msg.getType());
        ResponseCallback callback = getAndRemoveSeq(msg.getSeq());
        if (callback != null) {
            callback.cancelTimeout();
            logger.trace(
                    " receive response, seq: {}, result: {}, content: {}",
                    msg.getSeq(),
                    msg.getResult(),
                    new String(msg.getData()));

            Response response = new Response();
            if (msg.getResult() != 0) {
                response.setErrorMessage("Response error");
            }
            response.setErrorCode(msg.getResult());
            response.setMessageID(msg.getSeq());
            response.setContentBytes(msg.getData());
            response.setCtx(ctx);
            callback.onResponse(response);
        } else {
            logger.trace(
                    " receive response with invalid seq, type: {}, result: {}, content: {}",
                    (int) msg.getType(),
                    msg.getResult(),
                    new String(msg.getData()));
            MsgHandler msgHandler = msgHandlers.get(msg.getType().intValue());
            if (msgHandler != null) {
                msgHandler.onMessage(ctx, msg);
            }
        }
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        String host = ChannelVersionNegotiation.getPeerHost(ctx);
        logger.debug("onDisconnect in ChannelMsgHandler called, host : {}", host);
        for (MsgHandler handle : msgDisconnectHandleList) {
            handle.onDisconnect(ctx);
        }
        removeAvailablePeers(host);
    }

    private void queryNodeVersion(ChannelHandlerContext ctx) {
        ChannelRequest request = new ChannelRequest("getClientVersion", Arrays.asList());
        String seq = ChannelUtils.newSeq();
        Message message = new Message();
        try {
            byte[] payload = objectMapper.writeValueAsBytes(request);
            message.setSeq(seq);
            message.setResult(0);
            message.setType((short) MsgType.CHANNEL_RPC_REQUEST.getType());
            message.setData(payload);
            logger.trace(
                    "encodeRequestToMessage, seq: {}, method: {}, messageType: {}",
                    message.getSeq(),
                    request.getMethod(),
                    message.getType());
        } catch (JsonProcessingException e) {
            logger.error(
                    "encodeRequestToMessage failed for decode the message exception, errorMessage: {}",
                    e.getMessage());
        }

        ResponseCallback callback =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        Boolean disconnect = true;
                        try {
                            if (response.getErrorCode()
                                    == ChannelMessageError.MESSAGE_TIMEOUT.getError()) {
                                // The node version number is below 2.1.0 when request timeout
                                ChannelVersionNegotiation.setProtocolVersion(
                                        ctx,
                                        EnumChannelProtocolVersion.VERSION_1,
                                        "below-2.1.0-timeout");

                                logger.info(
                                        " query node version timeout, content: {}",
                                        response.getContent());
                                return;
                            } else if (response.getErrorCode() != 0) {
                                logger.error(
                                        " node version response, code: {}, message: {}",
                                        response.getErrorCode(),
                                        response.getErrorMessage());

                                throw new ChannelPrococolExceiption(
                                        " query node version failed, code: "
                                                + response.getErrorCode()
                                                + ", message: "
                                                + response.getErrorMessage());
                            }

                            NodeVersion nodeVersion =
                                    objectMapper.readValue(
                                            response.getContent(), NodeVersion.class);
                            logger.info(
                                    " node: {}, content: {}",
                                    nodeVersion.getResult(),
                                    response.getContent());

                            if (EnumNodeVersion.channelProtocolHandleShakeSupport(
                                    nodeVersion.getResult().getSupportedVersion())) {
                                // node support channel protocol handshake, start it
                                logger.info(" support channel handshake node");
                                queryChannelProtocolVersion(ctx);
                            } else { // default channel protocol
                                logger.info(" not support channel handshake set default");
                                ChannelVersionNegotiation.setProtocolVersion(
                                        ctx,
                                        EnumChannelProtocolVersion.VERSION_1,
                                        nodeVersion.getResult().getSupportedVersion());
                                addPeerHost(ctx);
                            }
                            disconnect = false;
                        } catch (Exception e) {
                            logger.error(" query node version failed, message: {}", e.getMessage());
                        }

                        if (disconnect) {
                            ctx.disconnect();
                            ctx.close();
                        }
                    }
                };

        ctx.writeAndFlush(message);
        addSeq2CallBack(seq, callback);
    }

    private void queryChannelProtocolVersion(ChannelHandlerContext ctx)
            throws ChannelPrococolExceiption {
        final String host = ChannelVersionNegotiation.getPeerHost(ctx);
        String seq = ChannelUtils.newSeq();
        Message message = new Message();

        try {
            ChannelHandshake channelHandshake = new ChannelHandshake();
            byte[] payload = objectMapper.writeValueAsBytes(channelHandshake);
            message.setSeq(seq);
            message.setResult(0);
            message.setType(Short.valueOf((short) MsgType.CLIENT_HANDSHAKE.getType()));
            message.setData(payload);
            logger.trace(
                    "encodeChannelHandshakeToMessage, seq: {}, data: {}, messageType: {}",
                    message.getSeq(),
                    channelHandshake.toString(),
                    message.getType());
        } catch (JsonProcessingException e) {
            logger.error(
                    "queryChannelProtocolVersion failed for decode the message exception, errorMessage: {}",
                    e.getMessage());
            throw new ChannelPrococolExceiption(e.getMessage());
        }

        ResponseCallback callback =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        Boolean disconnect = true;
                        try {
                            if (response.getErrorCode() != 0) {
                                logger.error(
                                        " channel protocol handshake request failed, code: {}, message: {}",
                                        response.getErrorCode(),
                                        response.getErrorMessage());
                                throw new ChannelPrococolExceiption(
                                        " channel protocol handshake request failed, code: "
                                                + response.getErrorCode()
                                                + ", message: "
                                                + response.getErrorMessage());
                            }

                            ChannelProtocol channelProtocol =
                                    objectMapper.readValue(
                                            response.getContent(), ChannelProtocol.class);
                            EnumChannelProtocolVersion enumChannelProtocolVersion =
                                    EnumChannelProtocolVersion.toEnum(
                                            channelProtocol.getProtocol());
                            channelProtocol.setEnumProtocol(enumChannelProtocolVersion);
                            logger.info(
                                    " channel protocol handshake success, set socket channel protocol, host: {}, channel protocol: {}",
                                    host,
                                    channelProtocol);

                            ctx.channel()
                                    .attr(
                                            AttributeKey.valueOf(
                                                    EnumSocketChannelAttributeKey
                                                            .CHANNEL_PROTOCOL_KEY.getKey()))
                                    .set(channelProtocol);
                            disconnect = false;
                        } catch (Exception e) {
                            logger.error(
                                    " channel protocol handshake failed, exception: {}",
                                    e.getMessage());
                        }
                        if (disconnect) {
                            ctx.disconnect();
                            ctx.close();
                        } else {
                            addPeerHost(ctx);
                        }
                    }
                };

        ctx.writeAndFlush(message);
        addSeq2CallBack(seq, callback);
    }

    private void addPeerHost(ChannelHandlerContext ctx) {
        String host = ChannelVersionNegotiation.getPeerHost(ctx);
        addAvailablePeer(host, ctx);
    }
}
