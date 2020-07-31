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
package org.fisco.bcos.sdk.transaction.codec.decode;

import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.transaction.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;

public class FunctionReturnParamsDecoder implements FunctionReturnParamsDecoderInterface {

    @Override
    public List<Type> decode(String rawInput, String abi) throws TransactionBaseException {
        AbiDefinition ad = JsonUtils.fromJson(abi, AbiDefinition.class);
        List<TypeReference<Type>> list =
                ContractAbiUtil.paramFormat(ad.getOutputs())
                        .stream()
                        .map(l -> (TypeReference<Type>) l)
                        .collect(Collectors.toList());
        return FunctionReturnDecoder.decode(rawInput, list);
    }

    @Override
    public String decodeCall(String rawInput, String abi) throws TransactionBaseException {
        return JsonUtils.toJson(decode(rawInput, abi));
    }
}
