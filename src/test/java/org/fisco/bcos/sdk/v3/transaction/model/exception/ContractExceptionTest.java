package org.fisco.bcos.sdk.v3.transaction.model.exception;

import org.junit.Assert;
import org.junit.Test;

public class ContractExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Contract error";
        ContractException exception = new ContractException(message);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(-1, exception.getErrorCode());
    }

    @Test
    public void testConstructorWithMessageAndErrorCode() {
        String message = "Contract error";
        int errorCode = 100;
        
        ContractException exception = new ContractException(message, errorCode);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    public void testConstructorWithMessageErrorCodeAndReceipt() {
        String message = "Contract error";
        int errorCode = 200;
        
        ContractException exception = new ContractException(message, errorCode, null);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(errorCode, exception.getErrorCode());
        Assert.assertNull(exception.getReceipt());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Contract error";
        Throwable cause = new RuntimeException("Root cause");
        
        ContractException exception = new ContractException(message, cause);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testConstructorWithMessageCauseAndOutput() {
        String message = "Contract error";
        Throwable cause = new RuntimeException("Root cause");
        
        ContractException exception = new ContractException(message, cause, null);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertNull(exception.getResponseOutput());
    }

    @Test
    public void testConstructorWithMessageAndOutput() {
        String message = "Contract error";
        
        ContractException exception = new ContractException(message, (org.fisco.bcos.sdk.v3.client.protocol.response.Call.CallOutput) null);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertNull(exception.getResponseOutput());
    }

    @Test
    public void testSetErrorCode() {
        ContractException exception = new ContractException("Test");
        exception.setErrorCode(300);
        
        Assert.assertEquals(300, exception.getErrorCode());
    }

    @Test
    public void testSetResponseOutput() {
        ContractException exception = new ContractException("Test");
        exception.setResponseOutput(null);
        
        Assert.assertNull(exception.getResponseOutput());
    }

    @Test
    public void testSetReceipt() {
        ContractException exception = new ContractException("Test");
        exception.setReceipt(null);
        
        Assert.assertNull(exception.getReceipt());
    }

    @Test
    public void testToString() {
        ContractException exception = new ContractException("Test error", 100);
        
        String result = exception.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("ContractException"));
        Assert.assertTrue(result.contains("100"));
    }

    @Test
    public void testEquals() {
        ContractException exception1 = new ContractException("Test", 100);
        ContractException exception2 = new ContractException("Test", 100);
        ContractException exception3 = new ContractException("Test", 200);
        
        Assert.assertEquals(exception1, exception2);
        Assert.assertNotEquals(exception1, exception3);
        Assert.assertEquals(exception1, exception1);
        Assert.assertNotEquals(exception1, null);
        Assert.assertNotEquals(exception1, "String");
    }

    @Test
    public void testHashCode() {
        ContractException exception1 = new ContractException("Test", 100);
        ContractException exception2 = new ContractException("Test", 100);
        
        Assert.assertEquals(exception1.hashCode(), exception2.hashCode());
    }
}
