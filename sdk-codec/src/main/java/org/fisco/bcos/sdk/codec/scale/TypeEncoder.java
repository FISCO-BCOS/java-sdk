package org.fisco.bcos.sdk.codec.scale;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.fisco.bcos.sdk.codec.datatypes.*;

public class TypeEncoder {
    public static void encode(Type parameter, ScaleCodecWriter writer) throws IOException {
        if (parameter instanceof NumericType) {
            encodeNumeric((NumericType) parameter, writer);
        } else if (parameter instanceof Bool) {
            encodeBool((Bool) parameter, writer);
        } else if (parameter instanceof BytesType) {
            encodeBytes((BytesType) parameter, writer);
        } else if (parameter instanceof Utf8String) {
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
        if ((bitSize & (bitSize - 1)) != 0 && bitSize < 8) {
            throw new UnsupportedOperationException(
                    "Type cannot be encoded: " + numericType.getTypeAsString());
        }

        byte[] byteArray = new byte[bitSize >> 3];
        BigInteger value = numericType.getValue();
        byte[] byteValue = value.toByteArray();
        for (int i = 0; i < byteArray.length; ++i) {
            byteArray[i] = byteValue[byteValue.length - i - 1];
        }
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
