package org.fisco.bcos.sdk.codec.scale;

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

    /**
     * Read string, encoded as UTF-8 bytes
     *
     * @return string value
     */
    public String readString() {
        return new String(readByteArray());
    }
}
