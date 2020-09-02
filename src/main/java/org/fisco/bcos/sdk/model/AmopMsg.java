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

package org.fisco.bcos.sdk.model;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.UnsupportedEncodingException;

public class AmopMsg extends Message {
    private static final long serialVersionUID = -7276897518418560354L;
    private String topic;

    public AmopMsg() {
        result = 0;
    }

    public AmopMsg(Message msg) {
        length = msg.getLength();
        type = msg.getType();
        seq = msg.getSeq();
        result = msg.getResult();
    }

    public void decodeAmopBody(byte[] in) {
        ByteBuf amopBody = Unpooled.wrappedBuffer(in);
        if (result == 0) {
            Short topicLength = amopBody.readUnsignedByte();
            byte[] topicBytes = new byte[topicLength - 1];
            amopBody.readBytes(topicBytes, 0, topicLength - 1);
            topic = new String(topicBytes);
            /*data = new byte[length - Message.HEADER_LENGTH - topicLength];
            amopBody.readBytes(data, 0, length - Message.HEADER_LENGTH - topicLength);*/
            data = new byte[in.length - topicLength];
            amopBody.readBytes(data, 0, in.length - topicLength);
        }
    }

    public Message getMessage() {
        Message msg = new Message();
        msg.setResult(this.result);
        msg.setType(this.type);
        msg.setSeq(this.seq);

        byte[] msgData = new byte[length - Message.HEADER_LENGTH + 1 + topic.length()];
        ByteBuf out = Unpooled.buffer();
        writeExtra(out);
        out.readBytes(msgData, 0, length - Message.HEADER_LENGTH + 1 + topic.length());
        msg.setData(msgData);
        return msg;
    }

    @Override
    public void encode(ByteBuf encodedData) {
        writeHeader(encodedData);
        writeExtra(encodedData);
    }

    @Override
    public void writeHeader(ByteBuf out) {
        // total length
        try {
            length = Message.HEADER_LENGTH + 1 + topic.getBytes("utf-8").length + data.length;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(" topic string to utf8 failed, topic: " + topic);
        }
        super.writeHeader(out);
    }

    public void writeExtra(ByteBuf out) {
        try {
            out.writeByte(1 + topic.getBytes("utf-8").length);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(" topic string to utf8 failed, topic: " + topic);
        }
        out.writeBytes(topic.getBytes());

        out.writeBytes(data);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String toTopic) {
        this.topic = toTopic;
    }
}
