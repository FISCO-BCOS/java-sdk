package org.fisco.bcos.sdk.v3.codec;

import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.Numeric;

/**
 * Ethereum filter encoding. Further limited details are available <a
 * href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI#events">here</a>.
 */
public class EventEncoder {
    private final CryptoSuite cryptoSuite;

    public EventEncoder(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public String encode(Event event) {
        String methodSignature = buildMethodSignature(event.getName(), event.getParameters());

        return buildEventSignature(methodSignature);
    }

    public <T extends Type> String buildMethodSignature(
            String methodName, List<TypeReference<T>> parameters) {
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        result.append("(");
        String params =
                parameters.stream().map(p -> Utils.getTypeName(p)).collect(Collectors.joining(","));
        result.append(params);
        result.append(")");
        return result.toString();
    }

    public String buildEventSignature(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = this.cryptoSuite.hash(input);
        return Numeric.toHexString(hash);
    }
}
