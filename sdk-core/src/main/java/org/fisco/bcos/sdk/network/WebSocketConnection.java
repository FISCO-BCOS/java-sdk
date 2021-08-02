package org.fisco.bcos.sdk.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.utils.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Security;
import java.util.concurrent.Semaphore;

public class WebSocketConnection implements Connection {
    private static Logger logger = LoggerFactory.getLogger(WebSocketConnection.class);
    private ConfigOption configOption;
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private WebSocketHandler handler;
    ChannelMsgHandler channelMsgHandler;
    private SslContext sslCtx;
    private final String uri;

    public WebSocketConnection(ConfigOption configOption) {
        this.configOption = configOption;
        if (!configOption.getNetworkConfig().getPeers().get(0).startsWith("wss://")) {
            this.uri = "wss://" + configOption.getNetworkConfig().getPeers().get(0);
        } else {
            this.uri = configOption.getNetworkConfig().getPeers().get(0);
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
        try {
            Security.setProperty("jdk.disabled.namedCurves", "");
            System.setProperty("jdk.sunec.disableNative", "false");
            // Get file, file existence is already checked when check config file.
            FileInputStream caCert =
                    new FileInputStream(
                            new File(this.configOption.getCryptoMaterialConfig().getCaCertPath()));
            FileInputStream sslCert =
                    new FileInputStream(
                            new File(this.configOption.getCryptoMaterialConfig().getSdkCertPath()));
            FileInputStream sslKey =
                    new FileInputStream(
                            new File(
                                    this.configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()));
            // Init SslContext
            logger.info(" build ssl context with configured certificates ");
            this.sslCtx =
                    SslContextBuilder.forClient()
                            .trustManager(caCert)
                            .keyManager(sslCert, sslKey)
                            .sslProvider(SslProvider.OPENSSL)
                            // .sslProvider(SslProvider.JDK)
                            .build();
        } catch (FileNotFoundException | SSLException e) {
            logger.error(
                    "initSslContext failed, caCert: {}, sslCert: {}, sslKey: {}, error: {}, e: {}",
                    this.configOption.getCryptoMaterialConfig().getCaCertPath(),
                    this.configOption.getCryptoMaterialConfig().getSdkCertPath(),
                    this.configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath(),
                    e.getMessage(),
                    e);
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("initSslContext failed, error: {}, e: {}", e.getMessage(), e);
            return false;
        }
        try {
            URI uri = new URI(this.uri);
            final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
            final int port;
            if (uri.getPort() == -1) {
                port = 443;
            } else {
                port = uri.getPort();
            }
            WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri,
                    WebSocketVersion.V13, null,
                    false, new DefaultHttpHeaders());
            this.channelMsgHandler = new ChannelMsgHandler();
            this.handler = new WebSocketHandler(handshaker, this.channelMsgHandler);
            this.bootstrap.group(this.workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(WebSocketConnection.this.sslCtx.newHandler(ch.alloc(), host, port));
                            p.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
//                                    WebSocketClientCompressionHandler.INSTANCE,
                                    WebSocketConnection.this.handler);
                        }
                    });
            //TODO: reconnect
            this.channel = this.bootstrap.connect(host, port).sync().channel();
//            this.channel.closeFuture().addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//
//                }
//            })

            this.handler.handshakeFuture().sync();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
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

    public Boolean doConnect() {
        // TODO: implement reconnect
        return true;
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
        Callback callback = new Callback();
        Message message = new Message((short) MsgType.RPC_REQUEST.ordinal(), ChannelUtils.newSeq(), request.getBytes());
        // FIXME: complete message use request
        this.asyncSendMessage(message, callback);
        this.waitResponse(callback);
        if (callback.retResponse.getErrorCode() != 0) {
            // TODO: throw exception
        }
        return callback.retResponse.getContent();
    }

    @Override
    public String getUri() {
        return this.uri;
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
        Message message = new Message((short) MsgType.RPC_REQUEST.ordinal(), ChannelUtils.newSeq(), request.getBytes());
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
