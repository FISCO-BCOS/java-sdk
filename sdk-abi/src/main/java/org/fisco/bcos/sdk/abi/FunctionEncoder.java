package org.fisco.bcos.sdk.abi;

import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Uint;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ethereum Contract Application Binary Interface (ABI) encoding for functions. Further details are
 * available <a href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 */
public class FunctionEncoder {

    private CryptoSuite cryptoSuite;

    public FunctionEncoder(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public byte[] encode(Function function) {
        List<Type> parameters = function.getInputParameters();

        String methodSignature = buildMethodSignature(function.getName(), parameters);
        byte[] methodId = this.buildMethodId(methodSignature);

        StringBuilder result = new StringBuilder();
        result.append(Numeric.toHexString(methodId));

        return Hex.decode(encodeParameters(parameters, result));
    }

    public static byte[] encodeConstructor(List<Type> parameters) {
        return Hex.decode(encodeParameters(parameters, new StringBuilder()));
    }

    public static String encodeParameters(List<Type> parameters, StringBuilder result) {
        // TODO: support wasm and scale codec
        int dynamicDataOffset = Utils.getLength(parameters) * Type.MAX_BYTE_LENGTH;
        StringBuilder dynamicData = new StringBuilder();

        for (Type parameter : parameters) {
            String encodedValue = TypeEncoder.encode(parameter);

            if (parameter.dynamicType()) {
                String encodedDataOffset =
                        TypeEncoder.encodeNumeric(new Uint(BigInteger.valueOf(dynamicDataOffset)));
                result.append(encodedDataOffset);
                dynamicData.append(encodedValue);
                dynamicDataOffset += (encodedValue.length() >> 1);
            } else {
                result.append(encodedValue);
            }
        }
        result.append(dynamicData);

        return result.toString();
    }

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
        byte[] hash = this.cryptoSuite.hash(input);
        return Arrays.copyOfRange(hash, 0, 4);
    }

    /**
     * @return the cryptoSuite
     */
    public CryptoSuite getCryptoSuite() {
        return this.cryptoSuite;
    }

    /**
     * @param cryptoSuite the cryptoSuite to set
     */
    public void setCryptoSuite(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }
}
