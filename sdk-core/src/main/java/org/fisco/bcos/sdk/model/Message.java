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
import org.fisco.bcos.sdk.model.exceptions.DecodeMessageException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Messages between sdk and FISCO BCOS node.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -7276897518418560354L;
    protected Short type = 0;
    protected Short errorCode = 0;
    protected String seq = "";
    protected byte[] data;

    public static final int HEADER_LENGTH = 4 + 2 + 32 + 4;

    public Message(Short type, String seq, byte[] data) {
        this.type = type;
        this.seq = seq;
        this.data = data;
    }

    public Message(ByteBuf in) {
        this.decode(in);
    }

    /**
     * encode the message into ByteBuf
     *
     * @param out the ByteBuf stores the encodedData
     */
    public void encode(ByteBuf out) {
        out.writeShort(this.type);
        out.writeShort(this.errorCode);
        out.writeBytes(this.seq.getBytes(), 0, 32);
        out.writeBytes(this.data);
    }

    /**
     * decode the message from the given ByteBuf
     *
     * @param in the ByteBuf that needs to decoded into the message
     */
    public void decode(ByteBuf in) {
        this.type = in.readShort();
        this.errorCode = in.readShort();
        byte[] dst = new byte[32];
        in.readBytes(dst);
        try {
            this.seq = new String(dst, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new DecodeMessageException(
                    "readHeader failed, seq: "
                            + this.seq
                            + ", type:"
                            + this.type
                            + " dataLen : "
                            + this.data.length,
                    e);
        }
        this.data = new byte[in.capacity() - in.readerIndex()];
        in.readBytes(this.data);
    }

    public Short getType() {
        return this.type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getSeq() {
        return this.seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public Short getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Short errorCode) {
        this.errorCode = errorCode;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
