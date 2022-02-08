package org.fisco.bcos.sdk.codec.scale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        } else if (parameter instanceof Address) {
            encodeAddress((Address) parameter, writer);
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

    public static byte[] encode(Type parameter) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ScaleCodecWriter scaleCodecWriter = new ScaleCodecWriter(outputStream);
        encode(parameter, scaleCodecWriter);
        return outputStream.toByteArray();
    }

    public static void encodeAddress(Address address, ScaleCodecWriter writer) throws IOException {
        encodeNumeric(address.toUint160(), writer);
    }

    public static void encodeNumeric(NumericType numericType, ScaleCodecWriter writer)
            throws IOException {
        int bitSize = numericType.getBitSize();
        int byteSize = bitSize / 8;
        boolean signedInteger = (numericType.getTypeAsString().contains("uint")) ? false : true;
        if (byteSize >= 1 && byteSize <= 16) {
            if (!signedInteger) {
                writer.writeUnsignedInteger(numericType.getValue(), byteSize);
                return;
            }
            writer.writeInteger(numericType.getValue(), byteSize);
            return;
        }
        writer.writeBigInt256(signedInteger, numericType.getValue());
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
