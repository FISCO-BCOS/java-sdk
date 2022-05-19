package org.fisco.bcos.sdk.transaction.manager;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.junit.Assert;
import org.junit.Test;

public class ContractConstructorTest {

    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String abiFile = "src/integration-test/resources/abi/";
    private static final String binFile = "src/integration-test/resources/bin/";
    // init the sdk, and set the config options.
    private BcosSDK sdk = BcosSDK.build(configFile);
    // group 1
    private Client client = sdk.getClient(Integer.valueOf(1));
    private CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();

    @Test
    public void test2ComplexDeploy() throws Exception {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        client, cryptoKeyPair, abiFile, binFile);
        // deploy
        List<Object> params = Lists.newArrayList();
        params.add("1");
        params.add("2");
        params.add("3");
        params.add("4");

        String abi = FileUtils.readFileToString(new File(abiFile + "CheckInfoManager.abi"));
        String bin = FileUtils.readFileToString(new File(binFile + "CheckInfoManager.bin"));

        TransactionResponse txResponse =
                transactionProcessor.deployAndGetResponse(abi, bin, params);
        // System.out.println(JsonUtils.toJson(txResponse));
        Assert.assertEquals("0x0", txResponse.getTransactionReceipt().getStatus());

        TransactionResponse response =
                transactionProcessor.deployByContractLoader("CheckInfoManager", params);
        // System.out.println(JsonUtils.toJson(response));
        Assert.assertEquals("0x0", response.getTransactionReceipt().getStatus());
    }
}
