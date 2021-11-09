package org.fisco.bcos.sdk.transaction.manager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.model.tars.TransactionData;
import org.fisco.bcos.sdk.codec.ABICodecException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerServcie;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;

public class AssembleTransactionWithRemoteSignProcessor extends AssembleTransactionProcessor
        implements AssembleTransactionWithRemoteSignProviderInterface {
    private final RemoteSignProviderInterface transactionSignProvider;

    public AssembleTransactionWithRemoteSignProcessor(
            Client client,
            CryptoKeyPair cryptoKeyPair,
            String groupId,
            String chainId,
            String contractName,
            RemoteSignProviderInterface transactionSignProvider) {
        super(client, cryptoKeyPair, groupId, chainId, contractName, "", "");
        this.transactionSignProvider = transactionSignProvider;
        super.transactionEncoder =
                new TransactionEncoderService(this.cryptoSuite, transactionSignProvider);
    }

    public AssembleTransactionWithRemoteSignProcessor(
            Client client,
            CryptoKeyPair cryptoKeyPair,
            String groupId,
            String chainId,
            ContractLoader contractLoader,
            RemoteSignProviderInterface transactionSignProvider) {
        super(client, cryptoKeyPair, groupId, chainId, contractLoader);
        this.transactionSignProvider = transactionSignProvider;
        super.transactionEncoder =
                new TransactionEncoderService(this.cryptoSuite, transactionSignProvider);
    }

    @Override
    public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
            throws ABICodecException {
        return this.deployAndGetResponse(abi, this.createSignedConstructor(abi, bin, params));
    }

    @Override
    public void deployAsync(
            TransactionData rawTransaction, RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException {
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(rawTransaction);
        this.transactionSignProvider.requestForSignAsync(
                rawTxHash, this.cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    }

    @Override
    public void deployAsync(
            String abi,
            String bin,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException {
        TransactionData rawTransaction = this.getRawTransactionForConstructor(abi, bin, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(rawTransaction);
        this.transactionSignProvider.requestForSignAsync(
                rawTxHash, this.cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params) throws ABICodecException {
        TransactionData rawTransaction = this.getRawTransactionForConstructor(abi, bin, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(rawTransaction);
        return this.signAndPush(rawTransaction, rawTxHash);
    }

    @Override
    public void deployByContractLoaderAsync(
            String contractName,
            List<Object> args,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException, NoSuchTransactionFileException {
        this.deployAsync(
                super.contractLoader.getABIByContractName(contractName),
                super.contractLoader.getBinaryByContractName(contractName),
                args,
                remoteSignCallbackInterface);
    }

    @Override
    public void sendTransactionAndGetReceiptByContractLoaderAsync(
            String contractName,
            String to,
            String functionName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException, TransactionBaseException {
        this.sendTransactionAsync(
                to,
                super.contractLoader.getABIByContractName(contractName),
                functionName,
                params,
                remoteSignCallbackInterface);
    }

    @Override
    public void sendTransactionAsync(
            String to,
            String abi,
            String functionName,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ABICodecException {
        TransactionData rawTransaction = this.getRawTransaction(to, abi, functionName, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(rawTransaction);
        this.transactionSignProvider.requestForSignAsync(
                rawTxHash, this.cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            String to, String abi, String functionName, List<Object> params)
            throws ABICodecException {
        TransactionData rawTransaction = this.getRawTransaction(to, abi, functionName, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(rawTransaction);
        return this.signAndPush(rawTransaction, rawTxHash);
    }

    @Override
    public TransactionReceipt encodeAndPush(TransactionData rawTransaction, String signatureStr) {
        SignatureResult signatureResult =
                TransactionSignerServcie.decodeSignatureString(
                        signatureStr,
                        this.cryptoSuite.getCryptoTypeConfig(),
                        this.cryptoSuite.getCryptoKeyPair().getHexPublicKey());
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(rawTransaction);
        byte[] signedTransaction =
                this.transactionEncoder.encodeToTransactionBytes(
                        rawTransaction, rawTxHash, signatureResult);
        return this.transactionPusher.push(Hex.toHexString(signedTransaction));
    }

    @Override
    public CompletableFuture<TransactionReceipt> signAndPush(
            TransactionData rawTransaction, byte[] rawTxHash) {
        CompletableFuture<SignatureResult> future =
                CompletableFuture.supplyAsync(
                        () ->
                                this.transactionSignProvider.requestForSign(
                                        rawTxHash, this.cryptoSuite.getCryptoTypeConfig()));
        future.exceptionally(
                e -> {
                    AssembleTransactionProcessor.log.error(
                            "Request remote sign Error: {}", e.getMessage());
                    return null;
                });
        CompletableFuture<TransactionReceipt> cr =
                future.thenApplyAsync(
                        s -> {
                            if (s == null) {
                                AssembleTransactionProcessor.log.error(
                                        "Request remote signature is null");
                                return null;
                            }
                            return this.encodeAndPush(rawTransaction, s.convertToString());
                        });
        AssembleTransactionProcessor.log.info("Sign and push over, wait for callback...");
        return cr;
    }
}
