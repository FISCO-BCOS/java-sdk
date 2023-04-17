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
package org.fisco.bcos.sdk.v3.contract.precompiled.consensus;

import static org.fisco.bcos.sdk.v3.model.PrecompiledConstant.SYNC_KEEP_UP_THRESHOLD;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.SealerList;
import org.fisco.bcos.sdk.v3.client.protocol.response.SyncStatus;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class ConsensusService {
    private final ConsensusPrecompiled consensusPrecompiled;
    private final Client client;

    public ConsensusService(Client client, CryptoKeyPair credential) {
        this.client = client;
        // load the ConsensusPrecompiled
        this.consensusPrecompiled =
                ConsensusPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.CONSENSUS_PRECOMPILED_NAME
                                : PrecompiledAddress.CONSENSUS_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    private boolean existsInNodeList(String nodeId) {
        List<String> nodeIdList = client.getGroupPeers().getGroupPeers();
        // this will cause in free nodes, which group peers is null
        if (nodeIdList == null) {
            return false;
        }
        return nodeIdList.contains(nodeId);
    }

    public RetCode addSealer(String nodeId, BigInteger weight) throws ContractException {
        // check the nodeId exists in the nodeList or not
        if (!existsInNodeList(nodeId)) {
            throw new ContractException(PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST);
        }
        // check the node exists in the sealerList or not
        List<SealerList.Sealer> sealerList = client.getSealerList().getResult();
        if (sealerList != null) {
            for (SealerList.Sealer sealer : sealerList) {
                if (sealer.getNodeID().equals(nodeId)) {
                    throw new ContractException(PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST);
                }
            }
        }
        List<String> observerList = client.getObserverList().getObserverList();
        if (observerList != null && !observerList.contains(nodeId)) {
            throw new ContractException(
                    PrecompiledRetCode.CODE_ADD_SEALER_SHOULD_IN_OBSERVER.getMessage(),
                    PrecompiledRetCode.CODE_ADD_SEALER_SHOULD_IN_OBSERVER.getCode());
        }
        // sdk sync status
        SyncStatus syncStatus = client.getSyncStatus();
        // sdk block number
        BigInteger highestNumber =
                BigInteger.valueOf(syncStatus.getSyncStatus().getKnownHighestNumber());
        boolean anyMatch;
        if (syncStatus.getSyncStatus().getNodeId().equals(nodeId)) {
            // sdk connect observer to be added to sealerList
            anyMatch =
                    syncStatus.getSyncStatus().getBlockNumber()
                            >= highestNumber.longValue() - SYNC_KEEP_UP_THRESHOLD;
        } else {
            anyMatch =
                    syncStatus.getSyncStatus().getPeers().stream()
                            .anyMatch(
                                    peersInfo ->
                                            peersInfo.getNodeId().equals(nodeId)
                                                    && peersInfo.getBlockNumber()
                                                            >= (highestNumber.longValue()
                                                                    - SYNC_KEEP_UP_THRESHOLD));
        }

        if (!anyMatch) {
            throw new ContractException(
                    "Observer should keep up the block number sync threshold: "
                            + SYNC_KEEP_UP_THRESHOLD);
        }
        TransactionReceipt receipt = consensusPrecompiled.addSealer(nodeId, weight);
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> consensusPrecompiled.getAddSealerOutput(tr).getValue1());
    }

    public RetCode addObserver(String nodeId) throws ContractException {
        // check the nodeId exists in the nodeList or not
        if (!existsInNodeList(nodeId)) {
            throw new ContractException(PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST);
        }
        List<String> observerList = client.getObserverList().getResult();
        if (observerList.contains(nodeId)) {
            throw new ContractException(PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST);
        }
        TransactionReceipt receipt = consensusPrecompiled.addObserver(nodeId);
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> consensusPrecompiled.getAddObserverOutput(tr).getValue1());
    }

    public RetCode removeNode(String nodeId) throws ContractException {
        TransactionReceipt receipt = consensusPrecompiled.remove(nodeId);
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> consensusPrecompiled.getRemoveOutput(tr).getValue1());
    }

    public RetCode setWeight(String nodeId, BigInteger weight) throws ContractException {
        TransactionReceipt receipt = consensusPrecompiled.setWeight(nodeId, weight);
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> consensusPrecompiled.getSetWeightOutput(tr).getValue1());
    }
}
