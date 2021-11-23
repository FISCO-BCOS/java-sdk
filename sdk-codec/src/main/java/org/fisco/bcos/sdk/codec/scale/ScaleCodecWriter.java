package org.fisco.bcos.sdk.codec.scale;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.fisco.bcos.sdk.codec.scale.writer.*;

public class ScaleCodecWriter implements Closeable {
    public static class EncodingCategoryLimits {
        public static final BigInteger kMinUint16 = BigInteger.valueOf(1 << 6);
        public static final BigInteger kMinUint32 = BigInteger.valueOf(1 << 14);
        public static final BigInteger kMinBigInteger = BigInteger.valueOf(1 << 30);
    };

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

    public void writeCompactInteger(BigInteger value) throws IOException {
        if (value.compareTo(EncodingCategoryLimits.kMinUint16) < 0) {
            directWrite((byte) (value.longValue() << 2));
            return;
        }
        if (value.compareTo(EncodingCategoryLimits.kMinUint32) < 0) {
            writeSecondCategory(value);
            return;
        }
        if (value.compareTo(EncodingCategoryLimits.kMinBigInteger) < 0) {
            encodeThirdCategory(value);
            return;
        }
        writeBigInteger(value);
    }

    private void encodeThirdCategory(BigInteger value) throws IOException {
        long v = value.longValue();
        v = (v << 2) + 2;
        writeInteger(BigInteger.valueOf(v), 32);
    }

    public void writeUnsignedInteger(BigInteger value, int valueByteSize) throws IOException {
        BigInteger maxSignedValue = BigInteger.valueOf((1 << (valueByteSize * 8 - 1)) - 1);
        if (value.compareTo(maxSignedValue) <= 0) {
            writeInteger(value, valueByteSize);
            return;
        }
        // the highest bit is 1, convert to the negative
        BigInteger minOverflowUnsignedValue = BigInteger.valueOf((1 << (valueByteSize * 8)));
        if (value.compareTo(minOverflowUnsignedValue) >= 0) {
            throw new UnsupportedOperationException(
                    "writeInteger exception for overflow, value: " + value);
        }
        BigInteger convertedValue =
                BigInteger.ZERO.subtract((minOverflowUnsignedValue.subtract(value)));
        writeInteger(convertedValue, valueByteSize);
    }

    public void writeInteger(BigInteger value, int valueByteSize) throws IOException {
        byte[] byteArray = new byte[valueByteSize];
        BigInteger v = value;
        byte[] byteValue = v.toByteArray();
        if (byteArray.length < byteValue.length) {
            throw new UnsupportedOperationException(
                    "writeInteger exception for overflow, value: " + value);
        }
        for (int i = 0; i < byteValue.length; ++i) {
            byteArray[i] = byteValue[byteValue.length - i - 1];
        }
        writeByteArray(byteArray);
    }

    private void writeSecondCategory(BigInteger value) throws IOException {
        // only values from [kMinUint16, kMinUint32) can be put here
        long v = value.longValue();
        v <<= 2; // v *= 4
        v += 1; // set 0b01 flag
        byte minorByte = (byte) (v & 0xff);
        v >>= 8;
        byte majorByte = (byte) (v & 0xff);
        directWrite(minorByte);
        directWrite(majorByte);
    }

    private void writeBigInteger(BigInteger value) throws IOException {
        byte[] valueBytes = value.toByteArray();
        int requiredLen = 1 + valueBytes.length;
        if (requiredLen > 67) {
            throw new UnsupportedOperationException(
                    "encodeCompactInteger exception for COMPACT_INTEGER_TOO_BIG");
        }
        // header
        byte header = (byte) ((valueBytes.length - 4) * 4 + 3);
        writeByte(header);
        writeInteger(value, valueBytes.length);
    }

    public void writeCompact(int value) throws IOException {
        COMPACT_UINT.write(this, value);
    }
}
