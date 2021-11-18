package org.fisco.bcos.sdk.codec;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;


import org.fisco.bcos.sdk.codec.abi.TestUtils;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.generated.Fixed64x16;
import org.fisco.bcos.sdk.codec.datatypes.generated.Fixed72x16;
import org.fisco.bcos.sdk.codec.datatypes.generated.Uint32;
import org.fisco.bcos.sdk.codec.scale.FunctionEncoder;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.junit.Test;

public class ScaleEncoderTest {
    private final FunctionEncoder ecdsaEncoder = new FunctionEncoder(new CryptoSuite(0));
    private final FunctionEncoder smEncoder = new FunctionEncoder(new CryptoSuite(1));

    @Test
    public void EmptyTest() {
        assertEquals(
                "empty()",
                FunctionEncoder.buildMethodSignature("empty", Collections.emptyList()));
    }

    @Test
    public void testBuildMethodId() {
        byte[] methodId = ecdsaEncoder.buildMethodId("baz(uint32,fixed64x16)");
        assertEquals("9584369f", TestUtils.bytesToString(methodId));

        // methodId = smEncoder.buildMethodId("baz(uint32,bool)");
        // assertEquals("638408c2", TestUtils.bytesToString(methodId));
    }

//     @Test
//     public void testFunctionSimpleEncode() {
//         Function function =
//                 new Function(
//                         "baz",
//                         Arrays.asList(new Uint32(BigInteger.valueOf(69)), new Fixed64x16(new BigInteger(TestUtils.stringToBytes("000000000001")), new BigInteger(TestUtils.stringToBytes("0001")))),
//                         Collections.emptyList());

//         assertEquals(
//                 "9584369f"
//                         + "45000000"
//                         + "0000000000010001",
//                 TestUtils.bytesToString(ecdsaEncoder.encode(function)));
//     }

    @Test
    public void testFunctionFixedEncode() {
        Function function =
                new Function(
                        "baz",
                        Arrays.asList(new Uint32(BigInteger.valueOf(69)), new Fixed72x16(new BigDecimal("1.25"))),
                        Collections.emptyList());

        assertEquals(
                "257e44f7"
                        + "45000000"
                        + "000000000000014000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
                
        Function function1 =
                new Function(
                        "baz",
                        Arrays.asList(new Uint32(BigInteger.valueOf(69)), new Fixed72x16(new BigDecimal("-1.201"))),
                        Collections.emptyList());

        assertEquals(
                "257e44f7"
                        + "45000000"
                        + "ff0000000000013374",
                TestUtils.bytesToString(ecdsaEncoder.encode(function1)));


        Function function2 =
                new Function(
                        "baz",
                        Arrays.asList(new Uint32(BigInteger.valueOf(69)), new Fixed72x16(new BigDecimal("1099511627775.99998"))),
                        Collections.emptyList());

        assertEquals(
                "257e44f7"
                        + "45000000"
                        + "0000fffffffffffffe",
                TestUtils.bytesToString(ecdsaEncoder.encode(function2)));

        // Function function3 =
        //         new Function(
        //                 "baz",
        //                 Arrays.asList(new Uint32(BigInteger.valueOf(69)), new Fixed72x16(new BigDecimal("-281474976710655.99998"))),
        //                 Collections.emptyList());

        // assertEquals(
        //         "257e44f7"
        //                 + "45000000"
        //                 + "fffffffffffffffffe",
        //         TestUtils.bytesToString(ecdsaEncoder.encode(function3)));
                
    }

    @Test
    public void testFunctionEncode() {
        Function function =
                new Function(
                        "baz",
                        Arrays.asList(new Uint32(BigInteger.valueOf(69)), new Fixed64x16("1.999")),
                        Collections.emptyList());

        assertEquals(
                "9584369f"
                        + "45000000"
                        + "00000000000103e7",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

}
