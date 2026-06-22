package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.v3.model.RetCode;
import org.junit.Assert;
import org.junit.Test;

public class PrecompiledRetCodeTest {

    @Test
    public void testSuccessCode() {
        Assert.assertNotNull(PrecompiledRetCode.CODE_SUCCESS);
        Assert.assertEquals(0, PrecompiledRetCode.CODE_SUCCESS.getCode());
        Assert.assertEquals("Success", PrecompiledRetCode.CODE_SUCCESS.getMessage());
    }

    @Test
    public void testUnknownFailedCode() {
        Assert.assertNotNull(PrecompiledRetCode.CODE_UNKNOWN_FAILED);
        Assert.assertEquals(-1, PrecompiledRetCode.CODE_UNKNOWN_FAILED.getCode());
        Assert.assertEquals("Unknown failed", PrecompiledRetCode.CODE_UNKNOWN_FAILED.getMessage());
    }

    @Test
    public void testFileSystemPrecompiledCodes() {
        Assert.assertEquals(-53005, PrecompiledRetCode.CODE_FILE_INVALID_PATH.getCode());
        Assert.assertEquals(-53004, PrecompiledRetCode.CODE_FILE_SET_WASM_FAILED.getCode());
        Assert.assertEquals(-53003, PrecompiledRetCode.CODE_FILE_BUILD_DIR_FAILED.getCode());
        Assert.assertEquals(-53002, PrecompiledRetCode.CODE_FILE_ALREADY_EXIST.getCode());
        Assert.assertEquals(-53001, PrecompiledRetCode.CODE_FILE_NOT_EXIST.getCode());
    }

    @Test
    public void testChainGovernancePrecompiledCodes() {
        Assert.assertEquals(-52012, PrecompiledRetCode.CODE_CURRENT_VALUE_IS_EXPECTED_VALUE.getCode());
        Assert.assertEquals(-52011, PrecompiledRetCode.CODE_ACCOUNT_FROZEN.getCode());
        Assert.assertEquals(-52010, PrecompiledRetCode.CODE_ACCOUNT_ALREADY_AVAILABLE.getCode());
        Assert.assertEquals(-52009, PrecompiledRetCode.CODE_INVALID_ACCOUNT_ADDRESS.getCode());
        Assert.assertEquals(-52008, PrecompiledRetCode.CODE_ACCOUNT_NOT_EXIST.getCode());
    }

    @Test
    public void testConsensusPrecompiledCodes() {
        Assert.assertEquals(-51104, PrecompiledRetCode.CODE_ADD_SEALER_SHOULD_IN_OBSERVER.getCode());
        Assert.assertEquals(-51103, PrecompiledRetCode.CODE_NODE_NOT_EXIST.getCode());
        Assert.assertEquals(-51102, PrecompiledRetCode.CODE_INVALID_WEIGHT.getCode());
        Assert.assertEquals(-51101, PrecompiledRetCode.CODE_LAST_SEALER.getCode());
        Assert.assertEquals(-51100, PrecompiledRetCode.CODE_INVALID_NODEID.getCode());
    }

    @Test
    public void testCRUDPrecompiledCodes() {
        Assert.assertEquals(-51508, PrecompiledRetCode.CODE_REMOVE_KEY_NOT_EXIST.getCode());
        Assert.assertEquals(-51507, PrecompiledRetCode.CODE_UPDATE_KEY_NOT_EXIST.getCode());
        Assert.assertEquals(-51506, PrecompiledRetCode.CODE_INSERT_KEY_EXIST.getCode());
        Assert.assertEquals(-51505, PrecompiledRetCode.CODE_KEY_NOT_EXIST_IN_COND.getCode());
        Assert.assertEquals(-51504, PrecompiledRetCode.CODE_KEY_NOT_EXIST_IN_ENTRY.getCode());
    }

    @Test
    public void testCommonErrorCodes() {
        Assert.assertEquals(-50105, PrecompiledRetCode.CODE_TABLE_OPEN_ERROR.getCode());
        Assert.assertEquals(-50104, PrecompiledRetCode.CODE_TABLE_CREATE_ERROR.getCode());
        Assert.assertEquals(-50103, PrecompiledRetCode.CODE_TABLE_SET_ROW_ERROR.getCode());
        Assert.assertEquals(-50102, PrecompiledRetCode.CODE_ADDRESS_INVALID.getCode());
        Assert.assertEquals(-50101, PrecompiledRetCode.CODE_UNKNOWN_FUNCTION_CALL.getCode());
        Assert.assertEquals(-50100, PrecompiledRetCode.CODE_TABLE_NOT_EXIST.getCode());
    }

    @Test
    public void testTableErrorCodes() {
        Assert.assertEquals(-50000, PrecompiledRetCode.CODE_NO_AUTHORIZED.getCode());
        Assert.assertEquals(-50001, PrecompiledRetCode.CODE_TABLE_NAME_ALREADY_EXIST.getCode());
        Assert.assertEquals(-50002, PrecompiledRetCode.CODE_TABLE_NAME_LENGTH_OVERFLOW.getCode());
        Assert.assertEquals(-50003, PrecompiledRetCode.CODE_TABLE_FILED_LENGTH_OVERFLOW.getCode());
    }

    @Test
    public void testGetPrecompiledResponseWithKnownCode() {
        RetCode retCode = PrecompiledRetCode.getPrecompiledResponse(0, "Test Message");
        Assert.assertNotNull(retCode);
        Assert.assertEquals(PrecompiledRetCode.CODE_SUCCESS, retCode);
    }

    @Test
    public void testGetPrecompiledResponseWithUnknownCode() {
        int unknownCode = -99999;
        String message = "Custom error message";
        RetCode retCode = PrecompiledRetCode.getPrecompiledResponse(unknownCode, message);
        
        Assert.assertNotNull(retCode);
        Assert.assertEquals(unknownCode, retCode.getCode());
        Assert.assertEquals(message, retCode.getMessage());
    }

    @Test
    public void testGetPrecompiledResponseForMultipleCodes() {
        // Test a few different known codes
        Assert.assertEquals(PrecompiledRetCode.CODE_FILE_NOT_EXIST,
                          PrecompiledRetCode.getPrecompiledResponse(-53001, ""));
        Assert.assertEquals(PrecompiledRetCode.CODE_INVALID_NODEID,
                          PrecompiledRetCode.getPrecompiledResponse(-51100, ""));
        Assert.assertEquals(PrecompiledRetCode.CODE_TABLE_NOT_EXIST,
                          PrecompiledRetCode.getPrecompiledResponse(-50100, ""));
    }

    @Test
    public void testErrorMessageConstants() {
        Assert.assertEquals("The operated node must be in the list returned by getGroupPeers",
                          PrecompiledRetCode.MUST_EXIST_IN_NODE_LIST);
        Assert.assertEquals("The node already exists in the sealerList",
                          PrecompiledRetCode.ALREADY_EXISTS_IN_SEALER_LIST);
        Assert.assertEquals("The node already exists in the observerList",
                          PrecompiledRetCode.ALREADY_EXISTS_IN_OBSERVER_LIST);
        Assert.assertEquals("The node already has been removed from the group",
                          PrecompiledRetCode.ALREADY_REMOVED_FROM_THE_GROUP);
    }

    @Test
    public void testOverTableKeyLengthLimit() {
        Assert.assertNotNull(PrecompiledRetCode.OVER_TABLE_KEY_LENGTH_LIMIT);
        Assert.assertTrue(PrecompiledRetCode.OVER_TABLE_KEY_LENGTH_LIMIT.contains("255"));
    }

    @Test
    public void testAllRetCodesHaveMessages() {
        Assert.assertNotNull(PrecompiledRetCode.CODE_SUCCESS.getMessage());
        Assert.assertNotNull(PrecompiledRetCode.CODE_FILE_NOT_EXIST.getMessage());
        Assert.assertNotNull(PrecompiledRetCode.CODE_INVALID_NODEID.getMessage());
        Assert.assertNotNull(PrecompiledRetCode.CODE_TABLE_NOT_EXIST.getMessage());
    }

    @Test
    public void testNegativeErrorCodes() {
        // All error codes should be negative except success
        Assert.assertTrue(PrecompiledRetCode.CODE_SUCCESS.getCode() >= 0);
        Assert.assertTrue(PrecompiledRetCode.CODE_UNKNOWN_FAILED.getCode() < 0);
        Assert.assertTrue(PrecompiledRetCode.CODE_FILE_NOT_EXIST.getCode() < 0);
        Assert.assertTrue(PrecompiledRetCode.CODE_INVALID_NODEID.getCode() < 0);
    }

    @Test
    public void testContractLifeCycleCodes() {
        Assert.assertEquals(-51907, PrecompiledRetCode.CODE_INVALID_REVOKE_LAST_AUTHORIZATION.getCode());
        Assert.assertEquals(-51906, PrecompiledRetCode.CODE_INVALID_NON_EXIST_AUTHORIZATION.getCode());
        Assert.assertEquals(-51905, PrecompiledRetCode.CODE_INVALID_NO_AUTHORIZED.getCode());
        Assert.assertEquals(-51904, PrecompiledRetCode.CODE_INVALID_TABLE_NOT_EXIST.getCode());
        Assert.assertEquals(-51903, PrecompiledRetCode.CODE_INVALID_CONTRACT_ADDRESS.getCode());
    }

    @Test
    public void testVersionErrorCodes() {
        Assert.assertEquals(-51202, PrecompiledRetCode.CODE_ADDRESS_OR_VERSION_ERROR.getCode());
        Assert.assertEquals(-51201, PrecompiledRetCode.CODE_VERSION_LENGTH_OVERFLOW.getCode());
    }
}
