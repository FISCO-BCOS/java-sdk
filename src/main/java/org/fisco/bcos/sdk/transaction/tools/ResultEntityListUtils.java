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

import com.google.common.collect.Lists;
import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition.NamedType;
import org.fisco.bcos.sdk.transaction.model.bo.ResultEntity;

/**
 * ResultEntityListUtils @Description: ResultEntityListUtils
 *
 * @author maojiayu
 * @data Aug 13, 2020 10:05:05 PM
 */
public class ResultEntityListUtils {

    @SuppressWarnings("rawtypes")
    public static List<ResultEntity> forward(List<NamedType> abiNamedTypes, List<Type> resultType) {
        List<ResultEntity> resultList = Lists.newArrayListWithExpectedSize(abiNamedTypes.size());
        for (int i = 0; i < abiNamedTypes.size(); i++) {
            resultList.add(
                    new ResultEntity(
                            abiNamedTypes.get(i).getName(),
                            abiNamedTypes.get(i).getType(),
                            resultType.get(i)));
        }
        return resultList;
    }
}
