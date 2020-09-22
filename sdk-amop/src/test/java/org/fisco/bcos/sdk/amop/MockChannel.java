package org.fisco.bcos.sdk.amop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.PeerSelectRule;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.channel.model.Options;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.network.ConnectionInfo;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.fisco.bcos.sdk.network.Network;

public class MockChannel implements Channel {

    private Message msg;
    private ResponseCallback callback;

    public Message getMsg() {
        return msg;
    }

    @Override
    public Network getNetwork() {
        return null;
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void stop() {
        // do nothing
    }

    @Override
    public void addMessageHandler(MsgType type, MsgHandler handler) {
        // do nothing
    }

    @Override
    public void addConnectHandler(MsgHandler handler) {
        // do nothing
    }

    @Override
    public void addEstablishHandler(MsgHandler handler) {
        // do nothing
    }

    @Override
    public void addDisconnectHandler(MsgHandler handler) {
        // do nothing
    }

    @Override
    public void broadcastToGroup(Message out, String groupId) {
        // do nothing
    }

    @Override
    public void broadcast(Message out) {}

    @Override
    public Response sendToPeer(Message out, String peerIpPort) {
        return null;
    }

    @Override
    public Response sendToPeerWithTimeOut(Message out, String peerIpPort, Options options) {
        return null;
    }

    @Override
    public Response sendToRandomWithTimeOut(Message out, Options options) {
        return null;
    }

    @Override
    public Response sendToPeerByRuleWithTimeOut(Message out, PeerSelectRule rule, Options options) {
        return null;
    }

    @Override
    public void asyncSendToPeer(
            Message out, String peerIpPort, ResponseCallback callback, Options options) {
        // do nothing
    }

    @Override
    public void asyncSendToRandom(Message out, ResponseCallback callback, Options options) {
        msg = out;
        this.callback = callback;
    }

    @Override
    public void asyncSendToPeerByRule(
            Message out, PeerSelectRule rule, ResponseCallback callback, Options options) {
        // do nothing
    }

    @Override
    public List<ConnectionInfo> getConnectionInfo() {
        return null;
    }

    @Override
    public List<String> getAvailablePeer() {
        List<String> list = new ArrayList<>();
        return list;
    }

    @Override
    public void setThreadPool(ExecutorService threadPool) {}

    public ResponseCallback getCallback() {
        return callback;
    }
}
