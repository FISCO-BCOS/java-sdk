package org.fisco.bcos.sdk.v3.transaction.model.exception;

import org.junit.Assert;
import org.junit.Test;

public class JsonExceptionTest {

    @Test
    public void testDefaultConstructor() {
        JsonException exception = new JsonException();
        
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testConstructorWithMessage() {
        String message = "JSON parsing error";
        JsonException exception = new JsonException(message);
        
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "JSON parsing error";
        Throwable cause = new RuntimeException("Root cause");
        
        JsonException exception = new JsonException(message, cause);
        
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testConstructorWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid JSON");
        
        JsonException exception = new JsonException(cause);
        
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        JsonException exception = new JsonException("Test");
        
        Assert.assertTrue(exception instanceof RuntimeException);
    }
}
