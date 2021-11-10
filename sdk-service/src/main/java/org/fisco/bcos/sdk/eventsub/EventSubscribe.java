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

import java.util.Set;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.ConfigOption;

/**
 * Event subscribe interface.
 *
 * @author Maggie
 */
public interface EventSubscribe {
    /**
     * Create Event subscribe instance
     *
     * @param group group
     * @return EventSubscribe Object
     */
    static EventSubscribe build(String group, ConfigOption configOption) {
        return new EventSubscribeImp(group, configOption);
    }

    /**
     * Create Event subscribe instance
     *
     * @param client Client
     * @return EventSubscribe Object
     */
    static EventSubscribe build(Client client) {
        return new EventSubscribeImp(client.getGroup(), client.getConfigOption());
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
     * @param id the ID of event subscribe task
     * @param callback the EventCallback instance
     */
    void unsubscribeEvent(String id, EventCallback callback);

    /** @return */
    Set<String> getAllSubscribedEvents();

    /** Start */
    void start();

    /** Stop */
    void stop();
}
