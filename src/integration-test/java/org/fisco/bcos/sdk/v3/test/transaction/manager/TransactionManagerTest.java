package org.fisco.bcos.sdk.v3.test.transaction.manager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint128;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.AssembleTransactionService;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.ProxySignTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.tools.ContractLoader;
import org.fisco.bcos.sdk.v3.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TransactionManagerTest {

    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String ABI_FILE = "src/integration-test/resources/abi/";
    private static final String BIN_FILE = "src/integration-test/resources/bin/";

    private static final String COMPLEX_CODEC_TEST = "ComplexCodecTest";

    private final AssembleTransactionService transactionService;

    private final ContractLoader contractLoader;

    private final Client client;

    @Parameterized.Parameter
    public boolean useProxySign;
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][]{
                        {false},
                        {true},
                });
    }

    public TransactionManagerTest() throws IOException {
        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        // group
        client = sdk.getClient("group0");
        transactionService = new AssembleTransactionService(client);
        if (useProxySign) {
            ProxySignTransactionManager proxySignTransactionManager = new ProxySignTransactionManager(client, (hash, transactionSignCallback) -> {
                SignatureResult sign = client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
                transactionSignCallback.handleSignedTransaction(sign);
            });
            transactionService.setTransactionManager(proxySignTransactionManager);
        }
        contractLoader = new ContractLoader(ABI_FILE, BIN_FILE);
    }

    @Test
    public void test1ComplexCodecWithType() throws Exception {
        if (client.getChainCompatibilityVersion().compareTo(EnumNodeVersion.BCOS_3_6_0.toVersionObj()) < 0) {
            return;
        }
        // test deploy with struct
        List<Object> deployParams = new ArrayList<>();
        {
            DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test"));
            DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
            DynamicStruct structA = new DynamicStruct(array, bytes32DynamicArray);
            deployParams.add(structA);
        }

        Pair<String, String> abiAndBinaryByContractName = contractLoader.getABIAndBinaryByContractName(COMPLEX_CODEC_TEST);
        String abi = abiAndBinaryByContractName.getKey();
        TransactionRequestBuilder builder = new TransactionRequestBuilder(abi, abiAndBinaryByContractName.getValue());
        DeployTransactionRequest request = builder.buildDeployRequest(deployParams);

        TransactionResponse response = transactionService.deployContract(request);

        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call get struct
        {
            TransactionRequest callRequest = builder.setTo(contractAddress).setMethod("getStructA").buildRequest(new ArrayList<>());
            CallResponse callResponse = transactionService.sendCall(callRequest);

            List<Object> returnObject = callResponse.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse.getReturnABIObject().size(), 1);
            System.out.println(JsonUtils.toJson(returnObject));

            List<Object> callParams = new ArrayList<>();
            DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test3125643123"));
            DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
            DynamicStruct structA = new DynamicStruct(array, bytes32DynamicArray);
            callParams.add(structA);


            TransactionRequest callRequest2 = builder.setMethod("getStructA").buildRequest(callParams);
            CallResponse callResponse1 = transactionService.sendCall(callRequest2);

            returnObject = callResponse1.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse1.getReturnABIObject().size(), 1);
            System.out.println("getStructA:");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[][] set and get
        {
            List<Object> params = new ArrayList<>();
            DynamicBytes b = new DynamicBytes("1234".getBytes());
            DynamicArray<DynamicBytes> bs = new DynamicArray<>(DynamicBytes.class, b);
            DynamicArray<DynamicArray<DynamicBytes>> bbs = new DynamicArray<>(bs);
            params.add(bbs);

            TransactionRequest transactionRequest = builder.setMethod("setBytesArrayArray").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), Hex.toHexString("1234".getBytes()));
            System.out.println("setBytesArrayArray, bytes[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[][] set and get
        {
            List<Object> params = new ArrayList<>();
            Bytes32 b = new Bytes32(Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
            DynamicArray<Bytes32> bs = new DynamicArray<>(Bytes32.class, b);
            DynamicArray<DynamicArray<Bytes32>> bbs = new DynamicArray<>(bs);
            params.add(bbs);

            TransactionRequest transactionRequest = builder.setMethod("setBytes32ArrayArray").buildRequest(params);

            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("setBytes32ArrayArray, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[2][] set and get
        {
            List<Object> params = new ArrayList<>();
            DynamicBytes b1 = new DynamicBytes(Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
            DynamicBytes b2 = DynamicBytes.DEFAULT;
            DynamicArray<DynamicBytes> bs = new DynamicArray<>(DynamicBytes.class, b1, b2);
            DynamicArray<DynamicArray<DynamicBytes>> bbs = new DynamicArray<>(bs);
            params.add(bbs);

            TransactionRequest transactionRequest = builder.setMethod("setBytesStaticArrayArray").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("setBytesStaticArrayArray, bytes[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[2][] set and get
        {
            List<Object> params = new ArrayList<>();
            Bytes32 b1 = new Bytes32(Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff"));
            Bytes32 b2 = Bytes32.DEFAULT;
            StaticArray<Bytes32> bs = new StaticArray<>(Bytes32.class, b1, b2);
            DynamicArray<StaticArray<Bytes32>> bbs = new DynamicArray<>(bs);
            params.add(bbs);

            TransactionRequest transactionRequest = builder.setMethod("setBytes32StaticArrayArray").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes) ((StaticArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("setBytes32StaticArrayArray, bytes32[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test struct set and get
        {
            List<Object> params = new ArrayList<>();
            DynamicArray<Utf8String> array = new DynamicArray<>(Utf8String.class, new Utf8String("test2132131"));
            DynamicArray<Bytes32> bytes32DynamicArray = new DynamicArray<>(Bytes32.class, Bytes32.DEFAULT);
            DynamicStruct structA = new DynamicStruct(array, bytes32DynamicArray);
            params.add(structA);

            TransactionRequest transactionRequest = builder.setMethod("buildStructB").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 2);
            Assert.assertEquals(returnObject.size(), 2);
            Assert.assertEquals(returnABIObject.size(), 2);
            System.out.println("buildStructB, StructB, StructA[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }

        // test static struct set and get
        {
            List<Object> params = new ArrayList<>();
            StaticArray<Int32> staticArray = new StaticArray<>(Int32.class, 1, new Int32(1));
            Int128 int128 = new Int128(128);
            Uint128 uint128 = new Uint128(127);
            StaticStruct struct = new StaticStruct(int128, uint128, staticArray);
            params.add(struct);

            // use static struct params, get single struct
            TransactionRequest transactionRequest = builder.setMethod("buildStaticStruct").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct");
            System.out.println(JsonUtils.toJsonWithException(returnObject));

            // use number params, get static struct list
            List<Object> params2 = new ArrayList<>();
            params2.add(new Int128(256));
            params2.add(new Uint128(288));
            TransactionRequest transactionRequest1 = builder.setMethod("buildStaticStruct").buildRequest(params2);
            TransactionResponse transactionResponse1 = transactionService.sendTransaction(transactionRequest1);
            Assert.assertEquals(transactionResponse1.getTransactionReceipt().getStatus(), 0);
            results = transactionResponse1.getResults();
            returnObject = transactionResponse1.getReturnObject();
            returnABIObject = transactionResponse1.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }
    }

    @Test
    public void test2ComplexCodecWithStringParams() throws Exception {
        if (client.getChainCompatibilityVersion().compareTo(EnumNodeVersion.BCOS_3_6_0.toVersionObj()) < 0) {
            return;
        }
        // test deploy with struct
        List<String> deployParams = new ArrayList<>();

        deployParams.add("[[\"test\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");
        Pair<String, String> abiAndBinaryByContractName = contractLoader.getABIAndBinaryByContractName(COMPLEX_CODEC_TEST);
        String abi = abiAndBinaryByContractName.getKey();
        TransactionRequestBuilder requestBuilder = new TransactionRequestBuilder(abi, abiAndBinaryByContractName.getValue());
        DeployTransactionRequestWithStringParams deployTransactionRequestWithStringParams = requestBuilder.buildDeployStringParamsRequest(deployParams);
        TransactionResponse response = transactionService.deployContractWithStringParams(deployTransactionRequestWithStringParams);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call send struct get struct
        {
            List<String> callParams = new ArrayList<>();
            // use no params method
            TransactionRequestWithStringParams request = requestBuilder.setMethod("getStructA").setTo(contractAddress).buildStringParamsRequest(callParams);
            CallResponse callResponse = transactionService.sendCallWithStringParams(request);
            List<Object> returnObject = callResponse.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse.getReturnABIObject().size(), 1);
            System.out.println(JsonUtils.toJson(returnObject));

            // use one params method
            callParams.add("[[\"test2312312312312\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");
            TransactionRequestWithStringParams request2 = requestBuilder.setMethod("getStructA").setTo(contractAddress).buildStringParamsRequest(callParams);
            CallResponse callResponse2 = transactionService.sendCallWithStringParams(request2);
            returnObject = callResponse2.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse2.getReturnABIObject().size(), 1);
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"0xabcd\"],[\"0x1234\"]]");
            TransactionRequestWithStringParams transactionRequestWithStringParams = requestBuilder.setMethod("setBytesArrayArray").setTo(contractAddress).buildStringParamsRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransactionWithStringParams(transactionRequestWithStringParams);
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
            TransactionRequestWithStringParams transactionRequestWithStringParams = requestBuilder.setMethod("setBytes32ArrayArray").buildStringParamsRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransactionWithStringParams(transactionRequestWithStringParams);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("setBytes32ArrayArray, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[2][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"0xabcdef\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0x1234\"]]");
            TransactionRequestWithStringParams transactionRequestWithStringParams = requestBuilder.setMethod("setBytesStaticArrayArray").buildStringParamsRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransactionWithStringParams(transactionRequestWithStringParams);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "abcdef");
            System.out.println("setBytesStaticArrayArray, bytes[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes32[2][] set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"0x1234567890123456789012345678901234567890123456789012345678901234\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0x1234567890123456789012345678901234567890123456789012345678901234\",\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"],[\"0xffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\",\"0x1234567890123456789012345678901234567890123456789012345678901234\"]]");
            TransactionRequestWithStringParams transactionRequestWithStringParams = requestBuilder.setMethod("setBytes32StaticArrayArray").buildStringParamsRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransactionWithStringParams(transactionRequestWithStringParams);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes) ((StaticArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "1234567890123456789012345678901234567890123456789012345678901234");
            System.out.println("setBytes32StaticArrayArray, bytes32[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test struct set and get
        {
            List<String> params = new ArrayList<>();
            params.add("[[\"12312314565456345test\"],[\"ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff\"]]");
            TransactionRequestWithStringParams transactionRequestWithStringParams = requestBuilder.setMethod("buildStructB").buildStringParamsRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransactionWithStringParams(transactionRequestWithStringParams);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 2);
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
            TransactionRequestWithStringParams transactionRequestWithStringParams = requestBuilder.setMethod("buildStaticStruct").buildStringParamsRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransactionWithStringParams(transactionRequestWithStringParams);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
            // use number params, get static struct list

            List<String> params2 = new ArrayList<>();
            params2.add("-256");
            params2.add("12321421");
            TransactionRequestWithStringParams transactionRequestWithStringParams2 = requestBuilder.setMethod("buildStaticStruct").buildStringParamsRequest(params2);
            TransactionResponse transactionResponse2 = transactionService.sendTransactionWithStringParams(transactionRequestWithStringParams2);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            results = transactionResponse2.getResults();
            returnObject = transactionResponse2.getReturnObject();
            returnABIObject = transactionResponse2.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));

        }
    }

    @Test
    public void test1ComplexCodecWithJavaObject() throws Exception {
        if (client.getChainCompatibilityVersion().compareTo(EnumNodeVersion.BCOS_3_6_0.toVersionObj()) < 0) {
            return;
        }
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
        Pair<String, String> abiAndBinaryByContractName = contractLoader.getABIAndBinaryByContractName(COMPLEX_CODEC_TEST);
        String abi = abiAndBinaryByContractName.getKey();
        TransactionRequestBuilder requestBuilder = new TransactionRequestBuilder(abi, abiAndBinaryByContractName.getValue());
        DeployTransactionRequest request = requestBuilder.buildDeployRequest(deployParams);

        TransactionResponse response = transactionService.deployContract(request);

        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        // test call get struct
        {
            TransactionRequest transactionRequest = requestBuilder.setTo(contractAddress).setMethod("getStructA").buildRequest(new ArrayList<>());
            // not params method
            CallResponse callResponse = transactionService.sendCall(transactionRequest);
            List<Object> returnObject = callResponse.getReturnObject();
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(callResponse.getReturnABIObject().size(), 1);
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

            TransactionRequest transactionRequest2 = requestBuilder.setMethod("getStructA").buildRequest(callParams);
            CallResponse callResponse2 = transactionService.sendCall(transactionRequest2);

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

            TransactionRequest transactionRequest = requestBuilder.setMethod("setBytesArrayArray").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), Hex.toHexString("1234".getBytes()));
            System.out.println("setBytesArrayArray, bytes[][]");
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

            TransactionRequest transactionRequest = requestBuilder.setMethod("setBytes32ArrayArray").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("setBytes32ArrayArray, bytes32[][]");
            System.out.println(JsonUtils.toJson(returnObject));
        }

        // test bytes[2][] set and get
        {
            List<Object> params = new ArrayList<>();
            byte[] b1 = Hex.decode("ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            byte[] b2 = DynamicBytes.DEFAULT.getValue();
            List<byte[]> bs = new ArrayList<>();
            bs.add(b1);
            bs.add(b2);
            List<List<byte[]>> bss = new ArrayList<>();
            bss.add(bs);
            params.add(bss);

            TransactionRequest transactionRequest = requestBuilder.setMethod("setBytesStaticArrayArray").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((DynamicBytes) ((DynamicArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
            System.out.println("setBytesStaticArrayArray, bytes[2][]");
            System.out.println(JsonUtils.toJson(returnObject));
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

            TransactionRequest transactionRequest = requestBuilder.setMethod("setBytes32StaticArrayArray").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            Assert.assertEquals(Hex.toHexString(((Bytes) ((StaticArray<?>) ((DynamicArray<?>) results.get(0)).getValue().get(0)).getValue().get(0)).getValue()), "ffffffff1234567890123456ffffffffffffffff1234567890123456ffffffff");
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
            TransactionRequest transactionRequest = requestBuilder.setMethod("buildStructB").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);

            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 2);
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
            TransactionRequest transactionRequest = requestBuilder.setMethod("buildStaticStruct").buildRequest(params);
            TransactionResponse transactionResponse = transactionService.sendTransaction(transactionRequest);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            List<Type> results = transactionResponse.getResults();
            List<Object> returnObject = transactionResponse.getReturnObject();
            List<ABIObject> returnABIObject = transactionResponse.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct");
            System.out.println(JsonUtils.toJsonWithException(returnObject));

            // use number params, get static struct list
            List<Object> params2 = new ArrayList<>();
            params2.add(256);
            params2.add(288);
            TransactionRequest transactionRequest1 = requestBuilder.setMethod("buildStaticStruct").buildRequest(params2);
            TransactionResponse transactionResponse1 = transactionService.sendTransaction(transactionRequest1);
            Assert.assertEquals(transactionResponse1.getTransactionReceipt().getStatus(), 0);
            results = transactionResponse1.getResults();
            returnObject = transactionResponse1.getReturnObject();
            returnABIObject = transactionResponse1.getReturnABIObject();
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals(returnObject.size(), 1);
            Assert.assertEquals(returnABIObject.size(), 1);
            System.out.println("buildStaticStruct, staticStruct[]");
            System.out.println(JsonUtils.toJsonWithException(returnObject));
        }
    }
}