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
package org.fisco.bcos.sdk.transaction.builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.tools.AbiMatchHandler;
import org.fisco.bcos.sdk.abi.tools.ArgsConvertHandler;
import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.SolidityFunction;
import org.fisco.bcos.sdk.transaction.model.dto.ResultCodeEnum;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionBuilderService implements FunctionBuilderInterface {
    protected static Logger log = LoggerFactory.getLogger(FunctionBuilderService.class);

    private ContractLoader contractLoader;

    public FunctionBuilderService() {
        super();
        contractLoader = null;
    }

    public FunctionBuilderService(ContractLoader contractLoader) {
        super();
        this.contractLoader = contractLoader;
    }

    @Override
    public SolidityFunction buildFunction(
            String contractName, String functionName, List<Object> args)
            throws TransactionBaseException {
        if (contractLoader == null) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(), "contractLoader cann't be null");
        }
        List<ABIDefinition> definitions =
                contractLoader.getFunctionABIListByContractName(contractName);
        return buildFunctionByABIDefinitionList(definitions, functionName, args);
    }

    @Override
    public SolidityFunction buildFunctionByAbi(String abi, String functionName, List<Object> args) {
        List<ABIDefinition> definitions = ContractAbiUtil.getFuncABIDefinition(abi);
        return buildFunctionByABIDefinitionList(definitions, functionName, args);
    }

    @Override
    public SolidityFunction buildFunctionByABIDefinitionList(
            List<ABIDefinition> definitions, String functionName, List<Object> args) {
        if (definitions == null) {
            throw new RuntimeException("Unconfigured contract functionName :" + functionName);
        }
        // Build function from java inputs
        return buildFunc(definitions, functionName, args);
    }

    @Override
    public SolidityConstructor buildConstructor(String contractName, List<Object> args)
            throws TransactionBaseException {
        if (contractLoader == null) {
            throw new TransactionBaseException(
                    ResultCodeEnum.PARAMETER_ERROR.getCode(), "contractLoader cann't be null");
        }
        String bin = contractLoader.getBinaryByContractName(contractName);
        if (StringUtils.isEmpty(bin)) {
            throw new RuntimeException("bin not found");
        }
        return buildConstructor(
                contractLoader.getABIByContractName(contractName), bin, contractName, args);
    }

    @Override
    public SolidityConstructor buildConstructor(
            String abi, String bin, String contractName, List<Object> args) {
        String encodedConstructorArgs = encodeConstuctorArgs(abi, args);
        // Build deploy transaction data
        String data = bin + encodedConstructorArgs;
        return new SolidityConstructor(contractName, args, bin, abi, data);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SolidityFunction buildFunc(
            List<ABIDefinition> contractFunctions, String functionName, List<Object> args) {

        if (args == null) args = Collections.EMPTY_LIST;
        // match possible definitions
        Stream<ABIDefinition> possibleDefinitions =
                AbiMatchHandler.matchPossibleDefinitions(contractFunctions, functionName, args);
        // match on build
        Iterator<ABIDefinition> iterator = possibleDefinitions.iterator();
        while (iterator.hasNext()) {
            ABIDefinition ABIDefinition = iterator.next();
            List<Type> params = ArgsConvertHandler.tryConvertToSolArgs(args, ABIDefinition);
            if (params == null) {
                log.debug(
                        "Skip abi definition for {}:{}, type not match",
                        ABIDefinition.getName(),
                        ABIDefinition.getInputs().size());
                continue;
            }
            if (params.size() != args.size()) {
                log.debug(
                        "Skip abi definition for {}:{}, arg size not match",
                        ABIDefinition.getName(),
                        ABIDefinition.getInputs().size());
                continue;
            }
            Function result = new Function(functionName, params, Collections.EMPTY_LIST);
            return new SolidityFunction(result, ABIDefinition);
        }
        throw new RuntimeException("No matching args for function " + functionName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String encodeConstuctorArgs(String abi, List<Object> args) {
        if (args == null) {
            args = Collections.EMPTY_LIST;
        }
        List<ABIDefinition> abiList = ContractAbiUtil.getFuncABIDefinition(abi);
        ABIDefinition ABIDefinition = ContractLoader.selectConstructor(abiList);
        ensureValid(ABIDefinition, args);
        List<Type> solArgs = ArgsConvertHandler.tryConvertToSolArgs(args, ABIDefinition);
        return FunctionEncoder.encodeConstructor(solArgs);
    }

    private void ensureValid(ABIDefinition ABIDefinition, List<Object> args) {
        // The case where no constructor is defined, abi is null
        if (ABIDefinition == null && (CollectionUtils.isEmpty(args))) {
            return;
        }
        if (ABIDefinition != null && ABIDefinition.getInputs().size() == args.size()) {
            return;
        }
        throw new RuntimeException("Arguments size not match");
    }

    @Override
    public ContractLoader getContractLoader() {
        return contractLoader;
    }

    @Override
    public void setContractLoader(ContractLoader contractLoader) {
        this.contractLoader = contractLoader;
    }
}
