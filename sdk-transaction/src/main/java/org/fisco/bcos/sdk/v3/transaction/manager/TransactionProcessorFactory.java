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
package org.fisco.bcos.sdk.v3.transaction.manager;

import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.signer.RemoteSignProviderInterface;
import org.fisco.bcos.sdk.v3.transaction.tools.ContractLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionProcessorFactory {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessorFactory.class);

    public static Pair<String, String> getChainIdAndGroupId(Client client) {
        // get chainId
        String chainId = client.getChainId();
        // get group
        String group = client.getGroup();
        logger.debug("createTransactionManager, chainId: {}, groupId: {}", chainId, group);
        return Pair.of(chainId, group);
    }

    public static TransactionProcessor createTransactionProcessor(
            Client client, CryptoKeyPair cryptoKeyPair) {
        Pair<String, String> pair = getChainIdAndGroupId(client);
        return new TransactionProcessor(client, cryptoKeyPair, pair.getRight(), pair.getLeft());
    }

    public static AssembleTransactionProcessor createAssembleTransactionProcessor(
            Client client, CryptoKeyPair cryptoKeyPair) {
        Pair<String, String> pair = getChainIdAndGroupId(client);
        return new AssembleTransactionProcessor(
                client, cryptoKeyPair, pair.getRight(), pair.getLeft(), null);
    }

    public static AssembleTransactionProcessor createAssembleTransactionProcessor(
            Client client, CryptoKeyPair cryptoKeyPair, String abiFilePath, String binFilePath)
            throws IOException {
        Pair<String, String> pair = getChainIdAndGroupId(client);
        ContractLoader contractLoader = new ContractLoader(abiFilePath, binFilePath);
        return new AssembleTransactionProcessor(
                client, cryptoKeyPair, pair.getRight(), pair.getLeft(), contractLoader);
    }

    public static AssembleTransactionProcessor createAssembleTransactionProcessor(
            Client client,
            CryptoKeyPair cryptoKeyPair,
            String contractName,
            String abi,
            String bin) {
        Pair<String, String> pair = getChainIdAndGroupId(client);
        return new AssembleTransactionProcessor(
                client, cryptoKeyPair, pair.getRight(), pair.getLeft(), contractName, abi, bin);
    }

    public static AssembleTransactionWithRemoteSignProcessor
            createAssembleTransactionWithRemoteSignProcessor(
                    Client client,
                    CryptoKeyPair cryptoKeyPair,
                    String contractName,
                    RemoteSignProviderInterface transactionSignProvider) {
        Pair<String, String> pair = getChainIdAndGroupId(client);
        return new AssembleTransactionWithRemoteSignProcessor(
                client,
                cryptoKeyPair,
                pair.getRight(),
                pair.getLeft(),
                contractName,
                transactionSignProvider);
    }

    public static AssembleTransactionWithRemoteSignProcessor
            createAssembleTransactionWithRemoteSignProcessor(
                    Client client,
                    CryptoKeyPair cryptoKeyPair,
                    String abiFilePath,
                    String binFilePath,
                    RemoteSignProviderInterface transactionSignProvider)
                    throws IOException {
        Pair<String, String> pair = getChainIdAndGroupId(client);
        ContractLoader contractLoader = new ContractLoader(abiFilePath, binFilePath);
        return new AssembleTransactionWithRemoteSignProcessor(
                client,
                cryptoKeyPair,
                pair.getRight(),
                pair.getLeft(),
                contractLoader,
                transactionSignProvider);
    }
}
