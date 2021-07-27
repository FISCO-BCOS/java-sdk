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

    public AmopMsg(Message msg) {
        super(msg.getType(), msg.getSeq(), msg.getData());
        super.setErrorCode(msg.getErrorCode());
        this.type = msg.getType();
        this.seq = msg.getSeq();
        this.errorCode = msg.getErrorCode();
    }

    public void decodeAmopBody(byte[] in) {
        ByteBuf amopBody = Unpooled.wrappedBuffer(in);
        if (this.errorCode == 0) {
            Short topicLength = amopBody.readUnsignedByte();
            byte[] topicBytes = new byte[topicLength - 1];
            amopBody.readBytes(topicBytes, 0, topicLength - 1);
            this.topic = new String(topicBytes);
            /*data = new byte[length - Message.HEADER_LENGTH - topicLength];
            amopBody.readBytes(data, 0, length - Message.HEADER_LENGTH - topicLength);*/
            this.data = new byte[in.length - topicLength];
            amopBody.readBytes(this.data, 0, in.length - topicLength);
        }
    }

    public Message getMessage() {
        Message msg = new Message(this.type, this.seq, null);
        msg.setErrorCode(this.errorCode);

        byte[] msgData = new byte[1 + this.topic.getBytes().length];
        ByteBuf out = Unpooled.buffer();
        out.readBytes(msgData, 0, 1 + this.topic.getBytes().length);
        msg.setData(msgData);
        return msg;
    }

    @Override
    public void encode(ByteBuf out) {
        try {
            out.writeByte(1 + this.topic.getBytes("utf-8").length);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(" topic string to utf8 failed, topic: " + this.topic);
        }
        out.writeBytes(this.topic.getBytes());
        out.writeBytes(this.data);
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String toTopic) {
        this.topic = toTopic;
    }
}
