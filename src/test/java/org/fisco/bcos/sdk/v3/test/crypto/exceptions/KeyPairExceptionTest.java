package org.fisco.bcos.sdk.v3.test.crypto.exceptions;

import org.fisco.bcos.sdk.v3.crypto.exceptions.KeyPairException;
import org.junit.Assert;
import org.junit.Test;

public class KeyPairExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Key pair generation failed";
        KeyPairException exception = new KeyPairException(message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Key pair error occurred";
        Exception cause = new IllegalStateException("Invalid state");
        KeyPairException exception = new KeyPairException(message, cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        KeyPairException exception = new KeyPairException("Test");
        Assert.assertTrue(exception instanceof RuntimeException);
        Assert.assertTrue(exception instanceof Exception);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new KeyPairException("Key pair exception");
        } catch (KeyPairException e) {
            Assert.assertEquals("Key pair exception", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Should have caught KeyPairException specifically");
        }
    }

    @Test
    public void testNullMessage() {
        KeyPairException exception = new KeyPairException(null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testEmptyMessage() {
        String message = "";
        KeyPairException exception = new KeyPairException(message);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testNullCause() {
        KeyPairException exception = new KeyPairException("Message", null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }
}
