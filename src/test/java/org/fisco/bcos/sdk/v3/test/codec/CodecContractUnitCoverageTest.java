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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.Encoder;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.EventValues;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Fixed;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Ufixed;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Test;

/**
 * Additional unit coverage for the codec and contract-base logic that is not exercised by the other
 * Codec*CoverageTest / ABICodec*Test classes. The focus here is on:
 *
 * <ul>
 *   <li>Nested struct / array-of-struct round-trips through {@link ContractCodec} in both ABI and
 *       WASM (SCALE) modes.
 *   <li>The {@link Fixed} / {@link Ufixed} fixed-point datatypes and their validation branches.
 *   <li>{@link EventEncoder} signatures over array / struct parameters (exercising the {@link
 *       Utils} struct / array signature branches).
 *   <li>{@link Utils} helper methods (getTypeName / getMethodSign / convert / typeMap variants).
 *   <li>Error branches across encode / decode paths.
 * </ul>
 */
public class CodecContractUnitCoverageTest {

    private CryptoSuite cryptoSuite() {
        return TestUtils.getCryptoSuite();
    }

    private ContractCodec abiCodec() {
        return new ContractCodec(cryptoSuite(), false);
    }

    private ContractCodec wasmCodec() {
        return new ContractCodec(cryptoSuite().getHashImpl(), true);
    }

    // ABI containing a nested-tuple function, an array-of-tuple function and an event with a
    // tuple parameter. Used by multiple round-trip tests below.
    private static final String NESTED_ABI =
            "[\n"
                    + "  {\n"
                    + "    \"inputs\": [\n"
                    + "      {\n"
                    + "        \"components\": [\n"
                    + "          {\"name\": \"id\", \"type\": \"uint256\"},\n"
                    + "          {\n"
                    + "            \"components\": [\n"
                    + "              {\"name\": \"x\", \"type\": \"uint256\"},\n"
                    + "              {\"name\": \"y\", \"type\": \"uint256\"}\n"
                    + "            ],\n"
                    + "            \"name\": \"point\",\n"
                    + "            \"type\": \"tuple\"\n"
                    + "          }\n"
                    + "        ],\n"
                    + "        \"name\": \"box\",\n"
                    + "        \"type\": \"tuple\"\n"
                    + "      }\n"
                    + "    ],\n"
                    + "    \"name\": \"setBox\",\n"
                    + "    \"outputs\": [],\n"
                    + "    \"type\": \"function\"\n"
                    + "  },\n"
                    + "  {\n"
                    + "    \"inputs\": [\n"
                    + "      {\n"
                    + "        \"components\": [\n"
                    + "          {\"name\": \"name\", \"type\": \"string\"},\n"
                    + "          {\"name\": \"value\", \"type\": \"uint256\"}\n"
                    + "        ],\n"
                    + "        \"name\": \"entries\",\n"
                    + "        \"type\": \"tuple[]\"\n"
                    + "      }\n"
                    + "    ],\n"
                    + "    \"name\": \"setEntries\",\n"
                    + "    \"outputs\": [\n"
                    + "      {\"name\": \"count\", \"type\": \"uint256\"}\n"
                    + "    ],\n"
                    + "    \"type\": \"function\"\n"
                    + "  }\n"
                    + "]";

    // ------------------------------------------------------------------------------------------
    // ContractCodec nested struct / array-of-struct round-trips (ABI + WASM modes)
    // ------------------------------------------------------------------------------------------

    @Test
    public void testEncodeNestedStructFromStringAbi() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded =
                codec.encodeMethodFromString(
                        NESTED_ABI,
                        "setBox",
                        Collections.singletonList("{\"id\":1,\"point\":{\"x\":2,\"y\":3}}"));
        assertNotNull(encoded);
        assertTrue(encoded.length > 4);
        // round-trip decode back to string
        List<String> decoded = codec.decodeMethodInputToString(NESTED_ABI, "setBox", encoded);
        assertNotNull(decoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testEncodeNestedStructAndGetInputObjectAbi() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded =
                codec.encodeMethodFromString(
                        NESTED_ABI,
                        "setBox",
                        Collections.singletonList("{\"id\":7,\"point\":{\"x\":8,\"y\":9}}"));
        ABIObject abiObject = codec.decodeMethodAndGetInputABIObject(NESTED_ABI, "setBox", Hex.toHexString(encoded));
        assertNotNull(abiObject);
        assertEquals(ABIObject.ObjectType.STRUCT, abiObject.getType());
    }

    @Test
    public void testEncodeArrayOfStructFromStringAbi() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded =
                codec.encodeMethodFromString(
                        NESTED_ABI,
                        "setEntries",
                        Collections.singletonList(
                                "[{\"name\":\"a\",\"value\":1},{\"name\":\"bb\",\"value\":2}]"));
        assertNotNull(encoded);
        List<String> decoded = codec.decodeMethodInputToString(NESTED_ABI, "setEntries", encoded);
        assertNotNull(decoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testEncodeNestedStructFromStringWasm() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded =
                codec.encodeMethodFromString(
                        NESTED_ABI,
                        "setBox",
                        Collections.singletonList("{\"id\":1,\"point\":{\"x\":2,\"y\":3}}"));
        assertNotNull(encoded);
        // WASM mode encode also writes a 4-byte method id prefix
        assertTrue(encoded.length > 4);
    }

    @Test
    public void testEncodeArrayOfStructFromStringWasm() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded =
                codec.encodeMethodFromString(
                        NESTED_ABI,
                        "setEntries",
                        Collections.singletonList(
                                "[{\"name\":\"a\",\"value\":1},{\"name\":\"bb\",\"value\":2}]"));
        assertNotNull(encoded);
        assertTrue(encoded.length > 4);
    }

    @Test
    public void testEncodeNestedStructByObjectAbi() throws Exception {
        ContractCodec codec = abiCodec();
        // box = (uint256 id, (uint256 x, uint256 y) point)
        StaticStruct point = new StaticStruct(new Uint256(5), new Uint256(6));
        StaticStruct box = new StaticStruct(new Uint256(4), point);
        byte[] encoded =
                codec.encodeMethod(
                        NESTED_ABI, "setBox", Collections.<Object>singletonList(box));
        assertNotNull(encoded);
        assertTrue(encoded.length > 4);
    }

    @Test
    public void testDecodeConstructorInputEmptyReturnsEmpty() throws Exception {
        // when there is nothing after the bin, the result is an empty list (no constructor parse)
        ContractCodec codec = abiCodec();
        String abi = "[{\"inputs\":[],\"type\":\"constructor\"}]";
        List<Object> decoded = codec.decodeConstructorInput(abi, "6060", "6060");
        assertNotNull(decoded);
        assertTrue(decoded.isEmpty());
        List<String> decodedStr = codec.decodeConstructorInputToString(abi, "6060", "6060");
        assertNotNull(decodedStr);
        assertTrue(decodedStr.isEmpty());
    }

    // ------------------------------------------------------------------------------------------
    // ContractCodec error branches
    // ------------------------------------------------------------------------------------------

    @Test(expected = ContractCodecException.class)
    public void testEncodeMethodWrongArgCountThrows() throws Exception {
        // setBox expects 1 argument; provide 0
        abiCodec().encodeMethod(NESTED_ABI, "setBox", new ArrayList<>());
    }

    @Test(expected = ContractCodecException.class)
    public void testEncodeMethodFromStringUnknownMethodThrows() throws Exception {
        abiCodec()
                .encodeMethodFromString(
                        NESTED_ABI, "doesNotExist", Collections.singletonList("1"));
    }

    @Test(expected = ContractCodecException.class)
    public void testEncodeMethodByInterfaceWrongArgCountThrows() throws Exception {
        // signature wants 1 param but we pass 0 -> no encode possible -> exception
        abiCodec().encodeMethodByInterface("setNum(uint256)", new ArrayList<>());
    }

    @Test(expected = ContractCodecException.class)
    public void testEncodeMethodByInterfaceFromStringWrongArgCountThrows() throws Exception {
        abiCodec()
                .encodeMethodByInterfaceFromString(
                        "setNum(uint256)", new ArrayList<>());
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodInputToStringUnknownMethodThrows() throws Exception {
        abiCodec().decodeMethodInputToString(NESTED_ABI, "missing", new byte[] {0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodToStringUnknownMethodThrows() throws Exception {
        abiCodec().decodeMethodToString(NESTED_ABI, "missing", new byte[] {0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testEncodeMethodByIdUnknownIdThrows() throws Exception {
        abiCodec()
                .encodeMethodById(
                        NESTED_ABI, new byte[] {0x00, 0x11, 0x22, 0x33}, new ArrayList<>());
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodInputByIdUnknownIdThrows() throws Exception {
        abiCodec()
                .decodeMethodInputById(
                        NESTED_ABI,
                        new byte[] {0x00, 0x11, 0x22, 0x33},
                        new byte[] {0, 0, 0, 0, 0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodByIdUnknownIdThrows() throws Exception {
        abiCodec()
                .decodeMethodById(
                        NESTED_ABI,
                        new byte[] {0x00, 0x11, 0x22, 0x33},
                        new byte[] {0, 0, 0, 0, 0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeEventUnknownEventThrows() throws Exception {
        EventLog log = new EventLog();
        log.setData("0x");
        log.setTopics(new ArrayList<>());
        abiCodec().decodeEvent(NESTED_ABI, "noSuchEvent", log);
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeEventToStringUnknownEventThrows() throws Exception {
        EventLog log = new EventLog();
        log.setData("0x");
        log.setTopics(new ArrayList<>());
        abiCodec().decodeEventToString(NESTED_ABI, "noSuchEvent", log);
    }

    @Test
    public void testIsWasmAndCryptoSuiteAccessors() {
        ContractCodec abi = abiCodec();
        assertFalse(abi.isWasm());
        assertNotNull(abi.getCryptoSuite());
        assertNotNull(abi.getAbiDefinitionFactory());
        assertNotNull(abi.getFunctionEncoder());

        ContractCodec wasm = wasmCodec();
        assertTrue(wasm.isWasm());
        // Keccak256 hash impl maps back to an ECDSA crypto suite for compatibility
        assertNotNull(wasm.getCryptoSuite());
    }

    // ------------------------------------------------------------------------------------------
    // Fixed / Ufixed fixed-point datatypes
    // ------------------------------------------------------------------------------------------

    @Test
    public void testFixedConstructAndDefault() {
        Fixed fixed = new Fixed(BigInteger.valueOf(42));
        assertNotNull(fixed.getValue());
        assertTrue(fixed.getTypeAsString().startsWith("fixed"));
        assertNotNull(Fixed.DEFAULT);
        assertEquals(BigInteger.ZERO, Fixed.DEFAULT.getValue());
        assertEquals("fixed", Fixed.TYPE_NAME);
    }

    @Test
    public void testFixedFromMAndN() {
        Fixed fixed = new Fixed(BigInteger.ONE, BigInteger.TEN);
        assertNotNull(fixed.getValue());
    }

    @Test
    public void testUfixedConstructAndDefault() {
        Ufixed ufixed = new Ufixed(BigInteger.valueOf(7));
        assertNotNull(ufixed.getValue());
        assertTrue(ufixed.getTypeAsString().startsWith("ufixed"));
        assertNotNull(Ufixed.DEFAULT);
        assertEquals("ufixed", Ufixed.TYPE_NAME);
    }

    @Test
    public void testUfixedFromMAndN() {
        Ufixed ufixed = new Ufixed(BigInteger.valueOf(2), BigInteger.valueOf(3));
        assertNotNull(ufixed.getValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUfixedNegativeRejected() {
        // Ufixed overrides valid() to reject negative values
        new Ufixed(BigInteger.valueOf(-100));
    }

    @Test
    public void testFixedEqualsAndHashCode() {
        Fixed a = new Fixed(BigInteger.valueOf(11));
        Fixed b = new Fixed(BigInteger.valueOf(11));
        Fixed c = new Fixed(BigInteger.valueOf(12));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "fixed");
    }

    // ------------------------------------------------------------------------------------------
    // EventEncoder signatures over array / struct parameters (Utils struct + array branches)
    // ------------------------------------------------------------------------------------------

    @Test
    public void testEventEncoderWithArrayParameter() {
        EventEncoder encoder = new EventEncoder(cryptoSuite().getHashImpl());
        List<TypeReference<?>> params = new ArrayList<>();
        params.add(new TypeReference<DynamicArray<Uint256>>() {});
        Event event = new Event("Transfer", new ArrayList<TypeReference<?>>(params));
        String sig = encoder.encode(event);
        assertNotNull(sig);
        assertTrue(sig.startsWith("0x"));
    }

    @Test
    public void testEventEncoderBuildMethodSignatureStaticArray() {
        EventEncoder encoder = new EventEncoder(cryptoSuite().getHashImpl());
        List<TypeReference<StaticArray2<Uint256>>> params = new ArrayList<>();
        params.add(new TypeReference<StaticArray2<Uint256>>() {});
        String sig = encoder.buildMethodSignature("Pair", params);
        assertEquals("Pair(uint256[2])", sig);
    }

    @Test
    public void testEventEncoderBuildMethodSignatureSimpleTypes() {
        EventEncoder encoder = new EventEncoder(cryptoSuite().getHashImpl());
        List<TypeReference<Uint256>> params = new ArrayList<>();
        params.add(new TypeReference<Uint256>() {});
        String sig = encoder.buildMethodSignature("Single", params);
        assertEquals("Single(uint256)", sig);
        assertNotNull(encoder.buildEventSignature(sig));
    }

    // ------------------------------------------------------------------------------------------
    // Utils helper methods
    // ------------------------------------------------------------------------------------------

    @Test
    public void testUtilsGetTypeNameAndMethodSignSimple() {
        TypeReference<Uint256> ref = new TypeReference<Uint256>() {};
        assertEquals("uint256", Utils.getTypeName(ref));
        assertEquals("uint256", Utils.getMethodSign(ref));
    }

    @Test
    public void testUtilsGetTypeNameDynamicArray() {
        TypeReference<DynamicArray<Uint256>> ref =
                new TypeReference<DynamicArray<Uint256>>() {};
        assertEquals("uint256[]", Utils.getTypeName(ref));
        assertEquals("uint256[]", Utils.getMethodSign(ref));
    }

    @Test
    public void testUtilsGetTypeNameStaticArray() {
        TypeReference<StaticArray2<Uint256>> ref =
                new TypeReference<StaticArray2<Uint256>>() {};
        assertEquals("uint256[2]", Utils.getTypeName(ref));
        assertEquals("uint256[2]", Utils.getMethodSign(ref));
    }

    @Test
    public void testUtilsGetSimpleTypeNameStringAndBytes() {
        assertEquals("string", Utils.getSimpleTypeName(Utf8String.class));
        assertEquals("bytes", Utils.getSimpleTypeName(DynamicBytes.class));
        assertEquals("string", Utils.getSimpleMethodSign(Utf8String.class));
        assertEquals("bytes", Utils.getSimpleMethodSign(DynamicBytes.class));
        assertEquals("bool", Utils.getSimpleTypeName(Bool.class));
        assertEquals("address", Utils.getSimpleTypeName(Address.class));
    }

    @Test
    public void testUtilsConvert() {
        List<TypeReference<?>> input = new ArrayList<>();
        input.add(new TypeReference<Uint256>() {});
        input.add(new TypeReference<Bool>() {});
        List<TypeReference<Type>> converted = Utils.convert(input);
        assertEquals(2, converted.size());
    }

    @Test
    public void testUtilsTypeMapSingle() {
        List<BigInteger> values = Arrays.asList(BigInteger.ONE, BigInteger.TEN);
        List<Uint256> mapped = Utils.typeMap(values, Uint256.class);
        assertEquals(2, mapped.size());
        assertEquals(BigInteger.ONE, mapped.get(0).getValue());
    }

    @Test
    public void testUtilsTypeMapNested() {
        List<List<BigInteger>> input = new ArrayList<>();
        input.add(Arrays.asList(BigInteger.ONE, BigInteger.valueOf(2)));
        input.add(Arrays.asList(BigInteger.valueOf(3)));
        List<DynamicArray> mapped =
                Utils.typeMap(input, DynamicArray.class, Uint256.class);
        assertEquals(2, mapped.size());
        assertEquals(2, mapped.get(0).getValue().size());
    }

    @Test
    public void testUtilsTypeMapEmptyReturnsEmpty() {
        List<BigInteger> empty = new ArrayList<>();
        List<Uint256> mapped = Utils.typeMap(empty, Uint256.class);
        assertTrue(mapped.isEmpty());
    }

    @Test
    public void testUtilsGetLengthAndOffset() throws Exception {
        // static array of 3 uint256 contributes 3 to the length
        StaticArray<Uint256> arr =
                new StaticArray<>(
                        Uint256.class,
                        Arrays.asList(new Uint256(1), new Uint256(2), new Uint256(3)));
        List<Type> params = new ArrayList<>();
        params.add(arr);
        params.add(new Uint256(9));
        assertEquals(4, Utils.getLength(params));

        // getOffset on a simple type is 1
        assertEquals(1, Utils.getOffset(new TypeReference<Uint256>() {}.getType()));
        // getOffset on a static array of static type multiplies by length
        assertEquals(2, Utils.getOffset(new TypeReference<StaticArray2<Uint256>>() {}.getType()));
    }

    @Test
    public void testUtilsDynamicTypeChecks() throws Exception {
        assertTrue(Utils.dynamicType(new TypeReference<Utf8String>() {}.getType()));
        assertTrue(Utils.dynamicType(new TypeReference<DynamicBytes>() {}.getType()));
        assertTrue(Utils.dynamicType(new TypeReference<DynamicArray<Uint256>>() {}.getType()));
        assertFalse(Utils.dynamicType(new TypeReference<Uint256>() {}.getType()));
        // static array of static type is not dynamic
        assertFalse(
                Utils.dynamicType(new TypeReference<StaticArray2<Uint256>>() {}.getType()));
    }

    @Test
    public void testUtilsGetParameterizedTypeFromArray() throws Exception {
        Class<? extends Type> cls =
                Utils.getParameterizedTypeFromArray(new TypeReference<DynamicArray<Uint256>>() {});
        assertEquals(Uint256.class, cls);
    }

    @Test
    public void testUtilsGetClassType() throws Exception {
        assertEquals(
                Uint256.class, Utils.getClassType(new TypeReference<Uint256>() {}.getType()));
    }

    // ------------------------------------------------------------------------------------------
    // EventValues / Encoder simple value classes
    // ------------------------------------------------------------------------------------------

    @Test
    public void testEventValues() {
        List<Type> indexed = new ArrayList<>();
        indexed.add(new Uint256(1));
        List<Type> nonIndexed = new ArrayList<>();
        nonIndexed.add(new Bool(true));
        EventValues values = new EventValues(indexed, nonIndexed);
        assertEquals(1, values.getIndexedValues().size());
        assertEquals(1, values.getNonIndexedValues().size());
        assertEquals(BigInteger.ONE, values.getIndexedValues().get(0).getValue());
    }

    @Test
    public void testEncoderAccessors() {
        Encoder encoder = new Encoder(cryptoSuite().getHashImpl());
        assertNotNull(encoder.getHashImpl());
        encoder.setHashImpl(cryptoSuite().getHashImpl());
        assertNotNull(encoder.getHashImpl());
    }

    @Test
    public void testEncoderDeprecatedCryptoSuiteAccessors() {
        Encoder encoder = new Encoder(cryptoSuite());
        assertNotNull(encoder.getHashImpl());
        assertNotNull(encoder.getCryptoSuite());
        CryptoSuite replacement = cryptoSuite();
        encoder.setCryptoSuite(replacement);
        assertEquals(replacement, encoder.getCryptoSuite());
    }

    // ------------------------------------------------------------------------------------------
    // ContractCodecJsonWrapper.decode(ABIObject) for nested struct + list JsonNode
    // ------------------------------------------------------------------------------------------

    @Test
    public void testJsonWrapperDecodeNestedAbiObjectToJsonNode() throws Exception {
        ContractCodec codec = abiCodec();
        ContractABIDefinition contractABIDefinition =
                codec.getAbiDefinitionFactory().loadABI(NESTED_ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getFunctions().get("setBox").get(0);
        ABIObject inputObject = ABIObjectFactory.createInputObject(abiDefinition);

        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        ABIObject encodedObject =
                wrapper.encode(
                        inputObject,
                        Collections.singletonList("{\"id\":1,\"point\":{\"x\":2,\"y\":3}}"));
        // decode(ABIObject) -> JsonNode walks STRUCT recursively
        assertNotNull(wrapper.decode(encodedObject));
        assertTrue(wrapper.decode(encodedObject).isArray());
    }

    @Test
    public void testJsonWrapperEncodeStructWrongFieldCountThrows() throws Exception {
        ContractCodec codec = abiCodec();
        ContractABIDefinition contractABIDefinition =
                codec.getAbiDefinitionFactory().loadABI(NESTED_ABI);
        ABIDefinition abiDefinition = contractABIDefinition.getFunctions().get("setBox").get(0);
        ABIObject inputObject = ABIObjectFactory.createInputObject(abiDefinition);

        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        try {
            // box struct expects 2 fields; the array form supplies only 1
            wrapper.encode(inputObject, Collections.singletonList("[1]"));
            fail("expected an exception for wrong struct field count");
        } catch (RuntimeException expected) {
            assertNotNull(expected);
        }
    }

    // ------------------------------------------------------------------------------------------
    // datatypes deep equals / getTypeAsString branches
    // ------------------------------------------------------------------------------------------

    @Test
    public void testDynamicStructGetTypeAsStringNested() {
        StaticStruct point = new StaticStruct(new Uint256(1), new Uint256(2));
        DynamicStruct outer = new DynamicStruct(new Utf8String("hi"), point);
        String typeStr = outer.getTypeAsString();
        assertTrue(typeStr.startsWith("("));
        assertTrue(typeStr.contains("string"));
        assertEquals(2, outer.getComponentTypes().size());
        assertTrue(outer.bytes32PaddedLength() > 0);
    }

    @Test
    public void testStaticStructGetTypeAsStringAndComponents() {
        StaticStruct s = new StaticStruct(new Uint256(1), new Bool(true));
        assertEquals("(uint256,bool)", s.getTypeAsString());
        assertEquals(2, s.getComponentTypes().size());
    }

    @Test
    public void testStaticArrayEqualsAndHashCode() {
        StaticArray<Uint256> a =
                new StaticArray<>(Uint256.class, Arrays.asList(new Uint256(1), new Uint256(2)));
        StaticArray<Uint256> b =
                new StaticArray<>(Uint256.class, Arrays.asList(new Uint256(1), new Uint256(2)));
        StaticArray<Uint256> c =
                new StaticArray<>(Uint256.class, Arrays.asList(new Uint256(1), new Uint256(3)));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertEquals("uint256[2]", a.getTypeAsString());
        assertFalse(a.isFixed());
        assertEquals(Uint256.class, a.getComponentType());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testStaticArrayWrongExpectedSizeThrows() {
        // expectedSize mismatch triggers the checkValid branch
        new StaticArray<>(
                Uint256.class, 3, Arrays.asList(new Uint256(1), new Uint256(2)));
    }

    @Test
    public void testNumericTypeEqualsAndBitSize() {
        Uint256 a = new Uint256(5);
        Uint256 b = new Uint256(5);
        Uint256 c = new Uint256(6);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertEquals(256, a.getBitSize());
        a.setBitSize(128);
        assertEquals(128, a.getBitSize());
    }

    // ------------------------------------------------------------------------------------------
    // scale ScaleCodecReader corner cases not covered elsewhere
    // ------------------------------------------------------------------------------------------

    @Test
    public void testScaleReaderHasNextAndHasMore() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {1, 2, 3});
        assertTrue(reader.hasNext());
        assertTrue(reader.hasMore(3));
        assertFalse(reader.hasMore(4));
        // hasMore(0) is always true
        assertTrue(reader.hasMore(0));
        assertEquals(1, reader.readByte());
        assertEquals(2, reader.readByte());
        assertEquals(3, reader.readByte());
        assertFalse(reader.hasNext());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testScaleReaderReadByteOverflowThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[0]);
        reader.readByte();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testScaleReaderReadByteArrayNegativeThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {1, 2});
        reader.readByteArray(-1);
    }

    @Test(expected = NullPointerException.class)
    public void testScaleReaderReadNullThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {1});
        reader.read(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testScaleReaderDecodeIntegerNotEnoughDataThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {1, 2});
        reader.decodeInteger(false, 4);
    }
}
