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

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.channel.model.EnumChannelProtocolVersion;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigException;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of channel.
 *
 * @author chaychen
 */
public class ChannelImp implements Channel {

    private static Logger logger = LoggerFactory.getLogger(ChannelImp.class);

    private ChannelMsgHandler msgHandler;
    private Network network;
    private Map<String, List<String>> groupId2PeerIpPortList; // upper module settings are required
    private Timer timeoutHandler = new HashedWheelTimer();

    public ChannelImp(String filepath) {
        try {
            ConfigOption config = Config.load(filepath);
            msgHandler = new ChannelMsgHandler();
            network = new NetworkImp(config, msgHandler);
            network.start();
        } catch (ConfigException e) {
            logger.error("init channel config error, {} ", e.getMessage());
        } catch (NetworkException e) {
            logger.error("init channel network error, {} ", e.getMessage());
        }
    }

    @Override
    public void addConnectHandler(MsgHandler handler) {
        msgHandler.addConnectHandler(handler);
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
                            logger.debug("send message to {} success ", peer);
                        });
    }

    @Override
    public Response sendToPeer(Message out, String peerIpPort) {
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
            public void onResponse(Response response) {
                retResponse = response;

                if (retResponse != null && retResponse.getContent() != null) {
                    logger.debug("response: {}", retResponse.getContent());
                } else {
                    logger.error("response is null");
                }

                semaphore.release();
            }
        }

        Callback callback = new Callback();
        asyncSendToPeer(out, peerIpPort, callback, new Options());
        try {
            callback.semaphore.acquire(1);
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }

        return callback.retResponse;
    }

    @Override
    public Response sendToRandom(Message out) {
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
            public void onResponse(Response response) {
                retResponse = response;

                if (retResponse != null && retResponse.getContent() != null) {
                    logger.debug("response: {}", retResponse.getContent());
                } else {
                    logger.error("response is null");
                }

                semaphore.release();
            }
        }

        Callback callback = new Callback();
        asyncSendToRandom(out, callback, new Options());
        try {
            callback.semaphore.acquire(1);
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }

        return callback.retResponse;
    }

    @Override
    public Response sendToPeerByRule(Message out, PeerSelectRule rule) {
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
            public void onResponse(Response response) {
                retResponse = response;

                if (retResponse != null && retResponse.getContent() != null) {
                    logger.debug("response: {}", retResponse.getContent());
                } else {
                    logger.error("response is null");
                }

                semaphore.release();
            }
        }

        Callback callback = new Callback();
        asyncSendToPeerByRule(out, rule, callback, new Options());
        try {
            callback.semaphore.acquire(1);
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }

        return callback.retResponse;
    }

    @Override
    public void asyncSendToPeer(
            Message out, String peerIpPort, ResponseCallback callback, Options options) {
        msgHandler.addSeq2CallBack(out.getSeq(), callback);
        ChannelHandlerContext ctx = msgHandler.getAvailablePeer().get(peerIpPort);
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
        if (ctx != null) {
            ctx.writeAndFlush(out);
            logger.debug("send message to {} success ", peerIpPort);
        } else {
            logger.debug("send message to {} failed ", peerIpPort);
        }
    }

    @Override
    public void asyncSendToRandom(Message out, ResponseCallback callback, Options options) {
        List<String> peerList = getAvailablePeer();
        int random = (int) (Math.random() * (peerList.size()));
        String peerIpPort = peerList.get(random);
        logger.debug("send message to random peer {} ", peerIpPort);
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

    @Override
    public EnumChannelProtocolVersion getVersion() {
        return null;
    }
}
