package org.fisco.bcos.sdk.v3.test.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.EventEncoder;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder;
import org.fisco.bcos.sdk.v3.codec.abi.TypeEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Event;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Int;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Uint;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int128;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int64;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint16;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint160;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint64;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecWriter;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Round-trip and direct unit-test coverage for the codec stack: datatypes, ABI TypeEncoder /
 * TypeDecoder / FunctionEncoder / FunctionReturnDecoder / EventEncoder, SCALE TypeEncoder /
 * TypeDecoder / ScaleCodecReader / ScaleCodecWriter, and the high-level ContractCodec (ABI &
 * SCALE).
 */
public class CodecRoundTripCoverageTest {

    private CryptoSuite cryptoSuite() {
        return TestUtils.getCryptoSuite();
    }

    // -------------------------------------------------------------------------
    // datatypes: direct construction / getters / equals / hashCode / toString
    // -------------------------------------------------------------------------

    @Test
    public void testUintBasics() {
        Uint a = new Uint(BigInteger.valueOf(255));
        assertEquals(BigInteger.valueOf(255), a.getValue());
        assertEquals("uint256", a.getTypeAsString());
        assertEquals(256, a.getBitSize());
        Uint b = new Uint(BigInteger.valueOf(255));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.validUint(BigInteger.TEN));
        assertFalse(a.validUint(BigInteger.valueOf(-1)));
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "not a uint");
    }

    @Test
    public void testIntBasics() {
        Int neg = new Int(BigInteger.valueOf(-42));
        assertEquals(BigInteger.valueOf(-42), neg.getValue());
        assertEquals("int256", neg.getTypeAsString());
        assertTrue(neg.validInt(BigInteger.valueOf(-100)));
        Int neg2 = new Int(BigInteger.valueOf(-42));
        assertEquals(neg, neg2);
        assertEquals(neg.hashCode(), neg2.hashCode());
        assertNotEquals(neg, new Int(BigInteger.ONE));
    }

    @Test
    public void testGeneratedSizedIntegers() {
        Uint8 u8 = new Uint8(7L);
        assertEquals("uint8", u8.getTypeAsString());
        assertEquals(8, u8.getBitSize());
        assertEquals(BigInteger.valueOf(7), u8.getValue());

        Uint16 u16 = new Uint16(300L);
        assertEquals("uint16", u16.getTypeAsString());

        Uint64 u64 = new Uint64(BigInteger.valueOf(123456789L));
        assertEquals("uint64", u64.getTypeAsString());

        Int8 i8 = new Int8(-3L);
        assertEquals("int8", i8.getTypeAsString());
        assertEquals(BigInteger.valueOf(-3), i8.getValue());

        Int64 i64 = new Int64(-1234L);
        assertEquals("int64", i64.getTypeAsString());

        Int128 i128 = new Int128(BigInteger.valueOf(-99));
        assertEquals("int128", i128.getTypeAsString());
    }

    @Test
    public void testBoolBasics() {
        Bool t = new Bool(true);
        Bool f = new Bool(Boolean.FALSE);
        assertEquals("bool", t.getTypeAsString());
        assertTrue(t.getValue());
        assertFalse(f.getValue());
        assertNotEquals(t, f);
        assertEquals(t, new Bool(true));
        assertEquals(t.hashCode(), new Bool(true).hashCode());
        assertEquals(0, f.hashCode());
        assertEquals(1, t.hashCode());
        assertNotEquals(t, null);
        assertNotEquals(t, "true");
    }

    @Test
    public void testAddressBasics() {
        Address fromHex = new Address("0x0000000000000000000000000000000000000abc");
        Address fromBig = new Address(BigInteger.valueOf(0xabc));
        assertEquals(fromHex, fromBig);
        assertEquals(fromHex.hashCode(), fromBig.hashCode());
        assertEquals("address", fromHex.getTypeAsString());
        assertNotNull(fromHex.getValue());
        assertEquals(fromHex.getValue(), fromHex.toString());
        assertNotNull(fromHex.toUint160());
        Address other = new Address(BigInteger.ONE);
        assertNotEquals(fromHex, other);
        assertNotEquals(fromHex, null);
    }

    @Test
    public void testStaticBytesBasics() {
        byte[] raw = new byte[] {1, 2, 3, 4};
        Bytes4 b4 = new Bytes4(raw);
        assertEquals("bytes4", b4.getTypeAsString());
        assertArrayEquals(raw, b4.getValue());
        assertEquals(32, b4.bytes32PaddedLength());
        Bytes4 same = new Bytes4(new byte[] {1, 2, 3, 4});
        assertEquals(b4, same);
        assertEquals(b4.hashCode(), same.hashCode());
        assertNotEquals(b4, new Bytes4(new byte[] {9, 9, 9, 9}));

        Bytes1 b1 = new Bytes1(new byte[] {(byte) 0xff});
        assertEquals("bytes1", b1.getTypeAsString());

        Bytes generic = new Bytes(2, new byte[] {7, 8});
        assertEquals("bytes2", generic.getTypeAsString());
    }

    @Test
    public void testStaticBytesInvalidLengthThrows() {
        try {
            new Bytes(4, new byte[] {1, 2, 3});
            Assert.fail("expected UnsupportedOperationException for length mismatch");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    @Test
    public void testDynamicBytesBasics() {
        byte[] raw = "hello".getBytes(StandardCharsets.UTF_8);
        DynamicBytes db = new DynamicBytes(raw);
        assertEquals("bytes", db.getTypeAsString());
        assertArrayEquals(raw, db.getValue());
        assertEquals(32, db.bytes32PaddedLength());
        assertEquals(db, new DynamicBytes(raw.clone()));
        DynamicBytes empty = new DynamicBytes(new byte[] {});
        assertEquals(32, empty.bytes32PaddedLength());
    }

    @Test
    public void testUtf8StringBasics() {
        Utf8String s = new Utf8String("hi there");
        assertEquals("string", s.getTypeAsString());
        assertEquals("hi there", s.getValue());
        assertEquals("hi there", s.toString());
        assertEquals(s, new Utf8String("hi there"));
        assertEquals(s.hashCode(), new Utf8String("hi there").hashCode());
        assertNotEquals(s, new Utf8String("other"));
        assertEquals(64, s.bytes32PaddedLength());
        assertEquals(32, new Utf8String("").bytes32PaddedLength());
    }

    @Test
    public void testDynamicArrayBasics() {
        List<Uint256> values = new ArrayList<>();
        values.add(new Uint256(BigInteger.ONE));
        values.add(new Uint256(BigInteger.TEN));
        DynamicArray<Uint256> arr = new DynamicArray<>(Uint256.class, values);
        assertEquals("uint256[]", arr.getTypeAsString());
        assertEquals(2, arr.getValue().size());
        assertEquals(Uint256.class, arr.getComponentType());
        assertFalse(arr.isFixed());
        arr.setFixed(true);
        assertTrue(arr.isFixed());
    }

    @Test
    public void testStaticArrayBasics() {
        StaticArray2<Uint256> arr =
                new StaticArray2<>(
                        Uint256.class,
                        Arrays.asList(new Uint256(BigInteger.ONE), new Uint256(BigInteger.TEN)));
        assertEquals("uint256[2]", arr.getTypeAsString());
        assertEquals(2, arr.getValue().size());
        assertEquals(Uint256.class, arr.getComponentType());
    }

    @Test
    public void testStaticArrayWrongSizeThrows() {
        try {
            new StaticArray3<>(Uint256.class, Arrays.asList(new Uint256(BigInteger.ONE)));
            Assert.fail("expected UnsupportedOperationException for size mismatch");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    // -------------------------------------------------------------------------
    // ABI TypeEncoder / TypeDecoder round trips
    // -------------------------------------------------------------------------

    @Test
    public void testAbiUint256RoundTrip() throws Exception {
        Uint256 value = new Uint256(BigInteger.valueOf(123456789L));
        byte[] encoded = TypeEncoder.encode(value);
        assertEquals(32, encoded.length);
        Uint256 decoded = TypeDecoder.decode(encoded, 0, TypeReference.create(Uint256.class));
        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testAbiUint256MaxRoundTrip() throws Exception {
        BigInteger big = BigInteger.ONE.shiftLeft(255).add(BigInteger.valueOf(7));
        Uint256 value = new Uint256(big);
        byte[] encoded = TypeEncoder.encode(value);
        Uint256 decoded = TypeDecoder.decode(encoded, 0, TypeReference.create(Uint256.class));
        assertEquals(big, decoded.getValue());
    }

    @Test
    public void testAbiInt256NegativeRoundTrip() throws Exception {
        Int256 value = new Int256(BigInteger.valueOf(-987654321L));
        byte[] encoded = TypeEncoder.encode(value);
        assertEquals(32, encoded.length);
        Int256 decoded = TypeDecoder.decode(encoded, 0, TypeReference.create(Int256.class));
        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testAbiBoolRoundTrip() throws Exception {
        byte[] encodedTrue = TypeEncoder.encode(new Bool(true));
        byte[] encodedFalse = TypeEncoder.encode(new Bool(false));
        assertEquals(BigInteger.ONE.intValue(), encodedTrue[31]);
        assertEquals(0, encodedFalse[31]);
        assertTrue(TypeDecoder.decodeBool(encodedTrue, 0).getValue());
        assertFalse(TypeDecoder.decodeBool(encodedFalse, 0).getValue());
    }

    @Test
    public void testAbiAddressRoundTrip() throws Exception {
        Address addr = new Address("0x00000000000000000000000000000000000000ff");
        byte[] encoded = TypeEncoder.encode(addr);
        assertEquals(32, encoded.length);
        Address decoded = TypeDecoder.decode(encoded, 0, TypeReference.create(Address.class));
        assertEquals(addr, decoded);
    }

    @Test
    public void testAbiStaticBytesRoundTrip() throws Exception {
        Bytes4 value = new Bytes4(new byte[] {0x11, 0x22, 0x33, 0x44});
        byte[] encoded = TypeEncoder.encode(value);
        assertEquals(32, encoded.length);
        Bytes4 decoded = TypeDecoder.decodeBytes(encoded, Bytes4.class);
        assertArrayEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testAbiDynamicBytesRoundTrip() throws Exception {
        byte[] raw = "the quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.UTF_8);
        DynamicBytes value = new DynamicBytes(raw);
        byte[] encoded = TypeEncoder.encode(value);
        DynamicBytes decoded = TypeDecoder.decodeDynamicBytes(encoded, 0);
        assertArrayEquals(raw, decoded.getValue());
    }

    @Test
    public void testAbiStringRoundTrip() throws Exception {
        Utf8String value = new Utf8String("Hello, codec round-trip!");
        byte[] encoded = TypeEncoder.encode(value);
        Utf8String decoded = TypeDecoder.decodeUtf8String(encoded, 0);
        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testAbiStaticArrayRoundTrip() throws Exception {
        StaticArray2<Uint256> value =
                new StaticArray2<>(
                        Uint256.class,
                        Arrays.asList(
                                new Uint256(BigInteger.valueOf(11)),
                                new Uint256(BigInteger.valueOf(22))));
        byte[] encoded = TypeEncoder.encode(value);
        assertEquals(64, encoded.length);
        StaticArray2<Uint256> decoded =
                TypeDecoder.decodeStaticArray(
                        encoded,
                        0,
                        new TypeReference.StaticArrayTypeReference<StaticArray2<Uint256>>(2) {},
                        2);
        assertEquals(2, decoded.getValue().size());
        assertEquals(BigInteger.valueOf(11), decoded.getValue().get(0).getValue());
        assertEquals(BigInteger.valueOf(22), decoded.getValue().get(1).getValue());
    }

    @Test
    public void testAbiDynamicArrayRoundTrip() throws Exception {
        List<Uint256> values =
                Arrays.asList(
                        new Uint256(BigInteger.valueOf(1)),
                        new Uint256(BigInteger.valueOf(2)),
                        new Uint256(BigInteger.valueOf(3)));
        DynamicArray<Uint256> value = new DynamicArray<>(Uint256.class, values);
        byte[] encoded = TypeEncoder.encode(value);
        DynamicArray<Uint256> decoded =
                TypeDecoder.decodeDynamicArray(
                        encoded, 0, new TypeReference<DynamicArray<Uint256>>() {});
        assertEquals(3, decoded.getValue().size());
        assertEquals(BigInteger.valueOf(3), decoded.getValue().get(2).getValue());
    }

    @Test
    public void testAbiDecodeUintAsInt() {
        Uint256 value = new Uint256(BigInteger.valueOf(4242));
        byte[] encoded = TypeEncoder.encode(value);
        assertEquals(4242, TypeDecoder.decodeUintAsInt(encoded, 0));
    }

    // -------------------------------------------------------------------------
    // ABI FunctionEncoder / FunctionReturnDecoder round trips
    // -------------------------------------------------------------------------

    @Test
    public void testAbiFunctionEncodeDecodeRoundTrip() throws Exception {
        List<Type> inputs = new ArrayList<>();
        inputs.add(new Uint256(BigInteger.valueOf(100)));
        inputs.add(new Bool(true));
        inputs.add(new Utf8String("payload"));

        List<TypeReference<?>> outputs = new ArrayList<>();
        outputs.add(TypeReference.create(Uint256.class));
        outputs.add(TypeReference.create(Bool.class));
        outputs.add(new TypeReference<Utf8String>() {});

        Function function = new Function("doWork", inputs, outputs);
        FunctionEncoder encoder = new FunctionEncoder(cryptoSuite().getHashImpl());
        byte[] encoded = encoder.encode(function);
        // first 4 bytes are method id
        assertTrue(encoded.length > 4);

        // Strip method id, decode parameters as if they were return values of the same layout.
        byte[] paramBytes = Arrays.copyOfRange(encoded, 4, encoded.length);
        StringBuilder hex = new StringBuilder();
        for (byte b : paramBytes) {
            hex.append(String.format("%02x", b));
        }

        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        List<Type> decoded =
                decoder.decode(hex.toString(), org.fisco.bcos.sdk.v3.codec.Utils.convert(outputs));
        assertEquals(3, decoded.size());
        assertEquals(BigInteger.valueOf(100), decoded.get(0).getValue());
        assertEquals(Boolean.TRUE, decoded.get(1).getValue());
        assertEquals("payload", decoded.get(2).getValue());
    }

    @Test
    public void testAbiEncodeParametersNonDynamic() {
        List<Type> params = new ArrayList<>();
        params.add(new Uint256(BigInteger.ONE));
        params.add(new Bool(false));
        byte[] encoded = FunctionEncoder.encodeParameters(params, null);
        assertEquals(64, encoded.length);
    }

    @Test
    public void testAbiEncodeConstructor() {
        List<Type> params = new ArrayList<>();
        params.add(new Uint256(BigInteger.valueOf(5)));
        byte[] encoded = FunctionEncoder.encodeConstructor(params);
        assertEquals(32, encoded.length);
    }

    @Test
    public void testAbiBuildMethodIdAndSignature() {
        FunctionEncoder encoder = new FunctionEncoder(cryptoSuite().getHashImpl());
        List<Type> params = new ArrayList<>();
        params.add(new Uint256(BigInteger.ONE));
        String sig =
                org.fisco.bcos.sdk.v3.codec.FunctionEncoderInterface.buildMethodSignature(
                        "foo", params);
        assertEquals("foo(uint256)", sig);
        byte[] id = encoder.buildMethodId(sig);
        assertEquals(4, id.length);
    }

    @Test
    public void testAbiFunctionReturnDecoderEmpty() {
        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        List<Type> result = decoder.decode("", new ArrayList<>());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAbiDecodeIndexedValue() {
        FunctionReturnDecoder decoder = new FunctionReturnDecoder();
        Uint256 value = new Uint256(BigInteger.valueOf(77));
        byte[] encoded = TypeEncoder.encode(value);
        StringBuilder hex = new StringBuilder();
        for (byte b : encoded) {
            hex.append(String.format("%02x", b));
        }
        Type decoded = decoder.decodeIndexedValue(hex.toString(), TypeReference.create(Uint256.class));
        assertEquals(BigInteger.valueOf(77), decoded.getValue());
    }

    // -------------------------------------------------------------------------
    // EventEncoder
    // -------------------------------------------------------------------------

    @Test
    public void testEventEncoderTopic() {
        List<TypeReference<?>> params = new ArrayList<>();
        params.add(TypeReference.create(Uint256.class));
        params.add(TypeReference.create(Address.class));
        Event event = new Event("Transfer", params);

        EventEncoder encoder = new EventEncoder(cryptoSuite().getHashImpl());
        String topic = encoder.encode(event);
        assertTrue(topic.startsWith("0x"));
        // 32 byte hash -> 64 hex chars + "0x"
        assertEquals(66, topic.length());

        String sameViaEvent = event.encodeToTopic(cryptoSuite().getHashImpl());
        assertEquals(topic, sameViaEvent);

        String sig = encoder.buildMethodSignature("Transfer", event.getParameters());
        assertEquals("Transfer(uint256,address)", sig);
        assertEquals(0, event.getIndexedParameters().size());
        assertEquals(2, event.getNonIndexedParameters().size());
    }

    // -------------------------------------------------------------------------
    // SCALE ScaleCodecWriter / ScaleCodecReader low level
    // -------------------------------------------------------------------------

    @Test
    public void testScaleWriterReaderCompactAndBytes() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        writer.writeCompact(42);
        writer.writeAsList(new byte[] {1, 2, 3});
        writer.writeByte((byte) 9);
        byte[] data = out.toByteArray();

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertTrue(reader.hasNext());
        assertEquals(42, reader.readCompact());
        assertArrayEquals(new byte[] {1, 2, 3}, reader.readByteArray());
        assertEquals(9, reader.readByte());
        assertFalse(reader.hasNext());
    }

    @Test
    public void testScaleWriterReaderBoolAndString() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        writer.writeByte((byte) 1);
        writer.writeAsList("scale-string".getBytes(StandardCharsets.UTF_8));
        byte[] data = out.toByteArray();

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertTrue(reader.readBoolean());
        assertEquals("scale-string", reader.readString());
    }

    @Test
    public void testScaleWriterReaderInteger() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        writer.writeInteger(BigInteger.valueOf(1000), 4);
        writer.writeUnsignedInteger(BigInteger.valueOf(250), 1);
        byte[] data = out.toByteArray();

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(BigInteger.valueOf(1000), reader.decodeInteger(true, 4));
        assertEquals(BigInteger.valueOf(250), reader.decodeInteger(false, 1));
    }

    @Test
    public void testScaleWriterReaderBigInt256() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(out);
        BigInteger value = BigInteger.valueOf(987654321L);
        writer.writeBigInt256(false, value);
        byte[] data = out.toByteArray();
        assertEquals(32, data.length);

        ScaleCodecReader reader = new ScaleCodecReader(data);
        assertEquals(value, reader.decodeInt256());
    }

    @Test
    public void testScaleReaderReadByteArrayBoundsCheck() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {0, 0});
        try {
            reader.readByteArray(100);
            Assert.fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            // ok
        }
    }

    @Test
    public void testScaleReaderHasMore() {
        ScaleCodecReader reader = new ScaleCodecReader(new byte[] {1, 2, 3});
        assertTrue(reader.hasMore(0));
        assertTrue(reader.hasMore(3));
        assertFalse(reader.hasMore(4));
    }

    // -------------------------------------------------------------------------
    // SCALE TypeEncoder / TypeDecoder round trips
    // -------------------------------------------------------------------------

    @Test
    public void testScaleUint256RoundTrip() throws Exception {
        Uint256 value = new Uint256(BigInteger.valueOf(424242L));
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        Uint256 decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(Uint256.class));
        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testScaleUint64RoundTrip() throws Exception {
        Uint64 value = new Uint64(BigInteger.valueOf(0xdeadbeefL));
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        Uint64 decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(Uint64.class));
        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testScaleInt256NegativeRoundTrip() throws Exception {
        Int256 value = new Int256(BigInteger.valueOf(-555));
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        Int256 decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(Int256.class));
        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testScaleBoolRoundTrip() throws Exception {
        byte[] encodedTrue = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(new Bool(true));
        byte[] encodedFalse = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(new Bool(false));
        Bool dTrue =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encodedTrue, TypeReference.create(Bool.class));
        Bool dFalse =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encodedFalse, TypeReference.create(Bool.class));
        assertTrue(dTrue.getValue());
        assertFalse(dFalse.getValue());
    }

    @Test
    public void testScaleAddressRoundTrip() throws Exception {
        Address addr = new Address(new Uint160(BigInteger.valueOf(0x12345L)));
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(addr);
        Address decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(Address.class));
        assertEquals(addr, decoded);
    }

    @Test
    public void testScaleStaticBytesRoundTrip() throws Exception {
        Bytes32 value = new Bytes32(sequentialBytes(32));
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        assertEquals(32, encoded.length);
        Bytes32 decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(Bytes32.class));
        assertArrayEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testScaleDynamicBytesRoundTrip() throws Exception {
        byte[] raw = "scale dynamic bytes".getBytes(StandardCharsets.UTF_8);
        DynamicBytes value = new DynamicBytes(raw);
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        DynamicBytes decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(DynamicBytes.class));
        assertArrayEquals(raw, decoded.getValue());
    }

    @Test
    public void testScaleStringRoundTrip() throws Exception {
        Utf8String value = new Utf8String("scale utf8 string value");
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        Utf8String decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, TypeReference.create(Utf8String.class));
        assertEquals(value.getValue(), decoded.getValue());
    }

    @Test
    public void testScaleDynamicArrayRoundTrip() throws Exception {
        List<Uint256> values =
                Arrays.asList(
                        new Uint256(BigInteger.valueOf(10)),
                        new Uint256(BigInteger.valueOf(20)),
                        new Uint256(BigInteger.valueOf(30)));
        DynamicArray<Uint256> value = new DynamicArray<>(Uint256.class, values);
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        DynamicArray<Uint256> decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, new TypeReference<DynamicArray<Uint256>>() {});
        assertEquals(3, decoded.getValue().size());
        assertEquals(BigInteger.valueOf(20), decoded.getValue().get(1).getValue());
    }

    @Test
    public void testScaleStaticArrayRoundTrip() throws Exception {
        StaticArray2<Uint256> value =
                new StaticArray2<>(
                        Uint256.class,
                        Arrays.asList(
                                new Uint256(BigInteger.valueOf(101)),
                                new Uint256(BigInteger.valueOf(202))));
        byte[] encoded = org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(value);
        StaticArray<Uint256> decoded =
                org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                        encoded, new TypeReference.StaticArrayTypeReference<StaticArray<Uint256>>(2) {
                            @Override
                            public java.lang.reflect.Type getType() {
                                return new java.lang.reflect.ParameterizedType() {
                                    @Override
                                    public java.lang.reflect.Type[] getActualTypeArguments() {
                                        return new java.lang.reflect.Type[] {Uint256.class};
                                    }

                                    @Override
                                    public java.lang.reflect.Type getRawType() {
                                        return StaticArray2.class;
                                    }

                                    @Override
                                    public java.lang.reflect.Type getOwnerType() {
                                        return Class.class;
                                    }
                                };
                            }
                        });
        assertEquals(2, decoded.getValue().size());
        assertEquals(BigInteger.valueOf(101), decoded.getValue().get(0).getValue());
        assertEquals(BigInteger.valueOf(202), decoded.getValue().get(1).getValue());
    }

    // -------------------------------------------------------------------------
    // High level ContractCodec round trips (ABI = non-wasm)
    // -------------------------------------------------------------------------

    private static final String SIMPLE_ABI =
            "[{\"constant\":false,\"inputs\":[{\"name\":\"u\",\"type\":\"uint256\"},{\"name\":\"b\",\"type\":\"bool\"},{\"name\":\"s\",\"type\":\"string\"},{\"name\":\"a\",\"type\":\"address\"}],\"name\":\"setAll\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    @Test
    public void testContractCodecAbiEncodeDecodeRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> args = new ArrayList<>();
        args.add(new BigInteger("12345"));
        args.add(Boolean.TRUE);
        args.add("hello contract");
        args.add("0x00000000000000000000000000000000000000ab");

        byte[] encoded = codec.encodeMethod(SIMPLE_ABI, "setAll", args);
        assertTrue(encoded.length > 4);

        List<Type> decodedTypes =
                codec.decodeMethodInput(SIMPLE_ABI, "setAll", Hex.toHexString(encoded));
        assertEquals(4, decodedTypes.size());
        assertEquals(new BigInteger("12345"), decodedTypes.get(0).getValue());
        assertEquals(Boolean.TRUE, decodedTypes.get(1).getValue());
        assertEquals("hello contract", decodedTypes.get(2).getValue());

        List<String> decodedStrings = codec.decodeMethodInputToString(SIMPLE_ABI, "setAll", encoded);
        assertEquals(4, decodedStrings.size());
    }

    @Test
    public void testContractCodecAbiEncodeFromStringMatchesObjects() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        List<Object> args = new ArrayList<>();
        args.add(new BigInteger("7"));
        args.add(Boolean.FALSE);
        args.add("abc");
        args.add("0x0000000000000000000000000000000000000001");
        byte[] fromObjects = codec.encodeMethod(SIMPLE_ABI, "setAll", args);

        List<String> stringArgs = new ArrayList<>();
        stringArgs.add("7");
        stringArgs.add("false");
        stringArgs.add("abc");
        stringArgs.add("0x0000000000000000000000000000000000000001");
        byte[] fromStrings = codec.encodeMethodFromString(SIMPLE_ABI, "setAll", stringArgs);

        assertArrayEquals(fromObjects, fromStrings);
    }

    @Test
    public void testContractCodecAbiByInterfaceRoundTrip() throws Exception {
        ContractCodec codec = new ContractCodec(cryptoSuite(), false);
        String sig = "setAll(uint256,bool,string,address)";
        List<Object> args = new ArrayList<>();
        args.add(new BigInteger("99"));
        args.add(Boolean.TRUE);
        args.add("iface");
        args.add("0x0000000000000000000000000000000000000002");

        byte[] byInterface = codec.encodeMethodByInterface(sig, args);
        byte[] methodId = codec.getFunctionEncoder().buildMethodId(sig);
        assertEquals(4, methodId.length);

        List<Object> decoded = codec.decodeMethodInputByInterface(SIMPLE_ABI, sig, byInterface);
        assertEquals(4, decoded.size());
    }

    @Test
    public void testContractCodecScaleEncodeDecodeRoundTrip() throws Exception {
        // isWasm = true selects the SCALE codec backend.
        ContractCodec codec = new ContractCodec(cryptoSuite(), true);
        List<Object> args = new ArrayList<>();
        args.add(new BigInteger("321"));
        args.add(Boolean.TRUE);
        args.add("scale path");
        args.add("0x00000000000000000000000000000000000000cd");

        byte[] encoded = codec.encodeMethod(SIMPLE_ABI, "setAll", args);
        assertNotNull(encoded);

        // decodeMethodInputToString routes through the wasm-aware (SCALE) decode path.
        List<String> decoded = codec.decodeMethodInputToString(SIMPLE_ABI, "setAll", encoded);
        assertEquals(4, decoded.size());
        assertTrue(decoded.get(0).contains("321"));
        assertTrue(decoded.get(2).contains("scale path"));
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private static byte[] sequentialBytes(int n) {
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            out[i] = (byte) (i + 1);
        }
        return out;
    }
}
