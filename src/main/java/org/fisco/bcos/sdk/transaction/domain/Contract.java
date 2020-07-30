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
package org.fisco.bcos.sdk.transaction.domain;

import java.math.BigInteger;
import org.fisco.bcos.sdk.abi.EventValues;
import org.fisco.bcos.sdk.abi.datatypes.Event;

public abstract class Contract {
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

    public static EventValues staticExtractEventParameters(Event event, Log log) {

        // TODO
        return null;
    }
}
