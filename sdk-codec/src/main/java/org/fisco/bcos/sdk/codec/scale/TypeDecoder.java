package org.fisco.bcos.sdk.codec.scale;

import static org.fisco.bcos.sdk.codec.Utils.getSimpleTypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.fisco.bcos.sdk.codec.Utils;
import org.fisco.bcos.sdk.codec.datatypes.*;
import org.fisco.bcos.sdk.codec.datatypes.generated.Uint160;
import org.fisco.bcos.sdk.utils.Hex;

public class TypeDecoder {
    @SuppressWarnings("unchecked")
    public static <T extends Type> T decode(ScaleCodecReader reader, TypeReference<T> typeReference)
            throws ClassNotFoundException {
        Class<T> type = typeReference.getClassType();
        if (NumericType.class.isAssignableFrom(type)) {
            return (T) decodeNumeric(reader, (Class<NumericType>) type);
        } else if (Bool.class.isAssignableFrom(type)) {
            return (T) decodeBool(reader);
        } else if (Address.class.isAssignableFrom(type)) {
            return (T) decodeAddress(reader);
        } else if (Bytes.class.isAssignableFrom(type)) {
            // static bytes
            return (T) decodeStaticBytes(reader, (Class<Bytes>) type);
        } else if (BytesType.class.isAssignableFrom(type)) {
            // dynamic bytes
            return (T) decodeBytes(reader, (Class<DynamicBytes>) type);
        } else if (Utf8String.class.isAssignableFrom(type)) {
            return (T) decodeUtf8String(reader);
        } else if (StructType.class.isAssignableFrom(type)) {
            return (T) decodeStruct(reader, typeReference);
        } else if (DynamicArray.class.isAssignableFrom(type)) {
            return (T) decodeDynamicArray(reader, typeReference);
        } else if (StaticArray.class.isAssignableFrom(type)) {
            return (T) decodeStaticArray(reader, typeReference);
        } else {
            throw new UnsupportedOperationException("Type cannot be decoded: " + type);
        }
    }

    public static <T extends Type> T decode(String input, TypeReference<T> typeReference)
            throws ClassNotFoundException {
        ScaleCodecReader scaleCodecReader = new ScaleCodecReader(Hex.decode(input));
        return decode(scaleCodecReader, typeReference);
    }

    public static Address decodeAddress(ScaleCodecReader reader) {
        return new Address(decodeNumeric(reader, Uint160.class));
    }

    public static <T extends NumericType> T decodeNumeric(ScaleCodecReader reader, Class<T> type) {
        try {
            int bitSize = 256;
            // TODO: optimize here(duplicated code with abi)
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
                    bitSize = Integer.parseInt(bitsCounts[0]) + Integer.parseInt(bitsCounts[1]);
                }
            }
            int bytesSize = bitSize >> 3;
            BigInteger value;
            boolean signedValue = type.toString().contains("Uint") ? false : true;
            if (bytesSize >= 1 && bytesSize <= 16) {
                value = reader.decodeInteger(signedValue, bytesSize);
            } else {
                value = reader.decodeInt256();
            }
            return type.getConstructor(BigInteger.class).newInstance(value);
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

    public static <T extends BytesType> T decodeBytes(ScaleCodecReader reader, Class<T> type) {
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

    public static <T extends BytesType> T decodeStaticBytes(
            ScaleCodecReader reader, Class<T> type) {
        try {
            int size =
                    Integer.parseInt(
                            type.getTypeName()
                                    .substring(
                                            type.getTypeName().lastIndexOf("Bytes")
                                                    + "Bytes".length()));
            byte[] bytes = reader.readByteArray(size);
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

        try {
            Class<T> cls = Utils.getParameterizedTypeFromArray(typeReference);
            List<T> elements = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                T value = decode(reader, TypeReference.create(cls));
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
            Constructor<?>[] declaredConstructors = classType.getDeclaredConstructors();
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
                java.lang.reflect.Type genericParameterType =
                        constructor.getGenericParameterTypes()[i];
                T value = decode(reader, TypeReference.create(genericParameterType));
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
