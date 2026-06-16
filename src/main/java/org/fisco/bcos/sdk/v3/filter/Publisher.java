package org.fisco.bcos.sdk.v3.filter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Publisher<T> {
    // CopyOnWriteArrayList: subscribe/unsubscribe run on caller threads while publish() iterates on
    // the poll thread; a plain ArrayList here throws ConcurrentModificationException and kills
    // polling.
    private final List<Consumer<T>> subscribers = new CopyOnWriteArrayList<>();

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
