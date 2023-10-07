package org.fisco.bcos.sdk.v3.client;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.tars.Callback;
import org.fisco.bcos.sdk.tars.Config;
import org.fisco.bcos.sdk.tars.CryptoSuite;
import org.fisco.bcos.sdk.tars.LogEntry;
import org.fisco.bcos.sdk.tars.RPCClient;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_bcos__bytesConstRef;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_bcos__h256;
import org.fisco.bcos.sdk.tars.SWIGTYPE_p_std__vectorT_unsigned_char_t;
import org.fisco.bcos.sdk.tars.SendTransaction;
import org.fisco.bcos.sdk.tars.StringVector;
import org.fisco.bcos.sdk.tars.Transaction;
import org.fisco.bcos.sdk.tars.TransactionFactoryImpl;
import org.fisco.bcos.sdk.tars.TransactionReceipt;
import org.fisco.bcos.sdk.tars.bcos;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarsClient extends ClientImpl implements Client {
    private static Logger logger = LoggerFactory.getLogger(TarsClient.class);
    private RPCClient tarsRPCClient;
    private TransactionFactoryImpl transactionFactory;
    private ThreadPoolExecutor asyncThreadPool;
    private Callback callback;

    private static final int queueSize = 10 * 10000;
    private static final String libFileName = System.mapLibraryName("bcos_swig_java");

    private static class Content {
        private SendTransaction sendTransaction;
        private Transaction transaction;
        private TransactionCallback callback;

        public SendTransaction getSendTransaction() {
            return sendTransaction;
        }

        public void setSendTransaction(SendTransaction sendTransaction) {
            this.sendTransaction = sendTransaction;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        public TransactionCallback getCallback() {
            return callback;
        }

        public void setCallback(TransactionCallback callback) {
            this.callback = callback;
        }
    };

    ConcurrentHashMap<Integer, Content> callbackMap =
            new ConcurrentHashMap<Integer, TarsClient.Content>();
    AtomicInteger currentSeq = new AtomicInteger();

    public RPCClient getTarsRPCClient() {
        return tarsRPCClient;
    }

    public void setTarsRPCClient(RPCClient tarsRPCClient) {
        this.tarsRPCClient = tarsRPCClient;
    }

    public TransactionFactoryImpl getTransactionFactory() {
        return transactionFactory;
    }

    public void setTransactionFactory(TransactionFactoryImpl transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    protected TarsClient(String groupID, ConfigOption configOption, long nativePointer) {
        super(groupID, configOption, nativePointer);

        loadLibrary();
        String connectionString =
                RPCClient.toConnectionString(
                        new StringVector(configOption.getNetworkConfig().getTarsPeers()));

        logger.info("Tars connection: {}", connectionString);
        Config config = new Config();
        config.setConnectionString(connectionString);
        config.setSendQueueSize(queueSize);
        config.setTimeoutMs(60 * 1000);
        tarsRPCClient = new RPCClient(config);

        CryptoSuite cryptoSuite =
                bcos.newCryptoSuite(configOption.getCryptoMaterialConfig().getUseSmCrypto());
        transactionFactory = new TransactionFactoryImpl(cryptoSuite);
        asyncThreadPool =
                new ThreadPoolExecutor(
                        1,
                        configOption.getThreadPoolConfig().getThreadPoolSize(),
                        0,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(queueSize));
        callback =
                new Callback() {
                    public void onMessage(int seq) {
                        asyncThreadPool.submit(
                                () -> {
                                    logger.debug("Receive seq: {}", seq);
                                    Content content = callbackMap.remove(seq);
                                    if (content != null) {
                                        TransactionReceipt receipt =
                                                content.getSendTransaction().get();
                                        content.getCallback()
                                                .onResponse(
                                                        toJSONTransactionReceipt(
                                                                receipt, content.getTransaction()));
                                    }
                                });
                    }
                };
    }

    private static AtomicBoolean loaded = new AtomicBoolean(false);

    private static void loadLibrary() {
        boolean inited = loaded.getAndSet(true);
        if (inited) {
            return;
        }
        try {
            File jniFile = File.createTempFile(libFileName, UUID.randomUUID().toString());
            InputStream jniStream = TarsClient.class.getResourceAsStream("/" + libFileName);
            Files.copy(
                    jniStream,
                    jniFile.getAbsoluteFile().toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.load(jniFile.getAbsolutePath());
            jniFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TarsClient build(String groupId, ConfigOption configOption, long nativePointer) {
        logger.info(
                "TarsClient build, groupID: {}, configOption: {}, nativePointer: {}",
                groupId,
                configOption,
                nativePointer);
        return new TarsClient(groupId, configOption, nativePointer);
    }

    @Override
    public BcosTransactionReceipt sendTransaction(
            String node, String signedTransactionData, boolean withProof) {
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
    public void sendTransactionAsync(
            String node,
            String signedTransactionData,
            boolean withProof,
            TransactionCallback callback) {
        logger.debug("sendTransactionAsync... {} {}", node, withProof);
        if (withProof) {
            super.sendTransactionAsync(node, signedTransactionData, withProof, callback);
            return;
        }
        node = Objects.isNull(node) ? "" : node;
        Transaction transaction = toTransaction(signedTransactionData);
        sendTransactionAsync(transaction, callback);
    }

    public void sendTransactionAsync(Transaction transaction, TransactionCallback callback) {
        int seq = currentSeq.addAndGet(1);

        SendTransaction sendTransaction = new SendTransaction(tarsRPCClient);
        sendTransaction.setCallback(this.callback);
        sendTransaction.setSeq(seq);

        Content content = new Content();
        content.setSendTransaction(sendTransaction);
        content.setTransaction(transaction);
        content.setCallback(callback);
        callbackMap.put(seq, content);

        sendTransaction.send(transaction);
    }

    private Transaction toTransaction(String signedTransactionData) {
        byte[] transactionBytes = Hex.decode(signedTransactionData);

        SWIGTYPE_p_std__vectorT_unsigned_char_t vectorTransactionBytes =
                bcos.toBytes(transactionBytes);
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
                bcos.logEntrySpanToVector(receipt.logEntries()).stream()
                        .map(
                                (LogEntry logEntry) -> {
                                    org.fisco.bcos.sdk.v3.model.TransactionReceipt.Logs
                                            rawLogEntry =
                                                    new org.fisco.bcos.sdk.v3.model
                                                            .TransactionReceipt.Logs();
                                    rawLogEntry.setAddress(bcos.toString(logEntry.address()));
                                    rawLogEntry.setBlockNumber(
                                            String.valueOf(receipt.blockNumber()));
                                    rawLogEntry.setData("0x" + bcos.toHex(logEntry.data()));
                                    rawLogEntry.setTopics(
                                            bcos.h256SpanToVector(logEntry.topics()).stream()
                                                    .map(
                                                            (SWIGTYPE_p_bcos__h256 hash) -> {
                                                                return "0x" + bcos.toHex(hash);
                                                            })
                                                    .collect(Collectors.toList()));
                                    return rawLogEntry;
                                })
                        .collect(Collectors.toList()));
        jsonReceipt.setStatus(receipt.status());
        jsonReceipt.setInput("0x" + bcos.toHex(transaction.input()));
        jsonReceipt.setOutput("0x" + bcos.toHex(receipt.output()));
        jsonReceipt.setExtraData(bcos.toString(transaction.extraData()));

        return jsonReceipt;
    }
}
