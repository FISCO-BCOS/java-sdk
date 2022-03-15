package org.fisco.bcos.sdk.v3.codec.scale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.FunctionReturnDecoderInterface;
import org.fisco.bcos.sdk.v3.codec.datatypes.Array;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.BytesType;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

public class FunctionReturnDecoder implements FunctionReturnDecoderInterface {
    public FunctionReturnDecoder() {}

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
            ScaleCodecReader scaleCodecReader = new ScaleCodecReader(Hex.decode(input));

            if (Bytes.class.isAssignableFrom(type)) {
                return org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decodeBytes(
                        scaleCodecReader, (Class<Bytes>) Class.forName(type.getName()));
            } else if (Array.class.isAssignableFrom(type)
                    || BytesType.class.isAssignableFrom(type)
                    || Utf8String.class.isAssignableFrom(type)) {
                return org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decodeBytes(
                        scaleCodecReader, Bytes32.class);
            } else {
                return org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        scaleCodecReader, typeReference);
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Invalid class reference provided", e);
        }
    }

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
        String input = Numeric.cleanHexPrefix(rawInput);

        if (StringUtils.isEmpty(input)) {
            return Collections.emptyList();
        } else {
            return build(input, outputParameters);
        }
    }

    private static List<Type> build(String input, List<TypeReference<Type>> outputParameters) {
        List<Type> results = new ArrayList<>(outputParameters.size());
        byte[] rawInput = Hex.decode(input);
        ScaleCodecReader reader = new ScaleCodecReader(rawInput);

        for (TypeReference<?> typeReference : outputParameters) {
            try {
                Type result = TypeDecoder.decode(reader, typeReference);
                results.add(result);
            } catch (ClassNotFoundException e) {
                throw new UnsupportedOperationException("Invalid class reference provided", e);
            }
        }
        return results;
    }
}
