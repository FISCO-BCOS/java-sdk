package org.fisco.bcos.sdk.channel.model;

public class Options {
    public long timeout = 0;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
