package org.fisco.bcos.sdk.abi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Array;
import org.fisco.bcos.sdk.abi.datatypes.Bool;
import org.fisco.bcos.sdk.abi.datatypes.Bytes;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.abi.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.abi.datatypes.Fixed;
import org.fisco.bcos.sdk.abi.datatypes.FixedPointType;
import org.fisco.bcos.sdk.abi.datatypes.Int;
import org.fisco.bcos.sdk.abi.datatypes.IntType;
import org.fisco.bcos.sdk.abi.datatypes.NumericType;
import org.fisco.bcos.sdk.abi.datatypes.StaticArray;
import org.fisco.bcos.sdk.abi.datatypes.StaticStruct;
import org.fisco.bcos.sdk.abi.datatypes.StructType;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Ufixed;
import org.fisco.bcos.sdk.abi.datatypes.Uint;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint160;
import org.fisco.bcos.sdk.utils.Numeric;

/**
 * Ethereum Contract Application Binary Interface (ABI) decoding for types. Decoding is not
 * documented, but is the reverse of the encoding details located <a
 * href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 */
public class TypeDecoder {

    static final int MAX_BYTE_LENGTH_FOR_HEX_STRING = Type.MAX_BYTE_LENGTH << 1;

    @SuppressWarnings("unchecked")
    public static <T extends Type> T decode(String input, int offset, Class<T> type) {
        if (NumericType.class.isAssignableFrom(type)) {
            return (T) decodeNumeric(input.substring(offset), (Class<NumericType>) type);
        } else if (Address.class.isAssignableFrom(type)) {
            return (T) decodeAddress(input.substring(offset));
        } else if (Bool.class.isAssignableFrom(type)) {
            return (T) decodeBool(input, offset);
        } else if (Bytes.class.isAssignableFrom(type)) {
            return (T) decodeBytes(input, offset, (Class<Bytes>) type);
        } else if (DynamicBytes.class.isAssignableFrom(type)) {
            return (T) decodeDynamicBytes(input, offset);
        } else if (Utf8String.class.isAssignableFrom(type)) {
            return (T) decodeUtf8String(input, offset);
        } else if (Array.class.isAssignableFrom(type)) {
            throw new UnsupportedOperationException(
                    "Array types must be wrapped in a TypeReference");
        } else {
            throw new UnsupportedOperationException("Type cannot be encoded: " + type.getClass());
        }
    }

    static Address decodeAddress(String input) {
        return new Address(decodeNumeric(input, Uint160.class));
    }

    static <T extends NumericType> T decodeNumeric(String input, Class<T> type) {
        try {
            byte[] inputByteArray = Numeric.hexStringToByteArray(input);
            int typeLengthAsBytes = getTypeLengthInBytes(type);

            byte[] resultByteArray = new byte[typeLengthAsBytes + 1];

            if (Int.class.isAssignableFrom(type) || Fixed.class.isAssignableFrom(type)) {
                resultByteArray[0] = inputByteArray[0]; // take MSB as sign bit
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

    static int decodeUintAsInt(String rawInput, int offset) {
        String input = rawInput.substring(offset, offset + MAX_BYTE_LENGTH_FOR_HEX_STRING);
        return decode(input, 0, Uint.class).getValue().intValue();
    }

    static Bool decodeBool(String rawInput, int offset) {
        String input = rawInput.substring(offset, offset + MAX_BYTE_LENGTH_FOR_HEX_STRING);
        BigInteger numericValue = Numeric.toBigInt(input);
        boolean value = numericValue.equals(BigInteger.ONE);
        return new Bool(value);
    }

    static <T extends Bytes> T decodeBytes(String input, Class<T> type) {
        return decodeBytes(input, 0, type);
    }

    static <T extends Bytes> T decodeBytes(String input, int offset, Class<T> type) {
        try {
            String simpleName = type.getSimpleName();
            String[] splitName = simpleName.split(Bytes.class.getSimpleName());
            int length = Integer.parseInt(splitName[1]);
            int hexStringLength = length << 1;

            byte[] bytes =
                    Numeric.hexStringToByteArray(input.substring(offset, offset + hexStringLength));
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

    static DynamicBytes decodeDynamicBytes(String input, int offset) {
        int encodedLength = decodeUintAsInt(input, offset);
        int hexStringEncodedLength = encodedLength << 1;

        int valueOffset = offset + MAX_BYTE_LENGTH_FOR_HEX_STRING;

        String data = input.substring(valueOffset, valueOffset + hexStringEncodedLength);
        byte[] bytes = Numeric.hexStringToByteArray(data);

        return new DynamicBytes(bytes);
    }

    static Utf8String decodeUtf8String(String input, int offset) {
        DynamicBytes dynamicBytesResult = decodeDynamicBytes(input, offset);
        byte[] bytes = dynamicBytesResult.getValue();

        return new Utf8String(new String(bytes, StandardCharsets.UTF_8));
    }

    /**
     * Decode the staticArray Static array length cannot be passed as a type
     *
     * @param input the staticArray need to be decoded
     * @param offset the size of the staticArray need to be decoded
     * @param type the type of the result
     * @param length the length of array
     * @param <T> the generic type
     * @return the decoded result
     */
    @SuppressWarnings("unchecked")
    public static <T extends Type> T decodeStaticArray(
            String input, int offset, java.lang.reflect.Type type, int length) {

        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        throw new UnsupportedOperationException(
                                "Zero length fixed array is invalid type");
                    } else {
                        return instantiateStaticArray(type, elements);
                    }
                };

        return decodeArrayElements(input, offset, type, length, function);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> T instantiateStaticArray(
            java.lang.reflect.Type type, List<T> elements) {
        try {

            Class<T> cls = Utils.getClassType(type);
            return cls.getConstructor(List.class).newInstance(elements);

        } catch (ReflectiveOperationException e) {
            // noinspection unchecked
            return (T) new StaticArray<>(elements);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> T decodeDynamicArray(
            String input, int offset, java.lang.reflect.Type type) {

        int length = decodeUintAsInt(input, offset);

        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        return (T) DynamicArray.empty(typeName);
                    } else {
                        return (T) new DynamicArray<>(elements);
                    }
                };

        int valueOffset = offset + MAX_BYTE_LENGTH_FOR_HEX_STRING;

        return decodeArrayElements(input, valueOffset, type, length, function);
    }

    @SuppressWarnings("rawtypes")
    private static <T extends Type> T decodeArrayElements(
            String input,
            int offset,
            java.lang.reflect.Type type,
            int length,
            BiFunction<List<T>, String, T> consumer) {

        try {
            List<T> elements = new ArrayList<>(length);

            java.lang.reflect.Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Class<T> paraType = Utils.getClassType(types[0]);

            if (StructType.class.isAssignableFrom(paraType)) {
                for (int i = 0, currOffset = offset;
                        i < length;
                        i++,
                                currOffset +=
                                        getSingleElementLength(input, currOffset, paraType)
                                                * MAX_BYTE_LENGTH_FOR_HEX_STRING) {
                    T value;
                    if (DynamicStruct.class.isAssignableFrom(paraType)) {
                        value =
                                TypeDecoder.decodeDynamicStruct(
                                        input,
                                        offset
                                                + FunctionReturnDecoder.getDataOffset(
                                                        input, currOffset, type),
                                        TypeReference.create(paraType));
                    } else {
                        value =
                                TypeDecoder.decodeStaticStruct(
                                        input, currOffset, TypeReference.create(paraType));
                    }
                    elements.add(value);
                }

                String typeName = Utils.getSimpleTypeName(paraType);
                return consumer.apply(elements, typeName);
            } else {
                for (int i = 0; i < length; ++i) {

                    int currEleOffset =
                            offset
                                    + (i
                                            * MAX_BYTE_LENGTH_FOR_HEX_STRING
                                            * Utils.getOffset(types[0]));

                    T t = null;
                    if (Array.class.isAssignableFrom(paraType)) { // nest array
                        int size = 0;
                        if (StaticArray.class.isAssignableFrom(paraType)) {
                            size =
                                    Integer.parseInt(
                                            Utils.getClassType(types[0])
                                                    .getSimpleName()
                                                    .substring(
                                                            StaticArray.class
                                                                    .getSimpleName()
                                                                    .length()));
                            t = decodeStaticArray(input, currEleOffset, types[0], size);
                        } else {
                            int getOffset = TypeDecoder.decodeUintAsInt(input, currEleOffset) << 1;
                            t = decodeDynamicArray(input, offset + getOffset, types[0]);
                        }

                    } else {
                        if (Utf8String.class.isAssignableFrom(paraType)
                                || DynamicBytes.class.isAssignableFrom(paraType)) { // dynamicType
                            int getOffset = TypeDecoder.decodeUintAsInt(input, currEleOffset) << 1;
                            t = decode(input, offset + getOffset, paraType);
                        } else {
                            t = decode(input, currEleOffset, paraType);
                        }
                    }

                    elements.add(t);
                }

                String typeName = Utils.getSimpleTypeName(paraType);
                return consumer.apply(elements, typeName);
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                    "Unable to access parameterized type " + type.getTypeName(), e);
        }
    }

    static <T extends Type> int getSingleElementLength(String input, int offset, Class<T> type) {
        if (input.length() == offset) {
            return 0;
        } else if (DynamicBytes.class.isAssignableFrom(type)
                || Utf8String.class.isAssignableFrom(type)) {
            // length field + data value
            return (decodeUintAsInt(input, offset) / MAX_BYTE_LENGTH_FOR_HEX_STRING) + 2;
        } else if (StaticStruct.class.isAssignableFrom(type)) {
            return Utils.staticStructNestedPublicFieldsFlatList((Class<Type>) type).size();
        } else {
            return 1;
        }
    }

    static <T extends Type> T decodeDynamicStruct(
            String input, int offset, TypeReference<T> typeReference) {
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

    private static <T extends Type> T instantiateStruct(
            final TypeReference<T> typeReference, final List<T> parameters) {
        try {
            Constructor ctor =
                    Arrays.stream(typeReference.getClassType().getDeclaredConstructors())
                            .filter(
                                    declaredConstructor ->
                                            Arrays.stream(declaredConstructor.getParameterTypes())
                                                    .allMatch(Type.class::isAssignableFrom))
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

    private static <T extends Type> T decodeDynamicStructElements(
            final String input,
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
                                                    .allMatch(Type.class::isAssignableFrom))
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
                final T value;
                final int beginIndex = offset + staticOffset;
                if (isDynamic(declaredField)) {
                    final int parameterOffset =
                            (decodeDynamicStructDynamicParameterOffset(
                                            input.substring(
                                                    beginIndex,
                                                    beginIndex + MAX_BYTE_LENGTH_FOR_HEX_STRING)))
                                    + offset;
                    parameterOffsets.add(parameterOffset);
                    staticOffset += MAX_BYTE_LENGTH_FOR_HEX_STRING;
                } else {
                    if (StaticStruct.class.isAssignableFrom(declaredField)) {
                        value =
                                decodeStaticStruct(
                                        input.substring(beginIndex),
                                        0,
                                        TypeReference.create(declaredField));
                        staticOffset +=
                                Utils.staticStructNestedPublicFieldsFlatList(
                                                        (Class<Type>) classType)
                                                .size()
                                        * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                    } else {
                        value = decode(input.substring(beginIndex), 0, declaredField);
                        staticOffset += MAX_BYTE_LENGTH_FOR_HEX_STRING;
                    }
                    parameters.put(i, value);
                }
            }
            int dynamicParametersProcessed = 0;
            int dynamicParametersToProcess =
                    getDynamicStructDynamicParametersCount(constructor.getParameterTypes());
            for (int i = 0; i < length; ++i) {
                java.lang.reflect.Type genericParameterType =
                        constructor.getGenericParameterTypes()[i];
                TypeReference<T> typeReferenceElement =
                        TypeReference.create(Utils.getClassType(genericParameterType));
                if (isDynamic(typeReferenceElement.getClassType())) {
                    final boolean isLastParameterInStruct =
                            dynamicParametersProcessed == (dynamicParametersToProcess - 1);
                    final int parameterLength =
                            isLastParameterInStruct
                                    ? input.length()
                                            - parameterOffsets.get(dynamicParametersProcessed)
                                    : parameterOffsets.get(dynamicParametersProcessed + 1)
                                            - parameterOffsets.get(dynamicParametersProcessed);
                    parameters.put(
                            i,
                            decodeDynamicParameterFromStruct(
                                    input,
                                    parameterOffsets.get(dynamicParametersProcessed),
                                    parameterLength,
                                    typeReferenceElement,
                                    genericParameterType));
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

    static <T extends Type> boolean isDynamic(Class<T> parameter) {
        return DynamicBytes.class.isAssignableFrom(parameter)
                || Utf8String.class.isAssignableFrom(parameter)
                || DynamicArray.class.isAssignableFrom(parameter);
    }

    private static int decodeDynamicStructDynamicParameterOffset(final String input) {
        return decodeUintAsInt(input, 0) << 1;
    }

    public static <T extends Type> T decodeStaticStruct(
            final String input, final int offset, final TypeReference<T> typeReference) {
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
            final String input,
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
                                                    .allMatch(Type.class::isAssignableFrom))
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

                if (StaticStruct.class.isAssignableFrom(declaredField)) {
                    final int nestedStructLength =
                            classType
                                            .getDeclaredFields()[i]
                                            .getType()
                                            .getConstructors()[0]
                                            .getParameters()
                                            .length
                                    * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                    value =
                            decodeStaticStruct(
                                    input.substring(currOffset, currOffset + nestedStructLength),
                                    0,
                                    TypeReference.create(declaredField));
                    currOffset += nestedStructLength;
                } else {
                    value =
                            decode(
                                    input.substring(
                                            currOffset,
                                            currOffset + MAX_BYTE_LENGTH_FOR_HEX_STRING),
                                    0,
                                    declaredField);
                    currOffset += MAX_BYTE_LENGTH_FOR_HEX_STRING;
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

    @SuppressWarnings("unchecked")
    private static <T extends Type> int getDynamicStructDynamicParametersCount(
            final Class<?>[] cls) {
        return (int) Arrays.stream(cls).filter(c -> isDynamic((Class<T>) c)).count();
    }

    private static <T extends Type> T decodeDynamicParameterFromStruct(
            final String input,
            final int parameterOffset,
            final int parameterLength,
            TypeReference<T> typeReference,
            java.lang.reflect.Type genericParameterType)
            throws ClassNotFoundException {
        final String dynamicElementData =
                input.substring(parameterOffset, parameterOffset + parameterLength);

        final T value;
        if (DynamicStruct.class.isAssignableFrom(typeReference.getClassType())) {
            value = decodeDynamicStruct(dynamicElementData, 0, typeReference);
        } else if (DynamicArray.class.isAssignableFrom(typeReference.getClassType())) {
            value = decodeDynamicArray(dynamicElementData, 0, genericParameterType);
        } else {
            value = decode(dynamicElementData, 0, typeReference.getClassType());
        }
        return value;
    }
}
