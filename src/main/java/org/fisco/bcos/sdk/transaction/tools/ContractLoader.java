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
import org.fisco.bcos.sdk.transaction.model.exception.NoSuchTransactionFileException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionRetCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContractLoader @Description: ContractLoader
 *
 * @author maojiayu
 */
public class ContractLoader {
    private static final Logger log = LoggerFactory.getLogger(ContractLoader.class);
    private Map<String, List<ABIDefinition>> contractFuncAbis;
    private Map<String, ABIDefinition> contractConstructorAbi;
    private Map<String, String> contractBinMap;
    private Map<String, String> contractAbiMap;

    public ContractLoader(String abiFilePath, String binaryFilePath) throws Exception {
        this.binInfo(binaryFilePath);
        this.abiInfo(abiFilePath);
    }

    public BinInfo binInfo(String binaryFilePath) throws IOException {
        String[] s = {"bin"};
        Collection<File> fileCollection = FileUtils.listFiles(new File(binaryFilePath), s, true);
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

    public AbiInfo abiInfo(String abiFilePath) throws Exception {
        String[] s = {"abi"};
        Collection<File> fileCollection = FileUtils.listFiles(new File(abiFilePath), s, true);
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

    public static ABIDefinition selectConstructor(List<ABIDefinition> abiList) {
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

    public String getABIByContractName(String contractName) throws NoSuchTransactionFileException {
        if (contractAbiMap.get(contractName) == null) {
            log.error("Contract {} not found.", contractName);
            throw new NoSuchTransactionFileException(TransactionRetCodeConstants.NO_SUCH_ABI_FILE);
        }
        return contractAbiMap.get(contractName);
    }

    public String getBinaryByContractName(String contractName)
            throws NoSuchTransactionFileException {
        if (contractBinMap.get(contractName) == null) {
            log.error("Contract {} not found.", contractName);
            throw new NoSuchTransactionFileException(
                    TransactionRetCodeConstants.NO_SUCH_BINARY_FILE);
        }
        return contractBinMap.get(contractName);
    }

    public Pair<String, String> getABIAndBinaryByContractName(String contractName)
            throws NoSuchTransactionFileException {
        if (contractAbiMap.get(contractName) == null) {
            log.error("Contract {} not found.", contractName);
            throw new NoSuchTransactionFileException(TransactionRetCodeConstants.NO_SUCH_ABI_FILE);
        }
        if (contractBinMap.get(contractName) == null) {
            log.error("Contract {} not found.", contractName);
            throw new NoSuchTransactionFileException(
                    TransactionRetCodeConstants.NO_SUCH_BINARY_FILE);
        }
        return Pair.of(contractAbiMap.get(contractName), contractBinMap.get(contractName));
    }

    public ABIDefinition getConstructorABIByContractName(String contractName)
            throws NoSuchTransactionFileException {
        return selectConstructor(getFunctionABIListByContractName(contractName));
    }

    public List<ABIDefinition> getFunctionABIListByContractName(String contractName)
            throws NoSuchTransactionFileException {
        if (contractFuncAbis.get(contractName) == null) {
            log.error("Contract {} not found.", contractName);
            throw new NoSuchTransactionFileException(TransactionRetCodeConstants.NO_SUCH_ABI_FILE);
        }
        return contractFuncAbis.get(contractName);
    }
}
