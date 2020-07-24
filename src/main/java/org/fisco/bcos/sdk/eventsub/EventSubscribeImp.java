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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.fisco.bcos.sdk.channel.Channel;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.eventsub.filter.EventLogFilter;
import org.fisco.bcos.sdk.eventsub.filter.EventLogFilterStatus;
import org.fisco.bcos.sdk.eventsub.filter.EventLogResponse;
import org.fisco.bcos.sdk.eventsub.filter.EventPushMsgHandler;
import org.fisco.bcos.sdk.eventsub.filter.EventSubNodeRespStatus;
import org.fisco.bcos.sdk.eventsub.filter.FilterManager;
import org.fisco.bcos.sdk.eventsub.filter.ScheduleTimeConfig;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.model.Response;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSubscribeImp implements EventSubscribe {
    private static final Logger logger = LoggerFactory.getLogger(EventSubscribeImp.class);
    private Channel ch;
    private String groupId;
    private FilterManager filterManager;
    private EventPushMsgHandler msgHander;
    private boolean running = false;
    ScheduledThreadPoolExecutor resendSchedule = new ScheduledThreadPoolExecutor(1);

    public EventSubscribeImp(Channel ch, String groupId) {
        this.ch = ch;
        this.groupId = groupId;
        filterManager = new FilterManager();
        msgHander = new EventPushMsgHandler(filterManager);
        ch.addMessageHandler(MsgType.EVENT_LOG_PUSH, msgHander);
    }

    @Override
    public void subscribeEvent(EventLogParams params, EventCallback callback) {
        if (!params.valid()) {
            callback.onReceiveLog(EventSubNodeRespStatus.INVALID_PARAMS.getStatus(), null);
            return;
        }
        EventLogFilter filter = new EventLogFilter();
        filter.setRegisterID(EventSubscribe.newSeq());
        filter.setParams(params);
        filter.setCallback(callback);
        filterManager.addFilter(filter);
        sendFilter(filter);
    }

    @Override
    public void unsubscribeEvent(String filterID) {
        // Todo need node support
    }

    @Override
    public List<EventLogFilter> getAllSubscribedEvent() {
        return filterManager.getAllSubscribedEvent();
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        running = true;
        resendSchedule.scheduleAtFixedRate(
                () -> {
                    resendWaitingFilters();
                },
                0,
                ScheduleTimeConfig.resendFrequency,
                TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        resendSchedule.shutdown();
    }

    private void resendWaitingFilters() {
        List<EventLogFilter> filters = filterManager.getWaitingReqFilters();
        for (EventLogFilter filter : filters) {
            sendFilter(filter);
        }
        logger.info("Resend waiting filters, size: {}", filters.size());
    }

    private void sendFilter(EventLogFilter filter) {
        Message msg = new Message();
        msg.setSeq(EventSubscribe.newSeq());
        msg.setType((short) MsgType.CLIENT_REGISTER_EVENT_LOG.ordinal());
        msg.setResult(0);
        try {
            String content = filter.getNewParamJsonString(groupId);
            msg.setData(content.getBytes());
        } catch (JsonProcessingException e) {
            logger.error(
                    "send filter error, registerID: {},filterID : {}, error: {}",
                    filter.getRegisterID(),
                    filter.getFilterID(),
                    e.getMessage());
            logger.error(
                    "remove bad filter , registerID: {},filterID : {}",
                    filter.getRegisterID(),
                    filter.getFilterID());
            filterManager.removeFilter(filter.getRegisterID());
        }

        filterManager.addCallback(filter.getFilterID(), filter.getCallback());

        // Todo check send to group function. this function may not in channel module.
        ch.asyncSendToGroup(
                msg,
                groupId,
                new RegisterEventSubRespCallback(
                        filterManager, filter, filter.getFilterID(), filter.getRegisterID()));
    }

    class RegisterEventSubRespCallback extends ResponseCallback {
        FilterManager filterManager;
        EventLogFilter filter;
        String filterID;
        String registerID;

        public RegisterEventSubRespCallback(
                FilterManager filterManager,
                EventLogFilter filter,
                String filterID,
                String registerID) {
            this.filterManager = filterManager;
            this.filter = filter;
            this.filterID = filterID;
            this.registerID = registerID;
        }

        @Override
        public void onResponse(Response response) {
            logger.info(
                    " event filter callback response, registerID: {}, filterID: {}, seq: {}, error code: {},  content: {}",
                    registerID,
                    filterID,
                    response.getMessageID(),
                    response.getErrorCode(),
                    response.getContent());
            try {
                if (0 == response.getErrorCode()) {
                    EventLogResponse resp =
                            ObjectMapperFactory.getObjectMapper()
                                    .readValue(response.getContent(), EventLogResponse.class);
                    if (resp.getResult() == 0) {
                        // node give an "OK" response, event log will be pushed soon
                        filterManager.updateFilterStatus(
                                filter, EventLogFilterStatus.EVENT_LOG_PUSHING, null);
                    } else {
                        // node give a bad response, will not push event log, trigger callback
                        filter.getCallback().onReceiveLog(resp.getResult(), null);
                        filterManager.removeFilter(registerID);
                        filterManager.removeCallback(filterID);
                    }
                } else {
                    filterManager.updateFilterStatus(
                            filter, EventLogFilterStatus.WAITING_REQUEST, null);
                    filterManager.removeCallback(filterID);
                }
            } catch (Exception e) {
                logger.error(
                        " event filter response message exception, filterID: {}, registerID: {}, exception message: {}",
                        filterID,
                        registerID,
                        e.getMessage());
                filter.getCallback()
                        .onReceiveLog(EventSubNodeRespStatus.OTHER_ERROR.getStatus(), null);
                filterManager.removeFilter(registerID);
                filterManager.removeCallback(filterID);
            }
        }
    }
}
