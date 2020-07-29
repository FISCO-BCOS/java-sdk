package org.fisco.bcos.sdk.channel.model;

public enum ChannelMessageError {
    MESSAGE_TIMEOUT(102), // timeout
    INTERNAL_MESSAGE_HANDLE_FAILED(-5000),
    CONNECTION_INVALID(-5001);

    private int error;

    private ChannelMessageError(int error) {
        this.setError(error);
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
