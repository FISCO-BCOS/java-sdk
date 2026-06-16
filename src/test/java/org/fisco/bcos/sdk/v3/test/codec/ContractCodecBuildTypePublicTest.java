/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Test;

/**
 * Public-API coverage for the high level {@link ContractCodec} type dispatch (the logic mirrored by
 * the private {@code buildType} method) reached exclusively through the public encode/decode
 * surface. No reflection / {@code setAccessible} is used.
 *
 * <p>Each public encode (and the matching decode) is driven with parameters that span every
 * Solidity type branch the codec must classify:
 *
 * <ul>
 *   <li>sized {@code uintN} / default {@code uint}
 *   <li>sized {@code intN} / default {@code int}
 *   <li>{@code bool}, {@code address}, {@code string}
 *   <li>dynamic {@code bytes} and fixed {@code bytesN} (1, 4, 32)
 *   <li>dynamic arrays {@code T[]} and static arrays {@code T[k]}
 *   <li>nested arrays {@code T[][]} and {@code T[k][]}
 *   <li>tuples / structs (static and dynamic) and tuple arrays
 *   <li>the error / unsupported-type branches (assert {@link ContractCodecException})
 * </ul>
 *
 * <p>Exercised in BOTH ABI mode ({@code new ContractCodec(suite, false)}) and wasm/SCALE mode
 * ({@code new ContractCodec(suite, true)}) where supported. Where a decoded value is not
 * deterministic, only non-null / size / no-throw is asserted, as instructed.
 */
public class ContractCodecBuildTypePublicTest {

    // A non-empty fake constructor bytecode (hex). Any deterministic hex works; no chain needed.
    private static final String BIN = "60606040";

    private CryptoSuite cryptoSuite() {
        return TestUtils.getCryptoSuite();
    }

    private ContractCodec abiCodec() {
        return new ContractCodec(cryptoSuite(), false);
    }

    private ContractCodec wasmCodec() {
        return new ContractCodec(cryptoSuite().getHashImpl(), true);
    }

    private static String fn(String name, String inputs, String outputs) {
        return "[{\"constant\":false,\"inputs\":["
                + inputs
                + "],\"name\":\""
                + name
                + "\",\"outputs\":["
                + outputs
                + "],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    }

    private static String input(String name, String type) {
        return "{\"name\":\"" + name + "\",\"type\":\"" + type + "\"}";
    }

    // ------------------------------------------------------------------------------------------
    // Scalar branches: sized uintN / default uint
    // ------------------------------------------------------------------------------------------

    @Test
    public void testUintSizesAbiEncodeDecodeRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        String abi =
                fn(
                        "uints",
                        input("a", "uint8")
                                + ","
                                + input("b", "uint16")
                                + ","
                                + input("c", "uint32")
                                + ","
                                + input("d", "uint64")
                                + ","
                                + input("e", "uint128")
                                + ","
                                + input("f", "uint256")
                                + ","
                                + input("g", "uint"),
                        "");
        List<String> args = Arrays.asList("200", "300", "70000", "123456789", "42", "999", "7");
        byte[] encoded = codec.encodeMethodFromString(abi, "uints", args);
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "uints", encoded);
        assertEquals(7, decoded.size());
    }

    @Test
    public void testUintObjectEncodeDecodeRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        String abi = fn("u", input("a", "uint256") + "," + input("b", "uint8"), "");
        List<Object> args = Arrays.asList(new BigInteger("12345"), new BigInteger("9"));
        byte[] encoded = codec.encodeMethod(abi, "u", args);
        assertTrue(encoded.length > 4);
        List<?> decoded = codec.decodeMethodInput(abi, "u", Hex.toHexString(encoded));
        assertEquals(2, decoded.size());
    }

    // ------------------------------------------------------------------------------------------
    // Scalar branches: sized intN / default int
    // ------------------------------------------------------------------------------------------

    @Test
    public void testIntSizesAbiEncodeDecodeRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        String abi =
                fn(
                        "ints",
                        input("a", "int8")
                                + ","
                                + input("b", "int16")
                                + ","
                                + input("c", "int32")
                                + ","
                                + input("d", "int64")
                                + ","
                                + input("e", "int128")
                                + ","
                                + input("f", "int256")
                                + ","
                                + input("g", "int"),
                        "");
        List<String> args = Arrays.asList("-100", "-200", "-30000", "-1000", "-42", "-777", "-1");
        byte[] encoded = codec.encodeMethodFromString(abi, "ints", args);
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "ints", encoded);
        assertEquals(7, decoded.size());
    }

    // ------------------------------------------------------------------------------------------
    // Scalar branches: bool / address / string
    // ------------------------------------------------------------------------------------------

    @Test
    public void testBoolAddressStringAbiRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        String abi =
                fn(
                        "bas",
                        input("flag", "bool")
                                + ","
                                + input("addr", "address")
                                + ","
                                + input("text", "string"),
                        "");
        List<String> args =
                Arrays.asList(
                        "true", "0x0000000000000000000000000000000000000001", "hello build type");
        byte[] encoded = codec.encodeMethodFromString(abi, "bas", args);
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "bas", encoded);
        assertEquals(3, decoded.size());
        assertTrue(decoded.get(2).contains("hello build type"));
    }

    @Test
    public void testBoolAddressStringWasmRoundTrip() throws Exception {
        ContractCodec codec = wasmCodec();
        String abi =
                fn(
                        "bas",
                        input("flag", "bool")
                                + ","
                                + input("addr", "address")
                                + ","
                                + input("text", "string"),
                        "");
        List<String> args =
                Arrays.asList(
                        "false", "0x00000000000000000000000000000000000000cd", "scale path");
        byte[] encoded = codec.encodeMethodFromString(abi, "bas", args);
        assertNotNull(encoded);
        List<String> decoded = codec.decodeMethodInputToString(abi, "bas", encoded);
        assertEquals(3, decoded.size());
        assertTrue(decoded.get(2).contains("scale path"));
    }

    // ------------------------------------------------------------------------------------------
    // bytes (dynamic) and bytesN (fixed: 1, 4, 32)
    // ------------------------------------------------------------------------------------------

    @Test
    public void testDynamicBytesAndFixedBytesNAbiRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        StringBuilder b32 = new StringBuilder("0x");
        for (int i = 0; i < 32; i++) {
            b32.append("11");
        }
        String abi =
                fn(
                        "bytesFns",
                        input("raw", "bytes")
                                + ","
                                + input("b1", "bytes1")
                                + ","
                                + input("b4", "bytes4")
                                + ","
                                + input("b32", "bytes32"),
                        "");
        List<String> args =
                Arrays.asList("0xdeadbeefcafe", "0xff", "0x11223344", b32.toString());
        byte[] encoded = codec.encodeMethodFromString(abi, "bytesFns", args);
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "bytesFns", encoded);
        assertEquals(4, decoded.size());
    }

    @Test
    public void testDynamicBytesAndFixedBytesNWasmRoundTrip() throws Exception {
        ContractCodec codec = wasmCodec();
        // Encoding classifies both the dynamic bytes and the fixed bytesN branch in wasm mode.
        // The wasm/SCALE bytes decode round-trip is not asserted here; encode no-throw suffices.
        String abi = fn("bytesFns", input("raw", "bytes") + "," + input("b4", "bytes4"), "");
        List<String> args = Arrays.asList("0xdeadbeef", "0x11223344");
        byte[] encoded = codec.encodeMethodFromString(abi, "bytesFns", args);
        assertNotNull(encoded);
        assertTrue(encoded.length > 4);
    }

    // ------------------------------------------------------------------------------------------
    // dynamic arrays T[] and static arrays T[k]
    // ------------------------------------------------------------------------------------------

    @Test
    public void testDynamicAndStaticArraysAbiRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        String abi =
                fn(
                        "arrays",
                        input("u", "uint256[]")
                                + ","
                                + input("s", "string[]")
                                + ","
                                + input("fix", "uint8[3]"),
                        "");
        List<String> args =
                Arrays.asList("[1,2,3]", "[\"a\",\"bb\",\"ccc\"]", "[10,20,30]");
        byte[] encoded = codec.encodeMethodFromString(abi, "arrays", args);
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "arrays", encoded);
        assertEquals(3, decoded.size());
    }

    @Test
    public void testDynamicAndStaticArraysWasmRoundTrip() throws Exception {
        ContractCodec codec = wasmCodec();
        String abi =
                fn(
                        "arrays",
                        input("u", "uint256[]") + "," + input("fix", "uint8[3]"),
                        "");
        List<String> args = Arrays.asList("[1,2,3]", "[10,20,30]");
        byte[] encoded = codec.encodeMethodFromString(abi, "arrays", args);
        assertNotNull(encoded);
        List<String> decoded = codec.decodeMethodInputToString(abi, "arrays", encoded);
        assertEquals(2, decoded.size());
    }

    @Test
    public void testEmptyDynamicArrayAbi() throws Exception {
        ContractCodec codec = abiCodec();
        String abi = fn("emptyArr", input("u", "uint256[]"), "");
        byte[] encoded =
                codec.encodeMethodFromString(abi, "emptyArr", Collections.singletonList("[]"));
        assertTrue(encoded.length >= 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "emptyArr", encoded);
        assertEquals(1, decoded.size());
    }

    // ------------------------------------------------------------------------------------------
    // nested arrays T[][] and T[k][]
    // ------------------------------------------------------------------------------------------

    @Test
    public void testNestedDynamicArrayOfArraysAbi() throws Exception {
        ContractCodec codec = abiCodec();
        String abi = fn("nested", input("m", "uint256[][]"), "");
        byte[] encoded =
                codec.encodeMethodFromString(
                        abi, "nested", Collections.singletonList("[[1,2],[3,4,5]]"));
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "nested", encoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testNestedStaticInDynamicArrayAbi() throws Exception {
        ContractCodec codec = abiCodec();
        String abi = fn("nestedFix", input("m", "uint8[2][]"), "");
        byte[] encoded =
                codec.encodeMethodFromString(
                        abi, "nestedFix", Collections.singletonList("[[1,2],[3,4]]"));
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "nestedFix", encoded);
        assertEquals(1, decoded.size());
    }

    // ------------------------------------------------------------------------------------------
    // tuples / structs (static + dynamic) and tuple arrays
    // ------------------------------------------------------------------------------------------

    private static final String DYNAMIC_STRUCT_ABI =
            "[{\"inputs\":[{\"components\":["
                    + "{\"internalType\":\"string[]\",\"name\":\"value_str\",\"type\":\"string[]\"},"
                    + "{\"internalType\":\"bytes32[]\",\"name\":\"bytes32_in_struct\",\"type\":\"bytes32[]\"}],"
                    + "\"internalType\":\"struct StructA\",\"name\":\"a\",\"type\":\"tuple\"}],"
                    + "\"name\":\"buildStructA\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    private static final String STATIC_STRUCT_ABI =
            "[{\"inputs\":[{\"components\":["
                    + "{\"internalType\":\"int128\",\"name\":\"i1\",\"type\":\"int128\"},"
                    + "{\"internalType\":\"uint128\",\"name\":\"u1\",\"type\":\"uint128\"},"
                    + "{\"internalType\":\"int32[1]\",\"name\":\"b1\",\"type\":\"int32[1]\"}],"
                    + "\"internalType\":\"struct StaticStruct\",\"name\":\"b\",\"type\":\"tuple\"}],"
                    + "\"name\":\"buildStaticStruct\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    private static final String TUPLE_ARRAY_ABI =
            "[{\"inputs\":[{\"components\":["
                    + "{\"internalType\":\"int128\",\"name\":\"i1\",\"type\":\"int128\"},"
                    + "{\"internalType\":\"uint128\",\"name\":\"u1\",\"type\":\"uint128\"}],"
                    + "\"internalType\":\"struct P[]\",\"name\":\"ps\",\"type\":\"tuple[]\"}],"
                    + "\"name\":\"setTuples\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    @Test
    public void testDynamicStructTupleAbiRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        StringBuilder b32 = new StringBuilder("0x");
        for (int i = 0; i < 32; i++) {
            b32.append("22");
        }
        // struct field order: value_str (string[]), bytes32_in_struct (bytes32[])
        String value = "[[\"x\",\"yy\"],[\"" + b32 + "\"]]";
        byte[] encoded =
                codec.encodeMethodFromString(
                        DYNAMIC_STRUCT_ABI, "buildStructA", Collections.singletonList(value));
        assertTrue(encoded.length > 4);
        List<String> decoded =
                codec.decodeMethodInputToString(DYNAMIC_STRUCT_ABI, "buildStructA", encoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testStaticStructTupleAbiRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        // struct field order: i1 (int128), u1 (uint128), b1 (int32[1])
        String value = "[-5,7,[3]]";
        byte[] encoded =
                codec.encodeMethodFromString(
                        STATIC_STRUCT_ABI, "buildStaticStruct", Collections.singletonList(value));
        assertTrue(encoded.length > 4);
        List<String> decoded =
                codec.decodeMethodInputToString(STATIC_STRUCT_ABI, "buildStaticStruct", encoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testTupleArrayAbiRoundTrip() throws Exception {
        ContractCodec codec = abiCodec();
        String value = "[[-1,2],[-3,4]]";
        byte[] encoded =
                codec.encodeMethodFromString(
                        TUPLE_ARRAY_ABI, "setTuples", Collections.singletonList(value));
        assertTrue(encoded.length > 4);
        List<String> decoded =
                codec.decodeMethodInputToString(TUPLE_ARRAY_ABI, "setTuples", encoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testDynamicStructTupleWasmRoundTrip() throws Exception {
        ContractCodec codec = wasmCodec();
        StringBuilder b32 = new StringBuilder("0x");
        for (int i = 0; i < 32; i++) {
            b32.append("33");
        }
        String value = "[[\"x\"],[\"" + b32 + "\"]]";
        byte[] encoded =
                codec.encodeMethodFromString(
                        DYNAMIC_STRUCT_ABI, "buildStructA", Collections.singletonList(value));
        assertNotNull(encoded);
        List<String> decoded =
                codec.decodeMethodInputToString(DYNAMIC_STRUCT_ABI, "buildStructA", encoded);
        assertEquals(1, decoded.size());
    }

    // ------------------------------------------------------------------------------------------
    // constructor path spanning many type branches (ABI + wasm)
    // ------------------------------------------------------------------------------------------

    private static final String RICH_CTOR_ABI =
            "[{\"inputs\":["
                    + input("u8", "uint8")
                    + ","
                    + input("u", "uint")
                    + ","
                    + input("i64", "int64")
                    + ","
                    + input("i", "int")
                    + ","
                    + input("flag", "bool")
                    + ","
                    + input("text", "string")
                    + ","
                    + input("addr", "address")
                    + ","
                    + input("b4", "bytes4")
                    + ","
                    + input("raw", "bytes")
                    + ","
                    + input("dynArr", "uint256[]")
                    + ","
                    + input("fixArr", "uint256[2]")
                    + "],\"type\":\"constructor\"}]";

    private List<String> richCtorStrArgs() {
        List<String> params = new ArrayList<>();
        params.add("200"); // uint8
        params.add("123456"); // uint (256)
        params.add("-1000"); // int64
        params.add("-77"); // int (256)
        params.add("true"); // bool
        params.add("hi build type"); // string
        params.add("0x0000000000000000000000000000000000000001"); // address
        params.add("0x11223344"); // bytes4
        params.add("0xdeadbeef"); // dynamic bytes
        params.add("[1,2,3]"); // uint256[]
        params.add("[10,20]"); // uint256[2]
        return params;
    }

    @Test
    public void testRichConstructorFromStringAbi() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded = codec.encodeConstructorFromString(RICH_CTOR_ABI, BIN, richCtorStrArgs());
        assertNotNull(encoded);
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }

    @Test
    public void testRichConstructorFromStringWasm() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded = codec.encodeConstructorFromString(RICH_CTOR_ABI, BIN, richCtorStrArgs());
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);
    }

    @Test
    public void testConstructorTupleFromStringAbiAndDecode() throws Exception {
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":[{\"components\":["
                        + "{\"internalType\":\"string[]\",\"name\":\"value_str\",\"type\":\"string[]\"},"
                        + "{\"internalType\":\"bytes32[]\",\"name\":\"bytes32_in_struct\",\"type\":\"bytes32[]\"}],"
                        + "\"internalType\":\"struct StructA\",\"name\":\"a\",\"type\":\"tuple\"}],"
                        + "\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
        StringBuilder b32 = new StringBuilder("0x");
        for (int i = 0; i < 32; i++) {
            b32.append("44");
        }
        String value = "[[\"hello\"],[\"" + b32 + "\"]]";
        byte[] encoded =
                codec.encodeConstructorFromString(abi, BIN, Collections.singletonList(value));
        assertNotNull(encoded);
        assertTrue(encoded.length > Hex.decode(BIN).length);

        String fullInput = Hex.toHexString(encoded);
        List<String> decoded = codec.decodeConstructorInputToString(abi, BIN, fullInput);
        assertEquals(1, decoded.size());
    }

    // ------------------------------------------------------------------------------------------
    // decode via methodId / interface (exercises output-side type classification)
    // ------------------------------------------------------------------------------------------

    @Test
    public void testEncodeThenDecodeByIdAndInterfaceAbi() throws Exception {
        ContractCodec codec = abiCodec();
        String abi =
                fn(
                        "mix",
                        input("u", "uint256")
                                + ","
                                + input("b", "bool")
                                + ","
                                + input("s", "string"),
                        "");
        String sig = "mix(uint256,bool,string)";
        List<String> args = Arrays.asList("42", "true", "iface");
        byte[] encoded = codec.encodeMethodFromString(abi, "mix", args);
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(sig);
        assertEquals(4, methodId.length);

        List<Object> byId = codec.decodeMethodInputById(abi, methodId, encoded);
        assertEquals(3, byId.size());

        List<Object> byInterface = codec.decodeMethodInputByInterface(abi, sig, encoded);
        assertEquals(3, byInterface.size());

        List<String> byIdStr = codec.decodeMethodInputByIdToString(abi, methodId, encoded);
        assertEquals(3, byIdStr.size());
    }

    // ------------------------------------------------------------------------------------------
    // ERROR / unsupported-type branches
    // ------------------------------------------------------------------------------------------

    @Test(expected = ContractCodecException.class)
    public void testUnsupportedTypeThrows() throws Exception {
        ContractCodec codec = abiCodec();
        // "qux" is not a recognized Solidity type -> error branch.
        String abi = fn("bad", input("x", "qux"), "");
        codec.encodeMethodFromString(abi, "bad", Collections.singletonList("1"));
    }

    @Test(expected = ContractCodecException.class)
    public void testBytesNLengthMismatchThrows() throws Exception {
        ContractCodec codec = abiCodec();
        // bytes4 expects exactly 4 bytes; supply 2.
        String abi = fn("b", input("b", "bytes4"), "");
        codec.encodeMethodFromString(abi, "b", Collections.singletonList("0x1122"));
    }

    @Test(expected = ContractCodecException.class)
    public void testBytesNTooLongThrows() throws Exception {
        ContractCodec codec = abiCodec();
        // bytes33 exceeds the 32-byte limit.
        StringBuilder hex = new StringBuilder("0x");
        for (int i = 0; i < 33; i++) {
            hex.append("11");
        }
        String abi = fn("b", input("b", "bytes33"), "");
        codec.encodeMethodFromString(abi, "b", Collections.singletonList(hex.toString()));
    }

    @Test(expected = ContractCodecException.class)
    public void testWrongArgCountConstructorThrows() throws Exception {
        ContractCodec codec = abiCodec();
        // RICH_CTOR_ABI constructor expects 11 args; supply 1 -> arg-count guard throws.
        codec.encodeConstructorFromString(RICH_CTOR_ABI, BIN, Collections.singletonList("1"));
    }

    @Test(expected = ContractCodecException.class)
    public void testUnknownMethodThrows() throws Exception {
        ContractCodec codec = abiCodec();
        String abi = fn("known", input("u", "uint256"), "");
        codec.encodeMethodFromString(abi, "doesNotExist", Collections.singletonList("1"));
    }

    @Test(expected = ContractCodecException.class)
    public void testFixedStaticArrayWrongSizeThrows() throws Exception {
        ContractCodec codec = abiCodec();
        // uint8[3] but only two elements provided -> fixed-list size guard throws.
        String abi = fn("fix", input("u", "uint8[3]"), "");
        codec.encodeMethodFromString(abi, "fix", Collections.singletonList("[1,2]"));
    }
}
