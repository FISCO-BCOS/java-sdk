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
import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.SolidityFunction;
import org.fisco.bcos.sdk.transaction.codec.encode.FunctionEncoder;
import org.fisco.bcos.sdk.transaction.tools.AbiMatchHandler;
import org.fisco.bcos.sdk.transaction.tools.ArgsConvertHandler;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionBuilder implements FunctionBuilderInterface {
    protected static Logger log = LoggerFactory.getLogger(FunctionBuilder.class);

    private ContractLoader contractLoader;

    /** @param contractLoader */
    public FunctionBuilder(ContractLoader contractLoader) {
        super();
        this.contractLoader = contractLoader;
    }

    @Override
    public SolidityFunction buildFunction(
            String contractName, String contractAddress, String functionName, List<Object> args) {
        List<AbiDefinition> contractFunctions =
                contractLoader.getFunctionABIListByContractName(contractName);
        if (contractFunctions == null) {
            throw new RuntimeException("Unconfigured contract :" + contractName);
        }
        // Build function from java inputs
        return buildFunc(contractFunctions, functionName, args);
    }

    @Override
    public SolidityConstructor buildConstructor(String contractName, List<Object> args) {
        String bin = contractLoader.getBinaryByContractName(contractName);
        if (StringUtils.isEmpty(bin)) {
            throw new RuntimeException("bin not found");
        }
        // Encode constructor args
        String encodedConstructorArgs = encodeConstuctorArgs(contractName, args);
        // Build deploy transaction data
        String data = bin + encodedConstructorArgs;
        return new SolidityConstructor(
                contractName, args, bin, contractLoader.getABIByContractName(contractName), data);
    }

    public SolidityFunction buildFunc(
            List<AbiDefinition> contractFunctions, String functionName, List<Object> args) {

        if (args == null) args = Collections.EMPTY_LIST;
        // match possible definitions
        Stream<AbiDefinition> possibleDefinitions =
                AbiMatchHandler.matchPossibleDefinitions(contractFunctions, functionName, args);
        // match on build
        Iterator<AbiDefinition> iterator = possibleDefinitions.iterator();
        while (iterator.hasNext()) {
            AbiDefinition abiDefinition = iterator.next();
            List<Type> params = ArgsConvertHandler.tryConvertToSolArgs(args, abiDefinition);
            if (params == null) {
                log.debug(
                        "Skip abi definition for {}:{}, type not match",
                        abiDefinition.getName(),
                        abiDefinition.getInputs().size());
                continue;
            }
            if (params.size() != args.size()) {
                log.debug(
                        "Skip abi definition for {}:{}, arg size not match",
                        abiDefinition.getName(),
                        abiDefinition.getInputs().size());
                continue;
            }
            Function result = new Function(functionName, params, Collections.EMPTY_LIST);
            return new SolidityFunction(result, abiDefinition);
        }
        throw new RuntimeException("No matching args for function " + functionName);
    }

    public String encodeConstuctorArgs(String contractName, List<Object> args) {
        if (args == null) {
            args = Collections.EMPTY_LIST;
        }
        AbiDefinition abiDefinition = contractLoader.getConstructorABIByContractName(contractName);
        ensureValid(abiDefinition, args);
        List<Type> solArgs = ArgsConvertHandler.tryConvertToSolArgs(args, abiDefinition);
        return FunctionEncoder.encodeConstructor(solArgs);
    }

    private void ensureValid(AbiDefinition abiDefinition, List<Object> args) {
        // The case where no constructor is defined, abi is null
        if (abiDefinition == null && (CollectionUtils.isEmpty(args))) {
            return;
        }
        if (abiDefinition != null && abiDefinition.getInputs().size() == args.size()) {
            return;
        }
        throw new RuntimeException("Arguments size not match");
    }

    /** @return the contractLoader */
    public ContractLoader getContractLoader() {
        return contractLoader;
    }

    /** @param contractLoader the contractLoader to set */
    public void setContractLoader(ContractLoader contractLoader) {
        this.contractLoader = contractLoader;
    }
}
