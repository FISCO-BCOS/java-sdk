package org.fisco.bcos.sdk.transaction.manager;

import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.encode.TransactionEncoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.po.TransactionData;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignCallbackInterface;
import org.fisco.bcos.sdk.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.transaction.signer.TransactionSignerServcie;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    super.transactionEncoder = new TransactionEncoderService(cryptoSuite, transactionSignProvider);
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
    super.transactionEncoder = new TransactionEncoderService(cryptoSuite, transactionSignProvider);
  }

  @Override
  public TransactionResponse deployAndGetResponse(String abi, String bin, List<Object> params)
      throws ABICodecException {
    return deployAndGetResponse(abi, createSignedConstructor(abi, bin, params));
  }

  @Override
  public void deployAsync(
      TransactionData rawTransaction, RemoteSignCallbackInterface remoteSignCallbackInterface)
      throws ABICodecException {
    byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
    transactionSignProvider.requestForSignAsync(
        rawTxHash, cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
  }

  @Override
  public void deployAsync(
      String abi,
      String bin,
      List<Object> params,
      RemoteSignCallbackInterface remoteSignCallbackInterface)
      throws ABICodecException {
    TransactionData rawTransaction = getRawTransactionForConstructor(abi, bin, params);
    byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
    transactionSignProvider.requestForSignAsync(
        rawTxHash, cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
  }

  @Override
  public CompletableFuture<TransactionReceipt> deployAsync(
      String abi, String bin, List<Object> params) throws ABICodecException {
    TransactionData rawTransaction = getRawTransactionForConstructor(abi, bin, params);
    byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
    return signAndPush(rawTransaction, rawTxHash);
  }

  @Override
  public void deployByContractLoaderAsync(
      String contractName,
      List<Object> args,
      RemoteSignCallbackInterface remoteSignCallbackInterface)
      throws ABICodecException, NoSuchTransactionFileException {
    deployAsync(
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
    sendTransactionAsync(
        to,
        super.contractLoader.getABIByContractName(contractName),
        functionName,
        params,
        remoteSignCallbackInterface);
  }

  @Override
  public CompletableFuture<TransactionReceipt> sendTransactionAsync(
      String to,
      String abi,
      String functionName,
      List<Object> params,
      RemoteSignCallbackInterface remoteSignCallbackInterface)
      throws ABICodecException {
    TransactionData rawTransaction = getRawTransaction(to, abi, functionName, params);
    byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
    transactionSignProvider.requestForSignAsync(
        rawTxHash, cryptoSuite.getCryptoTypeConfig(), remoteSignCallbackInterface);
    return signAndPush(rawTransaction, rawTxHash);
  }

  @Override
  public CompletableFuture<TransactionReceipt> sendTransactionAsync(
      String to, String abi, String functionName, List<Object> params) throws ABICodecException {
    TransactionData rawTransaction = getRawTransaction(to, abi, functionName, params);
    byte[] rawTxHash = transactionEncoder.encodeAndHashBytes(rawTransaction, cryptoKeyPair);
    return signAndPush(rawTransaction, rawTxHash);
  }

  @Override
  public TransactionReceipt encodeAndPush(
      TransactionData rawTransaction, byte[] rawTxHash, String signatureStr) {
    SignatureResult signatureResult =
        TransactionSignerServcie.decodeSignatureString(
            signatureStr,
            cryptoSuite.getCryptoTypeConfig(),
            cryptoSuite.createKeyPair().getHexPublicKey());
    byte[] signedTransaction =
        transactionEncoder.encodeToTransactionBytes(rawTransaction, rawTxHash, signatureResult);
    return transactionPusher.push(Hex.toHexString(signedTransaction));
  }

  @Override
  public CompletableFuture<TransactionReceipt> signAndPush(
      TransactionData rawTransaction, byte[] rawTxHash) {
    CompletableFuture<SignatureResult> future =
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
              return encodeAndPush(rawTransaction, rawTxHash, s.convertToString());
            });
    log.info("Sign and push over, wait for callback...");
    return cr;
  }
}
