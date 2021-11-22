package org.fisco.bcos.sdk.codec.scale;

import static org.fisco.bcos.sdk.codec.Utils.getSimpleTypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.apache.commons.lang3.ArrayUtils;
import org.fisco.bcos.sdk.codec.Utils;
import org.fisco.bcos.sdk.codec.datatypes.*;

public class TypeDecoder {
    @SuppressWarnings("unchecked")
    public static <T extends Type> T decode(ScaleCodecReader reader, Class<T> type) {
        if (NumericType.class.isAssignableFrom(type)) {
            return (T) decodeNumeric(reader, (Class<NumericType>) type);
        } else if (Bool.class.isAssignableFrom(type)) {
            return (T) decodeBool(reader);
        } else if (Bytes.class.isAssignableFrom(type)
                || DynamicBytes.class.isAssignableFrom(type)) {
            return (T) decodeBytes(reader, (Class<Bytes>) type);
        } else if (Utf8String.class.isAssignableFrom(type)) {
            return (T) decodeUtf8String(reader);
        } else if (DynamicArray.class.isAssignableFrom(type)) {
            return (T) decodeDynamicArray(reader, TypeReference.create(type));
        } else if (StructType.class.isAssignableFrom(type)) {
            return (T) decodeStruct(reader, TypeReference.create(type));
        } else if (StaticArray.class.isAssignableFrom(type)) {
            return (T) decodeStaticArray(reader, TypeReference.create(type));
        } else if (Array.class.isAssignableFrom(type)) {
            throw new UnsupportedOperationException(
                    "Array types must be wrapped in a TypeReference");
        } else {
            throw new UnsupportedOperationException("Type cannot be decoded: " + type.getClass());
        }
    }

    public static <T extends NumericType> T decodeNumeric(ScaleCodecReader reader, Class<T> type) {

        try {
            int bitSize = 256;
            if (IntType.class.isAssignableFrom(type)) {
                String regex =
                        "(" + Uint.class.getSimpleName() + "|" + Int.class.getSimpleName() + ")";
                String[] splitName = type.getSimpleName().split(regex);
                if (splitName.length == 2) {
                    bitSize = Integer.parseInt(splitName[1]);
                }
            } else if (FixedPointType.class.isAssignableFrom(type)) {
                String regex =
                        "("
                                + Ufixed.class.getSimpleName()
                                + "|"
                                + Fixed.class.getSimpleName()
                                + ")";
                String[] splitName = type.getSimpleName().split(regex);
                if (splitName.length == 2) {
                    String[] bitsCounts = splitName[1].split("x");
                    // newly define the size is left to "x"
                    bitSize = Integer.parseInt(bitsCounts[0]);
                    int nbitSize = Integer.parseInt(bitsCounts[1]);
                    // int part
                    byte[] sig = reader.readByteArray(1);
                    byte[] resultIntBytes = reader.readByteArray(((bitSize - nbitSize) >> 3) - 1);
                    // decimal part
                    byte[] resultDecBytes = reader.readByteArray(nbitSize >> 3);
                    BigInteger numericIntValue = new BigInteger(resultIntBytes);

                    BigDecimal result = Utils.processFixedScaleDecode(resultDecBytes, nbitSize);
                    BigDecimal finalResult =
                            (sig[0] == (byte) 0)
                                    ? result.add(new BigDecimal(numericIntValue))
                                    : result.add(new BigDecimal(numericIntValue)).negate();
                    return type.getConstructor(BigDecimal.class).newInstance(finalResult);
                }
            }

            byte[] resultBytes = reader.readByteArray(bitSize >> 3);
            ArrayUtils.reverse(resultBytes);
            BigInteger numericValue = new BigInteger(resultBytes);
            return type.getConstructor(BigInteger.class).newInstance(numericValue);
        } catch (NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new UnsupportedOperationException(
                    "Unable to create instance of " + type.getName() + ": " + e.getMessage(), e);
        }
    }

    public static Bool decodeBool(ScaleCodecReader reader) {
        boolean boolValue = reader.readBoolean();
        return new Bool(boolValue);
    }

    public static <T extends Bytes> T decodeBytes(ScaleCodecReader reader, Class<T> type) {
        try {
            byte[] bytes = reader.readByteArray();
            return type.getConstructor(byte[].class).newInstance(bytes);
        } catch (NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new UnsupportedOperationException(
                    "Unable to create instance of " + type.getName(), e);
        }
    }

    public static Utf8String decodeUtf8String(ScaleCodecReader reader) {
        String string = reader.readString();
        return new Utf8String(string);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> T decodeArray(
            ScaleCodecReader reader,
            TypeReference<T> typeReference,
            BiFunction<List<T>, String, T> consumer,
            Integer length) {
        int len = length == null ? reader.readCompact() : length;
        if (len == 0) {
            throw new UnsupportedOperationException("Zero length fixed array is invalid type");
        }

        try {
            Class<T> cls = Utils.getParameterizedTypeFromArray(typeReference);
            List<T> elements = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                T value = decode(reader, cls);
                elements.add(value);
            }

            String typeName = getSimpleTypeName(cls);
            return consumer.apply(elements, typeName);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                    "Unable to access parameterized type " + typeReference.getType().getTypeName(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> T decodeStaticArray(
            ScaleCodecReader reader, TypeReference<T> typeReference) {
        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        throw new UnsupportedOperationException(
                                "Zero length fixed array is invalid type");
                    } else {
                        try {
                            Class<? extends StaticArray> arrayClass =
                                    (Class<? extends StaticArray>)
                                            Class.forName(
                                                    "org.fisco.bcos.sdk.codec.datatypes.generated.StaticArray"
                                                            + elements.size());
                            return (T) arrayClass.getConstructor(List.class).newInstance(elements);
                        } catch (ReflectiveOperationException e) {
                            throw new UnsupportedOperationException(e);
                        }
                    }
                };
        int length =
                Integer.parseInt(
                        ((ParameterizedType) typeReference.getType())
                                .getRawType()
                                .getClass()
                                .getSimpleName()
                                .substring(StaticArray.class.getSimpleName().length()));
        return decodeArray(reader, typeReference, function, length);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> T decodeDynamicArray(
            ScaleCodecReader reader, TypeReference<T> typeReference) {
        BiFunction<List<T>, String, T> function =
                (elements, typName) -> (T) new DynamicArray(AbiTypes.getType(typName), elements);
        return decodeArray(reader, typeReference, function, null);
    }

    public static <T extends Type> T decodeStruct(
            ScaleCodecReader reader, TypeReference<T> typeReference) {
        try {
            Class<T> classType = typeReference.getClassType();
            Constructor<?> constructor =
                    Arrays.stream(classType.getDeclaredConstructors())
                            .filter(
                                    declaredConstructor ->
                                            Arrays.stream(declaredConstructor.getParameterTypes())
                                                    .allMatch(Type.class::isAssignableFrom))
                            .findAny()
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "TypeReferenced struct must contain a constructor with types that extend Type"));
            final int length = constructor.getParameterCount();
            List<T> elements = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                final Class<T> declaredField = (Class<T>) constructor.getParameterTypes()[i];
                T value = decode(reader, declaredField);
                elements.add(value);
            }

            constructor.setAccessible(true);
            return (T) constructor.newInstance(elements.toArray());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new UnsupportedOperationException(
                    "Unable to access parameterized type " + typeReference.getType().getTypeName(),
                    e);
        }
    }
}
