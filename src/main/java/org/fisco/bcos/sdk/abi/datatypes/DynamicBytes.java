package org.fisco.bcos.sdk.abi.datatypes;

/** Dynamically allocated sequence of bytes. */
public class DynamicBytes extends BytesType {

    public static final String TYPE_NAME = "bytes";
    public static final DynamicBytes DEFAULT = new DynamicBytes(new byte[] {});

    public DynamicBytes(byte[] value) {
        super(value, TYPE_NAME);
    }

    @Override
    public boolean dynamicType() {
        return true;
    }

    @Override
    public int offset() {
        return 1;
    }
}
