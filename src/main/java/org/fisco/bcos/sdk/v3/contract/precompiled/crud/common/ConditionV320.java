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
package org.fisco.bcos.sdk.v3.contract.precompiled.crud.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;

public class ConditionV320 extends Condition {
    private final Map<Integer, Map<ConditionOperator, String>> conditions;

    public ConditionV320() {
        super();
        conditions = new HashMap<>();
    }

    public void EQ(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.EQ, value);
    }

    public void NE(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.NE, value);
    }

    public void GT(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.GT, value);
    }

    public void GE(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.GE, value);
    }

    public void LT(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.LT, value);
    }

    public void LE(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.LE, value);
    }

    public void STARTS_WITH(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.STARTS_WITH, value);
    }

    public void ENDS_WITH(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.ENDS_WITH, value);
    }

    public void CONTAINS(int field, String value) {
        conditions.putIfAbsent(field, new EnumMap<>(ConditionOperator.class));
        conditions.get(field).put(ConditionOperator.CONTAINS, value);
    }

    @Override
    public Map getConditions() {
        return conditions;
    }

    @Override
    public List getTableConditions() {
        List<TablePrecompiled.ConditionV320> tableConditions = new ArrayList<>();
        conditions.forEach(
                (field, map) ->
                        map.forEach(
                                (op, value) ->
                                        tableConditions.add(
                                                new TablePrecompiled.ConditionV320(
                                                        BigInteger.valueOf(field),
                                                        op.getBigIntValue(),
                                                        value))));
        return tableConditions;
    }

    @Override
    public String toString() {
        return "ConditionV320{" + "conditions=" + conditions + ", limit=" + getLimit() + '\'' + '}';
    }
}
