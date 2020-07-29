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
package org.fisco.bcos.sdk.transaction.manager;

import org.fisco.bcos.sdk.channel.model.EnumNodeVersion;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManagerFactory {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManagerFactory.class);
    /**
     * @param client
     * @param credential
     * @return
     */
    public static TransactionManager createTransactionManager(
            Client client, CryptoInterface credential) {
        try {
            // get supported version of the node
            NodeVersion version = client.getNodeVersion();
            String binaryVersion = version.getNodeVersion().getVersion();
            String supportedVersion = version.getNodeVersion().getSupportedVersion();
            logger.debug(
                    "getNodeVersion before createTransactionManager, binaryVerison: {}, supportedVersion:{}",
                    binaryVersion,
                    supportedVersion);
            // transaction manager for rc1 transaction (without groupId and chainId)
            // TODO: init TransactionManager with client and credential
            if (EnumNodeVersion.BCOS_2_0_0_RC1.equals(binaryVersion)
                    || EnumNodeVersion.BCOS_2_0_0_RC1.equals(supportedVersion)) {
                logger.debug("createTransactionManager for rc1 node");
                return new TransactionManager();
            }
            // transaction manager for >=rc2 transaction (with groupId and chainId)
            else {
                // get chainId
                String chainId = version.getNodeVersion().getChainId();
                // get groupId
                Integer groupId = client.getGroupId();
                logger.debug(
                        "createTransactionManager for >=rc2 node, chainId: {}, groupId: {}",
                        chainId,
                        groupId);
                return new TransactionManager();
            }
        } catch (ClientException e) {
            logger.error(
                    "createTransactionManager for query nodeVersion failed, error info: {}",
                    e.getMessage());
        }
        return new TransactionManager();
    }
}
