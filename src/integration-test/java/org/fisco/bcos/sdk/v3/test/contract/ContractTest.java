package org.fisco.bcos.sdk.v3.test.contract;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.contract.solidity.Incremental;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.nonce.DefaultNonceAndBlockLimitProvider;
import org.fisco.bcos.sdk.v3.transaction.nonce.NonceAndBlockLimitProvider;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.UUID;

public class ContractTest {
    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String GROUP = "group0";
    private final Client client;
    private Incremental incremental;
    private final NonceAndBlockLimitProvider nonceAndBlockLimitProvider =
            new DefaultNonceAndBlockLimitProvider();

    public ContractTest() throws ContractException {
        // init the sdk, and set the config options.
        BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
        // group
        client = sdk.getClient("group0");
    }

    void deployContract() throws ContractException {
        incremental = Incremental.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
    }

    @Test
    public void testV2Contract() throws ContractException {
        if (!client.isSupportTransactionV2()) {
            return;
        }
        deployContract();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        BigInteger blockLimit = nonceAndBlockLimitProvider.getBlockLimit(client);
        TransactionReceipt receipt =
                incremental
                        .buildMethodInc(uuid)
                        .setNonce(null)
                        .setBlockLimit(blockLimit)
                        .setExtension(uuid.getBytes())
                        .send();
        Assert.assertTrue(receipt.isStatusOK());
        String msg = incremental.getIncEventEvents(receipt).get(0).msg;
        Assert.assertEquals(msg, uuid);

        JsonTransactionResponse jsonTransactionResponse =
                client.getTransaction(receipt.getTransactionHash(), true).getTransaction().get();
        Assert.assertFalse(jsonTransactionResponse.getNonce().isEmpty());
        Assert.assertEquals(jsonTransactionResponse.getBlockLimit(), blockLimit.longValue());

        String nonce = nonceAndBlockLimitProvider.getNonce();
        TransactionReceipt receipt1 = incremental
                .buildMethodInc(nonce)
                .setNonce(nonce)
                .setBlockLimit(blockLimit)
                .setExtension("hello".getBytes())
                .send();
        Assert.assertTrue(receipt1.isStatusOK());
        JsonTransactionResponse jsonTransactionResponse1 = client.getTransaction(receipt1.getTransactionHash(), true).getTransaction().get();
        Assert.assertEquals(jsonTransactionResponse1.getNonce(), Hex.toHexString(nonce.getBytes()));
        Assert.assertEquals(jsonTransactionResponse1.getBlockLimit(), blockLimit.longValue());
        Assert.assertEquals(new String(jsonTransactionResponse1.getExtension()), "hello");
    }
}
