package org.fisco.bcos.sdk.v3.test.transaction.model.exception;

import org.fisco.bcos.sdk.v3.transaction.model.exception.JsonException;
import org.junit.Assert;
import org.junit.Test;

public class JsonExceptionTest {

    @Test
    public void testDefaultConstructor() {
        JsonException exception = new JsonException();
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void testConstructorWithMessage() {
        String message = "JSON parsing error";
        JsonException exception = new JsonException(message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithCause() {
        Exception cause = new IllegalArgumentException("Invalid JSON format");
        JsonException exception = new JsonException(cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Failed to parse JSON";
        Exception cause = new RuntimeException("Root cause");
        JsonException exception = new JsonException(message, cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        JsonException exception = new JsonException("Test");
        Assert.assertTrue(exception instanceof RuntimeException);
        Assert.assertTrue(exception instanceof Exception);
        Assert.assertTrue(exception instanceof Throwable);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new JsonException("JSON error");
        } catch (JsonException e) {
            Assert.assertEquals("JSON error", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Should have caught JsonException specifically");
        }
    }

    @Test
    public void testExceptionDoesNotRequireChecked() {
        // Since JsonException extends RuntimeException, it can be thrown without being declared
        JsonException exception = new JsonException("Unchecked exception");
        Assert.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testSerialVersionUID() {
        // Test that the exception is serializable
        JsonException exception = new JsonException("Test");
        Assert.assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void testNullMessage() {
        JsonException exception = new JsonException((String) null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testNullCause() {
        JsonException exception = new JsonException((Throwable) null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void testEmptyMessage() {
        String message = "";
        JsonException exception = new JsonException(message);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testChainedExceptions() {
        Exception rootCause = new IllegalStateException("Root");
        Exception middleCause = new RuntimeException("Middle", rootCause);
        JsonException exception = new JsonException("Top", middleCause);
        
        Assert.assertEquals(middleCause, exception.getCause());
        Assert.assertEquals(rootCause, exception.getCause().getCause());
    }
}
