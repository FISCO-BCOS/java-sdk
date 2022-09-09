package org.fisco.bcos.sdk.v3.codec.scale.writer;

import java.io.IOException;
import java.math.BigInteger;
import org.fisco.bcos.sdk.v3.codec.scale.CompactMode;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecWriter;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleWriter;

public class CompactULongWriter implements ScaleWriter<Long> {
    private static final CompactBigIntWriter BIGINT_WRITER = new CompactBigIntWriter();

    @Override
    public void write(ScaleCodecWriter wrt, Long value) throws IOException {
        CompactMode mode = CompactMode.forNumber(value);
        long compact;
        int bytes;
        if (mode == CompactMode.BIGINT) {
            BIGINT_WRITER.write(wrt, BigInteger.valueOf(value));
            return;
        } else {
            compact = (value << 2) + mode.getValue();
            if (mode == CompactMode.SINGLE) {
                bytes = 1;
            } else if (mode == CompactMode.TWO) {
                bytes = 2;
            } else {
                bytes = 4;
            }
        }
        while (bytes > 0) {
            wrt.directWrite((int) compact & 0xff);
            compact >>= 8;
            bytes--;
        }
    }
}
