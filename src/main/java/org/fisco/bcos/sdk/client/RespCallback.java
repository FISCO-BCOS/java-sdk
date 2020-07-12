package org.fisco.bcos.sdk.client;

/**
 * Callback function to executed when client get response from the node.
 * @author Maggie
 * @param <T> for the response data structures in package client/response
 */
public interface RespCallback<T> {
    /**
     * onResponse is the call back function
     * @param t the response data structure
     */
    void onResponse(T t);
}
