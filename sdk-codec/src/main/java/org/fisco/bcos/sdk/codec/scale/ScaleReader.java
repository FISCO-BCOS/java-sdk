package org.fisco.bcos.sdk.codec.scale;

public interface ScaleReader<T> {
    /**
     * Reads value from specified reader. The reader must be positioned on the beginning of the
     * value
     *
     * @param rdr reader with the encoded data
     * @return read value
     */
    T read(ScaleCodecReader rdr);
}
