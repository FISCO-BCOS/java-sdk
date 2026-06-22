package org.fisco.bcos.sdk.v3.transaction.model.exception;

import org.fisco.bcos.sdk.v3.model.RetCode;
import org.junit.Assert;
import org.junit.Test;

public class TransactionBaseExceptionTest {

    @Test
    public void testConstructorWithRetCode() {
        RetCode retCode = new RetCode(100, "Test error message");
        TransactionBaseException exception = new TransactionBaseException(retCode);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals("Test error message", exception.getMessage());
        Assert.assertEquals(retCode, exception.getRetCode());
        Assert.assertEquals(100, exception.getRetCode().getCode());
    }

    @Test
    public void testConstructorWithCodeAndMessage() {
        int code = 200;
        String message = "Custom error";
        TransactionBaseException exception = new TransactionBaseException(code, message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertNotNull(exception.getRetCode());
        Assert.assertEquals(code, exception.getRetCode().getCode());
        Assert.assertEquals(message, exception.getRetCode().getMessage());
    }

    @Test
    public void testGetRetCode() {
        RetCode retCode = new RetCode(404, "Not found");
        TransactionBaseException exception = new TransactionBaseException(retCode);
        
        RetCode returnedRetCode = exception.getRetCode();
        Assert.assertNotNull(returnedRetCode);
        Assert.assertEquals(404, returnedRetCode.getCode());
        Assert.assertEquals("Not found", returnedRetCode.getMessage());
    }

    @Test
    public void testExceptionIsThrowable() {
        TransactionBaseException exception = new TransactionBaseException(100, "Test");
        Assert.assertTrue(exception instanceof Exception);
        Assert.assertTrue(exception instanceof Throwable);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new TransactionBaseException(500, "Server error");
        } catch (TransactionBaseException e) {
            Assert.assertEquals("Server error", e.getMessage());
            Assert.assertEquals(500, e.getRetCode().getCode());
        } catch (Exception e) {
            Assert.fail("Should have caught TransactionBaseException specifically");
        }
    }

    @Test
    public void testSerialVersionUID() {
        // Test that the exception is serializable
        TransactionBaseException exception = new TransactionBaseException(100, "Test");
        Assert.assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void testNegativeErrorCode() {
        int code = -1;
        String message = "Negative error code";
        TransactionBaseException exception = new TransactionBaseException(code, message);
        
        Assert.assertEquals(code, exception.getRetCode().getCode());
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testZeroErrorCode() {
        TransactionBaseException exception = new TransactionBaseException(0, "Success");
        Assert.assertEquals(0, exception.getRetCode().getCode());
    }

    @Test
    public void testEmptyMessage() {
        TransactionBaseException exception = new TransactionBaseException(100, "");
        Assert.assertNotNull(exception);
        Assert.assertEquals("", exception.getMessage());
    }

    @Test
    public void testRetCodeWithNullMessage() {
        RetCode retCode = new RetCode(100, null);
        TransactionBaseException exception = new TransactionBaseException(retCode);
        Assert.assertNull(exception.getMessage());
        Assert.assertEquals(retCode, exception.getRetCode());
    }
}
