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
package org.fisco.bcos.sdk.contract.precompiled.permission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledAddress;
import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledConstant;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.ReceiptParser;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;

public class PermissionService {
    private final PermissionPrecompiled permissionPrecompiled;

    public PermissionService(Client client, CryptoInterface credential) {
        this.permissionPrecompiled =
                PermissionPrecompiled.load(
                        PrecompiledAddress.PERMISSION_PRECOMPILED_ADDRESS, client, credential);
    }

    public RetCode grantPermission(String tableName, String userAddress) throws ContractException {
        return ReceiptParser.parsePrecompiledReceipt(
                this.permissionPrecompiled.insert(tableName, userAddress));
    }

    public RetCode revokePermission(String tableName, String userAddress) throws ContractException {
        return ReceiptParser.parsePrecompiledReceipt(
                this.permissionPrecompiled.remove(tableName, userAddress));
    }

    public static List<PermissionInfo> parsePermissionInfo(String permissionInfo)
            throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        return objectMapper.readValue(
                permissionInfo,
                objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, PermissionInfo.class));
    }

    public List<PermissionInfo> queryPermission(String contractAddress) throws ContractException {
        try {
            String permissionInfo = this.permissionPrecompiled.queryPermission(contractAddress);
            return parsePermissionInfo(permissionInfo);
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "Query permission for "
                            + contractAddress
                            + " failed, error info: "
                            + e.getMessage(),
                    e);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    public RetCode grantWrite(String contractAddress, String userAddress) throws ContractException {
        return ReceiptParser.parsePrecompiledReceipt(
                this.permissionPrecompiled.grantWrite(contractAddress, userAddress));
    }

    public RetCode revokeWrite(String contractAddress, String userAddress)
            throws ContractException {
        return ReceiptParser.parsePrecompiledReceipt(
                this.permissionPrecompiled.revokeWrite(contractAddress, userAddress));
    }

    public List<PermissionInfo> queryPermissionByTableName(String tableName)
            throws ContractException {
        try {
            String permissionInfo = this.permissionPrecompiled.queryByName(tableName);
            return parsePermissionInfo(permissionInfo);
        } catch (JsonProcessingException e) {
            throw new ContractException(
                    "Query permission for " + tableName + " failed, error info: " + e.getMessage(),
                    e);
        } catch (ContractException e) {
            throw ReceiptParser.parseExceptionCall(e);
        }
    }

    // permission interfaces for _sys_table_
    public RetCode grantDeployAndCreateManager(String userAddress) throws ContractException {
        return grantPermission(PrecompiledConstant.SYS_TABLE, userAddress);
    }

    public RetCode revokeDeployAndCreateManager(String userAddress) throws ContractException {
        return revokePermission(PrecompiledConstant.SYS_TABLE, userAddress);
    }

    public List<PermissionInfo> listDeployAndCreateManager() throws ContractException {
        return queryPermissionByTableName(PrecompiledConstant.SYS_TABLE);
    }
    // permission interfaces for _sys_table_access_
    public RetCode grantPermissionManager(String userAddress) throws ContractException {
        return grantPermission(PrecompiledConstant.SYS_TABLE_ACCESS, userAddress);
    }

    public RetCode revokePermissionManager(String userAddress) throws ContractException {
        return revokePermission(PrecompiledConstant.SYS_TABLE_ACCESS, userAddress);
    }

    public List<PermissionInfo> listPermissionManager() throws ContractException {
        return queryPermissionByTableName(PrecompiledConstant.SYS_TABLE_ACCESS);
    }

    // permission interfaces for _sys_consensus_
    public RetCode grantNodeManager(String userAddress) throws ContractException {
        return grantPermission(PrecompiledConstant.SYS_CONSENSUS, userAddress);
    }

    public RetCode revokeNodeManager(String userAddress) throws ContractException {
        return revokePermission(PrecompiledConstant.SYS_CONSENSUS, userAddress);
    }

    public List<PermissionInfo> listNodeManager() throws ContractException {
        return queryPermissionByTableName(PrecompiledConstant.SYS_CONSENSUS);
    }
    // permission interfaces for _sys_cns_
    public RetCode grantCNSManager(String userAddress) throws ContractException {
        return grantPermission(PrecompiledConstant.SYS_CNS, userAddress);
    }

    public RetCode revokeCNSManager(String userAddress) throws ContractException {
        return revokePermission(PrecompiledConstant.SYS_CNS, userAddress);
    }

    public List<PermissionInfo> listCNSManager() throws ContractException {
        return queryPermissionByTableName(PrecompiledConstant.SYS_CNS);
    }
    // permission interfaces for _sys_config_
    public RetCode grantSysConfigManager(String userAddress) throws ContractException {
        return grantPermission(PrecompiledConstant.SYS_CONFIG, userAddress);
    }

    public RetCode revokeSysConfigManager(String userAddress) throws ContractException {
        return revokePermission(PrecompiledConstant.SYS_CONFIG, userAddress);
    }

    public List<PermissionInfo> listSysConfigManager() throws ContractException {
        return queryPermissionByTableName(PrecompiledConstant.SYS_CONFIG);
    }
}
