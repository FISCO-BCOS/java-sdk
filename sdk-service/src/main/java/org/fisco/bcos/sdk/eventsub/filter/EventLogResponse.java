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

import java.util.List;
import org.fisco.bcos.sdk.model.EventLog;

public class EventLogResponse {
    private int result;
    private String filterID;
    private List<EventLog> logs;

    @Override
    public String toString() {
        return "EventLogResponse [result="
                + result
                + ", filterID="
                + filterID
                + ", logs="
                + logs
                + "]";
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getFilterID() {
        return filterID;
    }

    public void setFilterID(String filterID) {
        this.filterID = filterID;
    }

    public List<EventLog> getLogs() {
        return logs;
    }

    public void setLogs(List<EventLog> logs) {
        this.logs = logs;
    }
}
