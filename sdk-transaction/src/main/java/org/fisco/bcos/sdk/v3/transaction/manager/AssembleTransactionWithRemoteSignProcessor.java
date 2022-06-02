package org.fisco.bcos.sdk.v3.transaction.manager;

import static org.fisco.bcos.sdk.v3.client.protocol.model.Transaction.LIQUID_CREATE;
import static org.fisco.bcos.sdk.v3.client.protocol.model.Transaction.LIQUID_SCALE_CODEC;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TxPair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.v3.transaction.signer.TransactionSignerService;
import org.fisco.bcos.sdk.v3.transaction.tools.ContractLoader;

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
    public TransactionResponse deployAndGetResponse(
            String abi, String bin, List<Object> params, String path)
            throws ContractCodecException {
        TxPair txPair = this.createSignedConstructor(abi, bin, params, path);
        return this.deployAndGetResponse(abi, txPair.getSignedTx());
    }

    @Override
    public void deployAsync(
            long transactionData, RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws JniException {
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(transactionData);
        this.transactionSignProvider.requestForSignAsync(
                rawTxHash, this.cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    }

    @Override
    public void deployAsync(
            String abi,
            String bin,
            List<Object> params,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ContractCodecException, JniException {
        long transactionData = this.getRawTransactionForConstructor(abi, bin, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(transactionData);
        this.transactionSignProvider.requestForSignAsync(
                rawTxHash, this.cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    }

    @Override
    public CompletableFuture<TransactionReceipt> deployAsync(
            String abi, String bin, List<Object> params)
            throws ContractCodecException, JniException {
        long transactionData = this.getRawTransactionForConstructor(abi, bin, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(transactionData);
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_CREATE | LIQUID_SCALE_CODEC;
        }
        return this.signAndPush(transactionData, rawTxHash, txAttribute);
    }

    @Override
    public void deployByContractLoaderAsync(
            String contractName,
            List<Object> args,
            RemoteSignCallbackInterface remoteSignCallbackInterface)
            throws ContractCodecException, NoSuchTransactionFileException, JniException {
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
            throws ContractCodecException, TransactionBaseException, JniException {
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
            throws ContractCodecException, JniException {
        long transactionData = this.getRawTransaction(to, abi, functionName, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(transactionData);
        this.transactionSignProvider.requestForSignAsync(
                rawTxHash, this.cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    }

    @Override
    public CompletableFuture<TransactionReceipt> sendTransactionAsync(
            String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException, JniException {
        long transactionData = this.getRawTransaction(to, abi, functionName, params);
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(transactionData);
        int txAttribute = 0;
        if (client.isWASM()) {
            txAttribute = LIQUID_SCALE_CODEC;
        }
        return this.signAndPush(transactionData, rawTxHash, txAttribute);
    }

    @Override
    public TransactionReceipt encodeAndPush(
            long transactionData, String signatureStr, int txAttribute) throws JniException {
        SignatureResult signatureResult =
                TransactionSignerService.decodeSignatureString(
                        signatureStr,
                        this.cryptoSuite.getCryptoTypeConfig(),
                        this.cryptoSuite.getCryptoKeyPair().getHexPublicKey());
        byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(transactionData);
        byte[] signedTransaction =
                this.transactionEncoder.encodeToTransactionBytes(
                        transactionData, rawTxHash, signatureResult, txAttribute);
        return this.transactionPusher.push(Hex.toHexString(signedTransaction));
    }

    @Override
    public CompletableFuture<TransactionReceipt> signAndPush(
            long transactionData, byte[] rawTxHash, int txAttribute) {
        CompletableFuture<SignatureResult> future =
                CompletableFuture.supplyAsync(
                        () ->
                                this.transactionSignProvider.requestForSign(
                                        rawTxHash, this.cryptoSuite.getCryptoTypeConfig()));
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
                            try {
                                return this.encodeAndPush(
                                        transactionData, s.convertToString(), txAttribute);
                            } catch (JniException e) {
                                log.error("jni e: ", e);
                            }
                            return null;
                        });
        log.info("Sign and push over, wait for callback...");
        return cr;
    }
}
