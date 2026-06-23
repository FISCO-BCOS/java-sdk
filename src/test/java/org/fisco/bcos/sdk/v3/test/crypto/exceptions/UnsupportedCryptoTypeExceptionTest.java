package org.fisco.bcos.sdk.v3.test.crypto.exceptions;

import org.fisco.bcos.sdk.v3.crypto.exceptions.UnsupportedCryptoTypeException;
import org.junit.Assert;
import org.junit.Test;

public class UnsupportedCryptoTypeExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Unsupported crypto type";
        UnsupportedCryptoTypeException exception = new UnsupportedCryptoTypeException(message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Crypto type not supported";
        Exception cause = new IllegalArgumentException("Invalid type");
        UnsupportedCryptoTypeException exception = new UnsupportedCryptoTypeException(message, cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        UnsupportedCryptoTypeException exception = new UnsupportedCryptoTypeException("Test");
        Assert.assertTrue(exception instanceof RuntimeException);
        Assert.assertTrue(exception instanceof Exception);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new UnsupportedCryptoTypeException("Unsupported exception");
        } catch (UnsupportedCryptoTypeException e) {
            Assert.assertEquals("Unsupported exception", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Should have caught UnsupportedCryptoTypeException specifically");
        }
    }

    @Test
    public void testNullMessage() {
        UnsupportedCryptoTypeException exception = new UnsupportedCryptoTypeException(null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testEmptyMessage() {
        String message = "";
        UnsupportedCryptoTypeException exception = new UnsupportedCryptoTypeException(message);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testNullCause() {
        UnsupportedCryptoTypeException exception = new UnsupportedCryptoTypeException("Message", null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }
}
