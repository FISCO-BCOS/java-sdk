package org.fisco.bcos.sdk.v3.test.transaction.manager;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.SystemConfig;
import org.fisco.bcos.sdk.v3.contract.precompiled.balance.BalanceService;
import org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig.SystemConfigFeature;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.contract.solidity.PayableTest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.AssembleTransactionService;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.ProxySignTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
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

    private final String abi = PayableTest.getABI();
    private final String binary;

    public TransactionManagerPayableTest() throws ContractException {
        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        // group
        client = sdk.getClient("group0");
        transactionService = new AssembleTransactionService(client);
        client.getCryptoSuite()
                .loadKeyPair("5ab8223a7b6656e939a4ebf233e3bcf8e230163d4048b9cb6380e1d9ad555ba9");
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
        binary = PayableTest.getBinary(client.getCryptoSuite());
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
    public void testPayableTestWithSDKInterface() throws Exception {
        if (checkSkip()) return;

        // test deploy payable contract
        List<Object> deployParams = new ArrayList<>();
        int deployBalance = 10;
        int setBalance = 10;

        TransactionRequestBuilder builder = new TransactionRequestBuilder(abi, binary);
        DeployTransactionRequest request =
                builder.setValue(BigInteger.valueOf(deployBalance)).buildDeployRequest(deployParams);

        TransactionResponse response = transactionService.deployContract(request);

        Assert.assertEquals(response.getTransactionReceipt().getStatus(), 0);
        String contractAddress = response.getContractAddress();
        Assert.assertTrue(StringUtils.isNotBlank(response.getContractAddress()));

        System.out.println(response);

        // test call payable contract
        {
            List<Object> params = new ArrayList<>();
            params.add("test");

            TransactionRequest transactionRequest =
                    builder.setValue(BigInteger.valueOf(setBalance))
                            .setMethod(PayableTest.FUNC_INCWITHPAYABLE)
                            .setTo(contractAddress)
                            .buildRequest(params);
            TransactionResponse transactionResponse =
                    transactionService.sendTransaction(transactionRequest);
            Assert.assertEquals(transactionResponse.getTransactionReceipt().getStatus(), 0);
            System.out.println(JsonUtils.toJson(transactionResponse));
        }
        // test call no payable but set value
        {
            List<Object> params = new ArrayList<>();
            params.add("test");

            TransactionRequest transactionRequest =
                    builder.setValue(BigInteger.valueOf(setBalance))
                            .setMethod(PayableTest.FUNC_INC)
                            .setTo(contractAddress)
                            .buildRequest(params);
            TransactionResponse transactionResponse =
                    transactionService.sendTransaction(transactionRequest);
            Assert.assertFalse(transactionResponse.getTransactionReceipt().isStatusOK());
        }
        BalanceService balanceService =
                new BalanceService(client, client.getCryptoSuite().getCryptoKeyPair());
        BigInteger balance = balanceService.getBalance(contractAddress);
        Assert.assertTrue(balance.compareTo(BigInteger.valueOf(deployBalance + setBalance)) == 0);
    }

    @Test
    public void testPayableTestWithCodeWrapper() throws ContractException {
        if (checkSkip()) return;
        int deployBalance = 10;
        int setBalance = 10;
        PayableTest payableTest =
                PayableTest.deploy(
                        client,
                        client.getCryptoSuite().getCryptoKeyPair(),
                        BigInteger.valueOf(deployBalance));

        TransactionReceipt transactionReceipt =
                payableTest.incWithPayable("test", BigInteger.valueOf(setBalance));

        Assert.assertTrue(transactionReceipt.isStatusOK());

        BalanceService balanceService =
                new BalanceService(client, client.getCryptoSuite().getCryptoKeyPair());
        BigInteger balance = balanceService.getBalance(payableTest.getContractAddress());
        Assert.assertTrue(balance.compareTo(BigInteger.valueOf(deployBalance + setBalance)) == 0);
    }

    private boolean checkSkip() throws ContractException {
        if (!client.isSupportTransactionV1()) {
            return true;
        }
        BalanceService balanceService =
                new BalanceService(client, client.getCryptoSuite().getCryptoKeyPair());

        try {
            SystemConfig balanceFeature =
                    client.getSystemConfigByKey(
                            SystemConfigFeature.Features.FEATURE_BALANCE.toString());
            if (!"1".equals(balanceFeature.getSystemConfig().getValue())) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("getSystemConfigByKey error, skip.");
            return true;
        }

        BigInteger balance =
                balanceService.getBalance(client.getCryptoSuite().getCryptoKeyPair().getAddress());
        if (balance.compareTo(BigInteger.valueOf(100000)) < 0) {
            System.out.println("balance is not enough");
        }
        return false;
    }
}
