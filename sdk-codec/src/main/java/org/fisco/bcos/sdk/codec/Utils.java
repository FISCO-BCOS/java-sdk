package org.fisco.bcos.sdk.codec;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.codec.abi.TypeMappingException;
import org.fisco.bcos.sdk.codec.datatypes.*;
import org.fisco.bcos.sdk.codec.datatypes.Type;

/** Utility functions. */
public class Utils {
    private Utils() {}

    public static <T extends Type> String getTypeName(TypeReference<T> typeReference) {
        return getTypeName(typeReference.getType());
    }

    public static <T extends Type> String getTypeName(java.lang.reflect.Type type) {
        try {

            Class<?> cls = Utils.getClassType(type);
            if (type instanceof ParameterizedType) { // array
                return getParameterizedTypeName(type);
            } else { // simple type
                return getSimpleTypeName(cls);
            }

        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Invalid class reference provided", e);
        }
    }

    private static <T extends Type, U extends Type> String getParameterizedTypeName(
            java.lang.reflect.Type type) {

        try {
            Class<?> cls = Utils.getClassType(type);

            if (DynamicArray.class.isAssignableFrom(cls)) {
                return getTypeName(((ParameterizedType) type).getActualTypeArguments()[0]) + "[]";
            } else if (StaticArray.class.isAssignableFrom(cls)) {

                int length =
                        Integer.parseInt(
                                cls.getSimpleName()
                                        .substring(StaticArray.class.getSimpleName().length()));

                return getTypeName(((ParameterizedType) type).getActualTypeArguments()[0])
                        + "["
                        + length
                        + "]";

            } else {
                throw new UnsupportedOperationException("Invalid type provided " + cls.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Invalid class reference provided", e);
        }
    }

    public static String getSimpleTypeName(Class<?> type) {
        String simpleName = type.getSimpleName().toLowerCase();

        if (type.equals(Uint.class)
                || type.equals(Int.class)
                || type.equals(Ufixed.class)
                || type.equals(Fixed.class)) {
            return simpleName + "256";
        } else if (type.equals(Utf8String.class)) {
            return "string";
        } else if (type.equals(DynamicBytes.class)) {
            return "bytes";
        } else if (StructType.class.isAssignableFrom(type)) {
            return type.getName();
        } else {
            return simpleName;
        }
    }

    @SuppressWarnings("rawtypes")
    public static <T extends Type> boolean dynamicType(java.lang.reflect.Type type)
            throws ClassNotFoundException {

        Class<T> cls = Utils.getClassType(type);
        // dynamic type
        if (Utf8String.class.isAssignableFrom(cls)
                || DynamicBytes.class.isAssignableFrom(cls)
                || DynamicArray.class.isAssignableFrom(cls)) {
            return true;
        }

        // not static type
        if (!StaticArray.class.isAssignableFrom(cls)) {
            return false;
        }

        // unpack static array for checking if dynamic type
        java.lang.reflect.Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        return dynamicType(types[0]);
    }

    public static int getLength(List<Type> parameters) {
        int count = 0;
        for (final Type type : parameters) {
            if (type instanceof StaticArray
                    && StaticStruct.class.isAssignableFrom(
                            ((StaticArray) type).getComponentType())) {
                count +=
                        staticStructNestedPublicFieldsFlatList(
                                                ((StaticArray) type).getComponentType())
                                        .size()
                                * ((StaticArray) type).getValue().size();
            } else if (type instanceof StaticArray
                    && DynamicStruct.class.isAssignableFrom(
                            ((StaticArray) type).getComponentType())) {
                count++;
            } else if (type instanceof StaticArray) {
                count += ((StaticArray) type).getValue().size();
            } else {
                count++;
            }
        }
        return count;
    }

    public static List<Field> staticStructNestedPublicFieldsFlatList(Class<Type> classType) {
        return staticStructsNestedFieldsFlatList(classType).stream()
                .filter(field -> Modifier.isPublic(field.getModifiers()))
                .collect(Collectors.toList());
    }

    public static List<Field> staticStructsNestedFieldsFlatList(Class<Type> classType) {
        List<Field> canonicalFields =
                Arrays.stream(classType.getDeclaredFields())
                        .filter(field -> !StaticStruct.class.isAssignableFrom(field.getType()))
                        .collect(Collectors.toList());
        List<Field> nestedFields =
                Arrays.stream(classType.getDeclaredFields())
                        .filter(field -> StaticStruct.class.isAssignableFrom(field.getType()))
                        .map(
                                field ->
                                        staticStructsNestedFieldsFlatList(
                                                (Class<Type>) field.getType()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
        return Stream.concat(canonicalFields.stream(), nestedFields.stream())
                .collect(Collectors.toList());
    }

    public static <T extends Type> int getOffset(java.lang.reflect.Type type)
            throws ClassNotFoundException {

        if (Utils.dynamicType(type)) {
            return 1;
        }

        Class<T> cls = Utils.getClassType(type);
        if (StaticArray.class.isAssignableFrom(cls)) {
            int length =
                    Integer.parseInt(
                            cls.getSimpleName()
                                    .substring(StaticArray.class.getSimpleName().length()));
            java.lang.reflect.Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return getOffset(types[0]) * length;
        } else {
            return 1;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Type> Class<T> getClassType(java.lang.reflect.Type type)
            throws ClassNotFoundException {
        if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else {
            return (Class<T>) Class.forName(type.getTypeName());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> Class<T> getParameterizedTypeFromArray(
            TypeReference typeReference) throws ClassNotFoundException {

        java.lang.reflect.Type type = typeReference.getType();
        java.lang.reflect.Type[] types = ((ParameterizedType) type).getActualTypeArguments();

        return Utils.getClassType(types[0]);
    }

    @SuppressWarnings("unchecked")
    public static List<TypeReference<Type>> convert(List<TypeReference<?>> input) {
        List<TypeReference<Type>> result = new ArrayList<>(input.size());
        result.addAll(
                input.stream()
                        .map(typeReference -> (TypeReference<Type>) typeReference)
                        .collect(Collectors.toList()));
        return result;
    }

    public static <T, R extends Type<T>, E extends Type<T>> List<E> typeMap(
            List<List<T>> input, Class<E> outerDestType, Class<R> innerType) {
        List<E> result = new ArrayList<>();
        try {
            Constructor<E> constructor = outerDestType.getDeclaredConstructor(List.class);
            for (List<T> ts : input) {
                E e = constructor.newInstance(typeMap(ts, innerType));
                result.add(e);
            }
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException e) {
            throw new TypeMappingException(e);
        }
        return result;
    }

    public static <T, R extends Type<T>> List<R> typeMap(List<T> input, Class<R> destType)
            throws TypeMappingException {

        List<R> result = new ArrayList<R>(input.size());

        if (!input.isEmpty()) {
            try {
                Constructor<R> constructor =
                        destType.getDeclaredConstructor(input.get(0).getClass());
                for (T value : input) {
                    result.add(constructor.newInstance(value));
                }
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | InvocationTargetException
                    | InstantiationException e) {
                throw new TypeMappingException(e);
            }
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public static List typeMapWithoutGenericType(List input, Class destType)
            throws TypeMappingException {
        List result = new ArrayList(input.size());
        if (!input.isEmpty()) {
            try {
                Constructor constructor = destType.getDeclaredConstructor(input.get(0).getClass());
                for (Object value : input) {
                    result.add(constructor.newInstance(value));
                }
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | InvocationTargetException
                    | InstantiationException e) {
                throw new TypeMappingException(e);
            }
        }
        return result;
    }

    public static Pair<BigInteger, BigDecimal> divideFixed(BigDecimal value) {
        // Decimal Part
        BigDecimal fractionalPart = value.remainder(BigDecimal.ONE);
        // Integer Part
        BigInteger in = value.subtract(fractionalPart).toBigInteger();
        return Pair.of(in, fractionalPart);
    }

    public static BigDecimal processFixedScaleDecode(byte[] decByteArray, int decimalCount) {
        BigDecimal decimal =
                new BigDecimal(
                        1
                                / Math.pow(2, decimalCount)
                                * ((new BigInteger(1, decByteArray)).doubleValue()));
        return decimal;
    }

    public static byte[] getBytesOfDecimalPart(BigDecimal decimal, int nBitSize) {
        double r = 0d;
        if (decimal.signum() < 0) {
            r = decimal.doubleValue() + 1;
        } else r = decimal.doubleValue();

        StringBuilder stringBuilder = new StringBuilder();

        int count = nBitSize;
        double num = 0;

        while (Math.abs(r) > 0.0000000001) {
            r = Math.abs(r);
            if (count == 0) {
                // throw new Exception("Cannot change the decimal number to binary!");
                break;
            }
            num = r * 2;
            if (num >= 1) {
                stringBuilder.append(1);
                r = num - 1;
                if (r == 0) {
                    for (int i = 0; i < count - 1; i++) {
                        stringBuilder.append(0);
                    }
                }
            } else {
                stringBuilder.append(0);
                r = num;
            }
            count--;
        }
        if (stringBuilder.length() == 0) {
            byte[] zeroByte = new byte[nBitSize / 8];
            return zeroByte;
        }
        long result = Long.parseLong(stringBuilder.toString(), 2);
        byte[] resultByte = new byte[nBitSize / 8];
        byte[] resultBytes = BigInteger.valueOf(result).toByteArray();
        if (resultBytes.length == nBitSize / 8) {
            for (int i = 0; i < resultByte.length; i++) {
                resultByte[i] = resultBytes[i];
            }
        } else {
            for (int i = 0; i < resultByte.length; i++) {
                resultByte[i] = resultBytes[i + 1];
            }
        }
        return resultByte;
    }
}
