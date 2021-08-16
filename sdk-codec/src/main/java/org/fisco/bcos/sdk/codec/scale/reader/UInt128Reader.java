package org.fisco.bcos.sdk.codec.scale.reader;

import java.math.BigInteger;
import org.fisco.bcos.sdk.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.codec.scale.ScaleReader;

public class UInt128Reader implements ScaleReader<BigInteger> {
    public static final int SIZE_BYTES = 16;

    public static void reverse(byte[] value) {
        for (int i = 0; i < value.length / 2; i++) {
            int other = value.length - i - 1;
            byte tmp = value[other];
            value[other] = value[i];
            value[i] = tmp;
        }
    }

    @Override
    public BigInteger read(ScaleCodecReader rdr) {
        byte[] value = rdr.readByteArray(SIZE_BYTES);
        reverse(value);
        return new BigInteger(1, value);
    }
}
