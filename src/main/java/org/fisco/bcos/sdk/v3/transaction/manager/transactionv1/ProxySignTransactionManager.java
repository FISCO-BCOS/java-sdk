package org.fisco.bcos.sdk.v3.transaction.manager.transactionv1;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderV1JniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionData;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionDataV1;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionDataV2;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionStructBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute;
import org.fisco.bcos.sdk.v3.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.v3.client.protocol.response.Call;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.v3.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.Response;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.RespCallback;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.ContractGasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.DefaultGasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.manager.transactionv1.dto.AbiEncodedRequest;
import org.fisco.bcos.sdk.v3.transaction.nonce.DefaultNonceAndBlockLimitProvider;
import org.fisco.bcos.sdk.v3.transaction.nonce.NonceAndBlockLimitProvider;
import org.fisco.bcos.sdk.v3.transaction.signer.AsyncTransactionSignercInterface;
import org.fisco.bcos.sdk.v3.transaction.signer.TransactionJniSignerService;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProxySignTransactionManager: customizable signer method, default use JNI signer. customizable key
 * pair to sign, default use client key pair.
 */
public class ProxySignTransactionManager extends TransactionManager {

    private ContractGasProvider contractGasProvider = new DefaultGasProvider();
    private NonceAndBlockLimitProvider nonceProvider = new DefaultNonceAndBlockLimitProvider();
    private AsyncTransactionSignercInterface asyncTxSigner = null;

    private static Logger logger = LoggerFactory.getLogger(ProxySignTransactionManager.class);

    private int cryptoType = CryptoType.ECDSA_TYPE;

    private Hash hashImpl;

    public ProxySignTransactionManager(Client client) {
        super(client);
        cryptoType = client.getCryptoSuite().cryptoTypeConfig;
        hashImpl = (cryptoType == CryptoType.ECDSA_TYPE) ? new Keccak256() : new SM3Hash();
        asyncTxSigner = new TransactionJniSignerService(client.getCryptoSuite().getCryptoKeyPair());
    }

    public ProxySignTransactionManager(
            Client client, AsyncTransactionSignercInterface asyncTxSigner) {
        this(client);
        this.asyncTxSigner = asyncTxSigner;
    }

    @Override
    public ContractGasProvider getGasProvider() {
        return contractGasProvider;
    }

    @Override
    public void setGasProvider(ContractGasProvider gasProvider) {
        contractGasProvider = gasProvider;
    }

    @Override
    public NonceAndBlockLimitProvider getNonceProvider() {
        return nonceProvider;
    }

    @Override
    public void setNonceProvider(NonceAndBlockLimitProvider nonceProvider) {
        this.nonceProvider = nonceProvider;
    }

    public void setAsyncTransactionSigner(AsyncTransactionSignercInterface asyncTxSigner) {
        this.asyncTxSigner = asyncTxSigner;
    }

    /**
     * This method is used to send transaction.
     *
     * @param request An instance of AbiEncodedRequest which contains the necessary information to
     *     create a transaction: if it is a contract creation, request should setCreate(true), and
     *     the abi field should be set; if it is EIP1559 transaction, request should set
     *     EIP1559Struct.
     * @return An instance of TxPair which contains the signed transaction and the transaction hash.
     * @throws JniException If there is an error during the JNI operation.
     */
    @Override
    public TransactionReceipt sendTransaction(AbiEncodedRequest request) throws JniException {
        TxPair txPair = createSignedTransaction(request);
        return client.sendTransaction(txPair.getSignedTx(), false).getTransactionReceipt();
    }

    /**
     * This method is used to send transaction asynchronously.
     *
     * @param request An instance of AbiEncodedRequest which contains the necessary information to
     *     create a transaction: if it is a contract creation, request should setCreate(true), and
     *     the abi field should be set; if it is EIP1559 transaction, request should set
     *     EIP1559Struct.
     * @param callback callback when transaction receipt is returned
     * @return transaction data hash
     * @throws JniException If there is an error during the JNI operation.
     */
    @Override
    public String asyncSendTransaction(AbiEncodedRequest request, TransactionCallback callback)
            throws JniException {
        TxPair txPair = createSignedTransaction(request);
        client.sendTransactionAsync(txPair.getSignedTx(), false, callback);
        return txPair.getTxHash();
    }

    /**
     * This method is used to create a signed transaction.
     *
     * @param request An instance of AbiEncodedRequest which contains the necessary information to
     *     create a transaction: if it is a contract creation, request should setCreate(true), and
     *     the abi field should be set; if it is EIP1559 transaction, request should set
     *     EIP1559Struct.
     * @return An instance of TxPair which contains the signed transaction and the transaction hash.
     * @throws JniException If there is an error during the JNI operation.
     */
    @Override
    public TxPair createSignedTransaction(AbiEncodedRequest request) throws JniException {
        if (!request.isTransactionEssentialSatisfy()) {
            throw new JniException("Transaction essential fields are not satisfied");
        }
        int transactionAttribute;
        if (client.isWASM()) {
            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
            if (request.isCreate()) {
                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
            }
        } else {
            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
        }
        byte[] methodId = new byte[4];
        if (!request.isCreate() && (request.getEncodedData().length >= 4)) {
            System.arraycopy(request.getEncodedData(), 0, methodId, 0, 4);
        }
        String nonce =
                request.getNonce() == null ? getNonceProvider().getNonce() : request.getNonce();
        BigInteger blockLimit =
                request.getBlockLimit() == null
                        ? getNonceProvider().getBlockLimit(client)
                        : request.getBlockLimit();
        EIP1559Struct eip1559Struct = null;
        if (getGasProvider().isEIP1559Enabled() || request.isEIP1559Enabled()) {
            eip1559Struct =
                    request.getEip1559Struct() == null
                            ? getGasProvider().getEIP1559Struct(methodId)
                            : request.getEip1559Struct();
        }
        BigInteger gasPrice =
                request.getGasPrice() == null
                        ? getGasProvider().getGasPrice(methodId)
                        : request.getGasPrice();
        BigInteger gasLimit =
                request.getGasLimit() == null
                        ? getGasProvider().getGasLimit(methodId)
                        : request.getGasLimit();

        TransactionData transactionData =
                new TransactionData()
                        .buildGroupId(client.getGroup())
                        .buildChainId(client.getChainId())
                        .buildTo(request.getTo())
                        .buildNonce(nonce)
                        .buildInput(request.getEncodedData())
                        .buildAbi(request.isCreate() ? request.getAbi() : "")
                        .buildBlockLimit(blockLimit.longValue());

        if (request.getVersion().getValue() >= TransactionVersion.V1.getValue()) {
            transactionData =
                    new TransactionDataV1(transactionData)
                            .buildGasLimit(gasLimit.longValue())
                            .buildGasPrice(
                                    eip1559Struct == null ? Numeric.toHexString(gasPrice) : "")
                            .buildValue(Numeric.toHexString(request.getValue()))
                            .buildMaxFeePerGas(
                                    eip1559Struct == null
                                            ? ""
                                            : Numeric.toHexString(eip1559Struct.getMaxFeePerGas()))
                            .buildMaxPriorityFeePerGas(
                                    eip1559Struct == null
                                            ? ""
                                            : Numeric.toHexString(
                                                    eip1559Struct.getMaxPriorityFeePerGas()));
        }
        if (request.getVersion().getValue() >= TransactionVersion.V2.getValue()) {
            transactionData =
                    new TransactionDataV2((TransactionDataV1) transactionData)
                            .buildExtension(request.getExtension());
        }
        String dataHash =
                TransactionStructBuilderJniObj.calcTransactionDataStructHash(
                        cryptoType, transactionData);

        CompletableFuture<SignatureResult> signFuture = new CompletableFuture<>();
        SignatureResult signatureResult;
        try {
            asyncTxSigner.signAsync(
                    Hex.decode(dataHash),
                    signature -> {
                        signFuture.complete(signature);
                        return 0;
                    });
            signatureResult =
                    signFuture.get(
                            client.getConfigOption().getNetworkConfig().getTimeout(),
                            TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Sign transaction failed, error message: {}", e.getMessage(), e);
            throw new JniException("Sign transaction failed, error message: " + e.getMessage());
        }
        String signedTransactionWithSignature =
                TransactionStructBuilderJniObj.createEncodedTransaction(
                        transactionData,
                        Hex.toHexString(signatureResult.encode()),
                        dataHash,
                        transactionAttribute,
                        client.getExtraData());
        return new TxPair(dataHash, signedTransactionWithSignature);
    }

    /**
     * Send tx with abi field
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    public TransactionReceipt sendTransaction(
            String to, byte[] data, BigInteger value, String abi, boolean constructor)
            throws JniException {
        byte[] methodId = new byte[4];
        if (data.length >= 4) {
            System.arraycopy(data, 0, methodId, 0, 4);
        }
        return sendTransaction(
                to,
                data,
                value,
                getGasProvider().getGasPrice(methodId),
                getGasProvider().getGasLimit(methodId),
                client.getBlockLimit(),
                abi,
                constructor);
    }

    /**
     * Send tx with gasPrice and gasLimit fields
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    public TransactionReceipt sendTransaction(
            String to,
            byte[] data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String abi,
            boolean constructor)
            throws JniException {
        return sendTransaction(
                to, data, value, gasPrice, gasLimit, client.getBlockLimit(), abi, constructor);
    }

    /**
     * Send tx with gasPrice and gasLimit fields
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    public TransactionReceipt sendTransaction(
            String to,
            byte[] data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            String abi,
            boolean constructor)
            throws JniException {
        String signedTransaction =
                createSignedTransaction(
                        to, data, value, gasPrice, gasLimit, blockLimit, abi, constructor);
        return client.sendTransaction(signedTransaction, false).getTransactionReceipt();
    }

    /**
     * This method is used to create a signed transaction.
     *
     * @param to The destination address for the transaction.
     * @param data The data to be sent with the transaction.
     * @param value The value to be transferred with the transaction.
     * @param gasPrice The price of gas for the transaction.
     * @param gasLimit The maximum amount of gas that can be used for the transaction.
     * @param blockLimit The maximum block number that can be used for the transaction, if
     *     blockLimit is zero, then get client blockLimit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return A Hex string representation of the signed transaction.
     */
    @Override
    public String createSignedTransaction(
            String to,
            byte[] data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            String abi,
            boolean constructor)
            throws JniException {
        if (blockLimit.compareTo(BigInteger.ZERO) == 0) {
            blockLimit = client.getBlockLimit();
        }
        String nonce = nonceProvider.getNonce();
        String dataHash =
                TransactionBuilderV1JniObj.calcTransactionDataHashWithFullFields(
                        cryptoType,
                        TransactionVersion.V1,
                        client.getGroup(),
                        client.getChainId(),
                        to == null ? "" : to,
                        nonce,
                        data,
                        (abi == null || !constructor) ? "" : abi,
                        blockLimit.longValue(),
                        Numeric.toHexString(value),
                        Numeric.toHexString(gasPrice),
                        gasLimit == null ? 0 : gasLimit.longValue(),
                        "",
                        "");
        CompletableFuture<SignatureResult> signFuture = new CompletableFuture<>();
        SignatureResult signatureResult;
        try {
            asyncTxSigner.signAsync(
                    Hex.decode(dataHash),
                    signature -> {
                        signFuture.complete(signature);
                        return 0;
                    });
            signatureResult =
                    signFuture.get(
                            client.getConfigOption().getNetworkConfig().getTimeout(),
                            TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Sign transaction failed, error message: {}", e.getMessage(), e);
            throw new JniException("Sign transaction failed, error message: " + e.getMessage());
        }
        int transactionAttribute;
        if (client.isWASM()) {
            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
            if (constructor) {
                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
            }
        } else {
            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
        }
        return TransactionBuilderV1JniObj.createSignedTransactionWithSignature(
                signatureResult.encode(),
                dataHash,
                TransactionVersion.V1,
                client.getGroup(),
                client.getChainId(),
                to == null ? "" : to,
                nonce,
                data,
                (abi == null || !constructor) ? "" : abi,
                blockLimit.longValue(),
                Numeric.toHexString(value),
                Numeric.toHexString(gasPrice),
                gasLimit == null ? 0 : gasLimit.longValue(),
                "",
                "",
                transactionAttribute,
                client.getExtraData());
    }

    /**
     * Send tx with abi field asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param callback callback function
     * @return receipt
     */
    @Override
    public String asyncSendTransaction(
            String to, byte[] data, BigInteger value, TransactionCallback callback)
            throws JniException {
        byte[] methodId = new byte[4];
        if (data.length >= 4) {
            System.arraycopy(data, 0, methodId, 0, 4);
        }
        return asyncSendTransaction(
                to,
                data,
                value,
                getGasProvider().getGasPrice(methodId),
                getGasProvider().getGasLimit(methodId),
                client.getBlockLimit(),
                null,
                false,
                callback);
    }

    /**
     * Send tx with abi field asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    public String asyncSendTransaction(
            String to,
            byte[] data,
            BigInteger value,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        byte[] methodId = new byte[4];
        if (data.length >= 4) {
            System.arraycopy(data, 0, methodId, 0, 4);
        }
        return asyncSendTransaction(
                to,
                data,
                value,
                getGasProvider().getGasPrice(methodId),
                getGasProvider().getGasLimit(methodId),
                client.getBlockLimit(),
                abi,
                constructor,
                callback);
    }

    /**
     * Send tx with gasPrice and gasLimit fields asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    public String asyncSendTransaction(
            String to,
            byte[] data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        return asyncSendTransaction(
                to,
                data,
                value,
                gasPrice,
                gasLimit,
                client.getBlockLimit(),
                abi,
                constructor,
                callback);
    }

    /**
     * Send tx with gasPrice and gasLimit fields asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param gasPrice price of gas
     * @param gasLimit use limit of gas
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    public String asyncSendTransaction(
            String to,
            byte[] data,
            BigInteger value,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger blockLimit,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        String nonce = nonceProvider.getNonce();
        String dataHash =
                TransactionBuilderV1JniObj.calcTransactionDataHashWithFullFields(
                        cryptoType,
                        TransactionVersion.V1,
                        client.getGroup(),
                        client.getChainId(),
                        to == null ? "" : to,
                        nonce,
                        data,
                        (abi == null || !constructor) ? "" : abi,
                        blockLimit.longValue(),
                        Numeric.toHexString(value),
                        Numeric.toHexString(gasPrice),
                        gasLimit == null ? 0 : gasLimit.longValue(),
                        "",
                        "");

        asyncTxSigner.signAsync(
                Hex.decode(dataHash),
                signature -> {
                    try {
                        int transactionAttribute;
                        if (client.isWASM()) {
                            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
                            if (constructor) {
                                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
                            }
                        } else {
                            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
                        }
                        String signedTransaction =
                                TransactionBuilderV1JniObj.createSignedTransactionWithSignature(
                                        signature.encode(),
                                        dataHash,
                                        TransactionVersion.V1,
                                        client.getGroup(),
                                        client.getChainId(),
                                        to == null ? "" : to,
                                        nonce,
                                        data,
                                        (abi == null || !constructor) ? "" : abi,
                                        blockLimit.longValue(),
                                        Numeric.toHexString(value),
                                        Numeric.toHexString(gasPrice),
                                        gasLimit.longValue(),
                                        "",
                                        "",
                                        transactionAttribute,
                                        client.getExtraData());
                        client.sendTransactionAsync(
                                signedTransaction,
                                false,
                                new TransactionCallback() {
                                    @Override
                                    public void onResponse(TransactionReceipt receipt) {
                                        callback.onResponse(receipt);
                                    }
                                });
                        return 0;
                    } catch (JniException e) {
                        logger.error(
                                "Create sign transaction failed, error message: {}",
                                e.getMessage(),
                                e);
                        callback.onError(
                                -1,
                                "Create sign transaction failed, error message:" + e.getMessage());
                    } catch (Exception e) {
                        logger.error(
                                "Send transaction failed, error message: {}", e.getMessage(), e);
                        callback.onError(
                                -1, "Send transaction failed, error message:" + e.getMessage());
                    }
                    return -1;
                });
        return dataHash;
    }

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    public TransactionReceipt sendTransactionEIP1559(
            String to,
            byte[] data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            String abi,
            boolean constructor)
            throws JniException {
        return sendTransactionEIP1559(
                to, data, value, eip1559Struct, client.getBlockLimit(), abi, constructor);
    }

    /**
     * Send tx with EIP1559
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     * @param constructor if you deploy contract, should set to be true
     * @return receipt
     */
    @Override
    public TransactionReceipt sendTransactionEIP1559(
            String to,
            byte[] data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            String abi,
            boolean constructor)
            throws JniException {
        CompletableFuture<TransactionReceipt> future = new CompletableFuture<>();
        asyncSendTransactionEIP1559(
                to,
                data,
                value,
                eip1559Struct,
                blockLimit,
                abi,
                constructor,
                new TransactionCallback() {
                    @Override
                    public void onResponse(TransactionReceipt receipt) {
                        future.complete(receipt);
                    }
                });
        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt =
                    future.get(
                            client.getConfigOption().getNetworkConfig().getTimeout(),
                            TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Get transaction receipt failed, error message: {}", e.getMessage(), e);
        }
        return transactionReceipt;
    }

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     *     contract
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    public String asyncSendTransactionEIP1559(
            String to,
            byte[] data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        return asyncSendTransactionEIP1559(
                to, data, value, eip1559Struct, client.getBlockLimit(), abi, constructor, callback);
    }

    /**
     * Send tx with EIP1559 asynchronously
     *
     * @param to to address
     * @param data input data
     * @param value transfer value
     * @param eip1559Struct EIP1559 transaction payload
     * @param blockLimit block limit
     * @param abi ABI JSON string, generated by compile contract, should fill in when you deploy
     * @param constructor if you deploy contract, should set to be true
     * @param callback callback function
     * @return receipt
     */
    @Override
    public String asyncSendTransactionEIP1559(
            String to,
            byte[] data,
            BigInteger value,
            EIP1559Struct eip1559Struct,
            BigInteger blockLimit,
            String abi,
            boolean constructor,
            TransactionCallback callback)
            throws JniException {
        String nonce = nonceProvider.getNonce();
        String dataHash =
                TransactionBuilderV1JniObj.calcTransactionDataHashWithFullFields(
                        cryptoType,
                        TransactionVersion.V1,
                        client.getGroup(),
                        client.getChainId(),
                        to == null ? "" : to,
                        nonce,
                        data,
                        (abi == null || !constructor) ? "" : abi,
                        blockLimit.longValue(),
                        Numeric.toHexString(value),
                        "",
                        eip1559Struct.getGasLimit().longValue(),
                        Numeric.toHexString(eip1559Struct.getMaxFeePerGas()),
                        Numeric.toHexString(eip1559Struct.getMaxPriorityFeePerGas()));
        asyncTxSigner.signAsync(
                Hex.decode(dataHash),
                signature -> {
                    try {
                        int transactionAttribute;
                        if (client.isWASM()) {
                            transactionAttribute = TransactionAttribute.LIQUID_SCALE_CODEC;
                            if (constructor) {
                                transactionAttribute |= TransactionAttribute.LIQUID_CREATE;
                            }
                        } else {
                            transactionAttribute = TransactionAttribute.EVM_ABI_CODEC;
                        }
                        String signedTransaction =
                                TransactionBuilderV1JniObj.createSignedTransactionWithSignature(
                                        signature.encode(),
                                        dataHash,
                                        TransactionVersion.V1,
                                        client.getGroup(),
                                        client.getChainId(),
                                        to == null ? "" : to,
                                        nonce,
                                        data,
                                        (abi == null || !constructor) ? "" : abi,
                                        blockLimit.longValue(),
                                        Numeric.toHexString(value),
                                        "",
                                        eip1559Struct.getGasLimit().longValue(),
                                        Numeric.toHexString(eip1559Struct.getMaxFeePerGas()),
                                        Numeric.toHexString(
                                                eip1559Struct.getMaxPriorityFeePerGas()),
                                        transactionAttribute,
                                        client.getExtraData());
                        client.sendTransactionAsync(
                                signedTransaction,
                                false,
                                new TransactionCallback() {
                                    @Override
                                    public void onResponse(TransactionReceipt receipt) {
                                        callback.onResponse(receipt);
                                    }
                                });
                        return 0;
                    } catch (JniException e) {
                        logger.error(
                                "Create sign transaction failed, error message: {}",
                                e.getMessage(),
                                e);
                        callback.onError(
                                -1,
                                "Create sign transaction failed, error message:" + e.getMessage());
                    } catch (Exception e) {
                        logger.error(
                                "Send transaction failed, error message: {}", e.getMessage(), e);
                        callback.onError(
                                -1, "Send transaction failed, error message:" + e.getMessage());
                    }
                    return -1;
                });
        return dataHash;
    }

    /**
     * Send call
     *
     * @param to to address
     * @param data input data
     * @return call result
     */
    @Override
    public Call sendCall(String to, byte[] data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(Hex.trimPrefix(to).getBytes());
            outputStream.write(data);
            byte[] hash = hashImpl.hash(outputStream.toByteArray());
            CompletableFuture<Call> future = new CompletableFuture<>();
            asyncTxSigner.signAsync(
                    hash,
                    signature -> {
                        Call call =
                                client.call(
                                        new Transaction("", to, data),
                                        Hex.toHexString(signature.encode()));
                        future.complete(call);
                        return 0;
                    });
            return future.get();
        } catch (Exception e) {
            logger.error("Send call failed, error message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Send call with signature of call data
     *
     * @param to to address
     * @param data input data
     * @param signature signature of call data
     */
    @Override
    public Call sendCall(String to, byte[] data, String signature) {
        return client.call(new Transaction("", to, data), signature);
    }

    /**
     * Send call asynchronously
     *
     * @param to to address
     * @param data input data
     * @param callback callback function
     */
    @Override
    public void asyncSendCall(String to, byte[] data, RespCallback<Call> callback) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(Hex.trimPrefix(to).getBytes());
            outputStream.write(data);
            byte[] hash = hashImpl.hash(outputStream.toByteArray());
            asyncTxSigner.signAsync(
                    hash,
                    signature -> {
                        client.callAsync(
                                new Transaction("", to, data),
                                Hex.toHexString(signature.encode()),
                                callback);
                        return 0;
                    });
        } catch (Exception e) {
            logger.error("Send call failed, error message: {}", e.getMessage(), e);
            callback.onError(new Response(-1, e.getMessage()));
        }
    }

    /**
     * Send call asynchronously with signature of call data
     *
     * @param to to address
     * @param data input data
     * @param signature signature of call data
     * @param callback callback function
     */
    @Override
    public void asyncSendCall(
            String to, byte[] data, String signature, RespCallback<Call> callback) {
        client.callAsync(new Transaction("", to, data), signature, callback);
    }
}
