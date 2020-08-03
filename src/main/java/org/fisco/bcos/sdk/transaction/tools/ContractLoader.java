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
import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.transaction.model.CommonConstant;
import org.fisco.bcos.sdk.transaction.model.bo.AbiInfo;
import org.fisco.bcos.sdk.transaction.model.bo.BinInfo;
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
    private String path;
    private Map<String, List<ABIDefinition>> contractFuncAbis;
    private Map<String, ABIDefinition> contractConstructorAbi;
    private Map<String, String> contractBinMap;
    private Map<String, String> contractAbiMap;

    public ContractLoader(String path) throws Exception {
        this.path = path;
        // TODO readType
        this.binInfo();
        this.abiInfo();
    }

    public BinInfo binInfo() throws IOException {
        String[] s = {"bin"};
        Collection<File> fileCollection =
                FileUtils.listFiles(new File(path + "/" + CommonConstant.BIN), s, true);
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
        String[] s = {"abi"};
        Collection<File> fileCollection =
                FileUtils.listFiles(new File(path + "/" + CommonConstant.ABI), s, true);
        this.contractFuncAbis = new HashMap<>();
        this.contractConstructorAbi = new HashMap<>();
        this.contractAbiMap = new HashMap<>();
        for (File file : fileCollection) {
            String contract = parseContractName(file);
            List<ABIDefinition> abiList = parseAbiBody(file);
            ABIDefinition constructorAbi = selectConstructor(abiList);
            contractFuncAbis.put(contract, abiList);
            contractConstructorAbi.put(contract, constructorAbi);
            contractAbiMap.put(contract, FileUtils.readFileToString(file));
        }
        return new AbiInfo(contractFuncAbis, contractConstructorAbi);
    }

    private ABIDefinition selectConstructor(List<ABIDefinition> abiList) {
        for (ABIDefinition ABIDefinition : abiList) {
            if (ABIDefinition.getType().equals(CommonConstant.ABI_CONSTRUCTOR)) {
                return ABIDefinition;
            }
        }
        // The case where the sol file does not define constructor
        return null;
    }

    private String parseContractName(File file) {
        String fileName = file.getName();
        return StringUtils.substringBefore(fileName, ".");
    }

    private List<ABIDefinition> parseAbiBody(File file) throws Exception {
        String abiStr = FileUtils.readFileToString(file);
        return ContractAbiUtil.getFuncABIDefinition(abiStr);
    }

    public String getABIByContractName(String contractName) {
        return contractAbiMap.get(contractName);
    }

    public String getBinaryByContractName(String contractName) {
        return contractBinMap.get(contractName);
    }

    public Pair<String, String> getABIAndBinaryByContractName(String contractName) {
        return Pair.of(contractAbiMap.get(contractName), contractBinMap.get(contractName));
    }

    public ABIDefinition getConstructorABIByContractName(String contractName) {
        return selectConstructor(getFunctionABIListByContractName(contractName));
    }

    public List<ABIDefinition> getFunctionABIListByContractName(String contractName) {
        return contractFuncAbis.get(contractName);
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
