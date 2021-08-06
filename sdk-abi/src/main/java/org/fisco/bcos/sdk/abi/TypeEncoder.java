package org.fisco.bcos.sdk.abi;

import static org.fisco.bcos.sdk.abi.datatypes.Type.MAX_BYTE_LENGTH;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.fisco.bcos.sdk.abi.datatypes.*;

/**
 * Ethereum Contract Application Binary Interface (ABI) encoding for types. Further details are
 * available <a href= "https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 */
public class TypeEncoder {

    private TypeEncoder() {}

    public static byte[] encode(Type parameter) {
        if (parameter instanceof NumericType) {
            return encodeNumeric(((NumericType) parameter));
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
            return encodeArrayValues((StaticArray) parameter);
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
                // byte array will contain a sign byte in it's MSB, which we should
                // ignore for this unsigned integer type.
                byte[] byteArray = new byte[MAX_BYTE_LENGTH];
                System.arraycopy(value.toByteArray(), 1, byteArray, 0, MAX_BYTE_LENGTH);
                return byteArray;
            }
        }
        return value.toByteArray();
    }

    static byte[] encodeBool(Bool value) {
        byte[] rawValue = new byte[MAX_BYTE_LENGTH];
        if (value.getValue()) {
            rawValue[rawValue.length - 1] = 1;
        }
        return rawValue;
    }

    static byte[] encodeBytes(BytesType bytesType) {
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

    static byte[] encodeDynamicBytes(DynamicBytes dynamicBytes) {
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

    static byte[] encodeString(Utf8String string) {
        byte[] utfEncoded = string.getValue().getBytes(StandardCharsets.UTF_8);
        return encodeDynamicBytes(new DynamicBytes(utfEncoded));
    }

    static <T extends Type> byte[] encodeArrayValues(Array<T> value) {

        ByteArrayOutputStream encodedOffset = new ByteArrayOutputStream();
        ByteArrayOutputStream encodedValue = new ByteArrayOutputStream();

        int offset = value.getValue().size() * MAX_BYTE_LENGTH;

        try {

            for (Type type : value.getValue()) {
                byte[] r = encode(type);
                encodedValue.write(r);
                if (type.dynamicType()) {
                    encodedOffset.write(encode(new Uint(BigInteger.valueOf(offset))));
                    offset += (r.length >> 1);
                }
            }
            encodedOffset.write(encodedValue.toByteArray());
            return encodedOffset.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static <T extends Type> byte[] encodeDynamicArray(DynamicArray<T> value) {

        ByteArrayOutputStream encodedSize = new ByteArrayOutputStream();
        ByteArrayOutputStream encodedOffset = new ByteArrayOutputStream();
        ByteArrayOutputStream encodedValue = new ByteArrayOutputStream();
        try {
            encodedSize.write(encode(new Uint(BigInteger.valueOf(value.getValue().size()))));
            int offset = value.getValue().size() * MAX_BYTE_LENGTH;
            for (Type type : value.getValue()) {
                byte[] r = encode(type);
                encodedValue.write(r);
                if (type.dynamicType()) {
                    encodedOffset.write(encode(new Uint(BigInteger.valueOf(offset))));
                    offset += (r.length >> 1);
                }
            }
            encodedSize.write(encodedOffset.toByteArray());
            encodedSize.write(encodedValue.toByteArray());
            return encodedSize.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
