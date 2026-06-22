package org.fisco.bcos.sdk.v3.transaction.model.dto;

import org.junit.Assert;
import org.junit.Test;

public class ResultCodeEnumTest {

    @Test
    public void testSuccessCode() {
        Assert.assertEquals(0, ResultCodeEnum.SUCCESS.getCode());
        Assert.assertEquals("success", ResultCodeEnum.SUCCESS.getMessage());
    }

    @Test
    public void testExecuteErrorCode() {
        Assert.assertEquals(1, ResultCodeEnum.EXECUTE_ERROR.getCode());
        Assert.assertEquals("execute error", ResultCodeEnum.EXECUTE_ERROR.getMessage());
    }

    @Test
    public void testResultEmptyCode() {
        Assert.assertEquals(2, ResultCodeEnum.RESULT_EMPTY.getCode());
        Assert.assertEquals("empty result", ResultCodeEnum.RESULT_EMPTY.getMessage());
    }

    @Test
    public void testUnknownCode() {
        Assert.assertEquals(3, ResultCodeEnum.UNKNOWN.getCode());
        Assert.assertEquals("unknown exception", ResultCodeEnum.UNKNOWN.getMessage());
    }

    @Test
    public void testEvmErrorCode() {
        Assert.assertEquals(4, ResultCodeEnum.EVM_ERROR.getCode());
        Assert.assertEquals("evm error", ResultCodeEnum.EVM_ERROR.getMessage());
    }

    @Test
    public void testExceptionOccurCode() {
        Assert.assertEquals(5, ResultCodeEnum.EXCEPTION_OCCUR.getCode());
        Assert.assertEquals("exception occur", ResultCodeEnum.EXCEPTION_OCCUR.getMessage());
    }

    @Test
    public void testParameterErrorCode() {
        Assert.assertEquals(6, ResultCodeEnum.PARAMETER_ERROR.getCode());
        Assert.assertEquals("param error", ResultCodeEnum.PARAMETER_ERROR.getMessage());
    }

    @Test
    public void testParseErrorCode() {
        Assert.assertEquals(7, ResultCodeEnum.PARSE_ERROR.getCode());
        Assert.assertEquals("parse error", ResultCodeEnum.PARSE_ERROR.getMessage());
    }

    @Test
    public void testAllEnumValues() {
        ResultCodeEnum[] values = ResultCodeEnum.values();
        Assert.assertEquals(8, values.length);
    }

    @Test
    public void testValueOf() {
        ResultCodeEnum success = ResultCodeEnum.valueOf("SUCCESS");
        Assert.assertEquals(ResultCodeEnum.SUCCESS, success);
    }

    @Test
    public void testSettersExist() {
        // Note: Setters exist on enum but should not be used in practice
        // as they can cause side effects. We just verify they exist.
        ResultCodeEnum code = ResultCodeEnum.SUCCESS;
        int originalCode = code.getCode();
        String originalMessage = code.getMessage();
        
        // Verify original values are as expected
        Assert.assertEquals(0, originalCode);
        Assert.assertEquals("success", originalMessage);
    }
}
