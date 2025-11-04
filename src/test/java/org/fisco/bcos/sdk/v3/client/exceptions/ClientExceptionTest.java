package org.fisco.bcos.sdk.v3.client.exceptions;

import org.junit.Assert;
import org.junit.Test;

public class ClientExceptionTest {

    @Test
    public void testConstructorWithErrorCodeAndMessage() {
        int errorCode = 100;
        String errorMessage = "Test Error";
        String message = "Full message";
        
        ClientException exception = new ClientException(errorCode, errorMessage, message);
        
        Assert.assertEquals(errorCode, exception.getErrorCode());
        Assert.assertEquals(errorMessage, exception.getErrorMessage());
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithErrorCodeMessageAndCause() {
        int errorCode = 200;
        String errorMessage = "Test Error";
        String message = "Full message";
        Throwable cause = new RuntimeException("Cause");
        
        ClientException exception = new ClientException(errorCode, errorMessage, message, cause);
        
        Assert.assertEquals(errorCode, exception.getErrorCode());
        Assert.assertEquals(errorMessage, exception.getErrorMessage());
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testConstructorWithMessage() {
        String message = "Simple message";
        
        ClientException exception = new ClientException(message);
        
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Message with cause";
        Throwable cause = new RuntimeException("Root cause");
        
        ClientException exception = new ClientException(message, cause);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testSetErrorCode() {
        ClientException exception = new ClientException("Test");
        exception.setErrorCode(300);
        
        Assert.assertEquals(300, exception.getErrorCode());
    }

    @Test
    public void testSetErrorMessage() {
        ClientException exception = new ClientException("Test");
        exception.setErrorMessage("New error message");
        
        Assert.assertEquals("New error message", exception.getErrorMessage());
    }

    @Test
    public void testToString() {
        int errorCode = 400;
        String errorMessage = "Error Message";
        String message = "Full Message";
        
        ClientException exception = new ClientException(errorCode, errorMessage, message);
        
        String result = exception.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains(String.valueOf(errorCode)));
        Assert.assertTrue(result.contains(errorMessage));
        Assert.assertTrue(result.contains(message));
    }

    @Test
    public void testEquals() {
        ClientException exception1 = new ClientException(100, "Error", "Message");
        ClientException exception2 = new ClientException(100, "Error", "Message");
        ClientException exception3 = new ClientException(200, "Error", "Message");
        
        Assert.assertEquals(exception1, exception2);
        Assert.assertNotEquals(exception1, exception3);
        Assert.assertEquals(exception1, exception1);
        Assert.assertNotEquals(exception1, null);
        Assert.assertNotEquals(exception1, "String");
    }

    @Test
    public void testHashCode() {
        ClientException exception1 = new ClientException(100, "Error", "Message");
        ClientException exception2 = new ClientException(100, "Error", "Message");
        
        Assert.assertEquals(exception1.hashCode(), exception2.hashCode());
    }
}
