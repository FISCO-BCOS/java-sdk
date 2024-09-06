package org.fisco.bcos.sdk.v3.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Publisher<T> {
    private List<Consumer<T>> subscribers = new ArrayList<>();

    public Subscription<T> subscribe(Consumer<T> subscriber) {
        subscribers.add(subscriber);
        return new Subscription<>(this, subscriber);
    }

    public void unsubscribe(Consumer<T> subscriber) {
        subscribers.remove(subscriber);
    }

    public void publish(T event) {
        for (Consumer<T> subscriber : subscribers) {
            subscriber.accept(event);
        }
    }
}
