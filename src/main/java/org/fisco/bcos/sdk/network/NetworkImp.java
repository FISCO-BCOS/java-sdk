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

import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of Network
 *
 * @author Maggie
 */
public class NetworkImp implements Network {
    private static Logger logger = LoggerFactory.getLogger(NetworkImp.class);
    private ConnectionManager connManager;
    private ConfigOption configOption;
    private String configFilePath;
    private MsgHandler handler;

    public NetworkImp(String configFilePath, MsgHandler handler) throws ConfigException {
        this.configFilePath = configFilePath;
        this.handler = handler;
        // default load ECDSA certificates
        this.configOption = Config.load(configFilePath, CryptoInterface.ECDSA_TYPE);
        connManager = new ConnectionManager(configOption, handler);
    }

    @Override
    public ConfigOption getConfigOption() {
        return configOption;
    }

    @Override
    public int getSslCryptoType() {
        return configOption.getCryptoMaterialConfig().getSslCryptoType();
    }

    @Override
    public void broadcast(Message out) {
        Map<String, ChannelHandlerContext> conns = connManager.getAvailableConnections();
        conns.forEach(
                (peer, ctx) -> {
                    ctx.writeAndFlush(out);
                    logger.trace("send message to  {} success ", peer);
                });
    }

    @Override
    public void sendToPeer(Message out, String peerIpPort) throws NetworkException {
        ChannelHandlerContext ctx = connManager.getConnectionCtx(peerIpPort);
        if (Objects.isNull(ctx)) {
            ctx.writeAndFlush(out);
            logger.trace("send message to  {} success ", peerIpPort);
        } else {
            logger.warn("send message to  {} failed ", peerIpPort);
            throw new NetworkException("Peer not available. Peer: " + peerIpPort);
        }
    }

    @Override
    public List<ConnectionInfo> getConnectionInfo() {
        return connManager.getConnectionInfoList();
    }

    @Override
    public void start() throws NetworkException {
        try {
            try {
                logger.debug("start connManager with ECDSA sslContext");
                connManager.startConnect(configOption);
                connManager.startReconnectSchedule();
                return;
            } catch (NetworkException e) {
                connManager.stopNetty();
                if (e.getErrorCode() == NetworkException.CONNECT_FAILED) {
                    throw e;
                }
                logger.debug(
                        "start connManager with the ECDSA sslContext failed, try to use SM sslContext, error info: {}",
                        e.getMessage());
            }
            // create a new connectionManager to connect the node with the SM sslContext
            connManager = new ConnectionManager(configOption, handler);
            configOption = Config.load(configFilePath, CryptoInterface.SM_TYPE);
            connManager.startConnect(configOption);
            connManager.startReconnectSchedule();
        } catch (ConfigException e) {
            throw new NetworkException(
                    "start connManager with the SM algorithm failed, error info: " + e.getMessage(),
                    e);
        }
    }

    @Override
    public Map<String, ChannelHandlerContext> getAvailableConnections() {
        return connManager.getAvailableConnections();
    }

    @Override
    public void removeConnection(String peerIpPort) {
        connManager.removeConnection(peerIpPort);
    }

    @Override
    public void setMsgHandleThreadPool(ExecutorService threadPool) {
        connManager.setMsgHandleThreadPool(threadPool);
    }

    @Override
    public void stop() {
        logger.debug("stop Network...");
        connManager.stopReconnectSchedule();
        connManager.stopNetty();
        return;
    }
}
