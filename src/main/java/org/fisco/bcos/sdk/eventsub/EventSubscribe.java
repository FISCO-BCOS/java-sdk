/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.eventsub;

import java.util.List;
import org.fisco.bcos.sdk.channel.Channel;

/**
 * Event subscribe interface.
 *
 * @author Maggie
 */
public interface EventSubscribe {
    /**
     * Create a Event Subscribe instance
     *
     * @param ch
     * @param groupId
     * @return EventSubscribe Object
     */
    static EventSubscribe build(Channel ch, String groupId) {
        return null;
    }

    /**
     * Subscribe event
     *
     * @param params
     * @param callback
     */
    void subscribeEvent(EventLogParams params, EventCallback callback);

    /**
     * Unsubscribe events
     *
     * @param filterId
     */
    void unsubscribeEvent(String filterId);

    /**
     * Get all subscribed event.
     *
     * @return list of event log filters
     */
    List<EventLogFilter> getAllSubscribedEvent();

    /** Start */
    void start();

    /** Stop */
    void stop();
}
