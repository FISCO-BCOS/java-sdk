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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.test.transaction.mock.RemoteSignCallbackMock;
import org.fisco.bcos.sdk.v3.test.transaction.mock.RemoteSignProviderMock;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionWithRemoteSignProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.v3.utils.Hex;
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
    private List<Object> params = new ArrayList<>(Collections.singletonList("test"));
    // prepare sdk， read from the config file
    private BcosSDK sdk = BcosSDK.build(configFile);
    // set the group number 1
    private Client client = this.sdk.getClient("group0");

    // create new keypair
    private CryptoKeyPair cryptoKeyPair = this.client.getCryptoSuite().getCryptoKeyPair();
    // mock remote sign service
    private RemoteSignProviderInterface remoteSignProviderMock =
            new RemoteSignProviderMock(this.client.getCryptoSuite());

    public AssembleTransactionWithRemoteSignProcessorTest() {
    }

    @Test
    public void test1HelloWorldSync() throws Exception {
        // build processor
        AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor =
                TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
                        this.client,
                        this.cryptoKeyPair,
                        abiFile,
                        binFile,
                        this.remoteSignProviderMock);
        // read HelloWorld contract abi and binary from the config file path
        String abi =
                assembleTransactionWithRemoteSignProcessor
                        .getContractLoader()
                        .getABIByContractName("HelloWorld");
        String bin = assembleTransactionWithRemoteSignProcessor.getContractLoader().getBinaryByContractName("HelloWorld");

        ContractCodec contractCodec = new ContractCodec(client.getCryptoSuite().getHashImpl(), client.isWASM());

        long transactionData = TransactionBuilderJniObj.createTransactionData(
                "group0",
                "chain0",
                "",
                Hex.toHexString(contractCodec.encodeConstructor(abi, bin, params)),
                abi,
                client.getBlockLimit().longValue());

        String rawTxHash = TransactionBuilderJniObj.calcTransactionDataHash(client.getCryptoSuite().cryptoTypeConfig, transactionData);

        SignatureResult signatureResult = remoteSignProviderMock.requestForSign(Hex.decode(rawTxHash), this.client.getCryptoSuite().cryptoTypeConfig);

        String signedTransaction = TransactionBuilderJniObj.createSignedTransaction(transactionData, Hex.toHexString(signatureResult.encode()), rawTxHash, 0);

        CompletableFuture<TransactionReceipt> receiptCompletableFuture = new CompletableFuture<>();
        assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(signedTransaction, new TransactionCallback() {
            @Override
            public void onResponse(TransactionReceipt receipt) {
                receiptCompletableFuture.complete(receipt);
            }
        });

        TransactionReceipt transactionReceipt = receiptCompletableFuture.get();

        Assert.assertEquals(transactionReceipt.getStatus(), 0);
        Assert.assertFalse(transactionReceipt.getContractAddress().isEmpty());
        String abi1 = client.getABI(transactionReceipt.getContractAddress()).getABI();
        Assert.assertEquals(abi1, abi);
    }

    @Test
    public void test2HelloWorldAsync() throws Exception {
        // build processor
        AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor =
                TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
                        this.client,
                        this.cryptoKeyPair,
                        abiFile,
                        binFile,
                        this.remoteSignProviderMock);
        // read HelloWorld contract abi and binary from the config file path
        String abi =
                assembleTransactionWithRemoteSignProcessor
                        .getContractLoader()
                        .getABIByContractName("HelloWorld");
        String bin =
                assembleTransactionWithRemoteSignProcessor
                        .getContractLoader()
                        .getBinaryByContractName("HelloWorld");

        // function1: deploy sync
        TransactionResponse response =
                assembleTransactionWithRemoteSignProcessor.deployByContractLoader(
                        "HelloWorld", new ArrayList<>());
        System.out.println("--- finish deploy with  sync ---");
        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String helloWorldAddress = response.getContractAddress();
        Assert.assertTrue(
                StringUtils.isNotBlank(response.getContractAddress())
                        && !StringUtils.equalsIgnoreCase(
                        helloWorldAddress,
                        "0x0000000000000000000000000000000000000000000000000000000000000000"));

        // function2: deploy async with callback
        long transactionData =
                assembleTransactionWithRemoteSignProcessor.getRawTransactionForConstructor(
                        abi, bin, new ArrayList<>());
        // mock a sign callback
        RemoteSignCallbackMock callbackMock =
                new RemoteSignCallbackMock(
                        assembleTransactionWithRemoteSignProcessor, transactionData, 0);
        System.out.println(System.currentTimeMillis() + " begin to deploy: ");
        assembleTransactionWithRemoteSignProcessor.deployAsync(transactionData, callbackMock);
        // will return first, and the hook function would be called async.
        System.out.println("--- finish deploy with callback async ---");

        // function3: deploy async with CompletableFuture
        CompletableFuture<TransactionReceipt> future =
                assembleTransactionWithRemoteSignProcessor.deployAsync(abi, bin, new ArrayList<>());
        // if normal.
        future.thenAccept(
                tr -> {
                    System.out.println("deploy succeed time " + System.currentTimeMillis());
                    Assert.assertEquals(0, tr.getStatus());
                });
        // if exceptional.
        future.exceptionally(
                e -> {
                    System.out.println("deploy failed:" + e.getMessage());
                    return null;
                });
        System.out.println("--- finish deploy with CompletableFuture ---");

        // function4: send transaction async with callback
        long sendTxRawTransaction =
                assembleTransactionWithRemoteSignProcessor.getRawTransaction(
                        helloWorldAddress, abi, "set", this.params);
        // create an instance of Remote Sign Service callback, and define the hook function.
        RemoteSignCallbackMock callbackMock2 =
                new RemoteSignCallbackMock(
                        assembleTransactionWithRemoteSignProcessor, sendTxRawTransaction, 0);
        System.out.println(System.currentTimeMillis() + " begin to send tx with callback: ");
        assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                helloWorldAddress, abi, "set", this.params, callbackMock2);

        // function5: async send with CompletableFuture
        CompletableFuture<TransactionReceipt> future2 =
                assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                        helloWorldAddress, abi, "set", this.params);
        // if exceptional
        future2.exceptionally(
                e -> {
                    System.out.println("send tx failed:" + e.getMessage());
                    return null;
                });

        // wait for the async thread
        Thread.sleep(1000);
    }
}
