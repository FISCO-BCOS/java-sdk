package org.fisco.bcos.sdk.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.*;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.utils.ChannelUtils;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketConnection implements Connection {
    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnection.class);
    private final ConfigOption configOption;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();
    private final AtomicReference<Channel> channel = new AtomicReference<>();
    private WebSocketHandler handler;
    private AtomicBoolean isRunning;
    WSMessageHandler channelMsgHandler;
    private int protocolVersion;
    private NodeInfo nodeInfo;
    private SslContext sslCtx;
    private URI uri;
    private final String scheme = "ws://";
    private final ScheduledExecutorService scheduledExecutorService =
            new ScheduledThreadPoolExecutor(1);
    private final int connectInterval = 1000; // milliseconds
    private final ThreadPoolService threadPoolService;
    private final String endPoint;
    private AtomicLong blockNumber = new AtomicLong(0);

    @Override
    public long getBlockNumber() {
        return blockNumber.get();
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber.set(blockNumber);
    }

    private Timer timeoutHandler = new HashedWheelTimer();

    public WebSocketConnection(ConfigOption configOption, String endpoint) {
        this.endPoint = endpoint;
        this.configOption = configOption;
        String urlString;
        if (!endpoint.startsWith(this.scheme)) {
            urlString = this.scheme + endpoint;
        } else {
            urlString = endpoint;
        }
        try {
            this.uri = new URI(urlString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.threadPoolService =
                new ThreadPoolService(
                        "wsProcessor",
                        configOption.getThreadPoolConfig().getChannelProcessorThreadSize(),
                        configOption.getThreadPoolConfig().getMaxBlockingQueueSize());
    }

    @Override
    public String getEndPoint() {
        return this.endPoint;
    }

    // TODO: use promise ?
    class Callback extends ResponseCallback {
        public transient Response retResponse;
        public transient Semaphore semaphore = new Semaphore(1, true);

        Callback() {
            try {
                this.semaphore.acquire(1);
            } catch (InterruptedException e) {
                WebSocketConnection.logger.error("error :", e);
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void onTimeout() {
            super.onTimeout();
            this.semaphore.release();
        }

        @Override
        public void onResponse(Response response) {
            this.retResponse = response;
            if (this.retResponse != null && this.retResponse.getContent() != null) {
                WebSocketConnection.logger.trace("response: {}", this.retResponse.getContent());
            } else {
                WebSocketConnection.logger.error("response is null");
            }
            this.semaphore.release();
        }
    }

    /** @description close connection */
    @Override
    public void close() {
        this.isRunning.set(false);
        try {
            if (this.threadPoolService != null) {
                this.threadPoolService.stop();
            }
            this.workerGroup.shutdownGracefully().sync();
            if (this.channel.get() != null) {
                this.channel.get().close().sync();
            }
            logger.info("The connection of {} has been stopped", this.uri);
        } catch (InterruptedException e) {
            logger.warn("Stop netty failed for {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @return true if connected
     * @description connect to node
     */
    @Override
    public Boolean connect() {
        this.isRunning = new AtomicBoolean(true);
        //        try {
        //            Security.setProperty("jdk.disabled.namedCurves", "");
        //            System.setProperty("jdk.sunec.disableNative", "false");
        //            // Get file, file existence is already checked when check config file.
        //            FileInputStream caCert =
        //                    new FileInputStream(
        //                            new
        // File(this.configOption.getCryptoMaterialConfig().getCaCertPath()));
        //            FileInputStream sslCert =
        //                    new FileInputStream(
        //                            new
        // File(this.configOption.getCryptoMaterialConfig().getSdkCertPath()));
        //            FileInputStream sslKey =
        //                    new FileInputStream(
        //                            new File(
        //
        // this.configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()));
        //            // Init SslContext
        //            logger.info(" build ssl context with configured certificates ");
        //            this.sslCtx =
        //                    SslContextBuilder.forClient()
        //                            .trustManager(caCert)
        //                            .keyManager(sslCert, sslKey)
        //                            .sslProvider(SslProvider.OPENSSL)
        //                            // .sslProvider(SslProvider.JDK)
        //                            .build();
        //        } catch (FileNotFoundException | SSLException e) {
        //            logger.error(
        //                    "initSslContext failed, caCert: {}, sslCert: {}, sslKey: {}, error:
        // {}, e: {}",
        //                    this.configOption.getCryptoMaterialConfig().getCaCertPath(),
        //                    this.configOption.getCryptoMaterialConfig().getSdkCertPath(),
        //                    this.configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath(),
        //                    e.getMessage(),
        //                    e);
        //            return false;
        //        } catch (IllegalArgumentException e) {
        //            logger.error("initSslContext failed, error: {}, e: {}", e.getMessage(), e);
        //            return false;
        //        }
        WebSocketClientHandshaker handshaker =
                WebSocketClientHandshakerFactory.newHandshaker(
                        this.uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
        this.channelMsgHandler = new WSMessageHandler();

        class BlockNotifyHandler implements MsgHandler {

            public BlockNotifyHandler(WebSocketConnection connection) {
                this.connection = connection;
            }

            private WebSocketConnection connection;

            public WebSocketConnection getConnection() {
                return connection;
            }

            public void setConnection(WebSocketConnection connection) {
                this.connection = connection;
            }

            @Override
            public void onConnect(ChannelHandlerContext ctx) {
                // TODO:
            }

            @Override
            public void onMessage(ChannelHandlerContext ctx, Message msg) {
                byte[] data = msg.getData();
                try {
                    BlockNotify blockNotify = objectMapper.readValue(data, BlockNotify.class);
                    logger.info("blockNotify, blockNumber: {}", blockNotify.getBlockNumber());
                    connection.setBlockNumber(blockNotify.getBlockNumber());
                } catch (Exception e) {
                    logger.warn("blockNotify, msg error: {}, e:", msg.getErrorCode(), e);
                }
            }

            @Override
            public void onDisconnect(ChannelHandlerContext ctx) {
                // TODO:
            }
        }

        BlockNotifyHandler handler = new BlockNotifyHandler(this);
        // register blockNumber notify
        this.channelMsgHandler.addMessageHandler(MsgType.BLOCK_NOTIFY, handler);

        this.handler = new WebSocketHandler(handshaker, this.channelMsgHandler);
        this.handler.setMsgHandleThreadPool(this.threadPoolService.getThreadPool());
        this.bootstrap
                .group(this.workerGroup)
                .channel(NioSocketChannel.class)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline p = ch.pipeline();
                                if (WebSocketConnection.this.sslCtx != null) {
                                    p.addLast(
                                            WebSocketConnection.this.sslCtx.newHandler(
                                                    ch.alloc(),
                                                    WebSocketConnection.this.uri.getHost(),
                                                    WebSocketConnection.this.uri.getPort()));
                                }
                                p.addLast(
                                        new HttpClientCodec(),
                                        new HttpObjectAggregator(8192),
                                        //
                                        // WebSocketClientCompressionHandler.INSTANCE,
                                        WebSocketConnection.this.handler);
                            }
                        });
        return this.doConnect();
    }

    private void scheduleConnect() {
        this.scheduledExecutorService.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        if (WebSocketConnection.this.doConnect()) {
                            WebSocketConnection.this.handshake();
                        }
                    }
                },
                this.connectInterval,
                TimeUnit.MILLISECONDS);
    }

    public Boolean doConnect() {
        if (!this.isRunning.get()) {
            logger.info("the connection is stopped");
            return false;
        }
        // reconnect
        ChannelFuture f = this.bootstrap.connect(this.uri.getHost(), this.uri.getPort());
        f.addListener(
                new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) { // if is not successful, reconnect
                            future.channel().close();
                            WebSocketConnection.this
                                    .bootstrap
                                    .connect(
                                            WebSocketConnection.this.uri.getHost(),
                                            WebSocketConnection.this.uri.getPort())
                                    .addListener(this);
                        } else {
                            Channel channel = future.channel();
                            // add a listener to detect the connection lost
                            channel.closeFuture()
                                    .addListener(
                                            (ChannelFutureListener)
                                                    future1 -> {
                                                        WebSocketConnection.logger.warn(
                                                                "connection lost, try to recconnnect to "
                                                                        + WebSocketConnection.this
                                                                                .uri);
                                                        WebSocketConnection.this.scheduleConnect();
                                                    });
                            WebSocketConnection.this.channel.set(channel);
                        }
                    }
                });
        try {
            this.channel.set(f.sync().channel());
            this.handler.handshakeFuture().sync();
        } catch (InterruptedException e) {
            WebSocketConnection.logger.info(
                    "reconnect to {} failed, message:{}",
                    WebSocketConnection.this.uri,
                    e.getMessage());
            this.scheduleConnect();
        }
        return true;
    }

    private Boolean handshake() {
        Callback callback = new Callback();
        ChannelHandshake channelHandshake = new ChannelHandshake();
        try {
            byte[] payload = this.objectMapper.writeValueAsBytes(channelHandshake);
            Message message =
                    new Message(
                            (short) MsgType.CLIENT_HANDSHAKE.getType(),
                            ChannelUtils.newSeq(),
                            payload);
            this.asyncSendMessage(message, callback);
            this.waitResponse(callback);
            if (callback.retResponse.getErrorCode() != 0) {
                // TODO: throw exception
                return false;
            }
            this.nodeInfo =
                    this.objectMapper.readValue(callback.retResponse.getContent(), NodeInfo.class);
            logger.info("handshake whit node got: {}", this.nodeInfo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String getUri() {
        return this.uri.toString();
    }

    private void waitResponse(Callback callback) {
        try {
            callback.semaphore.acquire(1);
        } catch (InterruptedException e) {
            logger.error("waitResponse exception, error info: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * call rpc method
     *
     * @param request jsonrpc format string
     * @return response
     * @throws IOException
     */
    @Override
    public Response callMethod(String request) throws IOException {
        Callback callback = new Callback();
        Message message =
                new Message(
                        (short) MsgType.RPC_REQUEST.getType(),
                        ChannelUtils.newSeq(),
                        request.getBytes());

        this.asyncSendMessage(message, callback);
        this.waitResponse(callback);
        if (callback.retResponse.getErrorCode() != 0) {
            throw new IOException(callback.retResponse.getErrorMessage());
        }
        return callback.retResponse;
    }

    /**
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param request The request to be sent
     * @param callback callback to be called after receiving response
     */
    @Override
    public void asyncCallMethod(String request, ResponseCallback callback) throws IOException {
        Message message =
                new Message(
                        (short) MsgType.RPC_REQUEST.getType(),
                        ChannelUtils.newSeq(),
                        request.getBytes());
        this.asyncSendMessage(message, callback);
    }

    public void asyncSendMessage(Message message, ResponseCallback callback) {
        Options options = new Options();
        // use default timeout value
        if (callback.getTimeoutValue() == -1) {
            callback.setTimeoutValue(30000);
        }
        // if connection is lost reconnect it and send
        ByteBuf byteBuf = Unpooled.buffer();
        message.encode(byteBuf);
        WebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        Channel channel = this.channel.get();
        if (channel.isActive()) {
            this.channelMsgHandler.addSeq2CallBack(message.getSeq(), callback);
            startTimer(callback, message.getSeq());
            channel.writeAndFlush(frame);
        } else {
            logger.warn("send message with seq {} failed ", message.getSeq());
            Response response = new Response();
            response.setErrorCode(ChannelMessageError.CONNECTION_INVALID.getError());
            String errorContent = "Send message failed for connect failed";
            response.setErrorMessage(errorContent);
            response.setContent(errorContent);
            response.setMessageID(message.getSeq());
            if (callback != null) {
                callback.onResponse(response);
            }
        }
    }

    private void startTimer(ResponseCallback callback, String seq) {
        // disable the timeout when perf test
        if (callback.getTimeoutValue() == 0) {
            return;
        }
        callback.setTimeout(
                timeoutHandler.newTimeout(
                        new TimerTask() {
                            @Override
                            public void run(Timeout timeout) {
                                // handle timer
                                callback.onTimeout();
                                channelMsgHandler.getAndRemoveSeq(seq);
                            }
                        },
                        callback.getTimeoutValue(),
                        TimeUnit.MILLISECONDS));
    }

    public WSMessageHandler getChannelMsgHandler() {
        return channelMsgHandler;
    }

    public void setChannelMsgHandler(WSMessageHandler channelMsgHandler) {
        this.channelMsgHandler = channelMsgHandler;
    }
}
