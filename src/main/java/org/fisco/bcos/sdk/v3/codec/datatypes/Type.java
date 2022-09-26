package org.fisco.bcos.sdk.v3.codec.datatypes;

/** ABI Types. */
public interface Type<T> {
    int MAX_BIT_LENGTH = 256;
    int MAX_BYTE_LENGTH = MAX_BIT_LENGTH / 8;

    T getValue();

    String getTypeAsString();

    default int bytes32PaddedLength() {
        return MAX_BYTE_LENGTH;
    }
}
