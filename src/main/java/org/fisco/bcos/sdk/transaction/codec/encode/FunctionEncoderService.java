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
package org.fisco.bcos.sdk.transaction.codec.encode;

import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.tools.ArgsConvertHandler;
import org.fisco.bcos.sdk.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.SolidityFunction;

public class FunctionEncoderService implements FunctionEncoderInterface {
    private CryptoInterface cryptoInterface;
    private FunctionEncoder functionEncoder;

    public FunctionEncoderService(CryptoInterface cryptoInterface) {
        super();
        this.cryptoInterface = cryptoInterface;
        this.functionEncoder = new FunctionEncoder(cryptoInterface);
    }

    @Override
    public String encodeFunction(SolidityFunction solidityFunction) {
        return functionEncoder.encode(solidityFunction.getFunction());
    }

    @Override
    public String encodeConstructor(SolidityConstructor constructor) {
        if (CollectionUtils.isEmpty(constructor.getParams())) {
            constructor.setParams(Collections.EMPTY_LIST);
        }
        ABIDefinition ABIDefinition =
                ContractAbiUtil.getConstructorABIDefinition(constructor.getAbi());
        ensureValid(ABIDefinition, constructor.getParams());
        List<Type> solArgs =
                ArgsConvertHandler.tryConvertToSolArgs(constructor.getParams(), ABIDefinition);
        return functionEncoder.encodeConstructor(solArgs);
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

    /** @return the cryptoInterface */
    public CryptoInterface getCryptoInterface() {
        return cryptoInterface;
    }

    /** @param cryptoInterface the cryptoInterface to set */
    public void setCryptoInterface(CryptoInterface cryptoInterface) {
        this.cryptoInterface = cryptoInterface;
        this.functionEncoder = new FunctionEncoder(cryptoInterface);
    }

    /** @return the functionEncoder */
    public FunctionEncoder getFunctionEncoder() {
        return functionEncoder;
    }

    /** @param functionEncoder the functionEncoder to set */
    public void setFunctionEncoder(FunctionEncoder functionEncoder) {
        this.functionEncoder = functionEncoder;
    }
}
