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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupInfo.GroupInfo;
import org.fisco.bcos.sdk.v3.client.protocol.response.BcosGroupNodeInfo;
import org.fisco.bcos.sdk.v3.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemConfigService {
    private final SystemConfigPrecompiled systemConfigPrecompiled;
    private final Client client;
    public static final String TX_COUNT_LIMIT = "tx_count_limit";
    public static final String TX_GAS_LIMIT = "tx_gas_limit";
    public static final String CONSENSUS_PERIOD = "consensus_leader_period";
    public static final String AUTH_STATUS = "auth_check_status";
    public static final String COMPATIBILITY_VERSION = "compatibility_version";
    public static final int TX_GAS_LIMIT_MIN = 100000;
    private static final Map<String, Predicate<BigInteger>> predicateMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigService.class);

    static {
        predicateMap.put(TX_COUNT_LIMIT, value -> value.compareTo(BigInteger.ONE) >= 0);
        predicateMap.put(CONSENSUS_PERIOD, value -> value.compareTo(BigInteger.ONE) >= 0);
        predicateMap.put(AUTH_STATUS, value -> value.compareTo(BigInteger.ZERO) >= 0);
        predicateMap.put(
                TX_GAS_LIMIT, value -> value.compareTo(BigInteger.valueOf(TX_GAS_LIMIT_MIN)) >= 0);
    }

    public SystemConfigService(Client client, CryptoKeyPair credential) {
        this.client = client;
        this.systemConfigPrecompiled =
                SystemConfigPrecompiled.load(
                        client.isWASM()
                                ? PrecompiledAddress.SYS_CONFIG_PRECOMPILED_NAME
                                : PrecompiledAddress.SYS_CONFIG_PRECOMPILED_ADDRESS,
                        client,
                        credential);
    }

    public RetCode setValueByKey(String key, String value) throws ContractException {
        if (COMPATIBILITY_VERSION.equals(key) && !checkCompatibilityVersion(client, value)) {
            String nodeVersionString =
                    client.getGroupInfo().getResult().getNodeList().stream()
                            .map(node -> node.getIniConfig().getBinaryInfo().getVersion())
                            .collect(Collectors.joining(","));
            throw new ContractException(
                    "The compatibility version "
                            + value
                            + " is not supported, please check the version of the chain. (The version of the chain is "
                            + nodeVersionString
                            + ")");
        }
        if (!checkAvailableFeatureKeys(client, key)) {
            throw new ContractException("Unsupported feature key: [" + key + "]");
        }

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

    static boolean checkAvailableFeatureKeys(Client client, String key) {
        if (!(key.startsWith("bugfix") || key.startsWith("feature"))) {
            return true;
        }

        Optional<GroupInfo> group =
                client.getGroupInfoList().getResult().stream()
                        .filter(
                                groupInfo -> {
                                    return groupInfo.getGroupID().equals(client.getGroup());
                                })
                        .findFirst();
        if (!group.isPresent()) {
            logger.warn(
                    "Not found group! {} {}",
                    client.getGroup(),
                    client.getGroupInfoList().getResult());
            return true;
        }

        for (BcosGroupNodeInfo.GroupNodeInfo groupNodeInfo : group.get().getNodeList()) {
            List<String> featureKeys = groupNodeInfo.getFeatureKeys();

            if (featureKeys == null) {
                if (groupNodeInfo.getProtocol().getCompatibilityVersion()
                        == EnumNodeVersion.BCOS_3_2_3.toVersionObj().toCompatibilityVersion()) {
                    featureKeys = Arrays.asList("bugfix_revert");
                } else {
                    return false;
                }
            }

            if (!featureKeys.contains(key)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCheckableInValueValidation(String key) {
        return predicateMap.containsKey(key);
    }

    public static boolean checkCompatibilityVersion(Client client, String version) {
        try {
            EnumNodeVersion.Version setVersion = EnumNodeVersion.getClassVersion(version);
            List<BcosGroupNodeInfo.GroupNodeInfo> nodeList =
                    client.getGroupInfo().getResult().getNodeList();
            return nodeList.stream()
                    .allMatch(
                            node ->
                                    setVersion.compareTo(
                                                    EnumNodeVersion.getClassVersion(
                                                            node.getIniConfig()
                                                                    .getBinaryInfo()
                                                                    .getVersion()))
                                            <= 0);
        } catch (Exception e) {
            return false;
        }
    }
}
