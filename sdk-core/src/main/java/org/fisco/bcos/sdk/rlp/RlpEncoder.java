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
package org.fisco.bcos.sdk.rlp;

import static org.fisco.bcos.sdk.rlp.RlpDecoder.OFFSET_SHORT_LIST;
import static org.fisco.bcos.sdk.rlp.RlpDecoder.OFFSET_SHORT_STRING;

import java.util.Arrays;
import java.util.List;

/** Recursive Length Prefix (RLP) encoder. */
public final class RlpEncoder {

    private RlpEncoder() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] encode(RlpType value) {
        if (value instanceof RlpString) {
            return encodeString((RlpString) value);
        } else {
            return encodeList((RlpList) value);
        }
    }

    private static byte[] encode(byte[] bytesValue, int offset) {
        if (bytesValue.length == 1
                && offset == OFFSET_SHORT_STRING
                && bytesValue[0] >= (byte) 0x00
                && bytesValue[0] <= (byte) 0x7f) {
            return bytesValue;
        } else if (bytesValue.length <= 55) {
            byte[] result = new byte[bytesValue.length + 1];
            result[0] = (byte) (offset + bytesValue.length);
            System.arraycopy(bytesValue, 0, result, 1, bytesValue.length);
            return result;
        } else {
            byte[] encodedStringLength = toMinimalByteArray(bytesValue.length);
            byte[] result = new byte[bytesValue.length + encodedStringLength.length + 1];

            result[0] = (byte) ((offset + 0x37) + encodedStringLength.length);
            System.arraycopy(encodedStringLength, 0, result, 1, encodedStringLength.length);
            System.arraycopy(
                    bytesValue, 0, result, encodedStringLength.length + 1, bytesValue.length);
            return result;
        }
    }

    private static byte[] encodeString(RlpString value) {
        return encode(value.getBytes(), OFFSET_SHORT_STRING);
    }

    private static byte[] toMinimalByteArray(int value) {
        byte[] encoded = toByteArray(value);

        for (int i = 0; i < encoded.length; i++) {
            if (encoded[i] != 0) {
                return Arrays.copyOfRange(encoded, i, encoded.length);
            }
        }

        return new byte[] {};
    }

    private static byte[] toByteArray(int value) {
        return new byte[] {
            (byte) ((value >> 24) & 0xff),
            (byte) ((value >> 16) & 0xff),
            (byte) ((value >> 8) & 0xff),
            (byte) (value & 0xff)
        };
    }

    private static byte[] encodeList(RlpList value) {
        List<RlpType> values = value.getValues();
        if (values.isEmpty()) {
            return encode(new byte[] {}, OFFSET_SHORT_LIST);
        } else {
            byte[] result = new byte[0];
            for (RlpType entry : values) {
                result = concat(result, encode(entry));
            }
            return encode(result, OFFSET_SHORT_LIST);
        }
    }

    private static byte[] concat(byte[] b1, byte[] b2) {
        byte[] result = Arrays.copyOf(b1, b1.length + b2.length);
        System.arraycopy(b2, 0, result, b1.length, b2.length);
        return result;
    }
}
