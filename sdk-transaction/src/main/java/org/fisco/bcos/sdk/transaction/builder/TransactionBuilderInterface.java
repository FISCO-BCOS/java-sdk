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
import org.fisco.bcos.sdk.transaction.model.po.RawTransaction;

/**
 * TransactionBuilderInterface @Description: TransactionBuilderInterface
 *
 * @author maojiayu
 */
public interface TransactionBuilderInterface {

    /**
     * Create fisco bcos transaction
     *
     * @param gasPrice, @see DefaultGasProvider
     * @param gasLimit, @see DefaultGasProvider
     * @param to target address
     * @param data encoded data
     * @param value default 0
     * @param chainId default 1
     * @param groupId the group that need create transaction
     * @param extraData default null
     * @return RawTransaction the created transaction
     */
    public RawTransaction createTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            BigInteger chainId,
            BigInteger groupId,
            String extraData);

    /**
     * Create fisco bcos transaction for short
     *
     * @param to target address
     * @param data encoded data
     * @param groupId the group that need create transaction
     * @return RawTransaction the created transaction
     */
    public RawTransaction createTransaction(String to, String data, BigInteger groupId);
}
