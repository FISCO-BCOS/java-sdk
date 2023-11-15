package org.fisco.bcos.sdk.v3.codec.abi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.FunctionEncoderInterface;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Uint;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;

/**
 * Ethereum Contract Application Binary Interface (ABI) encoding for functions. Further details are
 * available <a href="https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI">here</a>.
 */
public class FunctionEncoder extends FunctionEncoderInterface {
    @Deprecated
    public FunctionEncoder(CryptoSuite cryptoSuite) {
        super(cryptoSuite);
    }

    public FunctionEncoder(Hash hash) {
        super(hash);
    }

    @Override
    public byte[] encode(Function function) {
        List<Type> parameters = function.getInputParameters();

        String methodSignature = buildMethodSignature(function.getName(), parameters);
        byte[] methodId = this.buildMethodId(methodSignature);

        return encodeParameters(parameters, methodId);
    }

    public static byte[] encodeConstructor(List<Type> parameters) {
        return encodeParameters(parameters, null);
    }

    public static byte[] encodeParameters(List<Type> parameters, byte[] methodID) {
        int dynamicDataOffset = Utils.getLength(parameters) * Type.MAX_BYTE_LENGTH;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            if (methodID != null) {
                result.write(methodID);
            }
            ByteArrayOutputStream dynamicData = new ByteArrayOutputStream();
            for (Type parameter : parameters) {
                byte[] encodedValue = TypeEncoder.encode(parameter);

                if (TypeEncoder.isDynamic(parameter)) {
                    byte[] encodedDataOffset =
                            TypeEncoder.encodeNumeric(
                                    new Uint(BigInteger.valueOf(dynamicDataOffset)));
                    result.write(encodedDataOffset);
                    dynamicData.write(encodedValue);
                    dynamicDataOffset += encodedValue.length;
                } else {
                    result.write(encodedValue);
                }
            }
            result.write(dynamicData.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toByteArray();
    }
}
