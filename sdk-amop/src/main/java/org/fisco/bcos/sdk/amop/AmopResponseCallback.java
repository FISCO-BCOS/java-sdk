package org.fisco.bcos.sdk.amop;

import io.netty.util.Timeout;

public abstract class AmopResponseCallback {
    private Timeout timeout;

    public Timeout getTimeout() {
        return timeout;
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public abstract void onResponse(AmopResponse response);
}
