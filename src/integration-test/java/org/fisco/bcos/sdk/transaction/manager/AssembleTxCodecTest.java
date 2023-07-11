package org.fisco.bcos.sdk.transaction.manager;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.*;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssembleTxCodecTest {
    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String ABI_FILE = "src/integration-test/resources/abi/";
    private static final String BIN_FILE = "src/integration-test/resources/bin/";

    private static final String COMPLEX_CODEC_TEST = "ComplexCodecTest";

    private final AssembleTransactionProcessor transactionProcessor;

    public AssembleTxCodecTest() throws Exception {
        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        // group
        Client client = sdk.getClient(1);
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        client, cryptoKeyPair, ABI_FILE, BIN_FILE);
    }

    @Test
    public void test2ComplexCodecWithStringParams() throws Exception {
        // test deploy with struct
        List<String> deployParams = new ArrayList<>();

        deployParams.add(
                "[[\"test\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");
        String abi =
                transactionProcessor.getContractLoader().getABIByContractName(COMPLEX_CODEC_TEST);
        String bin =
                transactionProcessor
                        .getContractLoader()
                        .getBinaryByContractName(COMPLEX_CODEC_TEST);
        TransactionResponse response =
                transactionProcessor.deployAndGetResponseWithStringParams(abi, bin, deployParams);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), "0x0");
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call send struct get struct
        {
            List<String> callParams = new ArrayList<>();
            // use no params method
            CallResponse callResponse1 =
                    transactionProcessor.sendCallWithStringParams(
                            "0xf6f0484c6706bda3801a2713c31a883caab05e01",
                            contractAddress,
                            abi,
                            "getStructA",
                            callParams);
            List<Object> returnObject = callResponse1.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            System.out.println(JsonUtils.toJson(returnObject));

            // use one params method
            callParams.add(
                    "[[\"test2312312312312\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");
            CallResponse callResponse2 =
                    transactionProcessor.sendCallWithStringParams(
                            "0xf6f0484c6706bda3801a2713c31a883caab05e01",
                            contractAddress,
                            abi,
                            "getStructA",
                            callParams);
            returnObject = callResponse2.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"0xabcd\"],[\"0x1234\"]]");
            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, abi, "setBytesArrayArray", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
        }

        // test bytes32[][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add(
                    "[[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, abi, "setBytes32ArrayArray", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("setBytes32ArrayArray, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[2][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add(
                    "[[\"0xabcdef\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0x1234\"]]");

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, abi, "setBytesStaticArrayArray", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("setBytesStaticArrayArray, bytes[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[2][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add(
                    "[[\"0x1234567890123456789012345678901234567890123456789012345678901234\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0x1234567890123456789012345678901234567890123456789012345678901234\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0x1234567890123456789012345678901234567890123456789012345678901234\"]]");
            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, abi, "setBytes32StaticArrayArray", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("setBytes32StaticArrayArray, bytes32[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test struct set and get
        {
            List<String> params = new ArrayList<>();
            params.add(
                    "[[\"12312314565456345test\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, abi, "buildStructB", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 2);
            Assert.assertEquals(returnABIObject.size(), 2);
            System.out.println("buildStructB, StructB, StructA[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test static struct set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[-128,129,[32]]");
            // use static struct params, get single struct

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, abi, "buildStaticStruct", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
            // use number params, get static struct list

            List<String> params2 = new ArrayList<>();
            params2.add("-256");
            params2.add("12321421");
            TransactionResponse transactionResponse2 =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, abi, "buildStaticStruct", params2);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            returnObject = transactionResponse2.getReturnObject();
            returnABIObject = transactionResponse2.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
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
                transactionProcessor.deployByContractLoader(COMPLEX_CODEC_TEST, deployParams);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), "0x0");
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call get struct
        {
            // not params method
            CallResponse callResponse1 =
                    transactionProcessor.sendCallByContractLoader(
                            COMPLEX_CODEC_TEST, contractAddress, "getStructA", new ArrayList<>());
            List<Object> returnObject = callResponse1.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
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
            CallResponse callResponse2 =
                    transactionProcessor.sendCallByContractLoader(
                            COMPLEX_CODEC_TEST, contractAddress, "getStructA", callParams);

            returnObject = callResponse2.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            System.out.println("getStructA:");
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

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            COMPLEX_CODEC_TEST, contractAddress, "setBytesArrayArray", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("setBytesArrayArray, bytes[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[][] set and get
        {
            List<Object> params = new ArrayList<>();
            byte[] b =
                    Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            List<byte[]> bs = new ArrayList<>();
            bs.add(b);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            COMPLEX_CODEC_TEST, contractAddress, "setBytes32ArrayArray", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("setBytes32ArrayArray, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[2][] set and get
        {
            List<Object> params = new ArrayList<>();
            byte[] b1 =
                    Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            byte[] b2 = DynamicBytes.DEFAULT.getValue();
            List<byte[]> bs = new ArrayList<>();
            bs.add(b1);
            bs.add(b2);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            COMPLEX_CODEC_TEST,
                            contractAddress,
                            "setBytesStaticArrayArray",
                            params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("setBytesStaticArrayArray, bytes[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[2][] set and get
        {
            List<Object> params = new ArrayList<>();
            byte[] b1 =
                    Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            byte[] b2 = Bytes32.DEFAULT.getValue();
            List<byte[]> bs = new ArrayList<>();
            bs.add(b1);
            bs.add(b2);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            COMPLEX_CODEC_TEST,
                            contractAddress,
                            "setBytes32StaticArrayArray",
                            params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("setBytes32StaticArrayArray, bytes32[2][]");
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

            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            COMPLEX_CODEC_TEST, contractAddress, "buildStructB", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 2);
            Assert.assertEquals(returnABIObject.size(), 2);
            System.out.println("buildStructB, StructB, StructA[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test static struct set and get
        {
            List<Object> params = new ArrayList<>();
            List<Integer> staticArray = new ArrayList<>();
            staticArray.add(1);
            List<Object> struct = new ArrayList<>();
            struct.add(128);
            struct.add(127);
            struct.add(staticArray);
            params.add(struct);

            // use static struct params, get single struct
            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            COMPLEX_CODEC_TEST, contractAddress, "buildStaticStruct", params);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), "0x0");
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct");
            System.out.println(JsonUtils.toJsonWithException(returnObject));

            // use number params, get static struct list
            List<Object> params2 = new ArrayList<>();
            params2.add(256);
            params2.add(288);
            TransactionResponse transactionResponse2 =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            COMPLEX_CODEC_TEST, contractAddress, "buildStaticStruct", params);
            Assert.assertEquals(transactionResponse2.getTransactionReceipt().getStatus(), "0x0");
            returnObject = transactionResponse2.getReturnObject();
            returnABIObject = transactionResponse2.getReturnABIObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }
    }
}
