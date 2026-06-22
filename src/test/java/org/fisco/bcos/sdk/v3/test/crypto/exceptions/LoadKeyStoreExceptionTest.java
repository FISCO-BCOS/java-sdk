package org.fisco.bcos.sdk.v3.test.crypto.exceptions;

import org.fisco.bcos.sdk.v3.crypto.exceptions.LoadKeyStoreException;
import org.junit.Assert;
import org.junit.Test;

public class LoadKeyStoreExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Failed to load keystore";
        LoadKeyStoreException exception = new LoadKeyStoreException(message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Keystore load error";
        Exception cause = new java.io.IOException("File not found");
        LoadKeyStoreException exception = new LoadKeyStoreException(message, cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        LoadKeyStoreException exception = new LoadKeyStoreException("Test");
        Assert.assertTrue(exception instanceof RuntimeException);
        Assert.assertTrue(exception instanceof Exception);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new LoadKeyStoreException("Load exception");
        } catch (LoadKeyStoreException e) {
            Assert.assertEquals("Load exception", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Should have caught LoadKeyStoreException specifically");
        }
    }

    @Test
    public void testNullMessage() {
        LoadKeyStoreException exception = new LoadKeyStoreException(null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testEmptyMessage() {
        String message = "";
        LoadKeyStoreException exception = new LoadKeyStoreException(message);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testNullCause() {
        LoadKeyStoreException exception = new LoadKeyStoreException("Message", null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }
}
