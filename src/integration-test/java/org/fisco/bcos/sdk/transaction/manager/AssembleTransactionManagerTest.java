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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.common.collect.Lists;

/**
 * TransactionManagerTest
 *
 * @Description: TransactionManagerTest
 * @author maojiayu
 * @data Aug 13, 2020 8:00:11 PM
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssembleTransactionManagerTest {
    private static final String configFile = "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    private final String abi =
            "[{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"_addrDArray\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_addr\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"incrementUint256\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_bytesV\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_s\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"getSArray\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[2]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"bytesArray\",\"type\":\"bytes1[]\"}],\"name\":\"setBytesMapping\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"setBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"a\",\"type\":\"address[]\"},{\"name\":\"s\",\"type\":\"string\"}],\"name\":\"setValues\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"b\",\"type\":\"bytes1\"}],\"name\":\"getByBytes\",\"outputs\":[{\"name\":\"\",\"type\":\"bytes1[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"_intV\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"emptyArgs\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"i\",\"type\":\"int256\"},{\"name\":\"s\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"uint256\"}],\"name\":\"LogIncrement\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"sender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogInit\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"a\",\"type\":\"address[]\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"string\"}],\"name\":\"LogSetValues\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"bytes\"},{\"indexed\":false,\"name\":\"b\",\"type\":\"bytes\"}],\"name\":\"LogSetBytes\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"o\",\"type\":\"uint256[2]\"},{\"indexed\":false,\"name\":\"n\",\"type\":\"uint256[2]\"}],\"name\":\"LogSetSArray\",\"type\":\"event\"}]";

    @Test
    public void test1HelloWorld() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        // deploy
        TransactionResponse response = manager.deployByContractLoader("HelloWorld", new ArrayList<>());
        // System.out.println(JsonUtils.toJson(response));
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        Assert.assertTrue(response.getReturnCode() == 0);
        Assert.assertEquals("0x0", response.getTransactionReceipt().getStatus());
        String helloWorldAddrss = response.getContractAddress();
        Assert.assertTrue(
                StringUtils.isNotBlank(response.getContractAddress()) && !StringUtils.equalsIgnoreCase(helloWorldAddrss,
                        "0x0000000000000000000000000000000000000000000000000000000000000000"));
        // call
        CallResponse callResponse1 =
                manager.sendCallByContractLoader("HelloWorld", helloWorldAddrss, "name", new ArrayList<>());
        // System.out.println(JsonUtils.toJson(callResponse1));
        List<Object> l = JsonUtils.fromJsonList(callResponse1.getValues(), Object.class);
        Assert.assertEquals(l.size(), 1);
        Assert.assertEquals(l.get(0), "Hello, World!");
        // send transaction
        List<Object> params = new ArrayList<>();
        params.add("test");
        TransactionReceipt tr =
                manager.sendTransactionAndGetReceiptByContractLoader("HelloWorld", helloWorldAddrss, "set", params);
        Assert.assertEquals("0x0", tr.getStatus());
        // System.out.println(JsonUtils.toJson(tr));
        TransactionResponse res =
                manager.sendTransactionAndGetResponseByContractLoader("HelloWorld", helloWorldAddrss, "set", params);
        Assert.assertEquals("0x0", res.getTransactionReceipt().getStatus());
        // System.out.println(JsonUtils.toJson(res));
        
        // call
        CallResponse callResponse2 =
                manager.sendCallByContractLoader("HelloWorld", helloWorldAddrss, "name", new ArrayList<>());
        // System.out.println(JsonUtils.toJson(callResponse2));
        l = JsonUtils.fromJsonList(callResponse2.getValues(), Object.class);
        Assert.assertEquals(l.size(), 1);
        Assert.assertEquals(l.get(0), "test");
    }

    @Test
    public void test2ComplexDeploy() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
        // System.out.println(JsonUtils.toJson(response));
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            System.out.println(response.getReturnMessage());
            return;
        }
        Assert.assertTrue(response.getReturnCode() == 0);
        Assert.assertEquals("0x0", response.getTransactionReceipt().getStatus());
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(
                StringUtils.isNotBlank(response.getContractAddress()) && !StringUtils.equalsIgnoreCase(contractAddress,
                        "0x0000000000000000000000000000000000000000000000000000000000000000"));
        // System.out.println(JsonUtils.toJson(response));
        Map<String, List<Object>> map = response.getEventResultMap();
        Assert.assertEquals("test2", map.get("LogInit").get(1));
    }

    @Test
    public void test3ComplexQuery() throws Exception {
        try {
            BcosSDK sdk =  BcosSDK.build(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
            AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                    client.getCryptoInterface(), abiFile, binFile);
            // deploy
            List<Object> params = Lists.newArrayList();
            params.add(1);
            params.add("test2");
            TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
            if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
                return;
            }
            String contractAddress = response.getContractAddress();
            // query i and s
            CallResponse callResponse1 =
                    manager.sendCallByContractLoader("ComplexSol", contractAddress, "_intV", new ArrayList<>());
            // System.out.println(JsonUtils.toJson(callResponse1));
            // System.out.println("callResponse1 : " + callResponse1.getReturnMessage());
            if (callResponse1.getReturnCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                List<Object> entities = JsonUtils.fromJsonList(callResponse1.getValues(), Object.class);
                Assert.assertEquals(entities.size(), 1);
                Assert.assertEquals(entities.get(0), 1);
            }
            CallResponse callResponse2 =
                    manager.sendCallByContractLoader("ComplexSol", contractAddress, "_s", new ArrayList<>());
            // System.out.println("callResponse2 : " + callResponse2.getReturnMessage());
            if (callResponse2.getReturnCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                // System.out.println(JsonUtils.toJson(callResponse2));
                List<Object> entities2 = JsonUtils.fromJsonList(callResponse2.getValues(), Object.class);
                Assert.assertEquals(entities2.size(), 1);
                Assert.assertEquals(entities2.get(0), "test2");
            }
        }catch(TransactionBaseException e)
        {
            System.out.println("test3ComplexQuery exception, RetCode: " + e.getRetCode());
        }
    }

    @Test
    public void test4ComplexEmptyTx() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        String contractAddress = response.getContractAddress();
        // send empty tx
        TransactionReceipt tr = manager.sendTransactionAndGetReceiptByContractLoader("ComplexSol", contractAddress,
                "emptyArgs", ListUtils.emptyIfNull(null));
        Assert.assertEquals("0x0", tr.getStatus());
    }

    @Test
    public void test5ComplexIncrement() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        String contractAddress = response.getContractAddress();
        // increment v
        manager.sendTransactionAsync(contractAddress, abi, "incrementUint256",
                Lists.newArrayList(BigInteger.valueOf(10)), new TransactionCallback() {
                    @Override
                    public void onResponse(TransactionReceipt receipt) {
                        Assert.assertEquals("0x0", receipt.getStatus());
                        // getV
                        CallResponse callResponse3;
                        try {
                            callResponse3 =
                                    manager.sendCall(client.getCryptoInterface().getCryptoKeyPair().getAddress(),
                                            contractAddress, abi, "getUint256", Lists.newArrayList());
                            //System.out.println(JsonUtils.toJson(callResponse3));
                            Assert.assertEquals("Success", callResponse3.getReturnMessage());
                        } catch (TransactionBaseException | ABICodecException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });
    }

    @Test
    public void test6ComplexSetValues() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        String contractAddress = response.getContractAddress();
        // set values
        List<Object> paramsSetValues = Lists.newArrayList(20);
        String[] o = { "0x1", "0x2", "0x3" };
        List<String> a = Arrays.asList(o);
        paramsSetValues.add(a);
        paramsSetValues.add("set values 字符串");
        TransactionResponse transactionResponse =
                manager.sendTransactionAndGetResponse(contractAddress, abi, "setValues", paramsSetValues);
        // System.out.println(JsonUtils.toJson(transactionResponse));
        Map<String, List<Object>> eventsMap = transactionResponse.getEventResultMap();
        Assert.assertEquals(1, eventsMap.size());
        Assert.assertEquals("set values 字符串", eventsMap.get("LogSetValues").get(2));
    }

    @Test
    public void test7ComplexSetBytes() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        String contractAddress = response.getContractAddress();
        // setBytes
        List<String> paramsSetBytes = Lists.newArrayList(Base64.toBase64String("set bytes test".getBytes()));
        TransactionResponse transactionResponse3 =
                manager.sendTransactionWithStringParamsAndGetResponse(contractAddress, abi, "setBytes", paramsSetBytes);
        // System.out.println(JsonUtils.toJson(transactionResponse3));
        Assert.assertEquals(transactionResponse3.getValuesList().size(), 1);
        Assert.assertEquals(transactionResponse3.getValuesList().get(0), "set bytes test");

        Map<String, List<Object>> eventsMap3 = transactionResponse3.getEventResultMap();
        // System.out.println(JsonUtils.toJson(eventsMap3));
        Assert.assertEquals(1, eventsMap3.size());
        Assert.assertEquals("set bytes test", eventsMap3.get("LogSetBytes").get(1));

        // getBytes
        CallResponse callResponse4 = manager.sendCall(client.getCryptoInterface().getCryptoKeyPair().getAddress(),
                contractAddress, abi, "_bytesV", Lists.newArrayList());
        Assert.assertEquals(0, callResponse4.getReturnCode());
        List<Object> resultEntityList4 = JsonUtils.fromJsonList(callResponse4.getValues(), Object.class);
        Assert.assertEquals("set bytes test", resultEntityList4.get(0));
    }

    @Test
    public void test8ComplexSetBytesMapping() throws Exception {
        BcosSDK sdk =  BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        // System.out.println(cryptoInterface.getCryptoKeyPair().getAddress());
        AssembleTransactionManager manager = TransactionManagerFactory.createAssembleTransactionManager(client,
                client.getCryptoInterface(), abiFile, binFile);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader("ComplexSol", params);
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        String contractAddress = response.getContractAddress();
        // set Bytes Mapping
        List<Object> paramsSetBytes = Lists.newArrayList("set bytes2".getBytes());
        String data = manager.encodeFunction(abi, "setBytes", paramsSetBytes);
        String signedData = manager.createSignedTransaction(contractAddress, data);
        CompletableFuture<TransactionReceipt> future = manager.sendTransactionAsync(signedData);
        future.thenAccept(r -> {
            Assert.assertEquals("0x0", response.getTransactionReceipt().getStatus());
        });
    }
}
