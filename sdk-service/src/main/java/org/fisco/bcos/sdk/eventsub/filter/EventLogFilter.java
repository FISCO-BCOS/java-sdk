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

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.eventsub.EventLogParams;
import org.fisco.bcos.sdk.eventsub.EventSubscribe;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;

/** An event log filter is a subscription. */
public class EventLogFilter {
    private String registerID;
    private String filterID;
    private EventLogParams params;
    private EventCallback callback;
    private EventLogFilterStatus status = EventLogFilterStatus.WAITING_REQUEST;
    private ChannelHandlerContext ctx = null;

    private BigInteger lastBlockNumber = null;
    private long logCount = 0;

    public String getNewParamJsonString(String groupId) throws JsonProcessingException {
        String newFilterId = EventSubscribe.newSeq();
        EventLogRequestParams requestParams =
                new EventLogRequestParams(generateNewParams(), groupId, newFilterId);
        filterID = newFilterId;
        String content = requestParams.toJsonString();
        return content;
    }

    public String getParamJsonString(String groupId, String filterId)
            throws JsonProcessingException {
        EventLogRequestParams requestParams =
                new EventLogRequestParams(generateNewParams(), groupId, filterId);
        String content = requestParams.toJsonString();
        return content;
    }

    public class EventLogRequestParams extends EventLogParams {
        private String groupID;
        private String filterID;
        private int timeout = 0;

        public EventLogRequestParams(EventLogParams params, String groupID, String filterID) {
            this.setFromBlock(params.getFromBlock());
            this.setToBlock(params.getToBlock());
            this.setAddresses(params.getAddresses());
            this.setTopics(params.getTopics());
            this.setGroupID(groupID);
            this.setFilterID(filterID);
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }

        public void setFilterID(String filterID) {
            this.filterID = filterID;
        }

        public String getGroupID() {
            return this.groupID;
        }

        public String getFilterID() {
            return this.filterID;
        }

        public String toJsonString() throws JsonProcessingException {
            String content = ObjectMapperFactory.getObjectMapper().writeValueAsString(this);
            return content;
        }
    }

    private EventLogParams generateNewParams() {
        EventLogParams params = new EventLogParams();
        params.setToBlock(getParams().getToBlock());
        params.setAddresses(getParams().getAddresses());
        params.setTopics(getParams().getTopics());
        if (lastBlockNumber == null) {
            params.setFromBlock(params.getFromBlock());
        } else {
            params.setFromBlock(lastBlockNumber.toString());
        }
        return params;
    }

    public void updateCountsAndLatestBlock(List<EventLog> logs) {
        if (logs.isEmpty()) {
            return;
        }
        EventLog latestOne = logs.get(logs.size() - 1);
        if (lastBlockNumber == null) {
            lastBlockNumber = latestOne.getBlockNumber();
            logCount += logs.size();
        } else {
            if (latestOne.getBlockNumber().compareTo(lastBlockNumber) > 0) {
                lastBlockNumber = latestOne.getBlockNumber();
                logCount += logs.size();
            }
        }
    }

    public String getRegisterID() {
        return registerID;
    }

    public void setRegisterID(String registerID) {
        this.registerID = registerID;
    }

    public EventLogParams getParams() {
        return params;
    }

    public void setParams(EventLogParams params) {
        this.params = params;
    }

    public EventLogFilterStatus getStatus() {
        return status;
    }

    public void setStatus(EventLogFilterStatus status) {
        this.status = status;
    }

    public EventCallback getCallback() {
        return callback;
    }

    public void setCallback(EventCallback callback) {
        this.callback = callback;
    }

    public String getFilterID() {
        return filterID;
    }

    public void setFilterID(String filterID) {
        this.filterID = filterID;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
