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

package org.fisco.bcos.sdk.model.callback;

import io.netty.util.Timeout;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceiptStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransactionCallback {
    private static Logger logger = LoggerFactory.getLogger(TransactionCallback.class);
    private Timeout timeoutHandler;
    public static Integer DEFAULT_TRANS_TIMEOUT = 0;
    private Integer timeout = DEFAULT_TRANS_TIMEOUT;
    private String transactionHash;

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        if (logger.isDebugEnabled()) {
            logger.debug("transactionHash: {}", transactionHash);
        }
        this.transactionHash = transactionHash;
    }

    public abstract void onResponse(TransactionReceipt receipt);

    public void onError(int errorCode, String errorMessage) {
        cancelTimeout();
        logger.error(
                "transaction exception, transactionHash: {}, errorCode: {}, errorMessage: {}",
                transactionHash,
                errorCode,
                errorMessage);
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
        logger.warn("transactionSuc timeout, transactionHash: {}", transactionHash);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(String.valueOf(TransactionReceiptStatus.TimeOut.getCode()));
        receipt.setMessage(TransactionReceiptStatus.TimeOut.getMessage());
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
