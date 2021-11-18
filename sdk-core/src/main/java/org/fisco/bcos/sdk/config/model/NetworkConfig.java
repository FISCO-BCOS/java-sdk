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

package org.fisco.bcos.sdk.config.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Network configuration, include the peers */
public class NetworkConfig {

    private static Logger logger = LoggerFactory.getLogger(NetworkConfig.class);

    private List<String> peers;
    private String defaultGroup;
    private int timeout = -1;

    public NetworkConfig() {}

    public NetworkConfig(ConfigProperty configProperty) throws ConfigException {
        Map<String, Object> networkProperty = configProperty.getNetwork();
        if (networkProperty != null) {
            peers = (List<String>) networkProperty.get("peers");
            defaultGroup = (String) networkProperty.get("defaultGroup");
            Object value = networkProperty.get("messageTimeout");
            if (Objects.nonNull(value)) {
                timeout = Integer.valueOf((String) value);
            }
            logger.info("network config items, timeout: {}, peers: {}", timeout, peers);
        }
    }

    public List<String> getPeers() {
        return peers;
    }

    public void setPeers(List<String> peers) {
        this.peers = peers;
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
}
