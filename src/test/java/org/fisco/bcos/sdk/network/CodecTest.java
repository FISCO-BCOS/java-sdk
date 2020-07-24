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
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.junit.Assert;
import org.junit.Test;

public class CodecTest {
    String seq = UUID.randomUUID().toString().replaceAll("-", "");

    @Test
    public void testMsgDecoder() {
        ByteBuf in = getEventMsgByteBuf();
        MessageDecoder decoder = new MessageDecoder();
        List<Object> out = new ArrayList<>();
        try {
            decoder.decode(null, in, out);
            Message msg = (Message) out.get(0);
            Assert.assertEquals(seq, msg.getSeq());
            Assert.assertEquals(MsgType.EVENT_LOG_PUSH.ordinal(), msg.getType().intValue());
            Assert.assertEquals("data", new String(msg.getData()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMsgEncoder() {
        MessageEncoder encoder = new MessageEncoder();
        ByteBuf out = Unpooled.buffer();
        try {
            encoder.encode(null, getEventMsg(), out);
            Assert.assertEquals(out, getEventMsgByteBuf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message getEventMsg() {
        Message msg = new Message();
        msg.setSeq(seq);
        msg.setType((short) MsgType.EVENT_LOG_PUSH.ordinal());
        msg.setResult(0);
        msg.setData("data".getBytes());
        return msg;
    }

    private ByteBuf getEventMsgByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer();
        Message msg = new Message();
        msg.setSeq(seq);
        msg.setType((short) MsgType.EVENT_LOG_PUSH.ordinal());
        msg.setResult(0);
        msg.setData("data".getBytes());
        msg.encode(byteBuf);
        return byteBuf;
    }
}
