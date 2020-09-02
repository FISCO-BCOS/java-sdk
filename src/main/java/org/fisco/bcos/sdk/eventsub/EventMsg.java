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

package org.fisco.bcos.sdk.eventsub;

import io.netty.buffer.ByteBuf;
import java.io.UnsupportedEncodingException;
import org.fisco.bcos.sdk.model.Message;

public class EventMsg extends Message {

    private static final long serialVersionUID = -7276897518418560354L;
    private String topic;

    public EventMsg() {}

    public EventMsg(Message msg) {
        length = msg.getLength();
        type = msg.getType();
        seq = msg.getSeq();
        result = msg.getResult();
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
