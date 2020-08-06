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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.fisco.bcos.sdk.model.RetCode;

public class PrecompiledRetCode {
    // ChainGovernancePrecompiled -52099 ~ -52000
    public static final RetCode CODE_CURRENT_VALUE_IS_EXPECTED_VALUE =
            new RetCode(-52012, "The current value is expected");
    public static final RetCode CODE_ACCOUNT_FROZEN = new RetCode(-52011, "The account is frozen");
    public static final RetCode CODE_ACCOUNT_ALREADY_AVAILABLE =
            new RetCode(-52010, "The account is already available");
    public static final RetCode CODE_INVALID_ACCOUNT_ADDRESS =
            new RetCode(-52009, "Invalid account address");
    public static final RetCode CODE_ACCOUNT_NOT_EXIST = new RetCode(-52008, "Account not exist");
    public static final RetCode CODE_OPERATOR_NOT_EXIST =
            new RetCode(-52007, "The operator not exist");
    public static final RetCode CODE_OPERATOR_EXIST =
            new RetCode(-52006, "The operator already exist");
    public static final RetCode CODE_COMMITTEE_MEMBER_CANNOT_BE_OPERATOR =
            new RetCode(-52005, "The committee member cannot be operator");
    public static final RetCode CODE_OPERATOR_CANNOT_BE_COMMITTEE_MEMBER =
            new RetCode(-52004, "The operator cannot be committee member");
    public static final RetCode CODE_INVALID_THRESHOLD =
            new RetCode(-52003, "Invalid threshold, threshold should from 0 to 99");
    public static final RetCode CODE_INVALID_REQUEST_PERMISSION_DENIED =
            new RetCode(-52002, "Invalid request for permission deny");
    public static final RetCode CODE_COMMITTEE_MEMBER_NOT_EXIST =
            new RetCode(-52001, "The committee member not exist");
    public static final RetCode CODE_COMMITTEE_MEMBER_EXIST =
            new RetCode(-52000, "The committee member already exist");

    // ContractLifeCyclePrecompiled -51999 ~ -51900
    public static final RetCode CODE_INVALID_NO_AUTHORIZED =
            new RetCode(-51905, "Have no permission to access the contract table");
    public static final RetCode CODE_INVALID_TABLE_NOT_EXIST =
            new RetCode(-51904, "The queried contract address doesn't exist");
    public static final RetCode CODE_INVALID_CONTRACT_ADDRESS =
            new RetCode(-51903, "The contract address is invalid");
    public static final RetCode CODE_INVALID_CONTRACT_REPEAT_AUTHORIZATION =
            new RetCode(-51902, "The contract has been granted authorization with same user");
    public static final RetCode CODE_INVALID_CONTRACT_AVAILABLE =
            new RetCode(-51901, "The contract is available");
    public static final RetCode CODE_INVALID_CONTRACT_FEOZEN =
            new RetCode(-51900, "The contract has been frozen");

    // RingSigPrecompiled -51899 ~ -51800
    public static final RetCode VERIFY_RING_SIG_FAILED =
            new RetCode(-51800, "Verify ring signature failed");

    // GroupSigPrecompiled -51799 ~ -51700
    public static final RetCode VERIFY_GROUP_SIG_FAILED =
            new RetCode(-51700, "Verify group signature failed");

    // PaillierPrecompiled -51699 ~ -51600
    public static final RetCode CODE_INVALID_CIPHERS =
            new RetCode(-51600, "Execute PaillierAdd failed");

    // CRUDPrecompiled -51599 ~ -51500
    public static final RetCode CODE_CONDITION_OPERATION_UNDEFINED =
            new RetCode(-51502, "Undefined function of Condition Precompiled");
    public static final RetCode CODE_PARSE_CONDITION_ERROR =
            new RetCode(-51501, "Parse the input of Condition Precompiled failed");
    public static final RetCode CODE_PARSE_ENTRY_ERROR =
            new RetCode(-51500, "Parse the inpput of the Entriy Precompiled failed");

    // SystemConfigPrecompiled -51399 ~ -51300
    public static final RetCode CODE_INVALID_CONFIGURATION_VALUES =
            new RetCode(-51300, "Invalid configuration entry");

    // CNSPrecompiled -51299 ~ -51200
    public static final RetCode CODE_VERSION_LENGTH_OVERFLOW =
            new RetCode(-51201, "The version string length exceeds the maximum limit");
    public static final RetCode CODE_ADDRESS_AND_VERSION_EXIST =
            new RetCode(-51200, "The contract name and version already exist");

    // ConsensusPrecompiled -51199 ~ -51100
    public static final RetCode CODE_LAST_SEALER =
            new RetCode(-51101, "The last sealer cannot be removed");
    public static final RetCode CODE_INVALID_NODEID = new RetCode(-51100, "Invalid node ID");

    // PermissionPrecompiled -51099 ~ -51000
    public static final RetCode CODE_COMMITTEE_PERMISSION =
            new RetCode(
                    -51004,
                    "The committee permission control by ChainGovernancePrecompiled are recommended");
    public static final RetCode CODE_CONTRACT_NOT_EXIST =
            new RetCode(-51003, "The contract is not exist");
    public static final RetCode CODE_TABLE_NAME_OVERFLOW = new RetCode(-51002, "");
    public static final RetCode CODE_TABLE_AND_ADDRESS_NOT_EXIST =
            new RetCode(-51001, "The table name string length exceeds the maximum limit");
    public static final RetCode CODE_TABLE_AND_ADDRESS_EXIST =
            new RetCode(-51000, "The table name and address already exist");

    // Common error code among all precompiled contracts -50199 ~ -50100
    public static final RetCode CODE_ADDRESS_INVALID =
            new RetCode(-50102, "Invalid address format");
    public static final RetCode CODE_UNKNOW_FUNCTION_CALL =
            new RetCode(-50101, "Undefined function");
    public static final RetCode CODE_TABLE_NOT_EXIST =
            new RetCode(-50100, "Open table failed, please check the existence of the table");

    // correct return: code great or equal 0
    public static final RetCode CODE_SUCCESS = new RetCode(0, "Success");

    // internal error(for example: params check failed, etc.): -29999~-20000
    public static final RetCode MUST_EXIST_IN_NODE_LIST =
            new RetCode(-20000, "The operated node must be in the list returned by getNodeIDList");
    public static final RetCode ALREADY_EXISTS_IN_SEALER_LIST =
            new RetCode(-20001, "The node already exists in the sealerList");
    protected static Map<Integer, RetCode> codeToMessage = new HashMap<>();

    static {
        Field[] fields = PrecompiledRetCode.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(RetCode.class)) {
                try {
                    RetCode constantRetCode = (RetCode) field.get(null);
                    codeToMessage.put(constantRetCode.getCode(), constantRetCode);
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
        }
    }

    private PrecompiledRetCode() {}

    public static RetCode getPrecompiledResponse(int responseCode) {
        if (codeToMessage.containsKey(responseCode)) {
            return codeToMessage.get(responseCode);
        }
        return new RetCode(responseCode, "");
    }
}
