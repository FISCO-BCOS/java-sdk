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
package org.fisco.bcos.sdk.contract.precompiled.cns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledConstant;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.ReceiptParser;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;

public class CnsService {
    private final CNSPrecompiled cnsPrecompiled;

    public CnsService(Client client, CryptoInterface credential) {
        this.cnsPrecompiled =
                CNSPrecompiled.load(PrecompiledAddress.CNS_PRECOMPILED_ADDRESS, client, credential);
    }

    public RetCode registerCNS(
            String contractName, String contractVersion, String contractAddress, String abiData)
            throws ContractException {
        // check the length of the contractVersion
        if (contractVersion.length() > PrecompiledConstant.CNS_MAX_VERSION_LENGTH) {
            throw new ContractException(PrecompiledRetCode.OVER_CONTRACT_VERSION_LEN_LIMIT);
        }
        return ReceiptParser.parsePrecompiledReceipt(
                cnsPrecompiled.insert(contractName, contractVersion, contractAddress, abiData));
    }

    public List<CnsInfo> selectByName(String contractName) throws ContractException {
        try {
            String cnsInfo = cnsPrecompiled.selectByName(contractName);
            return ObjectMapperFactory.getObjectMapper()
                    .readValue(cnsInfo, new TypeReference<List<CnsInfo>>() {});
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "CnsService: failed to call selectByName interface, error message: "
                            + e.getMessage());
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public CnsInfo selectByNameAndVersion(String contractName, String contractVersion)
            throws ContractException {
        try {
            String cnsInfo = cnsPrecompiled.selectByNameAndVersion(contractName, contractVersion);
            return ObjectMapperFactory.getObjectMapper()
                    .readValue(cnsInfo, new TypeReference<CnsInfo>() {});
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "CnsService: failed to call selectByNameAndVersion interface, error message: "
                            + e.getMessage());
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public String getContractAddress(String contractName, String contractVersion)
            throws ContractException {
        try {
            return cnsPrecompiled.getContractAddress(contractName, contractVersion);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }
}
