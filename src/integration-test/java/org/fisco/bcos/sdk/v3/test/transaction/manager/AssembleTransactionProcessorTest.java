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
package org.fisco.bcos.sdk.v3.test.transaction.manager;

import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.test.transaction.mock.TransactionCallbackMock;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.v3.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.internal.matchers.Null;

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
            "[{\"inputs\":[{\"internalType\":\"int256\",\"name\":\"i\",\"type\":\"int256\"},{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"address\",\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogIncrement\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"address\",\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogInit\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"bytes\",\"name\":\"o\",\"type\":\"bytes\"},{\"indexed\":false,\"internalType\":\"bytes\",\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"LogSetBytes\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256[2]\",\"name\":\"o\",\"type\":\"uint256[2]\"},{\"indexed\":false,\"internalType\":\"uint256[2]\",\"name\":\"n\",\"type\":\"uint256[2]\"}],\"name\":\"LogSetSArray\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"int256\",\"name\":\"i\",\"type\":\"int256\"},{\"indexed\":false,\"internalType\":\"address[]\",\"name\":\"a\",\"type\":\"address[]\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogSetValues\",\"type\":\"event\"},{\"inputs\":[],\"name\":\"_addr\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"_addrDArray\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"_bytes4V\",\"outputs\":[{\"internalType\":\"bytes4\",\"name\":\"\",\"type\":\"bytes4\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"_bytesV\",\"outputs\":[{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"_intV\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"_s\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"emptyArgs\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"getByBytes\",\"outputs\":[{\"internalType\":\"bytes[]\",\"name\":\"\",\"type\":\"bytes[]\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getSArray\",\"outputs\":[{\"internalType\":\"uint256[2]\",\"name\":\"\",\"type\":\"uint256[2]\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getUint256\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getValues\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"internalType\":\"address[]\",\"name\":\"\",\"type\":\"address[]\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"incrementUint256\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes\",\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"setBytes\",\"outputs\":[{\"internalType\":\"bytes\",\"name\":\"\",\"type\":\"bytes\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes[]\",\"name\":\"bytesArray\",\"type\":\"bytes[]\"}],\"name\":\"setBytesMapping\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes4\",\"name\":\"b\",\"type\":\"bytes4\"}],\"name\":\"setStaticByte4\",\"outputs\":[{\"internalType\":\"bytes4\",\"name\":\"\",\"type\":\"bytes4\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"int256\",\"name\":\"i\",\"type\":\"int256\"},{\"internalType\":\"address[]\",\"name\":\"a\",\"type\":\"address[]\"},{\"internalType\":\"string\",\"name\":\"s\",\"type\":\"string\"}],\"name\":\"setValues\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    // init the sdk, and set the config options.
    private final BcosSDK sdk;
    // group
    private final Client client;
    private final CryptoKeyPair cryptoKeyPair;

    public AssembleTransactionProcessorTest() {
        sdk = BcosSDK.build(CONFIG_FILE);
        client = this.sdk.getClient("group0");
        cryptoKeyPair = this.client.getCryptoSuite().getCryptoKeyPair();
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

        TransactionReceipt transactionReceipt = response.getTransactionReceipt();
        // test TransactionReceipt all fields
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getTransactionHash()) && StringUtils.isNotBlank(transactionReceipt.getTransactionHash()));
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getGasUsed()) && StringUtils.isNotBlank(transactionReceipt.getGasUsed()) && Integer.parseInt(transactionReceipt.getGasUsed()) > 0);
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getVersion()) && transactionReceipt.getVersion() >= 0);
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getBlockNumber()) && transactionReceipt.getBlockNumber().compareTo(BigInteger.ZERO) >= 0);
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getOutput()) && StringUtils.isNotBlank(transactionReceipt.getOutput()));
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getReceiptHash()) && StringUtils.isNotBlank(transactionReceipt.getReceiptHash()));
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getFrom()) && StringUtils.isNotBlank(transactionReceipt.getFrom()));
        if (client.getChainVersion().compareToVersion(EnumNodeVersion.BCOS_3_1_0) >= 0) {
            Assert.assertTrue(Objects.nonNull(transactionReceipt.getChecksumContractAddress()) && StringUtils.isNotBlank(transactionReceipt.getChecksumContractAddress()));
            Assert.assertTrue(transactionReceipt.getChecksumContractAddress().equalsIgnoreCase(transactionReceipt.getContractAddress()));
        }
        Assert.assertTrue(Objects.nonNull(transactionReceipt.getTo()));

        if (client.getChainVersion().compareToVersion(EnumNodeVersion.BCOS_3_3_0) >= 0) {
            Assert.assertTrue(Objects.nonNull(transactionReceipt.getInput()) && StringUtils.isNotBlank(transactionReceipt.getInput()));
        }

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
        String abi = transactionProcessor.getContractLoader().getABIByContractName("HelloWorld");
        String bin = transactionProcessor.getContractLoader().getBinaryByContractName("HelloWorld");
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
        Assert.assertEquals("test2", map.get("LogInit").get(0).get(1));
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
                        } catch (TransactionBaseException | ContractCodecException e) {
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
        params.add(-1);
        params.add("test2");
        TransactionResponse response =
                transactionProcessor.deployByContractLoader("ComplexSol", params);
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        // set values
        List<Object> paramsSetValues = Lists.newArrayList(-20);
        String[] o = {"0x1", "0x2", "0x3"};
        List<String> a = Arrays.asList(o);
        paramsSetValues.add(a);
        paramsSetValues.add("set values 字符串");
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponse(
                        contractAddress, ABI, "setValues", paramsSetValues);
        Map<String, List<List<Object>>> eventsMap = transactionResponse.getEventResultMap();
        Assert.assertEquals(1, eventsMap.size());
        Assert.assertEquals("set values 字符串", eventsMap.get("LogSetValues").get(0).get(2));

        // getValues
        CallResponse callResponse4 =
                transactionProcessor.sendCall(
                        this.cryptoKeyPair.getAddress(),
                        contractAddress,
                        ABI,
                        "getValues",
                        Lists.newArrayList());
        Assert.assertEquals(0, callResponse4.getReturnCode());
        Assert.assertEquals(
                callResponse4.getResults().get(0).getValue(), new Int256(-20).getValue());
        Assert.assertEquals(callResponse4.getResults().get(2).getValue(), "set values 字符串");
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
        {
            List<String> paramsSetBytes = Lists.newArrayList(new String("123".getBytes()));
            TransactionResponse transactionResponse3 =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, ABI, "setBytes", paramsSetBytes);
            Assert.assertEquals(transactionResponse3.getResults().size(), 1);

            Map<String, List<List<Object>>> eventsMap3 = transactionResponse3.getEventResultMap();
            Assert.assertEquals(1, eventsMap3.size());
            Assert.assertEquals(Base64.getEncoder().encodeToString("123".getBytes()), eventsMap3.get("LogSetBytes").get(0).get(1));

            // getBytes
            CallResponse callResponse4 =
                    transactionProcessor.sendCall(
                            this.cryptoKeyPair.getAddress(),
                            contractAddress,
                            ABI,
                            "_bytesV",
                            Lists.newArrayList());
            Assert.assertEquals(0, callResponse4.getReturnCode());
            Assert.assertEquals(
                    Hex.toHexString((byte[]) callResponse4.getResults().get(0).getValue()),
                    Hex.toHexString(new DynamicBytes("123".getBytes()).getValue()));
        }

        // setBytes
        {
            List<String> paramsSetBytes2 =
                    Lists.newArrayList(new String("hex://0x1234".getBytes()));
            TransactionResponse transactionResponse4 =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, ABI, "setBytes", paramsSetBytes2);
            Assert.assertEquals(transactionResponse4.getResults().size(), 1);

            Map<String, List<List<Object>>> eventsMap4 = transactionResponse4.getEventResultMap();
            Assert.assertEquals(1, eventsMap4.size());

            // getBytes
            CallResponse callResponse4 =
                    transactionProcessor.sendCall(
                            this.cryptoKeyPair.getAddress(),
                            contractAddress,
                            ABI,
                            "_bytesV",
                            Lists.newArrayList());
            Assert.assertEquals(0, callResponse4.getReturnCode());
        }
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
        TxPair txPair =
                transactionProcessor.createSignedTransaction(
                        contractAddress, data, this.cryptoKeyPair, 0);
        CompletableFuture<TransactionReceipt> future =
                transactionProcessor.sendTransactionAsync(txPair.getSignedTx());
        future.thenAccept(
                r -> {
                    Assert.assertEquals(0, response.getTransactionReceipt().getStatus());
                });

        TxPair txPair0 =
                transactionProcessor.createSignedTransaction(
                        contractAddress, data, this.cryptoKeyPair, 0, "a");
        CompletableFuture<TransactionReceipt> future0 =
                transactionProcessor.sendTransactionAsync(txPair0.getSignedTx());
        future0.thenAccept(
                r -> {
                    Assert.assertEquals(0, response.getTransactionReceipt().getStatus());
                });
    }

    @Test
    public void test9ComplexSetStaticBytes() throws Exception {
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

        // setStaticByte4 in hex
        {
            List<String> paramsSetBytes = Lists.newArrayList("hex://0x12345678");
            TransactionResponse transactionResponse3 =
                    transactionProcessor.sendTransactionWithStringParamsAndGetResponse(
                            contractAddress, ABI, "setStaticByte4", paramsSetBytes);
            Assert.assertEquals(transactionResponse3.getResults().size(), 1);

            // get _bytes4V
            CallResponse callResponse4 =
                    transactionProcessor.sendCall(
                            this.cryptoKeyPair.getAddress(),
                            contractAddress,
                            ABI,
                            "_bytes4V",
                            Lists.newArrayList());
            Assert.assertEquals(0, callResponse4.getReturnCode());
            Assert.assertEquals(
                    Hex.toHexString((byte[]) callResponse4.getResults().get(0).getValue()),
                    "12345678");
        }
    }

    @Test
    public void test10EventDemo() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);
        String contractAddress = null;
        // deploy
        {
            List<Object> params = Lists.newArrayList();
            TransactionResponse response =
                    transactionProcessor.deployByContractLoader("EventSubDemo", params);
            Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
            contractAddress = response.getContractAddress();
            Assert.assertTrue(contractAddress != null && !contractAddress.isEmpty());
        }

        // transfer
        {
            List<Object> params = new ArrayList<>();
            params.add("test1");
            params.add("test2");
            params.add(BigInteger.valueOf(10));
            TransactionResponse transactionResponse3 =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            "EventSubDemo", contractAddress, "transfer", params);
            Assert.assertEquals(transactionResponse3.getReturnCode(), 0);
            Assert.assertEquals(transactionResponse3.getEventResultMap().size(), 4);
            List<Object> transferData = transactionResponse3.getEventResultMap().get("TransferData").get(0);
            List<List<Object>> result = (List<List<Object>>) transferData.get(0);
            Assert.assertEquals(result.get(0).get(0), "test1");
            Assert.assertEquals(result.get(0).get(1), "test2");
            Assert.assertEquals(result.get(0).get(2), 10);
        }

        // echo
        {
            List<Object> params = new ArrayList<>();
            params.add(BigInteger.valueOf(100));
            params.add(BigInteger.valueOf(-100));
            params.add("test");
            TransactionResponse transactionResponse3 =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            "EventSubDemo", contractAddress, "echo", params);
            Assert.assertEquals(transactionResponse3.getReturnCode(), 0);
            Assert.assertEquals(transactionResponse3.getEventResultMap().size(), 1);
            Assert.assertEquals(transactionResponse3.getEventResultMap().get("Echo").size(), 4);
        }
    }

    @Test
    public void test11CallWithSign() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        this.client, this.cryptoKeyPair, ABI_FILE, BIN_FILE);

        if(client.getChainVersion().compareToVersion(EnumNodeVersion.BCOS_3_4_0) < 0){
            return;
        }
        String contractAddress = null;
        // deploy
        {
            List<Object> params = Lists.newArrayList();
            TransactionResponse response =
                    transactionProcessor.deployByContractLoader("TestCallWithSign", params);
            Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
            contractAddress = response.getContractAddress();
            Assert.assertTrue(contractAddress != null && !contractAddress.isEmpty());
        }

        String abi = transactionProcessor.getContractLoader().getABIByContractName("TestCallWithSign");
        List<Object> params = new ArrayList<>();
        // getOrigin
        {
            CallResponse getOrigin = transactionProcessor.sendCallWithSign("", contractAddress, abi, "getOrigin", params);
            String origin = (String) getOrigin.getReturnObject().get(0);
            Assert.assertEquals(origin, this.cryptoKeyPair.getAddress());
        }

        // getSender
        {
            CallResponse getSender = transactionProcessor.sendCallWithSign("", contractAddress, abi, "getSender", params);
            String sender = (String) getSender.getReturnObject().get(0);
            Assert.assertEquals(sender, this.cryptoKeyPair.getAddress());
        }
    }
}
