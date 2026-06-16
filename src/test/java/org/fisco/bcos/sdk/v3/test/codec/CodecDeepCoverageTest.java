package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.Encoder;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.EventValues;
import org.fisco.bcos.sdk.v3.codec.Utils;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder;
import org.fisco.bcos.sdk.v3.codec.abi.TypeEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Int;
import org.fisco.bcos.sdk.v3.codec.datatypes.NumericType;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Uint;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint160;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecTools;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Deep / edge-case coverage for the codec stack that complements {@link CodecRoundTripCoverageTest}.
 * Targets: NumericType out-of-range validation, Address / Bytes edge cases, DynamicStruct /
 * StaticStruct, Function type accessors, ABIObject clone / newObject / toString / setters,
 * ABIObjectFactory raw / fixed-ufixed throwing branches, ContractABIDefinition lookup paths,
 * ContractCodecTools value conversion edge cases, ContractCodecJsonWrapper error paths, and
 * ContractCodec error / interface paths for both ABI and SCALE backends.
 */
public class CodecDeepCoverageTest {

    private CryptoSuite cryptoSuite() {
        return TestUtils.getCryptoSuite();
    }

    private static final String SIMPLE_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"},{\"name\":\"b\",\"type\":\"bool\"},{\"name\":\"s\",\"type\":\"string\"},{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"setAll\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    private static final String STRUCT_ABI =
            "[{\"constant\":false,\"inputs\":[{\"components\":[{\"name\":\"x\",\"type\":\"uint256\"},{\"name\":\"y\",\"type\":\"uint256\"}],\"name\":\"t\",\"type\":\"tuple\"}],\"name\":\"useStatic\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"components\":[{\"name\":\"s\",\"type\":\"string\"},{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"d\",\"type\":\"tuple\"}],\"name\":\"useDynamic\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    // ----------------------------------------------------------------------
    // NumericType / IntType / Uint out-of-range validation (throws)
    // ----------------------------------------------------------------------

    @Test
    public void testUint8OutOfRangeThrows() {
        try {
            new Uint8(BigInteger.valueOf(256)); // bitLength 9 > 8
            Assert.fail("expected UnsupportedOperationException for uint8 overflow");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testUintNegativeThrows() {
        try {
            new Uint256(BigInteger.valueOf(-1));
            Assert.fail("expected UnsupportedOperationException for negative uint");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testInt8OutOfRangeThrows() {
        try {
            new Int8(BigInteger.valueOf(256)); // bitLength 9 > 8
            Assert.fail("expected UnsupportedOperationException for int8 overflow");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testUintValidationBoundaries() {
        Uint u = new Uint(BigInteger.ZERO);
        assertTrue(u.validUint(BigInteger.ZERO));
        assertFalse(u.validUint(BigInteger.valueOf(-1)));
        assertTrue(u.validUint(org.fisco.bcos.sdk.v3.codec.abi.Constant.MAX_UINT256));
        assertFalse(
                u.validUint(
                        org.fisco.bcos.sdk.v3.codec.abi.Constant.MAX_UINT256.add(BigInteger.ONE)));
    }

    @Test
    public void testIntValidationBoundaries() {
        Int i = new Int(BigInteger.ZERO);
        assertTrue(i.validInt(BigInteger.valueOf(-12345)));
        assertEquals("int256", i.getTypeAsString());
        assertEquals(256, i.getBitSize());
    }

    @Test
    public void testNumericTypeEqualsAndHashViaAnonymous() {
        // Anonymous subclass to exercise NumericType base equals / hashCode / setBitSize.
        NumericType a = new NumericType("uint256", BigInteger.TEN, 256) {};
        NumericType b = new NumericType("uint256", BigInteger.TEN, 256) {};
        NumericType c = new NumericType("uint256", BigInteger.ONE, 256) {};
        assertEquals(a, a);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        a.setBitSize(128);
        assertEquals(128, a.getBitSize());
    }

    // ----------------------------------------------------------------------
    // Address variants
    // ----------------------------------------------------------------------

    @Test
    public void testAddressFromUint160AndDefault() {
        Address a = new Address(new Uint160(BigInteger.valueOf(0x42)));
        assertEquals(new Address(BigInteger.valueOf(0x42)), a);
        assertEquals(BigInteger.valueOf(0x42), a.toUint160().getValue());
        assertEquals(0, Address.DEFAULT.toUint160().getValue().signum());
        assertEquals(Address.LENGTH_IN_HEX, Address.LENGTH >> 2);
        assertNotEquals(a, Address.DEFAULT);
        assertNotEquals(a, "0x42");
    }

    // ----------------------------------------------------------------------
    // Bytes edge cases
    // ----------------------------------------------------------------------

    @Test
    public void testBytesInvalidLengthZeroThrows() {
        try {
            new Bytes(0, new byte[] {});
            Assert.fail("expected UnsupportedOperationException for empty bytes");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testBytesType32PaddedLengthOverThreshold() {
        // BytesType.bytes32PaddedLength when value.length > 32 -> rounds up.
        DynamicBytes db = new DynamicBytes(new byte[40]);
        assertEquals(64, db.bytes32PaddedLength());
        DynamicBytes exactly32 = new DynamicBytes(new byte[32]);
        assertEquals(32, exactly32.bytes32PaddedLength());
    }

    @Test
    public void testBytesEqualsHashCodeAndDiffType() {
        Bytes4 a = new Bytes4(new byte[] {1, 2, 3, 4});
        Bytes generic = new Bytes(4, new byte[] {1, 2, 3, 4});
        // different runtime classes -> not equal
        assertNotEquals(a, generic);
        assertNotEquals(a, null);
        assertNotEquals(a, "bytes");
    }

    // ----------------------------------------------------------------------
    // DynamicStruct / StaticStruct
    // ----------------------------------------------------------------------

    @Test
    public void testStaticStructTypeAsStringAndComponents() {
        StaticStruct s =
                new StaticStruct(
                        new Uint256(BigInteger.ONE), new Uint256(BigInteger.valueOf(2)));
        assertEquals("(uint256,uint256)", s.getTypeAsString());
        assertEquals(2, s.getComponentTypes().size());
        assertEquals(BigInteger.ONE, s.getComponentTypes().get(0).getValue());
    }

    @Test
    public void testDynamicStructTypeAsStringAndPaddedLength() {
        DynamicStruct s = new DynamicStruct(new Utf8String("hi"), new Uint256(BigInteger.TEN));
        assertEquals("(string,uint256)", s.getTypeAsString());
        assertEquals(2, s.getComponentTypes().size());
        // DynamicStruct.bytes32PaddedLength = super + 32
        assertTrue(s.bytes32PaddedLength() >= 32);
    }

    @Test
    public void testStaticStructEncodeRoundTripViaTypeEncoder() throws Exception {
        StaticStruct s =
                new StaticStruct(
                        new Uint256(BigInteger.valueOf(7)), new Uint256(BigInteger.valueOf(9)));
        byte[] encoded = TypeEncoder.encode(s);
        // two static words
        assertEquals(64, encoded.length);
        assertEquals(7, TypeDecoder.decodeUintAsInt(encoded, 0));
        assertEquals(9, TypeDecoder.decodeUintAsInt(encoded, 32));
    }

    @Test
    public void testDynamicStructEncodeViaTypeEncoder() throws Exception {
        DynamicStruct s = new DynamicStruct(new Utf8String("abc"), new Uint256(BigInteger.ONE));
        byte[] encoded = TypeEncoder.encode(s);
        assertTrue(encoded.length > 0);
        assertEquals(0, encoded.length % 32);
    }

    // ----------------------------------------------------------------------
    // Function type accessors / constructors
    // ----------------------------------------------------------------------

    @Test
    public void testFunctionDefaultConstructorAndSetters() {
        Function fn = new Function();
        assertEquals("", fn.getName());
        assertTrue(fn.getInputParameters().isEmpty());
        assertTrue(fn.getOutputParameters().isEmpty());
        assertEquals(0, fn.getTransactionAttribute());

        fn.setTransactionAttribute(3);
        assertEquals(3, fn.getTransactionAttribute());
        fn.setValue(BigInteger.valueOf(99));
        assertEquals(BigInteger.valueOf(99), fn.getValue());
        fn.setNonce("0xabc");
        assertEquals("0xabc", fn.getNonce());
        fn.setBlockLimit(BigInteger.valueOf(500));
        assertEquals(BigInteger.valueOf(500), fn.getBlockLimit());
    }

    @Test
    public void testFunctionWithAttributeValueNonceBlockLimit() {
        List<Type> inputs = new ArrayList<>();
        inputs.add(new Uint256(BigInteger.ONE));
        List<TypeReference<?>> outputs = new ArrayList<>();
        outputs.add(TypeReference.create(Uint256.class));

        Function fn =
                new Function(
                        "f",
                        inputs,
                        outputs,
                        1,
                        BigInteger.valueOf(10),
                        "0x01",
                        BigInteger.valueOf(20));
        assertEquals("f", fn.getName());
        assertEquals(1, fn.getInputParameters().size());
        assertEquals(1, fn.getOutputParameters().size());
        assertEquals(1, fn.getTransactionAttribute());
        assertEquals(BigInteger.valueOf(10), fn.getValue());
        assertEquals("0x01", fn.getNonce());
        assertEquals(BigInteger.valueOf(20), fn.getBlockLimit());
    }

    // ----------------------------------------------------------------------
    // EventValues + EventEncoder indexed / non-indexed handling
    // ----------------------------------------------------------------------

    @Test
    public void testEventValuesHolder() {
        List<Type> indexed = new ArrayList<>();
        indexed.add(new Uint256(BigInteger.ONE));
        List<Type> nonIndexed = new ArrayList<>();
        nonIndexed.add(new Bool(true));
        EventValues ev = new EventValues(indexed, nonIndexed);
        assertEquals(1, ev.getIndexedValues().size());
        assertEquals(1, ev.getNonIndexedValues().size());
        assertEquals(BigInteger.ONE, ev.getIndexedValues().get(0).getValue());
        assertEquals(Boolean.TRUE, ev.getNonIndexedValues().get(0).getValue());
    }

    @Test
    public void testEventEncoderDeprecatedConstructorAndDynamicTopic() {
        List<TypeReference<?>> params = new ArrayList<>();
        params.add(TypeReference.create(Utf8String.class));
        params.add(TypeReference.create(DynamicBytes.class));
        Event event = new Event("Logged", params);

        EventEncoder encoder = new EventEncoder(cryptoSuite());
        String topic = encoder.encode(event);
        assertTrue(topic.startsWith("0x"));
        assertEquals("Logged(string,bytes)", encoder.buildMethodSignature("Logged", event.getParameters()));
        assertEquals(topic, encoder.buildEventSignature("Logged(string,bytes)"));
    }

    @Test
    public void testEncoderHelperGettersSetters() {
        Encoder encoder = new Encoder(cryptoSuite().getHashImpl());
        assertNotNull(encoder.getHashImpl());
        encoder.setHashImpl(cryptoSuite().getHashImpl());
        assertNotNull(encoder.getHashImpl());
    }

    // ----------------------------------------------------------------------
    // Utils helpers
    // ----------------------------------------------------------------------

    @Test
    public void testUtilsGetSimpleTypeAndMethodSign() {
        assertEquals("uint256", Utils.getSimpleMethodSign(Uint.class));
        assertEquals("int256", Utils.getSimpleMethodSign(Int.class));
        assertEquals("string", Utils.getSimpleMethodSign(Utf8String.class));
        assertEquals("bytes", Utils.getSimpleMethodSign(DynamicBytes.class));
        assertEquals("bool", Utils.getSimpleMethodSign(Bool.class));

        assertEquals("uint256", Utils.getSimpleTypeName(Uint.class));
        assertEquals("string", Utils.getSimpleTypeName(Utf8String.class));
        assertEquals("bytes", Utils.getSimpleTypeName(DynamicBytes.class));
    }

    @Test
    public void testUtilsConvertAndGetMethodSignViaReference() {
        List<TypeReference<?>> refs = new ArrayList<>();
        refs.add(TypeReference.create(Uint256.class));
        refs.add(TypeReference.create(Address.class));
        List<TypeReference<Type>> converted = Utils.convert(refs);
        assertEquals(2, converted.size());
        assertEquals("uint256", Utils.getMethodSign(converted.get(0)));
        assertEquals("address", Utils.getTypeName(converted.get(1)));
    }

    @Test
    public void testUtilsGetMethodSignForDynamicArray() {
        TypeReference<DynamicArray<Uint256>> ref =
                new TypeReference<DynamicArray<Uint256>>() {};
        assertEquals("uint256[]", Utils.getMethodSign(ref));
        assertEquals("uint256[]", Utils.getTypeName(ref));
    }

    // ----------------------------------------------------------------------
    // ABIObjectFactory raw / fixed / ufixed throwing branches
    // ----------------------------------------------------------------------

    @Test
    public void testBuildRawTypeObjectVariants() {
        assertEquals(ABIObject.ValueType.UINT, ABIObjectFactory.buildRawTypeObject("uint128").getValueType());
        assertEquals(128, ABIObjectFactory.buildRawTypeObject("uint128").getBytesLength());
        assertEquals(ABIObject.ValueType.INT, ABIObjectFactory.buildRawTypeObject("int64").getValueType());
        assertEquals(ABIObject.ValueType.DBYTES, ABIObjectFactory.buildRawTypeObject("bytes").getValueType());
        assertEquals(ABIObject.ValueType.BYTES, ABIObjectFactory.buildRawTypeObject("bytes16").getValueType());
        assertEquals(16, ABIObjectFactory.buildRawTypeObject("bytes16").getBytesLength());
        assertEquals(ABIObject.ValueType.ADDRESS, ABIObjectFactory.buildRawTypeObject("address").getValueType());
    }

    @Test
    public void testBuildRawTypeObjectFixedThrows() {
        try {
            ABIObjectFactory.buildRawTypeObject("fixed128x18");
            Assert.fail("expected UnsupportedOperationException for fixed");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testBuildRawTypeObjectUnrecognizedThrows() {
        try {
            ABIObjectFactory.buildRawTypeObject("notatype");
            Assert.fail("expected UnsupportedOperationException for unknown type");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    // ----------------------------------------------------------------------
    // ABIObject deep-copy / newObject / newObjectWithoutValue / toString / setters
    // ----------------------------------------------------------------------

    @Test
    public void testAbiObjectNewObjectDeepCopyValueTypes() {
        ABIObject uintObj = new ABIObject(new Uint256(BigInteger.valueOf(123)));
        ABIObject clone = uintObj.newObject();
        assertNotNull(clone.getNumericValue());
        assertEquals(BigInteger.valueOf(123), clone.getNumericValue().getValue());

        ABIObject boolObj = new ABIObject(new Bool(true));
        assertTrue(boolObj.newObject().getBoolValue().getValue());

        ABIObject strObj = new ABIObject(new Utf8String("copy"));
        assertEquals("copy", strObj.newObject().getStringValue().getValue());

        ABIObject dbObj = new ABIObject(new DynamicBytes(new byte[] {1, 2, 3}));
        assertArrayEquals(new byte[] {1, 2, 3}, dbObj.newObject().getDynamicBytesValue().getValue());

        ABIObject addrObj = new ABIObject(new Address(BigInteger.valueOf(0x9)));
        assertEquals(addrObj.getAddressValue(), addrObj.newObject().getAddressValue());

        ABIObject bytesObj = new ABIObject(new Bytes(3, new byte[] {7, 8, 9}));
        assertArrayEquals(new byte[] {7, 8, 9}, bytesObj.newObject().getBytesValue().getValue());

        ABIObject intObj = new ABIObject(new Int256(BigInteger.valueOf(-5)));
        assertEquals(BigInteger.valueOf(-5), intObj.newObject().getNumericValue().getValue());
    }

    @Test
    public void testAbiObjectNewObjectStructAndList() {
        ABIObject struct = new ABIObject(ABIObject.ObjectType.STRUCT);
        struct.getStructFields().add(new ABIObject(new Uint256(BigInteger.ONE)));
        ABIObject list = new ABIObject(ABIObject.ListType.DYNAMIC);
        list.getListValues().add(new ABIObject(new Uint256(BigInteger.TEN)));
        struct.getStructFields().add(list);

        ABIObject cloned = struct.newObject();
        assertEquals(2, cloned.getStructFields().size());
        assertEquals(
                BigInteger.ONE, cloned.getStructFields().get(0).getNumericValue().getValue());
        assertEquals(
                BigInteger.TEN,
                cloned.getStructFields().get(1).getListValues().get(0).getNumericValue().getValue());

        ABIObject withoutValue = struct.newObjectWithoutValue();
        assertEquals(ABIObject.ObjectType.STRUCT, withoutValue.getType());
        assertEquals(2, withoutValue.getStructFields().size());
    }

    @Test
    public void testAbiObjectListValueTypeClone() {
        ABIObject fixedList = new ABIObject(ABIObject.ListType.FIXED);
        fixedList.setListLength(2);
        fixedList.setListValueType(new ABIObject(ABIObject.ValueType.UINT, 256));
        ABIObject clone = fixedList.newObject();
        assertEquals(2, clone.getListLength());
        assertNotNull(clone.getListValueType());
        assertEquals(ABIObject.ListType.FIXED, clone.getListType());

        ABIObject cloneNoValue = fixedList.newObjectWithoutValue();
        assertNotNull(cloneNoValue.getListValueType());
        assertEquals(2, cloneNoValue.getListLength());
    }

    @Test
    public void testAbiObjectSettersDriveTypeAndValueType() {
        ABIObject obj = new ABIObject(ABIObject.ObjectType.VALUE);
        obj.setBoolValue(new Bool(true));
        assertEquals(ABIObject.ValueType.BOOL, obj.getValueType());

        obj.setNumericValue(new Uint256(BigInteger.ONE));
        assertEquals(ABIObject.ValueType.UINT, obj.getValueType());

        obj.setNumericValue(new Int256(BigInteger.valueOf(-3)));
        assertEquals(ABIObject.ValueType.INT, obj.getValueType());

        obj.setAddressValue(new Address(BigInteger.ONE));
        assertEquals(ABIObject.ValueType.ADDRESS, obj.getValueType());

        obj.setBytesValue(new Bytes(2, new byte[] {1, 2}));
        assertEquals(ABIObject.ValueType.BYTES, obj.getValueType());

        obj.setName("field");
        assertEquals("field", obj.getName());
    }

    @Test
    public void testAbiObjectToStringForEachValueType() {
        assertTrue(new ABIObject(new Bool(true)).toString().contains("valueType=BOOL"));
        assertTrue(new ABIObject(new Uint256(BigInteger.TEN)).toString().contains("numericValue"));
        assertTrue(
                new ABIObject(new Address(BigInteger.ONE)).toString().contains("addressValue"));
        assertTrue(
                new ABIObject(new Bytes(1, new byte[] {1})).toString().contains("bytesValue"));
        assertTrue(
                new ABIObject(new DynamicBytes(new byte[] {1}))
                        .toString()
                        .contains("dynamicBytesValue"));
        assertTrue(new ABIObject(new Utf8String("xyz")).toString().contains("stringValue"));

        ABIObject list = new ABIObject(ABIObject.ListType.DYNAMIC);
        assertTrue(list.toString().contains("listType"));

        ABIObject struct = new ABIObject(ABIObject.ObjectType.STRUCT);
        assertTrue(struct.toString().contains("structFields"));
    }

    @Test
    public void testAbiObjectToStringWithNullValues() {
        // VALUE object with valueType set but inner value null -> "null" branches in toString.
        ABIObject obj = new ABIObject(ABIObject.ValueType.BOOL);
        assertTrue(obj.toString().contains("null"));
        ABIObject uintObj = new ABIObject(ABIObject.ValueType.UINT);
        assertTrue(uintObj.toString().contains("null"));
    }

    @Test
    public void testAbiObjectOffsetAndDynamic() {
        ABIObject staticStruct = new ABIObject(ABIObject.ObjectType.STRUCT);
        staticStruct.getStructFields().add(new ABIObject(new Uint256(BigInteger.ONE)));
        staticStruct.getStructFields().add(new ABIObject(new Bool(false)));
        assertFalse(staticStruct.isDynamic());
        assertEquals(2, staticStruct.offset());
        assertEquals(2 * Type.MAX_BYTE_LENGTH, staticStruct.offsetAsByteLength());

        ABIObject dynamicStruct = new ABIObject(ABIObject.ObjectType.STRUCT);
        dynamicStruct.getStructFields().add(new ABIObject(new Utf8String("x")));
        assertTrue(dynamicStruct.isDynamic());
        assertEquals(1, dynamicStruct.offset());

        ABIObject fixedListStatic = new ABIObject(ABIObject.ListType.FIXED);
        fixedListStatic.setListLength(3);
        fixedListStatic.setListValueType(new ABIObject(ABIObject.ValueType.UINT, 256));
        assertFalse(fixedListStatic.isDynamic());
        assertEquals(3, fixedListStatic.offset());
    }

    // ----------------------------------------------------------------------
    // ContractABIDefinition lookup paths
    // ----------------------------------------------------------------------

    @Test
    public void testContractABIDefinitionEventTopicLookup() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(EVENT_ABI);
        assertEquals(1, def.getEvents().size());
        ABIDefinition eventDef = def.getEvents().get("Transfer").get(0);
        String topic = eventDef.getMethodSignatureAsString();
        assertEquals("Transfer(address,address,uint256)", topic);
        EventEncoder encoder = new EventEncoder(cryptoSuite().getHashImpl());
        String eventTopic = encoder.buildEventSignature(topic);
        assertTrue(eventTopic.startsWith("0x"));
        assertEquals(66, eventTopic.length());
    }

    @Test
    public void testContractABIDefinitionMethodIdMissingReturnsNull() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(SIMPLE_ABI);
        assertNull(def.getABIDefinitionByMethodId(new byte[] {0, 0, 0, 0}));
        assertNotNull(def.toString());
    }

    private static final String EVENT_ABI =
            "[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"}]";

    // ----------------------------------------------------------------------
    // ContractCodecTools value conversion edge cases
    // ----------------------------------------------------------------------

    @Test
    public void testContractCodecToolsDecodeStringNumberToUint() {
        ABIObject template = new ABIObject(ABIObject.ValueType.UINT, 256);
        ABIObject decoded = ContractCodecTools.decodeABIObjectValue(template, "12345");
        assertEquals(BigInteger.valueOf(12345), decoded.getNumericValue().getValue());
    }

    @Test
    public void testContractCodecToolsDecodeBigIntegerToInt() {
        ABIObject template = new ABIObject(ABIObject.ValueType.INT, 256);
        ABIObject decoded = ContractCodecTools.decodeABIObjectValue(template, BigInteger.valueOf(-7));
        assertEquals(BigInteger.valueOf(-7), decoded.getNumericValue().getValue());
    }

    @Test
    public void testContractCodecToolsDecodeBoolFromBool() {
        ABIObject template = new ABIObject(ABIObject.ValueType.BOOL);
        ABIObject decoded = ContractCodecTools.decodeABIObjectValue(template, new Bool(true));
        assertTrue(decoded.getBoolValue().getValue());
    }

    @Test
    public void testContractCodecToolsDecodeBytesFromRaw() {
        ABIObject template = new ABIObject(ABIObject.ValueType.BYTES, 3);
        ABIObject decoded = ContractCodecTools.decodeABIObjectValue(template, new byte[] {1, 2, 3});
        assertArrayEquals(new byte[] {1, 2, 3}, decoded.getBytesValue().getValue());
    }

    @Test
    public void testContractCodecToolsDecodeDynamicBytesFromTyped() {
        ABIObject template = new ABIObject(ABIObject.ValueType.DBYTES);
        ABIObject decoded =
                ContractCodecTools.decodeABIObjectValue(template, new DynamicBytes(new byte[] {9}));
        assertArrayEquals(new byte[] {9}, decoded.getDynamicBytesValue().getValue());
    }

    @Test
    public void testContractCodecToolsDecodeAddressFromString() {
        ABIObject template = new ABIObject(ABIObject.ValueType.ADDRESS);
        ABIObject decoded =
                ContractCodecTools.decodeABIObjectValue(
                        template, "0x0000000000000000000000000000000000000abc");
        assertEquals(new Address(BigInteger.valueOf(0xabc)), decoded.getAddressValue());
    }

    @Test
    public void testContractCodecToolsValueTypeMismatchThrows() {
        ABIObject template = new ABIObject(ABIObject.ValueType.ADDRESS);
        try {
            ContractCodecTools.decodeABIObjectValue(template, new Bool(true));
            Assert.fail("expected InvalidParameterException for type mismatch");
        } catch (InvalidParameterException expected) {
            // ok
        }
    }

    @Test
    public void testContractCodecToolsFormatBytesNTruncates() {
        ABIObject template = new ABIObject(new Bytes(4, new byte[] {1, 2, 3, 4}), 2);
        // formatBytesN with bytesLength 2 and value length 4 -> truncates to first 2.
        byte[] formatted = ContractCodecTools.formatBytesN(template);
        assertArrayEquals(new byte[] {1, 2}, formatted);
    }

    @Test
    public void testContractCodecToolsGetTypeListResultEmptyForValue() {
        // a plain VALUE object yields an empty type-list result (not struct/list)
        ABIObject value = new ABIObject(new Uint256(BigInteger.ONE));
        List<Type> result = ContractCodecTools.getABIObjectTypeListResult(value);
        assertTrue(result.isEmpty());
    }

    // ----------------------------------------------------------------------
    // ContractCodecJsonWrapper error paths and tryDecodeInputData
    // ----------------------------------------------------------------------

    @Test
    public void testJsonWrapperArgumentSizeMismatchThrows() {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(SIMPLE_ABI);
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(def.getFunctions().get("setAll").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> tooFew = new ArrayList<>();
        tooFew.add("1");
        try {
            wrapper.encode(inputObject, tooFew);
            Assert.fail("expected InvalidParameterException for arg size mismatch");
        } catch (InvalidParameterException expected) {
            // ok
        } catch (Exception other) {
            Assert.fail("unexpected exception: " + other);
        }
    }

    @Test
    public void testJsonWrapperTryDecodeInputData() {
        // valid hex -> returns the decoded bytes
        byte[] payload = new byte[4 + 32];
        byte[] decoded = ContractCodecJsonWrapper.tryDecodeInputData(Hex.toHexString(payload));
        assertNotNull(decoded);
        assertEquals(payload.length, decoded.length);

        // empty input decodes to a zero-length array -> null
        assertNull(ContractCodecJsonWrapper.tryDecodeInputData(""));

        // odd-length / non-hex input -> DecoderException caught -> null
        assertNull(ContractCodecJsonWrapper.tryDecodeInputData("zzz"));
    }

    @Test
    public void testJsonWrapperHexEncodedRoundTrip() throws Exception {
        ContractABIDefinition def = TestUtils.getContractABIDefinition(SIMPLE_ABI);
        ABIObject inputObject =
                ABIObjectFactory.createInputObject(def.getFunctions().get("setAll").get(0));
        ContractCodecJsonWrapper wrapper = new ContractCodecJsonWrapper();
        List<String> args = new ArrayList<>();
        args.add("42");
        args.add("true");
        args.add("hello json");
        args.add("0x0000000000000000000000000000000000000001");
        ABIObject encoded = wrapper.encode(inputObject, args);

        ABIObject decodeTemplate =
                ABIObjectFactory.createInputObject(def.getFunctions().get("setAll").get(0));
        List<String> decoded = wrapper.decode(decodeTemplate, encoded.encode(false), false);
        assertEquals(4, decoded.size());
        assertEquals("42", decoded.get(0));
        assertEquals("true", decoded.get(1));
        assertEquals("hello json", decoded.get(2));
    }

    // ----------------------------------------------------------------------
    // ContractCodec error paths
    // ----------------------------------------------------------------------

    @Test
    public void testContractCodecEncodeMethodUnknownNameThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        try {
            codec.encodeMethod(SIMPLE_ABI, "doesNotExist", new ArrayList<>());
            Assert.fail("expected ContractCodecException for unknown method");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testContractCodecEncodeMethodWrongArgCountThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> args = new ArrayList<>();
        args.add(BigInteger.ONE); // setAll requires 4 args
        try {
            codec.encodeMethod(SIMPLE_ABI, "setAll", args);
            Assert.fail("expected ContractCodecException for wrong arg count");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testContractCodecEncodeMethodByIdUnknownThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        try {
            codec.encodeMethodById(SIMPLE_ABI, new byte[] {1, 2, 3, 4}, new ArrayList<>());
            Assert.fail("expected ContractCodecException for unknown methodId");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testContractCodecDecodeMethodInputToStringUnknownThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        try {
            codec.decodeMethodInputToString(SIMPLE_ABI, "nope", new byte[8]);
            Assert.fail("expected ContractCodecException for unknown method");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testContractCodecAccessors() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        assertFalse(codec.isWasm());
        assertNotNull(codec.getCryptoSuite());
        assertNotNull(codec.getAbiDefinitionFactory());
        assertNotNull(codec.getFunctionEncoder());

        ContractCodec wasmCodec = new ContractCodec(cryptoSuite().getHashImpl(), true);
        assertTrue(wasmCodec.isWasm());
        assertNotNull(wasmCodec.getFunctionEncoder());
    }

    @Test
    public void testContractCodecEncodeMethodByInterfaceWrongArgsThrows() {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> args = new ArrayList<>(); // signature wants 1 uint256
        try {
            codec.encodeMethodByInterface("foo(uint256)", args);
            Assert.fail("expected ContractCodecException for arg mismatch");
        } catch (ContractCodecException expected) {
            // ok
        }
    }

    @Test
    public void testContractCodecStaticStructEncode() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> tupleArg = new ArrayList<>();
        List<Object> fields = new ArrayList<>();
        fields.add(BigInteger.valueOf(11));
        fields.add(BigInteger.valueOf(22));
        tupleArg.add(fields);

        byte[] encoded = codec.encodeMethod(STRUCT_ABI, "useStatic", tupleArg);
        assertTrue(encoded.length > 4);
    }

    @Test
    public void testContractCodecDynamicStructEncodeDecodeRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> tupleArg = new ArrayList<>();
        List<Object> fields = new ArrayList<>();
        fields.add("dyn struct string");
        fields.add(BigInteger.valueOf(77));
        tupleArg.add(fields);

        byte[] encoded = codec.encodeMethod(STRUCT_ABI, "useDynamic", tupleArg);
        assertTrue(encoded.length > 4);

        List<String> decoded = codec.decodeMethodInputToString(STRUCT_ABI, "useDynamic", encoded);
        assertEquals(1, decoded.size());
        assertTrue(decoded.get(0).contains("dyn struct string"));
    }

    // ----------------------------------------------------------------------
    // abi TypeEncoder / TypeDecoder less-common types
    // ----------------------------------------------------------------------

    @Test
    public void testAbiBytes32RoundTrip() throws Exception {
        byte[] raw = new byte[32];
        for (int i = 0; i < 32; i++) {
            raw[i] = (byte) (i + 1);
        }
        Bytes32 value = new Bytes32(raw);
        byte[] encoded = TypeEncoder.encode(value);
        assertEquals(32, encoded.length);
        Bytes32 decoded = TypeDecoder.decodeBytes(encoded, 0, Bytes32.class);
        assertArrayEquals(raw, decoded.getValue());
    }

    @Test
    public void testAbiDecodeAddressHelper() {
        Address addr = new Address("0x00000000000000000000000000000000000000aa");
        byte[] encoded = TypeEncoder.encode(addr);
        Address decoded = TypeDecoder.decodeAddress(encoded);
        assertEquals(addr, decoded);
    }

    @Test
    public void testAbiNestedDynamicArrayOfStringRoundTrip() throws Exception {
        List<Utf8String> list =
                Arrays.asList(new Utf8String("aa"), new Utf8String("bb"), new Utf8String("cc"));
        DynamicArray<Utf8String> value = new DynamicArray<>(Utf8String.class, list);
        byte[] encoded = TypeEncoder.encode(value);
        DynamicArray<Utf8String> decoded =
                TypeDecoder.decodeDynamicArray(
                        encoded, 0, new TypeReference<DynamicArray<Utf8String>>() {});
        assertEquals(3, decoded.getValue().size());
        assertEquals("cc", decoded.getValue().get(2).getValue());
    }

    @Test
    public void testAbiNegativeNumericEncoding() {
        Int256 negative = new Int256(BigInteger.valueOf(-1));
        byte[] encoded = TypeEncoder.encode(negative);
        // two's complement -1 -> all 0xff
        for (byte b : encoded) {
            assertEquals((byte) 0xff, b);
        }
    }

    // ----------------------------------------------------------------------
    // abi FunctionReturnDecoder edge cases
    // ----------------------------------------------------------------------

    @Test
    public void testFunctionReturnDecoderIndexedDynamicAsHash() {
        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        // a string topic is hashed -> indexed decode returns a Bytes32
        byte[] data = new byte[32];
        for (int i = 0; i < 32; i++) {
            data[i] = (byte) i;
        }
        Type decoded =
                decoder.decodeIndexedValue(
                        Hex.toHexString(data), new TypeReference<Utf8String>() {});
        assertTrue(decoded instanceof Bytes32);
        assertArrayEquals(data, (byte[]) decoded.getValue());
    }

    @Test
    public void testFunctionReturnDecoderIndexedStaticBytes() {
        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        byte[] data = new byte[32];
        data[0] = 0x11;
        data[1] = 0x22;
        data[2] = 0x33;
        data[3] = 0x44;
        Type decoded =
                decoder.decodeIndexedValue(Hex.toHexString(data), TypeReference.create(Bytes4.class));
        assertTrue(decoded instanceof Bytes4);
        assertArrayEquals(new byte[] {0x11, 0x22, 0x33, 0x44}, (byte[]) decoded.getValue());
    }

    @Test
    public void testFunctionReturnDecoderMultiTypeBuild() {
        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        Uint256 u = new Uint256(BigInteger.valueOf(5));
        Bool b = new Bool(true);
        byte[] encU = TypeEncoder.encode(u);
        byte[] encB = TypeEncoder.encode(b);
        byte[] combined = new byte[encU.length + encB.length];
        System.arraycopy(encU, 0, combined, 0, encU.length);
        System.arraycopy(encB, 0, combined, encU.length, encB.length);

        List<TypeReference<?>> refs = new ArrayList<>();
        refs.add(TypeReference.create(Uint256.class));
        refs.add(TypeReference.create(Bool.class));
        List<Type> decoded = decoder.decode(Hex.toHexString(combined), Utils.convert(refs));
        assertEquals(2, decoded.size());
        assertEquals(BigInteger.valueOf(5), decoded.get(0).getValue());
        assertEquals(Boolean.TRUE, decoded.get(1).getValue());
    }

    @Test
    public void testFunctionReturnDecoderNullInputEmpty() {
        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        List<TypeReference<Type>> empty = new ArrayList<>();
        assertTrue(decoder.decode("0x", empty).isEmpty());
    }

    // ----------------------------------------------------------------------
    // SCALE backend struct / array via ContractCodec
    // ----------------------------------------------------------------------

    @Test
    public void testContractCodecScaleStaticStructRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), true);
        List<Object> tupleArg = new ArrayList<>();
        List<Object> fields = new ArrayList<>();
        fields.add(BigInteger.valueOf(3));
        fields.add(BigInteger.valueOf(4));
        tupleArg.add(fields);

        byte[] encoded = codec.encodeMethod(STRUCT_ABI, "useStatic", tupleArg);
        assertNotNull(encoded);
        List<String> decoded = codec.decodeMethodInputToString(STRUCT_ABI, "useStatic", encoded);
        assertEquals(1, decoded.size());
    }

    @Test
    public void testScaleBytes32EncodeRoundTrip() throws Exception {
        byte[] raw = new byte[32];
        Arrays.fill(raw, (byte) 0x7);
        Bytes32 value = new Bytes32(raw);
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        assertEquals(32, encoded.length);
        Bytes32 decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(Bytes32.class));
        assertArrayEquals(raw, decoded.getValue());
    }
}
