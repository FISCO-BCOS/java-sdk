package org.fisco.bcos.sdk.v3.codec.abi;

import org.fisco.bcos.sdk.v3.codec.TestUtils;
import org.fisco.bcos.sdk.v3.codec.datatypes.*;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class TypeDecoderTest {
    @Test
    public void testBoolDecode() throws Exception {
        Assert.assertEquals(
                TypeDecoder.decodeBool(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"), 0),
                (new Bool(false)));

        assertEquals(
                TypeDecoder.decodeBool(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000001"), 0),
                (new Bool(true)));
    }


    @Test
    public void testBoolDecodeGivenOffset() throws ClassNotFoundException {
        // Decode second parameter as Bool
        assertEquals(
                TypeDecoder.decode(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000007fffffffffffffff"
                                + "0000000000000000000000000000000000000000000000000000000000000000"
                                + "0000000000000000000000000000000000000000000000007fffffffffffffff"),
                        32,
                        TypeReference.create(Bool.class)),
                (new Bool(false)));

        assertEquals(
                TypeDecoder.decode(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000007fffffffffffffff"
                                + "0000000000000000000000000000000000000000000000000000000000000001"
                                + "0000000000000000000000000000000000000000000000007fffffffffffffff"),
                        32,
                        TypeReference.create(Bool.class)),
                (new Bool(true)));
    }

    @Test
    public void testUintDecode() throws Exception {
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint8.class),
                new Uint8(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000000000000000000000ff"),
                        Uint8.class),
                new Uint8(BigInteger.valueOf(255)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint16.class),
                new Uint16(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000000000000000000000ffff"),
                        Uint16.class),
                new Uint16(BigInteger.valueOf(65535)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint24.class),
                new Uint24(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000ffffff"),
                        Uint24.class),
                new Uint24(BigInteger.valueOf(16777215)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint32.class),
                new Uint32(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000000000000000ffffffff"),
                        Uint32.class),
                new Uint32(BigInteger.valueOf(4294967295L)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint40.class),
                new Uint40(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000000000000000ffffffffff"),
                        Uint40.class),
                new Uint40(BigInteger.valueOf(1099511627775L)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint48.class),
                new Uint48(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000ffffffffffff"),
                        Uint48.class),
                new Uint48(BigInteger.valueOf(281474976710655L)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint56.class),
                new Uint56(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000000000ffffffffffffff"),
                        Uint56.class),
                new Uint56(BigInteger.valueOf(72057594037927935L)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint64.class),
                (new Uint64(BigInteger.ZERO)));

        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000007fffffffffffffff"),
                        Uint64.class),
                (new Uint64(BigInteger.valueOf(Long.MAX_VALUE))));

        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint64.class),
                (new Uint64(new BigInteger("0ffffffffffffffff", 16))));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint72.class),
                new Uint72(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000ffffffffffffffffff"),
                        Uint72.class),
                new Uint72(new BigInteger("4722366482869645213695")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint80.class),
                new Uint80(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000ffffffffffffffffffff"),
                        Uint80.class),
                new Uint80(new BigInteger("1208925819614629174706175")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint88.class),
                new Uint88(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000ffffffffffffffffffffff"),
                        Uint88.class),
                new Uint88(new BigInteger("309485009821345068724781055")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint96.class),
                new Uint96(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000ffffffffffffffffffffffff"),
                        Uint96.class),
                new Uint96(new BigInteger("79228162514264337593543950335")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint104.class),
                new Uint104(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000ffffffffffffffffffffffffff"),
                        Uint104.class),
                new Uint104(new BigInteger("20282409603651670423947251286015")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint112.class),
                new Uint112(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000ffffffffffffffffffffffffffff"),
                        Uint112.class),
                new Uint112(new BigInteger("5192296858534827628530496329220095")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint120.class),
                new Uint120(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000ffffffffffffffffffffffffffffff"),
                        Uint120.class),
                new Uint120(new BigInteger("1329227995784915872903807060280344575")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint128.class),
                new Uint128(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000ffffffffffffffffffffffffffffffff"),
                        Uint128.class),
                new Uint128(new BigInteger("340282366920938463463374607431768211455")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint136.class),
                new Uint136(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000ffffffffffffffffffffffffffffffffff"),
                        Uint136.class),
                new Uint136(new BigInteger("87112285931760246646623899502532662132735")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint144.class),
                new Uint144(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000ffffffffffffffffffffffffffffffffffff"),
                        Uint144.class),
                new Uint144(new BigInteger("22300745198530623141535718272648361505980415")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint152.class),
                new Uint152(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000ffffffffffffffffffffffffffffffffffffff"),
                        Uint152.class),
                new Uint152(new BigInteger("5708990770823839524233143877797980545530986495")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint160.class),
                new Uint160(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000ffffffffffffffffffffffffffffffffffffffff"),
                        Uint160.class),
                new Uint160(new BigInteger("1461501637330902918203684832716283019655932542975")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint168.class),
                new Uint168(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000ffffffffffffffffffffffffffffffffffffffffff"),
                        Uint168.class),
                new Uint168(new BigInteger("374144419156711147060143317175368453031918731001855")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint176.class),
                new Uint176(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000ffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint176.class),
                new Uint176(
                        new BigInteger("95780971304118053647396689196894323976171195136475135")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint184.class),
                new Uint184(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000ffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint184.class),
                new Uint184(
                        new BigInteger(
                                "24519928653854221733733552434404946937899825954937634815")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint192.class),
                new Uint192(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000ffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint192.class),
                new Uint192(
                        new BigInteger(
                                "6277101735386680763835789423207666416102355444464034512895")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint200.class),
                new Uint200(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000ffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint200.class),
                new Uint200(
                        new BigInteger(
                                "1606938044258990275541962092341162602522202993782792835301375")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint208.class),
                new Uint208(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000ffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint208.class),
                new Uint208(
                        new BigInteger(
                                "411376139330301510538742295639337626245683966408394965837152255")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint216.class),
                new Uint216(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000ffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint216.class),
                new Uint216(
                        new BigInteger(
                                "105312291668557186697918027683670432318895095400549111254310977535")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint224.class),
                new Uint224(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint224.class),
                new Uint224(
                        new BigInteger(
                                "26959946667150639794667015087019630673637144422540572481103610249215")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint232.class),
                new Uint232(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint232.class),
                new Uint232(
                        new BigInteger(
                                "6901746346790563787434755862277025452451108972170386555162524223799295")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint240.class),
                new Uint240(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint240.class),
                new Uint240(
                        new BigInteger(
                                "1766847064778384329583297500742918515827483896875618958121606201292619775")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint248.class),
                new Uint248(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint248.class),
                new Uint248(
                        new BigInteger(
                                "452312848583266388373324160190187140051835877600158453279131187530910662655")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Uint256.class),
                new Uint256(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Uint256.class),
                new Uint256(
                        new BigInteger(
                                "115792089237316195423570985008687907853269984665640564039457584007913129639935")));
    }

    @Test
    public void testIntDecode() throws Exception {
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int8.class),
                new Int8(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000000000000000000000007f"),
                        Int8.class),
                new Int8(BigInteger.valueOf(127)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int16.class),
                new Int16(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000007FFF"),
                        Int16.class),
                new Int16(BigInteger.valueOf(32767)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int24.class),
                new Int24(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000000000000000007fffff"),
                        Int24.class),
                new Int24(BigInteger.valueOf(8388607)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int32.class),
                new Int32(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000000000000000007fffffff"),
                        Int32.class),
                new Int32(BigInteger.valueOf(2147483647)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int40.class),
                new Int40(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000007FFFFFFFFF"),
                        Int40.class),
                new Int40(BigInteger.valueOf(549755813887L)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int48.class),
                new Int48(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000000000007FFFFFFFFFFF"),
                        Int48.class),
                new Int48(BigInteger.valueOf(140737488355327L)));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int56.class),
                new Int56(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000000000007FFFFFFFFFFFFF"),
                        Int56.class),
                new Int56(BigInteger.valueOf(36028797018963967L)));

        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int64.class),
                (new Int64(BigInteger.ZERO)));

        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000007fffffffffffffff"),
                        Int64.class),
                (new Int64(BigInteger.valueOf(Long.MAX_VALUE))));

        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("fffffffffffffffffffffffffffffffffffffffffffffff88000000000000000"),
                        Int64.class),
                (new Int64(BigInteger.valueOf(Long.MIN_VALUE))));

        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int64.class),
                (new Int64(BigInteger.valueOf(-1))));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int72.class),
                new Int72(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000007fffffffffffffffff"),
                        Int72.class),
                new Int72(new BigInteger("2361183241434822606847")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int80.class),
                new Int80(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000007fffffffffffffffffff"),
                        Int80.class),
                new Int80(new BigInteger("604462909807314587353087")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int88.class),
                new Int88(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000007fffffffffffffffffffff"),
                        Int88.class),
                new Int88(new BigInteger("154742504910672534362390527")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int96.class),
                new Int96(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000007fffffffffffffffffffffff"),
                        Int96.class),
                new Int96(new BigInteger("39614081257132168796771975167")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int104.class),
                new Int104(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000000000007fffffffffffffffffffffffff"),
                        Int104.class),
                new Int104(new BigInteger("10141204801825835211973625643007")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int112.class),
                new Int112(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000007fffffffffffffffffffffffffff"),
                        Int112.class),
                new Int112(new BigInteger("2596148429267413814265248164610047")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int120.class),
                new Int120(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000000000007fffffffffffffffffffffffffffff"),
                        Int120.class),
                new Int120(new BigInteger("664613997892457936451903530140172287")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int128.class),
                new Int128(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000000000007fffffffffffffffffffffffffffffff"),
                        Int128.class),
                new Int128(new BigInteger("170141183460469231731687303715884105727")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int136.class),
                new Int136(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000007fffffffffffffffffffffffffffffffff"),
                        Int136.class),
                new Int136(new BigInteger("43556142965880123323311949751266331066367")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int144.class),
                new Int144(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000000000007fffffffffffffffffffffffffffffffffff"),
                        Int144.class),
                new Int144(new BigInteger("11150372599265311570767859136324180752990207")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int152.class),
                new Int152(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000000000007fffffffffffffffffffffffffffffffffffff"),
                        Int152.class),
                new Int152(new BigInteger("2854495385411919762116571938898990272765493247")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int160.class),
                new Int160(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000007fffffffffffffffffffffffffffffffffffffff"),
                        Int160.class),
                new Int160(new BigInteger("730750818665451459101842416358141509827966271487")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int168.class),
                new Int168(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000000000007fffffffffffffffffffffffffffffffffffffffff"),
                        Int168.class),
                new Int168(new BigInteger("187072209578355573530071658587684226515959365500927")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int176.class),
                new Int176(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000000000007fffffffffffffffffffffffffffffffffffffffffff"),
                        Int176.class),
                new Int176(
                        new BigInteger("47890485652059026823698344598447161988085597568237567")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int184.class),
                new Int184(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000007fffffffffffffffffffffffffffffffffffffffffffff"),
                        Int184.class),
                new Int184(
                        new BigInteger(
                                "12259964326927110866866776217202473468949912977468817407")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int192.class),
                new Int192(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000000000007fffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int192.class),
                new Int192(
                        new BigInteger(
                                "3138550867693340381917894711603833208051177722232017256447")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int200.class),
                new Int200(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000000000007fffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int200.class),
                new Int200(
                        new BigInteger(
                                "803469022129495137770981046170581301261101496891396417650687")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int208.class),
                new Int208(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000007fffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int208.class),
                new Int208(
                        new BigInteger(
                                "205688069665150755269371147819668813122841983204197482918576127")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int216.class),
                new Int216(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00000000007fffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int216.class),
                new Int216(
                        new BigInteger(
                                "52656145834278593348959013841835216159447547700274555627155488767")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int224.class),
                new Int224(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("000000007fffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int224.class),
                new Int224(
                        new BigInteger(
                                "13479973333575319897333507543509815336818572211270286240551805124607")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int232.class),
                new Int232(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000007fffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int232.class),
                new Int232(
                        new BigInteger(
                                "3450873173395281893717377931138512726225554486085193277581262111899647")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int240.class),
                new Int240(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("00007fffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int240.class),
                new Int240(
                        new BigInteger(
                                "883423532389192164791648750371459257913741948437809479060803100646309887")));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Int248.class),
                new Int248(BigInteger.ZERO));
        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("007fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int248.class),
                new Int248(
                        new BigInteger(
                                "226156424291633194186662080095093570025917938800079226639565593765455331327")));

        assertEquals(
                TypeDecoder.decodeNumeric(
                        TestUtils.stringToBytes("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
                        Int256.class),
                (new Int256(BigInteger.valueOf(-1))));
    }


    @Test
    public void testStaticBytes() throws Exception {
        byte[] testbytes = new byte[]{0, 1, 2, 3, 4, 5};
        Bytes6 staticBytes = new Bytes6(testbytes);
        assertEquals(
                TypeDecoder.decodeBytes(
                        TestUtils.stringToBytes("0001020304050000000000000000000000000000000000000000000000000000"),
                        Bytes6.class),
                (staticBytes));

        Bytes empty = new Bytes1(new byte[]{0});
        assertEquals(
                TypeDecoder.decodeBytes(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        Bytes1.class),
                (empty));

        Bytes dave = new Bytes4("dave".getBytes());
        assertEquals(
                TypeDecoder.decodeBytes(
                        TestUtils.stringToBytes("6461766500000000000000000000000000000000000000000000000000000000"),
                        Bytes4.class),
                (dave));
    }

    @Test
    public void testDynamicBytes() throws Exception {
        byte[] testbytes = new byte[]{0, 1, 2, 3, 4, 5};
        DynamicBytes dynamicBytes = new DynamicBytes(testbytes);
        assertEquals(
                TypeDecoder.decodeDynamicBytes(
                        TestUtils.stringToBytes(
                                "0000000000000000000000000000000000000000000000000000000000000006" // length
                                        + "0001020304050000000000000000000000000000000000000000000000000000"),
                        0),
                (dynamicBytes));

        DynamicBytes empty = new DynamicBytes(new byte[]{0});
        assertEquals(
                TypeDecoder.decodeDynamicBytes(
                        TestUtils.stringToBytes(
                                "0000000000000000000000000000000000000000000000000000000000000001"
                                        + "0000000000000000000000000000000000000000000000000000000000000000"),
                        0),
                (empty));

        DynamicBytes dave = new DynamicBytes("dave".getBytes());
        assertEquals(
                TypeDecoder.decodeDynamicBytes(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000004"
                                + "6461766500000000000000000000000000000000000000000000000000000000"),
                        0),
                (dave));

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

        assertEquals(
                TypeDecoder.decodeDynamicBytes(
                        TestUtils.stringToBytes("00000000000000000000000000000000000000000000000000000000000001bd"
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
                                + "74206d6f6c6c697420616e696d20696420657374206c61626f72756d2e000000"),
                        0),
                (loremIpsum));
    }

    @Test
    public void testAddress() throws Exception {
        assertEquals(
                TypeDecoder.decodeAddress(
                        TestUtils.stringToBytes("000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338")),
                (new Address("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338")));

        assertEquals(
                TypeDecoder.decodeAddress(
                        TestUtils.stringToBytes("000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338")),
                (new Address("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338")));
    }

    @Test
    public void testUtf8String() throws Exception {
        assertEquals(
                TypeDecoder.decodeUtf8String(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000000000000000000000000d" // length
                                + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000"),
                        0),
                (new Utf8String("Hello, world!")));
    }

    @Test
    public void testStaticArray() throws Exception {
        assertEquals(
                TypeDecoder.decodeStaticArray(
                        TestUtils.stringToBytes("000000000000000000000000000000000000000000000000000000000000000a"
                                + "0000000000000000000000000000000000000000000000007fffffffffffffff"),
                        0,
                        new TypeReference.StaticArrayTypeReference<StaticArray<Uint256>>(2) {
                        },
                        2),
                (new StaticArray2<>(
                        Uint256.class,
                        new Uint256(BigInteger.TEN),
                        new Uint256(BigInteger.valueOf(Long.MAX_VALUE)))));

        assertEquals(
                TypeDecoder.decodeStaticArray(
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000040"
                                + "0000000000000000000000000000000000000000000000000000000000000080"
                                + "000000000000000000000000000000000000000000000000000000000000000d"
                                + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000"
                                + "000000000000000000000000000000000000000000000000000000000000000d"
                                + "776f726c64212048656c6c6f2c00000000000000000000000000000000000000"),
                        0,
                        new TypeReference.StaticArrayTypeReference<StaticArray<Utf8String>>(2) {
                        },
                        2),
                (new StaticArray2<>(
                        Utf8String.class,
                        new Utf8String("Hello, world!"),
                        new Utf8String("world! Hello,"))));
    }

    @Test
    public void testEmptyStaticArray() {
        assertThrows(
                UnsupportedOperationException.class,
                () ->
                        TypeDecoder.decodeStaticArray(
                                TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                                0,
                                new TypeReference.StaticArrayTypeReference<StaticArray<Uint256>>(
                                        0) {
                                },
                                0));
    }

    @Test
    public void testDynamicArray() throws Exception {
        assertEquals(
                TypeDecoder.decodeDynamicArray(
                        // length
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                        0,
                        new TypeReference<DynamicArray<Uint256>>() {
                        }),
                (new DynamicArray<>(Uint256.class)));

        assertEquals(
                TypeDecoder.decodeDynamicArray(
                        // length
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000002"
                                + "000000000000000000000000000000000000000000000000000000000000000a"
                                + "0000000000000000000000000000000000000000000000007fffffffffffffff"),
                        0,
                        new TypeReference<DynamicArray<Uint256>>() {
                        }),
                (new DynamicArray<>(
                        Uint256.class,
                        new Uint256(BigInteger.TEN),
                        new Uint256(BigInteger.valueOf(Long.MAX_VALUE)))));

        assertEquals(
                TypeDecoder.decodeDynamicArray(
                        // length
                        TestUtils.stringToBytes("0000000000000000000000000000000000000000000000000000000000000002"
                                + "0000000000000000000000000000000000000000000000000000000000000040"
                                + "0000000000000000000000000000000000000000000000000000000000000080"
                                + "000000000000000000000000000000000000000000000000000000000000000d"
                                + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000"
                                + "000000000000000000000000000000000000000000000000000000000000000d"
                                + "776f726c64212048656c6c6f2c00000000000000000000000000000000000000"),
                        0,
                        new TypeReference<DynamicArray<Utf8String>>() {
                        }),
                (new DynamicArray<>(
                        Utf8String.class,
                        new Utf8String("Hello, world!"),
                        new Utf8String("world! Hello,"))));
    }
}
