package org.fisco.bcos.sdk.channel.model;

public enum EnumSocketChannelAttributeKey {
    CHANNEL_PROTOCOL_KEY("CHANNEL_PROTOCOL_KEY");

    private String key;

    EnumSocketChannelAttributeKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
