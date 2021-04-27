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
import org.fisco.bcos.sdk.utils.SystemInformation;
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
        if (configOption.getCryptoMaterialConfig().getCaInputStream() == null) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getCaCertPath() + " ";
        }
        if (configOption.getCryptoMaterialConfig().getSdkCertInputStream() == null) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getSdkCertPath() + " ";
        }
        if (configOption.getCryptoMaterialConfig().getSdkPrivateKeyInputStream() == null) {
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
        if (configOption.getCryptoMaterialConfig().getEnSSLCertInputStream() == null) {
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getEnSSLCertPath() + " ";
            result.setCheckPassed(false);
        }
        if (configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyInputStream() == null) {
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
        String ecdsaCryptoInfo = configOption.getCryptoMaterialConfig().toString();
        try {
            try {
                result = checkCertExistence(false);
                if (result.isCheckPassed()) {
                    logger.debug("start connManager with ECDSA sslContext");
                    connManager.startConnect(configOption);
                    connManager.startReconnectSchedule();
                    return;
                } else {
                    logger.warn(
                            "Try to connect node with ECDSA sslContext failed, the tried NON-SM certPath: "
                                    + ecdsaCryptoInfo
                                    + ", currentPath: "
                                    + new File("").getAbsolutePath());
                }
            } catch (NetworkException e) {
                configOption.reloadConfig(CryptoType.SM_TYPE);
                if (e.getErrorCode() == NetworkException.CONNECT_FAILED) {
                    String errorMessage = e.getMessage();
                    errorMessage +=
                            "\n* If your blockchain is NON-SM, please provide the NON-SM certificates: "
                                    + ecdsaCryptoInfo
                                    + ".\n";
                    errorMessage +=
                            "\n* If your blockchain is SM, please provide the SM certificates: "
                                    + configOption.getCryptoMaterialConfig().toString()
                                    + "\n";
                    throw new NetworkException(
                            errorMessage + "\n" + SystemInformation.getSystemInformation());
                }
                // means that all the ECDSA certificates exist
                tryEcdsaConnect = true;
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
                    String errorMessage =
                            "\n* Try init the sslContext failed.\n\n* If your blockchain channel config is NON-SM, please provide the NON-SM certificates: "
                                    + ecdsaCryptoInfo
                                    + ".\n";
                    errorMessage +=
                            "\n* If your blockchain channel config is SM, please provide the missing certificates: "
                                    + result.getErrorMessage()
                                    + "\n";
                    throw new NetworkException(errorMessage);
                } else {
                    String errorMessage =
                            "\n# Not providing all the certificates to connect to the node! Please provide the certificates to connect with the block-chain.\n";
                    errorMessage +=
                            "\n* If your blockchain is NON-SM, please provide the NON-SM certificates: "
                                    + ecdsaCryptoInfo
                                    + ". \n";
                    errorMessage +=
                            "\n* If your blockchain is SM, please provide the SM certificates: "
                                    + configOption.getCryptoMaterialConfig().toString()
                                    + "\n";
                    throw new NetworkException(errorMessage);
                }
            }
            try {
                // create a new connectionManager to connect the node with the SM sslContext
                connManager = new ConnectionManager(configOption, handler);
                connManager.startConnect(configOption);
                connManager.startReconnectSchedule();
            } catch (NetworkException e) {
                String errorMessage = e.getMessage();
                errorMessage +=
                        "\n* If your blockchain channel config is NON-SM, please provide the NON-SM certificates: "
                                + ecdsaCryptoInfo
                                + ".\n";
                errorMessage +=
                        "\n* If your blockchain channel config is SM, please provide the SM certificates: "
                                + configOption.getCryptoMaterialConfig().toString()
                                + "\n";
                throw new NetworkException(
                        errorMessage + "\n" + SystemInformation.getSystemInformation());
            }
        } catch (ConfigException e) {
            throw new NetworkException(e);
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
    public ConnectionManager getConnManager() {
        return connManager;
    }

    @Override
    public void stop() {
        logger.debug("stop Network...");
        connManager.stopReconnectSchedule();
        connManager.stopNetty();
        return;
    }
}
