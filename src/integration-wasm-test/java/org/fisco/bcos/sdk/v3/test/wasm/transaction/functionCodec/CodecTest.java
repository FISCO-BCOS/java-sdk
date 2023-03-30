package org.fisco.bcos.sdk.v3.test.wasm.transaction.functionCodec;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.Constant;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CodecTest {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    @Test
    public void testNumericType() throws Exception {
        org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest codecTest = getCodecTest();
        // string
        TransactionReceipt receipt = codecTest.set_str("Test test");
        Assert.assertEquals(0, receipt.getStatus());
        String s = codecTest.get_str();
        Assert.assertEquals("Test test", s);
        // u8
        codecTest.set_u8(BigInteger.TEN);
        BigInteger u8 = codecTest.get_u8();
        Assert.assertEquals(BigInteger.TEN, u8);

        // u128
        // 2^63-1 * 2^63-1
        codecTest.set_u128(Constant.MAX_INT128);
        BigInteger u128 = codecTest.get_u128();
        Assert.assertEquals(0, Constant.MAX_INT128.compareTo(u128));
        // u256
        codecTest.set_u256(Constant.MAX_INT256);
        BigInteger u256 = codecTest.get_u256();
        Assert.assertEquals(Constant.MAX_INT256, u256);
        // i8
        codecTest.set_i8(BigInteger.valueOf(-1));
        BigInteger i8 = codecTest.get_i8();
        Assert.assertEquals(BigInteger.valueOf(-1), i8);
        // i128
        codecTest.set_i128(Constant.MIN_INT128);
        BigInteger i128 = codecTest.get_i128();
        Assert.assertEquals(0, Constant.MIN_INT128.compareTo(i128));

        codecTest.set_i128(Constant.MAX_INT128);
        BigInteger maxI128 = codecTest.get_i128();
        Assert.assertEquals(0, Constant.MAX_INT128.compareTo(maxI128));
        // i256
        codecTest.set_i256(BigInteger.valueOf(-123456789));
        BigInteger i256 = codecTest.get_i256();
        Assert.assertEquals(0, i256.compareTo(BigInteger.valueOf(-123456789)));
    }

    @Test
    public void testByteType() throws Exception {
        org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest codecTest = getCodecTest();

        // address
        codecTest.set_addr("0x0000000000000000000000000000000000000000");
        String address = codecTest.get_addr();
        Assert.assertEquals("0x0000000000000000000000000000000000000000", address);
        // bytes1
        codecTest.set_bytes1(Hex.decode("ff"));
        byte[] bytes1 = codecTest.get_bytes1();
        Assert.assertEquals("ff", Hex.toHexString(bytes1));
        // bytes16
        String bytes16Str = "ffffffff1234567890123456ffffffff";
        codecTest.set_bytes16(Hex.decode(bytes16Str));
        byte[] bytes16 = codecTest.get_bytes16();
        Assert.assertEquals(bytes16Str, Hex.toHexString(bytes16));
        // bytes32
        codecTest.set_bytes32(Hex.decode(bytes16Str + bytes16Str));
        byte[] bytes32 = codecTest.get_bytes32();
        Assert.assertEquals(bytes16Str + bytes16Str, Hex.toHexString(bytes32));
        // bytes
        codecTest.set_bytes(Hex.decode(bytes16Str + bytes16Str + bytes16Str));
        byte[] bytes = codecTest.get_bytes();
        Assert.assertEquals(bytes16Str + bytes16Str + bytes16Str, Hex.toHexString(bytes));
    }

    @Test
    public void testUintNumericArrayType() throws Exception {
        org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest codecTest = getCodecTest();

        List<BigInteger> uint8Arr10 = new ArrayList<>();
        List<BigInteger> uint64Arr10 = new ArrayList<>();
        List<BigInteger> uint256Arr10 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            uint8Arr10.add(BigInteger.valueOf(i));
            uint64Arr10.add(BigInteger.valueOf(i));
            uint256Arr10.add(Constant.MAX_INT256.subtract(BigInteger.valueOf(i)));
        }
        /// static array
        System.out.println("=============uint static array=============");
        // uint8[10]
        codecTest.set_u8_sarr(uint8Arr10);
        List u8Arr = codecTest.get_u8_sarr();
        for (int i = 0; i < u8Arr.size(); i++) {
            Assert.assertEquals(uint8Arr10.get(i), u8Arr.get(i));
        }
        // uint64[10]
        codecTest.set_u64_sarr(uint64Arr10);
        List u64Arr = codecTest.get_u64_sarr();
        for (int i = 0; i < u64Arr.size(); i++) {
            Assert.assertEquals(uint64Arr10.get(i), u64Arr.get(i));
        }
        // uint256[10]
        codecTest.set_u256_sarr(uint256Arr10);
        List u256Arr = codecTest.get_u256_sarr();
        for (int i = 0; i < u256Arr.size(); i++) {
            Assert.assertEquals(uint256Arr10.get(i), u256Arr.get(i));
        }
        /// dynamic array
        System.out.println("=============uint dynamic array=============");
        // uint8[]
        uint8Arr10.add(BigInteger.valueOf(10));
        codecTest.set_u8_arr(uint8Arr10);
        List u8ArrDyn = codecTest.get_u8_arr();
        for (int i = 0; i < u8ArrDyn.size(); i++) {
            Assert.assertEquals(uint8Arr10.get(i), u8ArrDyn.get(i));
        }
        // uint128[]
        uint64Arr10.add(BigInteger.TEN);
        codecTest.set_u64_arr(uint64Arr10);
        List u128ArrDyn = codecTest.get_u64_arr();
        for (int i = 0; i < u128ArrDyn.size(); i++) {
            Assert.assertEquals(uint64Arr10.get(i), u128ArrDyn.get(i));
        }
        // uint256[]
        uint256Arr10.add(Constant.MAX_INT256.subtract(BigInteger.valueOf(10)));
        codecTest.set_u256_arr(uint256Arr10);
        List u256ArrDyn = codecTest.get_u256_arr();
        for (int i = 0; i < u256ArrDyn.size(); i++) {
            Assert.assertEquals(uint256Arr10.get(i), u256ArrDyn.get(i));
        }
    }

    @Test
    public void testIntNumericArrayType() throws Exception {
        org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest codecTest = getCodecTest();

        List<BigInteger> int8Arr10 = new ArrayList<>();
        List<BigInteger> int64Arr10 = new ArrayList<>();
        List<BigInteger> int256Arr10 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int8Arr10.add(BigInteger.valueOf(i));
            int64Arr10.add(BigInteger.TEN.pow(2).multiply(BigInteger.valueOf(i)));
            int256Arr10.add(Constant.MAX_INT256.subtract(BigInteger.valueOf(i)));
        }
        /// static array
        System.out.println("=============int static array=============");
        // int8[10]
        codecTest.set_i8_sarr(int8Arr10);
        List i8Arr = codecTest.get_i8_sarr();
        for (int i = 0; i < i8Arr.size(); i++) {
            Assert.assertEquals(int8Arr10.get(i), i8Arr.get(i));
        }
        // int128[10]
        codecTest.set_i64_sarr(int64Arr10);
        List i128Arr = codecTest.get_i64_sarr();
        for (int i = 0; i < i128Arr.size(); i++) {
            Assert.assertEquals(int64Arr10.get(i), i128Arr.get(i));
        }
        // int256[10]
        codecTest.set_i256_sarr(int256Arr10);
        List i256Arr = codecTest.get_i256_sarr();
        for (int i = 0; i < i256Arr.size(); i++) {
            Assert.assertEquals(int256Arr10.get(i), i256Arr.get(i));
        }
        /// dynamic array
        System.out.println("=============int dynamic array=============");
        // int8[]
        int8Arr10.add(BigInteger.valueOf(10));
        codecTest.set_i8_arr(int8Arr10);
        List i8ArrDyn = codecTest.get_i8_arr();
        for (int i = 0; i < i8ArrDyn.size(); i++) {
            Assert.assertEquals(int8Arr10.get(i), i8ArrDyn.get(i));
        }
        // int128[]
        int64Arr10.add(BigInteger.valueOf(10));
        codecTest.set_i64_arr(int64Arr10);
        List i128ArrDyn = codecTest.get_i64_arr();
        for (int i = 0; i < i128ArrDyn.size(); i++) {
            Assert.assertEquals(int64Arr10.get(i), i128ArrDyn.get(i));
        }
        // int256[]
        int256Arr10.add(BigInteger.valueOf(1234567890L + 10));
        codecTest.set_i256_arr(int256Arr10);
        List i256ArrDyn = codecTest.get_i256_arr();
        for (int i = 0; i < i256ArrDyn.size(); i++) {
            Assert.assertEquals(int256Arr10.get(i), i256ArrDyn.get(i));
        }
    }

    private static org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest getCodecTest() throws ContractException {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient("group0");
        return org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest.deploy(
                        client, client.getCryptoSuite().getCryptoKeyPair(), "codecTest" + new Random().nextInt(10000000));
    }

    @Test
    public void testIntByteArrayType() throws Exception {
        org.fisco.bcos.sdk.v3.test.wasm.liquid.CodecTest codecTest = getCodecTest();

        List<byte[]> bytes1Arr = new ArrayList<>();
        List<byte[]> bytes16Arr = new ArrayList<>();
        List<byte[]> bytes32Arr = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            bytes1Arr.add(Hex.decode("f" + i));
            bytes16Arr.add(Hex.decode("ffffffff1234567890123456fffffff" + i));
            bytes32Arr.add(
                    Hex.decode(
                            "ffffffff1234567890123456fffffff"
                                    + i
                                    + "ffffffff1234567890123456fffffff"
                                    + i));
        }
        /// static array
        System.out.println("=============bytes static array=============");
        // bytes1[2]
        codecTest.set_bytes1_sarr(bytes1Arr);
        List bytes1Arr2 = codecTest.get_bytes1_sarr();
        for (int i = 0; i < bytes1Arr2.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes1Arr.get(i)), Hex.toHexString((byte[]) bytes1Arr2.get(i)));
        }
        // bytes16[2]
        codecTest.set_bytes16_sarr(bytes16Arr);
        List bytes16Arr2 = codecTest.get_bytes16_sarr();
        for (int i = 0; i < bytes16Arr2.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes16Arr.get(i)), Hex.toHexString((byte[]) bytes16Arr2.get(i)));
        }
        // bytes32[2]
        codecTest.set_bytes32_sarr(bytes32Arr);
        List bytes32Arr2 = codecTest.get_bytes32_sarr();
        for (int i = 0; i < bytes32Arr2.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes32Arr.get(i)), Hex.toHexString((byte[]) bytes32Arr2.get(i)));
        }
        // bytes[2]
        codecTest.set_bytes_sarr(bytes32Arr);
        List bytesArr2 = codecTest.get_bytes_sarr();
        for (int i = 0; i < bytesArr2.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes32Arr.get(i)), Hex.toHexString((byte[]) bytesArr2.get(i)));
        }

        /// dynamic array
        System.out.println("=============bytes dynamic array=============");
        // bytes1[]
        bytes1Arr.add(Hex.decode("ff"));
        codecTest.set_bytes1_arr(bytes1Arr);
        List bytes1ArrD = codecTest.get_bytes1_arr();
        for (int i = 0; i < bytes1ArrD.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes1Arr.get(i)), Hex.toHexString((byte[]) bytes1ArrD.get(i)));
        }
        // bytes16[]
        bytes16Arr.add(Hex.decode("ffffffff1234567890123456ffffffff"));
        codecTest.set_bytes16_arr(bytes16Arr);
        List bytes16ArrD = codecTest.get_bytes16_arr();
        for (int i = 0; i < bytes16ArrD.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes16Arr.get(i)), Hex.toHexString((byte[]) bytes16ArrD.get(i)));
        }
        // bytes32[]
        bytes32Arr.add(
                Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
        codecTest.set_bytes32_arr(bytes32Arr);
        List bytes32ArrD = codecTest.get_bytes32_arr();
        for (int i = 0; i < bytes32ArrD.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes32Arr.get(i)), Hex.toHexString((byte[]) bytes32ArrD.get(i)));
        }

        // bytes[]
        bytes32Arr.add(
                Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
        codecTest.set_bytes_arr(bytes32Arr);
        List bytesArrD = codecTest.get_bytes_arr();
        for (int i = 0; i < bytesArrD.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes32Arr.get(i)), Hex.toHexString((byte[]) bytesArrD.get(i)));
        }
    }
}
