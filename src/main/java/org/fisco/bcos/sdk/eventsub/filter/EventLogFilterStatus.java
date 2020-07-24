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

package org.fisco.bcos.sdk.eventsub.filter;

public enum EventLogFilterStatus {
    // event log is pushing from node normally
    EVENT_LOG_PUSHING(0x1),
    // request already send, wait for response
    WAITING_RESPONSE(0x2),
    // response not ok, wait for resend
    WAITING_REQUEST(0x3);

    private int status;

    private EventLogFilterStatus(int i) {}

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
