package org.fisco.bcos.sdk.v3.test.codec.scale;

import org.fisco.bcos.sdk.v3.test.codec.TestUtils;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Uint;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes6;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray3;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder;
import org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder;
import org.fisco.bcos.sdk.v3.test.codec.TestFixture;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class TypeEncoderTest {
    @Test
    public void testBool() throws IOException, ClassNotFoundException {
        Assert.assertEquals(TestUtils.bytesToString(TypeEncoder.encode(new Bool(false))), ("00"));
        assertEquals(TestUtils.bytesToString(TypeEncoder.encode(new Bool(true))), ("01"));

        Assert.assertEquals(TypeDecoder.decode("00", TypeReference.create(Bool.class)), new Bool(false));
        assertEquals(TypeDecoder.decode("01", TypeReference.create(Bool.class)), new Bool(true));
    }

    @Test
    public void testStaticBytes() throws IOException, ClassNotFoundException {
        Bytes staticBytes = new Bytes6(new byte[]{0, 1, 2, 3, 4, 5});
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(staticBytes)),

                ("000102030405"));
        assertEquals(TypeDecoder.decode("000102030405", TypeReference.create(Bytes6.class)), staticBytes);

        Bytes empty = new Bytes1(new byte[]{0});
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(empty)),
                ("00"));
        assertEquals(TypeDecoder.decode("00", TypeReference.create(Bytes1.class)), empty);

        Bytes ones = new Bytes1(new byte[]{127});
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(ones)),
                ("7f"));

        assertEquals(TypeDecoder.decode( "7f", TypeReference.create(Bytes1.class)), ones);

        Bytes dave = new Bytes4("dave".getBytes());
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(dave)),
                ( "64617665"));

        assertEquals(TypeDecoder.decode( "64617665", TypeReference.create(Bytes4.class)), dave);
    }

    @Test
    public void testDynamicBytes() throws IOException, ClassNotFoundException {
        DynamicBytes dynamicBytes = new DynamicBytes(new byte[]{0, 1, 2, 3, 4, 5});
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(dynamicBytes)),
                ("18" + "000102030405"));
        assertEquals(TypeDecoder.decode("18" + "000102030405", TypeReference.create(DynamicBytes.class)), dynamicBytes);

        DynamicBytes empty = new DynamicBytes(new byte[]{0});
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(empty)),
                ("04" + "00"));
        assertEquals(TypeDecoder.decode("04" + "00", TypeReference.create(DynamicBytes.class)), empty);

        DynamicBytes dave = new DynamicBytes("dave".getBytes());
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(dave)),
                ("10" + "64617665"));

        assertEquals(TypeDecoder.decode("10" + "64617665", TypeReference.create(DynamicBytes.class)), dave);

        DynamicBytes loremIpsum =
                new DynamicBytes(
                        ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
                                + "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim "
                                + "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex "
                                + "ea commodo consequat. Duis aute irure dolor in reprehenderit in "
                                + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur "
                                + "sint occaecat cupidatat non proident, sunt in culpa qui officia "
                                + "deserunt mollit anim id est laborum.")
                                .getBytes());
        String expectHex = "f506"
                + "4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73"
                + "656374657475722061646970697363696e6720656c69742c2073656420646f20"
                + "656975736d6f642074656d706f7220696e6369646964756e74207574206c6162"
                + "6f726520657420646f6c6f7265206d61676e6120616c697175612e2055742065"
                + "6e696d206164206d696e696d2076656e69616d2c2071756973206e6f73747275"
                + "6420657865726369746174696f6e20756c6c616d636f206c61626f726973206e"
                + "69736920757420616c697175697020657820656120636f6d6d6f646f20636f6e"
                + "7365717561742e2044756973206175746520697275726520646f6c6f7220696e"
                + "20726570726568656e646572697420696e20766f6c7570746174652076656c69"
                + "7420657373652063696c6c756d20646f6c6f726520657520667567696174206e"
                + "756c6c612070617269617475722e204578636570746575722073696e74206f63"
                + "63616563617420637570696461746174206e6f6e2070726f6964656e742c2073"
                + "756e7420696e2063756c706120717569206f666669636961206465736572756e"
                + "74206d6f6c6c697420616e696d20696420657374206c61626f72756d2e";
        assertEquals(TestUtils.bytesToString(TypeEncoder.encode(loremIpsum)), expectHex);

        assertEquals(TypeDecoder.decode(expectHex, TypeReference.create(DynamicBytes.class)), loremIpsum);
    }

    @Test
    public void testUtf8String() throws IOException, ClassNotFoundException {
        Utf8String string = new Utf8String("Hello, world!");
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(string)),
                ("34" + "48656c6c6f2c20776f726c6421"));
        assertEquals(TypeDecoder.decode("34" + "48656c6c6f2c20776f726c6421", TypeReference.create(Utf8String.class)), string);
    }

    @Test
    public void testDynamicArray() throws IOException, ClassNotFoundException {
        DynamicArray<Uint> array =
                new DynamicArray<>(
                        Uint.class,
                        new Uint256(BigInteger.ONE),
                        new Uint256(BigInteger.valueOf(2)),
                        new Uint256(BigInteger.valueOf(3)));
        String expectedHex = "0c"
                + "0000000000000000000000000000000000000000000000000000000000000001"
                + "0000000000000000000000000000000000000000000000000000000000000002"
                + "0000000000000000000000000000000000000000000000000000000000000003";
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(array)),
                expectedHex);
        ScaleCodecReader scaleCodecReader = new ScaleCodecReader(Hex.decode(expectedHex));
        DynamicArray<Uint> uintDynamicArray = (DynamicArray<Uint>) TypeDecoder.decodeDynamicArray(scaleCodecReader, TypeReference.makeTypeReference("uint256[]", false));
        for (int i = 0; i < array.getValue().size(); i++) {
            assertEquals(array.getValue().get(i),uintDynamicArray.getValue().get(i));
        }
    }

    @Test
    public void testDynamicStringsArray() throws IOException, ClassNotFoundException {
        DynamicArray<Utf8String> array =
                new DynamicArray<>(
                        Utf8String.class,
                        new Utf8String("web3j"),
                        new Utf8String("arrays"),
                        new Utf8String("encoding"));
        String expectedHex = "0c"
                + "14"
                + "776562336a"
                + "18"
                + "617272617973"
                + "20"
                + "656e636f64696e67";
        assertEquals(
                expectedHex,
                TestUtils.bytesToString(TypeEncoder.encode(array)));
        ScaleCodecReader scaleCodecReader = new ScaleCodecReader(Hex.decode(expectedHex));
        DynamicArray<Utf8String> dynamicArray = (DynamicArray<Utf8String>) TypeDecoder.decodeDynamicArray(scaleCodecReader, TypeReference.makeTypeReference("string[]", false));
        for (int i = 0; i < array.getValue().size(); i++) {
            assertEquals(array.getValue().get(i),dynamicArray.getValue().get(i));
        }
    }

    @Test
    public void testStructsDynamicArray() throws IOException, ClassNotFoundException {
        DynamicArray<TestFixture.Foo> array =
                new DynamicArray<>(
                        TestFixture.Foo.class,
                        new TestFixture.Foo("id1", "name"),
                        new TestFixture.Foo("id2", "name"),
                        new TestFixture.Foo("id3", "name"));
        String expectedHex = "0c"
                + "0c"
                + "696431"
                + "10"
                + "6e616d65"
                + "0c"
                + "696432"
                + "10"
                + "6e616d65"
                + "0c"
                + "696433"
                + "10"
                + "6e616d65";
        assertEquals(TestUtils.bytesToString(TypeEncoder.encode(array)), expectedHex);
    }

    @Test
    public void testDynamicStructStaticArray() throws IOException {
        StaticArray3<TestFixture.Foo> array =
                new StaticArray3<>(
                        TestFixture.Foo.class, new TestFixture.Foo("", ""), new TestFixture.Foo("id", "name"), new TestFixture.Foo("", ""));

        assertEquals(
                ("0c"
                        + "0000"
                        + "08"
                        + "6964"
                        + "10"
                        + "6e616d65"
                        + "0000"),
                TestUtils.bytesToString(TypeEncoder.encode(array)));
    }

    @Test
    public void testStaticStructStaticArray() throws IOException {
        StaticArray3<TestFixture.Bar> array =
                new StaticArray3<>(
                        TestFixture.Bar.class,
                        new TestFixture.Bar(BigInteger.ONE, BigInteger.ZERO),
                        new TestFixture.Bar(BigInteger.ONE, BigInteger.ZERO),
                        new TestFixture.Bar(BigInteger.ONE, BigInteger.ZERO));

        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(array)),
                ("0c"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000000"));
    }

    @Test
    public void testArrayOfBytes() throws IOException {
        DynamicArray<DynamicBytes> array =
                new DynamicArray<>(
                        new DynamicBytes(
                                Numeric.hexStringToByteArray(
                                        "0x3c329ee8cd725a7f74f984cac52598eb170d731e7f3"
                                                + "80d59a18aa861d2c8d6c43c880b2bfe0f3cde4efcd7"
                                                + "11c010c2f1d8af5e796f06716539446f95420df4211c")),
                        new DynamicBytes(
                                Numeric.hexStringToByteArray("0xcafe0000cafe0000cafe0000")),
                        new DynamicBytes(
                                Numeric.hexStringToByteArray(
                                        "0x9215c928b97e0ebeeefd10003a4e3eea23f2eb3acba"
                                                + "b477eeb589d7a8874d7c5")));
        DynamicArray<DynamicBytes> arrayOfEmptyBytes =
                new DynamicArray<>(new DynamicBytes(new byte[0]), new DynamicBytes(new byte[0]));

        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(array)),
                //  array length
                ("0c"
                        // length first bytes, mode TWO 0x105 & 0xff || 0x1 & 0xff
                        + "0501"
                        // first bytes
                        + "3c329ee8cd725a7f74f984cac52598eb170d731e7f380d59a18aa861d2c8d6c4"
                        // first bytes continued
                        + "3c880b2bfe0f3cde4efcd711c010c2f1d8af5e796f06716539446f95420df4211c"
                        // length second bytes
                        + "30"
                        // second bytes
                        + "cafe0000cafe0000cafe0000"
                        // length third bytes
                        + "80"
                        // third bytes
                        + "9215c928b97e0ebeeefd10003a4e3eea23f2eb3acbab477eeb589d7a8874d7c5"));
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(arrayOfEmptyBytes)),
                //  array length
                ("08"
                        // length first bytes
                        + "00"
                        // length second bytes
                        + "00"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testArrayOfStrings() throws IOException {
        DynamicArray<Utf8String> array =
                new DynamicArray<>(
                        new Utf8String(
                                "This string value is extra long so that it "
                                        + "requires more than 32 bytes"),
                        new Utf8String("abc"),
                        new Utf8String(""),
                        new Utf8String("web3j"));
        DynamicArray<Utf8String> arrayOfEmptyStrings =
                new DynamicArray<>(new Utf8String(""), new Utf8String(""));

        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(array)),
                //  array length
                ("10"
                        // length first string
                        + "1901"
                        // first string
                        + "5468697320737472696e672076616c7565206973206578747261206c6f6e6720"
                        // first string continued
                        + "736f2074686174206974207265717569726573206d6f7265207468616e203332"
                        // first string continued
                        + "206279746573"
                        // length second string
                        + "0c"
                        // second string
                        + "616263"
                        // length third string
                        + "00"
                        // length fourth string
                        + "14"
                        // fourth string
                        + "776562336a"));
        assertEquals(
                TestUtils.bytesToString(TypeEncoder.encode(arrayOfEmptyStrings)),
                //  array length
                ("08"
                        // length first string
                        + "00"
                        // length second string
                        + "00"));
    }
}
