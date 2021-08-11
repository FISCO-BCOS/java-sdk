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
package org.fisco.bcos.sdk.contract.precompiled.consensus;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

public class ConsensusService {
    private final ConsensusPrecompiled consensusPrecompiled;
    private final Client client;

    public ConsensusService(Client client, CryptoKeyPair credential) {
        this.client = client;
        // load the ConsensusPrecompiled
        this.consensusPrecompiled =
                ConsensusPrecompiled.load(
                        PrecompiledAddress.CONSENSUS_PRECOMPILED_ADDRESS, client, credential);
    }

    private boolean existsInNodeList(String nodeId) {
        List<String> nodeIdList = client.getSealerList().getSealerList();
        nodeIdList.addAll(client.getObserverList().getObserverList());
        return nodeIdList.contains(nodeId);
    }

    public RetCode addSealer(String nodeId, BigInteger weight) throws ContractException {
        // check the nodeId exists in the nodeList or not
        if (!existsInNodeList(nodeId)) {
            throw new ContractException(PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST);
        }
        // check the node exists in the sealerList or not
        List<String> sealerList = client.getSealerList().getResult();
        if (sealerList.contains(nodeId)) {
            throw new ContractException(PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST);
        }
        return ReceiptParser.parseTransactionReceipt(
                consensusPrecompiled.addSealer(nodeId, weight));
    }

    public RetCode addObserver(String nodeId) throws ContractException {
        List<String> observerList = client.getObserverList().getResult();
        if (observerList.contains(nodeId)) {
            throw new ContractException(PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST);
        }
        return ReceiptParser.parseTransactionReceipt(consensusPrecompiled.addObserver(nodeId));
    }

    public RetCode removeNode(String nodeId) throws ContractException {
        List<String> sealerList = client.getSealerList().getResult();
        List<String> observerList = client.getObserverList().getResult();
        if (!sealerList.contains(nodeId) && !observerList.contains(nodeId)) {
            throw new ContractException(PrecompiledRetCode.ALREADY_REMOVED_FROM_THE_GROUP);
        }
        return ReceiptParser.parseTransactionReceipt(consensusPrecompiled.remove(nodeId));
    }

    public RetCode setWeight(String nodeId, BigInteger weight) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                consensusPrecompiled.setWeight(nodeId, weight));
    }
}
