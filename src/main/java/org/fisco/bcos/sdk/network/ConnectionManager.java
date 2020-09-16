/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SMSslClientContextFactory;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maintain peer connections. Start a schedule to reconnect failed peers.
 *
 * @author Maggie
 */
public class ConnectionManager {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private ChannelHandler channelHandler;
    private List<ConnectionInfo> connectionInfoList = new CopyOnWriteArrayList<>();
    private Map<String, ChannelHandlerContext> availableConnections = new ConcurrentHashMap<>();
    private EventLoopGroup workerGroup;
    private Boolean running = false;
    private Bootstrap bootstrap = new Bootstrap();
    private ScheduledExecutorService reconnSchedule = new ScheduledThreadPoolExecutor(1);

    public ConnectionManager(ConfigOption configOption, MsgHandler msgHandler) {
        for (String peerIpPort : configOption.getNetworkConfig().getPeers()) {
            connectionInfoList.add(new ConnectionInfo(peerIpPort));
        }
        channelHandler = new ChannelHandler(this, msgHandler);
        logger.info(" all connections: {}", connectionInfoList);
    }

    public void startConnect(ConfigOption configOption) throws NetworkException {
        if (running) {
            logger.debug("running");
            return;
        }
        logger.debug(" start connect. ");
        /** init netty * */
        initNetty(configOption);
        running = true;

        /** try connection */
        List<ChannelFuture> connChannelFuture = new ArrayList<ChannelFuture>();
        for (ConnectionInfo connect : connectionInfoList) {
            ChannelFuture channelFuture = bootstrap.connect(connect.getIp(), connect.getPort());
            connChannelFuture.add(channelFuture);
        }

        /** check connection result */
        boolean atLeastOneConnectSuccess = false;
        List<RetCode> errorMessageList = new ArrayList<>();
        for (int i = 0; i < connectionInfoList.size(); i++) {
            ConnectionInfo connInfo = connectionInfoList.get(i);
            ChannelFuture connectFuture = connChannelFuture.get(i);
            if (checkConnectionResult(connInfo, connectFuture, errorMessageList)) {
                atLeastOneConnectSuccess = true;
            }
        }

        /** check available connection */
        if (!atLeastOneConnectSuccess) {
            logger.error(" all connections have failed, {} ", errorMessageList);
            String errorMessageString = "";
            for (RetCode errorRetCode : errorMessageList) {
                errorMessageString += errorRetCode.getMessage() + "\n";
            }
            for (RetCode errorRetCode : errorMessageList) {
                if (errorRetCode.getCode() == NetworkException.SSL_HANDSHAKE_FAILED) {
                    throw new NetworkException(
                            " Failed to connect to all the nodes! errorMessage: \n"
                                    + errorMessageString,
                            NetworkException.SSL_HANDSHAKE_FAILED);
                }
            }
            throw new NetworkException(
                    " Failed to connect to all the nodes! errorMessage: \n" + errorMessageString,
                    NetworkException.CONNECT_FAILED);
        }
        logger.debug(" start connect end. ");
    }

    public void startReconnectSchedule() {
        logger.debug(" start reconnect schedule");
        reconnSchedule.scheduleAtFixedRate(
                () -> reconnect(),
                TimeoutConfig.reconnectDelay,
                TimeoutConfig.reconnectDelay,
                TimeUnit.MILLISECONDS);
    }

    public void stopReconnectSchedule() {
        ThreadPoolService.stopThreadPool(reconnSchedule);
    }

    public void stopNetty() {
        if (running) {
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            running = false;
        }
    }

    private void reconnect() {
        // Get connection which need reconnect
        List<ConnectionInfo> needReconnect = new ArrayList<>();
        int aliveConnectionCount = 0;
        for (ConnectionInfo connectionInfo : connectionInfoList) {
            ChannelHandlerContext ctx = availableConnections.get(connectionInfo.getEndPoint());
            if (Objects.isNull(ctx) || !ctx.channel().isActive()) {
                needReconnect.add(connectionInfo);
            } else {
                aliveConnectionCount++;
            }
        }
        logger.trace(" Keep alive nodes count: {}", aliveConnectionCount);

        // Reconnect
        for (ConnectionInfo connectionInfo : needReconnect) {
            ChannelFuture connectFuture =
                    bootstrap.connect(connectionInfo.getIp(), connectionInfo.getPort());
            List<RetCode> errorMessageList = new ArrayList<>();
            if (checkConnectionResult(connectionInfo, connectFuture, errorMessageList)) {
                logger.trace(
                        " reconnect to {}:{} success",
                        connectionInfo.getIp(),
                        connectionInfo.getPort());
            } else {
                logger.error(
                        " reconnect to {}:{}, error: {}",
                        connectionInfo.getIp(),
                        connectionInfo.getPort(),
                        errorMessageList);
            }
        }
    }

    public void setMsgHandleThreadPool(ExecutorService msgHandleThreadPool) {
        channelHandler.setMsgHandleThreadPool(msgHandleThreadPool);
    }

    public List<ConnectionInfo> getConnectionInfoList() {
        return connectionInfoList;
    }

    public Map<String, ChannelHandlerContext> getAvailableConnections() {
        return availableConnections;
    }

    public ChannelHandlerContext getConnectionCtx(String peer) {
        return availableConnections.get(peer);
    }

    private SslContext initSslContext(ConfigOption configOption) throws NetworkException {
        try {
            Security.setProperty("jdk.disabled.namedCurves", "");
            // Get file, file existence is already checked when check config file.
            FileInputStream caCert =
                    new FileInputStream(
                            new File(configOption.getCryptoMaterialConfig().getCaCertPath()));
            FileInputStream sslCert =
                    new FileInputStream(
                            new File(configOption.getCryptoMaterialConfig().getSdkCertPath()));
            FileInputStream sslKey =
                    new FileInputStream(
                            new File(
                                    configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()));

            // Init SslContext
            logger.info(" build ECDSA ssl context with configured certificates ");
            SslContext sslCtx =
                    SslContextBuilder.forClient()
                            .trustManager(caCert)
                            .keyManager(sslCert, sslKey)
                            .sslProvider(SslProvider.OPENSSL)
                            // .sslProvider(SslProvider.JDK)
                            .build();
            return sslCtx;
        } catch (FileNotFoundException | SSLException e) {
            throw new NetworkException(
                    "SSL context init failed, please make sure your cert and key files are properly configured. error info: "
                            + e.getMessage(),
                    NetworkException.INIT_CONTEXT_FAILED);
        } catch (IllegalArgumentException e) {
            throw new NetworkException(
                    "SSL context init failed, error info: " + e.getMessage(),
                    NetworkException.INIT_CONTEXT_FAILED);
        }
    }

    private SslContext initSMSslContext(ConfigOption configOption) throws NetworkException {
        try {
            // Get file, file existence is already checked when check config file.
            FileInputStream caCert =
                    new FileInputStream(
                            new File(configOption.getCryptoMaterialConfig().getCaCertPath()));
            FileInputStream sslCert =
                    new FileInputStream(
                            new File(configOption.getCryptoMaterialConfig().getSdkCertPath()));
            FileInputStream sslKey =
                    new FileInputStream(
                            new File(
                                    configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()));
            FileInputStream enCert =
                    new FileInputStream(
                            new File(configOption.getCryptoMaterialConfig().getEnSSLCertPath()));
            FileInputStream enKey =
                    new FileInputStream(
                            new File(
                                    configOption
                                            .getCryptoMaterialConfig()
                                            .getEnSSLPrivateKeyPath()));

            // Init SslContext
            logger.info(" build SM ssl context with configured certificates ");
            return SMSslClientContextFactory.build(caCert, enCert, enKey, sslCert, sslKey);
        } catch (IOException
                | CertificateException
                | NoSuchAlgorithmException
                | InvalidKeySpecException
                | NoSuchProviderException e) {
            throw new NetworkException(
                    "SSL context init failed, please make sure your cert and key files are properly configured. error info: "
                            + e.getMessage(),
                    e);
        }
    }

    private void initNetty(ConfigOption configOption) throws NetworkException {
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        // set connection timeout
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) TimeoutConfig.connectTimeout);
        int sslCryptoType = configOption.getCryptoMaterialConfig().getSslCryptoType();
        SslContext sslContext =
                (sslCryptoType == CryptoInterface.ECDSA_TYPE
                        ? initSslContext(configOption)
                        : initSMSslContext(configOption));
        SslContext finalSslContext = sslContext;
        ChannelInitializer<SocketChannel> initializer =
                new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /*
                         * Each connection is fetched from the socketChannel
                         */
                        SslHandler sslHandler = finalSslContext.newHandler(ch.alloc());
                        sslHandler.setHandshakeTimeoutMillis(TimeoutConfig.sslHandShakeTimeout);
                        ch.pipeline()
                                .addLast(
                                        sslHandler,
                                        new LengthFieldBasedFrameDecoder(
                                                Integer.MAX_VALUE, 0, 4, -4, 0),
                                        new IdleStateHandler(
                                                TimeoutConfig.idleTimeout,
                                                TimeoutConfig.idleTimeout,
                                                TimeoutConfig.idleTimeout,
                                                TimeUnit.MILLISECONDS),
                                        new MessageEncoder(),
                                        new MessageDecoder(),
                                        channelHandler);
                    }
                };
        bootstrap.handler(initializer);
    }

    private boolean checkConnectionResult(
            ConnectionInfo connInfo, ChannelFuture connectFuture, List<RetCode> errorMessageList) {
        connectFuture.awaitUninterruptibly();
        if (!connectFuture.isSuccess()) {
            /** connect failed. */
            if (Objects.isNull(connectFuture.cause())) {
                logger.error("connect to {}:{} failed. ", connInfo.getIp(), connInfo.getPort());
            } else {
                logger.error(
                        "connect to {}:{} failed. {}",
                        connInfo.getIp(),
                        connInfo.getPort(),
                        connectFuture.cause().getMessage());
            }
            errorMessageList.add(
                    new RetCode(
                            NetworkException.CONNECT_FAILED,
                            "connect to "
                                    + connInfo.getIp()
                                    + ":"
                                    + connInfo.getPort()
                                    + " failed"));
            return false;
        } else {
            /** connect success, check ssl handshake result. */
            SslHandler sslhandler = connectFuture.channel().pipeline().get(SslHandler.class);
            String checkerMessage =
                    "! Please check the certificate and ensure that the SDK and the node are in the same agency!";
            if (Objects.isNull(sslhandler)) {
                String sslHandshakeFailedMessage =
                        " ssl handshake failed:/"
                                + connInfo.getIp()
                                + ":"
                                + connInfo.getPort()
                                + checkerMessage;
                logger.debug(sslHandshakeFailedMessage);
                errorMessageList.add(
                        new RetCode(
                                NetworkException.SSL_HANDSHAKE_FAILED, sslHandshakeFailedMessage));
                return false;
            }

            Future<Channel> sshHandshakeFuture =
                    sslhandler.handshakeFuture().awaitUninterruptibly();
            if (sshHandshakeFuture.isSuccess()) {
                logger.trace(" ssl handshake success {}:{}", connInfo.getIp(), connInfo.getPort());
                return true;
            } else {
                String sslHandshakeFailedMessage =
                        " ssl handshake failed:/"
                                + connInfo.getIp()
                                + ":"
                                + connInfo.getPort()
                                + checkerMessage;
                logger.debug(sslHandshakeFailedMessage);
                errorMessageList.add(
                        new RetCode(
                                NetworkException.SSL_HANDSHAKE_FAILED, sslHandshakeFailedMessage));
                return false;
            }
        }
    }

    protected ChannelHandlerContext addConnectionContext(
            String ip, int port, ChannelHandlerContext ctx) {
        String endpoint = ip + ":" + port;
        return availableConnections.put(endpoint, ctx);
    }

    protected void removeConnectionContext(String ip, int port, ChannelHandlerContext ctx) {
        String endpoint = ip + ":" + port;
        if (Objects.isNull(availableConnections.get(endpoint))) {
            return;
        }
        Boolean result = availableConnections.remove(endpoint, ctx);
        if (logger.isDebugEnabled()) {
            logger.debug(
                    " result: {}, host: {}, port: {}, ctx: {}",
                    result,
                    ip,
                    port,
                    System.identityHashCode(ctx));
        }
    }

    protected void removeConnection(String peerIpPort) {
        for (ConnectionInfo conn : connectionInfoList) {
            String ipPort = conn.getIp() + ":" + conn.getPort();
            if (ipPort.equals(peerIpPort)) {
                connectionInfoList.remove(conn);
                return;
            }
        }
    }
}
