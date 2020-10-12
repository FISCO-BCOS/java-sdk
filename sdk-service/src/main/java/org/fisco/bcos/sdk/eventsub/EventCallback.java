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

package org.fisco.bcos.sdk.eventsub;

import java.util.List;
import org.fisco.bcos.sdk.model.EventLog;

/** Event callback */
public interface EventCallback {

    /**
     * onReceiveLog called when sdk receive any response of the target subscription. logs will be
     * parsed by the user through the ABI module.
     *
     * @param status the status that peer response to sdk.
     * @param logs logs from the message.
     */
    void onReceiveLog(int status, List<EventLog> logs);
}
