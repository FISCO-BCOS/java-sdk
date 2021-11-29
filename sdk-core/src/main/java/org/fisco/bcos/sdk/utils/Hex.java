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
package org.fisco.bcos.sdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.fisco.bcos.sdk.utils.exceptions.DecoderException;
import org.fisco.bcos.sdk.utils.exceptions.EncoderException;

/** Utility class for converting hex data to bytes and back again. */
public class Hex {
    private static final HexEncoder encoder = new HexEncoder();

    public static String trimPrefix(String data) {
        if (Objects.nonNull(data) && data.startsWith("0x")) {
            return data.substring(2);
        }
        return data;
    }

    public static String toHexString(byte[] data) {
        return toHexString(data, 0, data.length);
    }

    public static String toHexString(byte[] data, int off, int length) {
        byte[] encoded = encode(data, off, length);
        return StringUtils.fromByteArray(encoded);
    }

    /**
     * encode the input data producing a Hex encoded byte array.
     *
     * @param data the input data
     * @return a byte array containing the Hex encoded data.
     */
    public static byte[] encode(byte[] data) {
        return encode(data, 0, data.length);
    }

    /**
     * encode the input data producing a Hex encoded byte array.
     *
     * @param data the input byte array
     * @param off the offset of the data to be converted
     * @param length the length of the data to be converted
     * @return a byte array containing the Hex encoded data.
     */
    public static byte[] encode(byte[] data, int off, int length) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        try {
            encoder.encode(data, off, length, bOut);
        } catch (Exception e) {
            throw new EncoderException("exception encoding Hex string: " + e.getMessage(), e);
        }

        return bOut.toByteArray();
    }

    /**
     * Hex encode the byte data writing it to the given output stream.
     *
     * @param data the byte array
     * @param out the output stream
     * @throws IOException the I/O exception
     * @return the number of bytes produced.
     */
    public static int encode(byte[] data, OutputStream out) throws IOException {
        return encoder.encode(data, 0, data.length, out);
    }

    /**
     * Hex encode the byte data writing it to the given output stream.
     *
     * @param data the byte array
     * @param off the offset of the data to be converted
     * @param length the length of the data to be converted
     * @param out the output stream
     * @throws IOException the I/O exception
     * @return the number of bytes produced.
     */
    public static int encode(byte[] data, int off, int length, OutputStream out)
            throws IOException {
        return encoder.encode(data, off, length, out);
    }

    /**
     * decode the Hex encoded input data. It is assumed the input data is valid.
     *
     * @param data the input byte array
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode(byte[] data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        try {
            encoder.decode(data, 0, data.length, bOut);
        } catch (Exception e) {
            throw new DecoderException("exception decoding Hex data: " + e.getMessage(), e);
        }

        return bOut.toByteArray();
    }

    /**
     * decode the Hex encoded String data - whitespace will be ignored.
     *
     * @param data the input byte array
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode(String data) {
        if (data.startsWith("0x")) {
            return Hex.decodeInner(data.substring(2));
        } else {
            return Hex.decodeInner(data);
        }
    }

    private static byte[] decodeInner(String data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        try {
            encoder.decode(data, bOut);
        } catch (Exception e) {
            throw new DecoderException("exception decoding Hex string: " + e.getMessage(), e);
        }

        return bOut.toByteArray();
    }

    /**
     * decode the Hex encoded String data writing it to the given output stream, whitespace
     * characters will be ignored.
     *
     * @param data the input byte array
     * @param out the output stream
     * @throws IOException the I/O exception
     * @return the number of bytes produced.
     */
    public static int decode(String data, OutputStream out) throws IOException {
        return encoder.decode(data, out);
    }
}
