/*
 * Copyright 2012-2019 the original author or authors.
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
 */

package org.fisco.bcos.sdk.abi.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.abi.EventValues;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition.NamedType;
import org.fisco.bcos.sdk.model.TransactionReceipt.Logs;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
import org.fisco.bcos.sdk.transaction.model.po.Contract;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ContractAbiUtil. */
public class ContractAbiUtil {

    private static final Logger logger = LoggerFactory.getLogger(ContractAbiUtil.class);

    public static final String TYPE_CONSTRUCTOR = "constructor";
    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_EVENT = "event";

    /**
     * @param contractAbi
     * @return
     */
    public static ABIDefinition getConstructorABIDefinition(String contractAbi) {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            ABIDefinition[] ABIDefinitions =
                    objectMapper.readValue(contractAbi, ABIDefinition[].class);

            for (ABIDefinition ABIDefinition : ABIDefinitions) {
                if (TYPE_CONSTRUCTOR.equals(ABIDefinition.getType())) {
                    return ABIDefinition;
                }
            }
        } catch (JsonProcessingException e) {
            logger.warn(" invalid  json, abi: {}, e: {} ", contractAbi, e);
        }
        return null;
    }

    /**
     * @param contractAbi
     * @return
     */
    public static List<ABIDefinition> getFuncABIDefinition(String contractAbi) {
        List<ABIDefinition> result = new ArrayList<>();
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            ABIDefinition[] ABIDefinitions =
                    objectMapper.readValue(contractAbi, ABIDefinition[].class);

            for (ABIDefinition ABIDefinition : ABIDefinitions) {
                if (TYPE_FUNCTION.equals(ABIDefinition.getType())
                        || TYPE_CONSTRUCTOR.equals(ABIDefinition.getType())) {
                    result.add(ABIDefinition);
                }
            }
        } catch (JsonProcessingException e) {
            logger.warn(" invalid json, abi: {}, e: {} ", contractAbi, e);
        }
        return result;
    }

    /**
     * @param contractAbi
     * @return
     */
    public static List<ABIDefinition> getEventABIDefinitions(String contractAbi) {

        List<ABIDefinition> result = new ArrayList<>();
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            ABIDefinition[] ABIDefinitions =
                    objectMapper.readValue(contractAbi, ABIDefinition[].class);

            for (ABIDefinition ABIDefinition : ABIDefinitions) {
                if (TYPE_EVENT.equals(ABIDefinition.getType())) {
                    result.add(ABIDefinition);
                }
            }
        } catch (JsonProcessingException e) {
            logger.warn(" invalid json, abi: {}, e: {} ", contractAbi, e);
        }
        return result;
    }

    /**
     * @param ABIDefinition
     * @return
     */
    public static List<String> getFuncInputType(ABIDefinition ABIDefinition) {
        List<String> inputList = new ArrayList<>();
        if (ABIDefinition != null) {
            List<NamedType> inputs = ABIDefinition.getInputs();
            for (NamedType input : inputs) {
                inputList.add(input.getType());
            }
        }
        return inputList;
    }

    /**
     * @param ABIDefinition
     * @return
     */
    public static List<String> getFuncOutputType(ABIDefinition ABIDefinition) {
        List<String> outputList = new ArrayList<>();
        List<NamedType> outputs = ABIDefinition.getOutputs();
        for (NamedType output : outputs) {
            outputList.add(output.getType());
        }
        return outputList;
    }

    /**
     * @param paramTypes
     * @return
     * @throws TransactionBaseException
     */
    public static List<TypeReference<?>> paramFormat(List<NamedType> paramTypes)
            throws TransactionBaseException {
        List<TypeReference<?>> finalOutputs = new ArrayList<>();

        for (int i = 0; i < paramTypes.size(); i++) {

            ABIDefinition.Type type = new ABIDefinition.Type(paramTypes.get(i).getType());
            // nested array , not support now.
            if (type.getDimensions().size() > 1) {
                throw new TransactionBaseException(
                        201202,
                        String.format("type:%s unsupported array decoding", type.getType()));
            }

            TypeReference<?> typeReference = null;
            if (type.isDynamicList()) {
                typeReference =
                        DynamicArrayReference.create(
                                type.getRawType(), paramTypes.get(i).isIndexed());
            } else if (type.isFixedList()) {
                typeReference =
                        StaticArrayReference.create(
                                type.getRawType(),
                                type.getLastDimension(),
                                paramTypes.get(i).isIndexed());
            } else {
                typeReference =
                        TypeReference.create(
                                ContractTypeUtil.getType(paramTypes.get(i).getType()),
                                paramTypes.get(i).isIndexed());
            }

            finalOutputs.add(typeReference);
        }
        return finalOutputs;
    }

    /**
     * @param log
     * @param ABIDefinition
     * @return
     * @throws TransactionBaseException
     */
    public static EventValues decodeEvent(Logs log, ABIDefinition ABIDefinition)
            throws TransactionBaseException {

        List<TypeReference<?>> finalOutputs = paramFormat(ABIDefinition.getInputs());
        Event event = new Event(ABIDefinition.getName(), finalOutputs);
        EventValues eventValues = Contract.staticExtractEventParameters(event, log);
        return eventValues;
    }

    /**
     * Convert NamedType to TypeReference which refs to class of Solidity type(Address, Uint256,
     * etc..)
     *
     * @param solTypeDef
     * @return
     * @throws TransactionBaseException
     */
    public static TypeReference<?> paramInput(ABIDefinition.NamedType solTypeDef)
            throws TransactionBaseException {
        ABIDefinition.Type type = new ABIDefinition.Type(solTypeDef.getType());
        // nested array , not support now.
        if (type.getDimensions().size() > 1) {
            throw new TransactionBaseException(
                    201202, String.format("type:%s unsupported array decoding", type.getType()));
        }

        TypeReference<?> typeReference = null;
        if (type.isDynamicList()) {
            typeReference = DynamicArrayReference.create(type.getRawType(), solTypeDef.isIndexed());
        } else if (type.isFixedList()) {
            typeReference =
                    StaticArrayReference.create(
                            type.getRawType(), type.getLastDimension(), solTypeDef.isIndexed());
        } else {
            typeReference =
                    TypeReference.create(
                            ContractTypeUtil.getType(solTypeDef.getType()), solTypeDef.isIndexed());
        }
        return typeReference;
    }

    public static Type resolveArrayBasicType(TypeReference<?> typeReference) {
        java.lang.reflect.Type typeRefGenericClass =
                typeReference.getClass().getGenericSuperclass();
        ParameterizedType arrayType =
                (ParameterizedType)
                        ((ParameterizedType) typeRefGenericClass).getActualTypeArguments()[0];
        java.lang.reflect.Type elementType = (arrayType).getActualTypeArguments()[0];
        return elementType;
    }
}
