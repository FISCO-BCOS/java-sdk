package org.fisco.bcos.sdk.v3.client.protocol.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter implementation as per <a
 * href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_newfilter">docs</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Filter<T extends Filter> {

    private static int MAX_TOPICS_SIZE = 4;
    private T thisObj;
    private List<FilterTopic> topics;

    Filter() {
        thisObj = getThis();
        topics = new ArrayList<>();
    }

    Filter(List<FilterTopic> topics) {
        this.topics = topics;
    }

    public T addSingleTopic(String topic) {
        topics.add(new SingleTopic(topic));
        return getThis();
    }

    public T addNullTopic() {
        topics.add(new SingleTopic());
        return getThis();
    }

    // how to pass in null topic?
    public T addOptionalTopics(String... optionalTopics) {
        topics.add(new ListTopic(optionalTopics));
        return getThis();
    }

    public List<FilterTopic> getTopics() {
        return topics;
    }

    abstract T getThis();

    public interface FilterTopic<T> {
        @JsonValue
        T getValue();
    }

    public static class SingleTopic implements FilterTopic<String> {

        private String topic;

        public SingleTopic() {
            this.topic = null; // null topic
        }

        public SingleTopic(String topic) {
            this.topic = topic;
        }

        @Override
        public String getValue() {
            return topic;
        }
    }

    public static class ListTopic implements FilterTopic<List<SingleTopic>> {
        private List<SingleTopic> topics;

        public ListTopic(String... optionalTopics) {
            topics = new ArrayList<>();
            for (String topic : optionalTopics) {
                if (topic != null) {
                    topics.add(new SingleTopic(topic));
                } else {
                    topics.add(new SingleTopic());
                }
            }
        }

        public ListTopic(List<String> topics) {
            this.topics = new ArrayList<>();
            for (String topic : topics) {
                if (topic != null) {
                    this.topics.add(new SingleTopic(topic));
                } else {
                    this.topics.add(new SingleTopic());
                }
            }
        }

        @Override
        public List<SingleTopic> getValue() {
            return topics;
        }
    }

    public boolean checkParams() {
        return topics.size() <= MAX_TOPICS_SIZE;
    }
}
