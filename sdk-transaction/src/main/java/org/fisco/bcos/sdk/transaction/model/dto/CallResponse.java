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
package org.fisco.bcos.sdk.transaction.model.dto;

import java.util.List;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;

/**
 * CallResponse @Description: CallResponse
 *
 * @author maojiayu
 */
public class CallResponse extends CommonResponse {
    private String values;
    private List<Object> returnObject;
    private List<ABIObject> returnABIObject;

    /** @return the values */
    public String getValues() {
        return values;
    }

    /** @param values the values to set */
    public void setValues(String values) {
        this.values = values;
    }

    public List<Object> getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(List<Object> returnObject) {
        this.returnObject = returnObject;
    }

    public List<ABIObject> getReturnABIObject() {
        return returnABIObject;
    }

    public void setReturnABIObject(List<ABIObject> returnABIObject) {
        this.returnABIObject = returnABIObject;
    }
}
