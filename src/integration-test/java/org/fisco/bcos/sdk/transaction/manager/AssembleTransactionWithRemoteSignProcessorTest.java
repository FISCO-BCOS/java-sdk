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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.mock.RemoteSignCallbackMock;
import org.fisco.bcos.sdk.transaction.mock.RemoteSignProviderMock;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * TransactionProcessorTest @Description: TransactionProcessorTest
 *
 * @author maojiayu
 * @data Feb 20, 2021 8:00:11 PM
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssembleTransactionWithRemoteSignProcessorTest {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    String abi =
            "[{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";
    String bin =
            "608060405234801561001057600080fd5b506040805190810160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6102d3806101166000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde03146100515780634ed3885e146100e1575b600080fd5b34801561005d57600080fd5b5061006661014a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b50610148600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506101e8565b005b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101e05780601f106101b5576101008083540402835291602001916101e0565b820191906000526020600020905b8154815290600101906020018083116101c357829003601f168201915b505050505081565b80600090805190602001906101fe929190610202565b5050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024357805160ff1916838001178555610271565b82800160010185558215610271579182015b82811115610270578251825591602001919060010190610255565b5b50905061027e9190610282565b5090565b6102a491905b808211156102a0576000816000905550600101610288565b5090565b905600a165627a7a72305820f320e5eb2a59c810c188f5c3a74faacbea80ffac8d31427bdd05c71b2c51cec10029";

    @Test
    public void test1HelloWorld() throws Exception {
        // prepair
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        System.out.println(client.getCryptoSuite().getCryptoKeyPair().getAddress());
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
        RemoteSignProviderInterface remoteSignProviderMock =
                new RemoteSignProviderMock(client.getCryptoSuite());

        // build processor
        AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor =
                TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
                        client, cryptoKeyPair, abiFile, binFile, remoteSignProviderMock);
        abi =
                assembleTransactionWithRemoteSignProcessor.contractLoader.getABIByContractName(
                        "HelloWorld");
        bin =
                assembleTransactionWithRemoteSignProcessor.contractLoader.getBinaryByContractName(
                        "HelloWorld");

        // deploy sync
        TransactionResponse response =
                assembleTransactionWithRemoteSignProcessor.deployByContractLoader(
                        "HelloWorld", new ArrayList<>());
        System.out.println("--- finish deploy with  sync ---");
        System.out.println(JsonUtils.toJson(response));
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        Assert.assertTrue(response.getReturnCode() == 0);
        Assert.assertEquals("0x0", response.getTransactionReceipt().getStatus());
        String helloWorldAddrss = response.getContractAddress();
        Assert.assertTrue(
                StringUtils.isNotBlank(response.getContractAddress())
                        && !StringUtils.equalsIgnoreCase(
                                helloWorldAddrss,
                                "0x0000000000000000000000000000000000000000000000000000000000000000"));

        // deploy async with callback
        RawTransaction rawTransaction =
                assembleTransactionWithRemoteSignProcessor.getDeployedRawTransaction(
                        abi, bin, new ArrayList<>());
        RemoteSignCallbackMock callbackMock =
                new RemoteSignCallbackMock(
                        assembleTransactionWithRemoteSignProcessor, rawTransaction);
        System.out.println(System.currentTimeMillis() + " begin to deploy: ");
        assembleTransactionWithRemoteSignProcessor.deployAsync(rawTransaction, callbackMock);
        System.out.println("--- finish deploy with callback async ---");

        // deploy async with CompletableFuture
        CompletableFuture<TransactionReceipt> future =
                assembleTransactionWithRemoteSignProcessor.deployAsync(abi, bin, new ArrayList<>());
        future.thenAccept(
                tr -> {
                    System.out.println("deploy succeed time " + System.currentTimeMillis());
                    System.out.println(JsonUtils.toJson(tr));
                    Assert.assertEquals("0x0", tr.getStatus());
                });
        future.exceptionally(
                e -> {
                    System.out.println("deploy failed:" + e.getMessage());
                    return null;
                });
        System.out.println("--- finish deploy with CompletableFuture ---");

        // send transaction sync
        List<Object> params = Lists.newArrayList("test");
        TransactionResponse transactionResponse2 =
                assembleTransactionWithRemoteSignProcessor.sendTransactionAndGetResponse(
                        helloWorldAddrss, abi, "set", params);
        Assert.assertEquals("0x0", transactionResponse2.getTransactionReceipt().getStatus());

        // send transaction async with callback
        RawTransaction sendTxRawTransaction =
                assembleTransactionWithRemoteSignProcessor.getRawTransaction(
                        helloWorldAddrss, abi, "set", params);
        RemoteSignCallbackMock callbackMock2 =
                new RemoteSignCallbackMock(
                        assembleTransactionWithRemoteSignProcessor, sendTxRawTransaction);
        System.out.println(System.currentTimeMillis() + " begin to send tx with callback: ");
        assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                helloWorldAddrss, abi, "set", params, callbackMock2);
        System.out.println("--- finish send tx with callback async ---");

        // deploy async with CompletableFuture
        CompletableFuture<TransactionReceipt> future2 =
                assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                        helloWorldAddrss, abi, "set", params);
        future2.thenAccept(
                tr -> {
                    System.out.println(
                            "send tx async with CompletableFuture succeed time "
                                    + System.currentTimeMillis());
                    System.out.println(JsonUtils.toJson(tr));
                    Assert.assertEquals("0x0", tr.getStatus());
                });
        future2.exceptionally(
                e -> {
                    System.out.println("send tx failed:" + e.getMessage());
                    return null;
                });
        System.out.println("--- finish transaction with CompletableFuture ---");

        // test call
        CallResponse callResponse1 =
                assembleTransactionWithRemoteSignProcessor.sendCallByContractLoader(
                        "HelloWorld", helloWorldAddrss, "name", new ArrayList<>());
        Assert.assertEquals("test", callResponse1.getReturnObject().get(0));
        System.out.println("Call response: " + JsonUtils.toJson(callResponse1));

        Thread.sleep(2000);
    }

    // test old methods
    // @Test
    public void test2HelloWorld() throws Exception {
        // prepair
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        System.out.println(client.getCryptoSuite().getCryptoKeyPair().getAddress());
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
        RemoteSignProviderInterface remoteSignProviderMock =
                new RemoteSignProviderMock(client.getCryptoSuite());

        // build processor
        AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor =
                TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
                        client, cryptoKeyPair, "HelloWorld", remoteSignProviderMock);
        // deploy sync
        TransactionResponse response =
                assembleTransactionWithRemoteSignProcessor.deployAndGetResponse(
                        abi, bin, new ArrayList<>());
        System.out.println(JsonUtils.toJson(response));
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        Assert.assertTrue(response.getReturnCode() == 0);
        Assert.assertEquals("0x0", response.getTransactionReceipt().getStatus());
        String helloWorldAddrss = response.getContractAddress();
        Assert.assertTrue(
                StringUtils.isNotBlank(response.getContractAddress())
                        && !StringUtils.equalsIgnoreCase(
                                helloWorldAddrss,
                                "0x0000000000000000000000000000000000000000000000000000000000000000"));

        // deploy async with CompletableFuture
        CompletableFuture<TransactionReceipt> future =
                assembleTransactionWithRemoteSignProcessor.deployAsync(abi, bin, new ArrayList<>());
        future.thenAccept(
                tr -> {
                    System.out.println("deploy succeed time " + System.currentTimeMillis());
                    System.out.println(JsonUtils.toJson(tr));
                    Assert.assertEquals("0x0", tr.getStatus());
                });
        future.exceptionally(
                e -> {
                    System.out.println("deploy failed:" + e.getMessage());
                    return null;
                });

        System.out.println("--- finish deploy with CompletableFuture ---");

        Thread.sleep(2000);
    }
}
