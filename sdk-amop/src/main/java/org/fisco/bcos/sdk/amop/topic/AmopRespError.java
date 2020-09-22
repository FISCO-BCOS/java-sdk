package org.fisco.bcos.sdk.amop.topic;

public enum AmopRespError {
    /** Error code from node. */
    // nodes unreachable
    NODES_UNREACHABLE(99),
    // send failed after N times retry
    MESSAGE_SEND_EXCEPTION(100),
    // timeout
    MESSAGE_TIMEOUT(102),
    // no available session
    NO_AVAILABLE_SESSION(104),
    // decode error
    MESSAGE_DECODE_ERROR(105),
    // the AMOP_requests or the AMOP_multicast_requests have been rejected due to over bandwidth
    // limit
    REJECT_AMOP_REQ_FOR_OVER_BANDWIDTHLIMIT(103);

    private int error;

    private AmopRespError(int error) {
        this.setError(error);
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
