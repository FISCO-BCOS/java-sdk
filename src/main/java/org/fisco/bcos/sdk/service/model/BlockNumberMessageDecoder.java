/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.service.model;

import java.io.IOException;
import org.fisco.bcos.sdk.channel.model.EnumChannelProtocolVersion;
import org.fisco.bcos.sdk.model.Message;
import org.fisco.bcos.sdk.model.MsgType;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockNumberMessageDecoder {
    private static Logger logger = LoggerFactory.getLogger(BlockNumberMessageDecoder.class);
    private EnumChannelProtocolVersion version;

    public BlockNumberMessageDecoder(EnumChannelProtocolVersion version) {
        this.version = version;
    }

    public BlockNumberNotification decode(Message message) {
        if (!message.getType().equals(Short.valueOf((short) MsgType.BLOCK_NOTIFY.ordinal()))) {
            logger.warn(
                    "[BlockNumberMessageDecoder] invalid message, type: {}, seq: {}, size: {}",
                    message.getType(),
                    message.getSeq(),
                    message.getLength());
            return null;
        }
        BlockNumberNotification blockNumberNotification;
        switch (this.version) {
            case VERSION_1:
                {
                    blockNumberNotification = decodeByDefault(message);
                }
                break;
            default:
                {
                    blockNumberNotification = decodeV1(message);
                }
                break;
        }
        return blockNumberNotification;
    }

    /**
     * @param message
     * @return
     */
    protected BlockNumberNotification decodeByDefault(Message message) {
        try {
            return ObjectMapperFactory.getObjectMapper()
                    .readValue(message.getData(), BlockNumberNotification.class);
        } catch (IOException e) {
            logger.warn(
                    "[BlockNumberMessageDecoder] [] decode BlockNumberNotification message failed, type: {}, seq: {}, size: {}, reason: {}",
                    message.getType(),
                    message.getSeq(),
                    message.getLength(),
                    e.getMessage());
            return null;
        }
    }

    /**
     * @param message
     * @return
     */
    protected BlockNumberNotification decodeV1(Message message) {
        String[] split = message.getData().toString().split(",");
        if (split.length != 2) {
            return null;
        }
        return new BlockNumberNotification(split[0], split[1]);
    }
}
