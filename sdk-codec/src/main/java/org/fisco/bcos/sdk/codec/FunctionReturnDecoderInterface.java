package org.fisco.bcos.sdk.codec;

import java.util.List;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;

public interface FunctionReturnDecoderInterface {
    List<Type> decode(String rawInput, List<TypeReference<Type>> outputParameters);
}
