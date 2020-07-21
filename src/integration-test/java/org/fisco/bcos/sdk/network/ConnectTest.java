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

import io.netty.channel.ChannelHandlerContext;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigException;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Message;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ConnectTest {
    @Test
    public void testConnect() throws ConfigException {
        ConfigOption config = Config.load("src/integration-test/resources/config-example.yaml");
        class TestMsgHandler implements MsgHandler{

            @Override
            public void onConnect(ChannelHandlerContext ctx) {
                System.out.println("OnConnect called: "+ctx.channel().remoteAddress());
            }

            @Override
            public void onMessage(ChannelHandlerContext ctx, Message msg) {
            }

            @Override
            public void onDisconnect(ChannelHandlerContext ctx) {
            }
        }
        Network network = Network.build(config,new TestMsgHandler());
        try{
            network.start();
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception is not expected");
        }

    }
}
