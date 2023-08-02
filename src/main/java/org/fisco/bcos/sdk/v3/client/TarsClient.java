package org.fisco.bcos.sdk.v3.client;

import java.math.BigInteger;
import java.util.Objects;
import java.util.stream.Collectors;

import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.utils.Hex;

import org.fisco.bcos.sdk.tars.LogEntry;
import org.fisco.bcos.sdk.tars.TransactionReceipt;
import org.fisco.bcos.sdk.tars.bcos;
import org.fisco.bcos.sdk.tars.RPCClient;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_bcos__h256;
import org.fisco.bcos.sdk.tars.SendTransaction;
import org.fisco.bcos.sdk.tars.Transaction;
import org.fisco.bcos.sdk.tars.TransactionFactoryImpl;
import org.fisco.bcos.sdk.tars.Callback;
import org.fisco.bcos.sdk.tars.CryptoSuite;

public class TarsClient extends ClientImpl implements Client {
  private RPCClient tarsRPCClient;
  private TransactionFactoryImpl transactionFactory;;

  protected TarsClient(String groupID, ConfigOption configOption, long nativePointer,
      String connectionString) {
    super(groupID, configOption, nativePointer);
    tarsRPCClient = new RPCClient(connectionString);

    CryptoSuite cryptoSuite =
        bcos.newCryptoSuite(configOption.getCryptoMaterialConfig().getUseSmCrypto());
    transactionFactory = new TransactionFactoryImpl(cryptoSuite);
  }

  @Override
  public BcosTransactionReceipt sendTransaction(String node, String signedTransactionData,
      boolean withProof) {
    if (withProof) {
      return super.sendTransaction(node, signedTransactionData, withProof);
    }
    node = Objects.isNull(node) ? "" : node;

    Transaction transaction = transactionFactory
        .createTransaction(bcos.toBytesConstRef(Hex.decode(signedTransactionData)));
    TransactionReceipt receipt = new SendTransaction(tarsRPCClient).send(transaction).get();
    BcosTransactionReceipt bcosReceipt = new BcosTransactionReceipt();
    bcosReceipt.setResult(convert(receipt, transaction));

    return bcosReceipt;
  }

  @Override
  public void sendTransactionAsync(String node, String signedTransactionData, boolean withProof,
      TransactionCallback callback) {
    if (withProof) {
      super.sendTransactionAsync(node, signedTransactionData, withProof, callback);
      return;
    }
    node = Objects.isNull(node) ? "" : node;
    Transaction transaction = transactionFactory
        .createTransaction(bcos.toBytesConstRef(Hex.decode(signedTransactionData)));
    SendTransaction sendTransaction = new SendTransaction(tarsRPCClient);

    sendTransaction.setCallback(new Callback() {
      public void onMessage() {
        TransactionReceipt receipt = sendTransaction.get();
        callback.onResponse(convert(receipt, transaction));
      }
    });
    sendTransaction.send(transaction);
  }

  private org.fisco.bcos.sdk.v3.model.TransactionReceipt convert(TransactionReceipt receipt,
      Transaction transaction) {
    org.fisco.bcos.sdk.v3.model.TransactionReceipt jsonReceipt =
        new org.fisco.bcos.sdk.v3.model.TransactionReceipt();
    jsonReceipt.setTransactionHash("0x" + bcos.toHex(transaction.hash()));
    jsonReceipt.setVersion(receipt.version());
    jsonReceipt.setReceiptHash("0x" + bcos.toHex(receipt.hash()));
    jsonReceipt.setBlockNumber(BigInteger.valueOf(receipt.blockNumber()));
    jsonReceipt.setFrom(bcos.toString(transaction.sender()));
    jsonReceipt.setTo(bcos.toString(transaction.to()));
    jsonReceipt.setGasUsed(bcos.toString(receipt.gasUsed()));
    jsonReceipt.setContractAddress(bcos.toString(receipt.contractAddress()));
    jsonReceipt.setChecksumContractAddress(jsonReceipt.getContractAddress()); // FIXME: how to?
    jsonReceipt.setLogEntries(
        bcos.logEntrySpanToVector(receipt.logEntries()).stream().map((LogEntry logEntry) -> {
          org.fisco.bcos.sdk.v3.model.TransactionReceipt.Logs rawLogEntry =
              new org.fisco.bcos.sdk.v3.model.TransactionReceipt.Logs();
          rawLogEntry.setAddress(bcos.toString(logEntry.address()));
          rawLogEntry.setBlockNumber(String.valueOf(receipt.blockNumber()));
          rawLogEntry.setData("0x" + bcos.toHex(logEntry.data()));
          rawLogEntry.setTopics(bcos.h256SpanToVector(logEntry.topics()).stream()
              .map((SWIGTYPE_p_bcos__h256 hash) -> {
                return "0x" + bcos.toHex(hash);
              }).collect(Collectors.toList()));
          return rawLogEntry;
        }).collect(Collectors.toList()));
    jsonReceipt.setStatus(receipt.status());
    jsonReceipt.setInput("0x" + bcos.toHex(transaction.input()));
    jsonReceipt.setOutput("0x" + bcos.toHex(receipt.output()));
    jsonReceipt.setExtraData(bcos.toString(transaction.extraData()));

    return jsonReceipt;
  }
}
