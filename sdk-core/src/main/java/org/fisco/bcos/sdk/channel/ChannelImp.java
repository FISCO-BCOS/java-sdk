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

package org.fisco.bcos.sdk.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.channel.model.ChannelMessageError;
import org.fisco.bcos.sdk.channel.model.ChannelPrococolExceiption;
import org.fisco.bcos.sdk.channel.model.HeartBeatParser;
import org.fisco.bcos.sdk.channel.model.NodeHeartbeat;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.fisco.bcos.sdk.network.Network;
import org.fisco.bcos.sdk.network.NetworkException;
import org.fisco.bcos.sdk.network.NetworkImp;
import org.fisco.bcos.sdk.utils.ChannelUtils;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.utils.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of channel.
 *
 * @author chaychen
 */
public class ChannelImp implements Channel {

    private static Logger logger = LoggerFactory.getLogger(ChannelImp.class);
    private Integer connectSeconds = 30;
    private Integer connectSleepPerMillis = 30;
    private boolean running = false;

    private ChannelMsgHandler msgHandler;
    private Network network;
    private Map<String, List<String>> groupId2PeerIpPortList; // upper module settings are required
    private Timer timeoutHandler = new HashedWheelTimer();
    private long heartBeatDelay = (long) 2000;
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    public ChannelImp(ConfigOption configOption) throws ConfigException {
        msgHandler = new ChannelMsgHandler();
        network = new NetworkImp(configOption, msgHandler);
    }

    @Override
    public Network getNetwork() {
        return this.network;
    }

    @Override
    public void start() {
        try {
            if (running) {
                logger.warn("The channel has already been started!");
            }
            network.start();
            checkConnectionsToStartPeriodTask();
            running = true;
            logger.debug("Start the channel success");
        } catch (NetworkException e) {
            network.stop();
            logger.error("init channel network error, {} ", e.getMessage());
            throw new ChannelException("init channel network error: " + e.getMessage(), e);
        }
    }

    private void checkConnectionsToStartPeriodTask() {
        try {
            int sleepTime = 0;
            while (true) {
                if (getAvailablePeer().size() > 0 || sleepTime > connectSeconds * 1000) {
                    break;
                } else {
                    Thread.sleep(connectSleepPerMillis);
                    sleepTime += connectSleepPerMillis;
                }
            }

            List<String> peers = getAvailablePeer();
            String connectionInfoStr = "";
            for (String peer : peers) {
                connectionInfoStr += peer + ", ";
            }

            String baseMessage =
                    " nodes: "
                            + connectionInfoStr
                            + "java version: "
                            + System.getProperty("java.version")
                            + " ,java vendor: "
                            + System.getProperty("java.vm.vendor");

            if (getAvailablePeer().size() == 0) {
                String errorMessage = " Failed to connect to " + baseMessage;
                logger.error(errorMessage);
                throw new Exception(errorMessage);
            }

            logger.info(" Connect to " + baseMessage);

            startPeriodTask();
        } catch (InterruptedException e) {
            logger.warn(" thread interrupted exception: ", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error(" service init failed, error message: {}, error: ", e.getMessage(), e);
        }
    }

    private void startPeriodTask() {
        /** periodically send heartbeat message to all connected node, default period : 2s */
        scheduledExecutorService.scheduleAtFixedRate(
                () -> broadcastHeartbeat(), 0, heartBeatDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        if (!running) {
            logger.warn("The channel has already been stopped!");
        }
        logger.debug("stop channel...");
        timeoutHandler.stop();
        ThreadPoolService.stopThreadPool(scheduledExecutorService);
        network.stop();
        Thread.currentThread().interrupt();
        running = false;
        logger.debug("stop channel succ...");
    }

    @Override
    public void addConnectHandler(MsgHandler handler) {
        msgHandler.addConnectHandler(handler);
    }

    @Override
    public void addEstablishHandler(MsgHandler handler) {
        msgHandler.addEstablishHandler(handler);
    }

    @Override
    public void addMessageHandler(MsgType type, MsgHandler handler) {
        msgHandler.addMessageHandler(type, handler);
    }

    @Override
    public void addDisconnectHandler(MsgHandler handler) {
        msgHandler.addDisconnectHandler(handler);
    }

    public void setGroupId2PeerIpPortList(Map<String, List<String>> groupId2PeerIpPortList) {
        this.groupId2PeerIpPortList = groupId2PeerIpPortList;
    }

    @Override
    public void broadcastToGroup(Message out, String groupId) {
        List<String> peerIpPortList = groupId2PeerIpPortList.get(groupId);
        for (String peerIpPort : peerIpPortList) {
            if (msgHandler.getAvailablePeer().containsKey(peerIpPort)) {
                sendToPeer(out, peerIpPort);
            }
        }
    }

    @Override
    public void broadcast(Message out) {
        msgHandler
                .getAvailablePeer()
                .forEach(
                        (peer, ctx) -> {
                            ctx.writeAndFlush(out);
                            logger.trace("send message to {} success ", peer);
                        });
    }

    @Override
    public Response sendToPeer(Message out, String peerIpPort) {
        Options options = new Options();
        options.setTimeout(10000);
        return sendToPeerWithTimeOut(out, peerIpPort, options);
    }

    class Callback extends ResponseCallback {
        public transient Response retResponse;
        public transient Semaphore semaphore = new Semaphore(1, true);

        Callback() {
            try {
                semaphore.acquire(1);
            } catch (InterruptedException e) {
                logger.error("error :", e);
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void onTimeout() {
            super.onTimeout();
            semaphore.release();
        }

        @Override
        public void onResponse(Response response) {
            retResponse = response;
            if (retResponse != null && retResponse.getContent() != null) {
                logger.trace("response: {}", retResponse.getContent());
            } else {
                logger.error("response is null");
            }

            semaphore.release();
        }
    }

    public void waitResponse(Callback callback, Options options) {
        try {
            callback.semaphore.acquire(1);
        } catch (InterruptedException e) {
            logger.error("waitResponse exception, error info: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Response sendToPeerWithTimeOut(Message out, String peerIpPort, Options options) {
        Callback callback = new Callback();
        asyncSendToPeer(out, peerIpPort, callback, options);
        waitResponse(callback, options);
        return callback.retResponse;
    }

    @Override
    public Response sendToRandomWithTimeOut(Message out, Options options) {
        Callback callback = new Callback();
        asyncSendToRandom(out, callback, options);
        waitResponse(callback, options);
        return callback.retResponse;
    }

    @Override
    public Response sendToPeerByRuleWithTimeOut(Message out, PeerSelectRule rule, Options options) {
        Callback callback = new Callback();
        asyncSendToPeerByRule(out, rule, callback, options);
        waitResponse(callback, options);
        return callback.retResponse;
    }

    @Override
    public void asyncSendToPeer(
            Message out, String peerIpPort, ResponseCallback callback, Options options) {
        ChannelHandlerContext ctx = null;
        if (msgHandler.getAvailablePeer() != null) {
            ctx = msgHandler.getAvailablePeer().get(peerIpPort);
        }
        if (ctx != null) {
            if (callback == null) {
                ctx.writeAndFlush(out);
                return;
            }
            msgHandler.addSeq2CallBack(out.getSeq(), callback);
            if (options.getTimeout() > 0) {
                callback.setTimeout(
                        timeoutHandler.newTimeout(
                                new TimerTask() {
                                    @Override
                                    public void run(Timeout timeout) {
                                        // handle timer
                                        callback.onTimeout();
                                        msgHandler.removeSeq(out.getSeq());
                                    }
                                },
                                options.getTimeout(),
                                TimeUnit.MILLISECONDS));
            }
            ctx.writeAndFlush(out);
            logger.trace("send message {} to {} success ", out.getSeq(), peerIpPort);
        } else {
            logger.warn("send message with seq {} to {} failed ", out.getSeq(), peerIpPort);
            Response response = new Response();
            response.setErrorCode(ChannelMessageError.CONNECTION_INVALID.getError());
            String errorContent =
                    "Send message "
                            + peerIpPort
                            + " failed for connect failed, current available peers: "
                            + getAvailablePeer().toString();
            response.setErrorMessage(errorContent);
            response.setContent(errorContent);
            response.setMessageID(out.getSeq());
            if (callback != null) {
                callback.onResponse(response);
            }
        }
    }

    @Override
    public void asyncSendToRandom(Message out, ResponseCallback callback, Options options) {
        List<String> peerList = getAvailablePeer();
        int random = (int) (Math.random() * (peerList.size()));
        String peerIpPort = peerList.get(random);
        logger.trace("send message to random peer {} ", peerIpPort);
        asyncSendToPeer(out, peerIpPort, callback, options);
    }

    @Override
    public void asyncSendToPeerByRule(
            Message out, PeerSelectRule rule, ResponseCallback callback, Options options) {
        String target = rule.select(getConnectionInfo());
        asyncSendToPeer(out, target, callback, options);
    }

    @Override
    public List<ConnectionInfo> getConnectionInfo() {
        return network.getConnectionInfo();
    }

    @Override
    public List<String> getAvailablePeer() {
        List<String> peerList = new ArrayList<>();
        msgHandler
                .getAvailablePeer()
                .forEach(
                        (peer, ctx) -> {
                            peerList.add(peer);
                        });
        return peerList;
    }

    private void broadcastHeartbeat() {
        try {
            msgHandler
                    .getAvailablePeer()
                    .forEach(
                            (peer, ctx) -> {
                                sendHeartbeatMessage(ctx);
                                logger.trace("broadcastHeartbeat to {} success ", peer);
                            });
        } catch (Exception e) {
            logger.error("broadcastHeartbeat failed, error info: {}", e.getMessage());
        }
    }

    public void sendHeartbeatMessage(ChannelHandlerContext ctx) {
        String seq = ChannelUtils.newSeq();
        Message message = new Message();

        try {
            message.setSeq(seq);
            message.setResult(0);
            message.setType(Short.valueOf((short) MsgType.CLIENT_HEARTBEAT.getType()));
            HeartBeatParser heartBeatParser =
                    new HeartBeatParser(ChannelVersionNegotiation.getProtocolVersion(ctx));
            message.setData(heartBeatParser.encode("0"));
            logger.trace(
                    "encodeHeartbeatToMessage, seq: {}, content: {}, messageType: {}",
                    message.getSeq(),
                    heartBeatParser.toString(),
                    message.getType());
        } catch (JsonProcessingException e) {
            logger.error(
                    "sendHeartbeatMessage failed for decode the message exception, errorMessage: {}",
                    e.getMessage());
            return;
        }

        ResponseCallback callback =
                new ResponseCallback() {
                    @Override
                    public void onResponse(Response response) {
                        Boolean disconnect = true;
                        try {
                            if (response.getErrorCode() != 0) {
                                logger.error(
                                        " channel protocol heartbeat request failed, code: {}, message: {}",
                                        response.getErrorCode(),
                                        response.getErrorMessage());
                                throw new ChannelPrococolExceiption(
                                        " channel protocol heartbeat request failed, code: "
                                                + response.getErrorCode()
                                                + ", message: "
                                                + response.getErrorMessage());
                            }

                            NodeHeartbeat nodeHeartbeat =
                                    ObjectMapperFactory.getObjectMapper()
                                            .readValue(response.getContent(), NodeHeartbeat.class);
                            int heartBeat = nodeHeartbeat.getHeartBeat();
                            logger.trace(" heartbeat packet, heartbeat is {} ", heartBeat);
                            disconnect = false;
                        } catch (Exception e) {
                            logger.error(
                                    " channel protocol heartbeat failed, exception: {}",
                                    e.getMessage());
                        }
                        if (disconnect) {
                            String host = ChannelVersionNegotiation.getPeerHost(ctx);
                            network.removeConnection(host);
                        }
                    }
                };

        ctx.writeAndFlush(message);
        msgHandler.addSeq2CallBack(seq, callback);
    }

    @Override
    public void setThreadPool(ExecutorService threadPool) {
        network.setMsgHandleThreadPool(threadPool);
    }
}
