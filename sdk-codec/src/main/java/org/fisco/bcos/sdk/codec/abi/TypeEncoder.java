package org.fisco.bcos.sdk.codec.abi;

import static org.fisco.bcos.sdk.codec.datatypes.Type.MAX_BYTE_LENGTH;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.fisco.bcos.sdk.codec.Utils;
import org.fisco.bcos.sdk.codec.datatypes.*;

/**
 * Ethereum Contract Application Binary Interface (ABI) encoding for types. Further details are
 * available <a href= "https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 */
public class TypeEncoder {
    private TypeEncoder() {}

    public static byte[] encode(Type parameter) {
        if (parameter instanceof NumericType) {
            return encodeNumeric(((NumericType) parameter));
        } else if (parameter instanceof FixedType) {
            System.out.println("encoding FixedType");
            return encodeFixed(((FixedType) parameter));
        } else if (parameter instanceof Address) {
            return encodeAddress((Address) parameter);
        } else if (parameter instanceof Bool) {
            return encodeBool((Bool) parameter);
        } else if (parameter instanceof Bytes) {
            return encodeBytes((Bytes) parameter);
        } else if (parameter instanceof DynamicBytes) {
            return encodeDynamicBytes((DynamicBytes) parameter);
        } else if (parameter instanceof Utf8String) {
            return encodeString((Utf8String) parameter);
        } else if (parameter instanceof StaticArray) {
            if (DynamicStruct.class.isAssignableFrom(
                    ((StaticArray) parameter).getComponentType())) {
                return encodeStaticArrayWithDynamicStruct((StaticArray) parameter);
            } else {
                return encodeArrayValues((StaticArray) parameter);
            }
        } else if (parameter instanceof DynamicStruct) {
            return encodeDynamicStruct((DynamicStruct) parameter);
        } else if (parameter instanceof DynamicArray) {
            return encodeDynamicArray((DynamicArray) parameter);
        } else {
            throw new UnsupportedOperationException(
                    "Type cannot be encoded: " + parameter.getClass());
        }
    }

    public static byte[] encodeAddress(Address address) {
        return encodeNumeric(address.toUint160());
    }

    public static byte[] encodeFixed(FixedType fixedType) {
        int bitSize = fixedType.getBitSize();
        if ((bitSize & (bitSize - 1)) != 0 && bitSize < 8) {
            throw new UnsupportedOperationException(
                    "Type cannot be encoded: " + fixedType.getTypeAsString());
        }
        byte[] byteArray = new byte[bitSize >> 3];
        // FixedPointType processing
        byte[] byteInt = new byte[((fixedType.getBitSize() - fixedType.getNBitSize()) >> 3) - 1];
        BigDecimal dValue = fixedType.getDValue();
        if (dValue.signum() < 0) dValue = dValue.abs();
        byte[] decimalByteArray = Utils.getBytesOfDecimalPart(dValue, fixedType.getNBitSize());
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
                decimalByteArray, 0, byteArray, byteInt.length + 1, decimalByteArray.length);
        return byteArray;
    }

    public static byte[] encodeNumeric(NumericType numericType) {
        byte[] rawValue = toByteArray(numericType);
        byte paddingValue = getPaddingValue(numericType);
        byte[] paddedRawValue = new byte[MAX_BYTE_LENGTH];
        if (paddingValue != 0) {
            for (int i = 0; i < paddedRawValue.length; i++) {
                paddedRawValue[i] = paddingValue;
            }
        }

        System.arraycopy(
                rawValue, 0, paddedRawValue, MAX_BYTE_LENGTH - rawValue.length, rawValue.length);
        return paddedRawValue;
    }

    private static byte getPaddingValue(NumericType numericType) {
        if (numericType.getValue().signum() == -1) {
            return (byte) 0xff;
        } else {
            return 0;
        }
    }

    private static byte[] toByteArray(NumericType numericType) {
        BigInteger value = numericType.getValue();
        if (numericType instanceof Ufixed || numericType instanceof Uint) {
            if (value.bitLength() == Type.MAX_BIT_LENGTH) {
                // As BigInteger is signed, if we have a 256 bit value, the resultant
                // byte array will contain a sign byte in its MSB, which we should
                // ignore for this unsigned integer type.
                byte[] byteArray = new byte[MAX_BYTE_LENGTH];
                System.arraycopy(value.toByteArray(), 1, byteArray, 0, MAX_BYTE_LENGTH);
                return byteArray;
            }
        }
        return value.toByteArray();
    }

    public static byte[] encodeBool(Bool value) {
        byte[] rawValue = new byte[MAX_BYTE_LENGTH];
        if (value.getValue()) {
            rawValue[rawValue.length - 1] = 1;
        }
        return rawValue;
    }

    public static byte[] encodeBytes(BytesType bytesType) {
        byte[] value = bytesType.getValue();
        int length = value.length;
        int mod = length % MAX_BYTE_LENGTH;

        byte[] dest;
        if (mod != 0) {
            int padding = MAX_BYTE_LENGTH - mod;
            dest = new byte[length + padding];
            System.arraycopy(value, 0, dest, 0, length);
        } else {
            dest = value;
        }
        return dest;
    }

    public static byte[] encodeDynamicBytes(DynamicBytes dynamicBytes) {
        int size = dynamicBytes.getValue().length;
        byte[] encodedLength = encode(new Uint(BigInteger.valueOf(size)));
        byte[] encodedValue = encodeBytes(dynamicBytes);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(encodedLength);
            byteArrayOutputStream.write(encodedValue);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] encodeString(Utf8String string) {
        byte[] utfEncoded = string.getValue().getBytes(StandardCharsets.UTF_8);
        return encodeDynamicBytes(new DynamicBytes(utfEncoded));
    }

    public static <T extends Type> byte[] encodeArrayValues(Array<T> value) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            for (Type type : value.getValue()) {
                result.write(encode(type));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toByteArray();
    }

    public static <T extends Type> byte[] encodeDynamicArray(DynamicArray<T> value) {
        int size = value.getValue().size();
        byte[] encodedLength = encode(new Uint(BigInteger.valueOf(size)));
        byte[] valuesOffsets = encodeArrayValuesOffsets(value);
        byte[] encodedValues = encodeArrayValues(value);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            result.write(encodedLength);
            result.write(valuesOffsets);
            result.write(encodedValues);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toByteArray();
    }

    static <T extends Type> byte[] encodeArrayValuesOffsets(DynamicArray<T> value) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        boolean arrayOfBytes =
                !value.getValue().isEmpty() && value.getValue().get(0) instanceof DynamicBytes;
        boolean arrayOfString =
                !value.getValue().isEmpty() && value.getValue().get(0) instanceof Utf8String;
        boolean arrayOfDynamicStructs =
                !value.getValue().isEmpty() && value.getValue().get(0) instanceof DynamicStruct;
        try {
            if (arrayOfBytes || arrayOfString) {
                long offset = 0;
                for (int i = 0; i < value.getValue().size(); i++) {
                    if (i == 0) {
                        offset = value.getValue().size() * MAX_BYTE_LENGTH;
                    } else {
                        int bytesLength =
                                arrayOfBytes
                                        ? ((byte[]) value.getValue().get(i - 1).getValue()).length
                                        : ((String) value.getValue().get(i - 1).getValue())
                                                .length();
                        int numberOfWords = (bytesLength + MAX_BYTE_LENGTH - 1) / MAX_BYTE_LENGTH;
                        int totalBytesLength = numberOfWords * MAX_BYTE_LENGTH;
                        offset += totalBytesLength + MAX_BYTE_LENGTH;
                    }
                    result.write(
                            toBytesPadded(new BigInteger(Long.toString(offset)), MAX_BYTE_LENGTH));
                }
            } else if (arrayOfDynamicStructs) {
                result.write(encodeStructsArraysOffsets(value));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toByteArray();
    }

    static <T extends Type> byte[] encodeStructsArraysOffsets(Array<T> value) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        long offset = value.getValue().size();
        List<byte[]> tailsEncoding =
                value.getValue().stream().map(TypeEncoder::encode).collect(Collectors.toList());
        for (int i = 0; i < value.getValue().size(); i++) {
            if (i == 0) {
                offset = offset * MAX_BYTE_LENGTH;
            } else {
                offset += tailsEncoding.get(i - 1).length;
            }
            try {
                result.write(toBytesPadded(new BigInteger(Long.toString(offset)), MAX_BYTE_LENGTH));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result.toByteArray();
    }

    static byte[] toBytesPadded(BigInteger value, int length) {
        byte[] result = new byte[length];
        byte[] bytes = value.toByteArray();

        int bytesLength;
        int srcOffset;
        if (bytes[0] == 0) {
            bytesLength = bytes.length - 1;
            srcOffset = 1;
        } else {
            bytesLength = bytes.length;
            srcOffset = 0;
        }

        if (bytesLength > length) {
            throw new RuntimeException("Input is too large to put in byte array of size " + length);
        }

        int destOffset = length - bytesLength;
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
        return result;
    }

    static <T extends Type> byte[] encodeStaticArrayWithDynamicStruct(Array<T> value) {
        byte[] valuesOffsets = encodeStructsArraysOffsets(value);
        byte[] encodedValues = encodeArrayValues(value);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {

            result.write(valuesOffsets);
            result.write(encodedValues);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toByteArray();
    }

    public static byte[] encodeDynamicStruct(final DynamicStruct value) {
        byte[] encodedValues = encodeDynamicStructValues(value);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            result.write(encodedValues);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toByteArray();
    }

    private static byte[] encodeDynamicStructValues(final DynamicStruct value) {
        int staticSize = 0;
        for (int i = 0; i < value.getValue().size(); ++i) {
            final Type type = value.getValue().get(i);
            if (isDynamic(type)) {
                staticSize += 32;
            } else {
                staticSize += type.bytes32PaddedLength();
            }
        }
        int dynamicOffset = staticSize;
        final List<byte[]> offsetsAndStaticValues = new ArrayList<>();
        final List<byte[]> dynamicValues = new ArrayList<>();
        for (int i = 0; i < value.getValue().size(); ++i) {
            final Type type = value.getValue().get(i);
            if (isDynamic(type)) {
                offsetsAndStaticValues.add(
                        toBytesPadded(
                                new BigInteger(Long.toString(dynamicOffset)), MAX_BYTE_LENGTH));
                byte[] encodedValue = encode(type);
                dynamicValues.add(encodedValue);
                dynamicOffset += encodedValue.length;
            } else {
                offsetsAndStaticValues.add(encode(value.getValue().get(i)));
            }
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            for (byte[] offsetsAndStaticValue : offsetsAndStaticValues) {
                result.write(offsetsAndStaticValue);
            }
            for (byte[] dynamicValue : dynamicValues) {
                result.write(dynamicValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result.toByteArray();
    }

    static boolean isDynamic(Type parameter) {
        return parameter instanceof DynamicBytes
                || parameter instanceof Utf8String
                || parameter instanceof DynamicArray
                || (parameter instanceof StaticArray
                        && DynamicStruct.class.isAssignableFrom(
                                ((StaticArray) parameter).getComponentType()));
    }
}
