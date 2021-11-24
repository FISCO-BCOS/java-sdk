package org.fisco.bcos.sdk.codec.scale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.codec.FunctionReturnDecoderInterface;
import org.fisco.bcos.sdk.codec.datatypes.*;
import org.fisco.bcos.sdk.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.StringUtils;

public class FunctionReturnDecoder implements FunctionReturnDecoderInterface {
    public FunctionReturnDecoder() {}

    /**
     * Decode SCALE encoded return values from smart contract function call.
     *
     * @param rawInput SCALE encoded input
     * @param outputParameters list of return types as {@link TypeReference}
     * @return {@link List} of values returned by function, {@link Collections#emptyList()} if
     *     invalid response
     */
    @Override
    public List<Type> decode(String rawInput, List<TypeReference<Type>> outputParameters) {
        System.out.println("rawInput:" + rawInput);
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
    public static <T extends Type> Type decodeIndexedValue(
            String rawInput, TypeReference<T> typeReference) {
        try {
            Class<T> type = typeReference.getClassType();
            ScaleCodecReader reader = new ScaleCodecReader(Hex.decode(rawInput));

            if (Bytes.class.isAssignableFrom(type)) {
                return TypeDecoder.decodeBytes(
                        reader, (Class<Bytes>) Class.forName(type.getName()));
            } else if (Array.class.isAssignableFrom(type)
                    || BytesType.class.isAssignableFrom(type)
                    || Utf8String.class.isAssignableFrom(type)) {
                return TypeDecoder.decodeBytes(reader, Bytes32.class);
            } else {
                return TypeDecoder.decode(reader, type);
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Invalid class reference provided", e);
        }
    }

    private static List<Type> build(String input, List<TypeReference<Type>> outputParameters) {
        List<Type> results = new ArrayList<>(outputParameters.size());
        byte[] rawInput = Hex.decode(input);
        ScaleCodecReader reader = new ScaleCodecReader(rawInput);
        System.out.println("4: build");
        for (TypeReference<?> typeReference : outputParameters) {
            try {
                Class<Type> classType = (Class<Type>) typeReference.getClassType();
                Type result = TypeDecoder.decode(reader, classType);
                System.out.println("validation Fixed: " + (result instanceof FixedType));
                System.out.println("validation Fixed Value: " + result.getValue());
                results.add(result);
            } catch (ClassNotFoundException e) {
                throw new UnsupportedOperationException("Invalid class reference provided", e);
            }
        }
        return results;
    }
}
