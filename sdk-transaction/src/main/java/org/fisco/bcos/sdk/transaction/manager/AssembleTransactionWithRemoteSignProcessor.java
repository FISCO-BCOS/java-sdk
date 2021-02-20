package org.fisco.bcos.sdk.transaction.manager;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.gas.DefaultGasProvider;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerServcie;

public class AssembleTransactionWithRemoteSignProcessor extends AssembleTransactionProcessor
        implements AssembleTransactionWithRemoteSignProviderInterface {
    private final RemoteSignProviderInterface transactionSignProvider;

    public AssembleTransactionWithRemoteSignProcessor(
            Client client,
            CryptoKeyPair cryptoKeyPair,
            Integer groupId,
            String chainId,
            String contractName,
            String abi,
            String bin,
            RemoteSignProviderInterface transactionSignProvider) {
        super(client, cryptoKeyPair, groupId, chainId, contractName, abi, bin);
        this.transactionSignProvider = transactionSignProvider;
        super.transactionEncoder =
                new TransactionEncoderService(cryptoSuite, transactionSignProvider);
    }

    @Override
    public TransactionReceipt deployAndGetReceipt(String data) {
        String signedData = createSignedTransaction(null, data, this.cryptoKeyPair);
        return transactionPusher.push(signedData);
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ABICodecException {
        return deployAndGetResponse(createSignedConstructor(abi, bin, params), abi);
    }

    @Override
    public void deployAsync(
            String abi,
            String bin,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException {
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        DefaultGasProvider.GAS_PRICE,
                        DefaultGasProvider.GAS_LIMIT,
                        null,
                        abiCodec.encodeConstructor(abi, bin, params),
                        BigInteger.ZERO,
                        new BigInteger(this.chainId),
                        BigInteger.valueOf(this.groupId),
                        "");
        byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
        transactionSignProvider.requestForSignAsync(
                rawTxHash, cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params) throws ABICodecException {
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        DefaultGasProvider.GAS_PRICE,
                        DefaultGasProvider.GAS_LIMIT,
                        null,
                        abiCodec.encodeConstructor(abi, bin, params),
                        BigInteger.ZERO,
                        new BigInteger(this.chainId),
                        BigInteger.valueOf(this.groupId),
                        "");
        byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
        return signAndPush(rawTransaction, rawTxHash);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException {
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        DefaultGasProvider.GAS_PRICE,
                        DefaultGasProvider.GAS_LIMIT,
                        to,
                        abiCodec.encodeMethod(abi, functionName, params),
                        BigInteger.ZERO,
                        new BigInteger(this.chainId),
                        BigInteger.valueOf(this.groupId),
                        "");
        byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
        transactionSignProvider.requestForSignAsync(
                rawTxHash, cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
        return signAndPush(rawTransaction, rawTxHash);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            String to, String abi, String functionName, List<Object> params)
            throws ABICodecException {
        RawTransaction rawTransaction =
                transactionBuilder.createTransaction(
                        DefaultGasProvider.GAS_PRICE,
                        DefaultGasProvider.GAS_LIMIT,
                        to,
                        abiCodec.encodeMethod(abi, functionName, params),
                        BigInteger.ZERO,
                        new BigInteger(this.chainId),
                        BigInteger.valueOf(this.groupId),
                        "");
        byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
        return null;
    }

    public TransactionReceipt signAndPush(RawTransaction rawTransaction, String signatureStr) {
        SignatureResult signatureResult =
                TransactionSignerServcie.decodeSignatureString(
                        signatureStr,
                        cryptoSuite.getCryptoTypeConfig(),
                        cryptoSuite.createKeyPair().getHexPublicKey());
        byte[] signedTransaction = transactionEncoder.encode(rawTransaction, signatureResult);
        return transactionPusher.push(Hex.toHexString(signedTransaction));
    }

    public CompletableFuture<TransactionReceipt> signAndPush(
            RawTransaction rawTransaction, byte[] rawTxHash) {
        CompletableFuture<String> future =
                CompletableFuture.supplyAsync(
                        () -> {
                            return transactionSignProvider.requestForSign(
                                    rawTxHash, cryptoSuite.getCryptoTypeConfig());
                        });
        future.exceptionally(
                e -> {
                    log.error("Request remote sign Error: {}", e.getMessage());
                    return null;
                });
        CompletableFuture<TransactionReceipt> cr =
                future.thenApplyAsync(
                        s -> {
                            if (s == null) {
                                log.error("Request remote signature is null");
                                return null;
                            }
                            return signAndPush(rawTransaction, s);
                        });
        log.info("Sign and push over, wait for callback...");
        return cr;
    }
}
