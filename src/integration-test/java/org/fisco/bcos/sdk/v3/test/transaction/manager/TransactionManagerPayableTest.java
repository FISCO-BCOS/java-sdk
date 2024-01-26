package org.fisco.bcos.sdk.v3.test.transaction.manager;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.contract.precompiled.balance.BalanceService;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigFeature;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.test.contract.solidity.HelloWorldPayable;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.AssembleTransactionService;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.ProxySignTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv2.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.tools.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TransactionManagerPayableTest {

    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private final Client client;
    private final AssembleTransactionService transactionService;

    private final String abi = HelloWorldPayable.getABI();
    private final String binary;

    public TransactionManagerPayableTest() {

        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        // group
        client = sdk.getClient("group0");
        transactionService = new AssembleTransactionService(client);
        ProxySignTransactionManager proxySignTransactionManager =
                new ProxySignTransactionManager(
                        client,
                        (hash, transactionSignCallback) -> {
                            SignatureResult sign =
                                    client.getCryptoSuite()
                                            .sign(hash, client.getCryptoSuite().getCryptoKeyPair());
                            transactionSignCallback.handleSignedTransaction(sign);
                        });
        transactionService.setTransactionManager(proxySignTransactionManager);
        binary = HelloWorldPayable.getBinary(client.getCryptoSuite());
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
            client.stop();
            client.destroy();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test1ComplexCodecWithType() throws Exception {
        if ((client.getNegotiatedProtocol() >> 16) < 2) {
            return;
        }
        BalanceService balanceService =
                new BalanceService(client, client.getCryptoSuite().getCryptoKeyPair());

        SystemConfig balanceFeature =
                client.getSystemConfigByKey(
                        SystemConfigFeature.Features.FEATURE_BALANCE.toString());
        if (!"1".equals(balanceFeature.getSystemConfig().getValue())) {
            return;
        }

        BigInteger balance =
                balanceService.getBalance(client.getCryptoSuite().getCryptoKeyPair().getAddress());
        if (balance.compareTo(BigInteger.valueOf(200000)) < 0) {
            System.out.println("balance is not enough");
            return;
        }
        // test deploy payable contract
        List<Object> deployParams = new ArrayList<>();

        TransactionRequestBuilder builder = new TransactionRequestBuilder(abi, binary);
        DeployTransactionRequest request =
                builder.setValue(BigInteger.valueOf(100000)).buildDeployRequest(deployParams);

        TransactionResponse response = transactionService.deployContract(request);

        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        System.out.println(response);

        // test call set payable contract
        {
            List<Object> params = new ArrayList<>();
            params.add("test");

            TransactionRequest transactionRequest =
                    builder.setValue(BigInteger.valueOf(100000))
                            .setMethod("set")
                            .setTo(contractAddress)
                            .buildRequest(params);
            TransactionResponse transactionResponse =
                    transactionService.sendTransaction(transactionRequest);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            System.out.println(JsonUtils.toJson(transactionResponse));
        }

        System.out.println(contractAddress);
    }
}
