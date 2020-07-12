package org.fisco.bcos.sdk.eventsub;

import org.fisco.bcos.sdk.channel.Channel;

import java.util.List;

/**
 * Event subscribe interface
 * @author Maggie
 */
public interface EventSubscribe {
    /**
     * Create a Event Subscribe instance
     * @param ch
     * @param groupId
     * @return EventSubscribe Object
     */
    static EventSubscribe build(Channel ch, String groupId){
        // TODO
        return null;
    }

    /**
     * Subscribe event
     * @param params
     * @param callback
     */
    void subscribeEvent(EventLogParams params, EventCallback callback);

    /**
     * Unsubscribe events
     * @param filterId
     */
    void unsubscribeEvent(String filterId);

    /**
     * Get all subscribed event.
     * @return list of event log filters
     */
    List<EventLogFilter> getAllSubscribedEvent();

    /**
     * Start
     */
    void start();

    /**
     * Stop
     */
    void stop();
}
