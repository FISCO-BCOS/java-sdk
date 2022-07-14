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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;

public class Condition {

    private final Map<ConditionOperator, String> conditions;
    private TablePrecompiled.Limit limit;
    private String eqValue = "";

    public Condition() {
        conditions = new HashMap<>();
        limit = new TablePrecompiled.Limit();
    }

    public void GT(String value) {
        conditions.put(ConditionOperator.GT, value);
    }

    public void GE(String value) {
        conditions.put(ConditionOperator.GE, value);
    }

    public void LT(String value) {
        conditions.put(ConditionOperator.LT, value);
    }

    public void LE(String value) {
        conditions.put(ConditionOperator.LE, value);
    }

    public void EQ(String value) {
        eqValue = value;
    }

    public void setLimit(int offset, int count) {
        limit = new TablePrecompiled.Limit(offset, count);
    }

    public void setLimit(BigInteger offset, BigInteger count) {
        limit = new TablePrecompiled.Limit(offset, count);
    }

    public Map<ConditionOperator, String> getConditions() {
        return conditions;
    }

    public String getEqValue() {
        return eqValue;
    }

    public List<TablePrecompiled.Condition> getTableConditions() {
        List<TablePrecompiled.Condition> tableConditions = new ArrayList<>();
        conditions.forEach(
                (op, value) ->
                        tableConditions.add(
                                new TablePrecompiled.Condition(op.getBigIntValue(), value)));
        return tableConditions;
    }

    public TablePrecompiled.Limit getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "Condition{"
                + "conditions="
                + conditions
                + ", limit="
                + limit
                + ", eqValue='"
                + eqValue
                + '\''
                + '}';
    }

    public enum ConditionOperator {
        GT(0),
        GE(1),
        LT(2),
        LE(3),
        EQ(4);
        private final int value;

        private ConditionOperator(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public BigInteger getBigIntValue() {
            return BigInteger.valueOf(value);
        }

        @Override
        public String toString() {
            switch (value) {
                case 0:
                    return "GT";
                case 1:
                    return "GE";
                case 2:
                    return "LT";
                case 3:
                    return "LE";
                case 4:
                    return "EQ";
                default:
                    return "";
            }
        }
    }
}
