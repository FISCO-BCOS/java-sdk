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
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.ChannelHandshake;
import org.fisco.bcos.sdk.channel.model.NodeInfo;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.utils.ChannelUtils;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketConnection implements Connection {
    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(WebSocketConnection.class);
    private ConfigOption configOption;
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private WebSocketHandler handler;
    private AtomicBoolean isRunning;
    WSMessageHandler channelMsgHandler;
    private int protocolVersion;
    private NodeInfo nodeInfo;
    private SslContext sslCtx;
    private URI uri;
    private final String scheme = "ws://";
    private final Timer timer = new Timer();
    private final int connectInterval = 1000; // milliseconds


    public WebSocketConnection(ConfigOption configOption, String endpoint) {
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

    /**
     * @description close connection
     */
    @Override
    public void close() {
        this.isRunning.set(false);
        try {
            this.workerGroup.shutdownGracefully().sync();
            if (this.channel != null) {
                this.channel.closeFuture().sync();
            }
            if (this.channel != null) {
                this.channel.close().sync();
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
//                            new File(this.configOption.getCryptoMaterialConfig().getCaCertPath()));
//            FileInputStream sslCert =
//                    new FileInputStream(
//                            new File(this.configOption.getCryptoMaterialConfig().getSdkCertPath()));
//            FileInputStream sslKey =
//                    new FileInputStream(
//                            new File(
//                                    this.configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()));
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
//                    "initSslContext failed, caCert: {}, sslCert: {}, sslKey: {}, error: {}, e: {}",
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
    
        return doConnect();
    }


    private void scheduleConnect() {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                WebSocketConnection.this.doConnect();
            }
        }, this.connectInterval);
    }

    public Boolean doConnect() {
        if (!this.isRunning.get()) {
            logger.info("the connection is stopped");
            return false;
        }
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(this.uri,
                WebSocketVersion.V13, null,
                false, new DefaultHttpHeaders());
        this.channelMsgHandler = new WSMessageHandler();
        this.handler = new WebSocketHandler(handshaker, this.channelMsgHandler);
        this.bootstrap.group(this.workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (WebSocketConnection.this.sslCtx != null) {
                            p.addLast(WebSocketConnection.this.sslCtx.newHandler(ch.alloc(), WebSocketConnection.this.uri.getHost(), WebSocketConnection.this.uri.getPort()));
                        }
                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
//                                    WebSocketClientCompressionHandler.INSTANCE,
                                WebSocketConnection.this.handler);
                    }
                });
        // reconnect
        ChannelFuture f = this.bootstrap.connect(this.uri.getHost(), this.uri.getPort());
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {//if is not successful, reconnect
                    future.channel().close();
                    WebSocketConnection.this.bootstrap.connect(WebSocketConnection.this.uri.getHost(), WebSocketConnection.this.uri.getPort()).addListener(this);
                } else {//good, the connection is ok
                    WebSocketConnection.this.channel = future.channel();
                    //add a listener to detect the connection lost
                    WebSocketConnection.this.channel.closeFuture().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            WebSocketConnection.logger.warn("connection lost, try to recconnnect to " + WebSocketConnection.this.uri);
                            WebSocketConnection.this.scheduleConnect();
                        }
                    });

                }
            }
        });
        System.out.println("connecting :" + this.uri.getHost() + ":" + this.uri.getPort());
        try {
            this.channel = f.sync().channel();
            this.handler.handshakeFuture().sync();
        } catch (InterruptedException e) {
            WebSocketConnection.logger.info("reconnect to {} failed, message:{}", WebSocketConnection.this.uri,
                    e.getMessage());
            this.scheduleConnect();
        }
        return true;
    }

    private class CheckCertExistenceResult {
        private boolean checkPassed = true;
        private String errorMessage = "";

        public boolean isCheckPassed() {
            return this.checkPassed;
        }

        public void setCheckPassed(boolean checkPassed) {
            this.checkPassed = checkPassed;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private CheckCertExistenceResult checkCertExistence(boolean isSM) {

        CheckCertExistenceResult result = new CheckCertExistenceResult();
        result.setCheckPassed(true);
        String errorMessage = "";
        errorMessage = errorMessage + "Please make sure ";
        if (!new File(this.configOption.getCryptoMaterialConfig().getCaCertPath()).exists()) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + this.configOption.getCryptoMaterialConfig().getCaCertPath() + " ";
        }
        if (!new File(this.configOption.getCryptoMaterialConfig().getSdkCertPath()).exists()) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + this.configOption.getCryptoMaterialConfig().getSdkCertPath() + " ";
        }
        if (!new File(this.configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()).exists()) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage
                            + this.configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()
                            + " ";
        }
        if (!isSM) {
            errorMessage = errorMessage + "exists!";
            result.setErrorMessage(errorMessage);
            return result;
        }
        if (!new File(this.configOption.getCryptoMaterialConfig().getEnSSLCertPath()).exists()) {
            errorMessage =
                    errorMessage + this.configOption.getCryptoMaterialConfig().getEnSSLCertPath() + " ";
            result.setCheckPassed(false);
        }
        if (!new File(this.configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyPath()).exists()) {
            errorMessage =
                    errorMessage
                            + this.configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyPath()
                            + " ";
            result.setCheckPassed(false);
        }
        errorMessage = errorMessage + "exist!";
        result.setErrorMessage(errorMessage);
        return result;
    }

    /**
     * call rpc method
     *
     * @param request jsonrpc format string
     * @return response
     * @throws IOException
     */
    @Override
    public String callMethod(String request) throws IOException {
        System.out.println("executing request:" + request);
        Callback callback = new Callback();
        Message message = new Message((short) MsgType.RPC_REQUEST.getType(), ChannelUtils.newSeq(), request.getBytes());
        // FIXME: complete message use request
        this.asyncSendMessage(message, callback);
        this.waitResponse(callback);
        if (callback.retResponse.getErrorCode() != 0) {
            // TODO: throw exception
        }
        System.out.println("response : " + callback.retResponse.getContent());
        return callback.retResponse.getContent();
    }

    private Boolean handShake() {
        Callback callback = new Callback();
        ChannelHandshake channelHandshake = new ChannelHandshake();
        try {
            byte[] payload = this.objectMapper.writeValueAsBytes(channelHandshake);
            Message message = new Message((short) MsgType.CLIENT_HANDSHAKE.getType(), ChannelUtils.newSeq(), payload);
            this.asyncSendMessage(message, callback);
            this.waitResponse(callback);
            if (callback.retResponse.getErrorCode() != 0) {
                // TODO: throw exception
                return false;
            }
            this.nodeInfo = this.objectMapper.readValue(callback.retResponse.getContent(),
                    NodeInfo.class);
            System.out.println(this.nodeInfo);

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
     * Send a message to a node in the group and select the node with the highest block height in
     * the group
     *
     * @param request  The request to be sent
     * @param callback callback to be called after receiving response
     */
    @Override
    public void asyncCallMethod(String request, ResponseCallback callback) throws IOException {
        Message message = new Message((short) MsgType.RPC_REQUEST.getType(), ChannelUtils.newSeq(), request.getBytes());
        this.asyncSendMessage(message, callback);
    }

    public void asyncSendMessage(Message message, ResponseCallback callback) {
        // if connection is lost reconnect it and send
        ByteBuf byteBuf = Unpooled.buffer();
        message.encode(byteBuf);
        WebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        this.channel.writeAndFlush(frame);
        this.channelMsgHandler.addSeq2CallBack(message.getSeq(), callback);
    }
}
