package org.fisco.bcos.sdk.v3.test.crypto.exceptions;

import org.fisco.bcos.sdk.v3.crypto.exceptions.HashException;
import org.junit.Assert;
import org.junit.Test;

public class HashExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Hash computation failed";
        HashException exception = new HashException(message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Hash error occurred";
        Exception cause = new IllegalArgumentException("Invalid input");
        HashException exception = new HashException(message, cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        HashException exception = new HashException("Test");
        Assert.assertTrue(exception instanceof RuntimeException);
        Assert.assertTrue(exception instanceof Exception);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new HashException("Hash exception");
        } catch (HashException e) {
            Assert.assertEquals("Hash exception", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Should have caught HashException specifically");
        }
    }

    @Test
    public void testNullMessage() {
        HashException exception = new HashException(null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testEmptyMessage() {
        String message = "";
        HashException exception = new HashException(message);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testNullCause() {
        HashException exception = new HashException("Message", null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }
}
