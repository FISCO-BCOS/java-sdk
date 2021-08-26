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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.BcosSDKTest;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.contract.HelloWorld;
import org.fisco.bcos.sdk.contract.precompiled.bfs.BFSService;
import org.fisco.bcos.sdk.contract.precompiled.callback.PrecompiledCallback;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsInfo;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsService;
import org.fisco.bcos.sdk.contract.precompiled.consensus.ConsensusService;
import org.fisco.bcos.sdk.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.contract.precompiled.wasm.DeployWasmService;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.StringUtils;
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

    @Test
    public void test1ConsensusService() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            ConsensusService consensusService = new ConsensusService(client, cryptoKeyPair);
            // get the current sealerList
            List<SealerList.Sealer> sealerList = client.getSealerList().getResult();

            // select the node to operate
            SealerList.Sealer selectedNode = sealerList.get(0);

            // addSealer
            Assert.assertThrows(
                    ContractException.class,
                    () -> {
                        consensusService.addSealer(selectedNode.getNodeID(), BigInteger.ONE);
                    });

            // add the sealer to the observerList
            RetCode retCode = consensusService.addObserver(selectedNode.getNodeID());
            // query the observerList
            if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                List<String> observerList = client.getObserverList().getResult();
                Assert.assertTrue(observerList.contains(selectedNode.getNodeID()));
                // query the sealerList
                sealerList = client.getSealerList().getResult();
                Assert.assertFalse(sealerList.contains(selectedNode));
                // add the node to the observerList again
                Assert.assertThrows(
                        ContractException.class,
                        () -> {
                            consensusService.addObserver(selectedNode.getNodeID());
                        });
            }
            // add the node to the sealerList again
            retCode = consensusService.addSealer(selectedNode.getNodeID(), BigInteger.ONE);

            if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(client.getSealerList().getResult().contains(selectedNode));
                Assert.assertFalse(
                        client.getObserverList().getResult().contains(selectedNode.getNodeID()));
            }

            // removeNode
            retCode = consensusService.removeNode(selectedNode.getNodeID());
            if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertFalse(
                        client.getObserverList().getResult().contains(selectedNode.getNodeID()));
                Assert.assertFalse(client.getSealerList().getResult().contains(selectedNode));
            }

            // add the node to observerList again
            retCode = consensusService.addObserver(selectedNode.getNodeID());
            if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(
                        client.getObserverList().getResult().contains(selectedNode.getNodeID()));
                Assert.assertFalse(client.getSealerList().getResult().contains(selectedNode));
            }

            // add the node to the sealerList again
            retCode = consensusService.addSealer(selectedNode.getNodeID(), BigInteger.ONE);
            if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(client.getSealerList().getResult().contains(selectedNode));
                Assert.assertFalse(
                        client.getObserverList().getResult().contains(selectedNode.getNodeID()));
            }
        } catch (ClientException | ContractException e) {
            System.out.println(
                    "testConsensusPrecompiled exceptioned, error info:" + e.getMessage());
        }
    }

    @Test
    public void test2CnsService() throws ConfigException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);
            String contractAddress = helloWorld.getContractAddress().toLowerCase();
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
                    Numeric.cleanHexPrefix(cnsTuple.getValue1())
                            .equals(contractAddress)); // address
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
                                        cnsService.getContractAddress(
                                                contractName, contractVersion))
                                .equals(contractAddress));
                Assert.assertTrue(
                        Numeric.cleanHexPrefix(
                                        cnsService.getContractAddress(
                                                contractName, contractVersion2))
                                .equals(contractAddress));
            }
            // insert anther cns for other contract
            HelloWorld helloWorld2 = HelloWorld.deploy(client, cryptoKeyPair);
            String contractAddress2 = helloWorld2.getContractAddress().toLowerCase();
            String contractName2 = "hello";
            retCode = cnsService.registerCNS(contractName2, contractVersion, contractAddress2, "");
            if (retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                String abc = cnsService.getContractAddress(contractName, "abc");
                Assert.assertTrue(abc.equals("0x0000000000000000000000000000000000000000"));
                Assert.assertTrue(
                        Numeric.cleanHexPrefix(
                                        cnsService.getContractAddress(
                                                contractName2, contractVersion))
                                .equals(contractAddress2));
                Assert.assertTrue(
                        Numeric.cleanHexPrefix(
                                        cnsService.getContractAddress(
                                                contractName, contractVersion))
                                .equals(contractAddress));
            }
        } catch (ContractException e) {
            System.out.println("testCnsPrecompiled failed for " + e.getMessage());
        }
    }

    @Test
    public void test3SystemConfigService() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            SystemConfigService systemConfigService =
                    new SystemConfigService(client, cryptoKeyPair);
            this.testSystemConfigService(client, systemConfigService, "tx_count_limit");
            this.testSystemConfigService(client, systemConfigService, "tx_gas_limit");
        } catch (ClientException | ContractException e) {
            System.out.println(
                    "testSystemConfigPrecompiled exceptioned, error inforamtion:" + e.getMessage());
        }
    }

    private void testSystemConfigService(
            Client client, SystemConfigService systemConfigService, String key)
            throws ContractException {
        BigInteger value =
                new BigInteger(client.getSystemConfigByKey(key).getSystemConfig().getValue());
        BigInteger updatedValue = value.add(BigInteger.valueOf(1000));
        String updatedValueStr = String.valueOf(updatedValue);
        systemConfigService.setValueByKey(key, updatedValueStr);

        BigInteger queriedValue =
                new BigInteger(client.getSystemConfigByKey(key).getSystemConfig().getValue());
        System.out.println("queriedValue: " + queriedValue);
        // Assert.assertTrue(queriedValue.equals(updatedValue));
        // Assert.assertTrue(queriedValue.equals(value.add(BigInteger.valueOf(1000))));
    }

    // Note: Please make sure that the ut is before the permission-related ut
    @Test
    public void test5CRUDService() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            TableCRUDService tableCRUDService = new TableCRUDService(client, cryptoKeyPair);
            // create a user table
            String tableName = "test" + (int)(Math.random() * 1000);
            String key = "key";
            List<String> valueFields = new ArrayList<>(5);
            for (int i = 0; i < 5; i++) {
                valueFields.add(i, "field" + i);
            }
            tableCRUDService.createTable(tableName, key, valueFields);
            // desc
            List<Map<String, String>> desc = tableCRUDService.desc(tableName);

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
            if (result.size() > 0) {
                Assert.assertEquals(result.get(0).size(), fieldNameToValue.size());
            }
            System.out.println("tableCRUDService select result: " + result);
            // update
            fieldNameToValue.clear();
            fieldNameToValue.put(key, "key1");
            fieldNameToValueEntry.setFieldNameToValue(fieldNameToValue);
            tableCRUDService.update(tableName, fieldNameToValueEntry, null);
            result = tableCRUDService.select(tableName, condition);
            if (result.size() > 0) {
                Assert.assertTrue(result.get(0).size() == valueFields.size() + 1);
            }
            System.out.println("tableCRUDService select result: " + result);

            // remove
            tableCRUDService.remove(tableName, condition);
            result = tableCRUDService.select(tableName, condition);
            Assert.assertTrue(result.size() == 0);
            System.out.println(
                    "testCRUDPrecompiled tableCRUDService.remove size : " + result.size());
        } catch (ContractException e) {
            System.out.println("testCRUDPrecompiled exceptioned, error info: " + e.getMessage());
        }
    }

    // Note: Please make sure that the ut is before the permission-related ut
    @Test
    public void test51SyncCRUDService() throws ConfigException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            TableCRUDService crudService = new TableCRUDService(client, cryptoKeyPair);
            Random random = new Random();
            String tableName = "test_sync" + random.nextInt(1);
            List<String> valueFiled = new ArrayList<>();
            valueFiled.add("field");
            RetCode retCode = crudService.createTable(tableName, "key", valueFiled);
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
                        new Runnable() {
                            @Override
                            public void run() {
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
            Assert.assertTrue(
                    currentTxCount.compareTo(orgTxCount.add(BigInteger.valueOf(300))) >= 0);
        } catch (ContractException e) {
            System.out.println("test9SyncCRUDService failed, error info: " + e.getMessage());
        }
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

    @Test
    public void test52AsyncCRUDService() {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            TableCRUDService crudService = new TableCRUDService(client, cryptoKeyPair);
            // create table
            String tableName = "send_async" + new Random().nextInt(1);
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
                                FakeTransactionCallback callback =
                                        new FakeTransactionCallback();
                                crudService.asyncInsert(tableName, new Entry(value), callback);
                                // update
                                Condition condition = new Condition();
                                condition.EQ(key, "key" + index);
                                value.clear();
                                value.put("field", "field" + index + 100);
                                FakeTransactionCallback callback2 =
                                        new FakeTransactionCallback();
                                crudService.asyncUpdate(
                                        tableName, new Entry(value), condition, callback2);
                                // remove
                                FakeTransactionCallback callback3 =
                                        new FakeTransactionCallback();
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
            Assert.assertTrue(
                    currentTxCount.compareTo(orgTxCount.add(BigInteger.valueOf(300))) >= 0);
        } catch (ContractException | InterruptedException e) {
            System.out.println("test10AsyncCRUDService failed, error info: " + e.getMessage());
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index  > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    @Test
    public void test6DeployWasm() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            DeployWasmService deployWasmService = new DeployWasmService(client, cryptoKeyPair);
            String helloBin =
            "0061736d0100000001460c60027f7f0060037f7f7f0060017f0060037f7f7f017f60047f7f7f7f006000017f60057f7f7f7f7f0060000060017f017f60047f7f7f7f017f60027f7f017f60027f7f017e0277070462636f730a73657453746f7261676500040462636f730672657665727400000462636f730666696e69736800000462636f730a67657453746f7261676500030462636f730463616c6c00030462636f730f67657443616c6c4461746153697a6500050462636f730b67657443616c6c4461746100020346450100010a010002000001010402040406090b02000508010501040606000101000202000003090507020002020207020000000801000101000000030003000103020301000305030100110609017f01418080c0000b072604066d656d6f7279020009686173685f74797065002d066465706c6f79002e046d61696e00340adb70456601017f230041106b220324000240200141004e0440027f2002450440200341086a20011008200328020c210220032802080c010b20032001410110092003280204210220032802000b22010d010b000b2000200136020020002002360204200341106a24000b3301017f230041106b22022400200241086a200141001009200020022802083602002000200228020c360204200241106a24000b4a01017f4101210302402001450440410021010c010b20014101100a21030240200204402003450d0120032001104a0c020b20030d010b410021030b20002001360204200020033602000b9c0101027f230041106b2202240002402000200041046a22034d0440200341016b220020034d0d010b000b20004102762100200241d482c00028020036020c0240200020012002410c6a104622030d0020022000200110454100210320022802000d0020022802042203200228020c3602082002200336020c200020012002410c6a104621030b41d482c000200228020c360200200241106a240020030bd10101037f230041106b22032400024002400240200141004e0440200228020022040d0120032001100820032802042104200328020021020c020b20004101360200200041086a41003602000c020b20022802042205450440200341086a200141001009200328020c2104200328020821020c010b20014101100a2202450440410021020c010b20022004200510481a20042005100c200121040b2000027f200204402000200236020441000c010b200020013602044101210441010b360200200041086a20043602000b200341106a24000b850201037f200004402001200141046a22034d4100200341016b20034d1b450440000b41d482c0002802002103200041086b220120012802002204417e71360200024002402004417c71220220006b20024d044020004100360200200041046b280200417c712202450d0120022d00004101710d01200110472002280200210020012d000041027104402002200041027222003602000b200321012000417c71220020026b41086b20004d0d020b000b02402004417c712202450d004100200220044102711b2202450d0020022d00004101710d0020002002280208417c7136020020022001410172360208200321010c010b200020033602000b41d482c00020013602000b0b2601017f024020002802004100200028020422001b2201450d002000450d0020012000100c0b0b6f00024002400240200141054f0440200141056b0e020102030b20004204370200200041086a20013602000f0b20004205370200200041086a41053602000f0b2000428580808010370200200041086a41003602000f0b2000428680808010370200200041086a200141076b3602000ba70101037f230041206b220224000240200120002802042203200028020822046b4b0440200120046a22012004490d01200320036a22042003490d012004200120012004491b22014108200141084b1b2101024020030440200241186a410136020020022003360214200220002802003602100c010b200241003602100b20022001200241106a100b20022802004101460d01200020022902043702000b200241206a24000f0b000b3301017f20002002100f2000280208220320002802006a2001200210481a2003200220036a22014b0440000b200020013602080b4701027f230041106b22032400200341086a200241001007200328020821042000200328020c3602042000200436020020042001200210481a20002002360208200341106a24000b15002001200346044020002002200110481a0f0b000bc60101047f024020000d0020000d00024002402000450d002000450d000c010b410021000b0340024041002000047f20000d0141000541000b22006b22020d022002450d02200041d482c0006a22002c00002203417f4a0d02200020026a220421012002410147044020002d00011a200041026a21010b200341ff017141e001490d0220042200200147047f200141016a210020012d00000541000b1a200341ff017141f001490d022000200447044020002d00001a0b0c020b200041016b21000c000b000b000bec0101067f410121074101210403400240024002402004220620056a22042006490d00200220044d0d01200520086a22092008490d00200220094d0d0002400240200120046a2d00002204200120096a2d000022094b2003710d00200420094f200372450d0020042009460d01200641016a22042006490d024101210741002105200621080c050b200541016a22042005490d01200420066a22042006490d01200420086b220720044b0d010c030b2005200541016a22054b0d002006210420052007470d032004200420076a22044d0d020b000b20002007360204200020083602000f0b410021050c000b000b3701017f230041106b22042400200441086a41002003200120021016200020042802083602002000200428020c360204200441106a24000b36000240200120024d0440200220044d04402002200220016b2204490d02200020043602042000200120036a3602000f0b000b000b000ba00201077f4101210941012104034002400240024002402004220720056a22042007490d00200120044d0d03200741016a22042007490d00200420056a22082004490d00200120086b220820014b0d00200120084d0d00200a41016a2206200a490d002006200520066a22064b0d00200120066b220620014b0d00200120064d0d0002400240200020086a2d00002208200020066a2d000022064b2003710d00200620084d200372450d0020062008460d0141012109410021052007210a0c040b200541016a22042005490d01200420076a22042007490d012004200a6b220920044b0d010c020b2005200541016a22054b0d0020052009470440200721040c030b200720096a220420074f0d010b000b410021050b20022009470d010b0b200a0b2b01017e034020010440200141016b210142012000310000862002842102200041016a21000c010b0b20020b5e01047f230041206b22012400200041086a2202280200210320002802042104200141186a200228020036020020012000290200370310200141086a200141106a101a200128020c220020033b01e00120002004360200200141206a24000b3901027f2001280200220241016b220320024d0440200020033602002000200128020420012802084102746a41e4016a2802003602040f0b000b2101017f419402101c2200450440000b200041003b01e2012000410036020020000b080020004104100a0bb30101047f230041206b2203240020012f01e2012104200341003a001820032004360214200341003602102003200341106a2204290200370200200341086a200441086a28020036020020032d0008210620032802042105200328020021040340024020060d00200420054b0d00200320013602142003200236021020032004360218200420054f2106200420042005496a2104200341106a10190c010b0b2000200136020420002002360200200341206a24000b2101017f41e401101c2200450440000b200041003b01e2012000410036020020000b6c01037f230041106b2203240020002802082104200028020422002f01e2012105200341086a200141086a28020036020020032001290200370300200041046a200541016a220120042003102020004188016a20012004200241001021200020013b01e201200341106a24000b6e01017f0240200241016a22042002490d00200120044b04402001200120026b2201490d012001200141016b2201490d0120002004410c6c6a20002002410c6c6a2001410c6c10490b20002002410c6c6a22002003290200370200200041086a200341086a2802003602000f0b000b6201017f0240200241016a22052002490d00200120054b04402001200120026b2201490d012001200141016b2201490d01200020054103746a200020024103746a200141037410490b200020024103746a220020043a0004200020033602000f0b000b9c0201057f230041106b2206240020002802082105200028020422072f01e2012108200641086a200141086a28020036020020062001290200370300200741046a200841016a220920052006102020074188016a200920052002200310210240200541016a22012005490d00200141016a22032001490d00200841026a220220034b0440200220016b220520024b0d012005200541016b2205490d01200741e4016a220820034102746a200820014102746a200541027410490b200720014102746a41e4016a2004360200200720093b01e20120012002200120024b1b210220002802002100034020012002470440200620073602042006200036020020062001360208200141016a2101200610190c010b0b200641106a24000f0b000b5101037f0240200128020022022001280204460440410021020c010b20012002410c6a3602002001280208220341016a220420034f0440200120043602080c010b000b20002002360204200020033602000b5801027f02400240200228020022030440200141016a22042001490d022000200336020420002004360200200020022f01e0013602080c010b200041003602040b41940241e40120011b2200044020022000100c0b0f0b000b4501017f230041106b2203240041d882c00041003602002003200228020036020c2003410c6a410410262003102720002001200328020020032802041000200341106a24000b7101037f230041106b220224000240024041d882c000280200220320016a22042003490d002004418080014b0d00200241086a20032004103d2002280208200228020c20002001101241d882c000280200220020016a220120004f0d010b000b41d882c0002001360200200241106a24000b3701017f230041106b22012400200141086a41d882c0002802001044200020012802083602002000200128020c360204200141106a24000b2a01017f230041106b220124002001200010292001280200200128020810012001100d200141106a24000bb70102037f017e230041106b22032400024002402001280208220241046a220420024f04402001280200210120032004410010072003290300210520004100360208200020053702002002413f4d044020002002410274103f0c030b200241ffff004d0440200320024102744101723b010e20002003410e6a410210100c030b200241ffffffff034b0d012002410274410272200010400c020b000b20004103103f2002200010400b2000200120021010200341106a24000b2d01017f200128020041016a220241004c0440000b20012002360200200020013602042000200141046a3602000be70101057f230041206b22032400027f02402000411c6a2802002204450d00200041186a2802002106034020042f01e2012100200341003602182003200441046a2205360210200320052000410c6c6a360214024002400340200341086a200341106a1023200328020c2207450d012003280208210502402001200220072802002007280208102c4118744118750e020301000b0b200521000b2006450d022003200036021820032004360214200320063602102003200341106a101a20032802042104200328020021060c010b0b200420054103746a4188016a0c010b41000b200341206a24000b30002000200220032001200120034b1b104b2200450440417f200120034720012003491b0f0b417f410120004100481b0b040041000b9c0401067f230041d0016b22002400200041186a102f200041a8016a4100103002400240024020002d00a801450440200041c8016a200041b4016a280200360200200020002902ac013703c001200041186a1031200041d4006a10312000419c016a220228020041016a220141004c0d022000200136029c012001200141016b22034c21010240200041a4016a2d00002204410247044020010d052000200336029c012004450d01200041106a210120022802000440000b2002417f360200200120023602042001200241046a360200200028021022032d00044102460d0520002802142101200328020022032d0000410247044041d882c000410036020020004198016a2802002104200028029001200020032d00003a00cf01200041cf016a41011026200041086a102720042000280208200028020c10000b2001280200220341016a22042003480d052001200436020020022802000440000b2002417f360200200020023602042000200241046a36020020002802042102200028020022012d00044102470440200141003a00040b2002280200220141016a22032001480d05200220033602000c010b20010d042000200336029c010b0c010b200041c0016a41c082c00041141011200041c0016a10280b200041c0016a100d200041186a1032200041d4006a103220004190016a100d200041a4016a2d00004102470440200041a0016a2802004101100c0b200041d0016a24000f0b000b000b3c002000418082c000103b2000413c6a418882c000103b200041f8006a419082c000410610112000418c016a41023a000020004184016a41003602000b990202027f017e230041306b22022400024002401005220341034b0d002001450d0020004181083b01000c010b2002200310422002280200220310062000027f02402001044020022002280208220136021420022003360210024020014104490d002002410036021c200241106a2002411c6a410410430d00200241206a20022802141042200241106a2002280220220120022802281043450d02200241206a100d0b200041003a000141010c020b200041003a0000200041046a2002290300370200200041106a41003602002000410c6a200241086a2802003602000c020b20022902242104200041106a200228021c360200200041086a2004370200200041046a200136020041000b3a00002002100d0b200241306a24000baf0901087f23004180016b22012400024002400240200028020c41016a220341004a04402000410c6a21022000200336020c2003200341016b22034c21040240200041146a2d00002207410247044020040d05200220033602002007450d01200141c8006a210320022802000440000b2002417f360200200320023602042003200241046a360200200128024822032d00044102460d05200128024c210420032802002203280200410146044020002802002000280208200341046a10250b2004280200220741016a22032007480d0520042003360200200141406b210320022802000440000b2002417f360200200320023602042003200241046a36020020012802442107200128024022022d00044102470440200241003a00040b2007280200220241016a22032002480d05200720033602000c010b20040d04200220033602000b410021042000412c6a28020041016a220241004c0d012000200236022c200041346a280200220545044041002105410021020c030b20052104200041306a28020022022103034020052f01e2012108200345044020020d05200041386a28020021020c040b2002450d04200141003602582001200436025420012003360250200141386a200141d0006a101a200128023c210420012802382103200120083602582001200536025420012002360250200141306a200141d0006a101a20012802342105200128023021020c000b000b000b000b200041186a2107200141e4006a2008360200200141e0006a200536020020014200370358200120043602542001200336025003400240200204402001200241016b360268200141d0006a410020012802541b22062802002105200628020821042006280204210203400240200420022f01e201490d0020022802002203450d002005200541016a22054b0d0520022f01e0012104200321020c010b0b200441016a21082005450440200221030c020b200120083602782001200236027420012005360270200141286a200141f0006a101a200128022c21032001280228210503402005450440410021080c030b200141003602782001200336027420012005360270200141206a200141f0006a101a20012802242103200128022021050c000b000b200028022c220341016b220220034e0d022000200236022c20014180016a24000f0b2006200336020420064100360200200620083602080240200220044103746a2203418c016a2d0000450d0020034188016a28020022052802004101470d00200141186a2106200722032802000440000b2003417f360200200620033602042006200341046a360200200128021c2106200128021820022004410c6c6a220241046a2802002002410c6a28020010102006280200220441016a22022004480d0220062002360200200141106a2003102a20012802142106200128021022022802002002280208200541046a10252006280200220441016b220220044e0d0220062002360200200141086a210220032802000440000b2007417f360200200220073602042002200741046a360200200128020c210420002802282203200128020822022802084d0440200220033602080b2004280200220341016a22022003480d02200420023602000b200128026821020c000b000b000b2e002000100d200041146a2d00004102470440200041106a2802004108100c0b2000411c6a100d200041306a10330b9b0301067f230041306b22012400200028020421032000410036020402402003450d0020002802002102034020020440200141003602282001200336022420012002360220200141186a200141206a101a200128021c2103200128021821020c010b0b2000280208210603402006450440410021020340200141206a20022003102420012802242203450d03200128022021020c000b000b200641016b210641002104200521002003210203400240024020022f01e20120004b0440200041016a21052004450440200221030c020b200120053602282001200236022420012004360220200141106a200141206a101a200128021421032001280210210403402004450440410021050c030b200141003602282001200336022420012004360220200141086a200141206a101a200128020c2103200128020821040c000b000b200141206a200420021024200128022422030d01410021030b20022000410c6c6a41046a100d200220004103746a4188016a2802004108100c0c020b2001280228210020012802202104200321020c000b000b000b200141306a24000b980f02087f017e230041d0036b22002400200041d0006a102f200041c0026a41011030024002400240027f02400240024002400240024020002d00c0024101470440200041e8016a200041cc026a2802002201360200200020002902c40222083703e0012000200041d0026a2802003602840220004190026a200136020020002008370388020240024020004184026a280000419682c00028000047044020004184026a280000419a82c000280000460d0120004184026a280000419e82c000280000460d0220004188026a100d200041d0006a103541a282c0002102411021010c0c0b200020002802900236029c02200020002802880236029802200041c0026a20004198026a103620002802c002450d08200041f8016a200041c8026a2201280200360200200020002903c0023703f001200041c0026a20004198026a1036024020002802c0020440200041a8026a2001280200360200200020002903c0023703a002200041186a20004198026a10372000280218450d01200041a0026a100d0b200041f0016a100d0c090b200028021c210120004198036a2202200041a8026a280200360200200020002903a0023703900320002802f0012205450d0820002902f4012108200041b4026a200228020036020020002000290390033702ac02200020083702a402200020053602a002200020013602b802200041b0036a20004198026a103620002802b003450d07200041a8036a200041b8036a280200360200200020002903b0033703a003200041106a20004198026a103820002d0010410171450440200041c0026a20004198026a103620002802c002450d07200041c8036a200041c8026a2201280200360200200020002903c0023703c003200041c0026a20004198026a1036024020002802c0020440200041f8016a2001280200360200200020002903c0023703f001200041086a20004198026a10372000280208450d01200041f0016a100d0b200041c0036a100d0c080b20004198036a200041f8016a280200360200200020002903f0013703900320002802c003450d0720002802a003450d08200041d8026a200041b8026a280200360200200041d0026a200041b0026a290300370300200041c8026a200041a8026a290300370300200020002903a0023703c002200041d0006a200041c0026a2201410c6a2001280218103c000b200041a0036a100d0c070b200041b0026a4100360200200041a8026a4200370300200042003703a002200041a0026a10391a200041d0026a4100360200200041c8026a4200370300200042003703c002200041c0026a1039450d020c040b20002000280290023602b40320002000280288023602b003200041f0016a200041b0036a103620002802f001450d06200041c8036a200041f8016a22012802002203360200200020002903f00122083703c00320012003360200200020083703f0012008a72106027f20030440200041c8006a2006200341001014200028024c20002802482101200041406b20062003410110142000280244200041386a20062003200120002802402204200120044b22041b2201101520041b220420016a22072004490d03200028023c210220002802382105200041306a20042007200620031016027f200028023021072000280234200246047f200520072002104b450541000b450440200320016b220220034b0d0520012002200120024b1b220241016a22042002490d05417f2105200620031018210820012102417f0c010b410021052003200620032004410010172202200620032004410110172207200220074b1b6b220220034b0d04200041286a20062003200410152000280228200028022c1018210820030b2107200041fc026a2003360200200041f4026a4100360200200041e8026a2007360200200041e4026a2005360200200041dc026a4200370200200041d8026a2004360200200041d4026a2002360200200041d0026a2001360200200041c8026a2008370300200020063602f802200041d482c0003602f002200041013602c0022008422088a7210120030c010b200041fc026a4100360200200041f4026a4100360200200041cc026a4181023b0100200041c8026a4100360200200020063602f802200041d482c0003602f002200042003703c0024101210141000b2102024020030440200041c8026a2101200041e4026a280200417f470440200041a0026a20012002103a0c020b200041a0026a20012002103a0c010b20002802c40222020d03200141ff01710440200041a8026a2002360200200020023602a402200041013602a0020c010b200041003602a0020b20002802a0022101200041f0016a100d200041206a410141001007200041003602c802200020002903203703c002200020013a00a002200041c0026a200041a0026a4101101020002802c00220002802c8021002200041c0026a100d0c030b200041d0006a10350c070b000b20021013000b41030c030b200041a0036a100d0b200041a0026a100d200041ac026a100d0b41010b210520004188026a100d200041d0006a103541a282c0002102411021010240200541016b0e03000103020b41b282c0002102410e21010c010b41c082c0002102411421010b200041d0006a200220011011200041d0006a1028200041d0006a100d0b200041d0036a24000b3100200010322000413c6a1032200041f8006a100d2000418c016a2d0000410247044020004188016a2802004101100c0b0b8209020a7f017e230041306b22032400200341106a200110380240027f02400240027f02400240024020032d00104101710d00024020032d0011220641037122024103470440024002400240200241016b0e020201000b200641fc017141027621020c030b200320063a001d200341013a001c200320013602182003410036022c200341186a2003412c6a410410410d03200328022c220241ffff034d0d03200241027621020c020b200320063a001d200341013a001c20032001360218200341003b012c200341186a2003412c6a410210410d0220032f012c220241ff014d0d02200241027621020c010b200641044f0d01200341086a2001103720032802080d01200328020c220241ffffffff034d0d010b20012802042002490d00200341186a20021042200120032802182204200328022010430440200341186a100d0c010b4100200329021c220c422088a7220541076b2201200120054b1b2106200441036a417c7120046b210b410021010340200120054f04402005210620040c080b024002400240200120046a2d00002209411874411875220a41004e0440200b417f460d03200b20016b4103710d030340200120064f0d03200120046a220241046a280200200228020072418081828478710d032001200141086a22014d0d000b0c010b418002210741012108024002400240024002402009418080406b2d000041026b0e030002010e0b200141016a22022005490d02410021070c0c0b41002107200141016a220220054f0d0b200220046a2d000021020240024002400240200941f0016b0e050100000002000b200241bf014b0d0c200a410f6a41ff017141024b0d0c200241187441187541004e0d0c0c020b200241f0006a41ff017141304f0d0b0c010b2002411874411875417f4a0d0a2002418f014b0d0a0b200141026a220220054f0d0b200220046a2d000041c00171418001470d0841002108200141036a220220054f0d0c200220046a2d000041c00171418001460d024180060c0a0b41002107200141016a220220054f0d0a200220046a2d00002102024002400240200941e001470440200941ed01460d01200a411f6a41ff0171410c490d02200241bf014b0d0c200a41fe017141ee01470d0c200241187441187541004e0d0c0c030b200241e0017141a001470d0b0c020b2002411874411875417f4a0d0a200241a0014f0d0a0c010b2002411874411875417f4a0d09200241bf014b0d090b41002108200141026a220220054f0d0b200220046a2d000041c00171418001470d070c010b200220046a2d000041c00171418001470d0a0b200241016a21010c030b000b20012005200120054b1b2102034020012002460440200221010c030b200120046a2c00004100480d02200141016a21010c000b000b200141016a21010c000b000b200041003602000c060b4180040c010b4180020b2107410121080c010b410021080b2007200872210620010b2102200120054f04402000200c370204200020043602000c010b200341286a2006360200200320023602242003200c37021c20032004360218200341186a100d200041003602000b200341306a24000b4801027f230041106b220224002002410036020c024020012002410c6a41041043450440200228020c21010c010b410121030b2000200136020420002003360200200241106a24000b3f01027f230041106b22022400200241003a000f200020012002410f6a410110432201047f41000520022d000f0b3a0001200020013a0000200241106a24000b7601037f230041206b22012400200141086a410441001007200128020c21022001280208220341bf8a9e947c3600002001200336021020014104360218200120023602142000200128021041041004220045044041d882c000410036020020014100103e0b200141106a100d200141206a24002000450b4801017f024002402002200241016b2202490d0020012802081a20012802101a20012903001a20012802142203200220036a4b0d00200141003602140c010b000b200041003602000be00102027f017e230041306b220224002000200141081011200041146a41023a00002000410036020c200241086a4109410010072002410036022820022002290308370320200241206a200141081010200228022822012002280224460440200241206a4101100f200228022821010b200228022020016a41243a00002001200141016a22034b0440000b200241186a2003360200200220022903202204370310200041003602182000411c6a2004370200200041246a2003360200200041286a4109360200200041346a42003702002000412c6a4100360200200241306a24000ba11202177f027e230041c0016b22032400200041186a21100240034020034180016a20011029027f20102003280280012215200328028801220e102b22040440200441013a00042004280200220441046a410020042802004101461b0c010b200341f8006a2105201022042802000440000b2004417f360200200520043602042005200441046a360200200328027c210520032802782015200e10102005280200220741016a22062007480d0220052006360200200341f0006a2004102a2003280270220528020021062005280208210520032802742107200341e8006a410041d882c000280200103d027e42810620062005200328026810032205450d001a2005418180014f0d0341d882c0002005360200200341e0006a2005103e200320032903603703b001200341d8006a200341b0016a10372003280258410047ad200335025c422086840b211a2007280200220541016b220620054e0d0220072006360200200341d0006a210520042802000440000b2010417f360200200520103602042005201041046a3602002003280254210420002802282205200328025022072802084d0440200720053602080b2004280200220541016a22072005480d022004200736020041084104100a2207450d022007201a4220883e02042007201aa7417f734101713602000240024002400240200028022c4504402000417f36022c200341c8006a200e41001007200328024c211220032802482015200e1048210b2003200e3602a801200320123602a4012003200b3602a001027f20002802342206044020002802300c010b2000101e22063602342000410036023041000b21080240034020062f01e2012104200341003602b8012003200641046a22053602b001200320052004410c6c6a3602b40102400340200341406b200341b0016a10232003280244220c450d01200328024021050240200b200e200c280200200c280208102c4118744118750e020401000b0b200521040b20080440200320043602b801200320063602b401200320083602b001200341386a200341b0016a101a200328023c2106200328023821080c010b0b200341003602900120032006ad2004ad4220868437029401024020062f01e201410b4f0440200341b0016a2004100e20032802b801211420032802b401210d20032802b0012105101e210c20062f01e201220f20056b2208200f4b0d0a2008200841016b2204490d0a200c20043b01e201200541016a220a2005490d0a200f200f200a6b2208490d0a200620054103746a22114188016a280200210f2011418c016a2d000020062005410c6c6a220941046a2802002111200941086a290200211a200341306a2109200c41046a21132004410c4f0440000b200920043602042009201336020020082003280234470d0a20032802302006200a410c6c6a41046a2008410c6c10481a200341286a2109200c4188016a21132004410c4f0440000b20092004360204200920133602002008200328022c470d0a20032802282006200a4103746a4188016a200841037410481a200620053b01e201200320143602a8012003200c2006200d1b3602a40141002104200341003602a0012003200e3602b801200320123602b4012003200b3602b001200341a0016a200341b0016a2007101f41017121094100210a034020062802002205450d02200441016a22072004490d0b200320062f01e00122043602980120032005360294012003200736029001200741016b200a470d0b2009211420052f01e201410b490d06200341b0016a2004100e20032802b801211620032802b40120032802b001210620052f01e201101b210820052f01e201220b20066b2204200b4b0d0b2004200441016b2204490d0b200820043b01e201200641016a220a2006490d0b200b200a6b2209200b4b0d0b20052006410c6c6a220b41086a290200211b200b41046a280200210b200520064103746a220d4188016a2802002112200d418c016a2d00002119200341206a210d200841046a21172004410c4f0440000b200d2004360204200d201736020020092003280224470d0b20032802202005200a410c6c6a41046a2009410c6c10481a200341186a210d20084188016a21172004410c4f0440000b200d2004360204200d20173602002009200328021c470d0b20032802182005200a4103746a4188016a200941037410481a200520063b01e20141016a2206200a6b220420064b0d0b20082f01e201220641016a21092006410c4f0d0520042009470d0b200841e4016a2005200a4102746a41e4016a200441027410481a200341106a20082007101d200328021421062003280210210a2007210420052108044020062108200a21040b20194101712109200320163602a801200320083602a401200320043602a0012003201a3702b401200320113602b001200341a0016a200341b0016a200f2014200c102220072104200b2111201b211a2006210c2012210f200521060c000b000b2003200e3602b801200320123602b4012003200b3602b00120034190016a200341b0016a2007101f0c050b20002802342205450d0820002802302104101b220720053602e4012004200441016a22054b0d08200341086a20072005101d200328020821052000200328020c220436023420002005360230200541016b220720054b0d082007200a470d0820042f01e2012207410a4b0d082004200741016a22063b01e20120042007410c6c6a220841086a201a370200200841046a2011360200200420074103746a2207418c016a20093a000020074188016a200f360200200420064102746a41e4016a200c360200200320063602b801200320043602b401200320053602b001200341b0016a10190c040b200341a0016a100d200620054103746a2204418c016a22052d0000200541003a000020044188016a22042802002105200420073602004101714102460d0420054108100c0c040b000b000b2003201a3702b401200320113602b00120034190016a200341b0016a200f2014200c10220b2000280238220441016a22052004490d03200020053602380b200028022c220441016a22052004480d022000200536022c410020102015200e102b2204450d001a200441013a00042004280200220441046a410020042802004101461b0b210420034180016a100d200404402004280200220520026a22072005490d02200420073602000c010b0b000b000b3d01017f0240200120024d04402002418080014d04402002200220016b2203490d02200020033602042000200141dc82c0006a3602000f0b000b000b000b5801027f230041106b22022400200241086a41d882c0002802001044200228020821032001200228020c4b0440000b20022001360204200220033602002000200228020036020020002002280204360204200241106a24000b2601017f230041106b22022400200220013a000f20002002410f6a41011010200241106a24000b2601017f230041106b220224002002200036020c20012002410c6a41041010200241106a24000b4201017f20002f01042103200041003a0004200341017145044020002802002001200210430f0b200120034108763a00002000280200200141016a200241016b10430b3602017f017e230041106b22022400200241086a200141011007200229030821032000200136020820002003370200200241106a24000b3901027f2000280204220320024922044504402001200220002802002201200210122000200320026b3602042000200120026a3602000b20040b4b01027f230041106b22022400200241086a21032001418080014b0440000b20032001360204200341dc82c000360200200020022802083602002000200228020c360204200241106a24000bd20101017f0240200141ffffffff03712001470d002002200241406b22034b0d00200341ffffffff01712003470d002001410274220120034103742202200120024b1b220241086a22012002490d00027f024020012001418080046a22024d0440200241016b220120024d0d010b000b2001411076220240002201417f4604404100210141010c010b200141ffff03712001470d012002411074220241086b20024b0d012001411074220142003702042001200120026a41027236020041000b210220002001360204200020023602000f0b000baf04010a7f20004102742106410020016b2108200041ffffffff03712000472109200141016b220a20014b210b20022802002100024003402000450d01200021010240034002402001280208220041017145044020090d032001280200417c712203200141086a22056b220420034b0d0320042006490d01200320066b220c20034b0d03200b0d03200541086a22042005490d032004200441406b22044b0d03024020042008200c7122044b04402005200a710d0320022000417c7136020020012001280200410172360200200121000c010b200441086b220020044b0d04200320006b220220034b0d04200241086b20024b0d04200041003602082000420037020020002001280200417c71360200024020012802002202417c712203450d004100200320024102711b2202450d00200220022802044103712000723602040b2000200028020441037120017236020420012001280208417e71360208200120012802002202410371200072220336020002402002410271450440200028020021010c010b20012003417d713602002000200028020041027222013602000b200020014101723602000b200041086a21070c050b20012000417e71360208027f41002001280204417c712200450d001a4100200020002d00004101711b0b21002001104720012d00004102710440200020002802004102723602000b200220003602002000280200417c71220120006b41086b20014b0d02200021010c010b0b200220003602000c010b0b000b20070b7d01027f024020002802002201417c712202450d004100200220014102711b2201450d00200120012802044103712000280204417c71723602040b200020002802042201417c712202047f200220022802004103712000280200417c717236020020002802040520010b410371360204200020002802004103713602000b2b01017f03402002200346450440200020036a200120036a2d00003a0000200341016a21030c010b0b20000b6a0002402002200020016b4d044003402002450d02200020012d00003a0000200241016b2102200141016a2101200041016a21000c000b000b200141016b2101200041016b210003402002450d01200020026a200120026a2d00003a0000200241016b21020c000b000b0b2201017f034020012002470440200020026a41003a0000200241016a21020c010b0b0b3f01027f0340200245044041000f0b200241016b210220012d0000210320002d00002104200041016a2100200141016a210120032004460d000b200420036b0b0ba1020300418080c0000b800101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010041c281c0000b3302020202020202020202020202020202020202020202020202020202020203030303030303030303030303030303040404040400418082c0000b546d617070696e67316d617070696e673276616c7565316d255fc320455807c49202e3756e6b6e6f776e2073656c6563746f72696e76616c696420706172616d73636f756c64206e6f74207265616420696e70757400e002046e616d6501d8024c000966696d706f72742430010966696d706f72742431020966696d706f72742432030966696d706f72742433040966696d706f72742434050966696d706f72742435060966696d706f727424360701300801310901320a01330b01340c01350d01360e01370f01381001391102313012023131130231341402313715023138160231391702323218023233190232341a0232351b0232361c0232371d0232381e0232391f023334200233352102333622023337230233382402333925023430260234312702343228023433290234342a0234372b0234382c0234392d0235322e0235332f023534300235353102353632023537330235383402353935023631360236323702363338023634390236363a0236373b0236383c0237303d0237313e0237323f023733400237344102373542023736430237374402373845023832460238334702383448023836490238374a0238384b023839";
            byte[] code = hexStringToBytes(helloBin);
            byte[] params = new byte[0];
            RetCode retCode = deployWasmService.deployWasm(code, params, "/test/usr/local/Hello2", "");
            Assert.assertTrue(retCode.getCode() == 0);
            FunctionEncoder functionEncoder = new FunctionEncoder(client.getCryptoSuite());

            Function function =
                    new Function(
                            "get",
                            Arrays.<Type>asList(),
                            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                            }));
            byte[] encodedFunctionData = functionEncoder.encode(function);
            TransactionProcessor transactionProcessor = TransactionProcessorFactory.createTransactionProcessor(client, cryptoKeyPair);
            CallRequest callRequest =
                    new CallRequest(
                           "123", "/test/usr/local/Hello2", encodedFunctionData);
            Call response = transactionProcessor.executeCall(callRequest);
            // get value from the response
            String callResult = response.getCallResult().getOutput();
        } catch (ClientException | ContractException e) {
            System.out.println(
                    "testDeployWasmPrecompiled exceptioned, error inforamtion:" + e.getMessage());
        }
    }

    @Test
    public void test7BFSPrecompiled() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = BcosSDK.build(configFile);
            Client client =
                    sdk.getClientByEndpoint(sdk.getConfig().getNetworkConfig().getPeers().get(0));
            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
            BFSService bfsService = new BFSService(client, cryptoKeyPair);
            RetCode mkdir1 = bfsService.mkdir("/test/test1/temp");
            RetCode mkdir2 = bfsService.mkdir("/test/test2/temp");
            String list = bfsService.list("/test");
            System.out.println(list);
        } catch (ClientException | ContractException e) {
            System.out.println(
                    "testBFSPrecompiled exceptioned, error inforamtion:" + e.getMessage());
        }
    }

    //    @Test
    //    public void test7ContractLifeCycleService() throws ConfigException {
    //        try {
    //            BcosSDK sdk = BcosSDK.build(configFile);
    //            Client client = sdk.getClientByGroupID("1");
    //            CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
    //            ContractLifeCycleService contractLifeCycleService =
    //                    new ContractLifeCycleService(client, cryptoKeyPair);
    //            // deploy a helloWorld
    //            HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);
    //            String orgValue = helloWorld.get();
    //            contractLifeCycleService.freeze(helloWorld.getContractAddress());
    //            // call the contract
    //            TransactionReceipt receipt = helloWorld.set("Hello, Fisco");
    //
    //            // get contract status
    //            contractLifeCycleService.getContractStatus(helloWorld.getContractAddress());
    //
    //            // unfreeze the contract
    //            contractLifeCycleService.unfreeze(helloWorld.getContractAddress());
    //            String value = helloWorld.get();
    //            Assert.assertTrue(value.equals(orgValue));
    //
    //            helloWorld.set("Hello, Fisco1");
    //            value = helloWorld.get();
    //            System.out.println("==== after set: " + value);
    //            // Assert.assertTrue("Hello, Fisco1".equals(value));
    //            // grant Manager
    //            CryptoSuite cryptoSuite1 =
    //                    new CryptoSuite(client.getCryptoSuite().getCryptoTypeConfig());
    //            CryptoKeyPair cryptoKeyPair1 = cryptoSuite1.createKeyPair();
    //            ContractLifeCycleService contractLifeCycleService1 =
    //                    new ContractLifeCycleService(client, cryptoKeyPair1);
    //            // freeze contract without grant manager
    //            RetCode retCode =
    // contractLifeCycleService1.freeze(helloWorld.getContractAddress());
    //            Assert.assertTrue(retCode.equals(PrecompiledRetCode.CODE_INVALID_NO_AUTHORIZED));
    //            // grant manager
    //            contractLifeCycleService.grantManager(
    //                    helloWorld.getContractAddress(), cryptoKeyPair1.getAddress());
    //            // freeze the contract
    //            retCode = contractLifeCycleService1.freeze(helloWorld.getContractAddress());
    //            receipt = helloWorld.set("Hello, fisco2");
    //            //            Assert.assertTrue(
    //            //                    new BigInteger(receipt.getStatus().substring(2), 16)
    //            //                            .equals(BigInteger.valueOf(30)));
    //
    //            // unfreeze the contract
    //            contractLifeCycleService1.unfreeze(helloWorld.getContractAddress());
    //            helloWorld.set("Hello, fisco3");
    //            Assert.assertTrue("Hello, fisco3".equals(helloWorld.get()));
    //        } catch (ContractException | ClientException e) {
    //            System.out.println("testContractLifeCycleService failed, error info:" +
    // e.getMessage());
    //        }
    //    }
}
