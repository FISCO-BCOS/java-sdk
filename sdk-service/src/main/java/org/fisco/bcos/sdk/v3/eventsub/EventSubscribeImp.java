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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.fisco.bcos.sdk.jni.BcosSDKJniObj;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.event.EventSubJniObj;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSubscribeImp implements EventSubscribe {

    private static final Logger logger = LoggerFactory.getLogger(EventSubscribeImp.class);

    private String groupId;
    private ConfigOption configOption;
    private CryptoSuite cryptoSuite;
    private EventSubJniObj eventSubJniObj;

    private ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    public EventSubscribeImp(Client client, ConfigOption configOption) throws JniException {
        this.groupId = client.getGroup();
        this.configOption = configOption;
        this.cryptoSuite = client.getCryptoSuite();
        this.eventSubJniObj = EventSubJniObj.build(client.getNativePointer());
        this.configOption = client.getConfigOption();

        logger.info(" EventSub constructor, group: {}, configOption: {}", groupId, configOption);
    }

    public CryptoSuite getCryptoSuite() {
        return cryptoSuite;
    }

    public void setCryptoSuite(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ConfigOption getConfigOption() {
        return configOption;
    }

    public void setConfigOption(ConfigOption configOption) {
        this.configOption = configOption;
    }

    @Override
    public String subscribeEvent(EventSubParams params, EventSubCallback callback) {

        if (!params.checkParams()) {
            callback.onReceiveLog("", EventSubStatus.INVALID_PARAMS.getStatus(), null);
            return "";
        }

        String strParams = null;
        try {
            strParams = objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            logger.error("e: ", e);
            return "";
        }

        logger.info("EventSub subscribeEvent, params: {}", params);

        return eventSubJniObj.subscribeEvent(
                groupId,
                strParams,
                (response) -> {
                    {
                        if (response.getErrorCode() != 0) {
                            logger.error(
                                    "subscribeEvent response error, errorCode: {}, errorMessage: {}",
                                    response.getErrorCode(),
                                    response.getErrorMessage());
                            callback.onReceiveLog("", response.getErrorCode(), null);
                            return;
                        }

                        String strResp = new String(response.getData());
                        logger.debug(
                                "subscribeEvent response, errorCode: {}, errorMessage: {}, data: {}",
                                response.getErrorCode(),
                                response.getErrorMessage(),
                                strResp);

                        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
                        try {
                            EventSubResponse eventSubResponse =
                                    objectMapper.readValue(strResp, EventSubResponse.class);
                            callback.onReceiveLog(
                                    eventSubResponse.getId(),
                                    eventSubResponse.getStatus(),
                                    eventSubResponse.getLogs());
                        } catch (JsonProcessingException e) {
                            logger.error(
                                    "subscribeEvent response parser json error, resp: {}, e: {}",
                                    strResp,
                                    e);
                        }
                    }
                });
    }

    @Override
    public void unsubscribeEvent(String eventId) {
        eventSubJniObj.unsubscribeEvent(eventId);
    }

    @Override
    public Set<String> getAllSubscribedEvents() {
        // TODO: impl
        return null;
    }

    @Override
    public void start() {
        eventSubJniObj.start();
    }

    @Override
    public void stop() {
        eventSubJniObj.stop();
    }

    @Override
    public void destroy() {
        if (eventSubJniObj != null) {
            BcosSDKJniObj.destroy(eventSubJniObj.getNativePointer());
            eventSubJniObj = null;
        }
    }
}
