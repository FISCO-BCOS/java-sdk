package org.fisco.bcos.sdk.channel.model;

public enum EnumSocketChannelAttributeKey {
    CHANNEL_PROTOCOL_KEY("CHANNEL_PROTOCOL_KEY");

    private EnumSocketChannelAttributeKey(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
