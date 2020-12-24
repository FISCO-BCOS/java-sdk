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
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.model.CryptoType;
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
    private MsgHandler handler;

    public NetworkImp(ConfigOption configOption, MsgHandler handler) throws ConfigException {
        this.configOption = configOption;
        this.handler = handler;
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

    private class CheckCertExistenceResult {
        private boolean checkPassed = true;
        private String errorMessage = "";

        public boolean isCheckPassed() {
            return checkPassed;
        }

        public void setCheckPassed(boolean checkPassed) {
            this.checkPassed = checkPassed;
        }

        public String getErrorMessage() {
            return errorMessage;
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
        if (!new File(configOption.getCryptoMaterialConfig().getCaCertPath()).exists()) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getCaCertPath() + " ";
        }
        if (!new File(configOption.getCryptoMaterialConfig().getSdkCertPath()).exists()) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getSdkCertPath() + " ";
        }
        if (!new File(configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()).exists()) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage
                            + configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()
                            + " ";
        }
        if (!isSM) {
            errorMessage = errorMessage + "exists!";
            result.setErrorMessage(errorMessage);
            return result;
        }
        if (!new File(configOption.getCryptoMaterialConfig().getEnSSLCertPath()).exists()) {
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getEnSSLCertPath() + " ";
            result.setCheckPassed(false);
        }
        if (!new File(configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyPath()).exists()) {
            errorMessage =
                    errorMessage
                            + configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyPath()
                            + " ";
            result.setCheckPassed(false);
        }
        errorMessage = errorMessage + "exist!";
        result.setErrorMessage(errorMessage);
        return result;
    }

    @Override
    public void start() throws NetworkException {
        boolean tryEcdsaConnect = false;
        CheckCertExistenceResult result = null;
        try {
            try {
                result = checkCertExistence(false);
                if (result.isCheckPassed()) {
                    logger.debug("start connManager with ECDSA sslContext");
                    connManager.startConnect(configOption);
                    connManager.startReconnectSchedule();
                    tryEcdsaConnect = true;
                    return;
                } else {
                    logger.warn(
                            "Try to connect node with ECDSA sslContext failed, expected certPath: "
                                    + configOption.getCryptoMaterialConfig().toString()
                                    + ", currentPath: "
                                    + new File("").getAbsolutePath());
                }
            } catch (NetworkException e) {
                tryEcdsaConnect = true;
                configOption.reloadConfig(CryptoType.SM_TYPE);
                result = checkCertExistence(true);
                if (e.getErrorCode() == NetworkException.CONNECT_FAILED
                        || !result.isCheckPassed()) {
                    throw e;
                }
                connManager.stopNetty();
                logger.debug(
                        "start connManager with the ECDSA sslContext failed, try to use SM sslContext, error info: {}",
                        e.getMessage());
            }
            logger.debug("start connManager with SM sslContext");
            configOption.reloadConfig(CryptoType.SM_TYPE);
            result = checkCertExistence(true);
            if (!result.isCheckPassed()) {
                if (tryEcdsaConnect) {
                    throw new NetworkException("Certificate not exist:" + result.getErrorMessage());
                } else {
                    throw new NetworkException(
                            "Not providing all the certificates to connect to the node! Please provide the certificates to connect with the block-chain, expected certPath: ["
                                    + configOption.getCryptoMaterialConfig().toString()
                                    + "]");
                }
            }
            if (tryEcdsaConnect) {
                // create a new connectionManager to connect the node with the SM sslContext
                connManager = new ConnectionManager(configOption, handler);
            }
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
