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
package org.fisco.bcos.sdk.contract.precompiled.crud.common;

import java.util.HashMap;
import java.util.Map;

public class Condition {

    private Map<String, Map<ConditionOperator, String>> conditions;

    public Condition() {
        conditions = new HashMap<>();
    }

    public void EQ(String key, String value) {
        HashMap<ConditionOperator, String> map = new HashMap<ConditionOperator, String>();
        map.put(ConditionOperator.eq, value);
        conditions.put(key, map);
    }

    public void NE(String key, String value) {
        HashMap<ConditionOperator, String> map = new HashMap<ConditionOperator, String>();
        map.put(ConditionOperator.ne, value);
        conditions.put(key, map);
    }

    public void GT(String key, String value) {
        HashMap<ConditionOperator, String> map = new HashMap<ConditionOperator, String>();
        map.put(ConditionOperator.gt, value);
        conditions.put(key, map);
    }

    public void GE(String key, String value) {
        HashMap<ConditionOperator, String> map = new HashMap<ConditionOperator, String>();
        map.put(ConditionOperator.ge, value);
        conditions.put(key, map);
    }

    public void LT(String key, String value) {
        HashMap<ConditionOperator, String> map = new HashMap<ConditionOperator, String>();
        map.put(ConditionOperator.lt, value);
        conditions.put(key, map);
    }

    public void LE(String key, String value) {
        HashMap<ConditionOperator, String> map = new HashMap<ConditionOperator, String>();
        map.put(ConditionOperator.le, value);
        conditions.put(key, map);
    }

    public void Limit(int count) {
        Limit(0, count);
    }

    public void Limit(int offset, int count) {
        HashMap<ConditionOperator, String> map = new HashMap<ConditionOperator, String>();
        if (offset < 0) {
            offset = 0;
        }
        if (count < 0) {
            count = 0;
        }
        map.put(ConditionOperator.limit, offset + "," + count);
        conditions.put("limit", map);
    }

    public Map<String, Map<ConditionOperator, String>> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, Map<ConditionOperator, String>> conditions) {
        this.conditions = conditions;
    }
}
