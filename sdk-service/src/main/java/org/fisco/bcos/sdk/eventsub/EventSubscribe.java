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
import java.util.UUID;
import org.fisco.bcos.sdk.eventsub.filter.EventLogFilter;
import org.fisco.bcos.sdk.service.GroupManagerService;

/**
 * Event subscribe interface.
 *
 * @author Maggie
 */
public interface EventSubscribe {
    /**
     * Create a Event Subscraibe instance
     *
     * @param groupManagerService the groupManager Service instance
     * @param groupId the id of group
     * @param eventResource the eventResource instance
     * @return EventSubscribe Object
     */
    static EventSubscribe build(
            GroupManagerService groupManagerService, EventResource eventResource, Integer groupId) {
        return new EventSubscribeImp(groupManagerService, eventResource, groupId);
    }

    static String newSeq() {
        String seq = UUID.randomUUID().toString().replaceAll("-", "");
        return seq;
    }

    /**
     * Subscribe event
     *
     * @param params the EventLogParams instance
     * @param callback the EventCallback instance
     * @return registerId of event
     */
    String subscribeEvent(EventLogParams params, EventCallback callback);

    /**
     * Unsubscribe events
     *
     * @param registerID the ID of register
     * @param callback the EventCallback instance
     */
    void unsubscribeEvent(String registerID, EventCallback callback);

    /**
     * Get all subscribed event.
     *
     * @return list of event log filters
     */
    List<EventLogFilter> getAllSubscribedEvent();

    /**
     * Get EventPushMsgHandler and FilterManager.
     *
     * @return EventResource
     */
    EventResource getEventResource();

    /** Start */
    void start();

    /** Stop */
    void stop();
}
