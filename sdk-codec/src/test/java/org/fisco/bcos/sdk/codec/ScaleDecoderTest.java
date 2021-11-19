package org.fisco.bcos.sdk.codec;

import java.util.Arrays;
import java.util.Collections;

import org.fisco.bcos.sdk.codec.abi.TestUtils;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.codec.datatypes.Uint;
import org.fisco.bcos.sdk.codec.datatypes.generated.Fixed72x16;
import org.fisco.bcos.sdk.codec.scale.FunctionReturnDecoder;
import org.junit.Test;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;


public class ScaleDecoderTest {
    private final static FunctionReturnDecoder decoder = new FunctionReturnDecoder();

    @Test
    public void testSimpleFunctionDecode() {
        Function function =
            new Function(
                "test",
                Collections.<Type>emptyList(),
                Collections.singletonList(new TypeReference<Uint>() {}));

        Assert.assertEquals(
            decoder.decode(
                "0x3700000000000000000000000000000000000000000000000000000000000000",
                function.getOutputParameters()),
            (Collections.singletonList(new Uint(BigInteger.valueOf(55)))));
    }

    @Test
    public void testFixedFunctionDecode() {
      Function function =
          new Function(
              "test",
              Collections.<Type>emptyList(),
              Arrays.asList(new TypeReference<Uint>() {}, new TypeReference<Fixed72x16>() {}));
  
      assertEquals(
          decoder.decode(
              "0x3700000000000000000000000000000000000000000000000000000000000000"
                  + "000000000000014000",
              function.getOutputParameters()),
          (Arrays.asList(new Uint(BigInteger.valueOf(55)), new Fixed72x16(new BigDecimal("1.25")))));

        Function function1 =
          new Function(
              "test",
              Collections.<Type>emptyList(),
              Arrays.asList(new TypeReference<Uint>() {}, new TypeReference<Fixed72x16>() {}));
  
        assertEquals(
          decoder.decode(
              "0x3700000000000000000000000000000000000000000000000000000000000000"
                  + "ff0000000000010000",
              function1.getOutputParameters()),
          (Arrays.asList(new Uint(BigInteger.valueOf(55)), new Fixed72x16(new BigDecimal("-1.0")))));

          Function function2 =
          new Function(
              "test",
              Collections.<Type>emptyList(),
              Arrays.asList(new TypeReference<Uint>() {}, new TypeReference<Fixed72x16>() {}));
  
        assertEquals(
          decoder.decode(
              "0x3700000000000000000000000000000000000000000000000000000000000000"
                  + "000000000000013374",
              function2.getOutputParameters()),
          (Arrays.asList(new Uint(BigInteger.valueOf(55)), new Fixed72x16(new BigDecimal("1.201")))));
    }

}
