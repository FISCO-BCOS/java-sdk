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
package org.fisco.bcos.sdk.transaction.decoder;

import java.math.BigInteger;
import java.util.*;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.junit.Assert;
import org.junit.Test;

/**
 * TransactionDecoderServiceTest @Description: TransactionDecoderServiceTest
 *
 * @author maojiayu
 * @data Sep 17, 2020 10:36:56 AM
 */
public class TransactionDecoderServiceTest {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    private static final String contractName = "ComplexSol";

    @Test
    public void testDecode() throws Exception {
        BcosSDK sdk = BcosSDK.build(configFile);
        Client client = sdk.getClient(Integer.valueOf(1));
        TransactionDecoderInterface decoder =
                new TransactionDecoderService(client.getCryptoSuite());
        ContractLoader contractLoader = new ContractLoader(abiFile, binFile);
        String abi = contractLoader.getABIByContractName(contractName);
        AssembleTransactionProcessor manager =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        client, client.getCryptoSuite().createKeyPair(), abiFile, binFile);
        // deploy
        List<Object> params = new ArrayList<>();
        params.add(1);
        params.add("test2");
        TransactionResponse response = manager.deployByContractLoader(contractName, params);
        if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
            return;
        }
        String contractAddress = response.getContractAddress();
        Assert.assertEquals(2, response.getInputObject().size());
        // System.out.println(JsonUtils.toJson(response));

        // increment
        TransactionReceipt transactionReceipt =
                manager.sendTransactionAndGetReceiptByContractLoader(
                        contractName,
                        contractAddress,
                        "incrementUint256",
                        Collections.singletonList(BigInteger.ONE));
        TransactionResponse transactionResponseWithoutValues =
                decoder.decodeReceiptWithoutValues(abi, transactionReceipt);
        // System.out.println(JsonUtils.toJson(transactionResponseWithoutValues));
        TransactionResponse transactionResponseWithValues =
                decoder.decodeReceiptWithValues(abi, "incrementUint256", transactionReceipt);
        // System.out.println(JsonUtils.toJson(transactionResponseWithValues));
        Assert.assertEquals("Success", transactionResponseWithValues.getReceiptMessages());
        Map<String, List<List<Object>>> events =
                decoder.decodeEvents(abi, transactionReceipt.getLogs());
        // System.out.println(JsonUtils.toJson(events));
        Assert.assertEquals(1, events.size());
        // setBytes
        List<Object> s = Collections.singletonList("2".getBytes());
        List<Object> paramsSetBytes = new ArrayList<>();
        paramsSetBytes.add(s);
        TransactionReceipt transactionReceipt2 =
                manager.sendTransactionAndGetReceiptByContractLoader(
                        contractName, contractAddress, "setBytesMapping", paramsSetBytes);
        // decode receipt
        TransactionResponse transactionResponse2 = decoder.decodeReceiptStatus(transactionReceipt2);
        Assert.assertEquals(22, transactionResponse2.getReturnCode());
    }
}
