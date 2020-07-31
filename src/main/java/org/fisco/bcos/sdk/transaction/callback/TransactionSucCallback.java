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
package org.fisco.bcos.sdk.transaction.callback;

import io.netty.util.Timeout;
import org.fisco.bcos.sdk.channel.model.ChannelMessageError;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransactionSucCallback {
    private static Logger logger = LoggerFactory.getLogger(TransactionSucCallback.class);
    private Timeout timeout;

    public abstract void onResponse(TransactionReceipt receipt);

    public void onTimeout() {
        logger.error("transactionSuc timeout");
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setStatus("Transaction receipt timeout.");
        receipt.setStatus(String.valueOf(ChannelMessageError.MESSAGE_TIMEOUT.getError()));
        onResponse(receipt);
    }

    public Timeout getTimeout() {
        return timeout;
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }
}
