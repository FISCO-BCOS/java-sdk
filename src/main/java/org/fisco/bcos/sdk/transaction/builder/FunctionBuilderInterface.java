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

import java.util.List;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.SolidityFunction;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.tools.ContractLoader;

/**
 * FunctionBuilderInterface @Description: FunctionBuilderInterface
 *
 * @author maojiayu
 * @data Jul 17, 2020 2:34:36 PM
 */
public interface FunctionBuilderInterface {

    public SolidityFunction buildFunction(
            String contractName, String functionName, List<Object> args)
            throws TransactionBaseException;

    public SolidityFunction buildFunctionByAbi(String abi, String functionName, List<Object> args);

    public SolidityFunction buildFunctionByABIDefinitionList(
            List<ABIDefinition> definitions, String functionName, List<Object> args);

    public SolidityConstructor buildConstructor(String contractName, List<Object> args)
            throws TransactionBaseException;

    public SolidityConstructor buildConstructor(
            String abi, String bin, String contractName, List<Object> args);

    public ContractLoader getContractLoader();

    public void setContractLoader(ContractLoader contractLoader);
}
