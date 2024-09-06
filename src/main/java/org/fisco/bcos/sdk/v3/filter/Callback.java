package org.fisco.bcos.sdk.v3.filter;

/** Filter callback interface. */
public interface Callback<T> {
    void onEvent(T value);
}
