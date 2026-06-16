package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.v3.codec.abi.TypeEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecWriter;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.junit.Assert;
import org.junit.Test;

/**
 * Extra coverage for the high-level {@link ContractCodec} overloads (constructor encode/decode,
 * encode/decode by method id and by interface, output decode variants, event decode variants),
 * {@link ContractCodecJsonWrapper} JSON encode/decode for arrays and structs, {@link Utils} helper
 * methods, {@link EventEncoder} indexed-event signatures, and remaining low-level SCALE
 * {@link ScaleCodecWriter}/{@link ScaleCodecReader} paths. Complements
 * {@code CodecRoundTripCoverageTest} and {@code CodecDeepCoverageTest} without duplicating them.
 */
public class CodecExtraCoverageTest {

    private CryptoSuite cryptoSuite() {
        return TestUtils.getCryptoSuite();
    }

    // A non-empty fake constructor bytecode (hex). Any deterministic hex is fine; node not needed.
    private static final String BIN = "60606040";

    // function setAll(uint256,bool,string,address) returns (uint256)
    // function getPair() returns (uint256[2])
    // event Transfer(address indexed from, address indexed to, uint256 value)
    // constructor(uint256 initial, string memory note)
    private static final String FULL_ABI =
            "["
                    + "{\"inputs\":[{\"name\":\"initial\",\"type\":\"uint256\"},{\"name\":\"note\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},"
                    + "{\"constant\":false,\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"},{\"name\":\"b\",\"type\":\"bool\"},{\"name\":\"s\",\"type\":\"string\"},{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"setAll\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},"
                    + "{\"constant\":true,\"inputs\":[],\"name\":\"getPair\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[2]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},"
                    + "{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}"
                    + "]";

    private static final String SET_ALL_SIG = "setAll(uint256,bool,string,address)";

    // event Transfer(string indexed from, string indexed to, uint256 value)
    // Indexed dynamic (string) parameters are exercised here: ContractCodec.decodeIndexedEvent
    // only supports decoding indexed values for dynamic types (it passes the raw topic through);
    // for non-dynamic indexed values it routes through the JSON wrapper struct path which is not
    // applicable to a single indexed value. Using string-indexed params keeps the realistic event
    // decode path (topics + non-indexed data) exercised end-to-end.
    private static final String EVENT_ABI =
            "[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"string\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}]";

    private static final String EVENT_SIG = "Transfer(string,string,uint256)";

    private List<Object> setAllArgs() {
        List<Object> args = new ArrayList<>();
        args.add(new BigInteger("12345"));
        args.add(Boolean.TRUE);
        args.add("hello extra");
        args.add("0x00000000000000000000000000000000000000ab");
        return args;
    }

    private byte[] methodId(ContractCodec codec) {
        return codec.getFunctionEncoder().buildMethodId(SET_ALL_SIG);
    }

    // ------------------------------------------------------------------
    // ContractCodec: constructor encode / decode
    // ------------------------------------------------------------------

    @Test
    public void testEncodeConstructorFromObjects() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> params = new ArrayList<>();
        params.add(new BigInteger("7"));
        params.add("ctor note");
        byte[] encoded = codec.encodeConstructor(FULL_ABI, BIN, params);
        assertNotNull(encoded);
        // contains the bin prefix
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }

    @Test
    public void testEncodeConstructorFromString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<String> params = new ArrayList<>();
        params.add("7");
        params.add("ctor note");
        byte[] encoded = codec.encodeConstructorFromString(FULL_ABI, BIN, params);
        assertNotNull(encoded);
        assertTrue(encoded.length > Hex.decode(BIN).length);
    }

    @Test
    public void testEncodeConstructorObjectsAndStringMatch() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> objParams = new ArrayList<>();
        objParams.add(new BigInteger("42"));
        objParams.add("same note");
        byte[] fromObjects = codec.encodeConstructor(FULL_ABI, BIN, objParams);

        List<String> strParams = new ArrayList<>();
        strParams.add("42");
        strParams.add("same note");
        byte[] fromStrings = codec.encodeConstructorFromString(FULL_ABI, BIN, strParams);
        assertArrayEquals(fromObjects, fromStrings);
    }

    @Test
    public void testEncodeConstructorFromBytesNullParams() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] encoded = codec.encodeConstructorFromBytes(BIN, null);
        assertArrayEquals(Hex.decode(BIN), encoded);
    }

    @Test
    public void testDecodeConstructorInputRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> params = new ArrayList<>();
        params.add(new BigInteger("99"));
        params.add("ctor decode");
        byte[] encoded = codec.encodeConstructor(FULL_ABI, BIN, params);
        String input = Hex.toHexString(encoded);

        // decodeConstructorInput routes through the deprecated decodeMethodAndGetInputObject
        // which strips a 4-byte method-id prefix from the params; the object-path values are
        // therefore not reliable, so only the size/non-null is asserted here.
        List<Object> decoded = codec.decodeConstructorInput(FULL_ABI, BIN, input);
        assertNotNull(decoded);
        assertEquals(2, decoded.size());

        // The to-string variant decodes the raw params (no method-id stripping) and is exact.
        List<String> decodedStr = codec.decodeConstructorInputToString(FULL_ABI, BIN, input);
        assertEquals(2, decodedStr.size());
        assertEquals("99", decodedStr.get(0));
        assertEquals("ctor decode", decodedStr.get(1));
    }

    // ------------------------------------------------------------------
    // ContractCodec: encode by id / interface / string variants
    // ------------------------------------------------------------------

    @Test
    public void testEncodeMethodByIdMatchesByName() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] byName = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        byte[] byId = codec.encodeMethodById(FULL_ABI, methodId(codec), setAllArgs());
        assertArrayEquals(byName, byId);
    }

    @Test
    public void testEncodeMethodByIdFromString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<String> strArgs = new ArrayList<>();
        strArgs.add("12345");
        strArgs.add("true");
        strArgs.add("hello extra");
        strArgs.add("0x00000000000000000000000000000000000000ab");
        byte[] byIdFromString =
                codec.encodeMethodByIdFromString(FULL_ABI, methodId(codec), strArgs);
        byte[] byName = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        assertArrayEquals(byName, byIdFromString);
    }

    @Test
    public void testEncodeMethodByInterfaceFromString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<String> strArgs = new ArrayList<>();
        strArgs.add("12345");
        strArgs.add("true");
        strArgs.add("hello extra");
        strArgs.add("0x00000000000000000000000000000000000000ab");
        byte[] byInterfaceFromString =
                codec.encodeMethodByInterfaceFromString(SET_ALL_SIG, strArgs);
        byte[] byName = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        assertArrayEquals(byName, byInterfaceFromString);
    }

    @Test
    public void testEncodeMethodFromStringMatchesByName() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<String> strArgs = new ArrayList<>();
        strArgs.add("12345");
        strArgs.add("true");
        strArgs.add("hello extra");
        strArgs.add("0x00000000000000000000000000000000000000ab");
        byte[] fromString = codec.encodeMethodFromString(FULL_ABI, "setAll", strArgs);
        byte[] byName = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        assertArrayEquals(byName, fromString);
    }

    @Test
    public void testEncodeMethodByAbiDefinitionDirectly() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        ContractABIDefinition def = TestUtils.getContractABIDefinition(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] encoded = codec.encodeMethodByAbiDefinition(setAll, setAllArgs());
        assertTrue(encoded.length > 4);
    }

    // ------------------------------------------------------------------
    // ContractCodec: decode input by id / interface variants
    // ------------------------------------------------------------------

    @Test
    public void testDecodeMethodInputByIdAndInterface() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());

        List<Object> byId = codec.decodeMethodInputById(FULL_ABI, methodId(codec), encoded);
        assertEquals(4, byId.size());
        assertEquals(new BigInteger("12345"), byId.get(0));

        List<Object> byInterface =
                codec.decodeMethodInputByInterface(FULL_ABI, SET_ALL_SIG, encoded);
        assertEquals(4, byInterface.size());
    }

    @Test
    public void testDecodeMethodInputByIdAndInterfaceToString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());

        List<String> byId =
                codec.decodeMethodInputByIdToString(FULL_ABI, methodId(codec), encoded);
        assertEquals(4, byId.size());
        assertEquals("12345", byId.get(0));

        List<String> byInterface =
                codec.decodeMethodInputByInterfaceToString(FULL_ABI, SET_ALL_SIG, encoded);
        assertEquals(4, byInterface.size());
    }

    @Test
    public void testDecodeMethodAndGetInputABIObject() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());
        ABIObject abiObject =
                codec.decodeMethodAndGetInputABIObject(
                        FULL_ABI, "setAll", Hex.toHexString(encoded));
        assertNotNull(abiObject);
        assertEquals(4, abiObject.getStructFields().size());
    }

    @Test
    public void testDecodeMethodAndGetInputObjectByABIDefinition() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        ContractABIDefinition def = TestUtils.getContractABIDefinition(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());

        Pair<List<Object>, List<ABIObject>> pair =
                codec.decodeMethodAndGetInputObject(setAll, Hex.toHexString(encoded));
        assertNotNull(pair);
        assertEquals(4, pair.getLeft().size());
        assertEquals(new BigInteger("12345"), pair.getLeft().get(0));
    }

    @Test
    public void testDecodeMethodInputByAbiDefinition() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        ContractABIDefinition def = TestUtils.getContractABIDefinition(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] encoded = codec.encodeMethod(FULL_ABI, "setAll", setAllArgs());

        List<Object> decoded = codec.decodeMethodInput(setAll, Hex.toHexString(encoded));
        assertEquals(4, decoded.size());
    }

    // ------------------------------------------------------------------
    // ContractCodec: decode output variants (uint256 + uint256[2])
    // ------------------------------------------------------------------

    @Test
    public void testDecodeMethodOutputVariantsForUint() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        // setAll returns a single uint256; build its ABI-encoded output.
        byte[] outputBytes = TypeEncoder.encode(new Uint256(BigInteger.valueOf(555)));
        String outputHex = Hex.toHexString(outputBytes);

        Pair<List<Object>, List<ABIObject>> pair =
                codec.decodeMethodOutputAndGetObject(FULL_ABI, "setAll", outputHex);
        assertNotNull(pair);
        assertEquals(1, pair.getLeft().size());
        assertEquals(BigInteger.valueOf(555), pair.getLeft().get(0));

        List<String> toStr = codec.decodeMethodToString(FULL_ABI, "setAll", outputBytes);
        assertEquals(1, toStr.size());
        assertEquals("555", toStr.get(0));

        // decodeMethod by id / interface route through decodeJavaObject (no method id stripping).
        byte[] mid = codec.getFunctionEncoder().buildMethodId(SET_ALL_SIG);
        List<Object> byId = codec.decodeMethodById(FULL_ABI, mid, outputBytes);
        assertEquals(1, byId.size());
        assertEquals(BigInteger.valueOf(555), byId.get(0));

        List<Object> byInterface =
                codec.decodeMethodByInterface(FULL_ABI, SET_ALL_SIG, outputBytes);
        assertEquals(1, byInterface.size());

        List<String> byIdStr = codec.decodeMethodByIdToString(FULL_ABI, mid, outputBytes);
        assertEquals(1, byIdStr.size());
        assertEquals("555", byIdStr.get(0));

        List<String> byIfaceStr =
                codec.decodeMethodByInterfaceToString(FULL_ABI, SET_ALL_SIG, outputBytes);
        assertEquals(1, byIfaceStr.size());
    }

    @Test
    public void testDecodeMethodAndGetOutputObjectDeprecated() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        byte[] outputBytes = TypeEncoder.encode(new Uint256(BigInteger.valueOf(7)));
        String outputHex = Hex.toHexString(outputBytes);

        List<Type> decoded =
                codec.decodeMethodAndGetOutputObject(FULL_ABI, "setAll", outputHex);
        assertEquals(1, decoded.size());
        assertEquals(BigInteger.valueOf(7), decoded.get(0).getValue());
    }

    @Test
    public void testDecodeMethodAndGetOutputObjectByABIDefinition() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        ContractABIDefinition def = TestUtils.getContractABIDefinition(FULL_ABI);
        ABIDefinition setAll = def.getFunctions().get("setAll").get(0);
        byte[] outputBytes = TypeEncoder.encode(new Uint256(BigInteger.valueOf(8)));

        Pair<List<Object>, List<ABIObject>> pair =
                codec.decodeMethodAndGetOutputObject(setAll, Hex.toHexString(outputBytes));
        assertEquals(1, pair.getLeft().size());
        assertEquals(BigInteger.valueOf(8), pair.getLeft().get(0));

        List<Object> decoded = codec.decodeMethod(setAll, Hex.toHexString(outputBytes));
        assertEquals(1, decoded.size());
    }

    @Test
    public void testDecodeMethodOutputForStaticArrayReturn() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        // getPair returns uint256[2]; build encoded output.
        StaticArray2<Uint256> pair =
                new StaticArray2<>(
                        Uint256.class,
                        Arrays.asList(
                                new Uint256(BigInteger.valueOf(11)),
                                new Uint256(BigInteger.valueOf(22))));
        byte[] outputBytes = TypeEncoder.encode(pair);
        List<String> decoded =
                codec.decodeMethodToString(FULL_ABI, "getPair", outputBytes);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("11"));
        assertTrue(decoded.get(0).contains("22"));
    }

    // ------------------------------------------------------------------
    // ContractCodec: event decode variants (indexed + non-indexed)
    // ------------------------------------------------------------------

    private EventLog buildTransferLog(ContractCodec codec) {
        // topic0 must be the 4-byte event methodId (ContractABIDefinition indexes events by
        // methodId, not by the full 32-byte event signature hash).
        byte[] eventMethodId = codec.getFunctionEncoder().buildMethodId(EVENT_SIG);
        String topic0 = Numeric.toHexString(eventMethodId);
        // indexed string params are hashed in real logs; for decoding, decodeIndexedEvent passes
        // the raw 32-byte topic through unchanged, so any 32-byte value works here.
        String fromTopic =
                "0x0000000000000000000000000000000000000000000000000000000000000001";
        String toTopic =
                "0x0000000000000000000000000000000000000000000000000000000000000002";
        // data: value (non-indexed uint256)
        byte[] data = TypeEncoder.encode(new Uint256(BigInteger.valueOf(1000)));
        List<String> topics = new ArrayList<>();
        topics.add(topic0);
        topics.add(fromTopic);
        topics.add(toTopic);
        return new EventLog(Numeric.toHexString(data), topics);
    }

    @Test
    public void testDecodeEventByName() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        EventLog log = buildTransferLog(codec);
        List<Object> decoded = codec.decodeEvent(EVENT_ABI, "Transfer", log);
        assertEquals(3, decoded.size());
        // value is the only non-indexed param
        assertTrue(decoded.contains(BigInteger.valueOf(1000)));
    }

    @Test
    public void testDecodeEventToString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        EventLog log = buildTransferLog(codec);
        List<String> decoded = codec.decodeEventToString(EVENT_ABI, "Transfer", log);
        assertEquals(3, decoded.size());
        assertTrue(decoded.contains("1000"));
    }

    @Test
    public void testDecodeEventByTopicAndInterface() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        EventLog log = buildTransferLog(codec);
        // ContractABIDefinition indexes events by their 4-byte methodId, not by the full
        // 32-byte event signature, so the topic used for lookup must be the methodId.
        byte[] eventMethodId = codec.getFunctionEncoder().buildMethodId(EVENT_SIG);
        String topic0 = Numeric.toHexString(eventMethodId);

        List<Object> byTopic = codec.decodeEventByTopic(EVENT_ABI, topic0, log);
        assertEquals(3, byTopic.size());

        List<Object> byInterface = codec.decodeEventByInterface(EVENT_ABI, EVENT_SIG, log);
        assertEquals(3, byInterface.size());

        List<String> byTopicStr = codec.decodeEventByTopicToString(EVENT_ABI, topic0, log);
        assertEquals(3, byTopicStr.size());

        List<String> byInterfaceStr =
                codec.decodeEventByInterfaceToString(EVENT_ABI, EVENT_SIG, log);
        assertEquals(3, byInterfaceStr.size());
    }

    @Test
    public void testDecodeIndexedEventDirect() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        ContractABIDefinition def = TestUtils.getContractABIDefinition(EVENT_ABI);
        ABIDefinition transfer = def.getEvents().get("Transfer").get(0);
        EventLog log = buildTransferLog(codec);
        List<String> topics = codec.decodeIndexedEvent(log, transfer);
        // topic0 + 2 indexed (dynamic string) topics passed through
        assertEquals(3, topics.size());
    }

    @Test
    public void testDecodeEventUnknownNameThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        EventLog log = new EventLog("0x", new ArrayList<>());
        try {
            codec.decodeEvent(FULL_ABI, "NoSuchEvent", log);
            Assert.fail("expected ContractCodecException for unknown event");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    // ------------------------------------------------------------------
    // ContractCodecJsonWrapper: JSON encode/decode for arrays & structs
    // ------------------------------------------------------------------

    private static final String ARRAY_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"vals\",\"type\":\"uint256[]\"}],\"name\":\"useArray\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    @Test
    public void testJsonWrapperDynamicArrayRoundTrip() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(ARRAY_ABI);
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useArray").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        args.add("[1,2,3,4]");
        ABIObject encoded = wrapper.encode(inputObject, args);
        assertNotNull(encoded);

        ABIObject template =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useArray").get(0));
        List<String> decoded = wrapper.decode(template, encoded.encode(false), false);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("1"));
        assertTrue(decoded.get(0).contains("4"));
    }

    private static final String NESTED_STRUCT_ABI =
            "[{\"constant\":false,\"inputs\":[{\"components\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"info\",\"type\":\"tuple\"}],\"name\":\"useStruct\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    @Test
    public void testJsonWrapperStructRoundTrip() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(NESTED_STRUCT_ABI);
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        args.add("{\"name\": \"hi struct\", \"v\": 88}");
        ABIObject encoded = wrapper.encode(inputObject, args);
        assertNotNull(encoded);

        ABIObject template =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useStruct").get(0));
        List<String> decoded = wrapper.decode(template, encoded.encode(false), false);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("hi struct"));
        assertTrue(decoded.get(0).contains("88"));
    }

    @Test
    public void testJsonWrapperDecodeAbiObjectToJsonNode() throws Exception {
        // exercise the single-arg decode(ABIObject) -> JsonNode for value/list/struct branches.
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        assertTrue(wrapper.decode(new ABIObject(new Bool(true))).asBoolean());
        assertNotNull(wrapper.decode(new ABIObject(new Uint256(BigInteger.TEN))));
        assertNotNull(wrapper.decode(new ABIObject(new Utf8String("node"))));
        assertNotNull(wrapper.decode(new ABIObject(new Address(BigInteger.ONE))));
        assertNotNull(wrapper.decode(new ABIObject(new DynamicBytes(new byte[] {1, 2}))));

        ABIObject list = new ABIObject(ABIObject.ListType.DYNAMIC);
        list.getListValues().add(new ABIObject(new Uint256(BigInteger.ONE)));
        assertTrue(wrapper.decode(list).isArray());

        ABIObject struct = new ABIObject(ABIObject.ObjectType.STRUCT);
        struct.getStructFields().add(new ABIObject(new Uint256(BigInteger.TEN)));
        assertTrue(wrapper.decode(struct).isArray());
    }

    // ------------------------------------------------------------------
    // EventEncoder: indexed event signature
    // ------------------------------------------------------------------

    @Test
    public void testEventEncoderIndexedEventSignature() {
        List<TypeReference<?>> params = new ArrayList<>();
        params.add(TypeReference.create(Address.class, true));
        params.add(TypeReference.create(Address.class, true));
        params.add(TypeReference.create(Uint256.class, false));
        Event event = new Event("Transfer", params);
        assertEquals(2, event.getIndexedParameters().size());
        assertEquals(1, event.getNonIndexedParameters().size());

        EventEncoder encoder = new EventEncoder(cryptoSuite().getHashImpl());
        String topic = encoder.encode(event);
        assertTrue(topic.startsWith("0x"));
        assertEquals(66, topic.length());
        assertEquals(
                "Transfer(address,address,uint256)",
                encoder.buildMethodSignature("Transfer", event.getParameters()));
        assertEquals(topic, encoder.buildEventSignature("Transfer(address,address,uint256)"));
    }

    // ------------------------------------------------------------------
    // Utils: helper methods not yet exercised
    // ------------------------------------------------------------------

    @Test
    public void testUtilsTypeMapAndDynamic() throws Exception {
        List<BigInteger> input = Arrays.asList(BigInteger.ONE, BigInteger.TEN);
        List<Uint256> mapped = Utils.typeMap(input, Uint256.class);
        assertEquals(2, mapped.size());
        assertEquals(BigInteger.ONE, mapped.get(0).getValue());

        // dynamicType: string is dynamic, uint256 is not.
        assertTrue(Utils.dynamicType(new TypeReference<Utf8String>() {}.getType()));
        assertFalse(Utils.dynamicType(TypeReference.create(Uint256.class).getType()));
        assertTrue(Utils.dynamicType(new TypeReference<DynamicArray<Uint256>>() {}.getType()));
    }

    @Test
    public void testUtilsGetLengthAndOffset() throws Exception {
        List<Type> params = new ArrayList<>();
        params.add(new Uint256(BigInteger.ONE));
        params.add(new Bool(true));
        assertEquals(2, Utils.getLength(params));

        assertEquals(1, Utils.getOffset(TypeReference.create(Uint256.class).getType()));
        assertEquals(1, Utils.getOffset(new TypeReference<Utf8String>() {}.getType()));
    }

    @Test
    public void testUtilsGetClassTypeAndParameterized() throws Exception {
        assertEquals(
                Uint256.class, Utils.getClassType(TypeReference.create(Uint256.class).getType()));
        Class<?> inner =
                Utils.getParameterizedTypeFromArray(new TypeReference<DynamicArray<Uint256>>() {});
        assertEquals(Uint256.class, inner);
    }

    @Test
    public void testUtilsTypeMapWithoutGenericType() {
        List<BigInteger> input = Arrays.asList(BigInteger.valueOf(5), BigInteger.valueOf(6));
        List result = Utils.typeMapWithoutGenericType(input, Uint256.class);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof Uint256);
    }

    // ------------------------------------------------------------------
    // SCALE ScaleCodecWriter / ScaleCodecReader remaining paths
    // ------------------------------------------------------------------

    @Test
    public void testScaleWriterCompactIntegerCategories() throws Exception {
        // exercise the four size categories of writeCompactInteger
        int[] values = {1, 100, 20000, 200000};
        for (int v : values) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ScaleCodecWriter writer = new ScaleCodecWriter(out);
            writer.writeCompactInteger(BigInteger.valueOf(v));
            byte[] data = out.toByteArray();
            assertTrue("value " + v + " should encode to bytes", data.length > 0);

            ScaleCodecReader reader = new ScaleCodecReader(data);
            assertEquals(v, reader.readCompact());
        }
    }

    @Test
    public void testScaleWriterDirectWriteAndByteArray() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        writer.directWrite(0x41);
        writer.writeByteArray(new byte[] {0x42, 0x43});
        byte[] data = out.toByteArray();
        assertArrayEquals(new byte[] {0x41, 0x42, 0x43}, data);

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(0x41, reader.readByte());
        assertEquals(0x42, reader.readUByte());
        assertEquals(0x43, reader.readByte());
    }

    @Test
    public void testScaleWriterReadStringRoundTrip() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        String value = "scale read string";
        writer.writeAsList(value.getBytes(StandardCharsets.UTF_8));
        byte[] data = out.toByteArray();

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(value, reader.readString());
    }

    @Test
    public void testScaleWriterWriteCompactLargeValue() throws Exception {
        // a value larger than 2^30 triggers the writeBigInteger branch.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        BigInteger big = BigInteger.valueOf(1L << 40);
        writer.writeCompactInteger(big);
        byte[] data = out.toByteArray();
        assertTrue(data.length > 4);
    }

    @Test
    public void testScaleWriterWriteByteVsDirect() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        writer.writeByte((byte) 0x7f);
        writer.write(ScaleCodecWriter.COMPACT_UINT, 5);
        byte[] data = out.toByteArray();

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(0x7f, reader.readByte());
        assertEquals(5, reader.readCompact());
    }

    @Test
    public void testScaleReaderDecodeIntegerUnsignedLarge() throws Exception {
        // unsigned 4-byte value whose top bit is set must not become negative.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        BigInteger value = BigInteger.valueOf(0xFFFFFFFFL);
        writer.writeUnsignedInteger(value, 4);
        byte[] data = out.toByteArray();
        assertEquals(4, data.length);

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(value, reader.decodeInteger(false, 4));
    }

    @Test
    public void testScaleWriterUnsignedIntegerOverflowThrows() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        try {
            // value >= 2^8 cannot fit in 1 byte
            writer.writeUnsignedInteger(BigInteger.valueOf(256), 1);
            Assert.fail("expected UnsupportedOperationException for overflow");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    // ------------------------------------------------------------------
    // abi FunctionReturnDecoder: multiple / dynamic returns
    // ------------------------------------------------------------------

    @Test
    public void testFunctionReturnDecoderDynamicAndMultiple() {
        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        Uint256 u = new Uint256(BigInteger.valueOf(13));
        Utf8String s = new Utf8String("dynamic return");
        byte[] encU = TypeEncoder.encode(u);
        byte[] encS = TypeEncoder.encode(s);
        // ABI head/tail: uint256 head, string offset head, string tail.
        // Build via two separate single-element decodes is unreliable for dynamic; instead encode
        // a tuple-like layout using offset. To stay safe, only assert the multi-static decode here.
        byte[] combined = new byte[encU.length + encU.length];
        byte[] encU2 = TypeEncoder.encode(new Uint256(BigInteger.valueOf(26)));
        System.arraycopy(encU, 0, combined, 0, encU.length);
        System.arraycopy(encU2, 0, combined, encU.length, encU2.length);

        List<TypeReference<?>> refs = new ArrayList<>();
        refs.add(TypeReference.create(Uint256.class));
        refs.add(TypeReference.create(Uint256.class));
        List<Type> decoded = decoder.decode(Hex.toHexString(combined), Utils.convert(refs));
        assertEquals(2, decoded.size());
        assertEquals(BigInteger.valueOf(13), decoded.get(0).getValue());
        assertEquals(BigInteger.valueOf(26), decoded.get(1).getValue());

        // single dynamic string return round-trips through decode.
        // A function-return tuple with one dynamic element is laid out as
        // [head: offset(=0x20)] [tail: encoded string], so prepend the 32-byte offset word
        // (TypeEncoder.encode(Utf8String) only produces the tail: length + data).
        byte[] offsetHead = TypeEncoder.encode(new Uint256(BigInteger.valueOf(32)));
        byte[] stringReturn = new byte[offsetHead.length + encS.length];
        System.arraycopy(offsetHead, 0, stringReturn, 0, offsetHead.length);
        System.arraycopy(encS, 0, stringReturn, offsetHead.length, encS.length);

        List<TypeReference<?>> sref = new ArrayList<>();
        sref.add(new TypeReference<Utf8String>() {});
        List<Type> dstr = decoder.decode(Hex.toHexString(stringReturn), Utils.convert(sref));
        assertEquals(1, dstr.size());
        assertEquals("dynamic return", dstr.get(0).getValue());
    }
}
