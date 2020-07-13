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

import java.util.List;
import org.fisco.bcos.sdk.model.Message;

/** Network interface Modules interact with the network module through this interface. */
public interface Network {
    /**
     * Init network module
     *
     * @param configFile
     * @return a Network implementation instance
     */
    static Network build(String configFile, MsgHandler handler) {
        return null;
    }

    /**
     * Broadcast message
     *
     * @param out
     */
    void broadcast(Message out);

    /**
     * Send to peer
     *
     * @param out
     * @param peerIpPort
     */
    void sendToPeer(Message out, String peerIpPort);

    /**
     * Get connection information
     *
     * @return list of connection information
     */
    List<ConnectionInfo> getConnectionInfo();
}
