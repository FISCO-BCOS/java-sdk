package org.fisco.bcos.sdk.codec.scale;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import org.fisco.bcos.sdk.codec.scale.writer.*;

public class ScaleCodecWriter implements Closeable {
    public static final CompactUIntWriter COMPACT_UINT = new CompactUIntWriter();

    private final OutputStream out;

    public ScaleCodecWriter(OutputStream out) {
        this.out = out;
    }

    public void writeByteArray(byte[] value) throws IOException {
        out.write(value, 0, value.length);
    }

    public void writeAsList(byte[] value) throws IOException {
        writeCompact(value.length);
        out.write(value, 0, value.length);
    }

    /**
     * Write the byte into output stream as-is directly, the input is supposed to be already encoded
     *
     * @param b byte to write
     * @throws IOException if failed to write
     */
    public void directWrite(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public <T> void write(ScaleWriter<T> writer, T value) throws IOException {
        writer.write(this, value);
    }

    public void writeByte(byte value) throws IOException {
        directWrite(value);
    }

    public void writeCompact(int value) throws IOException {
        COMPACT_UINT.write(this, value);
    }
}
