package org.fisco.bcos.sdk.v3.eventsub;

import java.util.List;

public class EventLogAddrAndTopics {
    String address;
    List<String> topics;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
