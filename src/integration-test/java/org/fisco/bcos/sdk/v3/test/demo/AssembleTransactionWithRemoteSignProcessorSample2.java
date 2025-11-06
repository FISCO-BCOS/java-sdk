package org.fisco.bcos.sdk.v3.test.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecTools;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.test.transaction.mock.RemoteSignProviderMock;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionWithRemoteSignProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallRequest;
import org.fisco.bcos.sdk.v3.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.v3.utils.Hex;

public class AssembleTransactionWithRemoteSignProcessorSample2 {
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    // prepare sdk， read from the config file
    private final BcosSDK sdk;
    private final Client client;
    // mock remote sign service
    private final RemoteSignProviderInterface remoteSignProviderMock;
    private final AssembleTransactionWithRemoteSignProcessor
            assembleTransactionWithRemoteSignProcessor;

    private final FunctionEncoder functionEncoder;

    private final ABIDefinitionFactory abiDefinitionFactory;

    public AssembleTransactionWithRemoteSignProcessorSample2()
            throws IOException, NoSuchTransactionFileException {
        // create sdk by config file
        sdk = BcosSDK.build(configFile);
        client = this.sdk.getClient("group0");
        // RemoteSignProviderMock
        remoteSignProviderMock = new RemoteSignProviderMock(this.client.getCryptoSuite());
        // build processor
        assembleTransactionWithRemoteSignProcessor =
                TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
                        this.client,
                        this.client.getCryptoSuite().getCryptoKeyPair(),
                        "",
                        this.remoteSignProviderMock);

        functionEncoder = new FunctionEncoder(client.getCryptoSuite());
        abiDefinitionFactory = new ABIDefinitionFactory(client.getCryptoSuite());
    }

    public Tuple2<String, String> encodeData(String to, Function function) {
        byte[] encoded = functionEncoder.encode(function);
        return new Tuple2<>(to, Hex.toHexString(encoded));
    }

    private String encodeDeployData(String bin, String abi, List<Object> params)
            throws IOException {
        String encodeConstructor = bin;
        if (params != null && !params.isEmpty()) {
            ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(abi);
            ABIDefinition abiDefinition = contractABIDefinition.getConstructor();
            ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
            byte[] encodeParams =
                    ContractCodecTools.encode(
                            ContractCodecTools.decodeABIObjectValue(inputABIObject, params), false);
            encodeConstructor = encodeConstructor + Hex.toHexString(encodeParams);
        }
        return encodeConstructor;
    }

    public String mockWallet(String address, String data)
            throws JniException, ExecutionException, InterruptedException {
        long transactionData =
                TransactionBuilderJniObj.createTransactionData(
                        client.getGroup(),
                        client.getChainId(),
                        address,
                        data,
                        "",
                        client.getBlockLimit().longValue());
        try {

            String rawTxHash =
                    TransactionBuilderJniObj.calcTransactionDataHash(
                            client.getCryptoSuite().cryptoTypeConfig, transactionData);

            CompletableFuture<String> future = new CompletableFuture<>();
            remoteSignProviderMock.requestForSignAsync(
                    Hex.decode(rawTxHash),
                    client.getCryptoType(),
                    signature -> {
                        future.complete(Hex.toHexString(signature.encode()));
                        return 0;
                    });
            String sign = future.get();
            return TransactionBuilderJniObj.createSignedTransaction(
                    transactionData, sign, rawTxHash, 0);
        } finally {
            TransactionBuilderJniObj.destroyTransactionData(transactionData);
        }
    }

    public TransactionResponse sendRawTransaction(String signedTransaction, String abi)
            throws ExecutionException, InterruptedException, ClassNotFoundException {
        CompletableFuture<TransactionReceipt> receiptCompletableFuture = new CompletableFuture<>();

        assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                signedTransaction,
                new TransactionCallback() {
                    @Override
                    public void onResponse(TransactionReceipt receipt) {
                        receiptCompletableFuture.complete(receipt);
                    }
                });

        TransactionReceipt transactionReceipt = receiptCompletableFuture.get();

        /// decode receipt
        TransactionDecoderService transactionDecoderService =
                new TransactionDecoderService(client.getCryptoSuite(), client.isWASM());

        TransactionResponse transactionResponse =
                transactionDecoderService.decodeReceiptWithoutValues(abi, transactionReceipt);

        // if receipt status ok and not deploy contract tx
        if (transactionReceipt.isStatusOK() && !transactionReceipt.getTo().isEmpty()) {
            String methodId = Hex.trimPrefix(transactionReceipt.getInput()).substring(0, 8);
            ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(abi);
            ABIDefinition abiDefinition =
                    contractABIDefinition
                            .getMethodIDToFunctions()
                            .get(ByteBuffer.wrap(Hex.decode(methodId)));
            ABIObject outputABIObject = ABIObjectFactory.createOutputObject(abiDefinition);
            Pair<List<Object>, List<ABIObject>> outputAndABIObjects =
                    ContractCodecTools.decodeJavaObjectAndOutputObject(
                            outputABIObject, transactionReceipt.getOutput(), false);
            // final output result
            transactionResponse.setReturnObject(outputAndABIObjects.getLeft());
            transactionResponse.setReturnABIObject(outputAndABIObjects.getRight());
        }
        return transactionResponse;
    }

    public String deployContract(String bin, String abi, List<Object> params)
            throws JniException, ExecutionException, InterruptedException, IOException,
                    ClassNotFoundException {

        // 1. prepare tx params
        /// deploy contract should use bin, abi, constructor params

        // 2. construct tx struct
        String encodeConstructor = encodeDeployData(bin, abi, params);

        String rawTransaction = mockWallet("", encodeConstructor);
        // 5. construct sign tx and send to blockchain
        TransactionResponse transactionResponse = sendRawTransaction(rawTransaction, abi);

        String contractAddress = transactionResponse.getContractAddress();
        if (transactionResponse.getTransactionReceipt().isStatusOK()) {
            System.out.println("Deploy success, contract address: " + contractAddress);
        }
        return contractAddress;
    }

    public TransactionResponse sendTransaction(String address, String abi, Function function)
            throws JniException, ExecutionException, InterruptedException, ClassNotFoundException {

        // 1. prepare tx params
        // 2. construct tx
        Tuple2<String, String> encodeData = encodeData(address, function);

        String rawTransaction = mockWallet(encodeData.getValue1(), encodeData.getValue2());

        return sendRawTransaction(rawTransaction, abi);
    }

    public CallResponse callContract(String address, String abi, Function function)
            throws TransactionBaseException, ContractCodecException {
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(abi);
        List<ABIDefinition> abiDefinitions =
                contractABIDefinition.getFunctions().get(function.getName());
        CallRequest callRequest =
                new CallRequest(
                        "", address, functionEncoder.encode(function), abiDefinitions.get(0));
        callRequest.setSign("");
        return assembleTransactionWithRemoteSignProcessor.sendCall(callRequest);
    }

    public static void main(String[] args)
            throws TransactionBaseException, IOException, JniException, ContractCodecException,
                    ExecutionException, InterruptedException, ClassNotFoundException,
                    ContractException {

        AssembleTransactionWithRemoteSignProcessorSample2
                assembleTransactionWithRemoteSignProcessorSample =
                        new AssembleTransactionWithRemoteSignProcessorSample2();

        {
            String address =
                    assembleTransactionWithRemoteSignProcessorSample.deployContract(
                            HelloWorld.BINARY, HelloWorld.getABI(), null);
            String params = "test";
            /**
             * Note: Modifications to the Java file generated by Solidity may be required. For
             * details, refer to HelloWorld.java.
             */
            Function function = HelloWorld.set(params);

            TransactionResponse transactionResponse =
                    assembleTransactionWithRemoteSignProcessorSample.sendTransaction(
                            address, HelloWorld.getABI(), function);
            if (transactionResponse.getTransactionReceipt().isStatusOK()) {

                CallResponse callResponse =
                        assembleTransactionWithRemoteSignProcessorSample.callContract(
                                address, HelloWorld.getABI(), HelloWorld.get());
                System.out.println(callResponse);
                assert (params.equals(callResponse.getResults().get(0).getValue()));
            } else {
                System.out.println(transactionResponse);
            }
        }
    }
}
