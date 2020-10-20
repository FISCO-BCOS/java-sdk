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
package org.fisco.bcos.sdk.transaction.builder;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.model.gas.DefaultGasProvider;
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;

public class TransactionBuilderService implements TransactionBuilderInterface {
    private Client client;

    /**
     * create TransactionBuilderService
     *
     * @param client the client object
     */
    public TransactionBuilderService(Client client) {
        super();
        this.client = client;
    }

    @Override
    public RawTransaction createTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            BigInteger chainId,
            BigInteger groupId,
            String extraData) {
        Random r = ThreadLocalRandom.current();
        BigInteger randomId = new BigInteger(250, r);
        BigInteger blockLimit = client.getBlockLimit();
        return RawTransaction.createTransaction(
                randomId,
                gasPrice,
                gasLimit,
                blockLimit,
                to,
                value,
                data,
                chainId,
                groupId,
                extraData);
    }

    @Override
    public RawTransaction createTransaction(String to, String data, BigInteger groupId) {

        return createTransaction(
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                to,
                data,
                BigInteger.ZERO,
                BigInteger.ONE,
                groupId,
                null);
    }

    /** @return the client */
    public Client getClient() {
        return client;
    }

    /** @param client the client to set */
    public void setClient(Client client) {
        this.client = client;
    }
}
