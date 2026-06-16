package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.scale.CompactMode;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecWriter;
import org.fisco.bcos.sdk.v3.codec.scale.reader.UInt128Reader;
import org.fisco.bcos.sdk.v3.codec.scale.writer.CompactULongWriter;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecTools;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Final-pass coverage for still-partial codec packages. Targets distinct paths NOT covered by
 * {@code CodecRoundTripCoverageTest}, {@code CodecDeepCoverageTest}, {@code CodecExtraCoverageTest},
 * {@code ABIObjectTest}, {@code ABIObjectCodecTest}, {@code ABIObjectFactoryTest} and
 * {@code ABIDefinitionTest}:
 *
 * <ul>
 *   <li>wrapper: ContractCodecTools.encode/decode (wasm/scale + struct type-list), value-conversion
 *       helpers, ABIObjectFactory event-object factories + tuple-array building, ContractABIDefinition
 *       overload / event-topic lookup / manual add, ABIDefinition.NamedType + Type helpers,
 *       ABIDefinitionFactory hash-constructor + invalid-ABI, ContractCodecJsonWrapper sized-int /
 *       DBYTES-hex / error branches.
 *   <li>codec: ContractCodec encodeMethodFromString for nested tuple, EventEncoder SM3 / deprecated
 *       constructor.
 *   <li>scale + scale.reader/writer: UInt128Reader, CompactMode branches, CompactULongWriter,
 *       ScaleCodecReader/Writer remaining edges.
 * </ul>
 */
public class CodecFinalCoverageTest {

    private CryptoSuite cryptoSuite() {
        return TestUtils.getCryptoSuite();
    }

    private static final String STRUCT_ABI =
            "[{\"constant\":false,\"inputs\":[{\"components\":[{\"name\":\"x\",\"type\":\"uint256\"},{\"name\":\"y\",\"type\":\"uint256\"}],\"name\":\"t\",\"type\":\"tuple\"}],\"name\":\"useStatic\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    private static final String TUPLE_ARRAY_ABI =
            "[{\"constant\":false,\"inputs\":[{\"components\":[{\"name\":\"x\",\"type\":\"uint256\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"items\",\"type\":\"tuple[]\"}],\"name\":\"useTupleArray\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    private static final String EVENT_ABI =
            "[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}]";

    // ----------------------------------------------------------------------
    // scale.reader.UInt128Reader (only referenced by its own class)
    // ----------------------------------------------------------------------

    @Test
    public void testUInt128ReaderReadLittleEndian() {
        // 16-byte little-endian encoding of value 1 -> 0x01 followed by 15 zero bytes
        byte[] data = new byte[16];
        data[0] = 1;
        UInt128Reader reader = new UInt128Reader();
        BigInteger value = reader.read(new ScaleCodecReader(data));
        assertEquals(BigInteger.ONE, value);
        assertEquals(16, UInt128Reader.SIZE_BYTES);
    }

    @Test
    public void testUInt128ReaderReverseHelper() {
        byte[] data = new byte[] {1, 2, 3, 4};
        UInt128Reader.reverse(data);
        assertArrayEquals(new byte[] {4, 3, 2, 1}, data);

        byte[] odd = new byte[] {1, 2, 3};
        UInt128Reader.reverse(odd);
        assertArrayEquals(new byte[] {3, 2, 1}, odd);
    }

    @Test
    public void testUInt128ReaderLargerValue() {
        // little-endian 0x00..0001ff -> ff at byte0, 01 at byte1 => 0x01ff = 511
        byte[] data = new byte[16];
        data[0] = (byte) 0xff;
        data[1] = 0x01;
        BigInteger value = new UInt128Reader().read(new ScaleCodecReader(data));
        assertEquals(BigInteger.valueOf(0x01ff), value);
    }

    // ----------------------------------------------------------------------
    // scale.CompactMode enum branches
    // ----------------------------------------------------------------------

    @Test
    public void testCompactModeByValue() {
        assertEquals(CompactMode.SINGLE, CompactMode.byValue((byte) 0b00));
        assertEquals(CompactMode.TWO, CompactMode.byValue((byte) 0b01));
        assertEquals(CompactMode.FOUR, CompactMode.byValue((byte) 0b10));
        assertEquals(CompactMode.BIGINT, CompactMode.byValue((byte) 0b11));
        assertEquals(0b11, CompactMode.BIGINT.getValue());
    }

    @Test
    public void testCompactModeForNumberIntCategories() {
        assertEquals(CompactMode.SINGLE, CompactMode.forNumber(0x3f));
        assertEquals(CompactMode.TWO, CompactMode.forNumber(0x40));
        assertEquals(CompactMode.FOUR, CompactMode.forNumber(0x4000));
        assertEquals(CompactMode.BIGINT, CompactMode.forNumber(0x40000000));
    }

    @Test
    public void testCompactModeForNumberBigIntegerCategories() {
        assertEquals(CompactMode.SINGLE, CompactMode.forNumber(BigInteger.ZERO));
        assertEquals(CompactMode.TWO, CompactMode.forNumber(BigInteger.valueOf(0x40)));
        assertEquals(CompactMode.FOUR, CompactMode.forNumber(BigInteger.valueOf(0x4000)));
        assertEquals(CompactMode.BIGINT, CompactMode.forNumber(BigInteger.valueOf(0x40000000L)));
    }

    @Test
    public void testCompactModeForNumberNegativeThrows() {
        try {
            CompactMode.forNumber(-1L);
            Assert.fail("expected IllegalArgumentException for negative long");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        try {
            CompactMode.forNumber(BigInteger.valueOf(-1));
            Assert.fail("expected IllegalArgumentException for negative BigInteger");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void testCompactModeForNumberTooLargeThrows() {
        try {
            CompactMode.forNumber(BigInteger.valueOf(2).pow(600));
            Assert.fail("expected IllegalArgumentException for value > 2**536-1");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    // ----------------------------------------------------------------------
    // scale.writer.CompactULongWriter (long-based compact encoder)
    // ----------------------------------------------------------------------

    @Test
    public void testCompactULongWriterRoundTrip() throws Exception {
        long[] values = {5L, 100L, 20000L, 200000L};
        for (long v : values) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ScaleCodecWriter writer = new ScaleCodecWriter(out);
            new CompactULongWriter().write(writer, v);
            byte[] data = out.toByteArray();
            assertTrue("value " + v + " encodes to bytes", data.length > 0);

            ScaleCodecReader reader = new ScaleCodecReader(data);
            assertEquals((int) v, reader.readCompact());
        }
    }

    @Test
    public void testCompactULongWriterBigIntPath() throws Exception {
        // a value > 0x3fffffff selects the BIGINT writer branch
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        new CompactULongWriter().write(writer, 1L << 40);
        byte[] data = out.toByteArray();
        assertTrue(data.length > 4);
    }

    // ----------------------------------------------------------------------
    // ScaleCodecReader / ScaleCodecWriter additional edges
    // ----------------------------------------------------------------------

    @Test
    public void testScaleReaderReadComplexReaderNullThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {1});
        try {
            reader.read(null);
            Assert.fail("expected NullPointerException for null reader");
        } catch (NullPointerException expected) {
            // ok
        }
    }

    @Test
    public void testScaleReaderBooleanInvalidThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {2});
        try {
            reader.readBoolean();
            Assert.fail("expected IllegalStateException for non-boolean byte");
        } catch (IllegalStateException expected) {
            // ok
        }
    }

    @Test
    public void testScaleReaderUByteNegativeBranch() {
        // byte 0xff is negative -> UByteReader returns 256 + (-1) = 255
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {(byte) 0xff});
        assertEquals(255, reader.readUByte());
    }

    @Test
    public void testScaleReaderDecodeInt256Negative() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        BigInteger value = BigInteger.valueOf(-123456);
        writer.writeBigInt256(true, value);
        byte[] data = out.toByteArray();
        assertEquals(32, data.length);

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(value, reader.decodeInt256());
    }

    @Test
    public void testScaleReaderDecodeInt256NotEnoughDataThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {0, 0});
        try {
            reader.decodeInt256();
            Assert.fail("expected UnsupportedOperationException for short input");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testScaleReaderDecodeIntegerNotEnoughDataThrows() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {0});
        try {
            reader.decodeInteger(false, 4);
            Assert.fail("expected UnsupportedOperationException for short input");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testScaleWriterUnsignedIntegerHighBitConversion() throws Exception {
        // value with high bit set but < 2^(byteSize*8): exercises the negative-conversion branch.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        BigInteger value = BigInteger.valueOf(200); // > 127, < 256
        writer.writeUnsignedInteger(value, 1);
        byte[] data = out.toByteArray();
        assertEquals(1, data.length);

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(value, reader.decodeInteger(false, 1));
    }

    @Test
    public void testScaleWriterBigInt256UnsignedNegativeThrows() throws Exception {
        ScaleCodecWriter writer = new ScaleCodecWriter(new ByteArrayOutputStream());
        try {
            writer.writeBigInt256(false, BigInteger.valueOf(-1));
            Assert.fail("expected UnsupportedOperationException for unsigned negative");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testScaleWriterWriteCompactGenericWriter() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        writer.write(ScaleCodecWriter.COMPACT_UINT, 63);
        byte[] data = out.toByteArray();
        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(63, reader.readCompact());
    }

    // ----------------------------------------------------------------------
    // ContractCodecTools encode/decode (wasm/scale + struct type-list)
    // ----------------------------------------------------------------------

    @Test
    public void testContractCodecToolsEncodeDecodeAbiStruct() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject input = ABIObjectFactory.createInputObject(def.getFunctions().get("useStatic").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        args.add("[3,4]");
        ABIObject encodedObj = wrapper.encode(input, args);

        byte[] encoded = ContractCodecTools.encode(encodedObj, false);
        assertNotNull(encoded);
        assertEquals(0, encoded.length % 32);

        ABIObject template =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useStatic").get(0));
        ABIObject decoded = ContractCodecTools.decode(template, encoded, false);
        assertNotNull(decoded);
        assertEquals(1, decoded.getStructFields().size());
    }

    @Test
    public void testContractCodecToolsEncodeDecodeScalePath() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject input = ABIObjectFactory.createInputObject(def.getFunctions().get("useStatic").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        args.add("[7,8]");
        ABIObject encodedObj = wrapper.encode(input, args);

        // isWasm = true routes through the SCALE encode / decode paths.
        byte[] encoded = ContractCodecTools.encode(encodedObj, true);
        assertNotNull(encoded);

        ABIObject template =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useStatic").get(0));
        ABIObject decoded = ContractCodecTools.decode(template, encoded, true);
        assertNotNull(decoded);
    }

    @Test
    public void testContractCodecToolsTypeListForStruct() {
        ABIObject struct = new ABIObject(ABIObject.ObjectType.STRUCT);
        struct.getStructFields().add(new ABIObject(new Uint256(BigInteger.ONE)));
        struct.getStructFields().add(new ABIObject(new Uint256(BigInteger.TEN)));
        List<Type> result = ContractCodecTools.getABIObjectTypeListResult(struct);
        assertEquals(2, result.size());
        assertEquals(BigInteger.ONE, result.get(0).getValue());
    }

    @Test
    public void testContractCodecToolsTypeListForDynamicStruct() {
        ABIObject struct = new ABIObject(ABIObject.ObjectType.STRUCT);
        struct.getStructFields().add(new ABIObject(new Utf8String("dyn")));
        struct.getStructFields().add(new ABIObject(new Uint256(BigInteger.ONE)));
        List<Type> result = ContractCodecTools.getABIObjectTypeListResult(struct);
        assertEquals(2, result.size());
        assertEquals("dyn", result.get(0).getValue());
    }

    @Test
    public void testContractCodecToolsDecodeListValue() {
        ABIObject listTemplate = new ABIObject(ABIObject.ListType.DYNAMIC);
        listTemplate.setListValueType(new ABIObject(ABIObject.ValueType.UINT, 256));
        List<Object> values = new ArrayList<>();
        values.add(BigInteger.valueOf(11));
        values.add(BigInteger.valueOf(22));
        ABIObject decoded = ContractCodecTools.decodeAbiObjectListValue(listTemplate, values);
        assertEquals(2, decoded.getListValues().size());
        assertEquals(
                BigInteger.valueOf(11),
                decoded.getListValues().get(0).getNumericValue().getValue());
    }

    @Test
    public void testContractCodecToolsDecodeStructValue() {
        ABIObject structTemplate = new ABIObject(ABIObject.ObjectType.STRUCT);
        structTemplate.getStructFields().add(new ABIObject(ABIObject.ValueType.UINT, 256));
        structTemplate.getStructFields().add(new ABIObject(ABIObject.ValueType.STRING));
        List<Object> fields = new ArrayList<>();
        fields.add(BigInteger.valueOf(99));
        fields.add("hello tools");
        ABIObject decoded = ContractCodecTools.decodeAbiObjectStructValue(structTemplate, fields);
        assertEquals(2, decoded.getStructFields().size());
        assertEquals(
                BigInteger.valueOf(99),
                decoded.getStructFields().get(0).getNumericValue().getValue());
        assertEquals("hello tools", decoded.getStructFields().get(1).getStringValue().getValue());
    }

    // ----------------------------------------------------------------------
    // ABIObjectFactory event-object factories + tuple-array building
    // ----------------------------------------------------------------------

    @Test
    public void testAbiObjectFactoryEventIndexedAndInput() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(EVENT_ABI);
        ABIDefinition transfer = def.getEvents().get("Transfer").get(0);

        List<ABIObject> indexed = ABIObjectFactory.createEventIndexedObject(transfer);
        assertEquals(1, indexed.size());
        assertEquals(ABIObject.ValueType.ADDRESS, indexed.get(0).getValueType());

        ABIObject nonIndexed = ABIObjectFactory.createEventInputObject(transfer);
        assertNotNull(nonIndexed);
        assertEquals(1, nonIndexed.getStructFields().size());
        assertEquals(
                ABIObject.ValueType.UINT, nonIndexed.getStructFields().get(0).getValueType());
    }

    @Test
    public void testAbiObjectFactoryOutputObject() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIDefinition fn = def.getFunctions().get("useStatic").get(0);
        ABIObject output = ABIObjectFactory.createOutputObject(fn);
        assertNotNull(output);
        // useStatic has no outputs
        assertTrue(output.getStructFields().isEmpty());
    }

    @Test
    public void testAbiObjectFactoryBuildTupleArrayType() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(TUPLE_ARRAY_ABI);
        ABIDefinition fn = def.getFunctions().get("useTupleArray").get(0);
        ABIObject input = ABIObjectFactory.createInputObject(fn);
        assertEquals(1, input.getStructFields().size());
        ABIObject listField = input.getStructFields().get(0);
        assertEquals(ABIObject.ObjectType.LIST, listField.getType());
        assertEquals(ABIObject.ListType.DYNAMIC, listField.getListType());
        // a tuple[] is dynamic because each element struct contains a string
        assertTrue(listField.isDynamic());
        assertEquals(ABIObject.ObjectType.STRUCT, listField.getListValueType().getType());
    }

    @Test
    public void testAbiObjectFactoryBuildTypeObjectFixedBytesArray() {
        ABIDefinition.NamedType namedType = new ABIDefinition.NamedType("data", "bytes32[2]");
        ABIObject obj = ABIObjectFactory.buildTypeObject(namedType);
        assertEquals(ABIObject.ObjectType.LIST, obj.getType());
        assertEquals(ABIObject.ListType.FIXED, obj.getListType());
        assertEquals(2, obj.getListLength());
        assertEquals(ABIObject.ValueType.BYTES, obj.getListValueType().getValueType());
    }

    // ----------------------------------------------------------------------
    // ContractABIDefinition overload / event-topic / manual add / deprecated ctor
    // ----------------------------------------------------------------------

    @Test
    public void testContractABIDefinitionOverloadedFunctions() {
        String overloadAbi =
                "[{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"f\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},"
                        + "{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"string\"}],\"name\":\"f\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        ContractABIDefinition def = TestUtils.getContractABIDefinition(overloadAbi);
        // two overloads under the same name
        assertEquals(2, def.getFunctions().get("f").size());
        // both have distinct method ids registered
        assertEquals(2, def.getMethodIDToFunctions().size());
    }

    @Test
    public void testContractABIDefinitionEventTopicLookupByTopic() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(EVENT_ABI);
        ABIDefinition transfer = def.getEvents().get("Transfer").get(0);
        byte[] methodId = transfer.getMethodId(cryptoSuite().getHashImpl());
        String topic = Hex.toHexString(methodId);
        ABIDefinition found = def.getABIDefinitionByEventTopic(topic);
        assertNotNull(found);
        assertEquals("Transfer", found.getName());
        assertEquals(1, def.getEventTopicToEvents().size());
    }

    @Test
    public void testContractABIDefinitionDeprecatedConstructorAndManualAdd() {
        ContractABIDefinition def = new ContractABIDefinition(cryptoSuite());
        assertTrue(def.getFunctions().isEmpty());

        ABIDefinition fn = ABIDefinition.createABIDefinition("foo(uint256)");
        fn.setType("function");
        def.addFunction("foo", fn);
        assertEquals(1, def.getFunctions().get("foo").size());
        byte[] methodId = fn.getMethodId(cryptoSuite().getHashImpl());
        assertNotNull(def.getABIDefinitionByMethodId(methodId));

        def.setConstructor(ABIDefinition.createDefaultConstructorABIDefinition());
        assertNotNull(def.getConstructor());
        assertEquals("constructor", def.getConstructor().getType());
    }

    @Test
    public void testContractABIDefinitionHashConstructor() {
        // construct via Hash directly (non-deprecated ctor) and ensure crypto suite resolves.
        ContractABIDefinition def =
                new ContractABIDefinition(cryptoSuite().getHashImpl());
        ABIDefinition event = new ABIDefinition();
        event.setName("E");
        event.setType("event");
        event.setInputs(new ArrayList<>());
        def.addEvent("E", event);
        assertEquals(1, def.getEvents().get("E").size());
        assertEquals(1, def.getEventTopicToEvents().size());
    }

    // ----------------------------------------------------------------------
    // ABIDefinition.NamedType + Type helpers (tuple paths)
    // ----------------------------------------------------------------------

    @Test
    public void testNamedTypeTupleGetTypeAsString() {
        ABIDefinition.NamedType inner1 = new ABIDefinition.NamedType("x", "uint256");
        ABIDefinition.NamedType inner2 = new ABIDefinition.NamedType("y", "string");
        List<ABIDefinition.NamedType> components = new ArrayList<>();
        components.add(inner1);
        components.add(inner2);
        ABIDefinition.NamedType tuple =
                new ABIDefinition.NamedType("t", "tuple", "", false, components);
        assertEquals("(uint256,string)", tuple.getTypeAsString());
        // a tuple[] should produce (...)[] form
        ABIDefinition.NamedType tupleArray =
                new ABIDefinition.NamedType("t", "tuple[]", "", false, components);
        assertEquals("(uint256,string)[]", tupleArray.getTypeAsString());
    }

    @Test
    public void testNamedTypeNonTupleGetTypeAsString() {
        ABIDefinition.NamedType nt = new ABIDefinition.NamedType("a", "uint256");
        assertEquals("uint256", nt.getTypeAsString());
    }

    @Test
    public void testNamedTypeIsDynamicAndNestedness() {
        ABIDefinition.NamedType plainUint = new ABIDefinition.NamedType("a", "uint256");
        assertFalse(plainUint.isDynamic());
        assertEquals(0, plainUint.nestedness());

        ABIDefinition.NamedType str = new ABIDefinition.NamedType("s", "string");
        assertTrue(str.isDynamic());

        ABIDefinition.NamedType arr = new ABIDefinition.NamedType("a", "uint256[]");
        assertTrue(arr.isDynamic());

        List<ABIDefinition.NamedType> components = new ArrayList<>();
        components.add(new ABIDefinition.NamedType("s", "string"));
        ABIDefinition.NamedType tuple =
                new ABIDefinition.NamedType("t", "tuple", "", false, components);
        // tuple containing a dynamic field is dynamic, nestedness = 1
        assertTrue(tuple.isDynamic());
        assertEquals(1, tuple.nestedness());
    }

    @Test
    public void testNamedTypeStructIdentifierAndEquals() {
        List<ABIDefinition.NamedType> components = new ArrayList<>();
        components.add(new ABIDefinition.NamedType("x", "uint256"));
        ABIDefinition.NamedType a =
                new ABIDefinition.NamedType("t", "tuple[]", "struct Foo[]", false, components);
        // structIdentifier is deterministic for equal definitions
        ABIDefinition.NamedType b =
                new ABIDefinition.NamedType("t", "tuple[]", "struct Foo[]", false, components);
        assertEquals(a.structIdentifier(), b.structIdentifier());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        assertNotNull(a.toString());
        assertNotNull(a.newType());
    }

    @Test
    public void testNamedTypeGettersSetters() {
        ABIDefinition.NamedType nt = new ABIDefinition.NamedType();
        nt.setName("field");
        nt.setType("uint256");
        nt.setInternalType("uint256");
        nt.setIndexed(true);
        nt.setComponents(new ArrayList<>());
        assertEquals("field", nt.getName());
        assertEquals("uint256", nt.getType());
        assertEquals("uint256", nt.getInternalType());
        assertTrue(nt.isIndexed());
        assertTrue(nt.getComponents().isEmpty());
    }

    @Test
    public void testAbiDefinitionConstantAndPayableMutability() {
        ABIDefinition view = new ABIDefinition();
        view.setStateMutability("view");
        assertTrue(view.isConstant());

        ABIDefinition pure = new ABIDefinition();
        pure.setStateMutability("pure");
        assertTrue(pure.isConstant());

        ABIDefinition payable = new ABIDefinition();
        payable.setStateMutability("payable");
        assertTrue(payable.isPayable());

        ABIDefinition plain = new ABIDefinition();
        plain.setStateMutability("nonpayable");
        assertFalse(plain.isConstant());
        assertFalse(plain.isPayable());
    }

    @Test
    public void testAbiDefinitionCreateInvalidSignatureThrows() {
        try {
            ABIDefinition.createABIDefinition("noParens");
            Assert.fail("expected IllegalArgumentException for missing parentheses");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void testAbiDefinitionCreateTupleSignatureThrows() {
        try {
            ABIDefinition.createABIDefinition("f(tuple)");
            Assert.fail("expected IllegalArgumentException for tuple param");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void testAbiDefinitionEqualsAndHashCode() {
        ABIDefinition a = ABIDefinition.createABIDefinition("foo(uint256)");
        ABIDefinition b = ABIDefinition.createABIDefinition("foo(uint256)");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        Assert.assertNotEquals(a, null);
        Assert.assertNotEquals(a, "string");
        ABIDefinition c = ABIDefinition.createABIDefinition("bar(uint256)");
        Assert.assertNotEquals(a, c);
        assertNotNull(a.toString());
    }

    // ----------------------------------------------------------------------
    // ABIDefinitionFactory: hash constructor + invalid ABI returns null
    // ----------------------------------------------------------------------

    @Test
    public void testAbiDefinitionFactoryHashConstructor() {
        ABIDefinitionFactory factory = new ABIDefinitionFactory(cryptoSuite().getHashImpl());
        ContractABIDefinition def = factory.loadABI(STRUCT_ABI);
        assertNotNull(def);
        assertEquals(1, def.getFunctions().size());
        // even an ABI without a constructor entry gets a default constructor injected
        assertNotNull(def.getConstructor());
    }

    @Test
    public void testAbiDefinitionFactoryInvalidAbiReturnsNull() {
        ABIDefinitionFactory factory = new ABIDefinitionFactory(cryptoSuite().getHashImpl());
        assertNull(factory.loadABI("not valid json"));
    }

    // ----------------------------------------------------------------------
    // ContractCodecJsonWrapper: sized-int types, DBYTES via hex prefix, errors
    // ----------------------------------------------------------------------

    private static final String SIZED_INT_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"uint8\"},{\"name\":\"b\",\"type\":\"int8\"},{\"name\":\"c\",\"type\":\"uint64\"}],\"name\":\"sized\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    @Test
    public void testJsonWrapperSizedIntegerTypes() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(SIZED_INT_ABI);
        ABIObject input = ABIObjectFactory.createInputObject(def.getFunctions().get("sized").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        args.add("200");
        args.add("-5");
        args.add("123456789");
        ABIObject encoded = wrapper.encode(input, args);
        assertNotNull(encoded);

        ABIObject template =
                ABIObjectFactory.createInputObject(def.getFunctions().get("sized").get(0));
        List<String> decoded = wrapper.decode(template, encoded.encode(false), false);
        assertEquals(3, decoded.size());
        assertEquals("200", decoded.get(0));
        assertEquals("-5", decoded.get(1));
        assertEquals("123456789", decoded.get(2));
    }

    private static final String DBYTES_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"data\",\"type\":\"bytes\"}],\"name\":\"useBytes\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    @Test
    public void testJsonWrapperDynamicBytesHexPrefixRoundTrip() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(DBYTES_ABI);
        ABIObject input = ABIObjectFactory.createInputObject(def.getFunctions().get("useBytes").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        // hex:// prefix decodes through tryDecodeInputData
        args.add(ContractCodecJsonWrapper.HexEncodedDataPrefix + "0102030405");
        ABIObject encoded = wrapper.encode(input, args);
        assertNotNull(encoded);

        ABIObject template =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useBytes").get(0));
        List<String> decoded = wrapper.decode(template, encoded.encode(false), false);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).startsWith(ContractCodecJsonWrapper.HexEncodedDataPrefix));
        // the decoded payload contains the original hex bytes
        String payload = decoded.get(0).substring(ContractCodecJsonWrapper.HexEncodedDataPrefix.length());
        assertArrayEquals(new byte[] {1, 2, 3, 4, 5}, Hex.decode(payload));
    }

    @Test
    public void testJsonWrapperStructArgAsArrayRoundTrip() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject input = ABIObjectFactory.createInputObject(def.getFunctions().get("useStatic").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        // pass the tuple as a JSON array -> exercises the node.isArray() struct branch
        List<String> args = new ArrayList<>();
        args.add("[5,6]");
        ABIObject encoded = wrapper.encode(input, args);
        assertNotNull(encoded);

        ABIObject template =
                ABIObjectFactory.createInputObject(def.getFunctions().get("useStatic").get(0));
        List<String> decoded = wrapper.decode(template, encoded.encode(false), false);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("5"));
        assertTrue(decoded.get(0).contains("6"));
    }

    @Test
    public void testJsonWrapperStructWrongSizeThrows() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(STRUCT_ABI);
        ABIObject input = ABIObjectFactory.createInputObject(def.getFunctions().get("useStatic").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        // struct needs 2 fields, give 3 -> error
        args.add("[1,2,3]");
        try {
            wrapper.encode(input, args);
            Assert.fail("expected InvalidParameterException for struct field count");
        } catch (InvalidParameterException expected) {
            // ok
        }
    }

    @Test
    public void testJsonWrapperInvalidAddressThrows() throws Exception {
        String addrAbi =
                "[{\"constant\":false,\"inputs\":[{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"f\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
        ContractABIDefinition def = TestUtils.getContractABIDefinition(addrAbi);
        ABIObject input = ABIObjectFactory.createInputObject(def.getFunctions().get("f").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        args.add("not-a-valid-address");
        try {
            wrapper.encode(input, args);
            Assert.fail("expected InvalidParameterException for invalid address");
        } catch (InvalidParameterException expected) {
            // ok
        }
    }

    @Test
    public void testJsonWrapperDecodeSingleAbiObjectBytes() {
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        ABIObject bytesObj =
                new ABIObject(new org.fisco.bcos.sdk.v3.codec.datatypes.Bytes(2, new byte[] {1, 2}));
        assertNotNull(wrapper.decode(bytesObj));
        assertTrue(wrapper.decode(bytesObj).isTextual());
    }

    // ----------------------------------------------------------------------
    // ABIObject.encode(true) wasm path for value types
    // ----------------------------------------------------------------------

    @Test
    public void testAbiObjectEncodeWasmValueTypes() throws Exception {
        assertNotNull(new ABIObject(new Uint256(BigInteger.valueOf(42))).encode(true));
        assertNotNull(new ABIObject(new Bool(true)).encode(true));
        assertNotNull(new ABIObject(new Utf8String("wasm")).encode(true));
        assertNotNull(new ABIObject(new Address(BigInteger.valueOf(0xab))).encode(true));
        assertNotNull(new ABIObject(new DynamicBytes(new byte[] {1, 2, 3})).encode(true));
    }

    @Test
    public void testAbiObjectEncodeWasmStructRoundTrip() throws Exception {
        ABIObject struct = new ABIObject(ABIObject.ObjectType.STRUCT);
        struct.getStructFields().add(new ABIObject(new Uint256(BigInteger.valueOf(3))));
        struct.getStructFields().add(new ABIObject(new Uint256(BigInteger.valueOf(4))));
        byte[] encoded = struct.encode(true);
        assertNotNull(encoded);

        ABIObject template = new ABIObject(ABIObject.ObjectType.STRUCT);
        template.getStructFields().add(new ABIObject(ABIObject.ValueType.UINT, 256));
        template.getStructFields().add(new ABIObject(ABIObject.ValueType.UINT, 256));
        ABIObject decoded = template.decode(encoded, true);
        assertEquals(2, decoded.getStructFields().size());
    }

    // ----------------------------------------------------------------------
    // ContractCodec encodeMethodFromString for nested tuple[] ; EventEncoder SM3
    // ----------------------------------------------------------------------

    @Test
    public void testContractCodecEncodeTupleArrayFromString() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<String> args = new ArrayList<>();
        args.add("[[1,\"first\"],[2,\"second\"]]");
        byte[] encoded = codec.encodeMethodFromString(TUPLE_ARRAY_ABI, "useTupleArray", args);
        assertTrue(encoded.length > 4);

        List<String> decoded =
                codec.decodeMethodInputToString(TUPLE_ARRAY_ABI, "useTupleArray", encoded);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("first"));
        assertTrue(decoded.get(0).contains("second"));
    }

    @Test
    public void testEventEncoderSm3HashImpl() {
        CryptoSuite sm = new CryptoSuite(CryptoType.SM_TYPE);
        EventEncoder encoder = new EventEncoder(sm.getHashImpl());
        List<org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference<?>> params = new ArrayList<>();
        params.add(
                org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference.create(Uint256.class));
        org.fisco.bcos.sdk.v3.codec.datatypes.Event event =
                new org.fisco.bcos.sdk.v3.codec.datatypes.Event("E", params);
        String topic = encoder.encode(event);
        assertTrue(topic.startsWith("0x"));
        assertEquals(66, topic.length());
        assertEquals("E(uint256)", encoder.buildMethodSignature("E", event.getParameters()));
    }

    @Test
    public void testEventEncoderDeprecatedCryptoSuiteConstructor() {
        EventEncoder encoder = new EventEncoder(cryptoSuite());
        assertNotNull(encoder.buildEventSignature("Transfer(address,uint256)"));
        assertTrue(encoder.buildEventSignature("Transfer(address,uint256)").startsWith("0x"));
    }
}
