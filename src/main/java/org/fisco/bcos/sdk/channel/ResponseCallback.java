package org.fisco.bcos.sdk.channel;


import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.Response;

/**
 * ResponseCallback is to define a callback to handle response from node
 */
public abstract class ResponseCallback {
    private Message message;

    /**
     * OnResponse
     * @param response
     */
    public abstract void onResponse(Response response);
}
