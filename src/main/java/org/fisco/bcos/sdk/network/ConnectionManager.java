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
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private ConfigOption configOps;
    private MsgHandler msgHandler;
    private List<ConnectionInfo> connectionInfoList = new ArrayList<ConnectionInfo>();
    private Map<String, ChannelHandlerContext> availableConnections;
    private Boolean running = false;
    private Bootstrap bootstrap = new Bootstrap();
    private ChannelHandler channelHandler;
    private String algorithm = "ecdsa";
    private ScheduledExecutorService reconnSchedule = new ScheduledThreadPoolExecutor(1);

    public ConnectionManager(ConfigOption configOps, MsgHandler msgHandler) {
        this.configOps = configOps;
        this.msgHandler = msgHandler;
        init();
    }

    /** Init connections */
    public void init() {
        for (String peerIpPort : configOps.getPeers()) {
            connectionInfoList.add(new ConnectionInfo(peerIpPort));
        }
        if (null != configOps.getAlgorithm() && configOps.getAlgorithm().equals("sm2")) {
            this.algorithm = "sm2";
        }
        channelHandler = new ChannelHandler(this, msgHandler);
        logger.info(" all connections: {}", connectionInfoList);
    }

    public void startConnect() throws NetworkException {
        if (running) {
            logger.debug("running");
            return;
        }
        logger.debug(" start connect. ");

        /** init netty * */
        initNetty();

        /** try connection */
        List<ChannelFuture> connChannelFuture = new ArrayList<ChannelFuture>();
        for (ConnectionInfo connect : connectionInfoList) {
            ChannelFuture channelFuture = bootstrap.connect(connect.getIp(), connect.getPort());
            connChannelFuture.add(channelFuture);
        }

        /** check connection result */
        boolean atLeastOneConnectSuccess = false;
        List<String> errorMessageList = new ArrayList<>();
        for (int i = 0; i < connectionInfoList.size(); i++) {
            ConnectionInfo connInfo = connectionInfoList.get(i);
            ChannelFuture connectFuture = connChannelFuture.get(i);

            if (checkConnectionResult(connInfo, connectFuture, errorMessageList)) {
                atLeastOneConnectSuccess = true;
            }

            /** check available connection */
            if (!atLeastOneConnectSuccess) {
                logger.error(" all connections have failed, " + errorMessageList.toString());
                throw new NetworkException(
                        " Failed to connect to nodes: " + errorMessageList.toString());
            }
            running = true;
            logger.debug(" start connect end. ");
        }
    }

    public void startReconnectSchedule() {
        reconnSchedule.scheduleAtFixedRate(
                () -> reconnect(), 0, TimeoutConfig.reconnectDelay, TimeUnit.MILLISECONDS);
    }

    public void stopReconnectSchedule() {
        reconnSchedule.shutdown();
    }

    private void reconnect() {
        // Get connection which need reconnect
        List<ConnectionInfo> needReconnect = new ArrayList<>();
        int aliveConnectionCount = 0;
        for (ConnectionInfo connectionInfo : connectionInfoList) {
            ChannelHandlerContext ctx = availableConnections.get(connectionInfo.getEndPoint());
            if (Objects.isNull(ctx) || ctx.channel().isActive()) {
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
            List<String> errorMessageList = new ArrayList<>();
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
                        connectFuture.cause().getMessage());
            }
        }
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

    private SslContext initSslContext() throws NetworkException {
        try {
            // Get file, file existence is already checked when check config file.
            FileInputStream caCert = new FileInputStream(new File(configOps.getCaCert()));
            FileInputStream sslCert = new FileInputStream(new File(configOps.getSslCert()));
            FileInputStream sslKey = new FileInputStream(new File(configOps.getSslKey()));

            // Init SslContext
            logger.info(" build ECDSA ssl context with configured certificates ");
            SslContext sslCtx =
                    SslContextBuilder.forClient()
                            .trustManager(caCert)
                            .keyManager(sslCert, sslKey)
                            .sslProvider(SslProvider.JDK)
                            .build();
            return sslCtx;
        } catch (FileNotFoundException | SSLException e) {
            throw new NetworkException(e);
        }
    }

    private SslContext initSMSslContext() throws NetworkException {
        try {
            // Get file, file existence is already checked when check config file.
            FileInputStream caCert = new FileInputStream(new File(configOps.getCaCert()));
            FileInputStream sslCert = new FileInputStream(new File(configOps.getSslCert()));
            FileInputStream sslKey = new FileInputStream(new File(configOps.getSslKey()));
            FileInputStream enCert = new FileInputStream(new File(configOps.getEnSslCert()));
            FileInputStream enKey = new FileInputStream(new File(configOps.getEnSslKey()));

            // Init SslContext
            logger.info(" build SM ssl context with configured certificates ");
            return SMSslClientContextFactory.build(caCert, sslCert, sslKey, enCert, enKey);
        } catch (IOException
                | CertificateException
                | NoSuchAlgorithmException
                | InvalidKeySpecException
                | NoSuchProviderException e) {
            throw new NetworkException(e);
        }
    }

    private void initNetty() throws NetworkException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        // set connection timeout
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) TimeoutConfig.connectTimeout);
        SslContext sslContext = (algorithm.equals("ecdsa") ? initSslContext() : initSMSslContext());
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
            ConnectionInfo connInfo, ChannelFuture connectFuture, List<String> errorMessageList) {
        if (!connectFuture.isSuccess()) {
            /** connect failed. */
            String connectFailedMessage =
                    Objects.isNull(connectFuture.cause())
                            ? "connect to " + connInfo.getIp() + ":" + connInfo.getIp() + " failed"
                            : connectFuture.cause().getMessage();
            logger.error(connectFailedMessage);
            errorMessageList.add(connectFailedMessage);
            return false;
        } else {
            /** connect success, check ssl handshake result. */
            SslHandler sslhandler = connectFuture.channel().pipeline().get(SslHandler.class);
            if (Objects.isNull(sslhandler)) {
                String sslHandshakeFailedMessage =
                        " ssl handshake failed:/" + connInfo.getIp() + ":" + connInfo.getIp();
                logger.debug(sslHandshakeFailedMessage);
                errorMessageList.add(sslHandshakeFailedMessage);
                return false;
            }

            Future<Channel> sshHandshakeFuture =
                    sslhandler.handshakeFuture().awaitUninterruptibly();
            if (sshHandshakeFuture.isSuccess()) {
                logger.trace(" ssl handshake success {}:{}", connInfo.getIp(), connInfo.getIp());
                return true;
            } else {
                String sslHandshakeFailedMessage =
                        " ssl handshake failed:/" + connInfo.getIp() + ":" + connInfo.getIp();
                logger.debug(sslHandshakeFailedMessage);
                errorMessageList.add(sslHandshakeFailedMessage);
                return false;
            }
        }
    };

    protected ChannelHandlerContext addConnectionContext(
            String ip, int port, ChannelHandlerContext ctx) {
        String endpoint = ip + ":" + port;
        return availableConnections.put(endpoint, ctx);
    }

    protected void removeConnectionContext(String ip, int port, ChannelHandlerContext ctx) {
        String endpoint = ip + ":" + port;
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
}
