/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.test.precompiled;

import org.fisco.bcos.sdk.contract.precompiled.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.junit.Assert;
import org.junit.Test;

public class PrecompiledRetCodeTest {
    @Test
    public void testGetPrecompiledResponse() {
        checkResponse(
                PrecompiledRetCode.CODE_CURRENT_VALUE_IS_EXPECTED_VALUE.getCode(),
                PrecompiledRetCode.CODE_CURRENT_VALUE_IS_EXPECTED_VALUE.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_ACCOUNT_FROZEN.getCode(),
                PrecompiledRetCode.CODE_ACCOUNT_FROZEN.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_ACCOUNT_ALREADY_AVAILABLE.getCode(),
                PrecompiledRetCode.CODE_ACCOUNT_ALREADY_AVAILABLE.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_ACCOUNT_ADDRESS.getCode(),
                PrecompiledRetCode.CODE_INVALID_ACCOUNT_ADDRESS.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_ACCOUNT_NOT_EXIST.getCode(),
                PrecompiledRetCode.CODE_ACCOUNT_NOT_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_OPERATOR_NOT_EXIST.getCode(),
                PrecompiledRetCode.CODE_OPERATOR_NOT_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_OPERATOR_EXIST.getCode(),
                PrecompiledRetCode.CODE_OPERATOR_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_COMMITTEE_MEMBER_CANNOT_BE_OPERATOR.getCode(),
                PrecompiledRetCode.CODE_COMMITTEE_MEMBER_CANNOT_BE_OPERATOR.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_OPERATOR_CANNOT_BE_COMMITTEE_MEMBER.getCode(),
                PrecompiledRetCode.CODE_OPERATOR_CANNOT_BE_COMMITTEE_MEMBER.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_THRESHOLD.getCode(),
                PrecompiledRetCode.CODE_INVALID_THRESHOLD.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_REQUEST_PERMISSION_DENIED.getCode(),
                PrecompiledRetCode.CODE_INVALID_REQUEST_PERMISSION_DENIED.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_COMMITTEE_MEMBER_NOT_EXIST.getCode(),
                PrecompiledRetCode.CODE_COMMITTEE_MEMBER_NOT_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_COMMITTEE_MEMBER_EXIST.getCode(),
                PrecompiledRetCode.CODE_COMMITTEE_MEMBER_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_NO_AUTHORIZED.getCode(),
                PrecompiledRetCode.CODE_INVALID_NO_AUTHORIZED.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_TABLE_NOT_EXIST.getCode(),
                PrecompiledRetCode.CODE_INVALID_TABLE_NOT_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_CONTRACT_ADDRESS.getCode(),
                PrecompiledRetCode.CODE_INVALID_CONTRACT_ADDRESS.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_CONTRACT_REPEAT_AUTHORIZATION.getCode(),
                PrecompiledRetCode.CODE_INVALID_CONTRACT_REPEAT_AUTHORIZATION.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_CONTRACT_AVAILABLE.getCode(),
                PrecompiledRetCode.CODE_INVALID_CONTRACT_AVAILABLE.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_CONTRACT_FEOZEN.getCode(),
                PrecompiledRetCode.CODE_INVALID_CONTRACT_FEOZEN.getMessage());
        checkResponse(
                PrecompiledRetCode.VERIFY_RING_SIG_FAILED.getCode(),
                PrecompiledRetCode.VERIFY_RING_SIG_FAILED.getMessage());
        checkResponse(
                PrecompiledRetCode.VERIFY_GROUP_SIG_FAILED.getCode(),
                PrecompiledRetCode.VERIFY_GROUP_SIG_FAILED.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_CIPHERS.getCode(),
                PrecompiledRetCode.CODE_INVALID_CIPHERS.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_CONDITION_OPERATION_UNDEFINED.getCode(),
                PrecompiledRetCode.CODE_CONDITION_OPERATION_UNDEFINED.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_PARSE_CONDITION_ERROR.getCode(),
                PrecompiledRetCode.CODE_PARSE_CONDITION_ERROR.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_PARSE_ENTRY_ERROR.getCode(),
                PrecompiledRetCode.CODE_PARSE_ENTRY_ERROR.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_CONFIGURATION_VALUES.getCode(),
                PrecompiledRetCode.CODE_INVALID_CONFIGURATION_VALUES.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_VERSION_LENGTH_OVERFLOW.getCode(),
                PrecompiledRetCode.CODE_VERSION_LENGTH_OVERFLOW.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_ADDRESS_AND_VERSION_EXIST.getCode(),
                PrecompiledRetCode.CODE_ADDRESS_AND_VERSION_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_LAST_SEALER.getCode(),
                PrecompiledRetCode.CODE_LAST_SEALER.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_INVALID_NODEID.getCode(),
                PrecompiledRetCode.CODE_INVALID_NODEID.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_COMMITTEE_PERMISSION.getCode(),
                PrecompiledRetCode.CODE_COMMITTEE_PERMISSION.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_CONTRACT_NOT_EXIST.getCode(),
                PrecompiledRetCode.CODE_CONTRACT_NOT_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_TABLE_NAME_OVERFLOW.getCode(),
                PrecompiledRetCode.CODE_TABLE_NAME_OVERFLOW.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_TABLE_AND_ADDRESS_EXIST.getCode(),
                PrecompiledRetCode.CODE_TABLE_AND_ADDRESS_EXIST.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_ADDRESS_INVALID.getCode(),
                PrecompiledRetCode.CODE_ADDRESS_INVALID.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_UNKNOW_FUNCTION_CALL.getCode(),
                PrecompiledRetCode.CODE_UNKNOW_FUNCTION_CALL.getMessage());
        checkResponse(
                PrecompiledRetCode.CODE_SUCCESS.getCode(),
                PrecompiledRetCode.CODE_SUCCESS.getMessage());
    }

    private void checkResponse(int code, String expectedMessage) {
        RetCode retCode = PrecompiledRetCode.getPrecompiledResponse(code);
        Assert.assertTrue(expectedMessage.equals(retCode.getMessage()));
        Assert.assertEquals(code, retCode.getCode());
    }
}
