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
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Test;

/**
 * Extra unit coverage for {@link ContractCodec} method/constructor/event encode-decode paths that
 * are not exercised by the existing round-trip tests: constructor encode/decode (object & string),
 * encodeMethodById, by-interface, scale (wasm) path, event decode, plus the not-found error paths.
 */
public class ContractCodecMoreUnitCoverageTest {

    /** uint256, bool, string, address. */
    private static final String SIMPLE_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"},{\"name\":\"b\",\"type\":\"bool\"},{\"name\":\"s\",\"type\":\"string\"},{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"setAll\",\"outputs\":[{\"name\":\"r\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    /** Constructor takes a single uint256. */
    private static final String CTOR_ABI =
            "[{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},"
                    + "{\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]";

    /** Function taking a dynamic uint256 array. */
    private static final String ARRAY_ABI =
            "[{\"inputs\":[{\"internalType\":\"uint256[]\",\"name\":\"vals\",\"type\":\"uint256[]\"}],\"name\":\"sum\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    /** Function taking a struct {uint256 a; string b;}. */
    private static final String STRUCT_ABI =
            "[{\"inputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"b\",\"type\":\"string\"}],\"internalType\":\"struct S\",\"name\":\"s\",\"type\":\"tuple\"}],\"name\":\"setS\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    /**
     * Event with one indexed string and a non-indexed string. NOTE: decodeIndexedEvent only passes
     * the raw topic through for *dynamic* indexed types (string/bytes/array); non-dynamic indexed
     * values (e.g. indexed uint256) are not supported by this decode path, so we use an indexed
     * string here to exercise the realistic topics + non-indexed data flow.
     */
    private static final String EVENT_ABI =
            "[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"msg\",\"type\":\"string\"}],\"name\":\"Logged\",\"type\":\"event\"}]";

    private static final String SIMPLE_BIN = "60806040";

    private CryptoSuite cryptoSuite() {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }

    // -------------------------------------------------------------------------
    // basic state
    // -------------------------------------------------------------------------

    @Test
    public void testIsWasmAndAccessors() {
        ContractCodec abi = new ContractCodec(cryptoSuite(), false);
        assertFalse(abi.isWasm());
        assertNotNull(abi.getFunctionEncoder());
        assertNotNull(abi.getAbiDefinitionFactory());

        ContractCodec wasm = new ContractCodec(cryptoSuite(), true);
        assertTrue(wasm.isWasm());

        // Hash-based constructor
        ContractCodec viaHash =
                new ContractCodec(cryptoSuite().getHashImpl(), false);
        assertFalse(viaHash.isWasm());
        assertNotNull(viaHash.getCryptoSuite());
    }

    // -------------------------------------------------------------------------
    // constructor encode / decode
    // -------------------------------------------------------------------------

    @Test
    public void testEncodeConstructorObjects() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> params = new ArrayList<>();
        params.add(BigInteger.valueOf(42));

        byte[] encoded = codec.encodeConstructor(CTOR_ABI, SIMPLE_BIN, params);
        assertNotNull(encoded);
        // bin (4 bytes) + 32 byte encoded uint256
        assertEquals(Hex.decode(SIMPLE_BIN).length + 32, encoded.length);

        // The constructor params (no method-id prefix) round-trip via the string decoder.
        List<String> decoded =
                codec.decodeConstructorInputToString(
                        CTOR_ABI, SIMPLE_BIN, Hex.toHexString(encoded));
        assertEquals(1, decoded.size());
        assertEquals("42", decoded.get(0));
    }

    @Test
    public void testEncodeConstructorFromStringMatchesObjects() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] fromObjects =
                codec.encodeConstructor(
                        CTOR_ABI, SIMPLE_BIN, Collections.singletonList(BigInteger.valueOf(7)));
        byte[] fromStrings =
                codec.encodeConstructorFromString(
                        CTOR_ABI, SIMPLE_BIN, Collections.singletonList("7"));
        assertArrayEquals(fromObjects, fromStrings);
    }

    @Test
    public void testDecodeConstructorInputEmptyWhenNoParams() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        // input equal to the bin -> no trailing params
        List<Object> decoded = codec.decodeConstructorInput(CTOR_ABI, SIMPLE_BIN, SIMPLE_BIN);
        assertTrue(decoded.isEmpty());
    }

    @Test
    public void testDecodeConstructorInputToString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] encoded =
                codec.encodeConstructor(
                        CTOR_ABI, SIMPLE_BIN, Collections.singletonList(BigInteger.valueOf(99)));
        List<String> decoded =
                codec.decodeConstructorInputToString(
                        CTOR_ABI, SIMPLE_BIN, Hex.toHexString(encoded));
        assertEquals(1, decoded.size());
        assertEquals("99", decoded.get(0));
    }

    // -------------------------------------------------------------------------
    // method encode by id / decode by id
    // -------------------------------------------------------------------------

    @Test
    public void testEncodeMethodByIdRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] methodId =
                codec.getFunctionEncoder().buildMethodId("setAll(uint256,bool,string,address)");

        List<Object> args = new ArrayList<>();
        args.add(BigInteger.valueOf(5));
        args.add(Boolean.TRUE);
        args.add("byId");
        args.add("0x0000000000000000000000000000000000000003");

        byte[] encoded = codec.encodeMethodById(SIMPLE_ABI, methodId, args);
        assertTrue(encoded.length > 4);

        List<Object> decoded = codec.decodeMethodInputById(SIMPLE_ABI, methodId, encoded);
        assertEquals(4, decoded.size());
        assertEquals(BigInteger.valueOf(5), decoded.get(0));

        List<String> decodedStr = codec.decodeMethodInputByIdToString(SIMPLE_ABI, methodId, encoded);
        assertEquals(4, decodedStr.size());
    }

    @Test
    public void testEncodeMethodByIdFromStringMatches() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] methodId =
                codec.getFunctionEncoder().buildMethodId("setAll(uint256,bool,string,address)");

        List<Object> args = new ArrayList<>();
        args.add(BigInteger.valueOf(1));
        args.add(Boolean.FALSE);
        args.add("x");
        args.add("0x0000000000000000000000000000000000000001");
        byte[] fromObjects = codec.encodeMethodById(SIMPLE_ABI, methodId, args);

        List<String> strArgs =
                Arrays.asList("1", "false", "x", "0x0000000000000000000000000000000000000001");
        byte[] fromStrings = codec.encodeMethodByIdFromString(SIMPLE_ABI, methodId, strArgs);
        assertArrayEquals(fromObjects, fromStrings);
    }

    @Test
    public void testEncodeMethodByInterfaceFromString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        String sig = "setAll(uint256,bool,string,address)";

        List<Object> args = new ArrayList<>();
        args.add(BigInteger.valueOf(3));
        args.add(Boolean.TRUE);
        args.add("iface-str");
        args.add("0x0000000000000000000000000000000000000004");
        byte[] fromObjects = codec.encodeMethodByInterface(sig, args);

        List<String> strArgs =
                Arrays.asList(
                        "3", "true", "iface-str", "0x0000000000000000000000000000000000000004");
        byte[] fromStrings = codec.encodeMethodByInterfaceFromString(sig, strArgs);
        assertArrayEquals(fromObjects, fromStrings);
    }

    @Test
    public void testDecodeMethodByInterfaceToString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        String sig = "setAll(uint256,bool,string,address)";
        List<Object> args = new ArrayList<>();
        args.add(BigInteger.valueOf(8));
        args.add(Boolean.TRUE);
        args.add("hi");
        args.add("0x0000000000000000000000000000000000000005");
        byte[] encoded = codec.encodeMethodByInterface(sig, args);

        List<String> decoded = codec.decodeMethodInputByInterfaceToString(SIMPLE_ABI, sig, encoded);
        assertEquals(4, decoded.size());
    }

    // -------------------------------------------------------------------------
    // dynamic array + struct round trips
    // -------------------------------------------------------------------------

    @Test
    public void testDynamicArrayRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> arrayArg = new ArrayList<>();
        List<BigInteger> vals =
                Arrays.asList(BigInteger.ONE, BigInteger.TEN, BigInteger.valueOf(100));
        arrayArg.add(vals);

        byte[] encoded = codec.encodeMethod(ARRAY_ABI, "sum", arrayArg);
        assertTrue(encoded.length > 4);

        List<String> decoded = codec.decodeMethodInputToString(ARRAY_ABI, "sum", encoded);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("10"));
    }

    @Test
    public void testStructFromStringRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        // struct encoded as a json array string for the tuple argument
        List<String> structArg = Collections.singletonList("[123, \"nested\"]");
        byte[] encoded = codec.encodeMethodFromString(STRUCT_ABI, "setS", structArg);
        assertTrue(encoded.length > 4);

        List<String> decoded = codec.decodeMethodInputToString(STRUCT_ABI, "setS", encoded);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("123"));
        assertTrue(decoded.get(0).contains("nested"));
    }

    // -------------------------------------------------------------------------
    // event decode
    // -------------------------------------------------------------------------

    @Test
    public void testDecodeEvent() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);

        // non-indexed payload is a single string "hello"
        byte[] data =
                codec.encodeMethodFromString(
                        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"msg\",\"type\":\"string\"}],\"name\":\"f\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]",
                        "f",
                        Collections.singletonList("hello"));
        // strip the 4-byte method id -> raw abi data
        byte[] rawData = Arrays.copyOfRange(data, 4, data.length);

        // topic[0] = event signature, topic[1] = the (dynamic, indexed) string key topic which is
        // passed through verbatim by decodeIndexedEvent.
        EventEncoder encoder = new EventEncoder(cryptoSuite().getHashImpl());
        String sigTopic = encoder.buildEventSignature("Logged(string,string)");
        String keyTopic =
                "0x1111111111111111111111111111111111111111111111111111111111111111";

        List<String> topics = new ArrayList<>();
        topics.add(sigTopic);
        topics.add(keyTopic);

        EventLog log = new EventLog();
        log.setData(Hex.toHexString(rawData));
        log.setTopics(topics);

        List<Object> decoded = codec.decodeEvent(EVENT_ABI, "Logged", log);
        assertEquals(2, decoded.size());
        // indexed key is passed through as the raw topic
        assertEquals(keyTopic, decoded.get(0));

        List<String> decodedStr = codec.decodeEventToString(EVENT_ABI, "Logged", log);
        assertEquals(2, decodedStr.size());
        assertTrue(decodedStr.get(1).contains("hello"));
    }

    // -------------------------------------------------------------------------
    // scale (wasm) method path
    // -------------------------------------------------------------------------

    @Test
    public void testScaleMethodToStringRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), true);
        List<Object> args = new ArrayList<>();
        args.add(BigInteger.valueOf(77));
        args.add(Boolean.TRUE);
        args.add("wasm");
        args.add("0x00000000000000000000000000000000000000ee");

        byte[] encoded = codec.encodeMethod(SIMPLE_ABI, "setAll", args);
        assertNotNull(encoded);
        List<String> decoded = codec.decodeMethodInputToString(SIMPLE_ABI, "setAll", encoded);
        assertEquals(4, decoded.size());
        assertTrue(decoded.get(0).contains("77"));
        assertTrue(decoded.get(2).contains("wasm"));
    }

    // -------------------------------------------------------------------------
    // error / not-found paths
    // -------------------------------------------------------------------------

    @Test
    public void testEncodeMethodUnknownMethodThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        try {
            codec.encodeMethod(SIMPLE_ABI, "doesNotExist", new ArrayList<>());
            fail("expected ContractCodecException");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testEncodeMethodFromStringUnknownMethodThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        try {
            codec.encodeMethodFromString(SIMPLE_ABI, "ghost", new ArrayList<>());
            fail("expected ContractCodecException");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testDecodeMethodInputToStringUnknownMethodThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        try {
            codec.decodeMethodInputToString(SIMPLE_ABI, "nope", new byte[] {0, 0, 0, 0});
            fail("expected ContractCodecException");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testDecodeEventUnknownEventThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        EventLog log = new EventLog();
        log.setData("0x");
        log.setTopics(new ArrayList<>());
        try {
            codec.decodeEvent(EVENT_ABI, "Unknown", log);
            fail("expected ContractCodecException");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testDeprecatedDecodeMethodInputReturnsTypes() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> args = new ArrayList<>();
        args.add(BigInteger.valueOf(11));
        args.add(Boolean.TRUE);
        args.add("dep");
        args.add("0x0000000000000000000000000000000000000006");
        byte[] encoded = codec.encodeMethod(SIMPLE_ABI, "setAll", args);

        @SuppressWarnings("deprecation")
        List<Type> types =
                codec.decodeMethodInput(SIMPLE_ABI, "setAll", Hex.toHexString(encoded));
        assertEquals(4, types.size());
        assertEquals(BigInteger.valueOf(11), types.get(0).getValue());
    }
}
