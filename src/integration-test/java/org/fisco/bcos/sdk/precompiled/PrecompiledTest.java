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

package org.fisco.bcos.sdk.precompiled;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.fisco.bcos.sdk.BcosSDKTest;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.contract.precompiled.bfs.BFSService;
import org.fisco.bcos.sdk.contract.precompiled.bfs.FileInfo;
import org.fisco.bcos.sdk.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsInfo;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsService;
import org.fisco.bcos.sdk.contract.precompiled.consensus.ConsensusService;
import org.fisco.bcos.sdk.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.contract.solidity.HelloWorld;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrecompiledTest {
    private static final String configFile =
            BcosSDKTest.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    public AtomicLong receiptCount = new AtomicLong();
    private static final String GROUP = "group0";
    private Random random = new Random();

    public PrecompiledTest() throws NoSuchAlgorithmException {}

    @Test
    public void test1ConsensusService() throws ConfigException, ContractException, JniException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        ConsensusService consensusService = new ConsensusService(client, cryptoKeyPair);
        // get the current sealerList
        List<SealerList.Sealer> sealerList = client.getSealerList().getResult();

        // select the node to operate
        SealerList.Sealer selectedNode = sealerList.get(0);
        System.out.println("selectNode: " + selectedNode.getNodeID());

        // addSealer
        Assert.assertThrows(
                ContractException.class,
                () -> {
                    consensusService.addSealer(selectedNode.getNodeID(), BigInteger.ONE);
                });

        // add the sealer to the observerList
        RetCode retCode = consensusService.addObserver(selectedNode.getNodeID());
        // query the observerList
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), retCode.getCode());
        List<String> observerList = client.getObserverList().getResult();
        System.out.println("observerList: " + observerList);
        Assert.assertTrue(observerList.contains(selectedNode.getNodeID()));
        // query the sealerList
        sealerList = client.getSealerList().getResult();
        System.out.println("sealerList: " + sealerList);
        Assert.assertFalse(sealerList.contains(selectedNode));
        // add the node to the observerList again
        Assert.assertThrows(
                ContractException.class,
                () -> consensusService.addObserver(selectedNode.getNodeID()));

        // add the node to the sealerList again
        retCode = consensusService.addSealer(selectedNode.getNodeID(), BigInteger.ONE);

        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), retCode.getCode());
        List<SealerList.Sealer> sealerList1 = client.getSealerList().getResult();
        System.out.println("sealerList1: " + sealerList1);
        Assert.assertTrue(sealerList1.contains(selectedNode));
        List<String> observerList1 = client.getObserverList().getResult();
        System.out.println("observerList1: " + observerList1);
        Assert.assertFalse(observerList1.contains(selectedNode.getNodeID()));

        // removeNode
        retCode = consensusService.removeNode(selectedNode.getNodeID());
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), retCode.getCode());
        List<String> observerList2 = client.getObserverList().getResult();
        System.out.println("observerList2: " + observerList2);
        Assert.assertFalse(observerList2.contains(selectedNode.getNodeID()));
        List<SealerList.Sealer> sealerList2 = client.getSealerList().getResult();
        System.out.println("sealerList2: " + sealerList2);
        Assert.assertFalse(sealerList2.contains(selectedNode));

        // add the node to observerList again
        retCode = consensusService.addObserver(selectedNode.getNodeID());
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), retCode.getCode());
        List<String> observerList3 = client.getObserverList().getResult();
        System.out.println("observerList3: " + observerList3);
        Assert.assertTrue(observerList3.contains(selectedNode.getNodeID()));
        List<SealerList.Sealer> sealerList3 = client.getSealerList().getResult();
        System.out.println("sealerList3: " + sealerList3);
        Assert.assertFalse(sealerList3.contains(selectedNode));

        // add the node to the sealerList again
        retCode = consensusService.addSealer(selectedNode.getNodeID(), BigInteger.ONE);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS.getCode(), retCode.getCode());
        List<SealerList.Sealer> sealerList4 = client.getSealerList().getResult();
        System.out.println("sealerList4: " + sealerList4);

        Assert.assertTrue(sealerList4.contains(selectedNode));
        List<String> observerList4 = client.getObserverList().getResult();
        System.out.println("observerList4: " + observerList4);
        Assert.assertFalse(observerList4.contains(selectedNode.getNodeID()));
    }

    // @Test
    public void test2CnsService() throws ConfigException, ContractException, JniException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);
        String contractAddress = Hex.trimPrefix(helloWorld.getContractAddress().toLowerCase());
        String contractName = "HelloWorld";
        String contractVersion = String.valueOf(Math.random());
        CnsService cnsService = new CnsService(client, cryptoKeyPair);
        RetCode retCode =
                cnsService.registerCNS(contractName, contractVersion, contractAddress, "");
        // query the cns information
        List<CnsInfo> cnsInfos = cnsService.selectByName(contractName);
        if (!cnsInfos.isEmpty()) {
            boolean containContractAddress = false;
            for (CnsInfo cnsInfo : cnsInfos) {
                if (cnsInfo.getAddress().equals(contractAddress)) {
                    containContractAddress = true;
                    break;
                }
            }
            Assert.assertTrue(containContractAddress);
        }

        Tuple2<String, String> cnsTuple =
                cnsService.selectByNameAndVersion(contractName, contractVersion);
        Assert.assertTrue(
                Numeric.cleanHexPrefix(cnsTuple.getValue1()).equals(contractAddress)); // address
        Assert.assertTrue(cnsTuple.getValue2().equals("")); // abi

        if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
            boolean containContractAddress = false;
            for (CnsInfo cnsInfo : cnsInfos) {
                if (cnsInfo.getAddress().equals(contractAddress)) {
                    containContractAddress = true;
                }
            }
            Assert.assertTrue(containContractAddress);
        }
        Assert.assertTrue(cnsInfos.get(0).getName().equals(contractName));

        // query contractAddress
        cnsService.getContractAddress(contractName, contractVersion);
        // insert another cns info
        String contractVersion2 = String.valueOf(Math.random());
        retCode = cnsService.registerCNS(contractName, contractVersion2, contractAddress, "");

        if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
            List<CnsInfo> cnsInfos2 = cnsService.selectByName(contractName);
            Assert.assertTrue(cnsInfos2.size() == cnsInfos.size() + 1);
            Assert.assertTrue(
                    Numeric.cleanHexPrefix(
                                    cnsService.getContractAddress(contractName, contractVersion))
                            .equals(contractAddress));
            Assert.assertTrue(
                    Numeric.cleanHexPrefix(
                                    cnsService.getContractAddress(contractName, contractVersion2))
                            .equals(contractAddress));
        }
        // insert anther cns for other contract
        HelloWorld helloWorld2 = HelloWorld.deploy(client, cryptoKeyPair);
        String contractAddress2 = Hex.trimPrefix(helloWorld2.getContractAddress().toLowerCase());
        String contractName2 = "hello";
        retCode = cnsService.registerCNS(contractName2, contractVersion, contractAddress2, "");
        if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
            String abc = cnsService.getContractAddress(contractName, "abc");
            Assert.assertTrue(abc.equals("0x0000000000000000000000000000000000000000"));
            Assert.assertTrue(
                    Numeric.cleanHexPrefix(
                                    cnsService.getContractAddress(contractName2, contractVersion))
                            .equals(contractAddress2));
            Assert.assertTrue(
                    Numeric.cleanHexPrefix(
                                    cnsService.getContractAddress(contractName, contractVersion))
                            .equals(contractAddress));
        }
    }

    @Test
    public void test3SystemConfigService() throws ConfigException, ContractException, JniException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        SystemConfigService systemConfigService = new SystemConfigService(client, cryptoKeyPair);
        this.testSystemConfigService(client, systemConfigService, "tx_count_limit");
        this.testSystemConfigService(client, systemConfigService, "tx_gas_limit");
    }

    private void testSystemConfigService(
            Client client, SystemConfigService systemConfigService, String key)
            throws ContractException {
        BigInteger value =
                new BigInteger(client.getSystemConfigByKey(key).getSystemConfig().getValue());
        BigInteger updatedValue = value.add(BigInteger.valueOf(100));
        String updatedValueStr = String.valueOf(updatedValue);
        systemConfigService.setValueByKey(key, updatedValueStr);

        BigInteger queriedValue =
                new BigInteger(client.getSystemConfigByKey(key).getSystemConfig().getValue());
        System.out.println("queriedValue: " + queriedValue);
        Assert.assertTrue(queriedValue.equals(updatedValue));
        Assert.assertTrue(queriedValue.equals(value.add(BigInteger.valueOf(100))));
    }

    // FIXME: no use in FISCO BCOS v3.0.0-rc1
    // @Test
    public void test5CRUDService() throws ConfigException, ContractException, JniException {
        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        TableCRUDService tableCRUDService = new TableCRUDService(client, cryptoKeyPair);
        // create a user table
        String tableName = "test" + (int) (Math.random() * 1000);
        String key = "key";
        List<String> valueFields = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            valueFields.add(i, "field" + i);
        }
        RetCode code = tableCRUDService.createTable(tableName, key, valueFields);
        Assert.assertEquals(0, code.getCode());
        // desc
        List<Map<String, String>> desc = tableCRUDService.desc(tableName);
        Assert.assertEquals(desc.get(0).get("value_field"), "field0,field1,field2,field3,field4");

        // insert
        Map<String, String> fieldNameToValue = new HashMap<>();
        for (int i = 0; i < valueFields.size(); i++) {
            fieldNameToValue.put("field" + i, "value" + i);
        }
        fieldNameToValue.put(key, "key1");
        Entry fieldNameToValueEntry = new Entry(fieldNameToValue);
        tableCRUDService.insert(tableName, fieldNameToValueEntry);
        // select
        Condition condition = new Condition();
        condition.EQ(key, "key1");
        List<Map<String, String>> result = tableCRUDService.select(tableName, condition);
        // field value result + key result
        if (!result.isEmpty()) {
            Assert.assertEquals(result.get(0).size(), valueFields.size());
        }
        System.out.println("tableCRUDService select result: " + result);
        // update
        fieldNameToValue.clear();
        fieldNameToValue.put("field1", "value123");
        fieldNameToValueEntry.setFieldNameToValue(fieldNameToValue);
        tableCRUDService.update(tableName, fieldNameToValueEntry, condition);
        result = tableCRUDService.select(tableName, condition);
        Assert.assertEquals(result.get(0).size(), valueFields.size());
        Assert.assertEquals(result.get(0).get("field1"), "value123");
        System.out.println("tableCRUDService select result: " + result);

        // remove
        tableCRUDService.remove(tableName, condition);
        result = tableCRUDService.select(tableName, condition);
        Assert.assertTrue(result.isEmpty());
        System.out.println("testCRUDPrecompiled tableCRUDService.remove size : " + result.size());
    }

    // FIXME: no use in FISCO BCOS v3.0.0-rc1
    // @Test
    public void test51SyncCRUDService() throws ConfigException, ContractException, JniException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        TableCRUDService crudService = new TableCRUDService(client, cryptoKeyPair);
        String tableName = "test_sync" + new Random().nextInt(100);
        List<String> valueFiled = new ArrayList<>();
        valueFiled.add("field");
        RetCode retCode = crudService.createTable(tableName, "key", valueFiled);
        System.out.println("tableName" + tableName);
        System.out.println(
                "createResult: " + retCode.getCode() + ", message: " + retCode.getMessage());
        // create a thread pool to parallel insert and select
        ExecutorService threadPool = Executors.newFixedThreadPool(50);

        BigInteger orgTxCount =
                new BigInteger(
                        client.getTotalTransactionCount()
                                .getTotalTransactionCount()
                                .getTransactionCount());
        for (int i = 0; i < 100; i++) {
            Integer index = i;
            threadPool.execute(
                    () -> {
                        try {
                            Map<String, String> value = new HashMap<>();
                            value.put("field", "field" + index);
                            value.put("key", "key" + index);
                            // insert
                            crudService.insert(tableName, new Entry(value));
                            // select
                            Condition condition = new Condition();
                            condition.EQ("key", "key" + index);
                            crudService.select(tableName, condition);
                            // update
                            value.clear();
                            value.put("field", "field" + index + 100);
                            crudService.update(tableName, new Entry(value), condition);
                            // remove
                            crudService.remove(tableName, condition);
                        } catch (ContractException e) {
                            System.out.println(
                                    "call crudService failed, error information: "
                                            + e.getMessage());
                        }
                    });
        }
        ThreadPoolService.stopThreadPool(threadPool);
        BigInteger currentTxCount =
                new BigInteger(
                        client.getTotalTransactionCount()
                                .getTotalTransactionCount()
                                .getTransactionCount());
        System.out.println("orgTxCount: " + orgTxCount + ", currentTxCount:" + currentTxCount);
        Assert.assertTrue(currentTxCount.compareTo(orgTxCount.add(BigInteger.valueOf(300))) >= 0);
    }

    class FakeTransactionCallback implements PrecompiledCallback {
        public TransactionReceipt receipt;

        // wait until get the transactionReceipt
        @Override
        public void onResponse(RetCode retCode) {
            this.receipt = retCode.getTransactionReceipt();
            PrecompiledTest.this.receiptCount.addAndGet(1);
        }
    }

    // FIXME: no use in FISCO BCOS v3.0.0-rc1
    // @Test
    public void test52AsyncCRUDService()
            throws ConfigException, ContractException, InterruptedException, JniException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        TableCRUDService crudService = new TableCRUDService(client, cryptoKeyPair);
        // create table
        String tableName = "send_async" + new Random().nextInt(1000);
        List<String> valueFiled = new ArrayList<>();
        valueFiled.add("field");
        String key = "key";
        crudService.createTable(tableName, key, valueFiled);
        // create a thread pool to parallel insert and select
        ExecutorService threadPool = Executors.newFixedThreadPool(50);
        BigInteger orgTxCount =
                new BigInteger(
                        client.getTotalTransactionCount()
                                .getTotalTransactionCount()
                                .getTransactionCount());
        for (int i = 0; i < 100; i++) {
            int index = i;
            threadPool.execute(
                    () -> {
                        try {
                            Map<String, String> value = new HashMap<>();
                            value.put("field", "field" + index);
                            value.put("key", "key" + index);
                            // insert
                            FakeTransactionCallback callback = new FakeTransactionCallback();
                            crudService.asyncInsert(tableName, new Entry(value), callback);
                            // update
                            Condition condition = new Condition();
                            condition.EQ(key, "key" + index);
                            value.clear();
                            value.put("field", "field" + index + 100);
                            FakeTransactionCallback callback2 = new FakeTransactionCallback();
                            crudService.asyncUpdate(
                                    tableName, new Entry(value), condition, callback2);
                            // remove
                            FakeTransactionCallback callback3 = new FakeTransactionCallback();
                            crudService.asyncRemove(tableName, condition, callback3);
                        } catch (ContractException e) {
                            System.out.println(
                                    "call crudService failed, error information: "
                                            + e.getMessage());
                        }
                    });
        }
        while (this.receiptCount.get() != 300) {
            Thread.sleep(1000);
        }
        ThreadPoolService.stopThreadPool(threadPool);
        BigInteger currentTxCount =
                new BigInteger(
                        client.getTotalTransactionCount()
                                .getTotalTransactionCount()
                                .getTransactionCount());
        System.out.println("orgTxCount: " + orgTxCount + ", currentTxCount:" + currentTxCount);
        Assert.assertTrue(currentTxCount.compareTo(orgTxCount.add(BigInteger.valueOf(300))) >= 0);
    }

    // FIXME: this integration test should be fix in cpp sdk
    // @Test
    public void test7BFSPrecompiled() throws ConfigException, ContractException, JniException {

        ConfigOption configOption = Config.load(configFile);
        Client client = Client.build(GROUP, configOption);

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        BFSService bfsService = new BFSService(client, cryptoKeyPair);
        List<FileInfo> list = bfsService.list("/");
        System.out.println(list);
        String newDir = "local" + random.nextInt(10000) + random.nextInt(1000);
        RetCode mkdir = bfsService.mkdir("/apps/" + newDir);
        System.out.println("newDir: " + newDir);
        Assert.assertEquals(mkdir.code, 0);
        List<FileInfo> list2 = bfsService.list("/apps");
        System.out.println(list2);
        boolean flag = false;
        for (FileInfo fileInfo : list2) {
            if (Objects.equals(fileInfo.getName(), newDir)) {
                flag = true;
                break;
            }
        }
        Assert.assertTrue(flag);
        HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);
        String contractAddress = helloWorld.getContractAddress();
        String version = String.valueOf(random.nextInt(10000));
        bfsService.link("HelloWorld", version, contractAddress, HelloWorld.ABI);
        List<BFSPrecompiled.BfsInfo> listLink = bfsService.list("/apps/HelloWorld");
        System.out.println(listLink);
        flag = false;
        for (BFSPrecompiled.BfsInfo bfsInfo : listLink) {
            if (bfsInfo.getFileType().equals("link") && bfsInfo.getFileName().equals(version)) {
                flag = true;
                break;
            }
        }
        Assert.assertTrue(flag);
    }
}
