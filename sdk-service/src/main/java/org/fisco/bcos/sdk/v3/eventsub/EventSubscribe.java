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

package org.fisco.bcos.sdk.v3.eventsub;

import java.util.Set;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;

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
     * @param configOption configOption
     * @return EventSubscribe Object
     */
    static EventSubscribe build(String group, ConfigOption configOption) throws JniException {
        Client client = Client.build(group, configOption);
        return new EventSubscribeImp(client, configOption);
    }

    /**
     * Create Event subscribe instance
     *
     * @param client Client
     * @return EventSubscribe Object
     */
    static EventSubscribe build(Client client) throws JniException {
        return new EventSubscribeImp(client, client.getConfigOption());
    }

    /**
     * Subscribe event
     *
     * @param params the EventLogParams instance
     * @param callback the EventCallback instance
     * @return registerId of event
     */
    String subscribeEvent(EventSubParams params, EventSubCallback callback);

    /**
     * Unsubscribe events
     *
     * @param eventSubId the ID of event subscribe task
     */
    void unsubscribeEvent(String eventSubId);

    /**
     * get all events subscribed by clients
     *
     * @return event set
     */
    Set<String> getAllSubscribedEvents();

    /** Start */
    void start();

    /** Stop */
    void stop();

    void destroy();
}
