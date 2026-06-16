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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.abi.TypeEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.junit.Test;

/**
 * Exhaustive unit coverage for the public surface of {@link ContractCodec} that the existing
 * Codec*CoverageTest / ABICodecTest classes do not reach. The emphasis is on:
 *
 * <ul>
 *   <li>The wasm/SCALE backend ({@code new ContractCodec(hashImpl, true)}) for the constructor /
 *       method-input / method-output / event decode overloads (existing tests only exercise a small
 *       wasm subset).
 *   <li>The {@code *AbiObject*ByABIDefinition} / {@code decodeMethodAndGetOutputAbiObject} ABIObject
 *       returning overloads and the deprecated {@code decodeMethodByABIDefinition} /
 *       {@code decodeMethodAndGetInputObject(abi, name, input)} overloads.
 *   <li>The private {@code buildType} dispatcher reached through {@code encodeConstructor} /
 *       {@code encodeMethod} for sized int/uint, static bytesN, bool/address/bytes/string and
 *       array element types, plus its validation/error branches.
 *   <li>Both Hash-based constructor compatibility branches (SM3 -&gt; SM_TYPE, Keccak256 -&gt;
 *       ECDSA_TYPE, unknown Hash -&gt; null crypto suite).
 *   <li>The non-dynamic indexed event topic path in {@code decodeIndexedEvent}.
 * </ul>
 *
 * <p>Where a decode result is not deterministic (e.g. wasm round-trips through the JSON wrapper),
 * only non-null / size / no-throw is asserted, as instructed.
 */
public class ContractCodecExhaustiveUnitTest {

    // ----------------------------------------------------------------------------------------
    // fixtures
    // ----------------------------------------------------------------------------------------

    private CryptoSuite cryptoSuite() {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }

    private ContractCodec abiCodec() {
        return new ContractCodec(cryptoSuite(), false);
    }

    private ContractCodec wasmCodec() {
        return new ContractCodec(cryptoSuite().getHashImpl(), true);
    }

    // A non-empty fake constructor bytecode (hex). Any deterministic hex works; no node needed.
    private static final String BIN = "60606040";

    // constructor(uint256 initial, string note)
    // function setAll(uint256,bool,string,address) returns (uint256)
    // function getPair() returns (uint256[2])
    // function ping() (no args / no returns)
    private static final String FULL_ABI =
            "["
                    + "{\"inputs\":[{\"name\":\"initial\",\"type\":\"uint256\"},{\"name\":\"note\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},"
                    + "{\"constant\":false,\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"},{\"name\":\"b\",\"type\":\"bool\"},{\"name\":\"s\",\"type\":\"string\"},{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"setAll\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},"
                    + "{\"constant\":true,\"inputs\":[],\"name\":\"getPair\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[2]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},"
                    + "{\"constant\":false,\"inputs\":[],\"name\":\"ping\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}"
                    + "]";

    private static final String SET_ALL_SIG = "setAll(uint256,bool,string,address)";

    // event Pure(uint256 a, uint256 b) -- no indexed params; topics list only contains topic0.
    private static final String PURE_EVENT_ABI =
            "[{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"b\",\"type\":\"uint256\"}],\"name\":\"Pure\",\"type\":\"event\"}]";

    private static final String PURE_EVENT_SIG = "Pure(uint256,uint256)";

    private List<Object> setAllArgs() {
        List<Object> args = new ArrayList<>();
        args.add(new BigInteger("12345"));
        args.add(Boolean.TRUE);
        args.add("hello exhaustive");
        args.add("0x00000000000000000000000000000000000000ab");
        return args;
    }

    private List<String> setAllStrArgs() {
        List<String> args = new ArrayList<>();
        args.add("12345");
        args.add("true");
        args.add("hello exhaustive");
        args.add("0x00000000000000000000000000000000000000ab");
        return args;
    }

    // ----------------------------------------------------------------------------------------
    // Hash-based constructor compatibility branches
    // ----------------------------------------------------------------------------------------

    @Test
    public void testHashConstructorKeccakMapsToEcdsaCryptoSuite() {
        ContractCodec codec = new ContractCodec(new Keccak256(), false);
        assertFalse(codec.isWasm());
        assertNotNull(codec.getCryptoSuite());
        assertEquals(CryptoType.ECDSA_TYPE, codec.getCryptoSuite().getCryptoTypeConfig());
    }

    @Test
    public void testHashConstructorSm3MapsToSmCryptoSuite() {
        ContractCodec codec = new ContractCodec(new SM3Hash(), true);
        assertTrue(codec.isWasm());
        assertNotNull(codec.getCryptoSuite());
        assertEquals(CryptoType.SM_TYPE, codec.getCryptoSuite().getCryptoTypeConfig());
    }

    @Test
    public void testHashConstructorUnknownHashYieldsNullCryptoSuite() {
        // A custom Hash that is neither SM3Hash nor Keccak256 falls into the else branch where
        // the compatibility crypto suite is left null.
        Hash custom =
                new Hash() {
                    private final Keccak256 delegate = new Keccak256();

                    @Override
                    public String hash(String inputData) {
                        return delegate.hash(inputData);
                    }

                    @Override
                    public String hashBytes(byte[] inputBytes) {
                        return delegate.hashBytes(inputBytes);
                    }

                    @Override
                    public byte[] hash(byte[] inputBytes) {
                        return delegate.hash(inputBytes);
                    }
                };
        ContractCodec codec = new ContractCodec(custom, false);
        assertNull(codec.getCryptoSuite());
        assertNotNull(codec.getAbiDefinitionFactory());
        assertNotNull(codec.getFunctionEncoder());
    }

    // ----------------------------------------------------------------------------------------
    // wasm/SCALE constructor encode + decode
    // ----------------------------------------------------------------------------------------

    @Test
    public void testWasmEncodeConstructorWithParams() throws Exception {
        ContractCodec codec = wasmCodec();
        List<Object> params = new ArrayList<>();
        params.add(new BigInteger("7"));
        params.add("ctor note");
        byte[] encoded = codec.encodeConstructor(FULL_ABI, BIN, params);
        assertNotNull(encoded);
        // wasm constructor wraps the bin + encoded params as two DynamicBytes -> longer than bin.
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }

    @Test
    public void testWasmEncodeConstructorFromString() throws Exception {
        ContractCodec codec = wasmCodec();
        List<String> params = new ArrayList<>();
        params.add("7");
        params.add("ctor note");
        byte[] encoded = codec.encodeConstructorFromString(FULL_ABI, BIN, params);
        assertNotNull(encoded);
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }

    @Test
    public void testWasmEncodeConstructorFromBytesNullParamsUsesUint8Branch() throws Exception {
        // When params == null the wasm path appends a Uint8(0) instead of a DynamicBytes; this is
        // the branch the ABI-mode null test in CodecExtraCoverageTest does not reach.
        ContractCodec codec = wasmCodec();
        byte[] encoded = codec.encodeConstructorFromBytes(BIN, null);
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);
    }

    @Test
    public void testWasmEncodeConstructorFromBytesWithParams() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded = codec.encodeConstructorFromBytes(BIN, new byte[] {1, 2, 3, 4});
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);
    }

    // ----------------------------------------------------------------------------------------
    // wasm/SCALE method encode + decode round trips (input)
    // ----------------------------------------------------------------------------------------

    @Test
    public void testWasmEncodeMethodAndDecodeInputByIdAndInterface() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        assertNotNull(encoded);
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(SET_ALL_SIG);

        List<Object> byId = codec.decodeMethodInputById(FULL_ABI, methodId, encoded);
        assertNotNull(byId);
        assertEquals(4, byId.size());

        List<Object> byInterface =
                codec.decodeMethodInputByInterface(FULL_ABI, SET_ALL_SIG, encoded);
        assertNotNull(byInterface);
        assertEquals(4, byInterface.size());
    }

    @Test
    public void testWasmDecodeMethodInputToStringVariants() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(SET_ALL_SIG);

        List<String> byName = codec.decodeMethodInputToString(FULL_ABI, "setAll", encoded);
        assertEquals(4, byName.size());

        List<String> byId = codec.decodeMethodInputByIdToString(FULL_ABI, methodId, encoded);
        assertEquals(4, byId.size());

        List<String> byInterface =
                codec.decodeMethodInputByInterfaceToString(FULL_ABI, SET_ALL_SIG, encoded);
        assertEquals(4, byInterface.size());
    }

    @Test
    public void testWasmEncodeMethodFromStringMatchesObjects() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] fromObjects = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        byte[] fromStrings = codec.encodeMethodFromString(FULL_ABI, "setAll", setAllStrArgs());
        assertArrayEquals(fromObjects, fromStrings);
    }

    @Test
    public void testWasmEncodeMethodByIdFromStringAndByInterfaceFromString() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(SET_ALL_SIG);
        byte[] byName = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());

        byte[] byIdFromString =
                codec.encodeMethodByIdFromString(FULL_ABI, methodId, setAllStrArgs());
        assertArrayEquals(byName, byIdFromString);

        byte[] byInterfaceFromString =
                codec.encodeMethodByInterfaceFromString(SET_ALL_SIG, setAllStrArgs());
        assertArrayEquals(byName, byInterfaceFromString);
    }

    @Test
    public void testWasmEncodeMethodByInterfaceMatchesByName() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] byName = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        byte[] byInterface = codec.encodeMethodByInterface(SET_ALL_SIG, setAllArgs());
        assertArrayEquals(byName, byInterface);
    }

    @Test
    public void testWasmDecodeMethodAndGetInputObjectAndAbiObject() throws Exception {
        ContractCodec codec = wasmCodec();
        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        String hex = Hex.toHexString(encoded);

        Pair<List<Object>, List<ABIObject>> pair =
                codec.decodeMethodAndGetInputObject(setAll, hex);
        assertNotNull(pair);
        assertEquals(4, pair.getLeft().size());

        ABIObject abiObject = codec.decodeMethodAndGetInputObjectByABIDefinition(setAll, hex);
        assertNotNull(abiObject);
        assertEquals(ABIObject.ObjectType.STRUCT, abiObject.getType());

        List<Object> input = codec.decodeMethodInput(setAll, hex);
        assertEquals(4, input.size());

        ABIObject byName = codec.decodeMethodAndGetInputABIObject(FULL_ABI, "setAll", hex);
        assertNotNull(byName);
        assertEquals(4, byName.getStructFields().size());
    }

    // ----------------------------------------------------------------------------------------
    // ABIObject-returning input overload (ABI mode) reached directly
    // ----------------------------------------------------------------------------------------

    @Test
    public void testAbiDecodeMethodAndGetInputObjectByABIDefinition() throws Exception {
        ContractCodec codec = abiCodec();
        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());

        ABIObject abiObject =
                codec.decodeMethodAndGetInputObjectByABIDefinition(
                        setAll, Hex.toHexString(encoded));
        assertNotNull(abiObject);
        assertEquals(ABIObject.ObjectType.STRUCT, abiObject.getType());
        assertEquals(4, abiObject.getStructFields().size());
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodAndGetInputObjectByABIDefinitionBadInputThrows() throws Exception {
        ContractCodec codec = abiCodec();
        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        // far too short to be decoded -> exception branch.
        codec.decodeMethodAndGetInputObjectByABIDefinition(setAll, "0x00");
    }

    // ----------------------------------------------------------------------------------------
    // deprecated decodeMethodAndGetInputObject(abi, methodName, input)
    // ----------------------------------------------------------------------------------------

    @Test
    public void testDeprecatedDecodeMethodAndGetInputObjectByName() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        List<Type> decoded =
                codec.decodeMethodAndGetInputObject(FULL_ABI, "setAll", Hex.toHexString(encoded));
        assertNotNull(decoded);
        assertEquals(4, decoded.size());
        assertEquals(new BigInteger("12345"), decoded.get(0).getValue());
        assertEquals("hello exhaustive", decoded.get(2).getValue());
    }

    // ----------------------------------------------------------------------------------------
    // output decode: ABIObject-returning + deprecated overloads (ABI mode)
    // ----------------------------------------------------------------------------------------

    @Test
    public void testDecodeMethodAndGetOutAbiObjectByABIDefinition() throws Exception {
        ContractCodec codec = abiCodec();
        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] output = TypeEncoder.encode(new Uint256(BigInteger.valueOf(909)));

        ABIObject abiObject =
                codec.decodeMethodAndGetOutAbiObjectByABIDefinition(
                        setAll, Hex.toHexString(output));
        assertNotNull(abiObject);
        assertEquals(1, abiObject.getStructFields().size());
    }

    @Test
    public void testDecodeMethodAndGetOutputAbiObjectByName() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] output = TypeEncoder.encode(new Uint256(BigInteger.valueOf(31)));
        ABIObject abiObject =
                codec.decodeMethodAndGetOutputAbiObject(
                        FULL_ABI, "setAll", Hex.toHexString(output));
        assertNotNull(abiObject);
        assertEquals(1, abiObject.getStructFields().size());
    }

    @Test
    public void testDeprecatedDecodeMethodByABIDefinitionOutput() throws Exception {
        ContractCodec codec = abiCodec();
        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] output = TypeEncoder.encode(new Uint256(BigInteger.valueOf(64)));

        List<Type> decoded = codec.decodeMethodByABIDefinition(setAll, Hex.toHexString(output));
        assertNotNull(decoded);
        assertEquals(1, decoded.size());
        assertEquals(BigInteger.valueOf(64), decoded.get(0).getValue());
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodByABIDefinitionEmptyOutputsThrows() throws Exception {
        // ping() has no outputs; decoding any output for it yields an empty type list, but the
        // deprecated path still succeeds with an empty list. Use a malformed hex to force the
        // error branch instead.
        ContractCodec codec = abiCodec();
        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(FULL_ABI);
        ABIDefinition getPair = def.getFunctions().get("getPair").get(0);
        // getPair returns uint256[2]; "zz" is not valid hex -> decode throws.
        codec.decodeMethodByABIDefinition(getPair, "zz");
    }

    // ----------------------------------------------------------------------------------------
    // wasm output decode round trips
    // ----------------------------------------------------------------------------------------

    @Test
    public void testWasmDecodeMethodOutput() throws Exception {
        ContractCodec codec = wasmCodec();
        // setAll returns a single uint256; encode the scale output and round-trip decode.
        byte[] output = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(new Uint256(BigInteger.valueOf(42)));
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(SET_ALL_SIG);

        List<String> toStr = codec.decodeMethodToString(FULL_ABI, "setAll", output);
        assertNotNull(toStr);
        assertEquals(1, toStr.size());

        List<Object> byId = codec.decodeMethodById(FULL_ABI, methodId, output);
        assertNotNull(byId);
        assertEquals(1, byId.size());

        List<Object> byInterface =
                codec.decodeMethodByInterface(FULL_ABI, SET_ALL_SIG, output);
        assertNotNull(byInterface);
        assertEquals(1, byInterface.size());

        List<String> byIdStr = codec.decodeMethodByIdToString(FULL_ABI, methodId, output);
        assertEquals(1, byIdStr.size());

        List<String> byInterfaceStr =
                codec.decodeMethodByInterfaceToString(FULL_ABI, SET_ALL_SIG, output);
        assertEquals(1, byInterfaceStr.size());
    }

    @Test
    public void testWasmDecodeMethodOutputAndGetObject() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] output = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(new Uint256(BigInteger.valueOf(15)));
        Pair<List<Object>, List<ABIObject>> pair =
                codec.decodeMethodOutputAndGetObject(FULL_ABI, "setAll", Hex.toHexString(output));
        assertNotNull(pair);
        assertEquals(1, pair.getLeft().size());

        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        Pair<List<Object>, List<ABIObject>> pair2 =
                codec.decodeMethodAndGetOutputObject(setAll, Hex.toHexString(output));
        assertNotNull(pair2);
        assertEquals(1, pair2.getLeft().size());

        List<Object> decoded = codec.decodeMethod(setAll, Hex.toHexString(output));
        assertEquals(1, decoded.size());
    }

    // ----------------------------------------------------------------------------------------
    // wasm constructor input decode
    // ----------------------------------------------------------------------------------------

    @Test
    public void testWasmDecodeConstructorInputEmptyReturnsEmpty() throws Exception {
        // When there is nothing after the bin, both wasm decode variants return empty lists.
        ContractCodec codec = wasmCodec();
        List<Object> decoded = codec.decodeConstructorInput(FULL_ABI, BIN, BIN);
        assertNotNull(decoded);
        assertTrue(decoded.isEmpty());
        List<String> decodedStr = codec.decodeConstructorInputToString(FULL_ABI, BIN, BIN);
        assertNotNull(decodedStr);
        assertTrue(decodedStr.isEmpty());
    }

    // ----------------------------------------------------------------------------------------
    // event decode: no-indexed-params + by topic / interface (data-only path)
    // ----------------------------------------------------------------------------------------

    private EventLog buildPureLog(ContractCodec codec, long a, long b) {
        byte[] eventMethodId = codec.getFunctionEncoder().buildMethodId(PURE_EVENT_SIG);
        String topic0 = Numeric.toHexString(eventMethodId);
        byte[] ea = TypeEncoder.encode(new Uint256(BigInteger.valueOf(a)));
        byte[] eb = TypeEncoder.encode(new Uint256(BigInteger.valueOf(b)));
        byte[] data = new byte[ea.length + eb.length];
        System.arraycopy(ea, 0, data, 0, ea.length);
        System.arraycopy(eb, 0, data, ea.length, eb.length);
        return new EventLog(Numeric.toHexString(data), Collections.singletonList(topic0));
    }

    @Test
    public void testDecodeEventByTopicAndInterfaceDataOnly() throws Exception {
        ContractCodec codec = abiCodec();
        EventLog log = buildPureLog(codec, 11, 22);
        byte[] eventMethodId = codec.getFunctionEncoder().buildMethodId(PURE_EVENT_SIG);
        String topic0 = Numeric.toHexString(eventMethodId);

        List<Object> byTopic = codec.decodeEventByTopic(PURE_EVENT_ABI, topic0, log);
        assertEquals(2, byTopic.size());
        List<Object> byInterface =
                codec.decodeEventByInterface(PURE_EVENT_ABI, PURE_EVENT_SIG, log);
        assertEquals(2, byInterface.size());
        List<String> byTopicStr =
                codec.decodeEventByTopicToString(PURE_EVENT_ABI, topic0, log);
        assertEquals(2, byTopicStr.size());
        List<String> byInterfaceStr =
                codec.decodeEventByInterfaceToString(PURE_EVENT_ABI, PURE_EVENT_SIG, log);
        assertEquals(2, byInterfaceStr.size());
    }

    @Test
    public void testDecodeEventNoIndexedParamsOnlyData() throws Exception {
        // Pure(uint256 a, uint256 b): both non-indexed; only topic0 present.
        ContractCodec codec = abiCodec();
        byte[] eventMethodId = codec.getFunctionEncoder().buildMethodId(PURE_EVENT_SIG);
        String topic0 = Numeric.toHexString(eventMethodId);
        byte[] a = TypeEncoder.encode(new Uint256(BigInteger.valueOf(1)));
        byte[] b = TypeEncoder.encode(new Uint256(BigInteger.valueOf(2)));
        byte[] data = new byte[a.length + b.length];
        System.arraycopy(a, 0, data, 0, a.length);
        System.arraycopy(b, 0, data, a.length, b.length);
        EventLog log = new EventLog(Numeric.toHexString(data), Collections.singletonList(topic0));

        List<Object> decoded = codec.decodeEvent(PURE_EVENT_ABI, "Pure", log);
        assertEquals(2, decoded.size());
        assertTrue(decoded.contains(BigInteger.valueOf(1)));
        assertTrue(decoded.contains(BigInteger.valueOf(2)));

        List<String> str = codec.decodeEventToString(PURE_EVENT_ABI, "Pure", log);
        assertEquals(2, str.size());
    }

    @Test
    public void testDecodeIndexedEventEmptyTopicsYieldsEmpty() throws Exception {
        ContractCodec codec = abiCodec();
        ContractABIDefinition def = codec.getAbiDefinitionFactory().loadABI(PURE_EVENT_ABI);
        ABIDefinition pure = def.getEvents().get("Pure").get(0);
        EventLog log = new EventLog("0x", new ArrayList<>());
        List<String> topics = codec.decodeIndexedEvent(log, pure);
        assertNotNull(topics);
        assertTrue(topics.isEmpty());
    }

    // ----------------------------------------------------------------------------------------
    // buildType dispatcher: encodeConstructorFromString over a rich constructor signature
    // ----------------------------------------------------------------------------------------

    // constructor exercising sized int/uint, bytesN, dynamic bytes, address, bool, string,
    // and both dynamic & fixed arrays so buildType visits each leaf branch.
    private static final String RICH_CTOR_ABI =
            "[{\"inputs\":["
                    + "{\"name\":\"u8\",\"type\":\"uint8\"},"
                    + "{\"name\":\"u\",\"type\":\"uint\"},"
                    + "{\"name\":\"i64\",\"type\":\"int64\"},"
                    + "{\"name\":\"i\",\"type\":\"int\"},"
                    + "{\"name\":\"flag\",\"type\":\"bool\"},"
                    + "{\"name\":\"text\",\"type\":\"string\"},"
                    + "{\"name\":\"addr\",\"type\":\"address\"},"
                    + "{\"name\":\"b4\",\"type\":\"bytes4\"},"
                    + "{\"name\":\"raw\",\"type\":\"bytes\"},"
                    + "{\"name\":\"dynArr\",\"type\":\"uint256[]\"},"
                    + "{\"name\":\"fixArr\",\"type\":\"uint256[2]\"}"
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
    public void testBuildTypeRichConstructorFromStringAbi() throws Exception {
        ContractCodec codec = abiCodec();
        byte[] encoded = codec.encodeConstructorFromString(RICH_CTOR_ABI, BIN, richCtorStrArgs());
        assertNotNull(encoded);
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }

    @Test
    public void testBuildTypeRichConstructorFromStringWasm() throws Exception {
        ContractCodec codec = wasmCodec();
        byte[] encoded = codec.encodeConstructorFromString(RICH_CTOR_ABI, BIN, richCtorStrArgs());
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);
    }

    @Test
    public void testEncodeConstructorWithEmptyArray() throws Exception {
        // dynArr supplied as empty triggers the buildType "elements.isEmpty()" branch.
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":[{\"name\":\"dynArr\",\"type\":\"uint256[]\"}],\"type\":\"constructor\"}]";
        byte[] encoded =
                codec.encodeConstructorFromString(abi, BIN, Collections.singletonList("[]"));
        assertNotNull(encoded);
        assertTrue(encoded.length >= Hex.decode(BIN).length);
    }

    @Test
    public void testEncodeMethodWithIntAndBytesArgsAbi() throws Exception {
        // function f(int256 a, bytes2 b) reached through encodeMethodFromString -> buildType.
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":[{\"name\":\"a\",\"type\":\"int256\"},{\"name\":\"b\",\"type\":\"bytes2\"}],\"name\":\"f\",\"outputs\":[],\"type\":\"function\"}]";
        List<String> args = new ArrayList<>();
        args.add("-42");
        args.add("0x1234");
        byte[] encoded = codec.encodeMethodFromString(abi, "f", args);
        assertNotNull(encoded);
        assertTrue(encoded.length > 4);
        List<String> decoded = codec.decodeMethodInputToString(abi, "f", encoded);
        assertEquals(2, decoded.size());
    }

    // ----------------------------------------------------------------------------------------
    // buildType / encode error branches
    // ----------------------------------------------------------------------------------------

    @Test(expected = ContractCodecException.class)
    public void testEncodeConstructorBytesNLengthMismatchThrows() throws Exception {
        // bytes4 expects 4 bytes; supply 2 -> ContractCodecException from buildType.
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":[{\"name\":\"b\",\"type\":\"bytes4\"}],\"type\":\"constructor\"}]";
        codec.encodeConstructorFromString(abi, BIN, Collections.singletonList("0x1122"));
    }

    @Test(expected = ContractCodecException.class)
    public void testEncodeConstructorBytesNTooLongThrows() throws Exception {
        // static byte array > 32 is rejected by buildType.
        ContractCodec codec = abiCodec();
        String abi =
                "[{\"inputs\":[{\"name\":\"b\",\"type\":\"bytes33\"}],\"type\":\"constructor\"}]";
        StringBuilder hex = new StringBuilder("0x");
        for (int i = 0; i < 33; i++) {
            hex.append("11");
        }
        codec.encodeConstructorFromString(abi, BIN, Collections.singletonList(hex.toString()));
    }

    @Test(expected = ContractCodecException.class)
    public void testEncodeConstructorFromStringWrongArgCountThrows() throws Exception {
        // FULL_ABI constructor expects 2 args; supply 1 -> arg-count guard throws.
        ContractCodec codec = abiCodec();
        codec.encodeConstructorFromString(FULL_ABI, BIN, Collections.singletonList("1"));
    }

    // ----------------------------------------------------------------------------------------
    // remaining error branches across decode-by-id / interface (ABI + wasm)
    // ----------------------------------------------------------------------------------------

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodInputByIdToStringUnknownIdThrows() throws Exception {
        abiCodec()
                .decodeMethodInputByIdToString(
                        FULL_ABI, new byte[] {0x09, 0x08, 0x07, 0x06}, new byte[] {0, 0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodByIdToStringUnknownIdThrows() throws Exception {
        abiCodec()
                .decodeMethodByIdToString(
                        FULL_ABI, new byte[] {0x09, 0x08, 0x07, 0x06}, new byte[] {0, 0, 0, 0, 0});
    }

    @Test(expected = ContractCodecException.class)
    public void testEncodeMethodByIdFromStringUnknownIdThrows() throws Exception {
        abiCodec()
                .encodeMethodByIdFromString(
                        FULL_ABI, new byte[] {0x09, 0x08, 0x07, 0x06}, new ArrayList<>());
    }

    @Test(expected = ContractCodecException.class)
    public void testDecodeMethodToStringUnknownNameThrows() throws Exception {
        abiCodec().decodeMethodToString(FULL_ABI, "noSuchMethod", new byte[] {0, 0, 0, 0});
    }

    @Test(expected = Exception.class)
    public void testDecodeMethodOutputAndGetObjectUnknownNameThrows() throws Exception {
        // unknown method name -> the methods list is null and is iterated without a guard,
        // so an exception is raised (NPE) before any ContractCodecException is built.
        abiCodec().decodeMethodOutputAndGetObject(FULL_ABI, "noSuchMethod", "00");
    }

    @Test(expected = Exception.class)
    public void testDecodeMethodAndGetOutputAbiObjectUnknownNameThrows() throws Exception {
        abiCodec().decodeMethodAndGetOutputAbiObject(FULL_ABI, "noSuchMethod", "00");
    }

    @Test(expected = Exception.class)
    public void testDecodeMethodAndGetInputABIObjectUnknownNameThrows() throws Exception {
        abiCodec().decodeMethodAndGetInputABIObject(FULL_ABI, "noSuchMethod", "0x00000000");
    }
}
