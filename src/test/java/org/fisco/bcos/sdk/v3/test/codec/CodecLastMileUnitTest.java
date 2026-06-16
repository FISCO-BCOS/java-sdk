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
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.junit.Test;

/**
 * Last-mile codec coverage. The existing Codec*CoverageTest / ContractCodecExhaustiveUnitTest /
 * ABICodecTest classes leave one large reachable region of {@link ContractCodec} uncovered: the
 * private {@code buildType(ABIDefinition.NamedType, String)} dispatcher (~200 instructions). It is
 * only ever invoked recursively by itself and from {@code encodeConstructor}'s now-unused object
 * path, so no public-API test reaches it. This class drives it directly via reflection across every
 * leaf branch (sized uint/int, bool, string, dynamic bytes, address, static bytesN, dynamic &amp;
 * fixed arrays, dynamic &amp; static tuples) and every validation/error branch.
 *
 * <p>It additionally fills in the remaining reachable {@code catch} / error branches in the public
 * decode methods that the existing tests do not trigger (malformed-input catch blocks, the
 * deprecated by-name input decoder error path, the non-dynamic indexed-event topic decode path).
 *
 * <p>Where a decoded value is not deterministic, only non-null / size / no-throw is asserted.
 */
public class CodecLastMileUnitTest {

    // ------------------------------------------------------------------------------------------
    // fixtures
    // ------------------------------------------------------------------------------------------

    private CryptoSuite cryptoSuite() {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }

    private ContractCodec abiCodec() {
        return new ContractCodec(cryptoSuite(), false);
    }

    private ContractCodec wasmCodec() {
        return new ContractCodec(cryptoSuite().getHashImpl(), true);
    }

    private static final String BIN = "60606040";

    /**
     * Invokes the private {@code ContractCodec.buildType(NamedType, String)} via reflection,
     * unwrapping {@link InvocationTargetException} so callers can assert on the real cause.
     */
    private Type buildType(ContractCodec codec, ABIDefinition.NamedType nt, String param)
            throws Throwable {
        Method m =
                ContractCodec.class.getDeclaredMethod(
                        "buildType", ABIDefinition.NamedType.class, String.class);
        m.setAccessible(true);
        try {
            return (Type) m.invoke(codec, nt, param);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private ABIDefinition.NamedType named(String type) {
        ABIDefinition.NamedType nt = new ABIDefinition.NamedType();
        nt.setType(type);
        return nt;
    }

    private ABIDefinition.NamedType named(String name, String type) {
        ABIDefinition.NamedType nt = new ABIDefinition.NamedType();
        nt.setName(name);
        nt.setType(type);
        return nt;
    }

    // ------------------------------------------------------------------------------------------
    // buildType : scalar leaf branches
    // ------------------------------------------------------------------------------------------

    @Test
    public void testBuildTypeUintDefault256() throws Throwable {
        Type t = buildType(abiCodec(), named("uint"), "12345");
        assertNotNull(t);
        assertEquals(new BigInteger("12345"), t.getValue());
    }

    @Test
    public void testBuildTypeUintSized() throws Throwable {
        Type t = buildType(abiCodec(), named("uint64"), "65535");
        assertNotNull(t);
        assertEquals(BigInteger.valueOf(65535), t.getValue());
    }

    @Test
    public void testBuildTypeIntDefault256Negative() throws Throwable {
        Type t = buildType(abiCodec(), named("int"), "-987654321");
        assertNotNull(t);
        assertEquals(new BigInteger("-987654321"), t.getValue());
    }

    @Test
    public void testBuildTypeIntSizedNegative() throws Throwable {
        Type t = buildType(abiCodec(), named("int128"), "-42");
        assertNotNull(t);
        assertEquals(BigInteger.valueOf(-42), t.getValue());
    }

    @Test
    public void testBuildTypeBoolTrue() throws Throwable {
        Type t = buildType(abiCodec(), named("bool"), "true");
        assertTrue(t instanceof Bool);
        assertEquals(Boolean.TRUE, t.getValue());
    }

    @Test
    public void testBuildTypeBoolFalse() throws Throwable {
        Type t = buildType(abiCodec(), named("bool"), "notABoolean");
        assertTrue(t instanceof Bool);
        assertEquals(Boolean.FALSE, t.getValue());
    }

    @Test
    public void testBuildTypeString() throws Throwable {
        Type t = buildType(abiCodec(), named("string"), "hello last mile");
        assertTrue(t instanceof Utf8String);
        assertEquals("hello last mile", t.getValue());
    }

    @Test
    public void testBuildTypeAddress() throws Throwable {
        Type t = buildType(abiCodec(), named("address"), "0x00000000000000000000000000000000000000ab");
        assertTrue(t instanceof Address);
    }

    @Test
    public void testBuildTypeDynamicBytesHexInput() throws Throwable {
        // hex-prefixed payload is decoded via tryDecodeInputData.
        Type t = buildType(abiCodec(), named("bytes"), "0xdeadbeef");
        assertTrue(t instanceof DynamicBytes);
        byte[] value = (byte[]) t.getValue();
        assertEquals(4, value.length);
    }

    @Test
    public void testBuildTypeDynamicBytesPlainText() throws Throwable {
        // non-hex payload falls back to param.getBytes().
        Type t = buildType(abiCodec(), named("bytes"), "plain text payload");
        assertTrue(t instanceof DynamicBytes);
        byte[] value = (byte[]) t.getValue();
        assertEquals("plain text payload".getBytes().length, value.length);
    }

    @Test
    public void testBuildTypeBytesNExact() throws Throwable {
        Type t = buildType(abiCodec(), named("bytes4"), "0x11223344");
        assertNotNull(t);
        byte[] value = (byte[]) t.getValue();
        assertEquals(4, value.length);
    }

    @Test
    public void testBuildTypeBytes32() throws Throwable {
        StringBuilder hex = new StringBuilder("0x");
        for (int i = 0; i < 32; i++) {
            hex.append("aa");
        }
        Type t = buildType(abiCodec(), named("bytes32"), hex.toString());
        assertNotNull(t);
        assertEquals(32, ((byte[]) t.getValue()).length);
    }

    // ------------------------------------------------------------------------------------------
    // buildType : array branches
    // ------------------------------------------------------------------------------------------

    @Test
    public void testBuildTypeDynamicArray() throws Throwable {
        Type t = buildType(abiCodec(), named("uint256[]"), "[1,2,3]");
        assertTrue(t instanceof DynamicArray);
        assertEquals(3, ((List<?>) t.getValue()).size());
    }

    @Test
    public void testBuildTypeFixedArray() throws Throwable {
        Type t = buildType(abiCodec(), named("uint256[2]"), "[10,20]");
        assertTrue(t instanceof StaticArray);
        assertEquals(2, ((List<?>) t.getValue()).size());
    }

    @Test
    public void testBuildTypeEmptyDynamicArrayUsesAbiTypesBranch() throws Throwable {
        // empty array -> elements.isEmpty() branch using AbiTypes.getType(rawType).
        Type t = buildType(abiCodec(), named("uint256[]"), "[]");
        assertTrue(t instanceof DynamicArray);
        assertEquals(0, ((List<?>) t.getValue()).size());
    }

    @Test
    public void testBuildTypeEmptyFixedArrayUsesAbiTypesBranch() throws Throwable {
        Type t = buildType(abiCodec(), named("address[3]"), "[]");
        assertTrue(t instanceof StaticArray);
    }

    @Test
    public void testBuildTypeNestedArray() throws Throwable {
        // array element re-enters buildType with a reduced-dimension type.
        Type t = buildType(abiCodec(), named("uint256[][]"), "[[1,2],[3]]");
        assertTrue(t instanceof DynamicArray);
        assertEquals(2, ((List<?>) t.getValue()).size());
    }

    @Test
    public void testBuildTypeStringArray() throws Throwable {
        Type t = buildType(abiCodec(), named("string[]"), "[\"a\",\"bb\"]");
        assertTrue(t instanceof DynamicArray);
        assertEquals(2, ((List<?>) t.getValue()).size());
    }

    // ------------------------------------------------------------------------------------------
    // buildType : tuple / struct branches
    // ------------------------------------------------------------------------------------------

    @Test
    public void testBuildTypeStaticTuple() throws Throwable {
        // tuple(uint256, bool) is non-dynamic -> StaticStruct branch.
        ABIDefinition.NamedType tuple = named("t", "tuple");
        List<ABIDefinition.NamedType> components = new ArrayList<>();
        components.add(named("u", "uint256"));
        components.add(named("b", "bool"));
        tuple.setComponents(components);

        Type t = buildType(abiCodec(), tuple, "{\"u\":5,\"b\":true}");
        assertTrue(t instanceof StaticStruct);
        assertEquals(2, ((List<?>) t.getValue()).size());
    }

    @Test
    public void testBuildTypeDynamicTuple() throws Throwable {
        // tuple(uint256, string) contains a dynamic field -> DynamicStruct branch.
        ABIDefinition.NamedType tuple = named("t", "tuple");
        List<ABIDefinition.NamedType> components = new ArrayList<>();
        components.add(named("u", "uint256"));
        components.add(named("s", "string"));
        tuple.setComponents(components);

        Type t = buildType(abiCodec(), tuple, "{\"u\":7,\"s\":\"inside\"}");
        assertTrue(t instanceof DynamicStruct);
        assertEquals(2, ((List<?>) t.getValue()).size());
    }

    // ------------------------------------------------------------------------------------------
    // buildType : validation / error branches (each throws ContractCodecException)
    // ------------------------------------------------------------------------------------------

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeUnknownTypeThrows() throws Throwable {
        buildType(abiCodec(), named("notarealtype"), "0");
    }

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeBadUintSizeThrows() throws Throwable {
        // "uintX" -> NumberFormatException on the bit-size parse.
        buildType(abiCodec(), named("uintX"), "1");
    }

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeBadIntSizeThrows() throws Throwable {
        buildType(abiCodec(), named("intZ"), "1");
    }

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeUintConstructionFailureThrows() throws Throwable {
        // uint999 is not a generated class -> ClassNotFoundException -> ContractCodecException.
        buildType(abiCodec(), named("uint999"), "1");
    }

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeIntConstructionFailureThrows() throws Throwable {
        buildType(abiCodec(), named("int999"), "1");
    }

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeBytesNBadLengthLabelThrows() throws Throwable {
        // "bytesQ" -> NumberFormatException on the length parse.
        buildType(abiCodec(), named("bytesQ"), "0x11");
    }

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeBytesNTooLongThrows() throws Throwable {
        StringBuilder hex = new StringBuilder("0x");
        for (int i = 0; i < 33; i++) {
            hex.append("11");
        }
        buildType(abiCodec(), named("bytes33"), hex.toString());
    }

    @Test(expected = ContractCodecException.class)
    public void testBuildTypeBytesNLengthMismatchThrows() throws Throwable {
        // bytes4 declared but only 2 bytes provided.
        buildType(abiCodec(), named("bytes4"), "0x1122");
    }

    // ------------------------------------------------------------------------------------------
    // buildType in wasm mode (isWasm has no influence on buildType itself but exercises the
    // wasm-constructed codec instance going through the dispatcher).
    // ------------------------------------------------------------------------------------------

    @Test
    public void testBuildTypeWasmCodecScalarAndArray() throws Throwable {
        ContractCodec codec = wasmCodec();
        Type scalar = buildType(codec, named("uint32"), "99");
        assertEquals(BigInteger.valueOf(99), scalar.getValue());
        Type arr = buildType(codec, named("int64[]"), "[-1,-2,-3]");
        assertTrue(arr instanceof DynamicArray);
        assertEquals(3, ((List<?>) arr.getValue()).size());
    }

    // ------------------------------------------------------------------------------------------
    // remaining reachable decode error / catch branches in the public API
    // ------------------------------------------------------------------------------------------

    private static final String SIMPLE_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"}],"
                    + "\"name\":\"f\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],"
                    + "\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    private static final String F_SIG = "f(uint256)";

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodInputByIdMalformedInputThrows() throws Exception {
        // valid methodId but truncated payload -> decode catch -> ContractCodecException.
        ContractCodec codec = abiCodec();
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(F_SIG);
        byte[] tooShort = new byte[] {methodId[0], methodId[1], methodId[2], methodId[3], 0x01};
        codec.decodeMethodInputById(SIMPLE_ABI, methodId, tooShort);
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodByIdMalformedOutputThrows() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(F_SIG);
        codec.decodeMethodById(SIMPLE_ABI, methodId, new byte[] {0x01, 0x02});
    }

    @Test(expected = ContractCodecException.class)
    public void testDeprecatedDecodeMethodAndGetInputObjectMalformedThrows() throws Exception {
        // deprecated by-name input decoder: every candidate fails -> trailing throw branch.
        abiCodec().decodeMethodAndGetInputObject(SIMPLE_ABI, "f", "0xaabbccddee");
    }

    @Test(expected = ContractCodecException.class)
    public void testDeprecatedDecodeMethodAndGetOutputObjectMalformedThrows() throws Exception {
        // deprecated by-name output decoder error branch ("zz" is not valid hex).
        abiCodec().decodeMethodAndGetOutputObject(SIMPLE_ABI, "f", "zz");
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeConstructorInputToStringMalformedThrows() throws Exception {
        // constructor(uint256) but the trailing data is not valid hex.
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"}],\"type\":\"constructor\"}]";
        codec.decodeConstructorInputToString(abi, BIN, BIN + "zz");
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodToStringUnknownNameThrows() throws Exception {
        abiCodec().decodeMethodToString(SIMPLE_ABI, "ghost", new byte[] {0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodInputToStringUnknownNameThrows() throws Exception {
        abiCodec().decodeMethodInputToString(SIMPLE_ABI, "ghost", new byte[] {0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeEventUnknownNameThrows() throws Exception {
        EventLog log = new EventLog("0x", Collections.singletonList("0x00"));
        abiCodec()
                .decodeEvent(
                        "[{\"anonymous\":false,\"inputs\":[],\"name\":\"E\",\"type\":\"event\"}]",
                        "ghost",
                        log);
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeEventToStringUnknownNameThrows() throws Exception {
        EventLog log = new EventLog("0x", Collections.singletonList("0x00"));
        abiCodec()
                .decodeEventToString(
                        "[{\"anonymous\":false,\"inputs\":[],\"name\":\"E\",\"type\":\"event\"}]",
                        "ghost",
                        log);
    }

    // ------------------------------------------------------------------------------------------
    // additional happy-path round-trips through buildType-adjacent public encode paths so the
    // assertions above stay anchored to real encode behaviour.
    // ------------------------------------------------------------------------------------------

    @Test
    public void testEncodeConstructorFromStringRichLeafTypes() throws Exception {
        // exercises encodeConstructorFromString over many leaf types (json-wrapper path).
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":["
                        + "{\"name\":\"u8\",\"type\":\"uint8\"},"
                        + "{\"name\":\"i16\",\"type\":\"int16\"},"
                        + "{\"name\":\"flag\",\"type\":\"bool\"},"
                        + "{\"name\":\"txt\",\"type\":\"string\"},"
                        + "{\"name\":\"addr\",\"type\":\"address\"},"
                        + "{\"name\":\"b8\",\"type\":\"bytes8\"},"
                        + "{\"name\":\"raw\",\"type\":\"bytes\"},"
                        + "{\"name\":\"arr\",\"type\":\"uint256[]\"}"
                        + "],\"type\":\"constructor\"}]";
        List<String> params = new ArrayList<>();
        params.add("250");
        params.add("-300");
        params.add("true");
        params.add("constructor leaf");
        params.add("0x0000000000000000000000000000000000000009");
        params.add("0x1122334455667788");
        params.add("0xc0ffee");
        params.add("[7,8,9]");
        byte[] encoded = codec.encodeConstructorFromString(abi, BIN, params);
        assertNotNull(encoded);
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }

    @Test
    public void testReflectiveBuildTypeIsActuallyInvoked() throws Throwable {
        // sanity: confirm the reflective helper truly runs buildType (guards against silently
        // swallowing the target via a wrong signature).
        try {
            buildType(abiCodec(), named("uint8"), "200");
        } catch (NoSuchMethodException e) {
            fail("buildType signature changed: " + e.getMessage());
        }
    }
}
