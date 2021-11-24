package org.fisco.bcos.sdk.codec.scale;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.fisco.bcos.sdk.codec.Utils;
import org.fisco.bcos.sdk.codec.datatypes.*;

public class TypeEncoder {
    public static void encode(Type parameter, ScaleCodecWriter writer) throws IOException {
        if (parameter instanceof NumericType) {
            encodeNumeric((NumericType) parameter, writer);
        } else if (parameter instanceof FixedType) {
            encodeFixed((FixedType) parameter, writer);
        } else if (parameter instanceof Bool) {
            encodeBool((Bool) parameter, writer);
        } else if (parameter instanceof BytesType) {
            encodeBytes((BytesType) parameter, writer);
        } else if (parameter instanceof Utf8String) {
            System.out.println(parameter.getValue());
            encodeString((Utf8String) parameter, writer);
        } else if (parameter instanceof StructType) {
            encodeStruct((StructType) parameter, writer);
        } else if (parameter instanceof StaticArray || parameter instanceof DynamicArray) {
            encodeArray((Array) parameter, writer);
        } else {
            throw new UnsupportedOperationException(
                    "Type cannot be encoded: " + parameter.getClass());
        }
    }

    public static void encodeNumeric(NumericType numericType, ScaleCodecWriter writer)
            throws IOException {
        int bitSize = numericType.getBitSize();
        int byteSize = bitSize / 8;
        if (byteSize >= 1 && byteSize <= 16) {
            if (numericType.getTypeAsString().contains("uint")) {
                writer.writeUnsignedInteger(numericType.getValue(), byteSize);
                return;
            }
            writer.writeInteger(numericType.getValue(), byteSize);
            return;
        }
        // Note: modify with liquid u256 after modify the node
        writer.writeCompactInteger(numericType.getValue());
    }

    public static void encodeFixed(FixedType fixedType, ScaleCodecWriter writer)
            throws IOException {
        int bitSize = fixedType.getBitSize();
        if ((bitSize & (bitSize - 1)) != 0 && bitSize < 8) {
            throw new UnsupportedOperationException(
                    "Type cannot be encoded: " + fixedType.getTypeAsString());
        }
        byte[] byteArray = new byte[bitSize >> 3];
        // FixedPointType processing
        byte[] byteInt =
        new byte[((fixedType.getBitSize() - fixedType.getNBitSize()) >> 3) - 1];
        BigDecimal dValue = fixedType.getDValue();
        if (dValue.signum() < 0) dValue = dValue.abs();
        byte[] decimalByteArray =
                Utils.getBytesOfDecimalPart(dValue, fixedType.getNBitSize());
        BigInteger nValue = fixedType.getNValue();
        if (nValue.signum() < 0) nValue = nValue.abs();
        byte[] byteIntValue = nValue.toByteArray();

        for (int i = 0; i < byteIntValue.length; ++i) {
            byteInt[i] = byteIntValue[byteIntValue.length - i - 1];
        }

        ArrayUtils.reverse(byteInt);

        byteArray[0] = (fixedType.getSig() == 0) ? (byte) 0 : (byte) 255;

        System.arraycopy(byteInt, 0, byteArray, 1, byteInt.length);
        System.arraycopy(
                decimalByteArray,
                0,
                byteArray,
                byteInt.length + 1,
                decimalByteArray.length);
        // Note: modify with liquid u256 after modify the node
        writer.writeByteArray(byteArray);
    }

    public static void encodeBool(Bool boolType, ScaleCodecWriter writer) throws IOException {
        boolean value = boolType.getValue();
        writer.writeByte(value ? (byte) 1 : (byte) 0);
    }

    public static void encodeBytes(BytesType bytesType, ScaleCodecWriter writer)
            throws IOException {
        byte[] bytes = bytesType.getValue();
        writer.writeAsList(bytes);
    }

    public static void encodeString(Utf8String stringType, ScaleCodecWriter writer)
            throws IOException {
        String string = stringType.getValue();
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        writer.writeAsList(stringBytes);
    }

    public static void encodeStruct(StructType structType, ScaleCodecWriter writer)
            throws IOException {
        List<Type> componentTypes = structType.getComponentTypes();
        for (Type componentType : componentTypes) {
            encode(componentType, writer);
        }
    }

    public static void encodeArray(Array array, ScaleCodecWriter writer) throws IOException {
        List<? extends Type> values = array.getValue();
        writer.writeCompact(values.size());
        for (Type value : values) {
            encode(value, writer);
        }
    }
}
