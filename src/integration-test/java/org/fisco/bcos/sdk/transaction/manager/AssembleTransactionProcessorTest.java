/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package org.fisco.bcos.sdk.transaction.manager;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.network.NetworkException;
import org.fisco.bcos.sdk.transaction.mock.TransactionCallbackMock;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.utils.Hex;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * TransactionProcessorTest @Description: TransactionProcessorTest
 *
 * @author maojiayu
 * @data Aug 13, 2020 8:00:11 PM
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssembleTransactionProcessorTest {
    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String ABI_FILE = "src/integration-test/resources/abi/";
    private static final String BIN_FILE = "src/integration-test/resources/bin/";
    private static final String ABI =
            "[{\"constant\":true,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"getByBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"bytesArray\",\"type\":\"bytes[]\"}],\"name\":\"setBytesMapping\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"_addrDArray\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_addr\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"incrementUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_bytesV\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_s\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"getSArray\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[2]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"setBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"a\",\"type\":\"address[]\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"setValues\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_intV\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"emptyArgs\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"s\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogIncrement\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogInit\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"address[]\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogSetValues\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"bytes\"},{\"indexed\":false,\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"LogSetBytes\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"uint256[2]\"},{\"indexed\":false,\"name\":\"n\",\"type\":\"uint256[2]\"}],\"name\":\"LogSetSArray\",\"type\":\"event\"}]";
    // init the sdk, and set the config options.
    private final BcosSDK sdk;
    // group
    private final Client client;
    private final CryptoKeyPair cryptoKeyPair;

    public AssembleTransactionProcessorTest() throws NetworkException {
        sdk = BcosSDK.build(CONFIG_FILE);
        client = this.sdk.getClient("group");
        cryptoKeyPair = this.client.getCryptoSuite().getCryptoKeyPair();
    }
    
    private byte[] readBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        if (fileInputStream.read(bytes) != bytes.length) {
            throw new IOException("incomplete reading of file: " + file.toString());
        }
        fileInputStream.close();
        return bytes;
    }

    @Test
    public void test1LiquidHelloWolrd() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        File abiFile = new File(ABI_FILE + "fixed_pointsimple.abi");
        String abi = FileUtils.readFileToString(abiFile);
        File binFile = new File(BIN_FILE + "fixed_pointsimple.wasm");
        byte[] bin = readBytes(binFile);
        String binStr = Hex.toHexString(bin);
        String[] params = new String[1];
        params[0] = "1.5";
        List<String> inputParams = Arrays.asList(params);
        TransactionResponse response =
                transactionProcessor.deployAndGetResponseWithStringParams(abi, binStr, inputParams, "fixed");
                System.out.println(response.getEvents());
                System.out.println(response.getReturnMessage());
    }

    @Test
    public void testCallLiquid() {
        try {
                AssembleTransactionProcessor transactionProcessor;
                transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
                        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        String[] params = new String[0];
        File abiFile = new File(ABI_FILE + "fixed_pointsimple.abi");
        String abi = FileUtils.readFileToString(abiFile);
        List<String> inputParams = Arrays.asList(params);
        CallResponse response =
                transactionProcessor.sendCallWithStringParams(
                        cryptoKeyPair.getAddress(),
                        this.client.isWASM() ? "fixed" : "fixed",
                        abi,
                        "get",
                        inputParams);
        // System.out.println(response.getResults());
        } catch (TransactionBaseException|ABICodecException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
    }

    @Test
    public void test1HelloWorld() throws Exception {
        // create an instance of processor.
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // test sync deploy contract `HelloWorld`, which has no constructed parameter.
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("HelloWorld", new ArrayList<>());
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        Assert.assertEquals(response.getReturnCode(), 0);
        Assert.assertEquals(0, response.getTransactionReceipt().getStatus());
        String helloWorldAddress = response.getContractAddress();
        Assert.assertTrue(
                StringUtils.isNotBlank(response.getContractAddress())
                        && !StringUtils.equalsIgnoreCase(
                                helloWorldAddress,
                                "0x0000000000000000000000000000000000000000000000000000000000000000"));
        // test call, which would be queried off-chain.
        CallResponse callResponse1 =
                transactionProcessor.sendCallByContractLoader(
                        "HelloWorld", helloWorldAddress, "get", new ArrayList<>());
        List<Type> l = callResponse1.getResults();
        Assert.assertEquals(l.size(), 1);
        Assert.assertEquals(l.get(0).getValue(), "Hello, World!");

        // test send transaction
        List<Object> params = new ArrayList<>();
        params.add("test");
        // The contract loader would find abi and binary in the config file path by contract name.
        TransactionReceipt tr =
                transactionProcessor.sendTransactionAndGetReceiptByContractLoader(
                        "HelloWorld", helloWorldAddress, "set", params);
        Assert.assertEquals(0, tr.getStatus());
        TransactionResponse res =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "HelloWorld", helloWorldAddress, "set", params);
        Assert.assertEquals(0, res.getTransactionReceipt().getStatus());

        // test call by contract loader
        CallResponse callResponse2 =
                transactionProcessor.sendCallByContractLoader(
                        "HelloWorld", helloWorldAddress, "get", new ArrayList<>());
        l = callResponse2.getResults();
        Assert.assertEquals(l.size(), 1);
        Assert.assertEquals(l.get(0).getValue(), "test");

        // test deploy by contract loader
        response = transactionProcessor.deployByContractLoader("HelloWorld", new ArrayList<>());
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        Assert.assertEquals(0, response.getReturnCode());
        System.out.println("### AssembleTransactionProcessorTest test1HelloWorld passed");
    }

    @Test
    public void test11HelloWorldAsync() throws Exception {
        // create the instance of processor
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // get the string of abi & bin
        String abi = transactionProcessor.contractLoader.getABIByContractName("HelloWorld");
        String bin = transactionProcessor.contractLoader.getBinaryByContractName("HelloWorld");
        Assert.assertNotNull(bin);
        // deploy with callback. @see TransactionCallbackMock. Mock a quite simple callback.
        TransactionCallbackMock callbackMock = new TransactionCallbackMock();
        transactionProcessor.deployByContractLoaderAsync(
                "HelloWorld", new ArrayList<>(), callbackMock);
        Assert.assertEquals(0, callbackMock.getResult().getStatus());

        // send tx with callback
        String to = callbackMock.getResult().getContractAddress();
        System.out.println("contract address is " + to);
        List<Object> params = Lists.newArrayList("test");
        transactionProcessor.sendTransactionAsync(to, abi, "set", params, callbackMock);
        Assert.assertEquals(0, callbackMock.getResult().getStatus());

        // deploy with future
        CompletableFuture<TransactionReceipt> future =
                transactionProcessor.deployAsync(abi, bin, new ArrayList<>());
        // handle the normal situation.
        future.thenAccept(
                tr -> {
                    System.out.println("deploy succeed time " + System.currentTimeMillis());
                    Assert.assertEquals(0, tr.getStatus());
                });
        // handle exception.
        future.exceptionally(
                e -> {
                    System.out.println("deploy failed:" + e.getMessage());
                    return null;
                });
        System.out.println("--- finish deploy with CompletableFuture ---");

        // wait for the async thread
        Thread.sleep(1000);
    }

    @Test
    public void test2ComplexDeploy() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("ComplexSol", params);
        System.out.println(JsonUtils.toJson(response));
        if (response.getTransactionReceipt().getStatus() != 0) {
            System.out.println(response.getReturnMessage());
            return;
        }
        Assert.assertEquals(0, response.getReturnCode());
        Assert.assertEquals(0, response.getTransactionReceipt().getStatus());
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(
                StringUtils.isNotBlank(response.getContractAddress())
                        && !StringUtils.equalsIgnoreCase(
                                contractAddress,
                                "0x0000000000000000000000000000000000000000000000000000000000000000"));
        Map<String, List<List<Object>>> map = response.getEventResultMap();
        // FIXME: event is not supported now
        // Assert.assertEquals("test2", map.get("LogInit").get(0).get(1));
    }

    @Test
    public void test3ComplexQuery() throws Exception {
        try {
            AssembleTransactionProcessor transactionProcessor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
            // deploy
            List<Object> params = Lists.newArrayList();
            params.add(1);
            params.add("test2");
            TransactionResponse response =
                    transactionProcessor.deployByContractLoader("ComplexSol", params);
            if (response.getTransactionReceipt().getStatus() != 0) {
                return;
            }
            String contractAddress = response.getContractAddress();
            // query i and s
            CallResponse callResponse1 =
                    transactionProcessor.sendCallByContractLoader(
                            "ComplexSol", contractAddress, "_intV", new ArrayList<>());
            System.out.println(JsonUtils.toJson(callResponse1));
            System.out.println("callResponse1 : " + callResponse1.getReturnMessage());
            if (callResponse1.getReturnCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                List<Type> entities = callResponse1.getResults();
                Assert.assertEquals(entities.size(), 1);
                Assert.assertEquals(entities.get(0).getValue(), BigInteger.valueOf(1));
            }
            CallResponse callResponse2 =
                    transactionProcessor.sendCallByContractLoader(
                            "ComplexSol", contractAddress, "_s", new ArrayList<>());
            System.out.println("callResponse2 : " + callResponse2.getReturnMessage());
            if (callResponse2.getReturnCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                System.out.println(JsonUtils.toJson(callResponse2));
                List<Type> entities2 = callResponse2.getResults();
                Assert.assertEquals(entities2.size(), 1);
                Assert.assertEquals(entities2.get(0).getValue(), "test2");
            }
        } catch (TransactionBaseException e) {
            System.out.println("test3ComplexQuery exception, RetCode: " + e.getRetCode());
        }
    }

    @Test
    public void test4ComplexEmptyTx() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("ComplexSol", params);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        // send empty tx
        TransactionReceipt tr =
                transactionProcessor.sendTransactionAndGetReceiptByContractLoader(
                        "ComplexSol", contractAddress, "emptyArgs", ListUtils.emptyIfNull(null));
        Assert.assertEquals(0, tr.getStatus());
    }

    @Test
    public void test5ComplexIncrement() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("ComplexSol", params);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        // increment v
        transactionProcessor.sendTransactionAsync(
                contractAddress,
                this.ABI,
                "incrementUint256",
                Lists.newArrayList(BigInteger.valueOf(10)),
                new TransactionCallback() {
                    @Override
                    public void onResponse(TransactionReceipt receipt) {
                        Assert.assertEquals(0, receipt.getStatus());
                        // getV
                        CallResponse callResponse3;
                        try {
                            callResponse3 =
                                    transactionProcessor.sendCall(
                                            AssembleTransactionProcessorTest.this.cryptoKeyPair
                                                    .getAddress(),
                                            contractAddress,
                                            AssembleTransactionProcessorTest.this.ABI,
                                            "getUint256",
                                            Lists.newArrayList());
                            System.out.println(JsonUtils.toJson(callResponse3));
                            Assert.assertEquals("Success", callResponse3.getReturnMessage());
                        } catch (TransactionBaseException | ABICodecException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });
    }

    @Test
    public void test6ComplexSetValues() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("ComplexSol", params);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        // set values
        List<Object> paramsSetValues = Lists.newArrayList(20);
        String[] o = {"0x1", "0x2", "0x3"};
        List<String> a = Arrays.asList(o);
        paramsSetValues.add(a);
        paramsSetValues.add("set values 字符串");
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponse(
                        contractAddress, ABI, "setValues", paramsSetValues);
        System.out.println(JsonUtils.toJson(transactionResponse));
        Map<String, List<List<Object>>> eventsMap = transactionResponse.getEventResultMap();
        // FIXME: event is not supported now
        // Assert.assertEquals(1, eventsMap.size());
        // Assert.assertEquals("set values 字符串", eventsMap.get("LogSetValues").get(0).get(2));
    }

    @Test
    public void test7ComplexSetBytes() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("ComplexSol", params);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        // setBytes
        List<String> paramsSetBytes = Lists.newArrayList(new String("123".getBytes()));
        TransactionResponse transactionResponse3 =
                transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                        contractAddress, ABI, "setBytes", paramsSetBytes);
        System.out.println(JsonUtils.toJson(transactionResponse3));
        Assert.assertEquals(transactionResponse3.getResults().size(), 1);

        Map<String, List<List<Object>>> eventsMap3 = transactionResponse3.getEventResultMap();
        System.out.println(JsonUtils.toJson(eventsMap3));
        // FIXME: event not supported now
        // Assert.assertEquals(1, eventsMap3.size());
        // Assert.assertEquals("set bytes test", eventsMap3.get("LogSetBytes").get(0).get(1));

        // getBytes
        CallResponse callResponse4 =
                transactionProcessor.sendCall(
                        this.cryptoKeyPair.getAddress(),
                        contractAddress,
                        ABI,
                        "_bytesV",
                        Lists.newArrayList());
        Assert.assertEquals(0, callResponse4.getReturnCode());
        Assert.assertEquals(callResponse4.getResults().get(0), new DynamicBytes("123".getBytes()));
    }

    @Test
    public void test8ComplexSetBytesFuture() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("ComplexSol", params);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        List<Object> paramsSetBytes = Lists.newArrayList("2".getBytes());
        byte[] data = transactionProcessor.encodeFunction(ABI, "setBytes", paramsSetBytes);
        String signedData =
                transactionProcessor.createSignedTransaction(
                        contractAddress, data, this.cryptoKeyPair);
        CompletableFuture<TransactionReceipt> future =
                transactionProcessor.sendTransactionAsync(signedData);
        future.thenAccept(
                r -> {
                    Assert.assertEquals(0, response.getTransactionReceipt().getStatus());
                });
    }
}
