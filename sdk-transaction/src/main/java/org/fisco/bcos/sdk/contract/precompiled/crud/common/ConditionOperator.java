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

import java.math.BigInteger;

public enum ConditionOperator {
    eq(0),
    ne(1),
    gt(2),
    ge(3),
    lt(4),
    le(5);
    private final int value;

    private ConditionOperator(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public BigInteger getBigIntegerValue() {
        return BigInteger.valueOf(value);
    }
}
