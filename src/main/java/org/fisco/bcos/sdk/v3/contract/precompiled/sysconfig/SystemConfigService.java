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
package org.fisco.bcos.sdk.v3.contract.precompiled.sysconfig;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class SystemConfigService {
    private final SystemConfigPrecompiled systemConfigPrecompiled;
    public static final String TX_COUNT_LIMIT = "tx_count_limit";
    public static final String TX_GAS_LIMIT = "tx_gas_limit";
    public static final String CONSENSUS_PERIOD = "consensus_leader_period";
    public static final String AUTH_STATUS = "auth_status";
    public static final int TX_GAS_LIMIT_MIN = 100000;
    private static final Map<String, Predicate<BigInteger>> predicateMap = new HashMap<>();

    static {
        predicateMap.put(TX_COUNT_LIMIT, value -> value.compareTo(BigInteger.ONE) >= 0);
        predicateMap.put(CONSENSUS_PERIOD, value -> value.compareTo(BigInteger.ONE) >= 0);
        predicateMap.put(AUTH_STATUS, value -> value.compareTo(BigInteger.ONE) >= 0);
        predicateMap.put(
                TX_GAS_LIMIT, value -> value.compareTo(BigInteger.valueOf(TX_GAS_LIMIT_MIN)) >= 0);
    }

    public SystemConfigService(Client client, CryptoKeyPair credential) {
        this.systemConfigPrecompiled =
                SystemConfigPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.SYS_CONFIG_PRECOMPILED_NAME
                                : PrecompiledAddress.SYS_CONFIG_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    public RetCode setValueByKey(String key, String value) throws ContractException {
        TransactionReceipt receipt = systemConfigPrecompiled.setValueByKey(key, value);
        return ReceiptParser.parseTransactionReceipt(
                receipt, tr -> systemConfigPrecompiled.getSetValueByKeyOutput(receipt).getValue1());
    }

    public static boolean checkSysNumberValueValidation(String key, String value) {
        Predicate<BigInteger> valuePredicate = predicateMap.get(key);
        if (valuePredicate == null) {
            return true;
        }
        BigInteger sysValue;
        try {
            sysValue = new BigInteger(value);
        } catch (NumberFormatException ignored) {
            return false;
        }
        return valuePredicate.test(sysValue);
    }

    public static boolean isCheckableInValueValidation(String key) {
        return predicateMap.containsKey(key);
    }
}
