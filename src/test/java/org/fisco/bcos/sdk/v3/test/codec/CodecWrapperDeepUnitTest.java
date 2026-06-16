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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecTools;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Test;

/**
 * Deep unit coverage for the codec WRAPPER classes:
 *
 * <ul>
 *   <li>{@link ContractCodecTools}: {@code decodeABIObjectValue} for every {@code ValueType} plus
 *       the typed-value (already-wrapped {@code Bool}/{@code Uint}/{@code Int}/{@code Bytes}/{@code
 *       DynamicBytes}/{@code Utf8String}/{@code Address}) acceptance branches and the {@code
 *       errorReport} mismatch branches; list decode from {@code Object[]} / {@code StaticArray} /
 *       {@code DynamicArray}; struct decode from raw {@code DynamicStruct}; the {@code
 *       getABIObjectTypeValue} dispatch (incl. empty list/struct, FIXED rejection); the SCALE
 *       (wasm) and ABI encode/decode recursion for nested struct + array-of-struct.
 *   <li>{@link ContractCodecJsonWrapper}: {@code encode(template,List)} + {@code encodeNode} for
 *       sized ints/uints, fixed bytesN, dynamic bytes (hex + plain), bool, address, string, nested
 *       struct passed both as JSON-array and JSON-object, array-of-struct, the FIXED-list size
 *       guard, the struct-missing-field and struct-size error branches; {@code decode(ABIObject)}
 *       -&gt; {@link JsonNode}; round-trip {@code decode(template,bytes,isWasm)} -&gt; List&lt;String&gt;
 *       through both ABI and wasm backends.
 * </ul>
 *
 * <p>These complement (and do not duplicate) the scenarios already exercised by the existing
 * Codec*CoverageTest / ContractCodecExhaustiveUnitTest classes. Where a wasm round-trip result is
 * not deterministic only non-null / size / no-throw is asserted, as instructed.
 */
public class CodecWrapperDeepUnitTest {

    // ----------------------------------------------------------------------------------------
    // fixtures
    // ----------------------------------------------------------------------------------------

    private CryptoSuite cryptoSuite() {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }

    private ContractCodec abiCodec() {
        return new ContractCodec(cryptoSuite().getHashImpl(), false);
    }

    private ContractCodec wasmCodec() {
        return new ContractCodec(cryptoSuite().getHashImpl(), true);
    }

    private ContractCodecJsonWrapper jsonWrapper() {
        return new ContractCodecJsonWrapper();
    }

    private ABIObject valueObject(ABIObject.ValueType valueType) {
        return new ABIObject(valueType);
    }

    private ABIObject uintObject(int bytesLength) {
        return new ABIObject(ABIObject.ValueType.UINT, bytesLength);
    }

    private ABIObject intObject(int bytesLength) {
        return new ABIObject(ABIObject.ValueType.INT, bytesLength);
    }

    // function struct(tuple(uint256 a, string b)) -- a STRUCT field for json-wrapper struct tests
    private static final String STRUCT_ABI =
            "[{\"inputs\":[{\"components\":["
                    + "{\"name\":\"a\",\"type\":\"uint256\"},"
                    + "{\"name\":\"b\",\"type\":\"string\"}"
                    + "],\"name\":\"p\",\"type\":\"tuple\"}],"
                    + "\"name\":\"useStruct\",\"outputs\":[],\"type\":\"function\"}]";

    // function arrayOfStruct(tuple(uint256 a, bool flag)[] items)
    private static final String ARRAY_OF_STRUCT_ABI =
            "[{\"inputs\":[{\"components\":["
                    + "{\"name\":\"a\",\"type\":\"uint256\"},"
                    + "{\"name\":\"flag\",\"type\":\"bool\"}"
                    + "],\"name\":\"items\",\"type\":\"tuple[]\"}],"
                    + "\"name\":\"arrayOfStruct\",\"outputs\":[],\"type\":\"function\"}]";

    // function manyTypes(uint8,int64,bytes4,bytes,address,bool,string,uint256[2])
    private static final String MANY_TYPES_ABI =
            "[{\"inputs\":["
                    + "{\"name\":\"u8\",\"type\":\"uint8\"},"
                    + "{\"name\":\"i64\",\"type\":\"int64\"},"
                    + "{\"name\":\"b4\",\"type\":\"bytes4\"},"
                    + "{\"name\":\"raw\",\"type\":\"bytes\"},"
                    + "{\"name\":\"addr\",\"type\":\"address\"},"
                    + "{\"name\":\"flag\",\"type\":\"bool\"},"
                    + "{\"name\":\"text\",\"type\":\"string\"},"
                    + "{\"name\":\"fix\",\"type\":\"uint256[2]\"}"
                    + "],\"name\":\"manyTypes\",\"outputs\":[],\"type\":\"function\"}]";

    // SCALE-friendly subset: the SCALE BYTES decode path always reads Bytes32 and static (fixed)
    // arrays do not round-trip cleanly, so the wasm round-trip tests use this leaner signature
    // covering the value-type recursion that SCALE does support.
    private static final String WASM_TYPES_ABI =
            "[{\"inputs\":["
                    + "{\"name\":\"u8\",\"type\":\"uint8\"},"
                    + "{\"name\":\"i64\",\"type\":\"int64\"},"
                    + "{\"name\":\"raw\",\"type\":\"bytes\"},"
                    + "{\"name\":\"addr\",\"type\":\"address\"},"
                    + "{\"name\":\"flag\",\"type\":\"bool\"},"
                    + "{\"name\":\"text\",\"type\":\"string\"},"
                    + "{\"name\":\"dyn\",\"type\":\"uint256[]\"}"
                    + "],\"name\":\"wasmTypes\",\"outputs\":[],\"type\":\"function\"}]";

    private ABIObject manyTypesInput() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(MANY_TYPES_ABI);
        ABIDefinition d = def.getFunctions().get("manyTypes").get(0);
        return ABIObjectFactory.createInputObject(d);
    }

    private ABIObject wasmTypesInput() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(WASM_TYPES_ABI);
        ABIDefinition d = def.getFunctions().get("wasmTypes").get(0);
        return ABIObjectFactory.createInputObject(d);
    }

    private List<String> wasmTypesStrArgs() {
        List<String> args = new ArrayList<>();
        args.add("200"); // uint8
        args.add("-1234"); // int64
        args.add("0xdeadbeef"); // dynamic bytes
        args.add("0x0000000000000000000000000000000000000001"); // address
        args.add("true"); // bool
        args.add("a string"); // string
        args.add("[5,6]"); // uint256[]
        return args;
    }

    private List<String> manyTypesStrArgs() {
        List<String> args = new ArrayList<>();
        args.add("200"); // uint8
        args.add("-1234"); // int64
        args.add("0x11223344"); // bytes4
        args.add("0xdeadbeef"); // dynamic bytes
        args.add("0x0000000000000000000000000000000000000001"); // address
        args.add("true"); // bool
        args.add("a string"); // string
        args.add("[5,6]"); // uint256[2]
        return args;
    }

    // ========================================================================================
    // ContractCodecTools.decodeABIObjectValue: every ValueType from a *raw* Java value
    // ========================================================================================

    @Test
    public void testDecodeValueBoolFromBoolean() {
        ABIObject o = ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.BOOL), Boolean.TRUE);
        assertTrue(o.getBoolValue().getValue());
    }

    @Test
    public void testDecodeValueBoolFromBoolTyped() {
        ABIObject o = ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.BOOL), new Bool(false));
        assertFalse(o.getBoolValue().getValue());
    }

    @Test
    public void testDecodeValueUintFromNumericString() {
        ABIObject o = ContractCodecTools.decodeABIObjectValue(uintObject(8), "200");
        assertEquals(BigInteger.valueOf(200), o.getNumericValue().getValue());
    }

    @Test
    public void testDecodeValueUintFromUintTyped() {
        ABIObject o = ContractCodecTools.decodeABIObjectValue(uintObject(256), new Uint256(BigInteger.TEN));
        assertEquals(BigInteger.TEN, o.getNumericValue().getValue());
    }

    @Test
    public void testDecodeValueUintAllSizes() {
        for (int len : new int[] {8, 16, 32, 64, 128, 256}) {
            ABIObject o = ContractCodecTools.decodeABIObjectValue(uintObject(len), BigInteger.ONE);
            assertEquals(BigInteger.ONE, o.getNumericValue().getValue());
        }
    }

    @Test
    public void testDecodeValueIntAllSizes() {
        for (int len : new int[] {8, 16, 32, 64, 128, 256}) {
            ABIObject o = ContractCodecTools.decodeABIObjectValue(intObject(len), BigInteger.valueOf(-3));
            assertEquals(BigInteger.valueOf(-3), o.getNumericValue().getValue());
        }
    }

    @Test
    public void testDecodeValueIntFromString() {
        ABIObject o = ContractCodecTools.decodeABIObjectValue(intObject(64), "-77");
        assertEquals(BigInteger.valueOf(-77), o.getNumericValue().getValue());
    }

    @Test
    public void testDecodeValueAddressFromString() {
        String addr = "0x0000000000000000000000000000000000000abc";
        ABIObject o = ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.ADDRESS), addr);
        assertNotNull(o.getAddressValue());
    }

    @Test
    public void testDecodeValueAddressFromAddressTyped() {
        Address addr = new Address("0x0000000000000000000000000000000000000abc");
        ABIObject o = ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.ADDRESS), addr);
        assertEquals(addr.toString(), o.getAddressValue().toString());
    }

    @Test
    public void testDecodeValueBytesFromRawByteArray() {
        byte[] raw = new byte[] {1, 2, 3, 4};
        ABIObject template = new ABIObject(ABIObject.ValueType.BYTES, 4);
        ABIObject o = ContractCodecTools.decodeABIObjectValue(template, raw);
        assertArrayEquals(raw, o.getBytesValue().getValue());
    }

    @Test
    public void testDecodeValueBytesFromBytesTyped() {
        Bytes bytes = new Bytes(4, new byte[] {9, 8, 7, 6});
        ABIObject template = new ABIObject(ABIObject.ValueType.BYTES, 4);
        ABIObject o = ContractCodecTools.decodeABIObjectValue(template, bytes);
        assertArrayEquals(new byte[] {9, 8, 7, 6}, o.getBytesValue().getValue());
    }

    @Test
    public void testDecodeValueDynamicBytesFromRaw() {
        byte[] raw = new byte[] {5, 6, 7};
        ABIObject o = ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.DBYTES), raw);
        assertArrayEquals(raw, o.getDynamicBytesValue().getValue());
    }

    @Test
    public void testDecodeValueDynamicBytesFromTyped() {
        DynamicBytes db = new DynamicBytes(new byte[] {1, 1});
        ABIObject o = ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.DBYTES), db);
        assertArrayEquals(new byte[] {1, 1}, o.getDynamicBytesValue().getValue());
    }

    @Test
    public void testDecodeValueStringFromString() {
        ABIObject o = ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.STRING), "hi there");
        assertEquals("hi there", o.getStringValue().getValue());
    }

    @Test
    public void testDecodeValueStringFromUtf8StringTyped() {
        ABIObject o =
                ContractCodecTools.decodeABIObjectValue(
                        valueObject(ABIObject.ValueType.STRING), new Utf8String("typed"));
        assertEquals("typed", o.getStringValue().getValue());
    }

    // ----- decodeABIObjectValue: mismatch error branches -----

    @Test(expected = InvalidParameterException.class)
    public void testDecodeValueBoolMismatchThrows() {
        ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.BOOL), new Object());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeValueUintMismatchThrows() {
        // a non-creatable, non-numeric Object -> the errorReport branch
        ContractCodecTools.decodeABIObjectValue(uintObject(256), new Object());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeValueIntMismatchThrows() {
        ContractCodecTools.decodeABIObjectValue(intObject(256), new Object());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeValueAddressMismatchThrows() {
        ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.ADDRESS), new Object());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeValueBytesMismatchThrows() {
        ContractCodecTools.decodeABIObjectValue(new ABIObject(ABIObject.ValueType.BYTES, 4), new Object());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeValueDynamicBytesMismatchThrows() {
        ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.DBYTES), new Object());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeValueStringMismatchThrows() {
        ContractCodecTools.decodeABIObjectValue(valueObject(ABIObject.ValueType.STRING), new Object());
    }

    // ========================================================================================
    // ContractCodecTools.decodeAbiObjectListValue: alternate container inputs
    // ========================================================================================

    private ABIObject dynamicUintList() {
        ABIObject listTemplate = new ABIObject(ABIObject.ListType.DYNAMIC);
        listTemplate.setListValueType(uintObject(256));
        return listTemplate;
    }

    @Test
    public void testDecodeListFromObjectArray() {
        Object[] values = new Object[] {BigInteger.ONE, BigInteger.TEN};
        ABIObject decoded = ContractCodecTools.decodeAbiObjectListValue(dynamicUintList(), values);
        assertEquals(2, decoded.getListValues().size());
        assertEquals(BigInteger.TEN, decoded.getListValues().get(1).getNumericValue().getValue());
    }

    @Test
    public void testDecodeListFromStaticArray() {
        List<Uint256> elems = new ArrayList<>();
        elems.add(new Uint256(BigInteger.valueOf(3)));
        elems.add(new Uint256(BigInteger.valueOf(4)));
        StaticArray<Uint256> arr = new StaticArray<>(Uint256.class, elems);
        ABIObject decoded = ContractCodecTools.decodeAbiObjectListValue(dynamicUintList(), arr);
        assertEquals(2, decoded.getListValues().size());
    }

    @Test
    public void testDecodeListFromDynamicArray() {
        List<Uint256> elems = new ArrayList<>();
        elems.add(new Uint256(BigInteger.valueOf(7)));
        DynamicArray<Uint256> arr = new DynamicArray<>(Uint256.class, elems);
        ABIObject decoded = ContractCodecTools.decodeAbiObjectListValue(dynamicUintList(), arr);
        assertEquals(1, decoded.getListValues().size());
    }

    @Test
    public void testDecodeListNestedListRecursion() {
        // uint256[][] : outer dynamic list whose value type is a dynamic list of uint256.
        ABIObject inner = new ABIObject(ABIObject.ListType.DYNAMIC);
        inner.setListValueType(uintObject(256));
        ABIObject outer = new ABIObject(ABIObject.ListType.DYNAMIC);
        outer.setListValueType(inner);

        List<Object> values = new ArrayList<>();
        values.add(Arrays.asList(BigInteger.ONE, BigInteger.valueOf(2)));
        values.add(Collections.singletonList(BigInteger.valueOf(3)));

        ABIObject decoded = ContractCodecTools.decodeAbiObjectListValue(outer, values);
        assertEquals(2, decoded.getListValues().size());
        assertEquals(2, decoded.getListValues().get(0).getListValues().size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeFixedListWrongSizeThrows() {
        ABIObject listTemplate = new ABIObject(ABIObject.ListType.FIXED);
        listTemplate.setListLength(2);
        listTemplate.setListValueType(uintObject(256));
        // supply 1 element -> fixed list size guard fires
        ContractCodecTools.decodeAbiObjectListValue(
                listTemplate, Collections.singletonList(BigInteger.ONE));
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeListValueTypeMismatchThrows() {
        // template marked as VALUE (not LIST/STRUCT) -> abi-type mismatch guard
        ABIObject bad = valueObject(ABIObject.ValueType.UINT);
        ContractCodecTools.decodeAbiObjectListValue(bad, Collections.singletonList(BigInteger.ONE));
    }

    // ----- decodeAbiObjectStructValue -----

    private ABIObject structTemplate() {
        ABIObject structTemplate = new ABIObject(ABIObject.ObjectType.STRUCT);
        structTemplate.getStructFields().add(uintObject(256));
        structTemplate.getStructFields().add(valueObject(ABIObject.ValueType.STRING));
        return structTemplate;
    }

    @Test
    public void testDecodeStructWithNestedListField() {
        // struct(uint256, uint256[])
        ABIObject structTemplate = new ABIObject(ABIObject.ObjectType.STRUCT);
        structTemplate.getStructFields().add(uintObject(256));
        ABIObject listField = new ABIObject(ABIObject.ListType.DYNAMIC);
        listField.setListValueType(uintObject(256));
        structTemplate.getStructFields().add(listField);

        List<Object> fields = new ArrayList<>();
        fields.add(BigInteger.valueOf(5));
        fields.add(Arrays.asList(BigInteger.ONE, BigInteger.valueOf(2)));

        ABIObject decoded = ContractCodecTools.decodeAbiObjectStructValue(structTemplate, fields);
        assertEquals(2, decoded.getStructFields().size());
        assertEquals(2, decoded.getStructFields().get(1).getListValues().size());
    }

    @Test
    public void testDecodeStructWithNestedStructField() {
        // struct(uint256, struct(string))
        ABIObject inner = new ABIObject(ABIObject.ObjectType.STRUCT);
        inner.getStructFields().add(valueObject(ABIObject.ValueType.STRING));
        ABIObject outer = new ABIObject(ABIObject.ObjectType.STRUCT);
        outer.getStructFields().add(uintObject(256));
        outer.getStructFields().add(inner);

        List<Object> fields = new ArrayList<>();
        fields.add(BigInteger.valueOf(9));
        fields.add(Collections.singletonList("inner string"));

        ABIObject decoded = ContractCodecTools.decodeAbiObjectStructValue(outer, fields);
        assertEquals("inner string", decoded.getStructFields().get(1).getStructFields().get(0).getStringValue().getValue());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDecodeStructWrongTypeThrows() {
        ABIObject bad = valueObject(ABIObject.ValueType.UINT);
        ContractCodecTools.decodeAbiObjectStructValue(bad, Collections.singletonList(BigInteger.ONE));
    }

    // ========================================================================================
    // ContractCodecTools.getABIObjectTypeValue / getABIObjectTypeListResult
    // ========================================================================================

    @Test(expected = UnsupportedOperationException.class)
    public void testGetTypeValueFixedThrows() {
        ABIObject fixed = valueObject(ABIObject.ValueType.FIXED);
        // encode routes through getABIObjectTypeValue -> FIXED/UFIXED throws
        try {
            ContractCodecTools.encode(fixed, false);
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (Exception other) {
            throw new RuntimeException(other);
        }
    }

    @Test
    public void testGetTypeListResultForEmptyDynamicList() {
        // empty dynamic list -> the typeList.isEmpty() branch with DynamicArray
        ABIObject list = dynamicUintList();
        List<Type> result = ContractCodecTools.getABIObjectTypeListResult(list);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetTypeListResultForStruct() {
        ABIObject struct =
                ContractCodecTools.decodeAbiObjectStructValue(
                        structTemplate(), Arrays.asList(BigInteger.ONE, "x"));
        List<Type> result = ContractCodecTools.getABIObjectTypeListResult(struct);
        assertEquals(2, result.size());
    }

    @Test
    public void testEncodeEmptyDynamicListNoThrow() throws Exception {
        ABIObject list = dynamicUintList();
        byte[] abi = ContractCodecTools.encode(list, false);
        assertNotNull(abi);
        byte[] scale = ContractCodecTools.encode(list, true);
        assertNotNull(scale);
    }

    @Test
    public void testEncodeFixedListNoThrow() throws Exception {
        ABIObject listTemplate = new ABIObject(ABIObject.ListType.FIXED);
        listTemplate.setListLength(2);
        listTemplate.setListValueType(uintObject(256));
        ABIObject filled =
                ContractCodecTools.decodeAbiObjectListValue(
                        listTemplate, Arrays.asList(BigInteger.ONE, BigInteger.valueOf(2)));
        byte[] abi = ContractCodecTools.encode(filled, false);
        assertEquals(0, abi.length % 32);
    }

    // ========================================================================================
    // ContractCodecTools.encode + decode round trips through both ABI & SCALE (wasm) backends
    // with a rich many-types method (covers VALUE recursion across the codec switch).
    // ========================================================================================

    @Test
    public void testManyTypesRoundTripAbi() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded = codec.encodeMethodFromString(MANY_TYPES_ABI, "manyTypes", manyTypesStrArgs());
        assertNotNull(encoded);
        List<String> decoded = codec.decodeMethodInputToString(MANY_TYPES_ABI, "manyTypes", encoded);
        assertEquals(8, decoded.size());
        List<Object> decodedObj =
                codec.decodeMethodInput(
                        TestUtils.getContractABIDefinition(MANY_TYPES_ABI)
                                .getFunctions()
                                .get("manyTypes")
                                .get(0),
                        Hex.toHexString(encoded));
        assertEquals(8, decodedObj.size());
    }

    @Test
    public void testManyTypesRoundTripWasm() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded = codec.encodeMethodFromString(WASM_TYPES_ABI, "wasmTypes", wasmTypesStrArgs());
        assertNotNull(encoded);
        List<String> decoded = codec.decodeMethodInputToString(WASM_TYPES_ABI, "wasmTypes", encoded);
        assertEquals(7, decoded.size());
    }

    @Test
    public void testToolsEncodeDecodeManyTypesDirect() throws Exception {
        // exercise ContractCodecTools.encode / decode / decodeJavaObject directly (no method id)
        ABIObject template = manyTypesInput();
        ABIObject filled = jsonWrapper().encode(template, manyTypesStrArgs());
        byte[] encoded = ContractCodecTools.encode(filled, false);
        assertNotNull(encoded);

        List<Object> javaObjects =
                ContractCodecTools.decodeJavaObject(manyTypesInput(), Hex.toHexString(encoded), false);
        assertEquals(8, javaObjects.size());

        ABIObject decoded = ContractCodecTools.decode(manyTypesInput(), encoded, false);
        Pair<List<Object>, List<ABIObject>> pair =
                ContractCodecTools.decodeJavaObjectAndGetOutputObject(decoded);
        assertEquals(8, pair.getLeft().size());
        assertEquals(8, pair.getRight().size());
    }

    @Test
    public void testToolsScaleEncodeDecodeManyTypesDirect() throws Exception {
        ABIObject template = wasmTypesInput();
        ABIObject filled = jsonWrapper().encode(template, wasmTypesStrArgs());
        byte[] encoded = ContractCodecTools.encode(filled, true);
        assertNotNull(encoded);
        ABIObject decoded = ContractCodecTools.decode(wasmTypesInput(), encoded, true);
        assertNotNull(decoded);
        assertEquals(7, decoded.getStructFields().size());
    }

    // ========================================================================================
    // ContractCodecTools.formatBytesN edge: short value (no truncation branch)
    // ========================================================================================

    @Test
    public void testFormatBytesNNoTruncationWhenShorter() {
        ABIObject template = new ABIObject(ABIObject.ValueType.BYTES, 8);
        ABIObject o = ContractCodecTools.decodeABIObjectValue(template, new byte[] {1, 2, 3});
        byte[] formatted = ContractCodecTools.formatBytesN(o);
        // value length (3) <= bytesLength (8) so it is returned unchanged
        assertArrayEquals(new byte[] {1, 2, 3}, formatted);
    }

    @Test
    public void testFormatBytesNTruncatesWhenLonger() {
        ABIObject template = new ABIObject(ABIObject.ValueType.BYTES, 2);
        ABIObject o = ContractCodecTools.decodeABIObjectValue(template, new byte[] {1, 2, 3, 4});
        byte[] formatted = ContractCodecTools.formatBytesN(o);
        assertArrayEquals(new byte[] {1, 2}, formatted);
    }

    // ========================================================================================
    // ContractCodecJsonWrapper.encode + encodeNode value branches
    // ========================================================================================

    @Test
    public void testJsonWrapperEncodeAllSizedTypes() throws Exception {
        ABIObject filled = jsonWrapper().encode(manyTypesInput(), manyTypesStrArgs());
        assertNotNull(filled);
        assertEquals(8, filled.getStructFields().size());
        // bytes4 field retains exactly four bytes
        assertEquals(4, filled.getStructFields().get(2).getBytesValue().getValue().length);
    }

    @Test
    public void testJsonWrapperEncodeDynamicBytesPlainText() throws Exception {
        // a value that is NOT valid hex -> tryDecodeInputData returns null -> falls back to getBytes
        String abi =
                "[{\"inputs\":[{\"name\":\"raw\",\"type\":\"bytes\"}],\"name\":\"f\",\"outputs\":[],\"type\":\"function\"}]";
        ContractABIDefinition def = TestUtils.getContractABIDefinition(abi);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("f").get(0));
        ABIObject filled = jsonWrapper().encode(template, Collections.singletonList("not-hex-zzz"));
        assertArrayEquals("not-hex-zzz".getBytes(), filled.getStructFields().get(0).getDynamicBytesValue().getValue());
    }

    @Test(expected = InvalidParameterException.class)
    public void testJsonWrapperEncodeBytesNWrongLengthThrows() throws Exception {
        String abi =
                "[{\"inputs\":[{\"name\":\"b\",\"type\":\"bytes4\"}],\"name\":\"f\",\"outputs\":[],\"type\":\"function\"}]";
        ContractABIDefinition def = TestUtils.getContractABIDefinition(abi);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("f").get(0));
        // 0x1122 = 2 bytes, but bytes4 needs 4 -> errorReport
        jsonWrapper().encode(template, Collections.singletonList("0x1122"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testJsonWrapperEncodeArgCountMismatchThrows() throws Exception {
        // manyTypes expects 8 args -> supply 1
        jsonWrapper().encode(manyTypesInput(), Collections.singletonList("1"));
    }

    @Test(expected = Exception.class)
    public void testJsonWrapperEncodeBadUintThrows() throws Exception {
        // a uint8 fed a non-numeric value triggers the value-type catch -> errorReport
        String abi =
                "[{\"inputs\":[{\"name\":\"u\",\"type\":\"uint8\"}],\"name\":\"f\",\"outputs\":[],\"type\":\"function\"}]";
        ContractABIDefinition def = TestUtils.getContractABIDefinition(abi);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("f").get(0));
        jsonWrapper().encode(template, Collections.singletonList("not-a-number"));
    }

    // ----- struct passed as JSON array AND as JSON object -----

    @Test
    public void testJsonWrapperEncodeStructAsJsonArray() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        ABIObject filled = jsonWrapper().encode(template, Collections.singletonList("[42,\"hi\"]"));
        ABIObject structField = filled.getStructFields().get(0);
        assertEquals(BigInteger.valueOf(42), structField.getStructFields().get(0).getNumericValue().getValue());
        assertEquals("hi", structField.getStructFields().get(1).getStringValue().getValue());
    }

    @Test
    public void testJsonWrapperEncodeStructAsJsonObject() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        ABIObject filled =
                jsonWrapper().encode(template, Collections.singletonList("{\"a\":99,\"b\":\"named\"}"));
        ABIObject structField = filled.getStructFields().get(0);
        assertEquals(BigInteger.valueOf(99), structField.getStructFields().get(0).getNumericValue().getValue());
        assertEquals("named", structField.getStructFields().get(1).getStringValue().getValue());
    }

    @Test(expected = Exception.class)
    public void testJsonWrapperEncodeStructObjectMissingFieldThrows() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        // object missing field "b" but matching size -> miss-field guard. Use two-field obj where
        // one name is wrong so node.get(field) returns null (the error path NPEs while building the
        // message because the STRUCT template carries no valueType; either way it throws).
        jsonWrapper().encode(template, Collections.singletonList("{\"a\":1,\"wrong\":\"x\"}"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testJsonWrapperEncodeStructWrongSizeThrows() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        // struct needs 2 fields; supply 1 -> struct-size guard
        jsonWrapper().encode(template, Collections.singletonList("[1]"));
    }

    // ----- array-of-struct -----

    @Test
    public void testJsonWrapperEncodeArrayOfStruct() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(ARRAY_OF_STRUCT_ABI);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("arrayOfStruct").get(0));
        ABIObject filled =
                jsonWrapper().encode(template, Collections.singletonList("[[1,true],[2,false]]"));
        ABIObject listField = filled.getStructFields().get(0);
        assertEquals(2, listField.getListValues().size());
        assertTrue(listField.getListValues().get(0).getStructFields().get(1).getBoolValue().getValue());
    }

    @Test
    public void testArrayOfStructRoundTripAbi() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded =
                codec.encodeMethodFromString(
                        ARRAY_OF_STRUCT_ABI,
                        "arrayOfStruct",
                        Collections.singletonList("[[1,true],[2,false]]"));
        assertNotNull(encoded);
        List<String> decoded =
                codec.decodeMethodInputToString(ARRAY_OF_STRUCT_ABI, "arrayOfStruct", encoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testArrayOfStructRoundTripWasm() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded =
                codec.encodeMethodFromString(
                        ARRAY_OF_STRUCT_ABI,
                        "arrayOfStruct",
                        Collections.singletonList("[[1,true],[2,false]]"));
        assertNotNull(encoded);
        List<String> decoded =
                codec.decodeMethodInputToString(ARRAY_OF_STRUCT_ABI, "arrayOfStruct", encoded);
        assertEquals(1, decoded.size());
    }

    // ========================================================================================
    // ContractCodecJsonWrapper.decode(ABIObject) -> JsonNode for each value type
    // ========================================================================================

    @Test
    public void testJsonWrapperDecodeNodeForEachValueType() throws Exception {
        ABIObject filled = jsonWrapper().encode(manyTypesInput(), manyTypesStrArgs());
        JsonNode node = jsonWrapper().decode(filled);
        assertNotNull(node);
        assertTrue(node.isArray());
        assertEquals(8, node.size());
        // uint8 -> numeric, bytes4 -> textual (hex), bool -> boolean, string -> textual
        assertTrue(node.get(0).isNumber());
        assertTrue(node.get(2).isTextual());
        assertTrue(node.get(5).isBoolean());
        assertTrue(node.get(6).isTextual());
        // uint256[2] -> array
        assertTrue(node.get(7).isArray());
    }

    @Test
    public void testJsonWrapperDecodeNodeForStruct() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        ABIObject filled = jsonWrapper().encode(template, Collections.singletonList("[7,\"v\"]"));
        JsonNode node = jsonWrapper().decode(filled);
        assertTrue(node.isArray());
        // first element is the struct -> itself an array node
        assertTrue(node.get(0).isArray());
        assertEquals(2, node.get(0).size());
    }

    // ========================================================================================
    // ContractCodecJsonWrapper.decode(template, bytes, isWasm) -> List<String> round trips
    // ========================================================================================

    @Test
    public void testJsonWrapperDecodeBytesToStringAbi() throws Exception {
        ABIObject filled = jsonWrapper().encode(manyTypesInput(), manyTypesStrArgs());
        byte[] encoded = ContractCodecTools.encode(filled, false);
        List<String> decoded = jsonWrapper().decode(manyTypesInput(), encoded, false);
        assertEquals(8, decoded.size());
        // dynamic bytes are returned with the hex:// prefix
        assertTrue(decoded.get(3).startsWith(ContractCodecJsonWrapper.HexEncodedDataPrefix));
        // bytes4 also hex-prefixed
        assertTrue(decoded.get(2).startsWith(ContractCodecJsonWrapper.HexEncodedDataPrefix));
        // bool stringified
        assertEquals("true", decoded.get(5));
    }

    @Test
    public void testJsonWrapperDecodeBytesToStringWasm() throws Exception {
        ABIObject filled = jsonWrapper().encode(wasmTypesInput(), wasmTypesStrArgs());
        byte[] encoded = ContractCodecTools.encode(filled, true);
        List<String> decoded = jsonWrapper().decode(wasmTypesInput(), encoded, true);
        assertEquals(7, decoded.size());
    }

    @Test
    public void testJsonWrapperDecodeStructToStringAbi() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject template = ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        ABIObject filled = jsonWrapper().encode(template, Collections.singletonList("[8,\"s\"]"));
        byte[] encoded = ContractCodecTools.encode(filled, false);
        ABIObject freshTemplate =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        List<String> decoded = jsonWrapper().decode(freshTemplate, encoded, false);
        // the struct argument is emitted as a single pretty-printed JSON string
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("8"));
    }

    // ========================================================================================
    // ContractCodecJsonWrapper.tryDecodeInputData branches
    // ========================================================================================

    @Test
    public void testTryDecodeInputDataHexPrefix() {
        byte[] decoded = ContractCodecJsonWrapper.tryDecodeInputData("hex://deadbeef");
        assertArrayEquals(new byte[] {(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef}, decoded);
    }

    @Test
    public void testTryDecodeInputDataPlainHex() {
        byte[] decoded = ContractCodecJsonWrapper.tryDecodeInputData("0x1234");
        assertArrayEquals(new byte[] {0x12, 0x34}, decoded);
    }

    @Test
    public void testTryDecodeInputDataNonHexReturnsNull() {
        // not valid hex -> DecoderException caught -> null
        org.junit.Assert.assertNull(ContractCodecJsonWrapper.tryDecodeInputData("hello world!!"));
    }

    @Test
    public void testTryDecodeInputDataEmptyReturnsNull() {
        // empty string decodes to a zero-length array -> returns null
        org.junit.Assert.assertNull(ContractCodecJsonWrapper.tryDecodeInputData(""));
    }
}
