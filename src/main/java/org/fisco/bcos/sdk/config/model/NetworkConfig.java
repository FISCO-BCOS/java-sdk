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

import io.netty.util.NetUtil;
import java.util.List;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.utils.Host;

public class NetworkConfig {
    List<String> peers;

    public NetworkConfig(ConfigProperty configProperty) throws ConfigException {
        peers = configProperty.getPeers();
        checkPeers(peers);
    }

    private void checkPeers(List<String> peers) throws ConfigException {
        if (peers == null || peers.size() == 0) {
            throw new ConfigException(
                    "Invalid configuration, peers not configured, please config peers in yaml config file.");
        }
        for (String peer : peers) {
            int index = peer.lastIndexOf(':');
            if (index == -1) {
                throw new ConfigException(
                        " Invalid configuration, the peer value should in IP:Port format(eg: 127.0.0.1:1111), value: "
                                + peer);
            }
            String IP = peer.substring(0, index);
            String port = peer.substring(index + 1);

            if (!(NetUtil.isValidIpV4Address(IP) || NetUtil.isValidIpV6Address(IP))) {
                throw new ConfigException(
                        " Invalid configuration, invalid IP string format, value: " + IP);
            }

            if (!Host.validPort(port)) {
                throw new ConfigException(
                        " Invalid configuration, tcp port should from 1 to 65535, value: " + port);
            }
        }
    }

    public List<String> getPeers() {
        return peers;
    }

    public void setPeers(List<String> peers) {
        this.peers = peers;
    }
}
