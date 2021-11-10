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
package org.fisco.bcos.sdk.contract.precompiled.sysconfig;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

public class SystemConfigService {
    private final SystemConfigPrecompiled systemConfigPrecompiled;

    public SystemConfigService(Client client, CryptoKeyPair credential) {
        this.systemConfigPrecompiled =
                SystemConfigPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.SYSCONFIG_PRECOMPILED_NAME
                                : PrecompiledAddress.SYSCONFIG_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    public RetCode setValueByKey(String key, String value) throws ContractException {
        return ReceiptParser.parseTransactionReceipt(
                systemConfigPrecompiled.setValueByKey(key, value));
    }
}
