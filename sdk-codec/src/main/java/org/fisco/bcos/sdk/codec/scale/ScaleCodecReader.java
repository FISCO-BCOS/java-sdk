package org.fisco.bcos.sdk.codec.scale;

import java.math.BigInteger;
import org.apache.commons.lang3.ArrayUtils;
import org.fisco.bcos.sdk.codec.scale.reader.*;

public class ScaleCodecReader {
    public static final UByteReader UBYTE = new UByteReader();
    public static final CompactUIntReader COMPACT_UINT = new CompactUIntReader();
    public static final BoolReader BOOL = new BoolReader();

    private final byte[] source;
    private int pos = 0;

    public ScaleCodecReader(byte[] source) {
        this.source = source;
    }

    /** @return true if has more elements */
    public boolean hasNext() {
        return pos < source.length;
    }

    public boolean hasMore(int size) {
        if (size == 0) {
            return true;
        }
        return (pos + size - 1) < source.length;
    }
    /** @return a next single byte from reader */
    public byte readByte() {
        if (!hasNext()) {
            throw new IndexOutOfBoundsException("Cannot read " + pos + " of " + source.length);
        }
        return source[pos++];
    }

    /**
     * Read complex value from the reader
     *
     * @param scaleReader reader implementation
     * @param <T> resulting type
     * @return read value
     */
    public <T> T read(ScaleReader<T> scaleReader) {
        if (scaleReader == null) {
            throw new NullPointerException("ItemReader cannot be null");
        }
        return scaleReader.read(this);
    }

    public int readUByte() {
        return UBYTE.read(this);
    }

    public int readCompact() {
        return COMPACT_UINT.read(this);
    }

    public boolean readBoolean() {
        return BOOL.read(this);
    }

    public byte[] readByteArray() {
        int len = readCompact();
        return readByteArray(len);
    }

    public byte[] readByteArray(int len) {
        byte[] result = new byte[len];
        System.arraycopy(source, pos, result, 0, result.length);
        pos += len;
        return result;
    }

    public BigInteger decodeInteger(boolean signed, int bytesSize) {
        if (hasMore(bytesSize) == false) {
            throw new UnsupportedOperationException("decodeInteger exception for not enough data");
        }
        byte[] resultBytes = readByteArray(bytesSize);
        ArrayUtils.reverse(resultBytes);
        BigInteger value = new BigInteger(resultBytes);
        if (value.compareTo(BigInteger.ZERO) < 0 && signed == false) {
            BigInteger minOverflowUnsignedValue = BigInteger.valueOf((1 << (bytesSize * 8)));
            return value.add(minOverflowUnsignedValue);
        }
        return value;
    }

    public BigInteger decodeInt256() {
        if (hasMore(32) == false) {
            throw new UnsupportedOperationException("decodeInt256 exception for not enough data");
        }
        byte[] data = readByteArray(32);
        return new BigInteger(data);
    }

    public BigInteger decodeCompactInteger() {
        byte firstByte = readByte();
        int flag = ((int) (firstByte) & 0b00000011);
        if (flag == 0b00) {
            return BigInteger.valueOf(firstByte >> 2);
        }
        if (flag == 0b01) {
            byte secondByte = readByte();
            int value = ((firstByte & 0b11111100) + (secondByte * 256)) >> 2;
            return BigInteger.valueOf(value);
        }
        if (flag == 0b10) {
            long number = firstByte;
            long multiplier = 256;
            if (!hasNext()) {
                throw new UnsupportedOperationException(
                        "decodeCompactInteger exception for not enough data");
            }
            for (int i = 0; i < 3; i++) {
                // we assured that there are 3 more bytes,
                // no need to make checks in a loop
                number += readByte() * multiplier;
                multiplier = multiplier << 8;
            }
            number = number >> 2;
            return BigInteger.valueOf(number);
        }
        if (flag == 0b11) {
            int bytesCount = (firstByte >> 2) + 4;
            if (!hasNext()) {
                throw new UnsupportedOperationException(
                        "decodeCompactInteger exception for not enough data");
            }
            BigInteger multiplier = BigInteger.valueOf(1);
            BigInteger value = BigInteger.valueOf(0);
            // we assured that there are m more bytes,
            // no need to make checks in a loop
            for (int i = 0; i < bytesCount; i++) {
                value.add(multiplier.multiply(BigInteger.valueOf(readByte())));
                multiplier.multiply(BigInteger.valueOf(256));
            }
            return value;
        }
        throw new UnsupportedOperationException(
                "decodeCompactInteger exception for not supported flag, flag:" + flag);
    }

    /**
     * Read string, encoded as UTF-8 bytes
     *
     * @return string value
     */
    public String readString() {
        return new String(readByteArray());
    }
}
