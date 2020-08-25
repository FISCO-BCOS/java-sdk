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
package org.fisco.bcos.sdk.contract.precompiled.contractmgr;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.ReceiptParser;
import org.fisco.bcos.sdk.model.RetCode;

public class ContractLifeCycleService {
    private final ContractLifeCyclePrecompiled contractLifeCyclePrecompiled;

    public ContractLifeCycleService(Client client, CryptoInterface credential) {
        this.contractLifeCyclePrecompiled =
                ContractLifeCyclePrecompiled.load(
                        PrecompiledAddress.CONTRACT_LIFECYCLE_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    public RetCode freeze(String contractAddress) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                this.contractLifeCyclePrecompiled.freeze(contractAddress));
    }

    public RetCode unfreeze(String contractAddress) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                this.contractLifeCyclePrecompiled.unfreeze(contractAddress));
    }

    public RetCode grantManager(String contractAddress, String userAddress)
            throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                this.contractLifeCyclePrecompiled.grantManager(contractAddress, userAddress));
    }

    public String getContractStatus(String contractAddress) throws ContractException {
        try {
            Tuple2<BigInteger, String> result =
                    this.contractLifeCyclePrecompiled.getStatus(contractAddress);
            if (!result.getValue1().equals(PrecompiledRetCode.CODE_SUCCESS)) {
                return PrecompiledRetCode.getPrecompiledResponse(
                                result.getValue1().intValue(), result.getValue2())
                        .getMessage();
            }
            return result.getValue2();
        } catch (ContractException e) {
            throw new ContractException(
                    "ContractLifCycleService: getContractStatus for "
                            + contractAddress
                            + " failed, error info:"
                            + e.getMessage(),
                    e);
        }
    }

    public List<String> listManager(String contractAddress) throws ContractException {
        try {
            Tuple2<BigInteger, List<String>> result =
                    this.contractLifeCyclePrecompiled.listManager(contractAddress);
            if (!result.getValue1().equals(PrecompiledRetCode.CODE_SUCCESS)) {
                String errorMessage =
                        PrecompiledRetCode.getPrecompiledResponse(
                                        result.getValue1().intValue(),
                                        result.getValue2().toString())
                                .getMessage();
                throw new ContractException(
                        "contractLifCycleService: listManager for "
                                + contractAddress
                                + " failed, reason:"
                                + errorMessage);
            }
            return result.getValue2();
        } catch (ContractException e) {
            throw new ContractException(
                    "ContractLifCycleService: listManager for "
                            + contractAddress
                            + " failed, error info: "
                            + e.getMessage(),
                    e);
        }
    }
}
