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
import io.netty.channel.ChannelHandlerContext;
import org.fisco.bcos.sdk.channel.model.EnumChannelProtocolVersion;
import org.fisco.bcos.sdk.channel.model.HeartBeatParser;
import org.fisco.bcos.sdk.channel.model.NodeHeartbeat;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.fisco.bcos.sdk.utils.ChannelUtils;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.fail;

public class ChannelTest {
    private Logger logger = LoggerFactory.getLogger(ChannelImp.class);
    private Channel channel;

    @Test
    public void testConnect() throws ConfigException {
        channel = Channel.build("src/integration-test/resources/config-example.yaml");
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
        try{
            channel.start();
            sendMessage();
            Thread.sleep(10000);
            channel.stop();
        } catch (Exception e) {
            System.out.println("testConnect failed, error info: " + e.getMessage());
            fail("Exception is not expected");
        }
    }

    // use heart beat for case to send
    private void sendMessage() {
        List<String> peers =  channel.getAvailablePeer();
        if (peers.size() == 0) {
            fail("Empty available peer");
        }
        String host = peers.get(0);
        Message message = new Message();
        try {
            message.setSeq(ChannelUtils.newSeq());
            message.setResult(0);
            message.setType(Short.valueOf((short) MsgType.CLIENT_HEARTBEAT.getType()));
            HeartBeatParser heartBeatParser = new HeartBeatParser(EnumChannelProtocolVersion.VERSION_1);
            message.setData(heartBeatParser.encode("0"));
            logger.trace(
                    "encodeHeartbeatToMessage, seq: {}, content: {}, messageType: {}",
                    message.getSeq(),
                    heartBeatParser.toString(),
                    message.getType());
        } catch (JsonProcessingException e) {
            logger.error(
                    "sendHeartbeatMessage failed for decode the message exception, errorMessage: {}",
                    e.getMessage());
            return;
        }

        ResponseCallback callback =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            NodeHeartbeat nodeHeartbeat =
                                    ObjectMapperFactory.getObjectMapper()
                                            .readValue(response.getContent(), NodeHeartbeat.class);
                            int heartBeat = nodeHeartbeat.getHeartBeat();
                            logger.trace(" heartbeat packet in ChannelTest, heartbeat is {} ", heartBeat);
                            if (heartBeat != 1) {
                                fail("heartbeat packet in ChannelTest fail");
                            }
                        } catch (Exception e) {
                            fail(" channel protocol heartbeat failed, exception: " + e.getMessage());
                        }
                    }
                };

        logger.info(" test sendToPeer");
        channel.sendToPeer(message, host);
        logger.info(" test asyncSendToPeer");
        channel.asyncSendToPeer(message, host, callback, new Options());
    }
}
