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
package org.fisco.bcos.sdk.contract.precompiled.model;

public class PrecompiledAddress {
    public static final String SYS_CONFIG_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001000";

    @Deprecated
    public static final String TABLE_FACTORY_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001001";

    public static final String CONSENSUS_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001003";
    public static final String CNS_PRECOMPILED_ADDRESS = "0000000000000000000000000000000000001004";
    public static final String BFS_PRECOMPILED_ADDRESS = "000000000000000000000000000000000000100e";
    public static final String COMMITTEE_MANAGER_ADDRESS =
            "0000000000000000000000000000000000010001";
    public static final String CONTRACT_AUTH_ADDRESS = "0000000000000000000000000000000000001005";
    public static final String KV_TABLE_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001009";

    public static final String SYS_CONFIG_PRECOMPILED_NAME = "/sys/status";
    public static final String CONSENSUS_PRECOMPILED_NAME = "/sys/consensus";
    public static final String CNS_PRECOMPILED_NAME = "/sys/cns";
    public static final String BFS_PRECOMPILED_NAME = "/sys/bfs";
    public static final String KV_TABLE_PRECOMPILED_NAME = "/sys/kv_storage";
    @Deprecated public static final String TABLE_FACTORY_PRECOMPILED_NAME = "/sys/table_storage";

    private PrecompiledAddress() {}
}
