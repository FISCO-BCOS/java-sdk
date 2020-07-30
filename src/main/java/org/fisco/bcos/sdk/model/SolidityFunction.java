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

import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.abi.datatypes.Function;

/**
 * Function @Description: SolidityFunction
 *
 * @author maojiayu
 * @data Jul 17, 2020 2:36:36 PM
 */
public class SolidityFunction {

    private Function function;

    private AbiDefinition functionAbi;

    /**
     * @param function
     * @param functionAbi
     */
    public SolidityFunction(Function function, AbiDefinition functionAbi) {
        super();
        this.function = function;
        this.functionAbi = functionAbi;
    }

    /** @return the function */
    public Function getFunction() {
        return function;
    }

    /** @param function the function to set */
    public void setFunction(Function function) {
        this.function = function;
    }

    /** @return the functionAbi */
    public AbiDefinition getFunctionAbi() {
        return functionAbi;
    }

    /** @param functionAbi the functionAbi to set */
    public void setFunctionAbi(AbiDefinition functionAbi) {
        this.functionAbi = functionAbi;
    }
}
