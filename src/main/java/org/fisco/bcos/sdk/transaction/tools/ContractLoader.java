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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.transaction.domain.AbiInfo;
import org.fisco.bcos.sdk.transaction.domain.BinInfo;
import org.fisco.bcos.sdk.transaction.domain.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContractLoader @Description: ContractLoader
 *
 * @author maojiayu
 * @data Jul 17, 2020 3:24:40 PM
 */
public class ContractLoader {
    private static final Logger log = LoggerFactory.getLogger(ContractLoader.class);

    private int readType;
    private String path;
    private Map<String, List<AbiDefinition>> contractFuncAbis;
    private Map<String, AbiDefinition> contractConstructorAbi;
    private Map<String, String> contractBinMap;

    public ContractLoader(int readType, String path) throws Exception {
        this.readType = readType;
        this.path = path;
        this.binInfo();
        this.abiInfo();
    }

    public BinInfo binInfo() throws IOException  {
        String[] s = { "bin" };
        Collection<File> fileCollection = FileUtils.listFiles(new File(path + "/" + CommonConstant.BIN), s, true);
        if (fileCollection.isEmpty()) {
            log.warn("No bin found, cannot deploy any contract");
            return new BinInfo(Collections.emptyMap());
        }
        this.contractBinMap = new HashMap<>();
        for (File file : fileCollection) {
            String contract = parseContractName(file);
            String bin = FileUtils.readFileToString(file);
            contractBinMap.put(contract, bin);
        }
        return new BinInfo(contractBinMap);
    }

    public AbiInfo abiInfo() throws Exception {
        String[] s = { "abi" };
        Collection<File> fileCollection = FileUtils.listFiles(new File(path + "/" + CommonConstant.ABI), s, true);
        this.contractFuncAbis = new HashMap<>();
        this.contractConstructorAbi = new HashMap<>();
        for (File file : fileCollection) {
            String contract = parseContractName(file);
            List<AbiDefinition> abiList = parseAbiBody(file);
            AbiDefinition constructorAbi = selectConstructor(abiList);
            contractFuncAbis.put(contract, abiList);
            contractConstructorAbi.put(contract, constructorAbi);
        }
        return new AbiInfo(contractFuncAbis, contractConstructorAbi);
    }

    private AbiDefinition selectConstructor(List<AbiDefinition> abiList) {
        for (AbiDefinition abiDefinition : abiList) {
            if (abiDefinition.getType().equals(CommonConstant.ABI_CONSTRUCTOR)) {
                return abiDefinition;
            }
        }
        // The case where the sol file does not define constructor
        return null;
    }

    private String parseContractName(File file) {
        String fileName = file.getName();
        return StringUtils.substringBefore(fileName, ".");
    }

    private List<AbiDefinition> parseAbiBody(File file) throws Exception {
        String abiStr = FileUtils.readFileToString(file);
        return ContractAbiUtil.getFuncAbiDefinition(abiStr);
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
