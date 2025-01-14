package org.fisco.bcos.sdk.v3.test.demo;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionData;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionDataV1;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionDataV2;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionStructBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodec;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecJsonWrapper;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractCodecTools;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.test.transaction.mock.RemoteSignProviderMock;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.TransactionRequestWithStringParams;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.utils.TransactionRequestBuilder;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;

class AssembleTransaction {
    private String abi;
    private TransactionRequestWithStringParams transactionRequest;
    private ABIDefinitionFactory abiDefinitionFactory;
    private ContractCodec contractCodec;

    public AssembleTransaction(TransactionRequestWithStringParams transactionRequest) {
        this.transactionRequest = transactionRequest;
        this.abi = transactionRequest.getAbi();
        this.abiDefinitionFactory = new ABIDefinitionFactory(new Keccak256());
        contractCodec = new ContractCodec(new Keccak256(), false);
    }

    public AssembleTransaction(String abi, TransactionRequestWithStringParams transactionRequest) {
        this(transactionRequest);
        this.abi = abi;
    }

    public Pair<String, TransactionData> assembleTransaction()
            throws JniException, ContractCodecException {

        /// 如果abi固定，可以在初始化时就加载好
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(abi);
        ContractCodecJsonWrapper contractCodecJsonWrapper = new ContractCodecJsonWrapper();
        List<ABIDefinition> abiDefinitions =
                contractABIDefinition.getFunctions().get(transactionRequest.getMethod());
        if (abiDefinitions == null) {
            throw new RuntimeException("Method not found in ABI");
        }
        ABIDefinition abiDefinition = abiDefinitions.get(0);

        /// 下面的代码是替换掉了原来的encodeMethodFromString
        // byte[] encode = contractCodec.encodeMethodFromString(abi, transactionRequest.getMethod(),
        // transactionRequest.getStringParams());
        ABIObject inputABIObject = ABIObjectFactory.createInputObject(abiDefinition);
        byte[] encode = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(abiDefinition.getMethodId(new Keccak256()));
            outputStream.write(
                    ContractCodecTools.encode(
                            contractCodecJsonWrapper.encode(
                                    inputABIObject, transactionRequest.getStringParams()),
                            false));
            encode = outputStream.toByteArray();
        } catch (Exception e) {
            throw new ContractCodecException("encode input error");
        }

        TransactionData transactionData =
                new TransactionData()
                        .buildVersion(transactionRequest.getVersion().getValue())
                        .buildGroupId("group0")
                        .buildChainId("chain0")
                        .buildTo(transactionRequest.getTo())
                        .buildNonce(transactionRequest.getNonce())
                        .buildInput(encode)
                        .buildAbi(transactionRequest.getAbi())
                        .buildBlockLimit(transactionRequest.getBlockLimit().longValue());
        if (transactionRequest.getVersion().getValue() >= TransactionVersion.V1.getValue()) {
            transactionData =
                    new TransactionDataV1(transactionData)
                            .buildGasLimit(0)
                            .buildGasPrice("")
                            .buildValue(Numeric.toHexString(transactionRequest.getValue()))
                            .buildMaxFeePerGas("")
                            .buildMaxPriorityFeePerGas("");
        }
        if (transactionRequest.getVersion().getValue() >= TransactionVersion.V2.getValue()) {
            transactionData =
                    new TransactionDataV2((TransactionDataV1) transactionData)
                            .buildExtension(transactionRequest.getExtension());
        }

        String transactionDataHash =
                TransactionStructBuilderJniObj.calcTransactionDataStructHash(
                        CryptoType.ECDSA_TYPE, transactionData);

        return new MutablePair<>(transactionDataHash, transactionData);
    }

    public String assembleSignedTx(String signature, String txHash, TransactionData txData)
            throws JniException {
        return TransactionStructBuilderJniObj.createEncodedTransaction(
                txData, signature, txHash, 0, "extraData");
    }
}

class SignTransaction {
    private final RemoteSignProviderInterface remoteSignProviderMock;

    public SignTransaction() {
        remoteSignProviderMock = new RemoteSignProviderMock(new CryptoSuite(CryptoType.ECDSA_TYPE));
    }

    public String sign(String hash) {
        SignatureResult signatureResult =
                remoteSignProviderMock.requestForSign(Hex.decode(hash), CryptoType.ECDSA_TYPE);
        return Hex.toHexString(signatureResult.encode());
    }
}

class SendTransaction {
    private Client client;

    public SendTransaction(Client client) {
        this.client = client;
    }

    public TransactionReceipt sendTx(String signedTx) {
        return client.sendTransaction(signedTx, false).getTransactionReceipt();
    }
}

public class AssembleTransactionDemo {
    public static BcosSDK sdk;
    public static Client client;
    private static final String configFile =
            "src/integration-test/resources/" + ConstantConfig.CONFIG_FILE_NAME;

    public static void main(String[] args)
            throws ContractException, JniException, ContractCodecException {
        sdk = BcosSDK.build(configFile);
        client = sdk.getClient("group0");

        HelloWorld deployed = HelloWorld.deploy(client, client.getCryptoSuite().getCryptoKeyPair());

        TransactionRequestBuilder requestBuilder = new TransactionRequestBuilder();
        TransactionRequestWithStringParams set =
                requestBuilder
                        .setAbi(HelloWorld.getABI())
                        .setBlockLimit(client.getBlockLimit())
                        .setNonce(UUID.randomUUID().toString().replace("-", ""))
                        .setTo(deployed.getContractAddress())
                        .setMethod("set")
                        .buildStringParamsRequest(Collections.singletonList("Hello, World!"));
        AssembleTransaction assembleTransaction = new AssembleTransaction(HelloWorld.getABI(), set);
        Pair<String, TransactionData> hashTransactionDataPair =
                assembleTransaction.assembleTransaction();

        SignTransaction signTransaction = new SignTransaction();
        String sign = signTransaction.sign(hashTransactionDataPair.getLeft());

        String signedTx =
                assembleTransaction.assembleSignedTx(
                        sign,
                        hashTransactionDataPair.getLeft(),
                        hashTransactionDataPair.getRight());

        SendTransaction sendTransaction = new SendTransaction(client);
        TransactionReceipt transactionReceipt = sendTransaction.sendTx(signedTx);
        System.out.println(transactionReceipt);
    }
}
