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

package org.fisco.bcos.sdk.transaction.model.callback;

import io.netty.util.Timeout;
import org.fisco.bcos.sdk.channel.model.ChannelMessageError;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransactionCallback {
    private static Logger logger = LoggerFactory.getLogger(TransactionCallback.class);
    private Timeout timeoutHandler;
    public static Integer DEFAULT_TRANS_TIMEOUT = 10 * 1000;
    private Integer timeout = DEFAULT_TRANS_TIMEOUT;

    public abstract void onResponse(TransactionReceipt receipt);

    public void onError(int errorCode, String errorMessage) {
        cancelTimeout();
        logger.error(
                "transaction exception, errorCode: {}, errorMessage: {}", errorCode, errorMessage);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(String.valueOf(errorCode));
        receipt.setMessage(errorMessage);
        onResponse(receipt);
    }

    public void cancelTimeout() {
        if (getTimeoutHandler() != null && !getTimeoutHandler().isCancelled()) {
            getTimeoutHandler().cancel();
        }
    }

    public void onTimeout() {
        cancelTimeout();
        logger.warn("transactionSuc timeout");
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(
                "Transaction receipt timeout, error code:"
                        + String.valueOf(ChannelMessageError.MESSAGE_TIMEOUT.getError()));
        onResponse(receipt);
    }

    public Timeout getTimeoutHandler() {
        return timeoutHandler;
    }

    public void setTimeoutHandler(Timeout timeoutHandler) {
        this.timeoutHandler = timeoutHandler;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getTimeout() {
        return this.timeout;
    }
}
