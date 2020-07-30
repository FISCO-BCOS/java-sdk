package org.fisco.bcos.sdk.abi.datatypes;

/** Statically allocated sequence of bytes. */
public class Bytes extends BytesType {

    public static final String TYPE_NAME = "bytes";

    public Bytes(int byteSize, byte[] value) {
        super(value, TYPE_NAME + value.length);
        if (!isValid(byteSize, value)) {
            throw new UnsupportedOperationException(
                    "Input byte array must be in range 0 < M <= 32 and length must match type");
        }
    }

    private boolean isValid(int byteSize, byte[] value) {
        int length = value.length;
        return length > 0 && length <= 32 && length == byteSize;
    }

    @Override
    public boolean dynamicType() {
        return false;
    }

    @Override
    public int offset() {
        return 1;
    }
}
