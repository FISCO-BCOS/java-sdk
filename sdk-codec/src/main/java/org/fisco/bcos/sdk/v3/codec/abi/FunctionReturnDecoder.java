package org.fisco.bcos.sdk.v3.codec.abi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.FunctionReturnDecoderInterface;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.datatypes.Array;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.BytesType;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

/** Decodes values returned by function or event calls. */
public class FunctionReturnDecoder implements FunctionReturnDecoderInterface {
    public FunctionReturnDecoder() {}

    /**
     * Decode ABI encoded return values from smart contract function call.
     *
     * @param rawInput ABI encoded input
     * @param outputParameters list of return types as {@link TypeReference}
     * @return {@link List} of values returned by function, {@link Collections#emptyList()} if
     *     invalid response
     */
    @Override
    public List<Type> decode(String rawInput, List<TypeReference<Type>> outputParameters) {
        String input = Numeric.cleanHexPrefix(rawInput);

        if (StringUtils.isEmpty(input)) {
            return Collections.emptyList();
        } else {
            return build(input, outputParameters);
        }
    }

    /**
     * Decodes an indexed parameter associated with an event. Indexed parameters are individually
     * encoded, unlike non-indexed parameters which are encoded as per ABI-encoded function
     * parameters and return values.
     *
     * <p>If any of the following types are indexed, the Keccak-256 hashes of the values are
     * returned instead. These are returned as a bytes32 value.
     *
     * <ul>
     *   <li>Arrays
     *   <li>Strings
     *   <li>Bytes
     * </ul>
     *
     * <p>See the <a href="http://solidity.readthedocs.io/en/latest/contracts.html#events">Solidity
     * documentation</a> for further information.
     *
     * @param rawInput ABI encoded input
     * @param typeReference of expected result type
     * @param <T> type of TypeReference
     * @return the decode value
     */
    public <T extends Type> Type decodeIndexedValue(
            String rawInput, TypeReference<T> typeReference) {
        String input = Numeric.cleanHexPrefix(rawInput);

        try {
            Class<T> type = typeReference.getClassType();

            if (Bytes.class.isAssignableFrom(type)) {
                return TypeDecoder.decodeBytes(
                        Hex.decode(input), (Class<Bytes>) Class.forName(type.getName()));
            } else if (Array.class.isAssignableFrom(type)
                    || BytesType.class.isAssignableFrom(type)
                    || Utf8String.class.isAssignableFrom(type)) {
                return TypeDecoder.decodeBytes(Hex.decode(input), Bytes32.class);
            } else {
                return TypeDecoder.decode(Hex.decode(input), 0, typeReference);
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Invalid class reference provided", e);
        }
    }

    private static List<Type> build(String input, List<TypeReference<Type>> outputParameters) {
        List<Type> results = new ArrayList<>(outputParameters.size());
        byte[] rawInput = Hex.decode(input);

        int offset = 0;
        for (TypeReference<?> typeReference : outputParameters) {
            try {
                Class<Type> classType = (Class<Type>) typeReference.getClassType();

                int dataOffset = getDataOffset(rawInput, offset, typeReference);

                Type result;
                if (DynamicStruct.class.isAssignableFrom(classType)) {
                    result = TypeDecoder.decodeDynamicStruct(rawInput, dataOffset, typeReference);
                    offset += Type.MAX_BYTE_LENGTH;
                } else if (DynamicArray.class.isAssignableFrom(classType)) {
                    result = TypeDecoder.decodeDynamicArray(rawInput, dataOffset, typeReference);
                    offset += Type.MAX_BYTE_LENGTH;
                } else if (typeReference instanceof TypeReference.StaticArrayTypeReference) {
                    int length = ((TypeReference.StaticArrayTypeReference) typeReference).getSize();
                    result =
                            TypeDecoder.decodeStaticArray(
                                    rawInput, dataOffset, typeReference, length);
                    offset += length * Type.MAX_BYTE_LENGTH;

                } else if (StaticStruct.class.isAssignableFrom(classType)) {
                    result = TypeDecoder.decodeStaticStruct(rawInput, dataOffset, typeReference);
                    offset +=
                            Utils.staticStructNestedPublicFieldsFlatList(classType).size()
                                    * Type.MAX_BYTE_LENGTH;
                } else if (StaticArray.class.isAssignableFrom(classType)) {
                    int length =
                            Integer.parseInt(
                                    classType
                                            .getSimpleName()
                                            .substring(StaticArray.class.getSimpleName().length()));
                    result =
                            TypeDecoder.decodeStaticArray(
                                    rawInput, dataOffset, typeReference, length);
                    if (DynamicStruct.class.isAssignableFrom(
                            Utils.getParameterizedTypeFromArray(typeReference))) {
                        offset += Type.MAX_BYTE_LENGTH;
                    } else if (StaticStruct.class.isAssignableFrom(
                            Utils.getParameterizedTypeFromArray(typeReference))) {
                        offset +=
                                Utils.staticStructNestedPublicFieldsFlatList(
                                                        Utils.getParameterizedTypeFromArray(
                                                                typeReference))
                                                .size()
                                        * length
                                        * Type.MAX_BYTE_LENGTH;
                    } else {
                        offset += Type.MAX_BYTE_LENGTH * length;
                    }
                } else {
                    result = TypeDecoder.decode(rawInput, dataOffset, typeReference);
                    offset += Type.MAX_BYTE_LENGTH;
                }
                results.add(result);
            } catch (ClassNotFoundException e) {
                throw new UnsupportedOperationException("Invalid class reference provided", e);
            }
        }
        return results;
    }

    public static <T extends Type> int getDataOffset(
            byte[] input, int offset, TypeReference<?> typeReference)
            throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        Class<Type> type = (Class<Type>) typeReference.getClassType();
        if (DynamicBytes.class.isAssignableFrom(type)
                || Utf8String.class.isAssignableFrom(type)
                || DynamicArray.class.isAssignableFrom(type)
                || hasDynamicOffsetInStaticArray(typeReference, offset)) {
            return TypeDecoder.decodeUintAsInt(input, offset);
        } else {
            return offset;
        }
    }

    private static boolean hasDynamicOffsetInStaticArray(TypeReference<?> typeReference, int offset)
            throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        Class<Type> type = (Class<Type>) typeReference.getClassType();
        try {
            return StaticArray.class.isAssignableFrom(type)
                    && (DynamicStruct.class.isAssignableFrom(
                                    Utils.getParameterizedTypeFromArray(typeReference))
                            || TypeDecoder.isDynamic(
                                    Utils.getParameterizedTypeFromArray(typeReference)));
        } catch (ClassCastException e) {
            return false;
        }
    }
}
