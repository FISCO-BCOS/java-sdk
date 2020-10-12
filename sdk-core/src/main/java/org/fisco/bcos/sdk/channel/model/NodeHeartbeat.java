package org.fisco.bcos.sdk.channel.model;

public class NodeHeartbeat {
    public int heartBeat;

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int HeartBeat) {
        this.heartBeat = HeartBeat;
    }

    @Override
    public String toString() {
        return "NodeHeartbeat [heartBeat=" + heartBeat + "]";
    }
}
