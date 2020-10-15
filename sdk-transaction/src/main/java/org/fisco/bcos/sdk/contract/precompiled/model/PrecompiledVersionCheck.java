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

public class PrecompiledVersionCheck {
    public static final Version CNS_GET_CONTRACT_ADDRESS_PRECOMPILED_VERSION =
            new Version("getContractAddress", "2.3.0");
    public static final Version CONTRACT_LIFE_CYCLE_PRECOMPILED_VERSION =
            new Version("ContractLifeCycle", "2.3.0");
    public static final Version CONTRACT_LIFE_CYCLE_REVOKE_MANAGER_VERSION =
            new Version("ContractLifeCyclePrecompiled.revokeManager", "2.7.0");
    public static final Version TABLE_CRUD_PRECOMPILED_VERSION = new Version("CRUD", "2.0.0-rc3");
    public static final Version CHAIN_GOVERNANCE_PRECOMPILED_VERSION =
            new Version("ChainGovernance", "2.5.0");
    public static final Version CHAIN_GOVERNANCE_PRECOMPILED_QUERY_VERSION =
            new Version("queryVotesOfMember and queryVotesOfThreshold ", "2.7.0");
    public static final Version TABLE_PERMISSION_PRECOMPILED_VERSION =
            new Version("Permission", "2.0.0-rc3");
    public static final Version GRANT_WRITE_PERMISSION_PRECOMPILED_VERSION =
            new Version("grantWrite", "2.3.0");
    public static final Version REVOKE_WRITE_PERMISSION_PRECOMPILED_VERSION =
            new Version("revokeWrite", "2.3.0");
    public static final Version QUERY_WRITE_PERMISSION_PRECOMPILED_VERSION =
            new Version("queryPermission", "2.3.0");
}
