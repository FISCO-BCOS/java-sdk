package org.fisco.bcos.sdk.transaction.codec;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.Constant;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CodecTest {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    @Test
    public void test1NumericType() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(1);
        org.fisco.bcos.sdk.contract.CodecTest codecTest =
                org.fisco.bcos.sdk.contract.CodecTest.deploy(
                        client, client.getCryptoSuite().getCryptoKeyPair());
        // string
        TransactionReceipt receipt = codecTest.set("Test test");
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        String s = codecTest.get();
        Assert.assertEquals("Test test", s);
        // u8
        receipt = codecTest.setU8(BigInteger.TEN);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());

        BigInteger u8 = codecTest.getU8();
        Assert.assertEquals(BigInteger.TEN, u8);

        // u128
        // 2^63-1 * 2^63-1
        receipt = codecTest.setU128(Constant.MAX_UINT128);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        BigInteger u128 = codecTest.getU128();
        Assert.assertEquals(0, Constant.MAX_UINT128.compareTo(u128));
        // u256
        receipt = codecTest.setU256(Constant.MAX_UINT256);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        BigInteger u256 = codecTest.getU256();
        Assert.assertEquals(Constant.MAX_UINT256, u256);
        // i8
        receipt = codecTest.setI8(BigInteger.valueOf(-1));
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        BigInteger i8 = codecTest.getI8();
        Assert.assertEquals(BigInteger.valueOf(-1), i8);
        // i128
        receipt = codecTest.setI128(Constant.MIN_INT128);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        BigInteger i128 = codecTest.getI128();
        Assert.assertEquals(0, Constant.MIN_INT128.compareTo(i128));

        receipt = codecTest.setI128(Constant.MAX_INT128);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        BigInteger maxI128 = codecTest.getI128();
        Assert.assertEquals(0, Constant.MAX_INT128.compareTo(maxI128));
        // i256
        receipt = codecTest.setI256(BigInteger.valueOf(-123456789));
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        BigInteger i256 = codecTest.getI256();
        Assert.assertEquals(-123456789, i256.longValue());
    }

    @Test
    public void test2ByteType() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(1);
        org.fisco.bcos.sdk.contract.CodecTest codecTest =
                org.fisco.bcos.sdk.contract.CodecTest.deploy(
                        client, client.getCryptoSuite().getCryptoKeyPair());

        // address
        TransactionReceipt receipt =
                codecTest.setAddress("0x0000000000000000000000000000000000000000");
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        String address = codecTest.getAddress();
        Assert.assertEquals("0x0000000000000000000000000000000000000000", address);
        // bytes1
        receipt = codecTest.setBytes1(Hex.decode("ff"));
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        byte[] bytes1 = codecTest.getBytes1();
        Assert.assertEquals("ff", Hex.toHexString(bytes1));
        // bytes16
        String bytes16Str = "ffffffff1234567890123456ffffffff";
        receipt = codecTest.setBytes16(Hex.decode(bytes16Str));
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        byte[] bytes16 = codecTest.getBytes16();
        Assert.assertEquals(bytes16Str, Hex.toHexString(bytes16));
        // bytes32
        receipt = codecTest.setBytes32(Hex.decode(bytes16Str + bytes16Str));
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        byte[] bytes32 = codecTest.getBytes32();
        Assert.assertEquals(bytes16Str + bytes16Str, Hex.toHexString(bytes32));
        // bytes
        receipt = codecTest.setBytes(Hex.decode(bytes16Str + bytes16Str + bytes16Str));
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        byte[] bytes = codecTest.getBytes();
        Assert.assertEquals(bytes16Str + bytes16Str + bytes16Str, Hex.toHexString(bytes));
    }

    @Test
    public void test3UintNumericArrayType() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(1);
        org.fisco.bcos.sdk.contract.CodecTest codecTest =
                org.fisco.bcos.sdk.contract.CodecTest.deploy(
                        client, client.getCryptoSuite().getCryptoKeyPair());

        List<BigInteger> uint8Arr10 = new ArrayList<>();
        List<BigInteger> uint128Arr10 = new ArrayList<>();
        List<BigInteger> uint256Arr10 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            uint8Arr10.add(BigInteger.valueOf(i));
            uint128Arr10.add(Constant.MAX_UINT128.subtract(BigInteger.valueOf(i)));
            uint256Arr10.add(Constant.MAX_UINT256.subtract(BigInteger.valueOf(i)));
        }
        /// static array
        System.out.println("=============uint static array=============");
        // uint8[10]
        TransactionReceipt receipt = codecTest.setU8Arr(uint8Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List u8Arr = codecTest.getU8Arr();
        for (int i = 0; i < u8Arr.size(); i++) {
            Assert.assertEquals(uint8Arr10.get(i), u8Arr.get(i));
        }
        // uint128[10]
        receipt = codecTest.setU128Arr(uint128Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List u128Arr = codecTest.getU128Arr();
        for (int i = 0; i < u128Arr.size(); i++) {
            Assert.assertEquals(uint128Arr10.get(i), u128Arr.get(i));
        }
        // uint256[10]
        receipt = codecTest.setU256Arr(uint256Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List u256Arr = codecTest.getU256Arr();
        for (int i = 0; i < u256Arr.size(); i++) {
            Assert.assertEquals(uint256Arr10.get(i), u256Arr.get(i));
        }
        /// dynamic array
        System.out.println("=============uint dynamic array=============");
        // uint8[]
        uint8Arr10.add(BigInteger.valueOf(10));
        receipt = codecTest.setU8ArrDyn(uint8Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List u8ArrDyn = codecTest.getU8ArrDyn();
        for (int i = 0; i < u8ArrDyn.size(); i++) {
            Assert.assertEquals(uint8Arr10.get(i), u8ArrDyn.get(i));
        }
        // uint128[]
        uint128Arr10.add(Constant.MAX_UINT128.subtract(BigInteger.valueOf(10)));
        receipt = codecTest.setU128ArrDyn(uint128Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List u128ArrDyn = codecTest.getU128ArrDyn();
        for (int i = 0; i < u128ArrDyn.size(); i++) {
            Assert.assertEquals(uint128Arr10.get(i), u128ArrDyn.get(i));
        }
        // uint256[]
        uint256Arr10.add(Constant.MAX_UINT256.subtract(BigInteger.valueOf(10)));
        receipt = codecTest.setU256ArrDyn(uint256Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List u256ArrDyn = codecTest.getU256ArrDyn();
        for (int i = 0; i < u256ArrDyn.size(); i++) {
            Assert.assertEquals(uint256Arr10.get(i), u256ArrDyn.get(i));
        }
    }

    @Test
    public void test4IntNumericArrayType() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(1);
        org.fisco.bcos.sdk.contract.CodecTest codecTest =
                org.fisco.bcos.sdk.contract.CodecTest.deploy(
                        client, client.getCryptoSuite().getCryptoKeyPair());

        List<BigInteger> int8Arr10 = new ArrayList<>();
        List<BigInteger> int128Arr10 = new ArrayList<>();
        List<BigInteger> int256Arr10 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int8Arr10.add(BigInteger.valueOf(i));
            int128Arr10.add(Constant.MAX_INT128.subtract(BigInteger.valueOf(i)));
            int256Arr10.add(BigInteger.valueOf(1234567890L + i));
        }
        /// static array
        System.out.println("=============int static array=============");
        // int8[10]
        TransactionReceipt receipt = codecTest.setI8Arr(int8Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List i8Arr = codecTest.getI8Arr();
        for (int i = 0; i < i8Arr.size(); i++) {
            Assert.assertEquals(int8Arr10.get(i), i8Arr.get(i));
        }
        // int128[10]
        receipt = codecTest.setI128Arr(int128Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List i128Arr = codecTest.getI128Arr();
        for (int i = 0; i < i128Arr.size(); i++) {
            Assert.assertEquals(int128Arr10.get(i), i128Arr.get(i));
        }
        // int256[10]
        receipt = codecTest.setI256Arr(int256Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List i256Arr = codecTest.getI256Arr();
        for (int i = 0; i < i256Arr.size(); i++) {
            Assert.assertEquals(int256Arr10.get(i), i256Arr.get(i));
        }
        /// dynamic array
        System.out.println("=============int dynamic array=============");
        // int8[]
        int8Arr10.add(BigInteger.valueOf(10));
        receipt = codecTest.setI8ArrDyn(int8Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List i8ArrDyn = codecTest.getI8ArrDyn();
        for (int i = 0; i < i8ArrDyn.size(); i++) {
            Assert.assertEquals(int8Arr10.get(i), i8ArrDyn.get(i));
        }
        // int128[]
        int128Arr10.add(Constant.MAX_INT128.subtract(BigInteger.valueOf(10)));
        receipt = codecTest.setI128ArrDyn(int128Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List i128ArrDyn = codecTest.getI128ArrDyn();
        for (int i = 0; i < i128ArrDyn.size(); i++) {
            Assert.assertEquals(int128Arr10.get(i), i128ArrDyn.get(i));
        }
        // int256[]
        int256Arr10.add(BigInteger.valueOf(1234567890L + 10));
        receipt = codecTest.setI256ArrDyn(int256Arr10);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List i256ArrDyn = codecTest.getI256ArrDyn();
        for (int i = 0; i < i256ArrDyn.size(); i++) {
            Assert.assertEquals(int256Arr10.get(i), i256ArrDyn.get(i));
        }
    }

    @Test
    public void test5IntByteArrayType() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(1);
        org.fisco.bcos.sdk.contract.CodecTest codecTest =
                org.fisco.bcos.sdk.contract.CodecTest.deploy(
                        client, client.getCryptoSuite().getCryptoKeyPair());

        List<byte[]> bytes1Arr = new ArrayList<>();
        List<byte[]> bytes16Arr = new ArrayList<>();
        List<byte[]> bytes32Arr = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
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
        // bytes1[10]
        TransactionReceipt receipt = codecTest.setBytes1Arr10(bytes1Arr);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List<byte[]> bytes1Arr10 = codecTest.getBytes1Arr10();
        for (int i = 0; i < bytes1Arr10.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes1Arr.get(i)), Hex.toHexString(bytes1Arr10.get(i)));
        }
        // bytes16[10]
        receipt = codecTest.setBytes16Arr10(bytes16Arr);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());
        List<byte[]> bytes16Arr10 = codecTest.getBytes16Arr10();
        for (int i = 0; i < bytes16Arr10.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes16Arr.get(i)), Hex.toHexString(bytes16Arr10.get(i)));
        }
        // bytes32[10]
        receipt = codecTest.setBytes32Arr10(bytes32Arr);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());

        List<byte[]> bytes32Arr10 = codecTest.getBytes32Arr10();
        for (int i = 0; i < bytes32Arr10.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes32Arr.get(i)), Hex.toHexString(bytes32Arr10.get(i)));
        }

        /// dynamic array
        System.out.println("=============bytes dynamic array=============");
        // bytes1[]
        bytes1Arr.add(Hex.decode("ff"));
        receipt = codecTest.setBytes1Arr(bytes1Arr);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());

        List<byte[]> bytes1ArrD = codecTest.getBytes1Arr();
        for (int i = 0; i < bytes1ArrD.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes1Arr.get(i)), Hex.toHexString(bytes1ArrD.get(i)));
        }
        // bytes16[]
        bytes16Arr.add(Hex.decode("ffffffff1234567890123456ffffffff"));
        receipt = codecTest.setBytes16Arr(bytes16Arr);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());

        List<byte[]> bytes16ArrD = codecTest.getBytes16Arr();
        for (int i = 0; i < bytes16ArrD.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes16Arr.get(i)), Hex.toHexString(bytes16ArrD.get(i)));
        }
        // bytes32[]
        bytes32Arr.add(
                Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
        receipt = codecTest.setBytes32Arr(bytes32Arr);
        Assert.assertEquals(receipt.getStatus(), "0x0");
        System.out.println(receipt.getOutput());

        List<byte[]> bytes32ArrD = codecTest.getBytes32Arr();
        for (int i = 0; i < bytes32Arr10.size(); i++) {
            Assert.assertEquals(
                    Hex.toHexString(bytes32Arr.get(i)), Hex.toHexString(bytes32ArrD.get(i)));
        }
    }
}
