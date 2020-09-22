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

package org.fisco.bcos.sdk.network;

import org.fisco.bcos.sdk.utils.Host;

/** Connection information. */
public class ConnectionInfo {

    public ConnectionInfo(String peerIpPort) {
        String IP = Host.getIpFromString(peerIpPort);
        String port = Host.getPortFromString(peerIpPort);
        this.ip = IP;
        this.port = Integer.parseInt(port);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getEndPoint() {
        return ip + ":" + port;
    }

    private String ip = "";
    private Integer port = 0;

    @Override
    public String toString() {
        return "ConnectionInfo{" + "host='" + ip + '\'' + ", port=" + port + '}';
    }
}
