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

package org.fisco.bcos.sdk.amop;

import org.fisco.bcos.sdk.amop.topic.TopicManager;
import org.fisco.bcos.sdk.amop.topic.TopicType;

public class AmopMsgOut {
    private String topic;
    private byte[] content;
    private TopicType type = TopicType.NORMAL_TOPIC;
    private long timeout = 5000;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getTopic() {
        if (type.equals(TopicType.NORMAL_TOPIC)) {
            return topic;
        } else {
            return addNeedVerifyTopicPrefix(topic);
        }
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public TopicType getType() {
        return type;
    }

    public void setType(TopicType type) {
        this.type = type;
    }

    private String addNeedVerifyTopicPrefix(String topicName) {
        StringBuilder sb = new StringBuilder();
        sb.append(TopicManager.topicNeedVerifyPrefix);
        sb.append(topicName);
        return sb.toString();
    }
}
