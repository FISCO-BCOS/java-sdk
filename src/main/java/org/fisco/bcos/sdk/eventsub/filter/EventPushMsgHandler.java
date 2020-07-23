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
import java.util.List;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.EventLog;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.network.MsgHandler;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** EventPushMsgHandler is the type of EVENT_LOG_PUSH message handler. */
public class EventPushMsgHandler implements MsgHandler {
    private static final Logger logger = LoggerFactory.getLogger(EventPushMsgHandler.class);
    FilterManager filterManager;

    public EventPushMsgHandler(FilterManager filterManager) {
        this.filterManager = filterManager;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        logger.warn("onConnect accidentally called");
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Message msg) {
        String content = new String(msg.getData());
        try {
            EventLogResponse resp =
                    ObjectMapperFactory.getObjectMapper()
                            .readValue(content, EventLogResponse.class);
            if (resp == null || StringUtils.isEmpty(resp.getFilterID())) {
                logger.error(" event log response invalid format, content: {}", content);
                return;
            }

            EventCallback callback = filterManager.getCallBack(resp.getFilterID());

            if (callback == null) {
                logger.debug(
                        " event log push message cannot find callback, filterID: {}, content: {}",
                        resp.getFilterID(),
                        content);
                return;
            }

            if (resp.getResult() == EventSubNodeRespStatus.SUCCESS.getStatus()) {
                List<EventLog> logs = resp.getLogs();
                if (!logs.isEmpty()) {
                    callback.onReceiveLog(resp.getResult(), logs);
                    // update status
                    callback.updateCountsAndLatestBlock(logs);
                    logger.info(
                            " log size: {}, blocknumber: {}",
                            logs.size(),
                            logs.get(0).getBlockNumber());
                }
            }
        } catch (JsonProcessingException e) {
            // todo handle exception
        }
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        logger.warn("onDisconnect accidentally called");
    }
}
