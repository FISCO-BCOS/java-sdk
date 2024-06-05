package org.fisco.bcos.sdk.v3.filter;

import java.util.function.Consumer;

public class Subscription<T> {
    private Publisher<T> publisher;
    private Consumer<T> consumer;

    public Subscription(Publisher<T> publisher, Consumer<T> consumer) {
        this.publisher = publisher;
        this.consumer = consumer;
    }

    public void unsubscribe() {
        publisher.unsubscribe(consumer);
    }
}
