/**
 * Copyright 2014-2019 the original author or authors.
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
package org.fisco.bcos.sdk.transaction.pusher;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.fisco.bcos.sdk.client.RespCallback;
import org.fisco.bcos.sdk.client.protocol.response.SendTransaction;
import org.fisco.bcos.sdk.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransactionCallback @Description: TransactionCallback
 *
 * @author maojiayu
 * @data Mar 23, 2020 9:53:42 PM
 */
public class TransactionCallback implements RespCallback<SendTransaction> {
    private static Logger logger = LoggerFactory.getLogger(TransactionCallback.class);

    private SendTransaction sendTransaction;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition condition;

    public TransactionCallback() {
        condition = reentrantLock.newCondition();
    }

    public SendTransaction getResult() {
        try {
            reentrantLock.lock();
            while (sendTransaction == null) {
                condition.awaitUninterruptibly();
            }
            return sendTransaction;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void onError(Response errorResponse) {
        logger.error("transactionSuc timeout");
        SendTransaction sendTransaction = new SendTransaction();
        sendTransaction.setError(
                new SendTransaction.Error(
                        errorResponse.getErrorCode(), errorResponse.getErrorMessage()));
        onResponse(sendTransaction);
    }

    @Override
    public void onResponse(SendTransaction t) {
        try {
            reentrantLock.lock();
            this.sendTransaction = t;
            condition.signal();
        } finally {
            reentrantLock.unlock();
        }
    }
}
