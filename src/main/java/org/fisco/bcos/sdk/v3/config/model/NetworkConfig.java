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

package org.fisco.bcos.sdk.v3.config.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Network configuration, include the peers */
public class NetworkConfig {

    private static final Logger logger = LoggerFactory.getLogger(NetworkConfig.class);

    private List<String> peers;
    private List<String> tarsPeers;
    private int timeout = -1;
    private String defaultGroup;
    private boolean sendRpcRequestToHighestBlockNode = true;

    public NetworkConfig() {}

    public NetworkConfig(ConfigProperty configProperty) {
        Map<String, Object> networkProperty = configProperty.getNetwork();
        if (networkProperty != null) {
            peers = (List<String>) networkProperty.get("peers");
            tarsPeers = (List<String>) networkProperty.get("tarsPeers");
            defaultGroup = (String) networkProperty.get("defaultGroup");
            Object value = networkProperty.get("messageTimeout");
            if (Objects.nonNull(value)) {
                timeout = Integer.parseInt((String) value);
            }

            value = networkProperty.get("sendRpcRequestToHighestBlockNode");
            if (Objects.nonNull(value)) {
                sendRpcRequestToHighestBlockNode = Boolean.parseBoolean((String) value);
            }
            logger.info(
                    "network config items, sendRpcRequestToHighestBlockNode: {}, timeout: {}, peers: {}",
                    sendRpcRequestToHighestBlockNode,
                    timeout,
                    peers);
        }
    }

    public List<String> getPeers() {
        return peers;
    }

    public void setPeers(List<String> peers) {
        this.peers = peers;
    }
    
    public List<String> getTarsPeers() {
      return tarsPeers;
    }

    public void setTarsPeers(List<String> tarsPeers) {
      this.tarsPeers = tarsPeers;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isSendRpcRequestToHighestBlockNode() {
        return sendRpcRequestToHighestBlockNode;
    }

    public void setSendRpcRequestToHighestBlockNode(boolean sendRpcRequestToHighestBlockNode) {
        this.sendRpcRequestToHighestBlockNode = sendRpcRequestToHighestBlockNode;
    }

    @Override
    public String toString() {
        return "NetworkConfig{"
                + "peers="
                + peers
                + ", tarsPeers="
                + tarsPeers
                + ", timeout="
                + timeout
                + ", defaultGroup='"
                + defaultGroup
                + '\''
                + ", sendRpcRequestToHighestBlockNode="
                + sendRpcRequestToHighestBlockNode
                + '}';
    }
}
