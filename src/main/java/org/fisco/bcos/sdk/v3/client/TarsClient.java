package org.fisco.bcos.sdk.v3.client;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fisco.bcos.sdk.tars.LogEntry;
import org.fisco.bcos.sdk.tars.TransactionReceipt;
import org.fisco.bcos.sdk.tars.bcos;
import org.fisco.bcos.sdk.tars.RPCClient;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_bcos__bytesConstRef;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_bcos__h256;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_std__vectorT_unsigned_char_t;
import org.fisco.bcos.sdk.tars.SendTransaction;
import org.fisco.bcos.sdk.tars.StringVector;
import org.fisco.bcos.sdk.tars.Transaction;
import org.fisco.bcos.sdk.tars.TransactionFactoryImpl;
import org.fisco.bcos.sdk.tars.Callback;
import org.fisco.bcos.sdk.tars.CryptoSuite;

public class TarsClient extends ClientImpl implements Client {
  private static Logger logger = LoggerFactory.getLogger(TarsClient.class);
  private RPCClient tarsRPCClient;
  private TransactionFactoryImpl transactionFactory;
  private ThreadPoolExecutor asyncThreadPool;

  protected TarsClient(String groupID, ConfigOption configOption, long nativePointer) {
    super(groupID, configOption, nativePointer);
    String connectionString = RPCClient
        .toConnectionString(new StringVector(configOption.getNetworkConfig().getTarsPeers()));

    logger.info("Tars connection: {}", connectionString);
    tarsRPCClient = new RPCClient(connectionString);

    CryptoSuite cryptoSuite =
        bcos.newCryptoSuite(configOption.getCryptoMaterialConfig().getUseSmCrypto());
    transactionFactory = new TransactionFactoryImpl(cryptoSuite);
    asyncThreadPool =
        new ThreadPoolExecutor(1, configOption.getThreadPoolConfig().getThreadPoolSize(), 0,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100 * 10000));
  }

  public static void loadLibrary() {
    System.loadLibrary("bcos_swig_java");
  }

  public static TarsClient build(String groupId, ConfigOption configOption, long nativePointer) {
    logger.info("build, groupID: {}, configOption: {}, nativePointer: {}", groupId, configOption,
        nativePointer);
    return new TarsClient(groupId, configOption, nativePointer);
  }

  @Override
  public BcosTransactionReceipt sendTransaction(String node, String signedTransactionData,
      boolean withProof) {
    if (withProof) {
      return super.sendTransaction(node, signedTransactionData, withProof);
    }
    node = Objects.isNull(node) ? "" : node;

    Transaction transaction = toTransaction(signedTransactionData);
    TransactionReceipt receipt = new SendTransaction(tarsRPCClient).send(transaction).get();
    BcosTransactionReceipt bcosReceipt = new BcosTransactionReceipt();
    bcosReceipt.setResult(toJSONTransactionReceipt(receipt, transaction));

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
    Transaction transaction = toTransaction(signedTransactionData);
    SendTransaction sendTransaction = new SendTransaction(tarsRPCClient);

    sendTransaction.setCallback(new Callback() {
      public void onMessage() {
        asyncThreadPool.submit(() -> {
          TransactionReceipt receipt = sendTransaction.get();
          callback.onResponse(toJSONTransactionReceipt(receipt, transaction));
        });
      }
    });
    sendTransaction.send(transaction);
  }

  private Transaction toTransaction(String signedTransactionData) {
    byte[] transactionBytes = Hex.decode(signedTransactionData);

    // Move data from java to jni
    SWIGTYPE_p_std__vectorT_unsigned_char_t vectorTransactionBytes = bcos.toBytes(transactionBytes);

    SWIGTYPE_p_bcos__bytesConstRef ref = bcos.toBytesConstRef(vectorTransactionBytes);
    Transaction transaction = transactionFactory.createTransaction(ref, false, false);
    return transaction;
  }

  private org.fisco.bcos.sdk.v3.model.TransactionReceipt toJSONTransactionReceipt(
      TransactionReceipt receipt, Transaction transaction) {
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
