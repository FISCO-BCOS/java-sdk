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

package org.fisco.bcos.sdk.model;

/** Message types send from fisco bcos node. */
public enum MsgType {

    /**
     * Message types which Client module interested in. RPC_REQUEST: type of rpc request message
     * TRANSACTION_NOTIFY: type of transaction notify message BLOCK_NOTIFY: type of block notify
     * message
     */
    CLIENT_HANDSHAKE(0x100),
    BLOCK_NOTIFY(0x101),
    RPC_REQUEST(0x102),

    /**
     * Message types processed by EventSubscribe module CLIENT_REGISTER_EVENT_LOG:type of event log
     * filter register request and response message EVENT_LOG_PUSH:type of event log push message
     */
    CLIENT_REGISTER_EVENT_LOG(0x15),
    CLIENT_UNREGISTER_EVENT_LOG(0x16),
    EVENT_LOG_PUSH(0x1002),

    /**
     * Message types processed by AMOP module AMOP_REQUEST:type of request message from sdk
     * AMOP_RESPONSE:type of response message to sdk AMOP_MULBROADCAST:type of mult broadcast
     * message AMOP_CLIENT_TOPICS:type of topic request message REQUEST_TOPICCERT:type of request
     * verify message UPDATE_TOPIICSTATUS:type of update status message
     */
    AMOP_REQUEST(0x110),
    AMOP_RESPONSE(0x31),
    AMOP_MULBROADCAST(0x112),
    AMOP_CLIENT_TOPICS(0x32),
    REQUEST_TOPICCERT(0x37),
    UPDATE_TOPIICSTATUS(0x38);

    private int type;

    private MsgType(int type) {
        this.setType(type);
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
