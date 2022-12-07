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

import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.precompiled.crud.TablePrecompiled;

public class Condition {
    private final List<Tuple3<ConditionOperator, BigInteger, String>> conditions;
    //    private final Map<ConditionOperator, String> conditions;
    private TablePrecompiled.Limit limit;

    public Condition() {
        conditions = new ArrayList<>();
        limit = new TablePrecompiled.Limit();
    }

    public void EQ(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.EQ, BigInteger.valueOf(field), value));
    }

    public void NE(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.NE, BigInteger.valueOf(field), value));
    }

    public void GT(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.GT, BigInteger.valueOf(field), value));
    }

    public void GE(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.GE, BigInteger.valueOf(field), value));
    }

    public void LT(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.LT, BigInteger.valueOf(field), value));
    }

    public void LE(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.LE, BigInteger.valueOf(field), value));
    }

    public void STARTS_WITH(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.STARTS_WITH, BigInteger.valueOf(field), value));
    }

    public void ENDS_WITH(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.ENDS_WITH, BigInteger.valueOf(field), value));
    }

    public void CONTAINS(int field, String value) {
        conditions.add(new Tuple3<>(ConditionOperator.CONTAINS, BigInteger.valueOf(field), value));
    }

    public void setLimit(int offset, int count) {
        limit = new TablePrecompiled.Limit(offset, count);
    }

    public void setLimit(BigInteger offset, BigInteger count) {
        limit = new TablePrecompiled.Limit(offset, count);
    }

    public List<Tuple3<ConditionOperator, BigInteger, String>> getConditions() {
        return conditions;
    }

    public List<TablePrecompiled.Condition> getTableConditions() {
        List<TablePrecompiled.Condition> tableConditions = new ArrayList<>();
        conditions.forEach(
                tuple3 ->
                        tableConditions.add(
                                new TablePrecompiled.Condition(tuple3.getValue1().getBigIntValue(),
                                        tuple3.getValue2(),
                                        tuple3.getValue3())));
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
                + '\''
                + '}';
    }

    public enum ConditionOperator {
        EQ(0),
        NE(1),
        GT(2),
        GE(3),
        LT(4),
        LE(5),
        STARTS_WITH(6),
        ENDS_WITH(7),
        CONTAINS(8);
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
                    return "EQ";
                case 1:
                    return "NE";
                case 2:
                    return "GT";
                case 3:
                    return "GE";
                case 4:
                    return "LT";
                case 5:
                    return "LE";
                case 6:
                    return "STARTS_WITH";
                case 7:
                    return "ENDS_WITH";
                case 8:
                    return "CONTAINS";
                default:
                    return "";
            }
        }
    }
}
