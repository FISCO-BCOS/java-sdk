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
import org.fisco.bcos.sdk.contract.precompiled.exceptions.PrecompiledException;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.contract.precompiled.sysconfig.SystemConfigService;
import org.fisco.bcos.sdk.demo.contract.HelloWorld;
import org.fisco.bcos.sdk.model.RetCode;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class PrecompiledTest
{
    private static final String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.yaml").getPath();
    @Test
    public void testConsensusPrecompiled() throws ConfigException, PrecompiledException {
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
        catch(ClientException|PrecompiledException e)
        {
            System.out.println("testConsensusPrecompiled exceptioned, error info:" + e.getMessage());
        }
    }

    @Test
    public void testCnsPrecompiled() throws ConfigException {
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
            Assert.assertTrue(!contractAddress.equals(contractAddress2));
            String contractName2 = "hello";
            retCode = cnsService.registerCNS(contractName2, contractVersion, contractAddress2, "");
            if(retCode.getCode() == PrecompiledRetCode.CODE_SUCCESS.getCode()) {
                Assert.assertTrue(cnsService.getContractAddress(contractName, "abc").equals(""));
                Assert.assertTrue(cnsService.getContractAddress(contractName2, contractVersion).equals(contractAddress2));
                Assert.assertTrue(cnsService.getContractAddress(contractName, contractVersion).equals(contractAddress));
            }
        }
        catch(ContractException | PrecompiledException e)
        {
            System.out.println("testCnsPrecompiled failed for " + e.getMessage());
        }
    }

    @Test
    public void testSystemConfigPrecompiled() throws ConfigException, PrecompiledException {
        try
        {
            BcosSDK sdk = new BcosSDK(configFile);
            Client client = sdk.getClient(Integer.valueOf(1));
            SystemConfigService systemConfigService = new SystemConfigService(client, sdk.getCryptoInterface());
            testSystemConfigPrecompiled(client, sdk,systemConfigService, "tx_count_limit");
            testSystemConfigPrecompiled(client, sdk, systemConfigService,"tx_gas_limit");
        }
        catch(PrecompiledException|ClientException e)
        {
            System.out.println("testSystemConfigPrecompiled exceptioned, error inforamtion:" + e.getMessage());
        }
    }
    private void testSystemConfigPrecompiled(Client client, BcosSDK sdk, SystemConfigService systemConfigService, String key) throws PrecompiledException {
        BigInteger value = new BigInteger(client.getSystemConfigByKey(key).getSystemConfig());
        BigInteger updatedValue = value.add(BigInteger.valueOf(1000));
        String updatedValueStr = String.valueOf(updatedValue);
        systemConfigService.setValueByKey(key, updatedValueStr);

        BigInteger queriedValue = new BigInteger(client.getSystemConfigByKey(key).getSystemConfig());
        Assert.assertTrue(queriedValue.equals(updatedValue));
        Assert.assertTrue(queriedValue.equals(value.add(BigInteger.valueOf(1000))));
    }
}