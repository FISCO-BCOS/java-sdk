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
package org.fisco.bcos.sdk.model;

import java.util.List;

/**
 * Constructor @Description: SolidityConstructor
 *
 * @author maojiayu
 */
public class SolidityConstructor {

    private String contractName;
    private List<Object> params;
    private String binary;
    private String abi;
    private String data;

    /**
     * @param contractName
     * @param params
     * @param binary
     * @param abi
     * @param data
     */
    public SolidityConstructor(
            String contractName, List<Object> params, String binary, String abi, String data) {
        this.contractName = contractName;
        this.params = params;
        this.binary = binary;
        this.abi = abi;
        this.data = data;
    }

    /** @return the contractName */
    public String getContractName() {
        return contractName;
    }

    /** @param contractName the contractName to set */
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    /** @return the params */
    public List<Object> getParams() {
        return params;
    }

    /** @param params the params to set */
    public void setParams(List<Object> params) {
        this.params = params;
    }

    /** @return the binary */
    public String getBinary() {
        return binary;
    }

    /** @param binary the binary to set */
    public void setBinary(String binary) {
        this.binary = binary;
    }

    /** @return the abi */
    public String getAbi() {
        return abi;
    }

    /** @param abi the abi to set */
    public void setAbi(String abi) {
        this.abi = abi;
    }

    /** @return the data */
    public String getData() {
        return data;
    }

    /** @param data the data to set */
    public void setData(String data) {
        this.data = data;
    }
}
