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
package org.fisco.bcos.sdk.transaction.tools;

import org.apache.commons.lang3.tuple.Pair;

/**
 * ContractLoader @Description: ContractLoader
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:24:40 PM
 */
public class ContractLoader {
    private int readType;
    private String path;

    /**
     * @param readType
     * @param path
     */
    public ContractLoader(int readType, String path) {
        super();
        this.readType = readType;
        this.path = path;
    }

    public String getABIByContractName(String contractName) {
        // TODO
        return null;
    }

    public String getBinaryByContractName(String contractName) {
        // TODO
        return null;
    }

    public Pair<String, String> getABIAndBinaryByContractName(String contractName) {
        // TODO
        return null;
    }

    // TODO
    /*
     * public AbiDefinition getConstructorABIByContractName(String contractName) {
     *
     * }
     *
     * public List<AbiDefinition> getFunctionABIListByContractName(String contractName) {
     *
     * }
     */

    /** @return the readType */
    public int getReadType() {
        return readType;
    }

    /** @param readType the readType to set */
    public void setReadType(int readType) {
        this.readType = readType;
    }

    /** @return the path */
    public String getPath() {
        return path;
    }

    /** @param path the path to set */
    public void setPath(String path) {
        this.path = path;
    }
}
