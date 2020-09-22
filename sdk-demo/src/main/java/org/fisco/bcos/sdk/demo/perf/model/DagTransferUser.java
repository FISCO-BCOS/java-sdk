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
package org.fisco.bcos.sdk.demo.perf.model;

import java.math.BigInteger;

public class DagTransferUser {
    private String user;
    private BigInteger amount;

    public String getUser() {
        return user;
    }

    public synchronized void setUser(String user) {
        this.user = user;
    }

    public synchronized BigInteger getAmount() {
        return amount;
    }

    public synchronized void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public synchronized void increase(BigInteger amount) {
        this.amount = this.amount.add(amount);
    }

    public synchronized void decrease(BigInteger amount) {
        this.amount = this.amount.subtract(amount);
    }
}
