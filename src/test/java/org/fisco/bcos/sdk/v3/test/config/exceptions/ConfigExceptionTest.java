package org.fisco.bcos.sdk.v3.test.config.exceptions;

import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.junit.Assert;
import org.junit.Test;

public class ConfigExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Configuration error occurred";
        ConfigException exception = new ConfigException(message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithCause() {
        Exception cause = new RuntimeException("Root cause");
        ConfigException exception = new ConfigException(cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertTrue(exception.getMessage().contains("Root cause"));
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Configuration error";
        Exception cause = new IllegalArgumentException("Invalid argument");
        ConfigException exception = new ConfigException(message, cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsThrowable() {
        ConfigException exception = new ConfigException("Test");
        Assert.assertTrue(exception instanceof Exception);
        Assert.assertTrue(exception instanceof Throwable);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new ConfigException("Test exception");
        } catch (ConfigException e) {
            Assert.assertEquals("Test exception", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Should have caught ConfigException specifically");
        }
    }

    @Test
    public void testNullMessage() {
        ConfigException exception = new ConfigException((String) null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testNullCause() {
        ConfigException exception = new ConfigException((Throwable) null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void testEmptyMessage() {
        String message = "";
        ConfigException exception = new ConfigException(message);
        Assert.assertEquals(message, exception.getMessage());
    }
}
