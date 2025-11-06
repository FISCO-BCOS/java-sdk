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

public class AssembleTransactionWithRemoteSignProcessorSample {
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

    public AssembleTransactionWithRemoteSignProcessorSample()
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

    /**
     * This method is used to construct the transaction body and calculate its hash.
     *
     * @param to The contract address. Use an empty string when deploying a contract.
     * @param data The data after ABI encoding, in hexadecimal format.
     * @param abi The ABI (Application Binary Interface) of the contract.
     * @return A pair of strings where the first string is the transaction body in JSON format and
     *     the second string is the transaction hash.
     * @throws JniException If any JNI (Java Native Interface) error occurs.
     */
    public Tuple2<String, String> calculateTransactionDataWithHash(
            String to, String data, String abi) throws JniException {
        long transactionData =
                TransactionBuilderJniObj.createTransactionData(
                        client.getGroup(),
                        client.getChainId(),
                        to,
                        data,
                        abi,
                        client.getBlockLimit().longValue());
        try {
            String transactionDataJson =
                    TransactionBuilderJniObj.decodeTransactionDataToJsonObj(
                            TransactionBuilderJniObj.encodeTransactionData(transactionData));

            String rawTxHash =
                    TransactionBuilderJniObj.calcTransactionDataHash(
                            client.getCryptoSuite().cryptoTypeConfig, transactionData);
            return new Tuple2<>(transactionDataJson, rawTxHash);
        } finally {
            TransactionBuilderJniObj.destroyTransactionData(transactionData);
        }
    }

    /**
     * This method sends a transaction hash to a remote service for signing. It is used here only as
     * a demonstration of obtaining a signature. In actuality, the signing side would be a wallet or
     * something.
     *
     * @param txDataHash The transaction hash in hexadecimal format.
     * @return The signature result. If ECDSA, the signature format is r||s||v; If SM3, the
     *     signature format is r||s||pk.
     * @throws ExecutionException If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted while waiting.
     */
    public String signTxHash(String txDataHash) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        remoteSignProviderMock.requestForSignAsync(
                Hex.decode(txDataHash),
                client.getCryptoType(),
                signature -> {
                    future.complete(Hex.toHexString(signature.encode()));
                    return 0;
                });
        return future.get();
    }

    /**
     * Builds a transaction using the provided transaction data JSON string, transaction data hash,
     * and transaction data signature.
     *
     * @param txDataJsonString The transaction data in JSON format
     * @param txDataHash The transaction data hash in hex string format
     * @param txDataSignature The transaction data signature in hex string format
     * @return The transaction receipt
     * @throws JniException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public TransactionResponse buildTransactionAndSend(
            String txDataJsonString, String txDataHash, String txDataSignature, String abi)
            throws JniException, ExecutionException, InterruptedException, ClassNotFoundException {
        long transactionDataWithJson =
                TransactionBuilderJniObj.createTransactionDataWithJson(txDataJsonString);
        try {
            String signedTransaction =
                    TransactionBuilderJniObj.createSignedTransaction(
                            transactionDataWithJson, txDataSignature, txDataHash, 0);
            CompletableFuture<TransactionReceipt> receiptCompletableFuture =
                    new CompletableFuture<>();
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
        } finally {
            TransactionBuilderJniObj.destroyTransactionData(transactionDataWithJson);
        }
    }

    public String deployContract(String bin, String abi, List<Object> params)
            throws JniException, ExecutionException, InterruptedException, IOException,
                    ClassNotFoundException {

        // 1. prepare tx params
        /// deploy contract should use bin, abi, constructor params

        // 2. construct tx struct
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

        // 3. calculate tx data hash
        Tuple2<String, String> txDataAndTxHash =
                calculateTransactionDataWithHash("", encodeConstructor, abi);
        String txDataJsonString = txDataAndTxHash.getValue1();
        String txDataHash = txDataAndTxHash.getValue2();
        // 4. sign on tx data hash
        String signedTxHash = signTxHash(txDataHash);
        // 5. construct sign tx and send to blockchain
        TransactionResponse transactionResponse =
                buildTransactionAndSend(txDataJsonString, txDataHash, signedTxHash, abi);

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
        byte[] encoded = functionEncoder.encode(function);
        // 3. calculate tx data hash
        Tuple2<String, String> txDataAndTxHash =
                calculateTransactionDataWithHash(address, Hex.toHexString(encoded), abi);
        String txDataJsonString = txDataAndTxHash.getValue1();
        String txDataHash = txDataAndTxHash.getValue2();
        // 4. sign on tx data hash
        String signedTxHash = signTxHash(txDataHash);
        // 5. construct sign tx and send to blockchain
        return buildTransactionAndSend(txDataJsonString, txDataHash, signedTxHash, abi);
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

        AssembleTransactionWithRemoteSignProcessorSample
                assembleTransactionWithRemoteSignProcessorSample =
                        new AssembleTransactionWithRemoteSignProcessorSample();

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
