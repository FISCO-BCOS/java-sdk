package org.fisco.bcos.sdk.v3.test.transaction.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionWithRemoteSignProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.v3.utils.Hex;

public class AssembleTransactionWithRemoteSignProcessorSample {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    // 合约ABI文件目录
    private static final String abiFile = "src/integration-test/resources/abi/";
    // 合约BIN文件目录
    private static final String binFile = "src/integration-test/resources/bin/";
    // prepare sdk， read from the config file
    private final BcosSDK sdk;
    private final Client client;
    // mock remote sign service
    private final RemoteSignProviderInterface remoteSignProviderMock;
    private final String abi;

    private final AssembleTransactionWithRemoteSignProcessor
            assembleTransactionWithRemoteSignProcessor;

    public AssembleTransactionWithRemoteSignProcessorSample()
            throws IOException, NoSuchTransactionFileException {
        // create sdk by config file
        sdk = BcosSDK.build(configFile);
        client = this.sdk.getClient("group0");
        // RemoteSignProviderMock 为mock服务，可以改为调用远程签名服务
        remoteSignProviderMock = new RemoteSignProviderMock(this.client.getCryptoSuite());
        // build processor
        assembleTransactionWithRemoteSignProcessor =
                TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
                        this.client,
                        this.client.getCryptoSuite().getCryptoKeyPair(),
                        abiFile,
                        binFile,
                        this.remoteSignProviderMock);
        // 从contract loader内获取ABI字符串，也可以直接从外部传入
        abi =
                assembleTransactionWithRemoteSignProcessor
                        .getContractLoader()
                        .getABIByContractName("HelloWorld");
    }

    public String deployContract()
            throws NoSuchTransactionFileException, ContractCodecException, JniException,
                    ExecutionException, InterruptedException, TimeoutException {

        // 从contract loader内获取ABI字符串，也可以直接从外部传入
        String abi =
                assembleTransactionWithRemoteSignProcessor
                        .getContractLoader()
                        .getABIByContractName("HelloWorld");
        // 从contract loader内获取BIN字符串，也可以直接从外部传入
        String bin =
                assembleTransactionWithRemoteSignProcessor
                        .getContractLoader()
                        .getBinaryByContractName("HelloWorld");

        /// 1. 准备部署交易参数
        List<Object> params = new ArrayList<>(Collections.singletonList("test"));
        // 部署合约需要bin、abi、构造参数
        /// 2. 构造交易体结构
        // 构造交易结构，long是指针类型，使用完后需要主动释放
        long transactionData =
                assembleTransactionWithRemoteSignProcessor.getRawTransactionForConstructor(
                        abi, bin, params);

        try {

            /// 3. 使用交易体计算交易哈希
            // 返回交易哈希，十六进制字符串
            String rawTxHash =
                    TransactionBuilderJniObj.calcTransactionDataHash(
                            client.getCryptoSuite().cryptoTypeConfig, transactionData);

            /// 4. 请求远程签名服务对交易哈希进行签名
            CompletableFuture<TransactionReceipt> receiptCompletableFuture =
                    new CompletableFuture<>();

            // 发起远程签名请求, 对bytes签名
            remoteSignProviderMock.requestForSignAsync(
                    Hex.decode(rawTxHash),
                    this.client.getCryptoSuite().cryptoTypeConfig,
                    signatureResult -> {

                        /// 5. 获取到签名之后，拼装完整交易，并获取编码后的交易
                        String signedTransaction = null;
                        try {
                            signedTransaction =
                                    TransactionBuilderJniObj.createSignedTransaction(
                                            transactionData,
                                            Hex.toHexString(signatureResult.encode()),
                                            rawTxHash,
                                            0);
                        } catch (JniException e) {
                            throw new RuntimeException(e);
                        }

                        /// 6. 将编码后的交易发送到链上
                        assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                                signedTransaction,
                                new TransactionCallback() {
                                    @Override
                                    public void onResponse(TransactionReceipt receipt) {
                                        receiptCompletableFuture.complete(receipt);
                                    }
                                });
                        return 0;
                    });

            TransactionReceipt transactionReceipt =
                    receiptCompletableFuture.get(10000, TimeUnit.MILLISECONDS);

            String contractAddress = transactionReceipt.getContractAddress();
            if (transactionReceipt.isStatusOK()) {
                System.out.println("部署合约成功，合约地址：" + contractAddress);
            }

            return contractAddress;
        } finally {
            TransactionBuilderJniObj.destroyTransactionData(transactionData);
        }
    }

    public TransactionResponse sendTransaction(String address, List<Object> params)
            throws JniException, ContractCodecException, ExecutionException, InterruptedException,
                    TimeoutException {

        // 1. 准备交易参数
        // 2. 构造交易体
        long transactionData =
                assembleTransactionWithRemoteSignProcessor.getRawTransaction(
                        address, abi, "set", params);

        try {

            /// 3. 使用交易体计算交易哈希
            // 返回交易哈希，十六进制字符串
            String rawTxHash =
                    TransactionBuilderJniObj.calcTransactionDataHash(
                            client.getCryptoSuite().cryptoTypeConfig, transactionData);

            /// 4. 请求远程签名服务对交易哈希进行签名
            CompletableFuture<TransactionReceipt> receiptCompletableFuture =
                    new CompletableFuture<>();

            // 发起远程签名请求, 对bytes签名
            remoteSignProviderMock.requestForSignAsync(
                    Hex.decode(rawTxHash),
                    this.client.getCryptoSuite().cryptoTypeConfig,
                    signatureResult -> {

                        /// 5. 获取到签名之后，拼装完整交易，并获取编码后的交易
                        String signedTransaction = null;
                        try {
                            signedTransaction =
                                    TransactionBuilderJniObj.createSignedTransaction(
                                            transactionData,
                                            Hex.toHexString(signatureResult.encode()),
                                            rawTxHash,
                                            0);
                        } catch (JniException e) {
                            throw new RuntimeException(e);
                        }

                        /// 6. 将编码后的交易发送到链上
                        assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                                signedTransaction,
                                new TransactionCallback() {
                                    @Override
                                    public void onResponse(TransactionReceipt receipt) {
                                        receiptCompletableFuture.complete(receipt);
                                    }
                                });
                        return 0;
                    });

            /// 5. 收到交易回执
            TransactionReceipt transactionReceipt =
                    receiptCompletableFuture.get(10000, TimeUnit.MILLISECONDS);

            /// 6. 解析交易回执
            TransactionDecoderService transactionDecoderService =
                    new TransactionDecoderService(client.getCryptoSuite(), client.isWASM());

            if (transactionReceipt.isStatusOK()) {
                /// 7. 如果回执中存在事件
                /// Map<String, List<List<Object>>> stringListMap =
                // transactionDecoderService.decodeEvents(abi, transactionReceipt.getLogEntries());
                /// 8. 如果合约有返回值
                // TransactionResponse transactionResponse =
                //          transactionDecoderService.decodeReceiptWithValues(abi, "set",
                // transactionReceipt);
                /// 9. 如果合约无返回值
                TransactionResponse transactionResponse =
                        transactionDecoderService.decodeReceiptWithoutValues(
                                abi, transactionReceipt);
                return transactionResponse;
            } else {
                String revertMessage =
                        transactionDecoderService.decodeRevertMessage(
                                transactionReceipt.getOutput());
                System.out.println("交易回滚，回滚信息：" + revertMessage);
                return null;
            }

        } finally {
            TransactionBuilderJniObj.destroyTransactionData(transactionData);
        }
    }

    String callContract(String address) throws TransactionBaseException, ContractCodecException {
        // get 方法没有参数
        List<Object> params = new ArrayList<>();
        CallResponse callResponse =
                assembleTransactionWithRemoteSignProcessor.sendCall(
                        "", address, abi, "get", params);
        List<Object> callResponseReturnObject = callResponse.getReturnObject();
        return (String) callResponseReturnObject.get(0);
    }

    public static void main(String[] args)
            throws TransactionBaseException, IOException, JniException, ContractCodecException,
                    ExecutionException, InterruptedException, TimeoutException {

        AssembleTransactionWithRemoteSignProcessorSample
                assembleTransactionWithRemoteSignProcessorSample =
                        new AssembleTransactionWithRemoteSignProcessorSample();

        // 部署合约
        String address = assembleTransactionWithRemoteSignProcessorSample.deployContract();

        String param = "test";
        TransactionResponse transactionResponse =
                assembleTransactionWithRemoteSignProcessorSample.sendTransaction(
                        address, Collections.singletonList(param));

        if (transactionResponse.getTransactionReceipt().isStatusOK()) {

            String result = assembleTransactionWithRemoteSignProcessorSample.callContract(address);
            assert param.equals(result);
            System.out.println("调用合约结束。");
        }
    }
}
