package org.fisco.bcos.sdk.v3.test.wasm.transaction.codec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.wasm.liquid.ComplexCodecTest;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CodecComplexTest {
    private static final String CONFIG_FILE =
            "src/integration-wasm-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private final Client client;

    public CodecComplexTest() {
        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        client = sdk.getClient("group0");
    }

    @Test
    public void test1ComplexCodecWithStruct() throws Exception {
        String bytes32Str = "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff";

        DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test"));
        DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
        ComplexCodecTest complexCodecTest =
                ComplexCodecTest.deploy(client, client.getCryptoSuite().getCryptoKeyPair(), "codecTest" + new Random().nextInt(10000000), new ComplexCodecTest.StructA(
                        array, bytes32DynamicArray
                ));

        Assert.assertEquals(complexCodecTest.getDeployReceipt().getStatus(), 0);
        Assert.assertTrue(StringUtils.isNotBlank(complexCodecTest.getContractAddress()));
        // test call get struct
        {
            List<ComplexCodecTest.StructA> structA = complexCodecTest.get_struct_a_without_args();
            Assert.assertEquals(structA.size(), 2);
            Assert.assertEquals(structA.get(0).value_str.get(0), "test");

            List<String> stringList = new ArrayList<>();
            stringList.add("test2");
            List<byte[]> bytes = new ArrayList<>();
            bytes.add(Hex.decode(bytes32Str));
            List<ComplexCodecTest.StructA> structA1 = complexCodecTest.get_struct_a(new ComplexCodecTest.StructA(
                    stringList, bytes
            ));
            Assert.assertEquals(structA1.size(), 2);
            Assert.assertEquals(structA1.get(0).value_str.get(0), "test2");
            Assert.assertEquals(structA1.get(1).value_str.get(0), "test");
            Assert.assertEquals(Hex.toHexString(structA1.get(0).bytes32_in_struct.get(0)), bytes32Str);
        }

        // test struct set and get
        {
            List<byte[]> bytes32DynamicArrays = new ArrayList<>();
            bytes32DynamicArrays.add(Bytes32.DEFAULT.getValue());
            List<String> arrays = new ArrayList<>();
            arrays.add("test2132131");
            List<String> ss = new ArrayList<>();
            ss.add("12312312312");
            TransactionReceipt receipt = complexCodecTest.build_struct_a(ss, bytes32DynamicArrays);
            Tuple1<DynamicArray<ComplexCodecTest.StructA>> buildStructAOutput = complexCodecTest.getBuild_struct_aOutput(receipt);
            Assert.assertEquals(buildStructAOutput.getValue1().getValue().get(0).value_str.get(0), "12312312312");
            Assert.assertEquals(buildStructAOutput.getValue1().getValue().get(0).bytes32_in_struct.size(), 1);


            TransactionReceipt transactionReceipt = complexCodecTest.build_struct_b(new ComplexCodecTest.StructA(arrays, bytes32DynamicArrays));
            Assert.assertEquals(transactionReceipt.getStatus(), 0);
            Tuple2<ComplexCodecTest.StructB, DynamicArray<ComplexCodecTest.StructA>> buildStructBOutput = complexCodecTest.getBuild_struct_bOutput(transactionReceipt);
            Assert.assertEquals(buildStructBOutput.getValue1().a_struct.getValue().size(), 1);
            Assert.assertEquals(buildStructBOutput.getValue2().getValue().size(), 1);
            Assert.assertEquals(buildStructBOutput.getValue1().a_struct.getValue().get(0).value_str.get(0), "test2132131");
            Assert.assertEquals(buildStructBOutput.getValue2().getValue().get(0).value_str.get(0), "test2132131");
        }
    }

    @Test
    public void test1ComplexCodecDynamicBytesArrayArray() throws Exception {
        DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test"));
        DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
        ComplexCodecTest complexCodecTest =
                ComplexCodecTest.deploy(client, client.getCryptoSuite().getCryptoKeyPair(), "codecTest" + new Random().nextInt(10000000), new ComplexCodecTest.StructA(
                        array, bytes32DynamicArray
                ));

        Assert.assertEquals(complexCodecTest.getDeployReceipt().getStatus(), 0);
        Assert.assertTrue(StringUtils.isNotBlank(complexCodecTest.getContractAddress()));

        // test bytes[][] set and get
        {
            byte[] b = Hex.decode("1234");
            List<byte[]> bs = new ArrayList<>();
            bs.add(b);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            TransactionReceipt transactionReceipt = complexCodecTest.set_bytes_array_array(bss);
            Assert.assertEquals(transactionReceipt.getStatus(), 0);
            Tuple1<List<List<byte[]>>> setBytesArrayArrayOutput = complexCodecTest.getSet_bytes_array_arrayOutput(transactionReceipt);
            Assert.assertEquals(Hex.toHexString(setBytesArrayArrayOutput.getValue1().get(0).get(0)), "1234");
        }

        // test bytes32[][] set and get
        {
            byte[] b = org.fisco.bcos.sdk.v3.utils.Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            List<byte[]> bs = new ArrayList<>();
            bs.add(b);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);

            TransactionReceipt transactionReceipt = complexCodecTest.set_bytes32_array_array(bss);
            Assert.assertEquals(transactionReceipt.getStatus(), 0);
            Tuple1<List<List<byte[]>>> setBytes32ArrayArrayOutput = complexCodecTest.getSet_bytes32_array_arrayOutput(transactionReceipt);
            Assert.assertEquals(Hex.toHexString(setBytes32ArrayArrayOutput.getValue1().get(0).get(0)), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
        }
    }

    @Test
    public void test1ComplexCodecWithStaticBytesArrayArray() throws Exception {
        DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test"));
        DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
        ComplexCodecTest complexCodecTest =
                ComplexCodecTest.deploy(client, client.getCryptoSuite().getCryptoKeyPair(), "codecTest" + new Random().nextInt(10000000), new ComplexCodecTest.StructA(
                        array, bytes32DynamicArray
                ));

        Assert.assertEquals(complexCodecTest.getDeployReceipt().getStatus(), 0);
        Assert.assertTrue(StringUtils.isNotBlank(complexCodecTest.getContractAddress()));

        // test bytes[2][] set and get
        {
            byte[] b1 = org.fisco.bcos.sdk.v3.utils.Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ff111111");
            byte[] b2 = Hex.decode("abcdef");
            List<byte[]> bs = new ArrayList<>();
            bs.add(b1);
            bs.add(b2);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            bss.add(bs);

            TransactionReceipt transactionReceipt = complexCodecTest.set_bytes_s_array_array(bss);
            Assert.assertEquals(transactionReceipt.getStatus(), 0);
            Tuple1<List<List<byte[]>>> setBytesStaticArrayArrayOutput = complexCodecTest.getSet_bytes_s_array_arrayOutput(transactionReceipt);
            Assert.assertEquals(Hex.toHexString(setBytesStaticArrayArrayOutput.getValue1().get(0).get(0)), "ffffffff1234567890123456ffffffffffffffff1234567890123456ff111111");
        }


        // test bytes32[2][] set and get
        {
            byte[] b1 = org.fisco.bcos.sdk.v3.utils.Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            byte[] b2 = org.fisco.bcos.sdk.v3.utils.Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            List<byte[]> bs = new ArrayList<>();
            bs.add(b1);
            bs.add(b2);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);

            TransactionReceipt transactionReceipt = complexCodecTest.set_bytes32_s_array_array(bss);
            Assert.assertEquals(transactionReceipt.getStatus(), 0);
            Tuple1<List<List<byte[]>>> setBytes32StaticArrayArrayOutput = complexCodecTest.getSet_bytes32_s_array_arrayOutput(transactionReceipt);
            Assert.assertEquals(Hex.toHexString(setBytes32StaticArrayArrayOutput.getValue1().get(0).get(0)), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
        }
    }

    @Test
    public void test1ComplexCodecWithStaticStruct() throws Exception {

        DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test"));
        DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
        ComplexCodecTest complexCodecTest =
                ComplexCodecTest.deploy(client, client.getCryptoSuite().getCryptoKeyPair(), "codecTest" + new Random().nextInt(10000000), new ComplexCodecTest.StructA(
                        array, bytes32DynamicArray
                ));

        Assert.assertEquals(complexCodecTest.getDeployReceipt().getStatus(), 0);
        Assert.assertTrue(StringUtils.isNotBlank(complexCodecTest.getContractAddress()));

        // test static struct set and get
        {
            List<BigInteger> staticArray = new ArrayList<>();
            staticArray.add(BigInteger.ONE);
            // use static struct params, get single struct
            //FIXME: static struct not support
//            TransactionReceipt transactionReceipt = complexCodecTest.build_static_struct(
//                    new ComplexCodecTest.StaticStruct(BigInteger.valueOf(-128), BigInteger.valueOf(256), staticArray));
//            Assert.assertEquals(transactionReceipt.getStatus(), 0);
//            Tuple1<ComplexCodecTest.StaticStruct> buildStaticStructTupleOutput = complexCodecTest.getBuildStaticStructTupleOutput(transactionReceipt);
//            Assert.assertEquals(buildStaticStructTupleOutput.getValue1().b1.size(), 1);
//            Assert.assertEquals(buildStaticStructTupleOutput.getValue1().b1.get(0), BigInteger.ONE);
//            Assert.assertEquals(buildStaticStructTupleOutput.getValue1().i1, BigInteger.valueOf(-127));
//            Assert.assertEquals(buildStaticStructTupleOutput.getValue1().u1, BigInteger.valueOf(257));

            // use number params, get static struct list
            TransactionReceipt transactionReceipt1 =
                    complexCodecTest.build_static_struct(BigInteger.valueOf(-256), BigInteger.TEN);
            Assert.assertEquals(transactionReceipt1.getStatus(), 0);
            Tuple1<DynamicArray<ComplexCodecTest.StaticStruct>> buildStaticStructInt128Uint128Output = complexCodecTest.getBuild_static_structOutput(transactionReceipt1);
            Assert.assertEquals(buildStaticStructInt128Uint128Output.getValue1().getValue().size(), 2);
            Assert.assertEquals(buildStaticStructInt128Uint128Output.getValue1().getValue().get(0).b1.size(), 1);
        }
    }
}
