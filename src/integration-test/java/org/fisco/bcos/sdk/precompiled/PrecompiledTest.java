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

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.BcosSDKTest;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.config.ConfigException;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsInfo;
import org.fisco.bcos.sdk.contract.precompiled.cns.CnsService;
import org.fisco.bcos.sdk.contract.precompiled.consensus.ConsensusService;
import org.fisco.bcos.sdk.contract.precompiled.contractmgr.ContractLifeCycleService;
import org.fisco.bcos.sdk.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.contract.precompiled.permission.ChainGovernanceService;
import org.fisco.bcos.sdk.contract.precompiled.permission.PermissionInfo;
import org.fisco.bcos.sdk.contract.precompiled.permission.PermissionService;
import org.fisco.bcos.sdk.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.demo.contract.HelloWorld;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrecompiledTest
{
    private static final String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.yaml").getPath();
    @Test
    public void test1ConsensusService() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            ConsensusService consensusService = new ConsensusService(client, sdk.getCryptoInterface());
            // get the current sealerList
            List<String> sealerList = client.getSealerList().getResult();

            // select the node to operate
            String selectedNode = sealerList.get(0);

            // addSealer
            Assert.assertTrue(PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST.equals(consensusService.addSealer(selectedNode)));

            // add the sealer to the observerList
            RetCode retCode = consensusService.addObserver(selectedNode);
            // query the observerList
            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                List<String> observerList = client.getObserverList().getResult();
                Assert.assertTrue(observerList.contains(selectedNode));
                // query the sealerList
                sealerList = client.getSealerList().getResult();
                Assert.assertTrue(!sealerList.contains(selectedNode));
                // add the node to the observerList again
                Assert.assertTrue(consensusService.addObserver(selectedNode).equals(PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST));
            }
            // add the node to the sealerList again
            retCode = consensusService.addSealer(selectedNode);

            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(client.getSealerList().getResult().contains(selectedNode));
                Assert.assertTrue(!client.getObserverList().getResult().contains(selectedNode));
            }

            // removeNode
            retCode = consensusService.removeNode(selectedNode);
            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(!client.getObserverList().getResult().contains(selectedNode));
                Assert.assertTrue(!client.getSealerList().getResult().contains(selectedNode));
            }

            // add the node to observerList again
            retCode = consensusService.addObserver(selectedNode);
            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(client.getObserverList().getResult().contains(selectedNode));
                Assert.assertTrue(!client.getSealerList().getResult().contains(selectedNode));
            }

            // add the node to the sealerList again
            retCode = consensusService.addSealer(selectedNode);
            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(client.getSealerList().getResult().contains(selectedNode));
                Assert.assertTrue(!client.getObserverList().getResult().contains(selectedNode));
            }
        }
        catch(ClientException|ContractException e)
        {
            System.out.println("testConsensusPrecompiled exceptioned, error info:" + e.getMessage());
        }
    }

    @Test
    public void test2CnsService() throws ConfigException {
        try {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            HelloWorld helloWorld = HelloWorld.deploy(client, sdk.getCryptoInterface());
            String contractAddress = helloWorld.getContractAddress();
            String contractName = "HelloWorld";
            String contractVersion = "1.0";
            CnsService cnsService = new CnsService(client, sdk.getCryptoInterface());
            RetCode retCode = cnsService.registerCNS(contractName, contractVersion, contractAddress, "");
            // query the cns information
            List<CnsInfo> cnsInfos = cnsService.selectByName(contractName);
            Assert.assertTrue(cnsInfos.get(0).getAbi().equals(""));
            Assert.assertTrue(cnsInfos.get(0).getVersion().equals(contractVersion));

            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode())
            {
                boolean containContractAddress = false;
                for(CnsInfo cnsInfo : cnsInfos) {
                    if(cnsInfo.getAddress().equals(contractAddress))
                    {
                        containContractAddress = true;
                    }
                }
                Assert.assertTrue(containContractAddress);
            }
            Assert.assertTrue(cnsInfos.get(0).getName().equals(contractName));

            // query contractAddress
            cnsService.getContractAddress(contractName, contractVersion);
            // insert another cns info
            String contractVersion2 = "2.0";
            retCode = cnsService.registerCNS(contractName, contractVersion2, contractAddress, "");

            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                List<CnsInfo> cnsInfos2 = cnsService.selectByName(contractName);
                Assert.assertTrue(cnsInfos2.size() == cnsInfos.size() + 1);

                Assert.assertTrue(cnsService.selectByNameAndVersion(contractName, contractVersion2).getVersion().equals(contractVersion2));

                Assert.assertTrue(cnsInfos2.contains(cnsService.selectByNameAndVersion(contractName, contractVersion)));
                Assert.assertTrue(cnsInfos2.contains(cnsService.selectByNameAndVersion(contractName, contractVersion2)));

                Assert.assertTrue(cnsService.getContractAddress(contractName, contractVersion).equals(contractAddress));
                Assert.assertTrue(cnsService.getContractAddress(contractName, contractVersion2).equals(contractAddress));
            }
            // insert anther cns for other contract
            HelloWorld helloWorld2 = HelloWorld.deploy(client, sdk.getCryptoInterface());
            String contractAddress2 = helloWorld2.getContractAddress();
            String contractName2 = "hello";
            retCode = cnsService.registerCNS(contractName2, contractVersion, contractAddress2, "");
            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(cnsService.getContractAddress(contractName, "abc").equals(""));
                Assert.assertTrue(cnsService.getContractAddress(contractName2, contractVersion).equals(contractAddress2));
                Assert.assertTrue(cnsService.getContractAddress(contractName, contractVersion).equals(contractAddress));
            }
        }
        catch(ContractException e)
        {
            System.out.println("testCnsPrecompiled failed for " + e.getMessage());
        }
    }

    @Test
    public void test3SystemConfigService() throws ConfigException, ContractException {
        try
        {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            SystemConfigService systemConfigService = new SystemConfigService(client, sdk.getCryptoInterface());
            testSystemConfigService(client, systemConfigService, "tx_count_limit");
            testSystemConfigService(client, systemConfigService,"tx_gas_limit");
        }
        catch(ClientException | ContractException e)
        {
            System.out.println("testSystemConfigPrecompiled exceptioned, error inforamtion:" + e.getMessage());
        }
    }

    private void testSystemConfigService(Client client, SystemConfigService systemConfigService, String key) throws ContractException {
        BigInteger value = new BigInteger(client.getSystemConfigByKey(key).getSystemConfig());
        BigInteger updatedValue = value.add(BigInteger.valueOf(1000));
        String updatedValueStr = String.valueOf(updatedValue);
        systemConfigService.setValueByKey(key, updatedValueStr);

        BigInteger queriedValue = new BigInteger(client.getSystemConfigByKey(key).getSystemConfig());
        Assert.assertTrue(queriedValue.equals(updatedValue));
        Assert.assertTrue(queriedValue.equals(value.add(BigInteger.valueOf(1000))));
    }

    @Test
    public void test5CRUDService() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            TableCRUDService tableCRUDService = new TableCRUDService(client, sdk.getCryptoInterface());
            // create a user table
            String tableName = "test";
            String key = "key";
            List<String> valueFields = new ArrayList<>(5);
            for (int i = 0; i < 5; i++) {
                valueFields.add(i, "field" + i);
            }
            tableCRUDService.createTable(tableName, key, valueFields);

            // insert
            Map<String, String> fieldNameToValue = new HashMap<>();
            for (int i = 0; i < valueFields.size(); i++) {
                fieldNameToValue.put("field" + i, "value" + i);
            }
            Entry fieldNameToValueEntry = new Entry(fieldNameToValue);
            tableCRUDService.insert(tableName, key, fieldNameToValueEntry, null);
            // select
            List<Map<String, String>> result = tableCRUDService.select(tableName, key, null);
            // field value result + key result
            Assert.assertTrue(result.get(0).size() == fieldNameToValue.size() + 1);
            // update
            fieldNameToValue.clear();
            fieldNameToValueEntry.setFieldNameToValue(fieldNameToValue);
            tableCRUDService.update(tableName, key, fieldNameToValueEntry, null);
            result = tableCRUDService.select(tableName, key, null);
            Assert.assertTrue(result.get(0).size() == valueFields.size() + 1);

            // remove
            tableCRUDService.remove(tableName, key, null);
            result = tableCRUDService.select(tableName, key, null);
            Assert.assertTrue(result.size() == 0);

            // desc
            tableCRUDService.desc(tableName);
        }
        catch(ContractException e)
        {
            System.out.println("testCRUDPrecompiled exceptioned, error info: " + e.getMessage());
        }
    }

    @Test
    public void test6PermissionService() throws ConfigException, ContractException {
        try {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            CryptoInterface cryptoInterface = sdk.getCryptoInterface();
            PermissionService permissionService = new PermissionService(client, cryptoInterface);

            String tableName = "test";
            permissionService.grantPermission(tableName, cryptoInterface.getCryptoKeyPair().getAddress());

            // insert data to the table with the account without permission
            CryptoInterface invalidCryptoInterface = new CryptoInterface(sdk.getCryptoInterface().getCryptoTypeConfig());
            TableCRUDService tableCRUDService = new TableCRUDService(client, invalidCryptoInterface);
            String key = "key2";
            Map<String, String> value = new HashMap<>(5);
            for(int i = 0; i < 5; i++)
            {
                value.put("field" + i, "value2"+i);
            }
            RetCode retCode = tableCRUDService.insert(tableName, key, new Entry(value), null);
            Assert.assertTrue(retCode.getCode() == PrecompiledRetCode.CODE_NO_AUTHORIZED.getCode());
            Assert.assertTrue(retCode.getMessage() == PrecompiledRetCode.CODE_NO_AUTHORIZED.getMessage());

            // insert data to the table with the account with permission
            TableCRUDService tableCRUDService2 = new TableCRUDService(client, cryptoInterface);
            retCode = tableCRUDService2.insert(tableName, key, new Entry(value), null);
            Assert.assertTrue(retCode.getCode() == 1);

            // revoke permission
            permissionService.revokePermission(tableName, cryptoInterface.getCryptoKeyPair().getAddress());
            retCode = tableCRUDService.insert(tableName, key, new Entry(value), null);
            Assert.assertTrue(retCode.getCode() == 1);
        }catch(ContractException e)
        {
            System.out.println("testPermissionPrecompiled exceptioned, error info: " + e.getMessage());
        }
    }

    @Test
    public void test7ContractLifeCycleService() throws ConfigException {
        try {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            CryptoInterface cryptoInterface = sdk.getCryptoInterface();
            ContractLifeCycleService contractLifeCycleService = new ContractLifeCycleService(client, cryptoInterface);
            // deploy a helloWorld
            HelloWorld helloWorld = HelloWorld.deploy(client, cryptoInterface);
            String orgValue = helloWorld.get();
            contractLifeCycleService.freeze(helloWorld.getContractAddress());
            // call the contract
            TransactionReceipt receipt = helloWorld.set("Hello, Fisco");
            BigInteger status = new BigInteger(receipt.getStatus().substring(2), 16);
            Assert.assertTrue(status.equals(BigInteger.valueOf(30)));

            // get contract status
            contractLifeCycleService.getContractStatus(helloWorld.getContractAddress());

            // unfreeze the contract
            contractLifeCycleService.unfreeze(helloWorld.getContractAddress());
            String value = helloWorld.get();
            Assert.assertTrue(value.equals(orgValue));

            helloWorld.set("Hello, Fisco1");
            value = helloWorld.get();
            Assert.assertTrue("Hello, Fisco1".equals(value));
            // grant Manager
            CryptoInterface cryptoInterface1 = new CryptoInterface(sdk.getCryptoInterface().getCryptoTypeConfig());
            ContractLifeCycleService contractLifeCycleService1 = new ContractLifeCycleService(client, cryptoInterface1);
            // freeze contract without grant manager
            RetCode retCode = contractLifeCycleService1.freeze(helloWorld.getContractAddress());
            Assert.assertTrue(retCode.equals(PrecompiledRetCode.CODE_INVALID_NO_AUTHORIZED));
            // grant manager
            contractLifeCycleService.grantManager(helloWorld.getContractAddress(), cryptoInterface1.getCryptoKeyPair().getAddress());
            // freeze the contract
            retCode = contractLifeCycleService1.freeze(helloWorld.getContractAddress());
            receipt = helloWorld.set("Hello, fisco2");
            Assert.assertTrue(new BigInteger(receipt.getStatus().substring(2), 16).equals(BigInteger.valueOf(30)));

            // unfreeze the contract
            contractLifeCycleService1.unfreeze(helloWorld.getContractAddress());
            helloWorld.set("Hello, fisco3");
            Assert.assertTrue("Hello, fisco3".equals(helloWorld.get()));
        }catch(ContractException e)
        {
            System.out.println("testContractLifeCycleService failed, error info:" + e.getMessage());
        }
    }

    @Test
    public void test8GovernanceService() throws ConfigException {
        try {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            CryptoInterface cryptoInterface = sdk.getCryptoInterface();
            ChainGovernanceService chainGovernanceService = new ChainGovernanceService(client, cryptoInterface);

            List<PermissionInfo> orgPermissionInfos = chainGovernanceService.listCommitteeMembers();
            chainGovernanceService.grantCommitteeMember(cryptoInterface.getCryptoKeyPair().getAddress());
            List<PermissionInfo> permissionInfos = chainGovernanceService.listCommitteeMembers();
            Assert.assertTrue(permissionInfos.size() == orgPermissionInfos.size() + 1);

            Assert.assertTrue(chainGovernanceService.queryCommitteeMemberWeight(cryptoInterface.getCryptoKeyPair().getAddress()).equals(BigInteger.valueOf(1)));

            RetCode retCode = chainGovernanceService.grantOperator(cryptoInterface.getCryptoKeyPair().getAddress());
            Assert.assertTrue(retCode.equals(PrecompiledRetCode.CODE_COMMITTEE_MEMBER_CANNOT_BE_OPERATOR));

            // create a new account and grantOperator
            int orgOperatorSize = chainGovernanceService.listOperators().size();
            CryptoInterface cryptoInterface1 = new CryptoInterface(sdk.getCryptoInterface().getCryptoTypeConfig());
            chainGovernanceService.grantOperator(cryptoInterface1.getCryptoKeyPair().getAddress());
            Assert.assertTrue(chainGovernanceService.listOperators().size() == orgOperatorSize + 1);

            // only the committeeMember can freeze account
            CryptoInterface cryptoInterface2 = new CryptoInterface(sdk.getCryptoInterface().getCryptoTypeConfig());
            chainGovernanceService.grantOperator(cryptoInterface2.getCryptoKeyPair().getAddress());
            // create the account
            HelloWorld helloWorld = HelloWorld.deploy(client, cryptoInterface2);
            TransactionReceipt receipt = helloWorld.set("test");
            Assert.assertTrue(receipt.getStatus().equals("0x0"));
            // the operator freeze account failed
            ChainGovernanceService chainGovernanceService1 = new ChainGovernanceService(client, cryptoInterface1);
            retCode = chainGovernanceService1.freezeAccount(cryptoInterface2.getCryptoKeyPair().getAddress());
            Assert.assertTrue(retCode.equals(PrecompiledRetCode.CODE_NO_AUTHORIZED));

            // the committeeMember freeze account succ
            chainGovernanceService.freezeAccount(cryptoInterface2.getCryptoKeyPair().getAddress());
            receipt = helloWorld.set("test_freeze");
            // account frozen: status is 31
            Assert.assertTrue(receipt.getStatus().equals("0x1f"));

            // unfreeze the account
            chainGovernanceService.unfreezeAccount(cryptoInterface2.getCryptoKeyPair().getAddress());
            receipt = helloWorld.set("test_unfreeze");
            Assert.assertTrue(receipt.getStatus().equals("0x0"));
            Assert.assertTrue("test_unfreeze".equals(helloWorld.get()));

            // revoke the committeeMember
            chainGovernanceService.revokeCommitteeMember(cryptoInterface.getCryptoKeyPair().getAddress());
            Assert.assertTrue(chainGovernanceService.listCommitteeMembers().size() == 0);
        }catch(ContractException e)
        {
            System.out.println("test8GovernanceService failed, error info:" + e.getMessage());
        }
    }
}