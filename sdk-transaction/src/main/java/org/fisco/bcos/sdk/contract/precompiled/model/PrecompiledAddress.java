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
    public static final String SYSCONFIG_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001000";
    public static final String TABLEFACTORY_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001001";
    public static final String CRUD_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001002";
    public static final String CONSENSUS_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001003";
    public static final String CNS_PRECOMPILED_ADDRESS = "0000000000000000000000000000000000001004";
    public static final String CONTRACT_LIFECYCLE_PRECOMPILED_ADDRESS =
            "0000000000000000000000000000000000001007";
    public static final String DEPLOY_WASM_PRECOMPILED_ADDRESS =
            "000000000000000000000000000000000000100d";
    public static final String BFS_PRECOMPILED_ADDRESS = "000000000000000000000000000000000000100e";

    private PrecompiledAddress() {}
}
