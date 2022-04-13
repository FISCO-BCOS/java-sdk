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

import static org.fisco.bcos.sdk.model.CryptoProviderType.HSM;

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

    private CheckCertExistenceResult checkCertExistence(boolean isSM) throws NetworkException {
        CheckCertExistenceResult result = new CheckCertExistenceResult();
        result.setCheckPassed(true);

        String errorMessage = "[";
        if (configOption.getCryptoMaterialConfig().getCaInputStream() == null) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getCaCertPath() + ",";
        }
        if (configOption.getCryptoMaterialConfig().getSdkCertInputStream() == null) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage + configOption.getCryptoMaterialConfig().getSdkCertPath() + ",";
        }

        if (configOption.getCryptoMaterialConfig().getSdkPrivateKeyInputStream() == null) {
            result.setCheckPassed(false);
            errorMessage =
                    errorMessage
                            + configOption.getCryptoMaterialConfig().getSdkPrivateKeyPath()
                            + ",";
        }

        if (!isSM) {
            errorMessage = errorMessage + "]";
            result.setErrorMessage(errorMessage);
            return result;
        }
        if (!configOption.getCryptoMaterialConfig().getCryptoProvider().equalsIgnoreCase(HSM)) {
            if (configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyInputStream() == null) {
                errorMessage =
                        errorMessage
                                + configOption.getCryptoMaterialConfig().getEnSSLPrivateKeyPath()
                                + ",";
                result.setCheckPassed(false);
            }
            if (configOption.getCryptoMaterialConfig().getEnSSLCertInputStream() == null) {
                errorMessage =
                        errorMessage
                                + configOption.getCryptoMaterialConfig().getEnSSLCertPath()
                                + ",";
                result.setCheckPassed(false);
            }
        }
        errorMessage = errorMessage + "]";
        result.setErrorMessage(errorMessage);
        return result;
    }

    @Override
    public void start() throws NetworkException {
        CheckCertExistenceResult result = null;
        String ecdsaCryptoInfo = configOption.getCryptoMaterialConfig().toString();
        String tipsInformation = "\n* TRACE INFORMATION:\n----------------------------\n";
        try {
            try {
                tipsInformation += "====> STEP1: try to connect nodes with ecdsa context...\n";
                logger.info("{}", tipsInformation);
                result = checkCertExistence(false);

                if (result.isCheckPassed()) {
                    String message =
                            "<==== STEP1-1: Load certificates for ecdsa context success...";
                    tipsInformation += message + "\n";
                    logger.info("====> {}, start connManager with ECDSA sslContext", message);
                    connManager.startConnect(configOption);
                    connManager.startReconnectSchedule();
                    return;
                } else {
                    String errorMessage =
                            "<==== STEP1 Result: try to connect nodes with ecdsa context failed for cert missing\n* Missed certificates: "
                                    + result.getErrorMessage()
                                    + "\n";
                    errorMessage += "currentPath: " + new File("").getAbsolutePath() + "\n";
                    tipsInformation += errorMessage + "\n";
                    logger.warn(errorMessage);
                }
            } catch (NetworkException e) {
                if (e.getErrorCode() == NetworkException.CONNECT_FAILED) {
                    String errorMessage = "<==== connect nodes failed, reason:\n" + e.getMessage();
                    tipsInformation += errorMessage + "\n";
                    logger.warn("{}", errorMessage);
                    throw new NetworkException(tipsInformation);
                }
                // means that all the ECDSA certificates exist
                String errorMessage =
                        "<==== STEP1 Result: try to connect nodes with ecdsa context failed. reason:\n"
                                + e.getMessage();
                tipsInformation += errorMessage + "\n";
                logger.info("{}", errorMessage);
                configOption.reloadConfig(CryptoType.SM_TYPE);
                connManager.stopNetty();
            }
            String message = "----------------------------\n";
            message +=
                    "====> STEP2: connect nodes with ecdsa context failed, try to connect nodes with sm-context...";
            tipsInformation += message + "\n";
            logger.info("{}", message);
            configOption.reloadConfig(CryptoType.SM_TYPE);
            result = checkCertExistence(true);
            if (!result.isCheckPassed()) {
                message =
                        "<==== STEP2 Result: connect with sm context failed for cert missing.\n* Missed certificates: \n"
                                + result.getErrorMessage()
                                + "\n";
                message += "currentPath: " + new File("").getAbsolutePath() + "\n";
                message += "----------------------------\n";
                message +=
                        "<====> Error: try to connect nodes with both ecdsa and sm context failed <====>\n";
                message +=
                        "<====>\033[1;31m Please refer to github issue: "
                                + SystemInformation.connectionFaqIssueUrl
                                + " \033[0m\n";
                message +=
                        "<====>\033[1;31m Please refer to fisco-docs: "
                                + SystemInformation.connectionFaqDocUrl
                                + " \033[0m\n";
                message += "----------------------------\n";

                message += SystemInformation.getSystemInformation();
                tipsInformation += message + "\n";
                logger.warn("{}", message);
                throw new NetworkException(tipsInformation);
            }
            try {
                message = "<==== STEP2-1: Load certificates for sm context success...";
                tipsInformation += message + "\n";
                logger.info("{}", message);
                // create a new connectionManager to connect the node with the SM sslContext
                connManager = new ConnectionManager(configOption, handler);
                connManager.startConnect(configOption);
                connManager.startReconnectSchedule();
            } catch (Exception e) {
                message =
                        "<==== STEP2 Result: connect nodes with sm context failed for "
                                + e.getMessage()
                                + "\n";
                message += "----------------------------\n";
                message +=
                        "<====> Error: try to connect nodes with both ecdsa and sm context failed <====>\n";
                message +=
                        "<====>\033[1;31m Please refer to github issue: "
                                + SystemInformation.connectionFaqIssueUrl
                                + " \033[0m\n";
                message +=
                        "<====>\033[1;31m Please refer to fisco-docs: "
                                + SystemInformation.connectionFaqDocUrl
                                + " \033[0m\n";
                message += "----------------------------\n";

                message += SystemInformation.getSystemInformation();
                tipsInformation += message;
                logger.warn("{}, e: ", message, e);
                throw new NetworkException(tipsInformation);
            }
        } catch (ConfigException e) {
            throw new NetworkException(e);
        } catch (Exception e) {
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
