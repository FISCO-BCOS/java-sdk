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
package org.fisco.bcos.sdk.transaction.domain.dto;

/**
 * TransactionRequest @Description: TransactionRequest
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:08:41 PM
 */
public class TransactionRequest extends CommonRequest {
    private String contractName;
    private String signedData;

    /** @return the contractName */
    public String getContractName() {
        return contractName;
    }

    /** @param contractName the contractName to set */
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    /** @return the signedData */
    public String getSignedData() {
        return signedData;
    }

    /** @param signedData the signedData to set */
    public void setSignedData(String signedData) {
        this.signedData = signedData;
    }
}
