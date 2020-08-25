/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.demo.perf.callback;

import java.math.BigInteger;
import org.fisco.bcos.sdk.demo.perf.collector.PerformanceCollector;
import org.fisco.bcos.sdk.demo.perf.model.DagTransferUser;
import org.fisco.bcos.sdk.demo.perf.model.DagUserInfo;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelOkCallback extends TransactionCallback {
    private static Logger logger = LoggerFactory.getLogger(ParallelOkCallback.class);
    public static final String ADD_USER_CALLBACK = "add";
    public static final String TRANS_CALLBACK = "transfer";
    private Long startTime;

    private final PerformanceCollector collector;
    private final DagUserInfo dagUserInfo;
    private final String callbackType;

    private DagTransferUser user = null;
    private DagTransferUser fromUser = null;
    private DagTransferUser toUser = null;
    private BigInteger amount = null;

    public ParallelOkCallback(
            PerformanceCollector collector, DagUserInfo dagUserInfo, String callbackType) {
        this.collector = collector;
        this.dagUserInfo = dagUserInfo;
        this.callbackType = callbackType;
    }

    public void recordStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void onResponse(TransactionReceipt receipt) {
        Long cost = System.currentTimeMillis() - startTime;
        try {
            if (receipt.isStatusOK()) {
                if (callbackType.compareTo(ADD_USER_CALLBACK) == 0) { // add test
                    dagUserInfo.addUser(user);
                } else if (callbackType.compareTo(TRANS_CALLBACK) == 0) { // transfer test
                    fromUser.decrease(amount);
                    toUser.increase(amount);
                }
            }
            collector.onMessage(receipt, cost);
        } catch (Exception e) {
            logger.error("onMessage error: ", e);
        }
    }

    public DagTransferUser getFromUser() {
        return fromUser;
    }

    public void setFromUser(DagTransferUser fromUser) {
        this.fromUser = fromUser;
    }

    public DagTransferUser getToUser() {
        return toUser;
    }

    public void setToUser(DagTransferUser toUser) {
        this.toUser = toUser;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public DagTransferUser getUser() {
        return user;
    }

    public void setUser(DagTransferUser user) {
        this.user = user;
    }
}
