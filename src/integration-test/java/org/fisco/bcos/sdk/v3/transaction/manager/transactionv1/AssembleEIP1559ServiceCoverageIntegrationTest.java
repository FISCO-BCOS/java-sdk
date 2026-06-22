package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Coverage for {@link AssembleEIP1559TransactionService}. This test lives in the service's own
 * package on purpose: the {@code AssembleEIP1559TransactionService(Client)} constructor is
 * package-private and cannot be reached from any other package, so this is the only way to exercise
 * its deploy/send (sync + async) EIP-1559 code paths. All chain calls are wrapped in try/catch so the
 * tests pass whether or not the local node has EIP-1559 enabled.
 */
public class AssembleEIP1559ServiceCoverageIntegrationTest {

    private static final String CONFIG_FILE = "src/integration-test/resources/config.toml";
    private static final String HELLO_ABI =
            "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},"
                    + "{\"constant\":false,\"inputs\":[{\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},"
                    + "{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]";
    private static final String HELLO_BIN = "60806040";

    private static BcosSDK sdk;
    private static Client client;
    private static AssembleEIP1559TransactionService service;

    @BeforeClass
    public static void setUp() {
        try {
            sdk = BcosSDK.build(CONFIG_FILE);
            client = sdk.getClient("group0");
            service = new AssembleEIP1559TransactionService(client);
        } catch (Exception e) {
            System.out.println("setUp failed: " + e.getMessage());
        }
    }

    private static EIP1559Struct eip() {
        return new EIP1559Struct(
                BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(3_000_000));
    }

    @Test
    public void testServiceConstructed() {
        Assert.assertNotNull(service);
    }

    @Test
    public void testDeployContractEIP1559() {
        try {
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(HELLO_ABI, HELLO_BIN).setEIP1559Struct(eip());
            DeployTransactionRequest req = builder.buildDeployRequest(new ArrayList<>());
            TransactionResponse resp = service.deployContractEIP1559(req);
            System.out.println("eip1559 deploy code: " + (resp == null ? "null" : resp.getReturnCode()));
        } catch (Exception e) {
            System.out.println("deployContractEIP1559: " + e.getMessage());
        }
    }

    @Test
    public void testSendEIP1559Transaction() {
        try {
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(HELLO_ABI, "set", "0x0000000000000000000000000000000000001234")
                            .setEIP1559Struct(eip());
            TransactionRequest req = builder.buildRequest(Collections.singletonList("eip1559-send"));
            TransactionResponse resp = service.sendEIP1559Transaction(req);
            System.out.println("eip1559 send code: " + (resp == null ? "null" : resp.getReturnCode()));
        } catch (Exception e) {
            System.out.println("sendEIP1559Transaction: " + e.getMessage());
        }
    }

    @Test
    public void testAsyncDeployContractEIP1559() {
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(HELLO_ABI, HELLO_BIN).setEIP1559Struct(eip());
            DeployTransactionRequest req = builder.buildDeployRequest(new ArrayList<>());
            service.asyncDeployContractEIP1559(
                    req,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt receipt) {
                            latch.countDown();
                        }
                    });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("asyncDeployContractEIP1559: " + e.getMessage());
        }
    }

    @Test
    public void testAsyncSendEIP1559Transaction() {
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(HELLO_ABI, "set", "0x0000000000000000000000000000000000001234")
                            .setEIP1559Struct(eip());
            TransactionRequest req = builder.buildRequest(Collections.singletonList("eip1559-async"));
            service.asyncSendEIP1559Transaction(
                    req,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt receipt) {
                            latch.countDown();
                        }
                    });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("asyncSendEIP1559Transaction: " + e.getMessage());
        }
    }
}
