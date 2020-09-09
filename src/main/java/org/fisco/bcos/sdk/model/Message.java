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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import org.fisco.bcos.sdk.model.exceptions.DecodeMessageException;

/** Messages between sdk and FISCO BCOS node. */
public class Message implements Serializable {
    private static final long serialVersionUID = -7276897518418560354L;
    protected Integer length = 0;
    protected Short type = 0;
    protected String seq = "";
    protected Integer result = 0;
    protected byte[] data;

    public static final int HEADER_LENGTH = 4 + 2 + 32 + 4;

    /**
     * encode the message into ByteBuf
     *
     * @param encodedData the ByteBuf stores the encodedData
     */
    public void encode(ByteBuf encodedData) {
        writeHeader(encodedData);
        writeDataToByteBuf(encodedData);
    }

    /**
     * decode the message from the given ByteBuf
     *
     * @param in the ByteBuf that needs to decoded into the message
     */
    public void decode(ByteBuf in) {
        readHeader(in);
        readDataFromByteBuf(in);
    }

    protected void readDataFromByteBuf(ByteBuf in) {
        data = new byte[length - HEADER_LENGTH];
        in.readBytes(data, 0, length - HEADER_LENGTH);
    }

    protected void writeDataToByteBuf(ByteBuf out) {
        out.writeBytes(data);
    }

    protected void readHeader(ByteBuf in) {
        length = in.readInt();
        type = in.readShort();
        byte[] dst = new byte[32];
        in.readBytes(dst);
        try {
            seq = new String(dst, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new DecodeMessageException(
                    "readHeader failed, seq: "
                            + seq
                            + ", type:"
                            + type
                            + " dataLen : "
                            + data.length,
                    e);
        }
        result = in.readInt();
    }

    protected void writeHeader(ByteBuf out) {
        // calculate the total length
        if (length.equals(0)) {
            length = HEADER_LENGTH + data.length;
        }

        out.writeInt(length);
        out.writeShort(type);
        out.writeBytes(seq.getBytes(), 0, 32);
        out.writeInt(result);
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.length = data.length + HEADER_LENGTH;
    }
}
