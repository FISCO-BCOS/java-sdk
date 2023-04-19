package org.fisco.bcos.sdk.v3.transaction.manager;

import static org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute.LIQUID_CREATE;
import static org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute.LIQUID_SCALE_CODEC;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute;
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
import org.fisco.bcos.sdk.v3.utils.Hex;

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
        try {
            byte[] input = this.contractCodec.encodeConstructor(abi, bin, params);
            long transactionData =
                    TransactionBuilderJniObj.createTransactionData(
                            this.groupId,
                            this.chainId,
                            Objects.nonNull(path) ? path : "",
                            Hex.toHexString(input),
                            abi,
                            client.getBlockLimit().longValue());
            byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(transactionData);
            SignatureResult signatureResult =
                    transactionSignProvider.requestForSign(
                            rawTxHash, this.cryptoSuite.getCryptoTypeConfig());
            byte[] bytes =
                    transactionEncoder.encodeToTransactionBytes(
                            transactionData,
                            signatureResult,
                            client.isWASM()
                                    ? LIQUID_CREATE | LIQUID_SCALE_CODEC
                                    : TransactionAttribute.EVM_ABI_CODEC);
            TransactionResponse transactionResponse =
                    this.deployAndGetResponse(abi, Hex.toHexString(bytes));
            if (Objects.nonNull(transactionResponse.getTransactionReceipt())
                    && (Objects.isNull(transactionResponse.getTransactionReceipt().getInput())
                            || transactionResponse.getTransactionReceipt().getInput().isEmpty())) {
                transactionResponse
                        .getTransactionReceipt()
                        .setInput(Hex.toHexStringWithPrefix(input));
            }
            return transactionResponse;
        } catch (JniException e) {
            log.error("Jni build transaction error, e:", e);
            throw new ContractCodecException("Jni build transaction error, e:" + e.getMessage());
        } catch (Exception e) {
            log.error("deployAndGetResponse exception, e:", e);
            throw new ContractCodecException("deployAndGetResponse exception, e:" + e.getMessage());
        }
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
    public TransactionResponse deployByContractLoader(String contractName, List<Object> args)
            throws ContractCodecException, TransactionBaseException {
        return this.deployAndGetResponse(
                contractLoader.getABIByContractName(contractName),
                contractLoader.getBinaryByContractName(contractName),
                args,
                "");
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
    public TransactionResponse sendTransactionAndGetResponseByContractLoader(
            String contractName,
            String contractAddress,
            String functionName,
            List<Object> funcParams)
            throws ContractCodecException, TransactionBaseException {
        return this.sendTransactionAndGetResponse(
                contractAddress,
                contractLoader.getABIByContractName(contractName),
                functionName,
                funcParams);
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
    public TransactionResponse sendTransactionAndGetResponse(
            String to, String abi, String functionName, List<Object> params)
            throws ContractCodecException {
        try {

            long rawTransaction = this.getRawTransaction(to, abi, functionName, params);
            byte[] rawTxHash = this.transactionEncoder.encodeAndHashBytes(rawTransaction);
            int txAttribute = 0;
            if (client.isWASM()) {
                txAttribute = LIQUID_SCALE_CODEC;
            }
            SignatureResult signatureResult =
                    this.transactionSignProvider.requestForSign(
                            rawTxHash, cryptoSuite.getCryptoTypeConfig());
            byte[] transactionBytes =
                    this.transactionEncoder.encodeToTransactionBytes(
                            rawTransaction, signatureResult, txAttribute);
            TransactionReceipt transactionReceipt =
                    this.transactionPusher.push(Hex.toHexString(transactionBytes));
            if (Objects.nonNull(transactionReceipt)
                    && (Objects.isNull(transactionReceipt.getInput())
                            || transactionReceipt.getInput().isEmpty())) {
                transactionReceipt.setInput(
                        Hex.toHexStringWithPrefix(
                                this.contractCodec.encodeMethod(abi, functionName, params)));
            }
            return transactionDecoder.decodeReceiptWithValues(
                    abi, functionName, transactionReceipt);
        } catch (JniException e) {
            log.error("Jni build transaction error, e:", e);
            throw new ContractCodecException("Jni build transaction error, e:" + e.getMessage());
        } catch (Exception e) {
            log.error("deployAndGetResponse exception, e:", e);
            throw new ContractCodecException(
                    "sendTransactionAndGetResponse exception, e:" + e.getMessage());
        }
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
