package org.fisco.bcos.sdk.codec.scale.reader;

import org.fisco.bcos.sdk.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.codec.scale.ScaleReader;

public class UByteReader implements ScaleReader<Integer> {
    @Override
    public Integer read(ScaleCodecReader rdr) {
        byte x = rdr.readByte();
        if (x < 0) {
            return 256 + (int) x;
        }
        return (int) x;
    }
}
