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
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
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
     * @return the abi definition
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
}
