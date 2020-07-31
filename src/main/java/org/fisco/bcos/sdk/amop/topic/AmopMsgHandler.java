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

package org.fisco.bcos.sdk.amop.topic;

import io.netty.channel.ChannelHandlerContext;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.network.MsgHandler;

public class AmopMsgHandler implements MsgHandler {

    @Override
    public void onConnect(ChannelHandlerContext ctx) {}

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {}

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {}
}
