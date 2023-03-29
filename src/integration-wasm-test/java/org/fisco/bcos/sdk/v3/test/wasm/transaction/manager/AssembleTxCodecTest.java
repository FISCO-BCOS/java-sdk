package org.fisco.bcos.sdk.v3.test.wasm.transaction.manager;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int128;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.StaticArray2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint128;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssembleTxCodecTest {
    private static final String CONFIG_FILE =
            "src/integration-wasm-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String ABI_FILE = "src/integration-wasm-test/resources/abi/";
    private static final String BIN_FILE = "src/integration-wasm-test/resources/bin/";

    private final String complexCodecTest = "complex_codec_test";

    // group
    private final Client client;
    private final CryptoKeyPair cryptoKeyPair;

    private final AssembleTransactionProcessor transactionProcessor;

    public AssembleTxCodecTest() throws IOException {
        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        client = sdk.getClient("group0");
        cryptoKeyPair = this.client.getCryptoSuite().getCryptoKeyPair();
        transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
    }

    @Test
    public void test1ComplexCodecWithType() throws Exception {
        // test deploy with struct
        List<Object> deployParams = new ArrayList<>();
        {
            DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test"));
            DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
            DynamicStruct structA = new DynamicStruct(array, bytes32DynamicArray);
            deployParams.add(structA);
        }
        TransactionResponse response =
                transactionProcessor.deployByContractLoader(complexCodecTest, deployParams, "complex_codec_test" + System.currentTimeMillis());
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call get struct
        {
            // not params method
            CallResponse callResponse1 =
                    transactionProcessor.sendCallByContractLoader(
                            complexCodecTest, contractAddress, "get_struct_a_without_args", new ArrayList<>());
            List<Object> returnObject = callResponse1.getReturnObject();
            List<Type> results = callResponse1.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof DynamicStruct);
            System.out.println(JsonUtils.toJson(returnObject));

            List<Object> callParams = new ArrayList<>();
            DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test3125643123"));
            DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
            DynamicStruct structA = new DynamicStruct(array, bytes32DynamicArray);
            callParams.add(structA);
            CallResponse callResponse2 = transactionProcessor.sendCallByContractLoader(
                    complexCodecTest, contractAddress, "get_struct_a", callParams);

            returnObject = callResponse2.getReturnObject();
            results = callResponse2.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof DynamicStruct);
            System.out.println("get_struct_a:");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[][] set and get
        {
            List<Object> params = new ArrayList<>();
            DynamicBytes b = new DynamicBytes("1234".getBytes());
            DynamicArray<DynamicBytes> bs = new DynamicArray<>(DynamicBytes.class, b);
            DynamicArray<DynamicArray<DynamicBytes>> bbs = new DynamicArray<>(bs);
            params.add(bbs);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "set_bytes_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), Hex.toHexString("1234".getBytes()));
            System.out.println("set_bytes_array_array, bytes[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[][] set and get
        {
            List<Object> params = new ArrayList<>();
            Bytes32 b = new Bytes32(Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
            DynamicArray<Bytes32> bs = new DynamicArray<>(Bytes32.class, b);
            DynamicArray<DynamicArray<Bytes32>> bbs = new DynamicArray<>(bs);
            params.add(bbs);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "set_bytes32_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes32) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("set_bytes32_array_array, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        //FIXME: bytes[2][]
//        // test bytes[2][] set and get
//        {
//            List<Object> params = new ArrayList<>();
//            DynamicBytes b1 = new DynamicBytes(Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
//            DynamicBytes b2 = DynamicBytes.DEFAULT;
//            StaticArray2<DynamicBytes> bs = new StaticArray2<>(DynamicBytes.class, b1, b2);
//            DynamicArray<StaticArray2<DynamicBytes>> bbs = new DynamicArray<>(bs);
//            params.add(bbs);
//
//            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
//                    complexCodecTest, contractAddress, "set_bytes_s_array_array", params);
//            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
//            List<Type> results = transactionResponse.getResults();
//            List<Object> returnObject = transactionResponse.getReturnObject();
//            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
//            Assert.assertEquals(results.size(), 1);
//            Assert.assertEquals(returnObject.size(), 1);
//            Assert.assertEquals(returnABIObject.size(), 1);
//            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
//            System.out.println("set_bytes_s_array_array, bytes[2][]");
//            System.out.println(JsonUtils.toJson(returnObject));
//        }

        // test bytes32[2][] set and get
        {
            List<Object> params = new ArrayList<>();
            Bytes32 b1 = new Bytes32(Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
            Bytes32 b2 = Bytes32.DEFAULT;
            StaticArray<Bytes32> bs = new StaticArray<>(Bytes32.class, b1, b2);
            DynamicArray<StaticArray<Bytes32>> bbs = new DynamicArray<>(bs);
            params.add(bbs);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "set_bytes32_s_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes32) ((StaticArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("set_bytes32_s_array_array, bytes32[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test struct set and get
        {
            List<Object> params = new ArrayList<>();
            DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test2132131"));
            DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
            DynamicStruct structA = new DynamicStruct(array, bytes32DynamicArray);
            params.add(structA);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "build_struct_b", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 2);
            Assert.assertEquals(returnObject.size(), 2);
            Assert.assertEquals(returnABIObject.size(), 2);
            System.out.println("build_struct_b, StructB, StructA[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test static struct set and get
        {
            // use number params, get static struct list
            List<Object> params2 = new ArrayList<>();
            params2.add(new Int128(-256));
            params2.add(new Uint128(288));
            TransactionResponse transactionResponse2 = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "build_static_struct", params2);
            Assert.assertEquals(transactionResponse2.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse2.getResults();
            List<Object> returnObject = transactionResponse2.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse2.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test call get static struct
        {
            // not params method
            CallResponse callResponse1 =
                    transactionProcessor.sendCallByContractLoader(
                            complexCodecTest, contractAddress, "get_static_struct_without_args", new ArrayList<>());
            List<Object> returnObject = callResponse1.getReturnObject();
            List<Type> results = callResponse1.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof StaticStruct);
            System.out.println(JsonUtils.toJson(returnObject));

            List<Object> callParams = new ArrayList<>();
            StaticStruct staticStruct = new StaticStruct(new Int128(-128), new Uint128(128), new StaticArray<>(Int32.class, 1, new Int32(1)));
            callParams.add(staticStruct);
            CallResponse callResponse2 = transactionProcessor.sendCallByContractLoader(
                    complexCodecTest, contractAddress, "get_static_struct", callParams);

            returnObject = callResponse2.getReturnObject();
            results = callResponse2.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof StaticStruct);
            System.out.println("get_struct_a:");
            System.out.println(JsonUtils.toJson(returnObject));
        }
    }

    @Test
    public void test2ComplexCodecWithStringParams() throws Exception {
        // test deploy with struct
        List<String> deployParams = new ArrayList<>();

        deployParams.add("[[\"test\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");
        String abi = transactionProcessor.getContractLoader().getABIByContractName(complexCodecTest);
        String bin = transactionProcessor.getContractLoader().getBinaryByContractName(complexCodecTest);
        TransactionResponse response = transactionProcessor.deployAndGetResponseWithStringParams(abi, bin, deployParams, "codec_test" + System.currentTimeMillis());
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call send struct get struct
        {
            List<String> callParams = new ArrayList<>();
            // use no params method
            CallResponse callResponse1 = transactionProcessor.sendCallWithStringParams("", contractAddress, abi, "get_struct_a_without_args", callParams);
            List<Object> returnObject = callResponse1.getReturnObject();
            List<Type> results = callResponse1.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof DynamicStruct);
            System.out.println(JsonUtils.toJson(returnObject));

            // use one params method
            callParams.add("[[\"test2312312312312\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");
            CallResponse callResponse2 = transactionProcessor.sendCallWithStringParams("", contractAddress, abi, "get_struct_a", callParams);
            results = callResponse2.getResults();
            returnObject = callResponse2.getReturnObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof DynamicStruct);
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"0xabcd\"],[\"0x1234\"]]");
            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(contractAddress, abi, "set_bytes_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "abcd");
        }

        // test bytes32[][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                    contractAddress, abi, "set_bytes32_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes32) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("set_bytes32_array_array, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // FIXME: bytes[2][]
        // test bytes[2][] set and get
//        {
//            List<String> params = new ArrayList<>();
//            params.add("[[\"0xabcdef\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0x1234\"]]");
//
//            TransactionResponse transactionResponse = transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
//                    contractAddress, abi, "set_bytes_s_array_array", params);
//            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
//            List<Type> results = transactionResponse.getResults();
//            List<Object> returnObject = transactionResponse.getReturnObject();
//            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
//            Assert.assertEquals(results.size(), 1);
//            Assert.assertEquals(returnObject.size(), 1);
//            Assert.assertEquals(returnABIObject.size(), 1);
//            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "abcdef");
//            System.out.println("set_bytes_s_array_array, bytes[2][]");
//            System.out.println(JsonUtils.toJson(returnObject));
//        }

        // test bytes32[2][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"0x1234567890123456789012345678901234567890123456789012345678901234\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0x1234567890123456789012345678901234567890123456789012345678901234\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0x1234567890123456789012345678901234567890123456789012345678901234\"]]");
            TransactionResponse transactionResponse = transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                    contractAddress, abi, "set_bytes32_s_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes32) ((StaticArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "1234567890123456789012345678901234567890123456789012345678901234");
            System.out.println("set_bytes32_s_array_array, bytes32[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test struct set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"12312314565456345test\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                    contractAddress, abi, "build_struct_b", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 2);
            Assert.assertEquals(returnObject.size(), 2);
            Assert.assertEquals(returnABIObject.size(), 2);
            System.out.println("build_struct_b, StructB, StructA[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test static struct set and get
        {
            // use number params, get static struct list

            List<String> params2 = new ArrayList<>();
            params2.add("256");
            params2.add("12321421");
            TransactionResponse transactionResponse2 = transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                    contractAddress, abi, "build_static_struct", params2);
            Assert.assertEquals(transactionResponse2.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse2.getResults();
            List<Object> returnObject = transactionResponse2.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse2.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));

        }

        // test call send struct get struct
        {
            List<String> callParams = new ArrayList<>();
            // use no params method
            CallResponse callResponse1 = transactionProcessor.sendCallWithStringParams("", contractAddress, abi, "get_static_struct_without_args", callParams);
            List<Object> returnObject = callResponse1.getReturnObject();
            List<Type> results = callResponse1.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof StaticStruct);
            System.out.println(JsonUtils.toJson(returnObject));

            // use one params method
            callParams.add("[-128,128,[-32]]");
            CallResponse callResponse2 = transactionProcessor.sendCallWithStringParams("", contractAddress, abi, "get_static_struct", callParams);
            results = callResponse2.getResults();
            returnObject = callResponse2.getReturnObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof StaticStruct);
            System.out.println(JsonUtils.toJson(returnObject));
        }
    }

    @Test
    public void test1ComplexCodecWithJavaObject() throws Exception {
        // test deploy with struct
        List<Object> deployParams = new ArrayList<>();
        {
            //    struct StructA {
            //        string[] value_str;
            //        bytes32[] bytes32_in_struct;
            //    }
            List<String> array = new ArrayList<>();
            array.add("test");
            List<byte[]> bytes = new ArrayList<>();
            byte[] b = Bytes32.DEFAULT.getValue();
            bytes.add(b);
            List<Object> structA = new ArrayList<>();
            structA.add(array);
            structA.add(bytes);
            deployParams.add(structA);
        }
        TransactionResponse response =
                transactionProcessor.deployByContractLoader(complexCodecTest, deployParams, "complex_codec_test" + System.currentTimeMillis());
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call get struct
        {
            // not params method
            CallResponse callResponse1 =
                    transactionProcessor.sendCallByContractLoader(
                            complexCodecTest, contractAddress, "get_struct_a_without_args", new ArrayList<>());
            List<Object> returnObject = callResponse1.getReturnObject();
            List<Type> results = callResponse1.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof DynamicStruct);
            System.out.println(JsonUtils.toJson(returnObject));

            //    struct StructA {
            //        string[] value_str;
            //        bytes32[] bytes32_in_struct;
            //    }
            List<Object> callParams = new ArrayList<>();
            List<String> array = new ArrayList<>();
            array.add("test31241233123");
            List<byte[]> bytes = new ArrayList<>();
            byte[] b = Bytes32.DEFAULT.getValue();
            byte[] b2 = Bytes32.DEFAULT.getValue();
            bytes.add(b);
            bytes.add(b2);
            List<Object> structA = new ArrayList<>();
            structA.add(array);
            structA.add(bytes);
            callParams.add(structA);
            CallResponse callResponse2 = transactionProcessor.sendCallByContractLoader(
                    complexCodecTest, contractAddress, "get_struct_a", callParams);

            returnObject = callResponse2.getReturnObject();
            results = callResponse2.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof DynamicStruct);
            System.out.println("get_struct_a:");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[][] set and get
        {
            List<Object> params = new ArrayList<>();
            byte[] b = "1234".getBytes();
            List<byte[]> bs = new ArrayList<>();
            bs.add(b);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "set_bytes_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), Hex.toHexString("1234".getBytes()));
            System.out.println("set_bytes_array_array, bytes[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[][] set and get
        {
            List<Object> params = new ArrayList<>();
            byte[] b = Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            List<byte[]> bs = new ArrayList<>();
            bs.add(b);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "set_bytes32_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes32) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("set_bytes32_array_array, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // FIXME: bytes[2][]
        // test bytes[2][] set and get
        {
//            List<Object> params = new ArrayList<>();
//            byte[] b1 = Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
//            byte[] b2 = DynamicBytes.DEFAULT.getValue();
//            List<byte[]> bs = new ArrayList<>();
//            bs.add(b1);
//            bs.add(b2);
//            List<List<byte[]>> bss = new ArrayList<>();
//            bss.add(bs);
//            params.add(bss);
//
//            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
//                    complexCodecTest, contractAddress, "set_bytes_s_array_array", params);
//            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
//            List<Type> results = transactionResponse.getResults();
//            List<Object> returnObject = transactionResponse.getReturnObject();
//            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
//            Assert.assertEquals(results.size(), 1);
//            Assert.assertEquals(returnObject.size(), 1);
//            Assert.assertEquals(returnABIObject.size(), 1);
//            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
//            System.out.println("set_bytes_s_array_array, bytes[2][]");
//            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[2][] set and get
        {
            List<Object> params = new ArrayList<>();
            byte[] b1 = Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            byte[] b2 = Bytes32.DEFAULT.getValue();
            List<byte[]> bs = new ArrayList<>();
            bs.add(b1);
            bs.add(b2);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "set_bytes32_s_array_array", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes32) ((StaticArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("set_bytes32_s_array_array, bytes32[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test struct set and get
        {
            List<Object> params = new ArrayList<>();
            List<String> array = new ArrayList<>();
            array.add("test2132131");
            List<byte[]> bytes32DynamicArray = new ArrayList<>();
            bytes32DynamicArray.add(Bytes32.DEFAULT.getValue());
            List<Object> structA = new ArrayList<>();
            structA.add(array);
            structA.add(bytes32DynamicArray);
            params.add(structA);

            TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "build_struct_b", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 2);
            Assert.assertEquals(returnObject.size(), 2);
            Assert.assertEquals(returnABIObject.size(), 2);
            System.out.println("build_struct_b, StructB, StructA[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test static struct set and get
        {
            // use number params, get static struct list
            List<Object> params2 = new ArrayList<>();
            params2.add(-256);
            params2.add(288);
            TransactionResponse transactionResponse2 = transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                    complexCodecTest, contractAddress, "build_static_struct", params2);
            Assert.assertEquals(transactionResponse2.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse2.getResults();
            List<Object> returnObject = transactionResponse2.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse2.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("build_static_struct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test call get static struct
        {
            // not params method
            CallResponse callResponse1 =
                    transactionProcessor.sendCallByContractLoader(
                            complexCodecTest, contractAddress, "get_static_struct_without_args", new ArrayList<>());
            List<Object> returnObject = callResponse1.getReturnObject();
            List<Type> results = callResponse1.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof StaticStruct);
            System.out.println(JsonUtils.toJson(returnObject));

            List<Object> callParams = new ArrayList<>();
            StaticStruct staticStruct = new StaticStruct(new Int128(-128), new Uint128(128), new StaticArray<>(Int32.class, 1, new Int32(1)));
            callParams.add(staticStruct);
            CallResponse callResponse2 = transactionProcessor.sendCallByContractLoader(
                    complexCodecTest, contractAddress, "get_static_struct", callParams);

            returnObject = callResponse2.getReturnObject();
            results = callResponse2.getResults();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            Assert.assertTrue(results.get(0) instanceof DynamicArray);
            Assert.assertTrue(((DynamicArray<?>) results.get(0)).getValue().get(0) instanceof StaticStruct);
            System.out.println("get_struct_a:");
            System.out.println(JsonUtils.toJson(returnObject));
        }
    }
}
