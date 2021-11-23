package org.fisco.bcos.sdk.codec.scale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.fisco.bcos.sdk.codec.FunctionEncoderInterface;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.crypto.CryptoSuite;

public class FunctionEncoder extends FunctionEncoderInterface {
    public FunctionEncoder(CryptoSuite cryptoSuite) {
        super(cryptoSuite);
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
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        // when wasm call, input first byte is 1; when wasm deploy, input first byte is 0
        try {
            ScaleCodecWriter writer = new ScaleCodecWriter(result);
            if (methodID != null) {
                byte[] wasmFlag = new byte[1];
                wasmFlag[0] = 1;
                result.write(wasmFlag);
                result.write(methodID);
            }
            for (Type parameter : parameters) {
                System.out.println(parameter.getTypeAsString());
                TypeEncoder.encode(parameter, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result.toByteArray()[0]);
        return result.toByteArray();
    }
}
