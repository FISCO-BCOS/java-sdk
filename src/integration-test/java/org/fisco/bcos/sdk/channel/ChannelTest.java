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
import org.fisco.bcos.sdk.config.ConfigException;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelTest {
    @Test
    public void testConnect() throws ConfigException {
        Logger logger = LoggerFactory.getLogger(ChannelImp.class);
        Channel channel = Channel.build("src/integration-test/resources/config-example.yaml");
        class TestMsgHandler implements MsgHandler {
            @Override
            public void onConnect(ChannelHandlerContext ctx) {
                logger.info("OnConnect in ChannelTest called: "+ctx.channel().remoteAddress());
            }
            @Override
            public void onMessage(ChannelHandlerContext ctx, Message msg) {
                logger.info("onMessage in ChannelTest called: "+ctx.channel().remoteAddress());
            }
            @Override
            public void onDisconnect(ChannelHandlerContext ctx) {
                logger.info("onDisconnect in ChannelTest called: "+ctx.channel().remoteAddress());
            }
        }
        TestMsgHandler testMsgHandler = new TestMsgHandler();
        channel.addConnectHandler(testMsgHandler);
        channel.addMessageHandler(MsgType.CHANNEL_RPC_REQUEST, testMsgHandler);
        channel.addDisconnectHandler(testMsgHandler);
    }
}
