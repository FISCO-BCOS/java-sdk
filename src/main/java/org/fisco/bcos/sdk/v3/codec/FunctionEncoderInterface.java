package org.fisco.bcos.sdk.v3.codec;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;

public abstract class FunctionEncoderInterface extends Encoder {
    @Deprecated
    public FunctionEncoderInterface(CryptoSuite cryptoSuite) {
        super(cryptoSuite);
    }

    public FunctionEncoderInterface(Hash hash) {
        super(hash);
    }

    public abstract byte[] encode(Function function);

    public static String buildMethodSignature(String methodName, List<Type> parameters) {
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        result.append("(");
        String params =
                parameters.stream().map(Type::getTypeAsString).collect(Collectors.joining(","));
        result.append(params);
        result.append(")");
        return result.toString();
    }

    public byte[] buildMethodId(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = this.getHashImpl().hash(input);
        return Arrays.copyOfRange(hash, 0, 4);
    }
}
