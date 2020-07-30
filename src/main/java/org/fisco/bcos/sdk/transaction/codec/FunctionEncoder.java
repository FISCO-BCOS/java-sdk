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
package org.fisco.bcos.sdk.transaction.codec;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.abi.TypeEncoder;
import org.fisco.bcos.sdk.abi.Utils;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Uint;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.model.SolidityConstructor;
import org.fisco.bcos.sdk.model.SolidityFunction;
import org.fisco.bcos.sdk.transaction.tools.ArgsConvertHandler;
import org.fisco.bcos.sdk.transaction.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.utils.Numeric;

public class FunctionEncoder implements FunctionEncoderInterface {
    private Hash hashTool;

    public FunctionEncoder(Hash hashTool) {
        super();
        this.hashTool = hashTool;
    }

    @Override
    public String encodeFunction(SolidityFunction solidityFunction) {
        return encode(solidityFunction.getFunction());
    }

    @Override
    public String encodeConstructor(SolidityConstructor constructor) {
        if (CollectionUtils.isEmpty(constructor.getParams())) {
            constructor.setParams(Collections.EMPTY_LIST);
        }
        AbiDefinition abiDefinition = ContractAbiUtil.getConstructorAbiDefinition(constructor.getAbi());
        ensureValid(abiDefinition, constructor.getParams());
        List<Type> solArgs = ArgsConvertHandler.tryConvertToSolArgs(constructor.getParams(), abiDefinition);
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

    public String encode(Function function) {
        List<Type> parameters = function.getInputParameters();
        String methodSignature = buildMethodSignature(function.getName(), parameters);
        String methodId = buildMethodId(methodSignature);

        StringBuilder result = new StringBuilder();
        result.append(methodId);

        return encodeParameters(parameters, result);
    }

    public static String encodeConstructor(List<Type> parameters) {
        return encodeParameters(parameters, new StringBuilder());
    }

    public static String encodeParameters(List<Type> parameters, StringBuilder result) {
        int dynamicDataOffset = Utils.getLength(parameters) * Type.MAX_BYTE_LENGTH;
        StringBuilder dynamicData = new StringBuilder();

        for (Type parameter : parameters) {
            String encodedValue = TypeEncoder.encode(parameter);

            if (parameter.dynamicType()) {
                String encodedDataOffset = TypeEncoder.encodeNumeric(new Uint(BigInteger.valueOf(dynamicDataOffset)));
                result.append(encodedDataOffset);
                dynamicData.append(encodedValue);
                dynamicDataOffset += (encodedValue.length() >> 1);
            } else {
                result.append(encodedValue);
            }
        }
        result.append(dynamicData);

        return result.toString();
    }

    static String buildMethodSignature(String methodName, List<Type> parameters) {
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        result.append("(");
        String params = parameters.stream().map(Type::getTypeAsString).collect(Collectors.joining(","));
        result.append(params);
        result.append(")");
        return result.toString();
    }

    public String buildMethodId(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = hashTool.hash(input);
        return Numeric.toHexString(hash).substring(0, 10);
    }

    /**
     * @return the hash
     */
    public Hash getHash() {
        return hashTool;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(Hash hash) {
        this.hashTool = hash;
    }
}
