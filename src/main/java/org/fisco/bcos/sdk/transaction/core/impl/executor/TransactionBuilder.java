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
package org.fisco.bcos.sdk.transaction.core.impl.executor;

import java.math.BigInteger;
import org.fisco.bcos.sdk.transaction.core.interf.executor.TransactionBuilderInterface;
import org.fisco.bcos.sdk.transaction.domain.RawTransaction;

public class TransactionBuilder implements TransactionBuilderInterface {

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RawTransaction createTransaction(String to, String data, BigInteger groupId) {
        // TODO Auto-generated method stub
        return null;
    }
}
