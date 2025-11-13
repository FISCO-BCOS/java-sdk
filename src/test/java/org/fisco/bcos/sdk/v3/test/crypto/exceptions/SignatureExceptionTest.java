package org.fisco.bcos.sdk.v3.test.crypto.exceptions;

import org.fisco.bcos.sdk.v3.crypto.exceptions.SignatureException;
import org.junit.Assert;
import org.junit.Test;

public class SignatureExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Signature verification failed";
        SignatureException exception = new SignatureException(message);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Signature error occurred";
        Exception cause = new IllegalArgumentException("Invalid signature");
        SignatureException exception = new SignatureException(message, cause);
        
        Assert.assertNotNull(exception);
        Assert.assertEquals(message, exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        SignatureException exception = new SignatureException("Test");
        Assert.assertTrue(exception instanceof RuntimeException);
        Assert.assertTrue(exception instanceof Exception);
    }

    @Test
    public void testExceptionCanBeCaught() {
        try {
            throw new SignatureException("Signature exception");
        } catch (SignatureException e) {
            Assert.assertEquals("Signature exception", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Should have caught SignatureException specifically");
        }
    }

    @Test
    public void testNullMessage() {
        SignatureException exception = new SignatureException(null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void testEmptyMessage() {
        String message = "";
        SignatureException exception = new SignatureException(message);
        Assert.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testNullCause() {
        SignatureException exception = new SignatureException("Message", null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }
}
