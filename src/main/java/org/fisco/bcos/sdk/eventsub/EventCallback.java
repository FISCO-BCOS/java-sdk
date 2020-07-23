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

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.model.EventLog;

public abstract class EventCallback {
    private BigInteger lastBlockNumber = null;
    private long logCount = 0;

    public BigInteger getLastBlockNumber() {
        return lastBlockNumber;
    }

    public void updateCountsAndLatestBlock(List<EventLog> logs) {
        if (logs.isEmpty()) {
            return;
        }
        EventLog latestOne = logs.get(logs.size() - 1);
        if (lastBlockNumber == null) {
            lastBlockNumber = latestOne.getBlockNumber();
            logCount += logs.size();
        } else {
            if (latestOne.getBlockNumber().compareTo(lastBlockNumber) > 0) {
                lastBlockNumber = latestOne.getBlockNumber();
                logCount += logs.size();
            }
        }
    }

    public abstract void onReceiveLog(int status, List<EventLog> logs);
}
