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

import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of channel.
 *
 * @author chaychen
 */
public class ChannelMsgHandler implements MsgHandler {

    private static Logger logger = LoggerFactory.getLogger(ChannelImp.class);

    private List<MsgHandler> msgConnectHandlerList = new ArrayList<>();
    private List<MsgHandler> msgDisconnectHandleList = new ArrayList<>();
    private Map<MsgType, MsgHandler> msgHandlers = new ConcurrentHashMap<>();
    private Map<String, Object> seq2Callback = new ConcurrentHashMap<>();

    public void addConnectHandler(MsgHandler handler) {
        msgConnectHandlerList.add(handler);
    }

    public void addMessageHandler(MsgType type, MsgHandler handler) {
        msgHandlers.put(type, handler);
    }

    public void addDisconnectHandler(MsgHandler handler) {
        msgDisconnectHandleList.add(handler);
    }

    public void addSeq2CallBack(String seq, ResponseCallback callback) {
        seq2Callback.put(seq, callback);
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        // TODO:
        // queryNodeVersion
        // queryBlockNumber
        // connection info -> available peers
        for (MsgHandler handle : msgConnectHandlerList) {
            handle.onConnect(ctx);
        }
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {
        // TODO: use msgHandlers to find special type to handle
        ResponseCallback callback = (ResponseCallback) seq2Callback.get(msg.getSeq());

        if (callback != null) {
            if (callback.getTimeout() != null) {
                callback.getTimeout().cancel();
            }

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
            response.setContent(new String(msg.getData()));
            callback.onResponse(response);
            seq2Callback.remove(msg.getSeq());
        } else {
            logger.debug("no callback");
        }
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        for (MsgHandler handle : msgDisconnectHandleList) {
            handle.onDisconnect(ctx);
        }
    }
}
