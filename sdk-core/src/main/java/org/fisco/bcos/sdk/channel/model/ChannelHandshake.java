package org.fisco.bcos.sdk.channel.model;

public class ChannelHandshake {

    private String clientType = "java-sdk";

    public ChannelHandshake() {}

    public String getClientType() {
        return this.clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return "ChannelHandshake [clientType=" + this.clientType + "]";
    }
}
