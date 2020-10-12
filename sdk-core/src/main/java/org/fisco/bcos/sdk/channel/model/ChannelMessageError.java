package org.fisco.bcos.sdk.channel.model;

public enum ChannelMessageError {
    NODES_UNREACHABLE(99), // node unreachable
    MESSAGE_SEND_EXCEPTION(100), // send failed after N times retry
    MESSAGE_TIMEOUT(102), // timeout
    REJECT_AMOP_REQ_FOR_OVER_BANDWIDTHLIMIT(
            103), // the AMOP_requests or the AMOP_multicast_requests have been rejected due to over
    // bandwidth limit
    MESSAGE_DECODE_ERROR(105), // decode error
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
