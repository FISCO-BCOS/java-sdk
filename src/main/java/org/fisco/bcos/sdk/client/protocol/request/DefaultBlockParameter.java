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

package org.fisco.bcos.sdk.client.protocol.request;

import java.math.BigInteger;

public interface DefaultBlockParameter {
    static DefaultBlockParameter valueOf(BigInteger blockNumber) {
        return new DefaultBlockParameterNumber(blockNumber);
    }

    static DefaultBlockParameter valueOf(String blockName) {
        return DefaultBlockParameterName.fromString(blockName);
    }

    String getValue();
}
