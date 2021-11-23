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
        } else if (parameter instanceof Bool) {
            encodeBool((Bool) parameter, writer);
        } else if (parameter instanceof BytesType) {
            System.out.println("encodeBytes");
            encodeBytes((BytesType) parameter, writer);
        } else if (parameter instanceof Utf8String) {
            System.out.println("ut8String");
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
        if ((bitSize & (bitSize - 1)) != 0 && bitSize < 8) {
            throw new UnsupportedOperationException(
                    "Type cannot be encoded: " + numericType.getTypeAsString());
        }

        byte[] byteArray = new byte[bitSize >> 3];
        BigInteger value = numericType.getValue();
        // For FixedPointType number encoding
        if (FixedPointType.class.isAssignableFrom(numericType.getClass())){
            String regex =
                        "("
                                + Ufixed.class.getSimpleName()
                                + "|"
                                + Fixed.class.getSimpleName()
                                + ")";
                String[] splitName = numericType.getClass().getSimpleName().split(regex);
                if (splitName.length == 2) {
                    // FixedPointType processing
                    byte[] byteInt = new byte[((numericType.getBitSize()-numericType.getNBitSize()) >> 3)-1];
                    BigDecimal dValue = numericType.getDValue();
                    if (dValue.signum()<0) dValue = dValue.abs();
                    byte[] decimalByteArray = Utils.getBytesOfDecimalPart(dValue, numericType.getNBitSize());
                    if (value.signum() < 0) value = value.abs();
                    byte[] byteIntValue = value.toByteArray();

                    
                    for (int i = 0; i < byteIntValue.length; ++i) {
                        byteInt[i] = byteIntValue[byteIntValue.length - i - 1];
                    }
                    
                    ArrayUtils.reverse(byteInt);
                    
                    byteArray[0] = (numericType.getSig() == 0) ? (byte)0 : (byte)255;
                    
                    System.arraycopy(byteInt, 0, byteArray, 1, byteInt.length);
                    System.arraycopy(decimalByteArray, 0, byteArray, byteInt.length+1, decimalByteArray.length);
                }
        }else {
            byte[] byteValue = value.toByteArray();
            for (int i = 0; i < byteValue.length; ++i) {
                byteArray[i] = byteValue[byteValue.length - i - 1];
            }
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
