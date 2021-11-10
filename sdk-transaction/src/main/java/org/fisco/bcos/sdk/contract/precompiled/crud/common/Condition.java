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

import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.contract.precompiled.crud.TablePrecompiled;

public class Condition {

    private List<TablePrecompiled.CompareTriple> conditions;

    public Condition() {
        conditions = new ArrayList<>();
    }

    public void EQ(String key, String value) {
        TablePrecompiled.CompareTriple compareTriple =
                new TablePrecompiled.CompareTriple(
                        key, value, ConditionOperator.eq.getBigIntegerValue());
        conditions.add(compareTriple);
    }

    public void NE(String key, String value) {
        TablePrecompiled.CompareTriple compareTriple =
                new TablePrecompiled.CompareTriple(
                        key, value, ConditionOperator.ne.getBigIntegerValue());
        conditions.add(compareTriple);
    }

    public void GT(String key, String value) {
        TablePrecompiled.CompareTriple compareTriple =
                new TablePrecompiled.CompareTriple(
                        key, value, ConditionOperator.gt.getBigIntegerValue());
        conditions.add(compareTriple);
    }

    public void GE(String key, String value) {
        TablePrecompiled.CompareTriple compareTriple =
                new TablePrecompiled.CompareTriple(
                        key, value, ConditionOperator.ge.getBigIntegerValue());
        conditions.add(compareTriple);
    }

    public void LT(String key, String value) {
        TablePrecompiled.CompareTriple compareTriple =
                new TablePrecompiled.CompareTriple(
                        key, value, ConditionOperator.lt.getBigIntegerValue());
        conditions.add(compareTriple);
    }

    public void LE(String key, String value) {
        TablePrecompiled.CompareTriple compareTriple =
                new TablePrecompiled.CompareTriple(
                        key, value, ConditionOperator.le.getBigIntegerValue());
        conditions.add(compareTriple);
    }

    public TablePrecompiled.Condition getConditions() {
        return new TablePrecompiled.Condition(conditions);
    }

    public void setConditions(List<TablePrecompiled.CompareTriple> conditions) {
        this.conditions = conditions;
    }
}
