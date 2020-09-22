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
import java.util.function.Consumer;
import org.fisco.bcos.sdk.channel.ChannelVersionNegotiation;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionNotifyHandler implements MsgHandler {
    private static Logger logger = LoggerFactory.getLogger(TransactionNotifyHandler.class);
    private final Consumer<Message> transactionNotifyReceiver;

    public TransactionNotifyHandler(Consumer<Message> transactionNotifyReceiver) {
        this.transactionNotifyReceiver = transactionNotifyReceiver;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        logger.debug("onConnect, endpoint: {}", ChannelVersionNegotiation.getPeerHost(ctx));
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {
        if (msg.getType() != MsgType.TRANSACTION_NOTIFY.getType()) {
            return;
        }
        transactionNotifyReceiver.accept(msg);
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        logger.debug("onDisconnect, endpoint: {}", ChannelVersionNegotiation.getPeerHost(ctx));
    }
}
