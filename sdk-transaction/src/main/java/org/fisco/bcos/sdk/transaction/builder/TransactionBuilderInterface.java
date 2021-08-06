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
import org.fisco.bcos.sdk.client.protocol.model.TransactionData;

/**
 * TransactionBuilderInterface @Description: TransactionBuilderInterface
 *
 * @author maojiayu
 */
public interface TransactionBuilderInterface {

    /**
     * Create fisco bcos transaction for short
     *
     * @param to target address
     * @param data encoded data
     * @param groupId group id
     * @param chainId chain id
     * @return TransactionData the created transaction
     */
    public TransactionData createTransaction(
            String to, byte[] data, String chainId, String groupId);

    /**
     * Create fisco bcos transaction for short
     *
     * @param blockLimit, block limit
     * @param to target address
     * @param data encoded data
     * @param groupId group id
     * @return TransactionData the created transaction
     */
    public TransactionData createTransaction(
            BigInteger blockLimit, String to, byte[] data, String chainId, String groupId);
}
