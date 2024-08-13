package org.fisco.bcos.sdk.v3.codec.abi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.datatypes.AbiTypes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Array;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Fixed;
import org.fisco.bcos.sdk.v3.codec.datatypes.FixedPointType;
import org.fisco.bcos.sdk.v3.codec.datatypes.Int;
import org.fisco.bcos.sdk.v3.codec.datatypes.IntType;
import org.fisco.bcos.sdk.v3.codec.datatypes.NumericType;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StructType;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Ufixed;
import org.fisco.bcos.sdk.v3.codec.datatypes.Uint;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint160;

/**
 * Ethereum Contract Application Binary Interface (ABI) decoding for types. Decoding is not
 * documented, but is the reverse of the encoding details located <a
 * href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 */
public class TypeDecoder {
    @SuppressWarnings("unchecked")
    public static <T extends Type> T decode(byte[] input, int offset, TypeReference<T> type)
            throws ClassNotFoundException {
        Class<T> cls = type.getClassType();
        if (NumericType.class.isAssignableFrom(cls)) {
            return (T)
                    decodeNumeric(
                            Arrays.copyOfRange(input, offset, input.length),
                            (Class<NumericType>) cls);
        } else if (Address.class.isAssignableFrom(cls)) {
            return (T) decodeAddress(Arrays.copyOfRange(input, offset, input.length));
        } else if (Bool.class.isAssignableFrom(cls)) {
            return (T) decodeBool(input, offset);
        } else if (Bytes.class.isAssignableFrom(cls)) {
            return (T) decodeBytes(input, offset, (Class<Bytes>) cls);
        } else if (DynamicBytes.class.isAssignableFrom(cls)) {
            return (T) decodeDynamicBytes(input, offset);
        } else if (Utf8String.class.isAssignableFrom(cls)) {
            return (T) decodeUtf8String(input, offset);
        } else if (StaticArray.class.isAssignableFrom(cls)) {
            int length;
            if (cls == StaticArray.class) {
                length = ((TypeReference.StaticArrayTypeReference<?>) type).getSize();
            } else {
                length =
                        Integer.parseInt(
                                cls.getSimpleName()
                                        .substring(StaticArray.class.getSimpleName().length()));
            }
            return decodeStaticArray(input, offset, type, length);
        } else if (DynamicArray.class.isAssignableFrom(cls)) {
            return decodeDynamicArray(input, offset, type);
        } else {
            throw new UnsupportedOperationException("Type cannot be encoded: " + type.getClass());
        }
    }

    public static Address decodeAddress(byte[] input) {
        return new Address(decodeNumeric(input, Uint160.class));
    }

    public static <T extends NumericType> T decodeNumeric(byte[] inputByteArray, Class<T> type) {
        try {
            int typeLengthAsBytes = getTypeLengthInBytes(type);

            byte[] resultByteArray = new byte[typeLengthAsBytes + 1];

            if (Int.class.isAssignableFrom(type) || Fixed.class.isAssignableFrom(type)) {
                // NOTE (first byte & 0xffff) >> 7 means take the MSB as sign bit
                resultByteArray[0] = (byte) ((inputByteArray[0] & 0xffff) >> 7);
            }

            int valueOffset = Type.MAX_BYTE_LENGTH - typeLengthAsBytes;
            System.arraycopy(inputByteArray, valueOffset, resultByteArray, 1, typeLengthAsBytes);

            BigInteger numericValue = new BigInteger(resultByteArray);
            return type.getConstructor(BigInteger.class).newInstance(numericValue);

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

    static <T extends NumericType> int getTypeLengthInBytes(Class<T> type) {
        return getTypeLength(type) >> 3; // divide by 8
    }

    static <T extends NumericType> int getTypeLength(Class<T> type) {
        if (IntType.class.isAssignableFrom(type)) {
            String regex = "(" + Uint.class.getSimpleName() + "|" + Int.class.getSimpleName() + ")";
            String[] splitName = type.getSimpleName().split(regex);
            if (splitName.length == 2) {
                return Integer.parseInt(splitName[1]);
            }
        } else if (FixedPointType.class.isAssignableFrom(type)) {
            String regex =
                    "(" + Ufixed.class.getSimpleName() + "|" + Fixed.class.getSimpleName() + ")";
            String[] splitName = type.getSimpleName().split(regex);
            if (splitName.length == 2) {
                String[] bitsCounts = splitName[1].split("x");
                return Integer.parseInt(bitsCounts[0]) + Integer.parseInt(bitsCounts[1]);
            }
        }
        return Type.MAX_BIT_LENGTH;
    }

    public static int decodeUintAsInt(byte[] rawInput, int offset) {
        byte[] input = Arrays.copyOfRange(rawInput, offset, offset + Type.MAX_BYTE_LENGTH);
        int result = 0;
        try {
            result = decode(input, 0, TypeReference.create(Uint.class)).getValue().intValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Bool decodeBool(byte[] rawInput, int offset) {
        BigInteger numericValue =
                new BigInteger(Arrays.copyOfRange(rawInput, offset, offset + Type.MAX_BYTE_LENGTH));
        boolean value = numericValue.equals(BigInteger.ONE);
        return new Bool(value);
    }

    public static <T extends Bytes> T decodeBytes(byte[] input, Class<T> type) {
        return decodeBytes(input, 0, type);
    }

    public static <T extends Bytes> T decodeBytes(byte[] input, int offset, Class<T> type) {
        try {
            String simpleName = type.getSimpleName();
            String[] splitName = simpleName.split(Bytes.class.getSimpleName());
            int length = Integer.parseInt(splitName[1]);
            byte[] bytes = Arrays.copyOfRange(input, offset, offset + length);
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

    public static DynamicBytes decodeDynamicBytes(byte[] input, int offset) {
        int encodedLength = decodeUintAsInt(input, offset);
        int valueOffset = offset + Type.MAX_BYTE_LENGTH;
        byte[] bytes = Arrays.copyOfRange(input, valueOffset, valueOffset + encodedLength);
        return new DynamicBytes(bytes);
    }

    public static Utf8String decodeUtf8String(byte[] input, int offset) {
        DynamicBytes dynamicBytesResult = decodeDynamicBytes(input, offset);
        byte[] bytes = dynamicBytesResult.getValue();

        return new Utf8String(new String(bytes, StandardCharsets.UTF_8));
    }

    public static <T extends Type> T decodeStaticArray(
            byte[] input, int offset, TypeReference<T> typeReference, int length) {
        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        throw new UnsupportedOperationException(
                                "Zero length fixed array is invalid type");
                    } else {
                        return instantiateStaticArray(elements, length);
                    }
                };

        return decodeArrayElements(input, offset, typeReference, length, function);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> T instantiateStaticArray(List<T> elements, int length) {
        try {
            Class<? extends StaticArray> arrayClass =
                    (Class<? extends StaticArray>)
                            Class.forName(
                                    "org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray"
                                            + length);
            return (T) arrayClass.getConstructor(List.class).newInstance(elements);
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static <T extends Type> T decodeDynamicArray(
            byte[] input, int offset, TypeReference<T> typeReference) {
        int length = decodeUintAsInt(input, offset);

        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        return (T) new DynamicArray(AbiTypes.getType(typeName), elements);
                    }
                    return (T) new DynamicArray<>(elements);
                };

        int valueOffset = offset + Type.MAX_BYTE_LENGTH;

        return decodeArrayElements(input, valueOffset, typeReference, length, function);
    }

    private static <T extends Type> T decodeArrayElements(
            byte[] input,
            int offset,
            TypeReference<T> typeReference,
            int length,
            BiFunction<List<T>, String, T> consumer) {
        List<T> elements = new ArrayList<>(length);
        try {
            java.lang.reflect.Type[] types =
                    ((ParameterizedType) typeReference.getType()).getActualTypeArguments();
            // cls without parameterized type
            Class<T> classType = Utils.getClassType(types[0]);
            if (StructType.class.isAssignableFrom(classType)) {
                for (int i = 0, currOffset = offset;
                        i < length;
                        i++,
                                currOffset +=
                                        getSingleElementLength(input, currOffset, classType)
                                                * Type.MAX_BYTE_LENGTH) {
                    T value;
                    if (DynamicStruct.class.isAssignableFrom(classType)) {
                        value =
                                TypeDecoder.decodeDynamicStruct(
                                        input,
                                        offset
                                                + FunctionReturnDecoder.getDataOffset(
                                                        input, currOffset, typeReference),
                                        TypeReference.create(types[0]));
                    } else {
                        value =
                                TypeDecoder.decodeStaticStruct(
                                        input, currOffset, TypeReference.create(types[0]));
                    }
                    elements.add(value);
                }

                String typeName = Utils.getSimpleTypeName(classType);
                return consumer.apply(elements, typeName);
            } else {
                int currOffset = offset;
                for (int i = 0; i < length; i++) {
                    T value;
                    if (Array.class.isAssignableFrom(classType)) {
                        if (StaticArray.class.isAssignableFrom(classType)) {
                            int size =
                                    Integer.parseInt(
                                            Utils.getSimpleTypeName(classType)
                                                    .substring(
                                                            StaticArray.class
                                                                    .getSimpleName()
                                                                    .length()));
                            value =
                                    decodeStaticArray(
                                            input,
                                            currOffset,
                                            TypeReference.create(types[0]),
                                            size);
                        } else {
                            int getOffset =
                                    FunctionReturnDecoder.getDataOffset(
                                            input, currOffset, TypeReference.create(types[0]));
                            value =
                                    decodeDynamicArray(
                                            input,
                                            offset + getOffset,
                                            TypeReference.create(types[0]));
                        }
                    } else {
                        if (isDynamic(classType)) {
                            int getOffset =
                                    FunctionReturnDecoder.getDataOffset(
                                            input, currOffset, typeReference);
                            value =
                                    decode(
                                            input,
                                            offset + getOffset,
                                            TypeReference.create(types[0]));
                            currOffset += Type.MAX_BYTE_LENGTH;
                        } else {
                            value = decode(input, currOffset, TypeReference.create(types[0]));
                            currOffset +=
                                    getSingleElementLength(input, currOffset, classType)
                                            * Type.MAX_BYTE_LENGTH;
                        }
                    }
                    elements.add(value);
                }

                String typeName = Utils.getSimpleTypeName(classType);
                return consumer.apply(elements, typeName);
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                    "Unable to access parameterized type " + typeReference.getType().getTypeName(),
                    e);
        }
    }

    static <T extends Type> boolean isDynamic(Class<T> parameter) {
        return DynamicBytes.class.isAssignableFrom(parameter)
                || Utf8String.class.isAssignableFrom(parameter)
                || DynamicArray.class.isAssignableFrom(parameter);
    }

    @SuppressWarnings("unchecked")
    static <T extends Type> int getSingleElementLength(byte[] input, int offset, Class<T> type) {
        if (input.length == offset) {
            return 0;
        } else if (DynamicBytes.class.isAssignableFrom(type)
                || Utf8String.class.isAssignableFrom(type)) {
            // length field + data value
            return (decodeUintAsInt(input, offset) / Type.MAX_BYTE_LENGTH) + 2;
        } else if (StaticStruct.class.isAssignableFrom(type)) {
            return Utils.staticStructNestedPublicFieldsFlatList((Class<Type>) type).size();
        } else {
            return 1;
        }
    }

    static <T extends Type> T decodeDynamicStruct(
            byte[] input, int offset, TypeReference<T> typeReference) {
        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        throw new UnsupportedOperationException(
                                "Zero length fixed array is invalid type");
                    } else {
                        return instantiateStruct(typeReference, elements);
                    }
                };

        return decodeDynamicStructElements(input, offset, typeReference, function);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> T instantiateStruct(
            final TypeReference<T> typeReference, final List<T> parameters) {
        try {
            Constructor ctor =
                    Arrays.stream(typeReference.getClassType().getDeclaredConstructors())
                            .filter(
                                    declaredConstructor ->
                                            Arrays.stream(declaredConstructor.getParameterTypes())
                                                            .allMatch(Type.class::isAssignableFrom)
                                                    && declaredConstructor.getParameterTypes()
                                                                    .length
                                                            > 0)
                            .findAny()
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "TypeReference struct must contain a constructor with types that extend Type"));
            ctor.setAccessible(true);
            return (T) ctor.newInstance(parameters.toArray());
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(
                    "Constructor cannot accept" + Arrays.toString(parameters.toArray()), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> T decodeDynamicStructElements(
            final byte[] input,
            final int offset,
            final TypeReference<T> typeReference,
            final BiFunction<List<T>, String, T> consumer) {
        try {
            final Class<T> classType = typeReference.getClassType();
            Constructor<?> constructor =
                    Arrays.stream(classType.getDeclaredConstructors())
                            .filter(
                                    declaredConstructor ->
                                            Arrays.stream(declaredConstructor.getParameterTypes())
                                                            .allMatch(Type.class::isAssignableFrom)
                                                    && declaredConstructor.getParameterTypes()
                                                                    .length
                                                            > 0)
                            .findAny()
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "TypeReferenced struct must contain a constructor with types that extend Type"));

            final int length = constructor.getParameterCount();
            final Map<Integer, T> parameters = new HashMap<>();
            int staticOffset = 0;
            final List<Integer> parameterOffsets = new ArrayList<>();
            for (int i = 0; i < length; ++i) {
                final Class<T> declaredField = (Class<T>) constructor.getParameterTypes()[i];
                TypeReference<T> typeReferenceElement =
                        TypeReference.create(constructor.getGenericParameterTypes()[i]);
                final T value;
                final int beginIndex = offset + staticOffset;
                if (isDynamic(declaredField)) {
                    final int parameterOffset =
                            (decodeDynamicStructDynamicParameterOffset(
                                            Arrays.copyOfRange(
                                                    input,
                                                    beginIndex,
                                                    beginIndex + Type.MAX_BYTE_LENGTH)))
                                    + offset;
                    parameterOffsets.add(parameterOffset);
                    staticOffset += Type.MAX_BYTE_LENGTH;
                } else {
                    if (StaticStruct.class.isAssignableFrom(declaredField)) {
                        value =
                                decodeStaticStruct(
                                        Arrays.copyOfRange(input, beginIndex, input.length),
                                        0,
                                        TypeReference.create(declaredField));
                        staticOffset +=
                                Utils.staticStructNestedPublicFieldsFlatList(
                                                        (Class<Type>) classType)
                                                .size()
                                        * Type.MAX_BYTE_LENGTH;
                    } else {
                        value =
                                decode(
                                        Arrays.copyOfRange(input, beginIndex, input.length),
                                        0,
                                        typeReferenceElement);
                        staticOffset += value.bytes32PaddedLength();
                    }
                    parameters.put(i, value);
                }
            }
            int dynamicParametersProcessed = 0;
            int dynamicParametersToProcess =
                    getDynamicStructDynamicParametersCount(constructor.getParameterTypes());
            for (int i = 0; i < length; ++i) {
                TypeReference<T> typeReferenceElement =
                        TypeReference.create(constructor.getGenericParameterTypes()[i]);
                if (isDynamic(typeReferenceElement.getClassType())) {
                    final boolean isLastParameterInStruct =
                            dynamicParametersProcessed == (dynamicParametersToProcess - 1);
                    final int parameterLength =
                            isLastParameterInStruct
                                    ? input.length
                                            - parameterOffsets.get(dynamicParametersProcessed)
                                    : parameterOffsets.get(dynamicParametersProcessed + 1)
                                            - parameterOffsets.get(dynamicParametersProcessed);
                    parameters.put(
                            i,
                            decodeDynamicParameterFromStruct(
                                    input,
                                    parameterOffsets.get(dynamicParametersProcessed),
                                    parameterLength,
                                    typeReferenceElement));
                    dynamicParametersProcessed++;
                }
            }

            String typeName = Utils.getSimpleTypeName(classType);

            final List<T> elements = new ArrayList<>();
            for (int i = 0; i < length; ++i) {
                elements.add(parameters.get(i));
            }

            return consumer.apply(elements, typeName);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                    "Unable to access parameterized type " + typeReference.getType().getTypeName(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> int getDynamicStructDynamicParametersCount(
            final Class<?>[] cls) {
        return (int) Arrays.stream(cls).filter(c -> isDynamic((Class<T>) c)).count();
    }

    private static int decodeDynamicStructDynamicParameterOffset(final byte[] input) {
        return decodeUintAsInt(input, 0);
    }

    private static <T extends Type> T decodeDynamicParameterFromStruct(
            final byte[] input,
            final int parameterOffset,
            final int parameterLength,
            TypeReference<T> typeReference)
            throws ClassNotFoundException {
        final byte[] dynamicElementData =
                Arrays.copyOfRange(input, parameterOffset, parameterOffset + parameterLength);

        final T value;
        if (DynamicStruct.class.isAssignableFrom(typeReference.getClassType())) {
            value = decodeDynamicStruct(dynamicElementData, 0, typeReference);
        } else if (DynamicArray.class.isAssignableFrom(typeReference.getClassType())) {
            value = decodeDynamicArray(dynamicElementData, 0, typeReference);
        } else {
            value = decode(dynamicElementData, 0, typeReference);
        }
        return value;
    }

    public static <T extends Type> T decodeStaticStruct(
            final byte[] input, final int offset, final TypeReference<T> typeReference) {
        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        throw new UnsupportedOperationException(
                                "Zero length fixed array is invalid type");
                    } else {
                        return instantiateStruct(typeReference, elements);
                    }
                };

        return decodeStaticStructElement(input, offset, typeReference, function);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> T decodeStaticStructElement(
            final byte[] input,
            final int offset,
            final TypeReference<T> typeReference,
            final BiFunction<List<T>, String, T> consumer) {
        try {
            Class<T> classType = typeReference.getClassType();
            Constructor<?> constructor =
                    Arrays.stream(classType.getDeclaredConstructors())
                            .filter(
                                    declaredConstructor ->
                                            Arrays.stream(declaredConstructor.getParameterTypes())
                                                            .allMatch(Type.class::isAssignableFrom)
                                                    && declaredConstructor.getParameterTypes()
                                                                    .length
                                                            > 0)
                            .findAny()
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "TypeReferenced struct must contain a constructor with types that extend Type"));
            final int length = constructor.getParameterCount();
            List<T> elements = new ArrayList<>(length);

            for (int i = 0, currOffset = offset; i < length; i++) {
                T value;
                final Class<T> declaredField = (Class<T>) constructor.getParameterTypes()[i];
                TypeReference<T> typeReferenceElement =
                        TypeReference.create(constructor.getGenericParameterTypes()[i]);
                if (StaticStruct.class.isAssignableFrom(declaredField)) {
                    final int nestedStructLength =
                            classType
                                            .getDeclaredFields()[i]
                                            .getType()
                                            .getConstructors()[0]
                                            .getParameters()
                                            .length
                                    * Type.MAX_BYTE_LENGTH;
                    value =
                            decodeStaticStruct(
                                    Arrays.copyOfRange(
                                            input, currOffset, currOffset + nestedStructLength),
                                    0,
                                    TypeReference.create(declaredField));
                    currOffset += nestedStructLength;
                } else {
                    value =
                            decode(
                                    Arrays.copyOfRange(
                                            input, currOffset, currOffset + Type.MAX_BYTE_LENGTH),
                                    0,
                                    typeReferenceElement);
                    currOffset += Type.MAX_BYTE_LENGTH;
                }
                elements.add(value);
            }

            String typeName = Utils.getSimpleTypeName(classType);
            return consumer.apply(elements, typeName);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                    "Unable to access parameterized type " + typeReference.getType().getTypeName(),
                    e);
        }
    }
}
