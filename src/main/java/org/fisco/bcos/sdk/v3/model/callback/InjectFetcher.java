package org.fisco.bcos.sdk.v3.model.callback;

public interface InjectFetcher<T> {
    T onFetch();
}
