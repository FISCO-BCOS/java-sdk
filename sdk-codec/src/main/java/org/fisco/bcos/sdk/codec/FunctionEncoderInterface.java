package org.fisco.bcos.sdk.codec;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.crypto.CryptoSuite;

public abstract class FunctionEncoderInterface extends Encoder {
    private CryptoSuite cryptoSuite;

    public FunctionEncoderInterface(CryptoSuite cryptoSuite) {
        super(cryptoSuite);
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
        byte[] hash = this.getCryptoSuite().hash(input);
        return Arrays.copyOfRange(hash, 0, 4);
    }
}
