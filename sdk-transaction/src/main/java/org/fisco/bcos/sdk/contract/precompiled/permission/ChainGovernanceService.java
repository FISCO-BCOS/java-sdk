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
package org.fisco.bcos.sdk.contract.precompiled.permission;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledVersionCheck;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

public class ChainGovernanceService {
    private final ChainGovernancePrecompiled chainGovernancePrecompiled;
    private String currentVersion;

    public ChainGovernanceService(Client client, CryptoKeyPair credential) {
        this.chainGovernancePrecompiled =
                ChainGovernancePrecompiled.load(
                        PrecompiledAddress.CHAINGOVERNANCE_PRECOMPILED_ADDRESS, client, credential);
        this.currentVersion = client.getNodeInfo().getSupportedVersion();
    }

    public RetCode grantCommitteeMember(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.grantCommitteeMember(userAddress));
    }

    public RetCode revokeCommitteeMember(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.revokeCommitteeMember(userAddress));
    }

    public List<PermissionInfo> listCommitteeMembers() throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        try {
            return PermissionService.parsePermissionInfo(
                    this.chainGovernancePrecompiled.listCommitteeMembers());
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "listCommitteeMembers exceptioned, error info: " + e.getMessage(), e);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public RetCode updateCommitteeMemberWeight(String userAddress, BigInteger weight)
            throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.updateCommitteeMemberWeight(userAddress, weight));
    }

    public RetCode updateThreshold(BigInteger threshold) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.updateThreshold(threshold));
    }

    public BigInteger queryThreshold() throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        try {
            return this.chainGovernancePrecompiled.queryThreshold();
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public BigInteger queryCommitteeMemberWeight(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        try {
            Tuple2<Boolean, BigInteger> queryResult =
                    this.chainGovernancePrecompiled.queryCommitteeMemberWeight(userAddress);
            if (queryResult.getValue1()) {
                return queryResult.getValue2();
            }
            // parse the error information
            RetCode errorMessage =
                    PrecompiledRetCode.getPrecompiledResponse(
                            queryResult.getValue2().intValue(), queryResult.getValue2().toString());
            throw new ContractException(
                    "queryCommitteeMemberWeight failed, error info: " + errorMessage.getMessage(),
                    errorMessage.getCode());
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public RetCode grantOperator(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.grantOperator(userAddress));
    }

    public RetCode revokeOperator(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.revokeOperator(userAddress));
    }

    public List<PermissionInfo> listOperators() throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        try {
            return PermissionService.parsePermissionInfo(
                    this.chainGovernancePrecompiled.listOperators());
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "listOperators exceptioned, error info:" + e.getMessage(), e);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public RetCode freezeAccount(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.freezeAccount(userAddress));
    }

    public RetCode unfreezeAccount(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        return ReceiptParser.parseTransactionReceipt(
                this.chainGovernancePrecompiled.unfreezeAccount(userAddress));
    }

    public String getAccountStatus(String userAddress) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_VERSION.checkVersion(currentVersion);
        try {
            return this.chainGovernancePrecompiled.getAccountStatus(userAddress);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public String queryVotesOfMember(String account) throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_QUERY_VERSION.checkVersion(
                currentVersion);
        try {
            return this.chainGovernancePrecompiled.queryVotesOfMember(account);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public String queryVotesOfThreshold() throws ContractException {
        PrecompiledVersionCheck.CHAIN_GOVERNANCE_PRECOMPILED_QUERY_VERSION.checkVersion(
                currentVersion);
        try {
            return this.chainGovernancePrecompiled.queryVotesOfThreshold();
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }
}
