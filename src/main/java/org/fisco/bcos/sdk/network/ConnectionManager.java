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

import org.fisco.bcos.sdk.config.ConfigOption;

public class ConnectionManager {
    private ConfigOption configOps;
    private MsgHandler msgHandler;

    public ConnectionManager(ConfigOption configOps, MsgHandler msgHandler) {
        this.configOps = configOps;
        this.msgHandler = msgHandler;
    }

    /** Init connections */
    public void init() {}

    public void startConnect() {}

    public void removeConnection() {}

    protected void initSSLContext() {}
}
