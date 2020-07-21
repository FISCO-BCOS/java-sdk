/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.test.model;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {
    @Test
    public void testMessageEncodeDecode() {
        String data = "getBlockByNumber";
        // Note: the seq must be 32 bytes
        checkAndTestMessage(
                (short) MsgType.AMOP_CLIENT_TOPICS.ordinal(), Channel.newSeq(), 0, data.getBytes());

        data = "test@12sd3*";
        checkAndTestMessage((short) 0x1010, Channel.newSeq(), 0, data.getBytes());

        data = "test\\sdf000 sd";
        checkAndTestMessage(
                (short) MsgType.EVENT_LOG_PUSH.ordinal(), Channel.newSeq(), 0, data.getBytes());

        // test json string
        data =
                "'{\"jsonrpc\":\"2.0\",\"method\":\"getPendingTxSize\",\"params\":[65535],\"id\":1}'";
        checkAndTestMessage(
                (short) MsgType.CHANNEL_RPC_REQUEST.ordinal(),
                Channel.newSeq(),
                0,
                data.getBytes());
    }

    private void checkAndTestMessage(short msgType, String seq, Integer result, byte[] data) {
        Message message = new Message();
        message.setSeq(seq);
        message.setResult(result);
        message.setType(msgType);
        message.setData(data);

        ByteBuf encodedData = Unpooled.buffer(1000);
        message.encode(encodedData);

        // decode the byteBuf into message object
        Message decodedMessage = new Message();
        decodedMessage.decode(encodedData);

        Assert.assertEquals(message.getType(), decodedMessage.getType());
        Assert.assertEquals(message.getSeq(), decodedMessage.getSeq());
        Assert.assertEquals(message.getResult(), decodedMessage.getResult());
        Assert.assertTrue(Arrays.equals(message.getData(), decodedMessage.getData()));
    }
}
