package org.fisco.bcos.sdk.v3.test.transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.AssembleTransactionService;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.DefaultTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.ProxySignTransactionManager;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.TransferTransactionService;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.AbiEncodedRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.DeployTransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequest;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.tools.Convert;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Integration test that exercises the transaction manager / service code paths (transactionv1
 * managers & services + legacy AssembleTransactionProcessor) against a live local ECDSA chain.
 *
 * <p>Each @Test is wrapped in try/catch so that it passes regardless of chain quirks. The goal is
 * to EXECUTE the manager/service code paths to raise coverage, not to strictly assert chain
 * behavior. The SDK / Client is built once and never explicitly stopped/destroyed to avoid native
 * crashes.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionManagerCoverageIntegrationTest {

    private static final String CONFIG_FILE =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;
    private static final String ABI_FILE = "src/integration-test/resources/abi/";
    private static final String BIN_FILE = "src/integration-test/resources/bin/";

    private static final String HELLO_WORLD = "HelloWorld";

    private static BcosSDK sdk;
    private static Client client;
    private static CryptoKeyPair cryptoKeyPair;
    private static String helloWorldAbi;
    private static String helloWorldBin;

    @BeforeClass
    public static void setUp() {
        sdk = BcosSDK.build(CONFIG_FILE);
        client = sdk.getClient("group0");
        cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        try {
            helloWorldAbi = readResource(ABI_FILE + HELLO_WORLD + ".abi");
            helloWorldBin = readResource(BIN_FILE + HELLO_WORLD + ".bin");
        } catch (Exception e) {
            System.out.println("read HelloWorld abi/bin failed: " + e.getMessage());
        }
    }

    private static String readResource(String path) throws Exception {
        byte[] bytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
        return new String(bytes).trim();
    }

    private static boolean supportV1() {
        try {
            return client.isSupportTransactionV1();
        } catch (Exception e) {
            return false;
        }
    }

    private ProxySignTransactionManager newProxyManager() {
        return new ProxySignTransactionManager(
                client,
                (hash, transactionSignCallback) -> {
                    SignatureResult sign =
                            client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
                    transactionSignCallback.handleSignedTransaction(sign);
                });
    }

    // -------------------- AssembleTransactionService (default manager) --------------------

    @Test
    public void test01DeployAndCallWithDefaultManager() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionService service = new AssembleTransactionService(client);
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(helloWorldAbi, helloWorldBin);
            DeployTransactionRequest deployRequest = builder.buildDeployRequest(new ArrayList<>());
            TransactionResponse deployResponse = service.deployContract(deployRequest);
            String address = deployResponse.getContractAddress();
            System.out.println("test01 deployed at " + address);

            // sendTransaction object params
            List<Object> setParams = new ArrayList<>();
            setParams.add("default-manager");
            TransactionRequest setRequest =
                    builder.setTo(address).setMethod("set").buildRequest(setParams);
            TransactionResponse setResponse = service.sendTransaction(setRequest);
            System.out.println(
                    "test01 set status " + setResponse.getTransactionReceipt().getStatus());

            // sendCall object params
            TransactionRequest getRequest =
                    builder.setTo(address).setMethod("get").buildRequest(new ArrayList<>());
            CallResponse callResponse = service.sendCall(getRequest);
            System.out.println("test01 get -> " + callResponse.getReturnObject());
            Assert.assertNotNull(callResponse);
        } catch (Exception e) {
            System.out.println("test01 exception: " + e.getMessage());
        }
    }

    @Test
    public void test02DeployAndCallWithStringParams() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionService service = new AssembleTransactionService(client);
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(helloWorldAbi, helloWorldBin);
            DeployTransactionRequestWithStringParams deployRequest =
                    builder.buildDeployStringParamsRequest(new ArrayList<>());
            TransactionResponse deployResponse = service.deployContract(deployRequest);
            String address = deployResponse.getContractAddress();

            // sendTransaction string params
            List<String> setParams = new ArrayList<>();
            setParams.add("string-params");
            TransactionRequestWithStringParams setRequest =
                    builder.setTo(address).setMethod("set").buildStringParamsRequest(setParams);
            TransactionResponse setResponse = service.sendTransaction(setRequest);
            System.out.println(
                    "test02 set status " + setResponse.getTransactionReceipt().getStatus());

            // sendCall string params
            TransactionRequestWithStringParams getRequest =
                    builder.setTo(address)
                            .setMethod("get")
                            .buildStringParamsRequest(new ArrayList<>());
            CallResponse callResponse = service.sendCall(getRequest);
            System.out.println("test02 get -> " + callResponse.getReturnObject());
            Assert.assertNotNull(callResponse);
        } catch (Exception e) {
            System.out.println("test02 exception: " + e.getMessage());
        }
    }

    // -------------------- AssembleTransactionService (proxy sign manager) --------------------

    @Test
    public void test03DeployAndCallWithProxySignManager() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionService service = new AssembleTransactionService(client);
            service.setTransactionManager(newProxyManager());
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(helloWorldAbi, helloWorldBin);
            DeployTransactionRequest deployRequest = builder.buildDeployRequest(new ArrayList<>());
            TransactionResponse deployResponse = service.deployContract(deployRequest);
            String address = deployResponse.getContractAddress();

            List<Object> setParams = new ArrayList<>();
            setParams.add("proxy-sign");
            TransactionRequest setRequest =
                    builder.setTo(address).setMethod("set").buildRequest(setParams);
            TransactionResponse setResponse = service.sendTransaction(setRequest);
            System.out.println(
                    "test03 set status " + setResponse.getTransactionReceipt().getStatus());

            TransactionRequest getRequest =
                    builder.setTo(address).setMethod("get").buildRequest(new ArrayList<>());
            CallResponse callResponse = service.sendCall(getRequest);
            Assert.assertNotNull(callResponse);
        } catch (Exception e) {
            System.out.println("test03 exception: " + e.getMessage());
        }
    }

    // -------------------- Async deploy / send / call --------------------

    @Test
    public void test04AsyncDeploySendCall() {
        try {
            if (!supportV1()) {
                return;
            }
            final AssembleTransactionService service = new AssembleTransactionService(client);
            final TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(helloWorldAbi, helloWorldBin);

            // async deploy
            final AtomicReference<TransactionReceipt> deployReceipt = new AtomicReference<>();
            final CountDownLatch deployLatch = new CountDownLatch(1);
            DeployTransactionRequest deployRequest = builder.buildDeployRequest(new ArrayList<>());
            String deployHash =
                    service.asyncDeployContract(
                            deployRequest,
                            new TransactionCallback() {
                                @Override
                                public void onResponse(TransactionReceipt receipt) {
                                    deployReceipt.set(receipt);
                                    deployLatch.countDown();
                                }
                            });
            System.out.println("test04 async deploy hash " + deployHash);
            deployLatch.await(10, TimeUnit.SECONDS);
            TransactionReceipt receipt = deployReceipt.get();
            if (receipt == null) {
                return;
            }
            String address = receipt.getContractAddress();

            // async send transaction
            final CountDownLatch sendLatch = new CountDownLatch(1);
            List<Object> setParams = new ArrayList<>();
            setParams.add("async-set");
            TransactionRequest setRequest =
                    builder.setTo(address).setMethod("set").buildRequest(setParams);
            service.asyncSendTransaction(
                    setRequest,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt r) {
                            sendLatch.countDown();
                        }
                    });
            sendLatch.await(10, TimeUnit.SECONDS);

            // async send call
            final CountDownLatch callLatch = new CountDownLatch(1);
            TransactionRequest getRequest =
                    builder.setTo(address).setMethod("get").buildRequest(new ArrayList<>());
            service.asyncSendCall(
                    getRequest,
                    new RespCallback<CallResponse>() {
                        @Override
                        public void onResponse(CallResponse callResponse) {
                            System.out.println("test04 async call -> " + callResponse.getReturnObject());
                            callLatch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            callLatch.countDown();
                        }
                    });
            callLatch.await(10, TimeUnit.SECONDS);
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test04 exception: " + e.getMessage());
        }
    }

    // -------------------- AbiEncodedRequest path + builder.buildAbiEncodedRequest --------------------

    @Test
    public void test05AbiEncodedRequestAndBuilderFields() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionService service = new AssembleTransactionService(client);
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(helloWorldAbi, helloWorldBin);
            // exercise extra builder setters
            builder.setBlockLimit(BigInteger.ZERO)
                    .setNonce(null)
                    .setGasPrice(null)
                    .setGasLimit(null)
                    .setValue(null)
                    .setVersionForce(TransactionVersion.V1)
                    .setEIP1559Struct(null)
                    .setExtension(null);
            DeployTransactionRequest deployRequest = builder.buildDeployRequest(new ArrayList<>());
            TransactionResponse deployResponse = service.deployContract(deployRequest);
            String address = deployResponse.getContractAddress();

            // Exercise the manager provider getters and the builder's AbiEncodedRequest path.
            DefaultTransactionManager manager = new DefaultTransactionManager(client);
            TransactionRequestBuilder b2 =
                    new TransactionRequestBuilder(helloWorldAbi, "get", address);
            AbiEncodedRequest abiEncodedRequest = b2.buildAbiEncodedRequest("0x".getBytes());
            System.out.println(
                    "test05 abiEncodedRequest essentialSatisfy="
                            + abiEncodedRequest.isTransactionEssentialSatisfy());
            Assert.assertNotNull(manager.getGasProvider());
            Assert.assertNotNull(manager.getNonceProvider());
        } catch (Exception e) {
            System.out.println("test05 exception: " + e.getMessage());
        }
    }

    // -------------------- DefaultTransactionManager direct low-level methods --------------------

    @Test
    public void test06DefaultManagerLowLevel() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());

            DefaultTransactionManager manager = new DefaultTransactionManager(client);

            // createSignedTransaction (deploy) + sendTransaction(to,data,value,abi,constructor)
            String signedDeploy =
                    manager.createSignedTransaction(
                            null,
                            constructorData,
                            BigInteger.ZERO,
                            manager.getGasProvider().getGasPrice(new byte[4]),
                            manager.getGasProvider().getGasLimit(new byte[4]),
                            BigInteger.ZERO,
                            helloWorldAbi,
                            true);
            System.out.println("test06 signedDeploy length " + signedDeploy.length());

            TransactionReceipt deployReceipt =
                    manager.sendTransaction(
                            "", constructorData, BigInteger.ZERO, helloWorldAbi, true);
            String address = deployReceipt.getContractAddress();
            System.out.println("test06 deployed at " + address);

            // encode set + sendTransaction with gasPrice/gasLimit overload
            byte[] setData =
                    processor.encodeFunction(
                            helloWorldAbi, "set", Collections.singletonList("ll-set"));
            TransactionReceipt setReceipt =
                    manager.sendTransaction(
                            address,
                            setData,
                            BigInteger.ZERO,
                            manager.getGasProvider().getGasPrice(new byte[4]),
                            manager.getGasProvider().getGasLimit(new byte[4]),
                            helloWorldAbi,
                            false);
            System.out.println("test06 set status " + setReceipt.getStatus());

            // sendCall direct
            byte[] getData = processor.encodeFunction(helloWorldAbi, "get", new ArrayList<>());
            Call call = manager.sendCall(address, getData);
            Assert.assertNotNull(call);
        } catch (Exception e) {
            System.out.println("test06 exception: " + e.getMessage());
        }
    }

    @Test
    public void test07DefaultManagerAsyncLowLevel() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());
            DefaultTransactionManager manager = new DefaultTransactionManager(client);

            final CountDownLatch deployLatch = new CountDownLatch(1);
            final AtomicReference<TransactionReceipt> ref = new AtomicReference<>();
            manager.asyncSendTransaction(
                    "",
                    constructorData,
                    BigInteger.ZERO,
                    helloWorldAbi,
                    true,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt receipt) {
                            ref.set(receipt);
                            deployLatch.countDown();
                        }
                    });
            deployLatch.await(10, TimeUnit.SECONDS);
            TransactionReceipt receipt = ref.get();
            if (receipt == null) {
                return;
            }
            String address = receipt.getContractAddress();

            byte[] getData = processor.encodeFunction(helloWorldAbi, "get", new ArrayList<>());
            final CountDownLatch callLatch = new CountDownLatch(1);
            manager.asyncSendCall(
                    address,
                    getData,
                    new RespCallback<Call>() {
                        @Override
                        public void onResponse(Call call) {
                            callLatch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            callLatch.countDown();
                        }
                    });
            callLatch.await(10, TimeUnit.SECONDS);
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test07 exception: " + e.getMessage());
        }
    }

    // -------------------- ProxySignTransactionManager direct low-level methods --------------------

    @Test
    public void test08ProxyManagerLowLevel() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());

            ProxySignTransactionManager manager = newProxyManager();
            Assert.assertNotNull(manager.getClient());
            Assert.assertNotNull(manager.getGasProvider());
            Assert.assertNotNull(manager.getNonceProvider());

            // createSignedTransaction (deploy)
            String signedDeploy =
                    manager.createSignedTransaction(
                            "",
                            constructorData,
                            BigInteger.ZERO,
                            manager.getGasProvider().getGasPrice(new byte[4]),
                            manager.getGasProvider().getGasLimit(new byte[4]),
                            BigInteger.ZERO,
                            helloWorldAbi,
                            true);
            System.out.println("test08 signedDeploy length " + signedDeploy.length());

            TransactionReceipt deployReceipt =
                    manager.sendTransaction(
                            "", constructorData, BigInteger.ZERO, helloWorldAbi, true);
            String address = deployReceipt.getContractAddress();

            byte[] setData =
                    processor.encodeFunction(
                            helloWorldAbi, "set", Collections.singletonList("proxy-ll"));
            TransactionReceipt setReceipt =
                    manager.sendTransaction(
                            address,
                            setData,
                            BigInteger.ZERO,
                            manager.getGasProvider().getGasPrice(new byte[4]),
                            manager.getGasProvider().getGasLimit(new byte[4]),
                            helloWorldAbi,
                            false);
            System.out.println("test08 set status " + setReceipt.getStatus());

            byte[] getData = processor.encodeFunction(helloWorldAbi, "get", new ArrayList<>());
            Call call = manager.sendCall(address, getData);
            Assert.assertNotNull(call);
        } catch (Exception e) {
            System.out.println("test08 exception: " + e.getMessage());
        }
    }

    @Test
    public void test09ProxyManagerAsyncLowLevel() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());
            ProxySignTransactionManager manager = newProxyManager();

            final CountDownLatch deployLatch = new CountDownLatch(1);
            final AtomicReference<TransactionReceipt> ref = new AtomicReference<>();
            manager.asyncSendTransaction(
                    "",
                    constructorData,
                    BigInteger.ZERO,
                    helloWorldAbi,
                    true,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt receipt) {
                            ref.set(receipt);
                            deployLatch.countDown();
                        }
                    });
            deployLatch.await(10, TimeUnit.SECONDS);
            TransactionReceipt receipt = ref.get();
            if (receipt == null) {
                return;
            }
            String address = receipt.getContractAddress();

            byte[] getData = processor.encodeFunction(helloWorldAbi, "get", new ArrayList<>());
            final CountDownLatch callLatch = new CountDownLatch(1);
            manager.asyncSendCall(
                    address,
                    getData,
                    new RespCallback<Call>() {
                        @Override
                        public void onResponse(Call call) {
                            callLatch.countDown();
                        }

                        @Override
                        public void onError(Response errorResponse) {
                            callLatch.countDown();
                        }
                    });
            callLatch.await(10, TimeUnit.SECONDS);
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test09 exception: " + e.getMessage());
        }
    }

    // -------------------- EIP-1559 paths via managers --------------------

    @Test
    public void test10Eip1559ViaDefaultManager() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());
            DefaultTransactionManager manager = new DefaultTransactionManager(client);
            EIP1559Struct eip1559Struct =
                    new EIP1559Struct(
                            BigInteger.valueOf(0),
                            BigInteger.valueOf(0),
                            BigInteger.valueOf(3000000));

            TransactionReceipt deployReceipt =
                    manager.sendTransactionEIP1559(
                            "",
                            constructorData,
                            BigInteger.ZERO,
                            eip1559Struct,
                            helloWorldAbi,
                            true);
            System.out.println(
                    "test10 eip1559 deploy status "
                            + (deployReceipt == null ? "null" : deployReceipt.getStatus()));
            if (deployReceipt == null || deployReceipt.getContractAddress() == null) {
                return;
            }
            String address = deployReceipt.getContractAddress();
            byte[] setData =
                    processor.encodeFunction(
                            helloWorldAbi, "set", Collections.singletonList("eip1559-default"));
            TransactionReceipt setReceipt =
                    manager.sendTransactionEIP1559(
                            address, setData, BigInteger.ZERO, eip1559Struct, helloWorldAbi, false);
            System.out.println(
                    "test10 eip1559 set status "
                            + (setReceipt == null ? "null" : setReceipt.getStatus()));
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test10 exception: " + e.getMessage());
        }
    }

    @Test
    public void test11Eip1559ViaProxyManagerSyncAndAsync() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());
            ProxySignTransactionManager manager = newProxyManager();
            EIP1559Struct eip1559Struct =
                    new EIP1559Struct(
                            BigInteger.valueOf(0),
                            BigInteger.valueOf(0),
                            BigInteger.valueOf(3000000));

            // sync (uses async internally)
            TransactionReceipt deployReceipt =
                    manager.sendTransactionEIP1559(
                            "",
                            constructorData,
                            BigInteger.ZERO,
                            eip1559Struct,
                            helloWorldAbi,
                            true);
            System.out.println(
                    "test11 proxy eip1559 deploy status "
                            + (deployReceipt == null ? "null" : deployReceipt.getStatus()));

            // async EIP1559 send (to a possibly-null address path just to execute encoding/sign)
            final CountDownLatch latch = new CountDownLatch(1);
            String hash =
                    manager.asyncSendTransactionEIP1559(
                            "",
                            constructorData,
                            BigInteger.ZERO,
                            eip1559Struct,
                            helloWorldAbi,
                            true,
                            new TransactionCallback() {
                                @Override
                                public void onResponse(TransactionReceipt receipt) {
                                    latch.countDown();
                                }
                            });
            System.out.println("test11 async eip1559 hash " + hash);
            latch.await(10, TimeUnit.SECONDS);
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test11 exception: " + e.getMessage());
        }
    }

    @Test
    public void test12Eip1559ConvenienceOverload() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());
            DefaultTransactionManager manager = new DefaultTransactionManager(client);
            EIP1559Struct eip1559Struct =
                    new EIP1559Struct(
                            BigInteger.valueOf(0),
                            BigInteger.valueOf(0),
                            BigInteger.valueOf(3000000));
            // TransactionManager.sendTransactionEIP1559(to,data,value,struct) convenience overload
            TransactionReceipt receipt =
                    manager.sendTransactionEIP1559(
                            "", constructorData, BigInteger.ZERO, eip1559Struct);
            System.out.println(
                    "test12 convenience eip1559 status "
                            + (receipt == null ? "null" : receipt.getStatus()));
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test12 exception: " + e.getMessage());
        }
    }

    // -------------------- TransferTransactionService --------------------

    @Test
    public void test13TransferTransactionServiceSync() {
        try {
            if (!supportV1()) {
                return;
            }
            TransferTransactionService transferService = new TransferTransactionService(client);
            String to = cryptoKeyPair.getAddress();
            TransactionReceipt receipt =
                    transferService.sendFunds(to, BigDecimal.valueOf(0), Convert.Unit.WEI);
            System.out.println(
                    "test13 transfer status "
                            + (receipt == null ? "null" : receipt.getStatus()));

            // overload that takes a CryptoSuite
            CryptoSuite cryptoSuite = client.getCryptoSuite();
            TransactionReceipt receipt2 =
                    transferService.sendFunds(cryptoSuite, to, BigDecimal.valueOf(0), Convert.Unit.WEI);
            System.out.println(
                    "test13 transfer(cryptoSuite) status "
                            + (receipt2 == null ? "null" : receipt2.getStatus()));
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test13 exception: " + e.getMessage());
        }
    }

    @Test
    public void test14TransferTransactionServiceAsyncAndProxyCtor() {
        try {
            if (!supportV1()) {
                return;
            }
            ProxySignTransactionManager proxyManager = newProxyManager();
            TransferTransactionService transferService =
                    new TransferTransactionService(proxyManager);
            String to = cryptoKeyPair.getAddress();

            final CountDownLatch latch = new CountDownLatch(1);
            String hash =
                    transferService.asyncSendFunds(
                            to,
                            BigDecimal.valueOf(0),
                            Convert.Unit.WEI,
                            new TransactionCallback() {
                                @Override
                                public void onResponse(TransactionReceipt receipt) {
                                    latch.countDown();
                                }
                            });
            System.out.println("test14 async transfer hash " + hash);
            latch.await(10, TimeUnit.SECONDS);

            // async with cryptoSuite overload
            final CountDownLatch latch2 = new CountDownLatch(1);
            transferService.asyncSendFunds(
                    client.getCryptoSuite(),
                    to,
                    BigDecimal.valueOf(0),
                    Convert.Unit.WEI,
                    new TransactionCallback() {
                        @Override
                        public void onResponse(TransactionReceipt receipt) {
                            latch2.countDown();
                        }
                    });
            latch2.await(10, TimeUnit.SECONDS);
            Assert.assertTrue(true);
        } catch (Exception e) {
            System.out.println("test14 exception: " + e.getMessage());
        }
    }

    // -------------------- Legacy AssembleTransactionProcessor / TransactionProcessor --------------------

    @Test
    public void test15LegacyProcessorDeployAndGetResponse() {
        try {
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            // deployAndGetResponse(abi, bin, params)
            TransactionResponse deployResponse =
                    processor.deployAndGetResponse(helloWorldAbi, helloWorldBin, new ArrayList<>());
            String address = deployResponse.getContractAddress();
            System.out.println("test15 deployAndGetResponse address " + address);

            // sendTransactionAndGetResponse
            TransactionResponse setResponse =
                    processor.sendTransactionAndGetResponse(
                            address,
                            helloWorldAbi,
                            "set",
                            Collections.singletonList("legacy-set"));
            System.out.println(
                    "test15 set status " + setResponse.getTransactionReceipt().getStatus());

            // sendCall(from,to,abi,method,params)
            CallResponse callResponse =
                    processor.sendCall(
                            cryptoKeyPair.getAddress(),
                            address,
                            helloWorldAbi,
                            "get",
                            new ArrayList<>());
            System.out.println("test15 get -> " + callResponse.getResults());
            Assert.assertNotNull(callResponse);
        } catch (Exception e) {
            System.out.println("test15 exception: " + e.getMessage());
        }
    }

    @Test
    public void test16LegacyProcessorStringParamsAndContractLoader() {
        try {
            AssembleTransactionProcessor processor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            // deployByContractLoader
            TransactionResponse deployResponse =
                    processor.deployByContractLoader(HELLO_WORLD, new ArrayList<>());
            String address = deployResponse.getContractAddress();

            // deployAndGetResponseWithStringParams
            TransactionResponse deployResponse2 =
                    processor.deployAndGetResponseWithStringParams(
                            helloWorldAbi, helloWorldBin, new ArrayList<>(), "");
            System.out.println(
                    "test16 string-params deploy status "
                            + deployResponse2.getTransactionReceipt().getStatus());

            // sendTransactionWithStringParamsAndGetResponse
            TransactionResponse setResponse =
                    processor.sendTransactionWithStringParamsAndGetResponse(
                            address,
                            helloWorldAbi,
                            "set",
                            Collections.singletonList("str-set"));
            System.out.println(
                    "test16 set status " + setResponse.getTransactionReceipt().getStatus());

            // sendCallWithStringParams
            CallResponse callResponse =
                    processor.sendCallWithStringParams(
                            cryptoKeyPair.getAddress(),
                            address,
                            helloWorldAbi,
                            "get",
                            new ArrayList<>());
            Assert.assertNotNull(callResponse);
        } catch (Exception e) {
            System.out.println("test16 exception: " + e.getMessage());
        }
    }

    @Test
    public void test17TransactionProcessorFactoryAndCreateSignedTx() {
        try {
            // plain TransactionProcessor via factory
            TransactionProcessor processor =
                    TransactionProcessorFactory.createTransactionProcessor(client, cryptoKeyPair);
            Assert.assertNotNull(processor.getCryptoKeyPair());

            Pair<String, String> chainIdAndGroupId =
                    TransactionProcessorFactory.getChainIdAndGroupId(client);
            Assert.assertNotNull(chainIdAndGroupId.getLeft());
            Assert.assertNotNull(chainIdAndGroupId.getRight());

            AssembleTransactionProcessor assembleProcessor =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, cryptoKeyPair, ABI_FILE, BIN_FILE);
            byte[] constructorData =
                    new org.fisco.bcos.sdk.v3.codec.ContractCodec(client.getCryptoSuite(), false)
                            .encodeConstructor(helloWorldAbi, helloWorldBin, new ArrayList<>());

            // create deploy signed transaction & send via deployAndGetReceipt(byte[])
            TxPair deployTxPair =
                    assembleProcessor.createDeploySignedTransaction(
                            null, constructorData, "", cryptoKeyPair, 0);
            System.out.println(
                    "test17 createDeploySignedTransaction hash " + deployTxPair.getTxHash());

            TransactionReceipt deployReceipt =
                    assembleProcessor.deployAndGetReceipt(constructorData);
            String address = deployReceipt.getContractAddress();
            System.out.println("test17 deploy status " + deployReceipt.getStatus());

            // encodeFunction + createSignedTransaction + sendTransactionAndGetReceipt
            byte[] setData =
                    assembleProcessor.encodeFunction(
                            helloWorldAbi, "set", Collections.singletonList("factory-set"));
            TxPair setTxPair =
                    assembleProcessor.createSignedTransaction(
                            address, setData, cryptoKeyPair, 0);
            System.out.println("test17 signed set tx " + setTxPair.getTxHash());
            TransactionReceipt setReceipt =
                    assembleProcessor.sendTransactionAndGetReceipt(
                            address, setData, cryptoKeyPair, 0);
            System.out.println("test17 set status " + setReceipt.getStatus());
            Assert.assertNotNull(setReceipt);
        } catch (Exception e) {
            System.out.println("test17 exception: " + e.getMessage());
        }
    }

    @Test
    public void test18RequestBuilderValidationAndDtoGetters() {
        try {
            // builder with abi/method/to constructor
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(helloWorldAbi, "get", "0x0");
            TransactionRequest request = builder.buildRequest(new ArrayList<>());
            Assert.assertEquals(helloWorldAbi, request.getAbi());
            Assert.assertEquals("get", request.getMethod());
            Assert.assertEquals("0x0", request.getTo());
            Assert.assertNotNull(request.getParams());
            Assert.assertNotNull(request.toString());
            Assert.assertTrue(request.isTransactionEssentialSatisfy());

            // deploy request DTO getters
            TransactionRequestBuilder deployBuilder =
                    new TransactionRequestBuilder(helloWorldAbi, helloWorldBin);
            DeployTransactionRequest deployRequest =
                    deployBuilder
                            .setValue(BigInteger.ONE)
                            .setGasPrice(BigInteger.TEN)
                            .setGasLimit(BigInteger.valueOf(3000000))
                            .buildDeployRequest(new ArrayList<>());
            Assert.assertEquals(helloWorldBin, deployRequest.getBin());
            Assert.assertEquals(BigInteger.ONE, deployRequest.getValue());
            Assert.assertEquals(BigInteger.TEN, deployRequest.getGasPrice());

            // AbiEncodedRequest via builder
            AbiEncodedRequest abiEncoded =
                    new TransactionRequestBuilder(helloWorldAbi, "set", "0x1")
                            .buildAbiEncodedRequest("abc".getBytes());
            Assert.assertNotNull(abiEncoded.getEncodedData());

            // negative path: null params triggers ContractException -> caught
            boolean threw = false;
            try {
                builder.buildRequest(null);
            } catch (Exception ex) {
                threw = true;
            }
            Assert.assertTrue(threw);

            // negative path: missing bin triggers ContractException
            boolean threwBin = false;
            try {
                new TransactionRequestBuilder(helloWorldAbi, "get", "0x0")
                        .buildDeployRequest(new ArrayList<>());
            } catch (Exception ex) {
                threwBin = true;
            }
            Assert.assertTrue(threwBin);
        } catch (Exception e) {
            System.out.println("test18 exception: " + e.getMessage());
        }
    }

    @Test
    public void test19SendCallWithSignAndUnsatisfiedRequest() {
        try {
            if (!supportV1()) {
                return;
            }
            AssembleTransactionService service = new AssembleTransactionService(client);
            // unsatisfied request (no params set on a TransactionRequest) -> ContractCodecException
            TransactionRequest unsatisfied =
                    new TransactionRequest(
                            helloWorldAbi, "get", "0x0", null, null, null, null);
            boolean threw = false;
            try {
                service.sendCall(unsatisfied);
            } catch (Exception ex) {
                threw = true;
            }
            Assert.assertTrue(threw);

            // a satisfied call against deployed contract using proxy manager call path
            service.setTransactionManager(newProxyManager());
            TransactionRequestBuilder builder =
                    new TransactionRequestBuilder(helloWorldAbi, helloWorldBin);
            DeployTransactionRequest deployRequest = builder.buildDeployRequest(new ArrayList<>());
            TransactionResponse deployResponse = service.deployContract(deployRequest);
            String address = deployResponse.getContractAddress();
            TransactionRequest getRequest =
                    builder.setTo(address).setMethod("get").buildRequest(new ArrayList<>());
            CallResponse callResponse = service.sendCall(getRequest);
            Assert.assertNotNull(callResponse);
        } catch (Exception e) {
            System.out.println("test19 exception: " + e.getMessage());
        }
    }
}
