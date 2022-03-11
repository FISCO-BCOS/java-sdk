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

package org.fisco.bcos.sdk.v3.model.callback;

import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.TransactionReceiptStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransactionCallback {
    private static Logger logger = LoggerFactory.getLogger(TransactionCallback.class);
    public static Integer DEFAULT_TRANS_TIMEOUT = 30 * 1000;
    private Integer timeout = DEFAULT_TRANS_TIMEOUT;

    public abstract void onResponse(TransactionReceipt receipt);

    public void onError(int errorCode, String errorMessage) {
        logger.error(
                "transaction exception, errorCode: {}, errorMessage: {}", errorCode, errorMessage);
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(errorCode);
        receipt.setMessage(errorMessage);
        this.onResponse(receipt);
    }

    public void onTimeout() {
        logger.warn("transactionSuc timeout");
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus(TransactionReceiptStatus.TimeOut.getCode());
        receipt.setMessage(TransactionReceiptStatus.TimeOut.getMessage());
        this.onResponse(receipt);
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getTimeout() {
        return this.timeout;
    }
}
