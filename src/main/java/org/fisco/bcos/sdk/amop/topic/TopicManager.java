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

package org.fisco.bcos.sdk.amop.topic;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.fisco.bcos.sdk.amop.AmopCallback;

public class TopicManager {
    Map<String, AmopCallback> seq2Callback;
    Map<String, KeyStore> topic2PrivateKey;
    Map<String, List<KeyStore>> topic2PublicKey;
    Set<String> topics = new HashSet<>();
    Map<String, Set<String>> peer2BlockNotify = new HashMap<>();

    public void addTopic(String topicString, AmopCallback callback) {
        topics.add(topicString);
        if (callback == null) {
            return;
        }
    }

    public void addPrivateTopic(String topicName, KeyStore privateKeyStore, AmopCallback callback) {
        return;
    }

    public void removeTopic(String topicName) {
        return;
    }

    public AmopCallback getCallback(String seq) {
        return seq2Callback.get(seq);
    }

    public Set<String> getSubByPeer(String peerIpPort) {
        Set<String> notify = peer2BlockNotify.get(peerIpPort);
        if (notify != null && topics != null) {
            Set<String> peerSub = new HashSet<>();
            peerSub.addAll(topics);
            peerSub.addAll(notify);
            return peerSub;
        } else if (notify != null && topics == null) {
            return notify;
        } else {
            return topics;
        }
    }

    public void addBlockNotify(String peerIpPort, List<String> groupInfo) {
        Set<String> pnf = peer2BlockNotify.get(peerIpPort);
        if (null == pnf) {
            pnf = new HashSet<>();
            for (String group : groupInfo) {
                pnf.add("_block_notify_" + group);
            }
            peer2BlockNotify.put(peerIpPort, pnf);
        } else {
            for (String group : groupInfo) {
                pnf.add("_block_notify_" + group);
            }
        }
    }
}
