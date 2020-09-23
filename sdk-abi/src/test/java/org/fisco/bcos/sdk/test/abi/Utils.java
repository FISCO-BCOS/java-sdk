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

package org.fisco.bcos.sdk.test.abi;

import org.fisco.bcos.sdk.abi.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.CryptoType;

public class Utils {

    public static CryptoSuite getCryptoSuite() {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }

    public static ContractABIDefinition getContractABIDefinition(String abiDesc) {
        CryptoSuite cryptoSuite = getCryptoSuite();
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(cryptoSuite);
        return abiDefinitionFactory.loadABI(abiDesc);
    }
}
