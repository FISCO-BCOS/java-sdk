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

package org.fisco.bcos.sdk.eventsub.filter;

import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter manager is to maintain a EventLogFilter list, as well as a EventCallback list. Include
 * add, remove, update operations to the two list and filters.
 */
public class FilterManager {
    private static final Logger logger = LoggerFactory.getLogger(FilterManager.class);

    /**
     * Register id of a filter is generated when register a subscribe. Filter id of a filter is
     * generated when send one subscribe request to node.
     */
    private Map<String, EventLogFilter> regId2Filter =
            new ConcurrentHashMap<String, EventLogFilter>();

    private Map<String, EventCallback> filterID2Callback =
            new ConcurrentHashMap<String, EventCallback>();

    public List<EventLogFilter> getAllSubscribedEvent() {
        List<EventLogFilter> list = new ArrayList<>();
        regId2Filter.forEach(
                (regId, filter) -> {
                    list.add(filter);
                });
        return list;
    }

    public EventLogFilter getFilterById(String filterId) {
        for (EventLogFilter filter : regId2Filter.values()) {
            if (filter.getFilterID().equals(filterId)) {
                return filter;
            }
        }
        return null;
    }

    public void addFilter(EventLogFilter filter) {
        regId2Filter.put(filter.getRegisterID(), filter);
        logger.info(
                "add event log filter , registerID: {}, filter: {}",
                filter.getRegisterID(),
                filter);
    }

    public EventLogFilter getFilter(String registerId) {
        return regId2Filter.get(registerId);
    }

    public void removeFilter(String registerId) {
        logger.info("remove filter, registerID: {}", registerId);
        regId2Filter.remove(registerId);
    }

    public void addCallback(String filterID, EventCallback callback) {
        filterID2Callback.put(filterID, callback);
    }

    public void removeCallback(String filterID) {
        filterID2Callback.remove(filterID);
    }

    public void updateFilterStatus(
            EventLogFilter filter, EventLogFilterStatus status, ChannelHandlerContext ctx) {
        synchronized (this) {
            filter.setStatus(status);
            filter.setCtx(ctx);
        }
    }

    public EventCallback getCallBack(String filterID) {
        return filterID2Callback.get(filterID);
    }

    public List<EventLogFilter> getWaitingReqFilters() {
        List<EventLogFilter> filters = new ArrayList<EventLogFilter>();
        synchronized (this) {
            for (EventLogFilter filter : regId2Filter.values()) {
                logger.trace(
                        " filter in list, id:{}, status:{}",
                        filter.getFilterID(),
                        filter.getStatus());
                if (filter.getStatus() == EventLogFilterStatus.WAITING_REQUEST) {
                    logger.info(
                            " resend filter, update event filter status: {}, registerID: {}, filter: {}",
                            filter.getStatus(),
                            filter.getRegisterID(),
                            filter);
                    filters.add(filter);
                    filter.setStatus(EventLogFilterStatus.WAITING_RESPONSE);
                }
            }
        }
        return filters;
    }

    // update event filter status when socket disconnect
    public void updateEventLogFilterStatus(ChannelHandlerContext ctx) {
        synchronized (this) {
            for (EventLogFilter filter : regId2Filter.values()) {
                if (filter.getCtx() == ctx) {
                    filter.setCtx(null);
                    filter.setStatus(EventLogFilterStatus.WAITING_REQUEST);
                    removeCallback(filter.getFilterID());

                    logger.info(
                            " disconnect, update event filter status, ctx: {}, status: {}, registerID: {}, filterID: {}, filter: {}",
                            System.identityHashCode(ctx),
                            filter.getStatus(),
                            filter.getFilterID(),
                            filter.getRegisterID(),
                            filter);
                }
            }
        }
    }
}
